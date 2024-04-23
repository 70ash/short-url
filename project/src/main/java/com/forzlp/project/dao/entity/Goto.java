package com.forzlp.project.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author 70ash
 * Date 2024/4/19 上午12:57
 * Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Goto {
    private String gid;
    private String shortUri;
}
