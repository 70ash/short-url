package com.forzlp.project.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author 70ash
 * Date 2024/5/30 下午6:54
 * Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkRecentStatsDTO {
    private String id;
    private int pv;
    private int uip;
    public LinkRecentStatsDTO(String id) {
        this.id = id;
    }
}
