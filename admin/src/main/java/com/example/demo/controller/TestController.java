package com.example.demo.controller;

import com.example.demo.common.convention.result.Result;
import com.example.demo.common.convention.result.Results;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author 70ash
 * Date 2024/5/12 下午3:35
 * Type: 
 * Description:
 */
@RestController
public class TestController {
    @GetMapping("/test")
    public Result<String> test() {
        return Results.success("666");
    }
}
