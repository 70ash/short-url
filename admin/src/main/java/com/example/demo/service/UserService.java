package com.example.demo.service;

import com.example.demo.dto.req.UserLoginReqDTO;
import com.example.demo.dto.resp.UserLoginRespDTO;
import com.example.demo.dto.resp.UserRegisterReqDTO;
import com.example.demo.dto.resp.UserRespDTO;

/**
 * Author 70ash
 * Date 2024/1/25 11:57
 * Description:
 */
public interface UserService{
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户名是否存在
     * @param username
     * @return 存在返回true, 不存在返回false
     */
    boolean hasUsername(String username);

    /**
     * 注册用户
     * @param requestParam 注册用户信息
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 用户登录
     * @param requestPram 用户登录参数
     * @return 用户登录返回参数
     */
    UserLoginRespDTO login(UserLoginReqDTO requestPram);

    /**
     * 检查用户登录状态
     * @param username 用户名
     * @param token 用户token
     * @return 已登录返回True, 未登录返回False
     */
    Boolean checkLogin(String username, String token);

    String logout(String username, String token);
}
