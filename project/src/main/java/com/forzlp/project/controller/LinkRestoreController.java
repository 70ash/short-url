package com.forzlp.project.controller;

import com.forzlp.project.service.LinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Author 70ash
 * Date 2024/4/22 下午8:13
 * Description:
 */
@RestController
@AllArgsConstructor
public class LinkRestoreController {
    private LinkService linkService;

    /**
     * 短链接跳转
     */
    @GetMapping("/{shortUri}")
    public void restore(@PathVariable("shortUri") String shortUri, HttpServletRequest request, HttpServletResponse response) throws IOException {
        linkService.restore(shortUri, request, response);
    }

}
