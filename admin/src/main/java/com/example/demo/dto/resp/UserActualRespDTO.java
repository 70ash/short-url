package com.example.demo.dto.resp;

import lombok.Data;

/**
 * Author 70ash
 * Date 2024/1/26 22:23
 * Description:
 */
@Data
public class UserActualRespDTO {
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
    private String phone;

    /**
     * 邮箱
     */
    private String mail;
}
