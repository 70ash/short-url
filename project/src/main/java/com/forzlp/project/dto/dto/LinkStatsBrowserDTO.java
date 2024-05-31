package com.forzlp.project.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author 70ash
 * Date 2024/5/29 下午9:19
 * Description: 短链接浏览器记录数据
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LinkStatsBrowserDTO {
    private String browser;
    private int cnt;
    private double ratio;
}
