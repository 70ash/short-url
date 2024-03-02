package com.example.demo.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author 70ash
 * @Date 2024/3/2 14:07
 * @Description:
 */
@Data
public class LinkCreateReqDTO {

    /**
     * 分组标识
     */
    private String gid;
    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 有效时间，0：永久有效，1：自定义
     */
    private Integer validType;

    /**
     * 有效时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date validTime;
}
