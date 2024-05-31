package com.forzlp.project.dto.resp;

// import com.baomidou.mybatisplus.annotation.IdType;
// import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * Author 70ash
 * Date 2024/4/11 22:32
 * Description:
 */
@Data
public class LinkSearchRespDTO {
    private static final long serialVersionUID = 1L;

    // @TableId(type = IdType.AUTO)
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
    private String shortUri;

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
     * 描述
     */
    private String describe;

    /**
     * 描述
     */
    private String title;

    /**
     * 网站标识
     */
    private String icon;

    /**
     * pv
     */
    private Integer pv;


    /**
     * uv
     */
    private Integer uv;


    /**
     * uip
     */
    private Integer uip;

}
