package com.forzlp.project.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.forzlp.project.common.convention.errorcode.BaseErrorCode;
import com.forzlp.project.common.convention.excetion.ClientException;
import com.forzlp.project.common.convention.excetion.ServiceException;
import com.forzlp.project.dao.entity.Goto;
import com.forzlp.project.dao.entity.Link;
import com.forzlp.project.dao.entity.LinkStats;
import com.forzlp.project.dao.mapper.GotoMapper;
import com.forzlp.project.dao.mapper.LinkMapper;
import com.forzlp.project.dao.mapper.LinkStatsMapper;
import com.forzlp.project.dto.req.LinkCreateReqDTO;
import com.forzlp.project.dto.req.LinkSearchReqDTO;
import com.forzlp.project.dto.resp.LinkCreateRespDTO;
import com.forzlp.project.dto.resp.LinkSearchRespDTO;
import com.forzlp.project.service.LinkService;
import com.forzlp.project.utils.HashUtil;
import com.forzlp.project.utils.LinkUtil;
import com.forzlp.project.utils.URLParser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.forzlp.project.common.constant.LinkRedisConstant.*;

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
    private final LinkStatsMapper linkStatsMapper;

    @Value("${short-link.stats.ip}")
    private String gaoDeKey;
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
                .favicon(extractIconUrl(requestParam.getOriginUrl()))
                .validType(requestParam.getValidType())
                .validTime(requestParam.getValidTime())
                .clickNum(0)
                .enableStatus(0)
                .build();
        Goto build = Goto.builder()
                .gid(requestParam.getGid())
                .shortUri(path)
                .build();
        LinkStats linkStats = LinkStats.builder()
                .gid(gid)
                .shortUri(path)
                .build();
        try {
            linkMapper.insertLink(link);
            gotoMapper.insertGoto(build);
            linkStatsMapper.insertLinkStats(linkStats);
            // 加入到布隆过滤器
            // 存入redis作缓存预热,
            Long linkCacheValidTime = LinkUtil.getLinkCacheValidTime(requestParam.getValidTime());
            stringRedisTemplate.opsForValue().set(
                    String.format(LINK_GOTO_KEY, path),
                    requestParam.getOriginUrl(),
                    linkCacheValidTime, TimeUnit.MILLISECONDS);

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
    //TODO 进行短链接统计
    @Override
    public void restore(String shortUri, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fullShortUrl = defaultDomain + "/" + shortUri;
        // 从redis之中获取
        String originUrl = stringRedisTemplate.opsForValue().get(String.format(LINK_GOTO_KEY, shortUri));
        // 如果获取的原始链接不为空，那么就进行跳转
        if (StrUtil.isNotEmpty(originUrl)) {
            linkStats(shortUri,request, response);
            response.sendRedirect(originUrl);
            return;
        }
        // 查询布隆过滤器
        boolean contains = shortUrlCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if (!contains) {
            response.sendRedirect("/page/notfound");
            return;
        }
        // 如果判断是否是空uri，如果是，返回一个空值
        String LinkIsNull = stringRedisTemplate.opsForValue().get(String.format(LINK_GOTO_IS_NULL_KEY, shortUri));
        if (StrUtil.isNotEmpty(LinkIsNull)) {
            response.sendRedirect("/page/notfound");
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
                linkStats(shortUri,request, response);
                response.sendRedirect(originUrl);
                return;
            }
            LinkIsNull = stringRedisTemplate.opsForValue().get(String.format(LINK_GOTO_IS_NULL_KEY, shortUri));
            if (StrUtil.isNotEmpty(LinkIsNull)) {
                response.sendRedirect("/page/notfound");
                return;
            }
            // 获取分组标识
            String gid = linkMapper.gotoByUri(shortUri);
            // 由分组标识和原始短链接进行跳转
            Link link = linkMapper.selectLinkByUriAndGid(shortUri, gid);
            if (link != null) {
                originUrl = link.getOriginUrl();
                stringRedisTemplate.opsForValue().set(String.format(LINK_GOTO_KEY, shortUri), originUrl, LinkUtil.getLinkCacheValidTime(link.getValidTime()), TimeUnit.MILLISECONDS);
                linkStats(shortUri,request, response);
                response.sendRedirect(originUrl);
            } else { // 如果数据库中不存在，缓存一个空值
                stringRedisTemplate.opsForValue().set(String.format(LOCK_LINK_GOTO_KEY, shortUri), "-", LINK_GOTO_NULL_TIME_KEY, TimeUnit.SECONDS);
                response.sendRedirect("/page/notfound");
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * 短链接数据统计
     */
    private void linkStats(String shortUri, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        // 默认uv和ip标记为false，即没有访问过
        // 如果没有标记，那么设置+1
        AtomicBoolean uvFlag = new AtomicBoolean();
        AtomicBoolean ipFlag = new AtomicBoolean();
        if (ArrayUtil.isNotEmpty(cookies)) {
            //找到cookie
            Arrays.stream(cookies)
                    .filter(each -> Objects.equals(each.getName(), "uvCookie"))
                    .findFirst()
                    .map(Cookie::getValue) // 获取cookie对应的值
                    .ifPresentOrElse(each -> { // 如果存在，可能是访问其他短链接得到的cookie
                        // 把存进redis的set之中
                        Long add = stringRedisTemplate.opsForSet().add(String.format(LINK_UV_KEY, each), shortUri);
                        // 如果不存在于redis，代表其第一次访问
                        uvFlag.set(add != null && add > 0);
                    }, () -> {
                        // 给请求添加uvCookie
                        Cookie cookie = new Cookie("uvCookie", UUID.randomUUID() + "");
                        stringRedisTemplate.opsForSet().add(String.format(LINK_UV_KEY, cookie), shortUri);
                        response.addCookie(cookie);
                        uvFlag.set(true);
                    });
        }else {
            Cookie cookie = new Cookie("uvCookie", UUID.randomUUID() + "");
            stringRedisTemplate.opsForSet().add(String.format(LINK_UV_KEY, cookie), shortUri);
            response.addCookie(cookie);
        }
        String remoteAddr = request.getRemoteAddr();
        // 当同一ip访问两个不同短链接时，两个短链接都应该被记录
        Long  ipAdd = stringRedisTemplate.opsForSet().add(String.format(LINK_IP_KEY, remoteAddr), shortUri);
        if (ipAdd != null && ipAdd > 0) ipFlag.set(true);
        String gid = linkMapper.gotoByUri(shortUri);
        Date date = new Date();
        int day = DateUtil.dayOfWeek(date);
        int hour = DateUtil.hour(date, true);
        LinkStats linkStats = LinkStats.builder()
                .gid(gid)
                .shortUri(shortUri)
                .pv(1)
                .uv(uvFlag.get() ? 0 : 1)
                .uip(ipFlag.get() ? 0 : 1)
                .date(date)
                .week(day)
                .hour(hour)
                .build();
        //TODO 待修改
        linkStatsMapper.updateLinkStats(linkStats);
    }
    /**
     * 获取网站图标
     * @param urlString 网站地址
     * @return 网站图标地址
     */
    @SneakyThrows
    public String extractIconUrl(String urlString) {
        URL targetUrl = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (HttpURLConnection.HTTP_OK == responseCode) {
            Document document = Jsoup.connect(urlString).get();
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
            if (faviconLink != null) {
                return faviconLink.attr("abs:href");
            }
        }
        return null;
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
