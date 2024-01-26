package com.forzlp.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.forzlp.admin.common.convention.exception.ServiceException;
import com.forzlp.admin.dao.entity.User;
import com.forzlp.admin.dao.mapper.UserMapper;
import com.forzlp.admin.dto.resp.UserInfoRespDTO;
import com.forzlp.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author 70ash
* @description 针对表【t_user】的数据库操作Service实现
* @createDate 2024-01-22 14:39:00
*/
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final UserMapper userMapper;

    @Override
    public UserInfoRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<User> eq = Wrappers.lambdaQuery(User.class).eq(User::getUsername, username);
        User user = getOne(eq);
        if(user == null) {
            throw new ServiceException("用户名不存在");
        }
        UserInfoRespDTO userInfoRespDTO = new UserInfoRespDTO();
        BeanUtils.copyProperties(user, userInfoRespDTO);
        return userInfoRespDTO;
    }
}




