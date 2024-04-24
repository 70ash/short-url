package com.forzlp.project.service;

import com.forzlp.project.dto.req.LinkReCycleReq;

/**
 * Author 70ash
 * Date 2024/4/24 下午11:47
 * Description:
 */
public interface LinkReCycleService {
    /**
     * 移至回收站
     */
    void linkRecycle(LinkReCycleReq linkReCycleReq);
}
