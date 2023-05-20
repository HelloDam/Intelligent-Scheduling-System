package com.dam.model.enums.quartz;



public enum QuartzEnum {

    J_GROUP_WORK_NOTICE(0,"工作通知"),
    J_GROUP_REST_NOTICE(1,"工作通知"),
    T_NAME_SEND_WORK_NOTICE(0,"发送工作通知"),
    T_NAME_SEND_REST_NOTICE(1,"发送休息通知"),
    T_GROUP_DAILY_NOTICE(0,"每天执行一次");

    private Integer code;
    private String name;

    QuartzEnum(Integer code, String name) {
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
