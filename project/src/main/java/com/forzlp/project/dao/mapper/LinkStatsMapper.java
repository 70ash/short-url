package com.forzlp.project.dao.mapper;

import com.forzlp.project.dao.entity.LinkStats;
import com.forzlp.project.dto.dto.LinkLocaleStatsDTO;
import com.forzlp.project.dto.req.LinkStatsReqDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Author 70ash
 * Date 2024/4/28 下午7:39
 * Description:
 */

public interface LinkStatsMapper {
    void updateLinkStats(LinkStats linkStats);

    void insertLinkStats(LinkStats linkStats);

    List<LinkLocaleStatsDTO> listLocaleByShortLink(@Param("param") LinkStatsReqDTO requestParam);
}
