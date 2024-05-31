package com.forzlp.project.dto.req;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Author 70ash
 * Date 2024/5/14 下午8:20
 * Type:
 * Description:
 */
@Data
public class LinkStatsReqDTO {
    private String gid;
    private String short_url;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String startTime;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String endTime;
}
