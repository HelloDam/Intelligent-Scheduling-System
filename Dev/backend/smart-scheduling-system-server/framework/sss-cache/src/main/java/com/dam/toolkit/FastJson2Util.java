package com.dam.toolkit;

import com.alibaba.fastjson2.util.ParameterizedTypeImpl;

import java.lang.reflect.Type;

/**
 * FastJson2 工具类
 */
public final class FastJson2Util {

    /**
     * 构建类型信息。
     * 此方法用于根据给定的类型参数构建一个 ParameterizedType 实例。
     * 这对于解析复杂类型的 JSON 数据特别有用，尤其是处理带有泛型的类。
     *
     * @param types 可变长度的类型参数数组
     * @return 构建的 ParameterizedType 实例
     */
    public static Type buildType(Type... types) {
        ParameterizedTypeImpl beforeType = null;
        if (types != null && types.length > 0) {
            // 如果只有一个类型参数，则直接返回一个新的 ParameterizedTypeImpl 实例
            if (types.length == 1) {
                return new ParameterizedTypeImpl(new Type[]{null}, null, types[0]);
            }

            // 循环遍历类型参数数组，从最后一个开始向前构建 ParameterizedType 实例
            for (int i = types.length - 1; i > 0; i--) {
                // 使用当前的类型参数和之前构建的 ParameterizedType 构建新的 ParameterizedType 实例
                beforeType = new ParameterizedTypeImpl(
                        new Type[]{beforeType == null ? types[i] : beforeType}, // 实际类型参数
                        null, // 没有类型参数的所有者类型
                        types[i - 1] // 原始类型
                );
            }
        }
        return beforeType;
    }
}
