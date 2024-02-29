package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dao.entity.Group;

/**
 * @Author 70ash
 * @Date 2024/2/20 16:43
 * @Description:
 */
public interface GroupService extends IService<Group> {
    void saveGroup(String groupName);

}
