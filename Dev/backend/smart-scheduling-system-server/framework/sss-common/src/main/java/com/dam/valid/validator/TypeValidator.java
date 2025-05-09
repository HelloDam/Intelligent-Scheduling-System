package com.dam.valid.validator;

import com.dam.valid.annotations.TypeAnno;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * 校验器：校验是否在所包含的数字中
 *
 * @Author dam
 * @create 2024/8/31 10:52
 */
public class TypeValidator implements ConstraintValidator<TypeAnno, Integer> {

    /**
     * 存储类型
     */
    private Set<Integer> typeSet = new HashSet<>();

    @Override
    public void initialize(TypeAnno constraintAnnotation) {
        for (int value : constraintAnnotation.values()) {
            typeSet.add(value);
        }
    }

    /**
     * 校验字段是否有效
     *
     * @param value 要校验的值
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        System.out.println("触发校验");
        return typeSet.contains(value);
    }
}
