package com.example.demo.service;

import com.example.demo.dto.req.GroupSaveReqDTO;
import com.example.demo.dto.req.UpdateGroupReqDTO;
import com.example.demo.dto.resp.ListGroupRespDTO;
import com.example.demo.dto.resp.ListLinkRespDTO;

import java.util.List;

/**
 * Author 70ash
 * Date 2024/2/20 16:43
 * Description:
 */
public interface GroupService{
    /** 新增短链接分组 **/
    void saveGroup(GroupSaveReqDTO requestParam);
    /** 新增短链接分组 **/
    void saveGroup(String username, String groupName, String description);
    /** 查询短链接分组 **/
    List<ListGroupRespDTO> listGroup(Integer pageSize, Integer pageNum);

    /** 新增短链接分组 **/
    //TODO
    List<ListLinkRespDTO> listGroupByName(String name);

    /** 修改分组 **/
    void updateGroup(UpdateGroupReqDTO requestParam);
    /** 删除分组 **/
    void updateGroup(String gid);

}
