package com.forzlp.project.controller;


import com.forzlp.project.common.convention.result.Result;
import com.forzlp.project.common.convention.result.Results;
import com.forzlp.project.dto.req.LinkCreateReqDTO;
import com.forzlp.project.dto.req.LinkSearchReqDTO;
import com.forzlp.project.dto.req.LinkStatsReqDTO;
import com.forzlp.project.dto.resp.LinkCreateRespDTO;
import com.forzlp.project.dto.resp.LinkSearchRespDTO;
import com.forzlp.project.dto.resp.LinkStatsRespDTO;
import com.forzlp.project.service.LinkService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @Author 70ash
 * @Date 2024/3/2 13:55
 * @Description:
 */
@RestController
@AllArgsConstructor
public class LinkController {
    private LinkService linkService;

    /**
     * 新增短链接
     */
    @PostMapping("/short-link/link/create")
    public Result<LinkCreateRespDTO> saveLink(@RequestBody LinkCreateReqDTO requestParam) throws URISyntaxException {
        LinkCreateRespDTO linkCreateRespDTO = linkService.saveLink(requestParam);
        return Results.success(linkCreateRespDTO);
    }

    @GetMapping("/short-link/link/page")
    public Result<List<LinkSearchRespDTO>> pageShortLink(LinkSearchReqDTO requestParam) {
        return Results.success(linkService.pageShortLink(requestParam));
    }

    /**
     * 短链接数据统计
     */
    @GetMapping("/short-link/link/stats")
    public Result<LinkStatsRespDTO> linkStats(LinkStatsReqDTO requestParam) {
        return Results.success(linkService.linkStats(requestParam));
    }
}
