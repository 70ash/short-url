package com.forzlp.project.utils;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Optional;

import static com.forzlp.project.common.constant.LinkConstant.DEFAULT_VALID_TIME;

/**
 * Author 70ash
 * Date 2024/4/23 下午5:57
 * Description: 短链接缓存时间工具类
 */
public class LinkUtil {
    /**
     * 获取短链接缓存有效时间
     * @param validDate 有效时间
     * @return 有效时间戳
     */
    public static Long getLinkCacheValidTime(Date validDate) {
        return Optional.ofNullable(validDate)
                .map(each -> DateUtil.between(new Date(), validDate, DateUnit.SECOND))
                .orElse(DEFAULT_VALID_TIME);
    }
}
