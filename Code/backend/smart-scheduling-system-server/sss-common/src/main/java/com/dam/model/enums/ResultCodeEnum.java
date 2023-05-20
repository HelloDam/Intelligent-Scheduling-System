package com.dam.model.enums;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 */
@Getter
public enum ResultCodeEnum {
    ////通用
    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),
    DATA_ERROR(204, "数据异常"),
    ILLEGAL_REQUEST(205, "非法请求"),
    REPEAT_SUBMIT(206, "重复提交"),
    ARGUMENT_VALID_ERROR(210, "参数校验异常"),
    Feign_ERROR(222, "远程服务调用失败"),

    ////权限模块
    ///user
    LOGIN_AUTH(1000, "未登录"),
    PERMISSION(1001, "没有权限"),
    //账号
    ACCOUNT_ERROR(1002, "账号不正确"),
    ACCOUNT_WRONGFUL(1003, "账号不合法"),
    ACCOUNT_EXIST_ERROR(1004, "账号已经存在"),
    ACCOUNT_STOP(1005, "账号已停用"),
    //密码
    PASSWORD_ERROR(1006, "密码不正确"),
    PASSWORD_WRONGFUL(1007, "密码不合法"),
    //邮箱
    MAIL_SEND_ERROR(1008, "邮箱验证码发送错误"),
    MAIL_EXIST_ERROR(1010, "邮箱已经被注册"),
    CODE_ERROR(1011, "验证码错误"),
    ///menu
    NODE_ERROR(1100, "该节点下有子节点，不可以删除"),

    ////流量控制
    TOO_MANY_REQUEST(2000, "当前请求太多，请稍后再试"),
    ;

    private Integer code;

    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}