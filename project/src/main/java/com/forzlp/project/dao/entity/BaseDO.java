package com.forzlp.project.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * Author 70ash
 * Date 2024/2/20 16:39
 * Description:
 */
@Data
public class BaseDO {
    /**
     * 创建时间
     */
    // @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    // @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标识 0：未删除 1：已删除
     */
    // @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;
}
