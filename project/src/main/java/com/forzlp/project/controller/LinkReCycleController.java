package com.forzlp.project.controller;

import com.forzlp.project.common.convention.result.Result;
import com.forzlp.project.common.convention.result.Results;
import com.forzlp.project.dto.req.LinkDeleteReqDTO;
import com.forzlp.project.dto.req.linkReCycleReqDTO;
import com.forzlp.project.dto.req.LinkRecoverReqDTO;
import com.forzlp.project.dto.resp.ShortLinkRecyclePageRespDTO;
import com.forzlp.project.service.LinkReCycleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author 70ash
 * Date 2024/4/24 下午11:44
 * Description: 短链接回收站
 */
@RestController
@AllArgsConstructor
public class LinkReCycleController {
    private LinkReCycleService linkReCycleService;
    /**
     * 移至回收站
     */
     @PostMapping("/short-link/link/recycle")
    public Result<Void> linkRecycle(@RequestBody linkReCycleReqDTO requestParam) {
        linkReCycleService.linkRecycle(requestParam);
        return Results.success();
    }

    /**
     * 回收站分页查询
     */
    @GetMapping("/short-link/link/recycle/page")
    public Result<List<ShortLinkRecyclePageRespDTO>> linkPageRecycle(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageNum") Integer pageSize) {
        List<ShortLinkRecyclePageRespDTO> list = linkReCycleService.linkPageRecycle(pageNum, pageSize);
        return Results.success(list);
    }

    /**
     * 移除回收站
     */
    @PostMapping("/short-link/link/recover")
    public Result<Void> linkRecover(@RequestBody LinkRecoverReqDTO requestParam) {
        linkReCycleService.linkRecover(requestParam);
        return Results.success();
    }
    /**
     * 从回收站中删除
     */
    @PostMapping("/short-link/recycle/delete")
    public Result<Void> linkDelete(@RequestBody LinkDeleteReqDTO requestParam) {
        linkReCycleService.linkDelete(requestParam);
        return Results.success();
    }
}
