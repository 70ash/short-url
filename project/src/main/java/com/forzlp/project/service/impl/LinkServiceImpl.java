package com.forzlp.project.service.impl;


import cn.hutool.core.util.StrUtil;
import com.forzlp.project.common.convention.errorcode.BaseErrorCode;
import com.forzlp.project.common.convention.excetion.ClientException;
import com.forzlp.project.common.convention.excetion.ServiceException;
import com.forzlp.project.dao.entity.Goto;
import com.forzlp.project.dao.entity.Link;
import com.forzlp.project.dao.mapper.GotoMapper;
import com.forzlp.project.dao.mapper.LinkMapper;
import com.forzlp.project.dto.req.LinkCreateReqDTO;
import com.forzlp.project.dto.req.LinkSearchReqDTO;
import com.forzlp.project.dto.resp.LinkCreateRespDTO;
import com.forzlp.project.dto.resp.LinkSearchRespDTO;
import com.forzlp.project.service.LinkService;
import com.forzlp.project.utils.HashUtil;
import com.forzlp.project.utils.URLParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.forzlp.project.common.constant.LinkRedisEnums.LINK_GOTO_KEY;
import static com.forzlp.project.common.constant.LinkRedisEnums.LOCK_LINK_GOTO_KEY;

/**
 * Author 70ash
 * Date 2024/3/2 13:56
 * Description:
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LinkServiceImpl implements LinkService {
    private final LinkMapper linkMapper;
    private final RBloomFilter shortUrlCreateCachePenetrationBloomFilter;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final GotoMapper gotoMapper;

    @Value("${short-link.domain.default}")
    private String defaultDomain;
    /**
     *
     * @param requestParam 创建短链接参数
     * @return 创建短链接返回参数
     * 获取参数信息，生成六位数随机数，拼接获得完整短链接(协议+域名+短链接)
     * 入库，把完整短链接插入布隆过滤器，根据短链接和分组标识插入路由表，后续用户输入短链接由路由表获取分组标识查询原始链接
     */
    @Override
    public LinkCreateRespDTO saveLink(LinkCreateReqDTO requestParam){
        // 协议
        String protocol = URLParser.extractProtocol(requestParam.getOriginUrl());
        // 域名
        String domain = URLParser.extractDomain(requestParam.getOriginUrl());
        // 路径
        String path = generateSuffix(requestParam);
        // 获取完整短链接
        String fullShortUrl = defaultDomain + "/" + path;
        String gid = requestParam.getGid();
        if (gid == null) {}//TODO 获取用户默认分组标识
        LinkCreateRespDTO linkCreateRespDTO = LinkCreateRespDTO.builder()
                .gid(gid)
                .originUrl(requestParam.getOriginUrl())
                .fullShortUrl(fullShortUrl)
                .validType(requestParam.getValidType())
                .validTime(requestParam.getValidTime())
                .build();
        // 短链接
        Link link = Link.builder()
                .gid(requestParam.getGid())
                .domain(domain)
                .originUrl(requestParam.getOriginUrl())
                .shortUrl(path)
                .fullShortUrl(fullShortUrl)
                .validType(requestParam.getValidType())
                .validTime(requestParam.getValidTime())
                .clickNum(0)
                .enableStatus(0)
                .build();
        Goto build = Goto.builder()
                .gid(requestParam.getGid())
                .shortUri(path)
                .build();
        try {
            linkMapper.insertLink(link);
            gotoMapper.insertGoto(build);
            // 加入到布隆过滤器
            // 存入redis
            String format = String.format(LINK_GOTO_KEY, path);
            stringRedisTemplate.opsForValue().set(format, requestParam.getOriginUrl());
            shortUrlCreateCachePenetrationBloomFilter.add(fullShortUrl);
        }catch (DuplicateKeyException ex) {
            log.warn("短链接频繁创建，请稍后再试");
            throw new ClientException(BaseErrorCode.URL_DUPLICATE_CREATE);
        }
        return linkCreateRespDTO;
    }

    @Override
    public List<LinkSearchRespDTO> pageShortLink(LinkSearchReqDTO requestParam) {
        requestParam.setCurrent(requestParam.getCurrent() - 1);
        List<LinkSearchRespDTO> linkSearchRespDTO = linkMapper.pageLink(requestParam);
        return linkSearchRespDTO;
    }

    /**
     * @param shortUri 短链接
     * 查路由表获取分组标识，由分组标识和短链接查询原始短链接并跳转
     */
    @Override
    public void restore(String shortUri, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String fullShortUrl = defaultDomain + "/" + shortUri;
        // 从redis之中获取
        String originUrl = stringRedisTemplate.opsForValue().get(String.format(LINK_GOTO_KEY, shortUri));
        // 如果获取的原始链接不为空，那么就进行跳转
        if (StrUtil.isNotEmpty(originUrl)) {
            response.sendRedirect(originUrl);
            return;
        }
        // 查询布隆过滤器
        boolean contains = shortUrlCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if (!contains) {
            return;
        }
        // 对单个短链接加锁
        RLock lock = redissonClient.getLock(String.format(LOCK_LINK_GOTO_KEY, shortUri));
        lock.lock();
        // 使用try--finally保证锁的释放
        try {
            // 双重判定锁
            originUrl = stringRedisTemplate.opsForValue().get(String.format(LINK_GOTO_KEY, shortUri));
            // 如果获取的原始链接不为空，那么就进行跳转
            if (StrUtil.isNotEmpty(originUrl)) {
                response.sendRedirect(originUrl);
                return;
            }

            // 获取分组标识
            String gid = linkMapper.gotoByUri(shortUri);
            // 由分组标识和原始短链接进行跳转
            originUrl = linkMapper.selectOriginUrlByUriAndGid(shortUri, gid);
            // 存到redis之中
            stringRedisTemplate.opsForValue().set(String.format(LINK_GOTO_KEY, shortUri), originUrl);
            response.sendRedirect(originUrl);
        }finally {
            lock.unlock();
        }
    }

    /**
     *
     * @param requestParam 短链接新增参数
     * @return 六位短链接
     * 获取短链接域名，获取原始链接拼接UUID，使用哈希函数转换为Base62的六位随机数，拼接域名和随机数，判断是否为空，如果已创建则再次生成短链接。未创建则返回六位随机数
     */
    public String generateSuffix(LinkCreateReqDTO requestParam) {
        String domain = URLParser.extractDomain(requestParam.getOriginUrl());
        String path = null;
        // 短链接需要保证唯一性(整个短链接)，所以需要判断其是否存在于数据库中，如果存在则不能创建
        // 这里海量数据判空使用布隆过滤器，布隆过滤器判断其存在时查询数据库(可能误判为存在)，如果
        // 数据库中也存在就代表存在，否则就代表不存在，可以创建
        int shortUrlCreateTimes = 0;
        while (true) {
            if (shortUrlCreateTimes > 10) {
                throw new ServiceException("短链接频繁创建，请稍后再试");
            }
            String originUrl = requestParam.getOriginUrl() + UUID.randomUUID();
            path = HashUtil.hashToBase62(originUrl);
            String fullShortUrl = domain + "/" + path;
            // 海量数据判空不推荐查数据库, 推荐使用布隆过滤器
            // 可能出现实际未创建，但误判其已创建的情况。这种情况下会再次生成短链接。
            if(!shortUrlCreateCachePenetrationBloomFilter.contains(fullShortUrl)){
                break;
            }
            shortUrlCreateTimes++;
        }
        return path;
    }
}
