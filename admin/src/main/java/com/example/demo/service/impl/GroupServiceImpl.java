package com.example.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.example.demo.common.biz.UserContext;
import com.example.demo.common.convention.excetion.ClientException;
import com.example.demo.common.convention.excetion.ServiceException;
import com.example.demo.dao.entity.Group;
import com.example.demo.dao.mapper.GroupMapper;
import com.example.demo.dto.req.GroupSaveReqDTO;
import com.example.demo.dto.req.UpdateGroupReqDTO;
import com.example.demo.dto.resp.ListGroupRespDTO;
import com.example.demo.dto.resp.ListLinkRespDTO;
import com.example.demo.service.GroupService;
import com.example.demo.util.RandomStringUtil;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.redisson.api.RBloomFilter;
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
    private final RBloomFilter<String> groupRegisterCacheBloomFilter;
    private GroupMapper groupMapper;

    @Override
    public void saveGroup(GroupSaveReqDTO requestParam) {
        String username = UserContext.getUsername();
        group(requestParam.getName(), username, requestParam.getDescription());
    }
    @Override
    public void saveGroup(String groupName, String username, String description) {
        group(groupName, username,description);
    }

    private void group(String groupName, String username, String description) {
        String gid;
        while (true) {
            gid = RandomStringUtil.generateRandomString();
            if (!groupRegisterCacheBloomFilter.contains(gid)){ // 如果布隆过滤器不存在分组标识
                break;
            }
        }
        groupRegisterCacheBloomFilter.add(gid);
        Group group = Group.builder()
                .gid(gid)
                .name(groupName)
                .username(username)
                .description(description)
                .build();
        try {
            groupMapper.insertGroup(group);
        } catch (Exception e) {
            throw new ClientException(USER_GROUP_EXIST);
        }
    }


    @Override
    public List<ListGroupRespDTO> listGroup(Integer pageSize, Integer pageNum) {
        String username = UserContext.getUsername();
        PageHelper.startPage(pageNum, pageSize);
        List<ListGroupRespDTO> list = groupMapper.selectBatchByUserName(username);
        for (ListGroupRespDTO listGroupRespDTO :list) {

        }

        return BeanUtil.copyToList(list, ListGroupRespDTO.class);
    }

    @Override
    public List<ListLinkRespDTO> listGroupByName(String name) {

        return null;
    }

    @Override
    public void updateGroup(UpdateGroupReqDTO requestParam) {
        String username = UserContext.getUsername();
        int i = groupMapper.updateGroup(username, requestParam);
        if(i < 1) {
            throw new ServiceException("更新分组名失败");
        }
    }

    @Override
    public void updateGroup(String gid) {
        String username = UserContext.getUsername();
        int i = groupMapper.deleteByGid(username, gid);
        if(i < 1) {
            throw new ServiceException("删除分组失败");
        }
    }
}
