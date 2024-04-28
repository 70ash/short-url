package com.forzlp.project.service;

import com.forzlp.project.dto.req.LinkDeleteReqDTO;
import com.forzlp.project.dto.req.linkReCycleReqDTO;
import com.forzlp.project.dto.req.LinkRecoverReqDTO;
import com.forzlp.project.dto.resp.ShortLinkRecyclePageRespDTO;

import java.util.List;

/**
 * Author 70ash
 * Date 2024/4/24 下午11:47
 * Description:
 */
public interface LinkReCycleService {
    /**
     * 移至回收站
     */
    void linkRecycle(linkReCycleReqDTO linkReCycleReqDTO);

    /**
     * 回收站分页查询短链接
     */
    List<ShortLinkRecyclePageRespDTO> linkPageRecycle(Integer pageNum, Integer pageSize);

    /**
     * 移除回收站
     */
    void linkRecover(LinkRecoverReqDTO requestParam);

    void linkDelete(LinkDeleteReqDTO requestParam);
}
