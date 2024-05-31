package com.forzlp.project.dto.dto;

import lombok.Data;

/**
 * Author 70ash
 * Date 2024/5/29 下午11:13
 * Description:
 */
@Data
public class LinkStatsHighIpDTO {
    private String ip;
    private int cnt;
    private double ratio;
}
