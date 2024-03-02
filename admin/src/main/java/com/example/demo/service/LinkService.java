package com.example.demo.service;

import com.example.demo.dto.req.LinkCreateReqDTO;
import com.example.demo.dto.resp.LinkCreateRespDTO;

import java.net.URISyntaxException;

/**
 * @Author 70ash
 * @Date 2024/3/2 13:56
 * @Description:
 */
public interface LinkService {
    /**
     *
     * @param requestParam 创建短链接相关参数
     * @return 创建短链接返回相关参数
     */
    LinkCreateRespDTO saveLink(LinkCreateReqDTO requestParam) throws URISyntaxException;
}
