package com.example.demo.service.impl;

import com.example.demo.dao.entity.Link;
import com.example.demo.dao.mapper.LinkMapper;
import com.example.demo.dto.req.LinkCreateReqDTO;
import com.example.demo.dto.resp.LinkCreateRespDTO;
import com.example.demo.service.LinkService;
import com.example.demo.util.HashUtil;
import com.example.demo.util.URLParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author 70ash
 * @Date 2024/3/2 13:56
 * @Description:
 */
@Service
@AllArgsConstructor
public class LinkServiceImpl implements LinkService {
    private final LinkMapper linkMapper;
    @Override
    public LinkCreateRespDTO saveLink(LinkCreateReqDTO requestParam){
        String domain = URLParser.extractDomain(requestParam.getOriginUrl());
        // 生成六位数短链接
        String path = HashUtil.hashToBase62(requestParam.getOriginUrl());
        String protocol = URLParser.extractProtocol(requestParam.getOriginUrl());
        String fullShortUrl = protocol + "://" + domain + "/" + path;
        LinkCreateRespDTO linkCreateRespDTO = LinkCreateRespDTO.builder()
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .fullShortUrl(fullShortUrl)
                .validType(requestParam.getValidType())
                .validTime(requestParam.getValidTime())
                .build();
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
        linkMapper.insertLink(link);
        return linkCreateRespDTO;
    }
}
