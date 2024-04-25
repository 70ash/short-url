package com.example.demo.dto.req;

import lombok.Data;

/**
 * Author 70ash
 * Date 2024/3/1 17:59
 * Description:
 */
@Data
public class UpdateGroupReqDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;
}
