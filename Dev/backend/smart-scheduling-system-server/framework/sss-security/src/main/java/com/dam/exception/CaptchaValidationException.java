package com.dam.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码不正确异常
 * @Author dam
 * @create 2024/4/20 18:31
 */
public class CaptchaValidationException extends AuthenticationException {

    public CaptchaValidationException(String message) {
        super(message);
    }

    public CaptchaValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}