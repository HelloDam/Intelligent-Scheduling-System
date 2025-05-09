package com.dam.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码过期异常
 * @Author dam
 * @create 2024/4/20 18:31
 */
public class CaptchaExpiredException extends AuthenticationException {

    public CaptchaExpiredException(String message) {
        super(message);
    }

    public CaptchaExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}