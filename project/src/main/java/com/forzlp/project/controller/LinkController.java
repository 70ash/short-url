package com.forzlp.project.controller;


import com.forzlp.project.common.convention.result.Result;
import com.forzlp.project.common.convention.result.Results;
import com.forzlp.project.dto.req.LinkCreateReqDTO;
import com.forzlp.project.dto.resp.LinkCreateRespDTO;
import com.forzlp.project.service.LinkService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

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
    @PostMapping()
    public Result<LinkCreateRespDTO> saveLink(@RequestBody LinkCreateReqDTO requestParam) throws URISyntaxException {
        LinkCreateRespDTO linkCreateRespDTO = linkService.saveLink(requestParam);
        return Results.success(linkCreateRespDTO);
    }
}
