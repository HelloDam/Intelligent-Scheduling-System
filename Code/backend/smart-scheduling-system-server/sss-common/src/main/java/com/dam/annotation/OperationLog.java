package com.dam.annotation;


import com.dam.model.enums.log.BusinessTypeEnum;
import com.dam.model.enums.log.OperatorTypeEnum;

import java.lang.annotation.*;


/**
 * @author dam
 */
@Target({ElementType.PARAMETER,ElementType.METHOD})//表示注解可以用在参数、方法上面
@Retention(RetentionPolicy.RUNTIME)//作用范围：运行时
@Documented
public @interface OperationLog {

    /**
     * 模块名称
     */
    public String title() default "";

    /**
     * 方法名称
     */
    public String detail() default "";

    /**
     * 业务类型
     */
    public BusinessTypeEnum businessType() default BusinessTypeEnum.OTHER;

    /**
     * 操作人类别（手机用户、网页用户）
     */
    public OperatorTypeEnum operatorType() default OperatorTypeEnum.MANAGE;

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    public boolean isSaveResponseData() default true;

}
