package com.dam.model.enums.system;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 *
 */
@Getter
public enum RoleCodeEnum {

    //菜单类型
    TYPE_SYSTEM_ROLE(0,"系统内角色 赋予企业管理员"),
    TYPE_ENTERPRISE_ROLE(1,"企业内角色 赋予门店管理员"),
    TYPE_STORE_ROLE(2,"门店内角色 普通用户");


    private Integer code;

    private String message;

    private RoleCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}