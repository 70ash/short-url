package com.example.demo.service;

import com.example.demo.dto.req.UpdateGroupReqDTO;
import com.example.demo.dto.resp.ListGroupRespDTO;
import com.example.demo.dto.resp.ListLinkRespDTO;

import java.util.List;

/**
 * @Author 70ash
 * @Date 2024/2/20 16:43
 * @Description:
 */
public interface GroupService{
    // 新增短链接分组
    void saveGroup(String groupName);
    // 查询短链接分组
    List<ListGroupRespDTO> listGroup();

    List<ListLinkRespDTO> listGroupByName(String name);

    void updateGroup(UpdateGroupReqDTO requestParam);
    void updateGroup(String gid);

}
