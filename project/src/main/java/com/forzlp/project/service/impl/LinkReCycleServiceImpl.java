package com.forzlp.project.service.impl;

import com.forzlp.project.dao.mapper.LinkMapper;
import com.forzlp.project.dto.req.LinkReCycleReq;
import com.forzlp.project.service.LinkReCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Author 70ash
 * Date 2024/4/24 下午11:48
 * Description:
 */
@Service
@RequiredArgsConstructor
public class LinkReCycleServiceImpl implements LinkReCycleService {
    private final LinkMapper linkMapper;
    /**
     * 移至回收站
     */
    @Override
    public void linkRecycle(LinkReCycleReq linkReCycleReq) {
        linkMapper.updateStatusByShortUri(linkReCycleReq.getGid(), linkReCycleReq.getShortUri());
    }
}
