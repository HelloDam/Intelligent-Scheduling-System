package com.dam.model.enums.log;

public enum OperatorTypeEnum {
    /**
     * 其它
     */
    OTHER(0,"其他"),

    /**
     * 后台用户
     */
    MANAGE(1,"后台用户"),

    /**
     * 手机端用户
     */
    MOBILE(2,"手机端用户");

    private Integer code;
    private String name;

    OperatorTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
