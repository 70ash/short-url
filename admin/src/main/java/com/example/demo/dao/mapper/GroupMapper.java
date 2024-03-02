package com.example.demo.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.dao.entity.Group;
import com.example.demo.dto.req.UpdateGroupReqDTO;
import com.example.demo.dto.resp.ListGroupRespDTO;

import java.util.List;

/**
 * @Author 70ash
 * @Date 2024/2/20 16:42
 * @Description:
 */
public interface GroupMapper extends BaseMapper<Group> {
    Group selectByGid(String gid);
    int insertGroup(Group group);

    List<ListGroupRespDTO> selectBatchByUserName(String username);

    int updateGroup(String username, UpdateGroupReqDTO requestParam);

    int deleteByGid(String username, String gid);
}
