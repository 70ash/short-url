package com.forzlp.project.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

/**
 * Author 70ash
 * Date 2024/5/29 下午11:06
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkStatsProvinceDTO {
    /**
     * 省份为键，数量为值
     */
    List<HashMap<String, Integer>> provinceMap;
}
