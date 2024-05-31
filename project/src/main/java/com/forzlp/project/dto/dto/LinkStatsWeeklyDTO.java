package com.forzlp.project.dto.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author 70ash
 * Date 2024/5/14 下午8:54
 * Type:
 * Description:
 */
@Data
@NoArgsConstructor
public class LinkStatsWeeklyDTO {
    private int day;
    private int cnt;
    private double ratio;
}
