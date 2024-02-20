package com.forzlp.admin.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forzlp.admin.common.serilize.PhoneDesensitizationSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 70ash
 * @Date 2024/1/25 11:50
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRespDTO {
    /**
     * ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * 邮箱
     */
    private String mail;
}
