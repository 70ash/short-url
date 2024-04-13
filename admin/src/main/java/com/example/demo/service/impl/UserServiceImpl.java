package com.example.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.example.demo.common.convention.errorcode.BaseErrorCode.*;
import static com.example.demo.common.enums.RedisEnums.LOCK_USER_REGISTER;
import static com.example.demo.common.enums.RedisEnums.USER_LOGIN_KEY;

/**
 * @Author 70ash
 * @Date 2024/1/25 11:58
 * @Description:
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
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<User> eq = Wrappers.lambdaQuery(User.class).eq(User::getUsername, username);
        User user = null;
        System.out.println(user);
        if(user == null) {
            throw new ClientException(USER_NOT_EXIST);
        }
        UserRespDTO userRespDTO = new UserRespDTO();
        BeanUtils.copyProperties(user, userRespDTO);
        System.out.println("----------------------------");
        System.out.println(userRespDTO);
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
            groupService.saveGroup("默认分组");
            lock.unlock();
        }
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestPram) {
        User user = userMapper.selectUserByInfo(requestPram);
        if (user == null) {
            throw new ClientException(USER_LOGIN_FAIL);
        }
        //防止重复登录
        if (stringRedisTemplate.hasKey(USER_LOGIN_KEY + requestPram.getUsername())) {
            throw new ClientException(USER_ALREADY_LOGIN);
        }
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(USER_LOGIN_KEY + requestPram.getUsername(), token, JSON.toJSONString(requestPram));
        return new UserLoginRespDTO(token);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().hasKey(USER_LOGIN_KEY + username, token);
    }

    @Override
    public String logout(String username, String token) {
        try {
            Long i = stringRedisTemplate.opsForHash().delete(USER_LOGIN_KEY + username, token);
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