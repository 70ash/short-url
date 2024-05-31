package com.forzlp.project.service;


import com.forzlp.project.dto.req.LinkCreateReqDTO;
import com.forzlp.project.dto.req.LinkSearchReqDTO;
import com.forzlp.project.dto.req.LinkStatsReqDTO;
import com.forzlp.project.dto.resp.LinkCreateRespDTO;
import com.forzlp.project.dto.resp.LinkSearchRespDTO;
import com.forzlp.project.dto.resp.LinkStatsRespDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Author 70ash
 * Date 2024/3/2 13:56
 * Description:
 */
public interface LinkService {
    /**
     * 创建短链接
     */
    LinkCreateRespDTO saveLink(LinkCreateReqDTO requestParam) throws URISyntaxException;
    /**
     * 分页查询短链接
     */
    List<LinkSearchRespDTO> pageShortLink(LinkSearchReqDTO requestParam);

    /**
     * 短链接跳转
     */
    void restore(String shortUri, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 查询短链接信息
     */
    LinkStatsRespDTO linkStats(LinkStatsReqDTO requestParam);
}
