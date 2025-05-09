package com.dam.core.handler.token;

import com.dam.constant.KeyConstants;
import com.dam.model.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基于 Token 验证请求幂等性控制器
 */
@RestController
@RequiredArgsConstructor
public class IdempotentTokenController {

    private final IdempotentTokenService idempotentTokenService;

    /**
     * 请求申请Token
     */
    @GetMapping("/token")
    public R createToken() {
        return R.ok().addData(KeyConstants.TOKEN_KEY, idempotentTokenService.createToken());
    }
}
