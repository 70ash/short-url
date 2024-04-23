package com.forzlp.project.dao.mapper;

import com.forzlp.project.dao.entity.Link;
import com.forzlp.project.dto.req.LinkSearchReqDTO;
import com.forzlp.project.dto.resp.LinkSearchRespDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Author 70ash
 * Date 2024/3/2 14:43
 * Description:
 */
public interface LinkMapper {
    String gotoByUri(String shortUri);

    void insertLink(Link link);

    Link selectByFullShortUrl(String fullShortUrl);

    List<LinkSearchRespDTO> pageLink(LinkSearchReqDTO requestParam);

    String selectOriginUrlByUriAndGid(@Param("shortUri") String shortUri, @Param("gid") String gid);
}
