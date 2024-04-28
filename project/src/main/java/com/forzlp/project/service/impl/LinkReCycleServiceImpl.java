package com.forzlp.project.service.impl;

import com.forzlp.project.common.convention.excetion.ServiceException;
import com.forzlp.project.dao.mapper.LinkMapper;
import com.forzlp.project.dto.req.LinkDeleteReqDTO;
import com.forzlp.project.dto.req.linkReCycleReqDTO;
import com.forzlp.project.dto.req.LinkRecoverReqDTO;
import com.forzlp.project.dto.resp.ShortLinkRecyclePageRespDTO;
import com.forzlp.project.service.LinkReCycleService;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

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
    public void linkRecycle(@RequestBody linkReCycleReqDTO linkReCycleReqDTO) {
        int i = linkMapper.updateRecycleByShortUri(linkReCycleReqDTO.getGid(), linkReCycleReqDTO.getShortUri());
        if (i == 0) {
            throw new ServiceException("移至回收站失败");
        }
    }

    @Override
    public List<ShortLinkRecyclePageRespDTO> linkPageRecycle(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ShortLinkRecyclePageRespDTO> list = linkMapper.selectRecycleLink();
        return list;
    }

    @Override
    public void linkRecover(@RequestBody LinkRecoverReqDTO requestParam) {
        int i = linkMapper.updateRecoverByShortUri(requestParam.getGid(), requestParam.getShortUri());
        if (i == 0) {
            throw new ServiceException("恢复失败");
        }
    }

    @Override
    public void linkDelete(LinkDeleteReqDTO requestParam) {
        int i = linkMapper.upDelLinkByGidAndShortUri(requestParam.getGid(), requestParam.getShortUri());
        if (i == 0) {
            throw new ServiceException("删除失败");
        }
    }
}
