package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.entity.Group;
import com.example.demo.dao.mapper.GroupMapper;
import com.example.demo.service.GroupService;
import com.example.demo.util.RandomStringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author 70ash
 * @Date 2024/2/20 16:43
 * @Description:
 */
@Service
@AllArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    @Override
    public void saveGroup(String groupName) {
        String gid;
        while (true) {
            gid = RandomStringUtil.generateRandomString();
            LambdaQueryWrapper<Group> wrapper = Wrappers.lambdaQuery(Group.class);
            LambdaQueryWrapper<Group> eq = wrapper.eq(Group::getGid, gid);
            Group one = getOne(eq);
            if (one == null) break;
        }
        Group group = Group.builder()
                .gid(gid)
                .name(groupName)
                //TODO 后续从网关得到username
                .username(null)
                .build();
        save(group);
    }
}
