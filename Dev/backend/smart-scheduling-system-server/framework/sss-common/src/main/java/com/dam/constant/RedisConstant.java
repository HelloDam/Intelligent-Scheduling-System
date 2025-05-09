package com.dam.constant;

public class RedisConstant {
    /**
     * 系统前缀
     */
    public static final String PREFIX = "sss:";

    public static final String SEPARATE = ":";

    /**
     * 权限
     */
    public static final String AUTHORITY_PERMISSION = PREFIX + "authority:permissionList:";

    /**
     * 验证码前缀
     */
    public static final String Verification_Code = PREFIX + "login:verificationCode:";

    /**
     * 验证码前缀
     */
    public static final String Mail_Code = PREFIX + "mail:code:";

    ////shift-scheduling-calculate-service模块
    /**
     * 模块 shift-scheduling-calculate-service
     */
    public static final String MODULE_SHIFT_SCHEDULING_CALCULATE_DATE = PREFIX + "module:shiftSchedulingCalculate:date";
    public static final String MODULE_SHIFT_SCHEDULING_CALCULATE_SHIFT = PREFIX + "module:shiftSchedulingCalculate:shift";
    /**
     * 企业管理员首页统计
     */
    public static final String MODULE_SHIFT_SCHEDULING_CALCULATE_ENTERPRISE_STATISTIC = PREFIX + "module:shiftSchedulingCalculate:enterpriseStatistic";
    /**
     * 门店管理员首页统计
     */
    public static final String MODULE_SHIFT_SCHEDULING_CALCULATE_STORE_STATISTIC = PREFIX + "module:shiftSchedulingCalculate:storeStatistic";
    /**
     * 企业注册
     */
    public static final String ENTERPRISE_REGISTER = PREFIX + "enterprise:register";
    /**
     * 用户注册布隆过滤器
     */
    public static final String BLOOM_USER_REGISTER = PREFIX + "bloom:user:register";

    ////////// 限流 ///////////
    public static final String IP_FLOW_CONTROL = PREFIX + "ip-flow-control";
}
