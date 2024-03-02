package com.example.demo.controller;

import com.example.demo.common.convention.result.Result;
import com.example.demo.common.convention.result.Results;
import com.example.demo.dto.req.UpdateGroupReqDTO;
import com.example.demo.dto.resp.ListGroupRespDTO;
import com.example.demo.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author 70ash
 * @Date 2024/2/20 21:30
 * @Description:
 */
@RestController
@RequestMapping("/short-link/group")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;
    // 新增短链接分组
    @PostMapping("/save")
    public Result<Void> saveGroup(@RequestParam("groupName") String groupName) {
        groupService.saveGroup(groupName);
        return Results.success();
    }
    // 修改短链接分组
    @PostMapping()
    public Result<Void> updateGroup(@RequestBody UpdateGroupReqDTO requestParam) {
        groupService.updateGroup(requestParam);
        return Results.success();
    }
    // 删除短链接分组
    @DeleteMapping()
    public Result<Void> updateGroup(String gid) {
        groupService.updateGroup(gid);
        return Results.success();
    }
    // 查看短链接分组
    @GetMapping("/list")
    public Result<List<ListGroupRespDTO>> listGroup() {
        List<ListGroupRespDTO> list = groupService.listGroup();
        return Results.success(list);
    }
}
