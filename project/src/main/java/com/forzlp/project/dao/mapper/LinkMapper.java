package com.forzlp.project.dao.mapper;

import com.forzlp.project.dao.entity.Link;
import com.forzlp.project.dao.entity.LinkStats;
import com.forzlp.project.dto.dto.*;
import com.forzlp.project.dto.req.LinkSearchReqDTO;
import com.forzlp.project.dto.req.LinkStatsReqDTO;
import com.forzlp.project.dto.resp.LinkSearchRespDTO;
import com.forzlp.project.dto.resp.ShortLinkRecyclePageRespDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Author 70ash
 * Date 2024/3/2 14:43
 * Description:
 */
public interface LinkMapper {
    /** 由短链接查询分组标识 **/
    String gotoByUri(String shortUri);

    /** 插入短链接 **/
    void insertLink(Link link);

    // Link selectByFullShortUrl(String fullShortUrl);

    /** 分页查询短链接 **/
    List<LinkSearchRespDTO> pageLink(LinkSearchReqDTO requestParam);

    /** 由短链接和分组标识查询原始链接 **/
    String selectOriginUrlByUriAndGid(@Param("shortUri") String shortUri, @Param("gid") String gid);

    /** 由短链接和分组标识查询原始链接 **/
    Link selectLinkByUriAndGid(@Param("shortUri") String shortUri, @Param("gid") String gid);

    /** 移动短链接到回收站 **/
    int updateRecycleByShortUri(@Param("gid") String gid, @Param("shortUri")String shortUri);

    /** 移出回收站 **/
    int updateRecoverByShortUri(@Param("gid") String gid, @Param("shortUri")String shortUri);

    /** 回收站分页查询 **/
    List<ShortLinkRecyclePageRespDTO> selectRecycleLink();

    int upDelLinkByGidAndShortUri(@Param("gid") String gid, @Param("shortUri")String shortUri);

    List<LinkStats> selectLinkStats(LinkStatsReqDTO requestParam);

    List<LinkStatsHighIpDTO> selectHighRatioIpLinkStats(String shortUrl);

    List<LinkStatsHourlyDTO> selectLinkStatsHourly(String shortUrl);

    List<LinkStatsWeeklyDTO> selectLinkStatsWeekly(String shortUrl);

    List<LinkStatsOsDTO> selectLinkStatsOs(String shortUrl);

    List<LinkStatsBrowserDTO> selectLinkStatsBrowser(String shortUrl);

    Date selectLinkCTimeByShortUri(@Param("gid") String gid, @Param("shortUrl") String shortUrl);

    List<LinkRecentStatsDTO> select7StatsHourly(String shortUrl);

    List<LinkRecentStatsDTO> select24StatsHourly(String shortUrl);
}
