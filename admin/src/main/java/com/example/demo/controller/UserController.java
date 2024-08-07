package com.example.demo.controller;

import com.example.demo.common.convention.result.Result;
import com.example.demo.common.convention.result.Results;
import com.example.demo.dto.req.UserLoginReqDTO;
import com.example.demo.dto.resp.UserLoginRespDTO;
import com.example.demo.dto.resp.UserRegisterReqDTO;
import com.example.demo.dto.resp.UserRespDTO;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Author 70ash
 * Date 2024/1/24 23:28
 * Description:
 */
@RestController
@RequestMapping("/short-link/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping()
    public Result<UserRespDTO> getUserByUsername() {
        return (Results.success(userService.getUserByUsername()));
    }

    // @GetMapping("/actual/{username}")
    // public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable String username) {
    //     // 使用hutool的工具类将UserRespDTO转化为UserActualRespDTO
    //     return (Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class)));
    // }

    @GetMapping("/has/{username}")
    public Result<Boolean> hasUsername(@PathVariable String username) {
        return Results.success(userService.hasUsername(username));
    }
    @PostMapping("/register")
    @CrossOrigin
    public Result<Void> register(@RequestBody @Validated UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * @return 返回token，后续请求中携带token进行验证
     */
    @PostMapping("/login")
    @CrossOrigin
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestPram) {
        return Results.success(userService.login(requestPram));
    }

    @PostMapping("/logout")
    public Result<String> login() {
        return Results.success(userService.logout());
    }
}
