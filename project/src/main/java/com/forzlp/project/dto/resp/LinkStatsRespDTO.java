package com.forzlp.project.dto.resp;

import com.forzlp.project.dto.dto.*;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * Author 70ash
 * Date 2024/5/14 下午8:21
 * Type:
 * Description:
 */
@Data
@Builder
public class LinkStatsRespDTO {
    private int pv;
    // private int uv;
    private int uip;
    List<HashMap> provinceStats;
    List<LinkRecentStatsDTO> list;
    List<LinkStatsHourlyDTO> dayStats;
    List<LinkStatsWeeklyDTO> weekStats;
    List<LinkStatsOsDTO> osStats;
    List<LinkStatsBrowserDTO> browserStats;
    List<LinkStatsHighIpDTO> highIpStats;
}
