package com.dam.constant;

/**
 * @Author dam
 * @create 2024/4/6 21:29
 */
public class KeyConstants {
    /**
     * 幂等前缀
     */
    public static String IDEMPOTENT_PREFIX = "sss:idempotent:";

    /**
     * 使用token机制，发起正式请求时的请求头或者请求参数的token名称
     */
    public static String TOKEN_KEY = "idempotentToken";
}
