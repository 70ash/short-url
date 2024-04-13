package com.forzlp.project.controller;


import com.forzlp.project.common.convention.result.Result;
import com.forzlp.project.common.convention.result.Results;
import com.forzlp.project.dto.req.LinkCreateReqDTO;
import com.forzlp.project.dto.req.LinkSearchReqDTO;
import com.forzlp.project.dto.resp.LinkCreateRespDTO;
import com.forzlp.project.dto.resp.LinkSearchRespDTO;
import com.forzlp.project.service.LinkService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @Author 70ash
 * @Date 2024/3/2 13:55
 * @Description:
 */
@RestController
@AllArgsConstructor
@RequestMapping("/short-link/link")
public class LinkController {
    private LinkService linkService;
    @PostMapping("/create")
    public Result<LinkCreateRespDTO> saveLink(@RequestBody LinkCreateReqDTO requestParam) throws URISyntaxException {
        LinkCreateRespDTO linkCreateRespDTO = linkService.saveLink(requestParam);
        return Results.success(linkCreateRespDTO);
    }

    @GetMapping("/page")
    public Result<List<LinkSearchRespDTO>> pageShortLink(LinkSearchReqDTO requestParam) {
        return Results.success(linkService.pageShortLink(requestParam));
    }
}
