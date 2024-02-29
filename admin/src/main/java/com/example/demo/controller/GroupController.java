package com.example.demo.controller;

import com.example.demo.common.convention.result.Result;
import com.example.demo.common.convention.result.Results;
import com.example.demo.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/save")
    public Result saveGroup(@RequestParam("groupName") String groupName) {
        groupService.saveGroup(groupName);
        return Results.success();
    }
}
