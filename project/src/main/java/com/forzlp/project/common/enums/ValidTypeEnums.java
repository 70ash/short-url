package com.forzlp.project.common.enums;


/**
 * Author 70ash
 * Date 2024/1/26 23:47
 * Description:
 */

import lombok.AllArgsConstructor;
@AllArgsConstructor
public enum ValidTypeEnums {
    /**
     * 永久有效
     */
    PERMANENT(0),
    /**
     * 自定义
     */
    CUSTOM(1);

    private final int type;
}
