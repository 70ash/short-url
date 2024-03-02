package com.forzlp.project.service.impl;


import com.forzlp.project.common.convention.errorcode.BaseErrorCode;
import com.forzlp.project.common.convention.excetion.ClientException;
import com.forzlp.project.dao.entity.Link;
import com.forzlp.project.dao.mapper.LinkMapper;
import com.forzlp.project.dto.req.LinkCreateReqDTO;
import com.forzlp.project.dto.resp.LinkCreateRespDTO;
import com.forzlp.project.service.LinkService;
import com.forzlp.project.utils.HashUtil;
import com.forzlp.project.utils.URLParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * @Author 70ash
 * @Date 2024/3/2 13:56
 * @Description:
 */
@Service
@AllArgsConstructor
@Slf4j
public class LinkServiceImpl implements LinkService {
    private final LinkMapper linkMapper;
    private RBloomFilter shortUrlCreateCachePenetrationBloomFilter;
    @Override
    public LinkCreateRespDTO saveLink(LinkCreateReqDTO requestParam){
        // 域名
        String domain = URLParser.extractDomain(requestParam.getOriginUrl());
        // 路径
        String path = generateSuffix(requestParam);
        // 获取完整短链接
        String fullShortUrl = domain + "/" + path;
        LinkCreateRespDTO linkCreateRespDTO = LinkCreateRespDTO.builder()
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .fullShortUrl(fullShortUrl)
                .validType(requestParam.getValidType())
                .validTime(requestParam.getValidTime())
                .build();
        // 短链接
        Link link = Link.builder()
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .shortUrl(path)
                .fullShortUrl(fullShortUrl)
                .validType(requestParam.getValidType())
                .validTime(requestParam.getValidTime())
                .clickNum(0)
                .enableStatus(null)
                .build();
        try {
            linkMapper.insertLink(link);
            shortUrlCreateCachePenetrationBloomFilter.add(fullShortUrl);
        }catch (DuplicateKeyException ex) {
            log.warn("短链接频繁创建，请稍后再试");
            throw new ClientException(BaseErrorCode.URL_DUPLICATE_CREATE);
        }
        return linkCreateRespDTO;
    }
    public String generateSuffix(LinkCreateReqDTO requestParam) {
        String domain = URLParser.extractDomain(requestParam.getOriginUrl());
        String path = null;
        int shortUrlCreateTimes = 0;
        while (true) {
            if (shortUrlCreateTimes > 10) {
                break;
            }
            path = HashUtil.hashToBase62(requestParam.getOriginUrl());
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
