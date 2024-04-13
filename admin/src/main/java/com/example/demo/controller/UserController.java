package com.example.demo.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.demo.common.convention.result.Result;
import com.example.demo.common.convention.result.Results;
import com.example.demo.dto.req.UserLoginReqDTO;
import com.example.demo.dto.resp.UserActualRespDTO;
import com.example.demo.dto.resp.UserLoginRespDTO;
import com.example.demo.dto.resp.UserRegisterReqDTO;
import com.example.demo.dto.resp.UserRespDTO;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author 70ash
 * @Date 2024/1/24 23:28
 * @Description:
 */
@RestController
@RequestMapping("/short-link/user")
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

    /**
     *
     * @param requestPram
     * @return 返回token，后续请求中携带token进行验证
     */
    @PostMapping("/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestPram) {
        return Results.success(userService.login(requestPram));
    }


    @GetMapping("/check/login")
    public Result<Boolean> checkLogin(String username, String token) {
        return Results.success(userService.checkLogin(username, token));
    }

    @PostMapping("/logout")
    public Result login(@RequestParam String username, String token) {
        return Results.success(userService.logout(username, token));
    }
}
