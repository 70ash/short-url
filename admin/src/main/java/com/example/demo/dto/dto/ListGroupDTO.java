package com.example.demo.dto.dto;

import lombok.Data;

/**
 * Author 70ash
 * Date 2024/4/25 上午12:17
 * Description: 查询短链接分组DTO
 */
@Data
public class ListGroupDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 分组名称
     */
    private String name;
    /**
     * 分组排序
     */
    private Integer sortOrder;
}
