package com.dam.core;

/**
 * 缓存加载器
 */
@FunctionalInterface
public interface CacheLoader<T> {
    /**
     * 从外部数据源加载数据。
     *
     * @return 加载的数据，类型为 T
     */
    T load();
}