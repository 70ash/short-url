package com.forzlp.project.dto.req;

import lombok.Data;

/**
 * Author 70ash
 * Date 2024/4/26 下午4:31
 * Description: 移除短链接Req
 */
@Data
public class LinkRecoverReqDTO {
    private String gid;
    private String shortUri;
}
