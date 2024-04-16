package com.forzlp.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author 70ash
 * Date 2024/2/29 22:04
 * Description:
 */
@Data
@Builder
public class Link extends BaseDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id     primary key
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUrl;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 启用状态，0：启用，1：启用
     */
    private Integer enableStatus;

    /**
     * 有效时间，0：永久有效，1：自定义
     */
    private Integer validType;

    /**
     * 有效时间
     * JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8") 用于前后端Date类型数据传输，使用这个注解后前端传输Date时可以使用字符串
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date validTime;
}
