package com.dam.model.vo.system;


import lombok.Data;

/**
 * 登录对象
 */
@Data
public class LoginVo {

    /**
     * 手机号
     */
    private String username;

    /**
     * 密码
     */
    private String password;
    /**
     * 用来获取redis的验证码
     */
    private String uuid;
    /**
     * 验证码
     */
    private String verificationCode;

}
