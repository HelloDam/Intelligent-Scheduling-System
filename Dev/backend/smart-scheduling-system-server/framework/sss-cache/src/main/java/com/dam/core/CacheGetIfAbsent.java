package com.dam.core;

/**
 * 缓存查询为空时的处理接口。
 * 当缓存中不存在某个键对应的数据时，可以通过实现该接口来定义执行的逻辑。
 */
@FunctionalInterface
public interface CacheGetIfAbsent<T> {

    /**
     * 当缓存查询结果为空时执行的逻辑。
     *
     * @param param 用于执行逻辑的参数，类型为 T
     */
    void execute(T param);
}
