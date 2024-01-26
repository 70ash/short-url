package com.forzlp.admin.controller;

import com.forzlp.admin.common.convention.result.Result;
import com.forzlp.admin.common.convention.result.Results;
import com.forzlp.admin.dto.resp.UserInfoRespDTO;
import com.forzlp.admin.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 70ash
 * @Date 2024/1/21 0:11
 * @Description:
 */
@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    /**
     * 查询用户信息
     */
    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserInfoRespDTO> getUserInfoByUsername(@PathVariable String username) {
        return Results.success(userService.getUserByUsername(username));
    }
}
