package com.forzlp.project.dto.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Author 70ash
 * Date 2024/5/14 下午8:57
 * Type:
 * Description:
 */
@Data
@Builder
public class LinkStatsTotalDTO {
    private Integer pv;
    private Integer uv;
    private Integer uip;
}
