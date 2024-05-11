package com.example.demo.dao.mapper;

import com.example.demo.dao.entity.Group;
import com.example.demo.dto.dto.ListGroupDTO;
import com.example.demo.dto.req.UpdateGroupReqDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Author 70ash
 * Date 2024/2/20 16:42
 * Description:
 */
public interface GroupMapper{
    Group selectByGid(@Param("username") String username, @Param("gid") String gid);

    int insertGroup(Group group);

    List<ListGroupDTO> selectBatchByUserName(String username);

    int updateGroup(String username, UpdateGroupReqDTO requestParam);

    int deleteByGid(String username, String gid);

    Integer countByGid(String gid);
}
