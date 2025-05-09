package com.dam.constant;

public class RabbitMqConstant {
    ////企业注册
    public static final String ENTERPRISE_REGISTER_QUEUE = "sss.enterprise-register.queue";
    public static final String ENTERPRISE_REGISTER_EXCHANGE = "sss.enterprise-register-event-exchange";
    public static final String ENTERPRISE_REGISTER_ROUTER_KEY = "sss.enterprise.register";
    ////邮件发送
    public static final String MAIL_SENDER_QUEUE = "sss.mail-sender.queue";
    public static final String MAIL_SENDER_EXCHANGE = "sss.mail-sender.exchange";
    public static final String MAIL_SENDER_ROUTER_KEY = "sss.mail-sender.routerKey";
}
