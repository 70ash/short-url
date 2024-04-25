package com.example.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.example.demo.common.convention.excetion.ClientException;
import com.example.demo.common.convention.excetion.ServiceException;
import com.example.demo.dao.entity.Group;
import com.example.demo.dao.mapper.GroupMapper;
import com.example.demo.dto.dto.ListGroupDTO;
import com.example.demo.dto.req.UpdateGroupReqDTO;
import com.example.demo.dto.resp.ListGroupRespDTO;
import com.example.demo.dto.resp.ListLinkRespDTO;
import com.example.demo.service.GroupService;
import com.example.demo.util.RandomStringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.common.convention.errorcode.BaseErrorCode.USER_GROUP_EXIST;

/**
 * Author 70ash
 * Date 2024/2/20 16:43
 * Description:
 */
@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {

    private GroupMapper groupMapper;

    @Override
    public void saveGroup(String groupName) {
        String gid;
        String username = "90ash";
        while (true) {
            gid = RandomStringUtil.generateRandomString();
            Group one = groupMapper.selectByGid(username, gid);
            if (one == null) break;
        }
        Group group = Group.builder()
                .gid(gid)
                .name(groupName)
                //TODO 后续从网关得到username
                .username(username)
                .build();
        try {
            groupMapper.insertGroup(group);
        } catch (Exception e) {
            throw new ClientException(USER_GROUP_EXIST);
        }
    }


    @Override
    public List<ListGroupRespDTO> listGroup() {
        // TODO 后续从网关中获取username
        String username = "90ash";
        List<ListGroupDTO> list = groupMapper.selectBatchByUserName(username);
        // List<ListGroupRespDTO> listGroupRespDTOList = new ArrayList<>();
        // for (ListGroupDTO item : list) {
        //     String gid = item.getGid();
        //     Integer count = groupMapper.countByGid(gid);
        //     listGroupRespDTOList.add(ListGroupRespDTO.builder()
        //                     .gid(gid)
        //                     .name(item.getName())
        //                     .sortOrder(item.getSortOrder())
        //                     .shortLinkCount(count)
        //             .build()
        //     );
        // }
        return BeanUtil.copyToList(list, ListGroupRespDTO.class);
    }

    @Override
    public List<ListLinkRespDTO> listGroupByName(String name) {

        return null;
    }

    @Override
    public void updateGroup(UpdateGroupReqDTO requestParam) {
        // TODO 后续从网关中获取username
        String username = "小明";
        int i = groupMapper.updateGroup(username, requestParam);
        if(i < 1) {
            throw new ServiceException("更新分组名失败");
        }
    }

    @Override
    public void updateGroup(String gid) {
        // TODO 后续从网关中获取username
        String username = "小明";
        int i = groupMapper.deleteByGid(username, gid);
        if(i < 1) {
            throw new ServiceException("删除分组失败");
        }
    }
}
