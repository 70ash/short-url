package com.forzlp.project.dto.dto;

import lombok.Data;

/**
 * Author 70ash
 * Date 2024/5/29 下午11:06
 * Description:
 */
@Data
public class LinkLocaleStatsDTO {

    /**
     * 访问量
     */
    private Integer cnt;

    /**
     * 省份名称
     */
    private String province;

    /**
     * 市名称
     */
    private String city;
}
