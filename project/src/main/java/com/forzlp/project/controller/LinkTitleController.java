package com.forzlp.project.controller;

import com.forzlp.project.common.convention.result.Result;
import com.forzlp.project.common.convention.result.Results;
import com.forzlp.project.service.LinkTitleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author 70ash
 * Date 2024/4/23 下午8:03
 * Description:
 */
@RestController
@AllArgsConstructor
public class LinkTitleController {
    private LinkTitleService linkTitleService;
    @GetMapping("/short-link/link/title")
    public Result<String> getTitle(@RequestParam("url") String url) {
        return Results.success(linkTitleService.extractTitle(url));
    }

    @GetMapping("/short-link/link/avatar")
    public Result<String> getAvatar(@RequestParam("url") String url) {
        return Results.success(linkTitleService.extractIconUrl(url));
    }
}
