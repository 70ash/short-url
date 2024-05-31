package com.example.demo.dao.mapper;

import com.example.demo.dao.entity.Group;
import com.example.demo.dto.req.UpdateGroupReqDTO;
import com.example.demo.dto.resp.ListGroupRespDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Author 70ash
 * Date 2024/2/20 16:42
 * Description:
 */
public interface GroupMapper{
    Group selectByGid(@Param("username") String username, @Param("gid") String gid);

    Integer insertGroup(Group group);

    List<ListGroupRespDTO> selectBatchByUserName(String username);

    int updateGroup(String username, UpdateGroupReqDTO requestParam);

    int deleteByGid(String username, String gid);

    Integer countByGid(String gid);
}
