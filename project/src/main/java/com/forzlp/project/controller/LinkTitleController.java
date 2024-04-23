package com.forzlp.project.controller;

import com.forzlp.project.common.convention.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author 70ash
 * Date 2024/4/23 下午8:03
 * Description:
 */
@RestController
public class LinkTitleController {
    @GetMapping("/short-link/link/title")
    public Result<String> getResult(@RequestParam("url") String url) {
        return null;
    }
}
