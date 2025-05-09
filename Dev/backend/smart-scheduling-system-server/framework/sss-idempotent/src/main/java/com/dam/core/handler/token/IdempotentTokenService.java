package com.dam.core.handler.token;

import com.dam.core.handler.IdempotentExecuteHandler;

/**
 * Token 实现幂等接口
 *
 */
public interface IdempotentTokenService extends IdempotentExecuteHandler {

    /**
     * 创建幂等验证Token
     */
    String createToken();
}
