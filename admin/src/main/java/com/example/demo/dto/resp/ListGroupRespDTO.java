package com.example.demo.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author 70ash
 * Date 2024/2/29 21:40
 * Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListGroupRespDTO {
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

    /**
     * 分组下短链接数量
     */
    private Integer shortLinkCount;
}
