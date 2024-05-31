package com.example.demo.dao.mapper;

import com.example.demo.dao.entity.User;
import com.example.demo.dto.req.UserLoginReqDTO;
import com.example.demo.dto.resp.UserRespDTO;

/**
 * Author 70ash
 * Date 2024/1/25 12:00
 * Description:
 */
public interface UserMapper{
    User selectUserByInfo(UserLoginReqDTO requestParam);

    int insert(User user);

    UserRespDTO selectUserInfoByUserName(String username);
}
