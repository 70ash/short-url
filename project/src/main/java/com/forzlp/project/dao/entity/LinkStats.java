package com.forzlp.project.dao.entity;

import com.forzlp.project.dto.dto.LinkRecentStatsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Author 70ash
 * Date 2024/4/28 下午7:35
 * Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkStats extends BaseDO{
    /**
     * 主键
     */
    private Integer id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接
     */
    private String shortUri;

    /** ip地址 **/
    private String ip;

    /**
     * 访问量
     */
    private Integer pv;

    /**
     * 独立访问量
     */
    private Integer uv;

    /**
     * 独立ip数
     */
    private Integer uip;

    private List<LinkRecentStatsDTO> recentStats;

    /** 操作系统 **/
    private String os;

    /** 浏览器 **/
    private String browser;

    /**
     * 省份
     */
    private String province;

    /** 城市 **/
    private String city;

    /**
     * 日期
     */
    private Date date;

    /**
     * 小时分布
     */
    private Integer hour;

    /**
     * 星期分布
     */
    private Integer week;

}
