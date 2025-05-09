package com.dam.valid.annotations;

import com.dam.valid.validator.TypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 注解：校验是否在所包含的数字中
 *
 * @Author dam
 * @create 2024/8/31 10:48
 */
@Documented
@Constraint(validatedBy = {TypeValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeAnno {

    //--------------- 必须包含字段 ----------------

    String message() default "字段必须是0或1";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    //--------------- 自定义字段 ----------------
    /**
     * 用来接收所包含的值
     **/
    int[] values() default {};
}
