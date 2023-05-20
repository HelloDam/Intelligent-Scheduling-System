package com.dam.model.enums.system;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 *
 */
@Getter
public enum UserCodeEnum {

    //用户类型
    TYPE_SYSTEM_MANAGER(0,"系统管理员"),
    TYPE_ENTERPRISE_MANAGER(1,"企业管理员"),
    TYPE_STORE_MANAGER(2,"门店管理员"),
    TYPE_ORDINARY_USER(10,"普通用户"),

    //性别
    Gender_MAN(0,"男性"),
    Gender_WOMAN(1,"女性"),

    //账号状态
    //性别
    STATUS_NORMAL(0,"正常"),
    STATUS_BAN(1,"已封禁"),
    ;

    private Integer code;

    private String message;

    private UserCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}