package com.example.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.example.demo.common.biz.UserContext;
import com.example.demo.common.biz.UserInfoDTO;
import com.example.demo.common.convention.excetion.ClientException;
import com.example.demo.dao.entity.User;
import com.example.demo.dao.mapper.UserMapper;
import com.example.demo.dto.req.UserLoginReqDTO;
import com.example.demo.dto.resp.UserLoginRespDTO;
import com.example.demo.dto.resp.UserRegisterReqDTO;
import com.example.demo.dto.resp.UserRespDTO;
import com.example.demo.service.GroupService;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.example.demo.common.constant.RedisConstant.*;
import static com.example.demo.common.convention.errorcode.BaseErrorCode.*;


/**
 * Author 70ash
 * Date 2024/1/25 11:58
 * Description:
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserMapper userMapper;
    private final GroupService groupService;
    @Override
    public UserRespDTO getUserByUsername() {
        String username = UserContext.getUsername();
        UserRespDTO userRespDTO = userMapper.selectUserInfoByUserName(username);
        if(userRespDTO == null) {
            throw new ClientException(USER_NOT_EXIST);
        }
        return userRespDTO;
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if (hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER + requestParam.getUsername());
        try {
            if (lock.tryLock()) {
                User user = BeanUtil.toBean(requestParam, User.class);
                if (userMapper.insert(user) < 1) {
                    throw new ClientException(USER_INSERT_FAIL);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                return;
            }
            throw new ClientException(USER_NAME_EXIST);
        }finally {
            //TODO 用户注册后创建默认短链接分组
            groupService.saveGroup("默认分组", requestParam.getUsername(),"默认分组");
            if(lock.isLocked()) { // 是否还是锁定状态
                if (lock.isHeldByCurrentThread()) { // 时候是当前执行线程的锁
                    lock.unlock(); // 释放锁
                }
            }
        }
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestPram) {
        //防止重复登录
        String token = UUID.randomUUID().toString();
        if (Boolean.FALSE.equals(stringRedisTemplate.opsForValue().setIfAbsent(USER_TOKEN_KEY+requestPram.getUsername(),token, USER_TOKEN_TIME, TimeUnit.MINUTES))
        ){
            throw new ClientException(USER_ALREADY_LOGIN);
        }
        User user = userMapper.selectUserByInfo(requestPram);
        if (user == null) {
            throw new ClientException(USER_LOGIN_FAIL);
        }
        UserInfoDTO build = UserInfoDTO.builder()
                .username(user.getUsername())
                .userId(user.getId())
                .token(token)
                .build();
        stringRedisTemplate.opsForValue().set(USER_TOKEN_KEY+token, JSONUtil.toJsonStr(build),USER_TOKEN_TIME,TimeUnit.MINUTES);
        return new UserLoginRespDTO(token);
    }

    @Override
    public String logout() {
        try {
            stringRedisTemplate.delete(USER_TOKEN_KEY+ UserContext.getToken());
            stringRedisTemplate.delete(USER_TOKEN_KEY+UserContext.getUsername());
            UserContext.removeUser();
        } catch (Exception e) {
            throw new ClientException(USER_NOT_LOGIN);
        }
        return "退出登录成功";
    }


    @Override
    public boolean hasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }
}
