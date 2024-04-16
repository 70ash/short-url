package com.forzlp.project.dto.req;

import lombok.Data;

/**
 * Author 70ash
 * Date 2024/4/11 22:30
 * Description:
 */
@Data
public class LinkSearchReqDTO {
    // 分组标识
    private String gid;
    // 当前页码
    private Integer current;
    // 每页数量
    private Integer size;
}
