package com.forzlp.project.dto.dto;

import lombok.Data;

/**
 * Author 70ash
 * Date 2024/5/29 下午11:39
 * Description: 操作系统访问数据
 */
@Data
public class LinkStatsOsDTO {
    private String os;
    private int cnt;
    private double ratio;
}
