package com.forzlp.project.controller;

import com.forzlp.project.common.convention.result.Result;
import com.forzlp.project.common.convention.result.Results;
import com.forzlp.project.dto.req.LinkReCycleReq;
import com.forzlp.project.service.LinkReCycleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author 70ash
 * Date 2024/4/24 下午11:44
 * Description:
 */
@RestController
@AllArgsConstructor
public class LinkReCycleController {
    private LinkReCycleService linkReCycleService;
    /**
     * 移至回收站
     */
    @DeleteMapping("/short-link/link/recycle")
    public Result<Void> linkRecycle(@RequestBody LinkReCycleReq linkReCycleReq) {
        linkReCycleService.linkRecycle(linkReCycleReq);
        return Results.success();
    }
}
