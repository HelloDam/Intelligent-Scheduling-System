package com.dam.model.enums.system;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 *
 */
@Getter
public enum MenuCodeEnum {

    //账号状态
    //性别
    STATUS_NORMAL(0,"正常"),
    STATUS_BAN(1,"禁用"),
    ;

    private Integer code;

    private String message;

    private MenuCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}