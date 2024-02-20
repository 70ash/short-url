package com.forzlp.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.forzlp.admin.common.convention.result.Result;
import com.forzlp.admin.common.convention.result.Results;
import com.forzlp.admin.dto.req.UserLoginReqDTO;
import com.forzlp.admin.dto.resp.UserActualRespDTO;
import com.forzlp.admin.dto.resp.UserLoginRespDTO;
import com.forzlp.admin.dto.resp.UserRegisterReqDTO;
import com.forzlp.admin.dto.resp.UserRespDTO;
import com.forzlp.admin.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author 70ash
 * @Date 2024/1/24 23:28
 * @Description:
 */
@RestController
@RequestMapping("/shortLink/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable String username) {
        return (Results.success(userService.getUserByUsername(username)));
    }
    @GetMapping("/actual/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable String username) {
        // 使用hutool的工具类将UserRespDTO转化为UserActualRespDTO
        return (Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class)));
    }

    @GetMapping("/has/{username}")
    public Result<Boolean> hasUsername(@PathVariable String username) {
        return Results.success(userService.hasUsername(username));
    }

    @PostMapping()
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    @PostMapping("/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestPram) {
        return Results.success(userService.login(requestPram));
    }

    @GetMapping("/check/login")
    public Result<Boolean> checkLogin(String username, String token) {
        return Results.success(userService.checkLogin(username, token));
    }
}
