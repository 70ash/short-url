package com.forzlp.project.dao.mapper;

import com.forzlp.project.dao.entity.Link;

/**
 * @Author 70ash
 * @Date 2024/3/2 14:43
 * @Description:
 */
public interface LinkMapper {
    void insertLink(Link link);

    Link selectByFullShortUrl(String fullShortUrl);
}
