package com.forzlp.project.controller;


import com.forzlp.project.common.convention.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author 70ash
 * Date 2024/5/12 上午1:00
 * Type:
 * Description:
 */
@RestController
public class ErrorController {
    @PostMapping("/myPError")
    public Result<Void> error(HttpServletRequest request) throws Exception {
        throw ((RuntimeException) request.getAttribute("filter.error"));
    }
}
