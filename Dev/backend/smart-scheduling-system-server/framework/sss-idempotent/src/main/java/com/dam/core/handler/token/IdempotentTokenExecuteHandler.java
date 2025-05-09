package com.dam.core.handler.token;

import cn.hutool.core.util.StrUtil;
import com.dam.constant.KeyConstants;
import com.dam.core.aop.IdempotentParam;
import com.dam.exception.SSSException;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.properties.IdempotentTokenProperties;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

/**
 * 基于 Token 验证请求幂等性, 通常应用于 RestAPI 方法
 */
@RequiredArgsConstructor
public final class IdempotentTokenExecuteHandler implements IdempotentTokenService {
    /**
     * Redis 操作模板，用于存储和验证幂等Token
     */
    private final StringRedisTemplate redisTemplate;
    /**
     * 幂等相关配置属性
     */
    private final IdempotentTokenProperties idempotentTokenProperties;

    /**
     * 如果配置文件里面没有写明，则使用该默认值
     */
    private static final String TOKEN_PREFIX_KEY = KeyConstants.IDEMPOTENT_PREFIX + "token:";

    /**
     * 幂等Token在Redis中的默认过期时间（毫秒），若配置文件未指定，则使用此值
     */
    private static final long TOKEN_EXPIRED_TIME = 6000;

    @Override
    public IdempotentParam buildParam(ProceedingJoinPoint joinPoint) {
        return new IdempotentParam();
    }

    /**
     * 创建一个新的幂等Token。生成一个全局唯一的UUID，并添加前缀（从配置或默认值获取），
     * 将此Token作为键存入Redis，并设置一个过期时间（从配置或默认值获取）。
     * 值为空字符串，因为仅用于标识Token的存在，不存储额外数据。
     *
     * @return 新创建的幂等Token字符串
     */
    @Override
    public String createToken() {
        String token = Optional.ofNullable(Strings.emptyToNull(idempotentTokenProperties.getPrefix())).orElse(TOKEN_PREFIX_KEY) + UUID.randomUUID();
        redisTemplate.opsForValue().set(token, "", Optional.ofNullable(idempotentTokenProperties.getTimeout()).orElse(TOKEN_EXPIRED_TIME));
        return token;
    }

    /**
     * 处理幂等Token验证逻辑。在接收到业务请求时，从请求头或请求参数中提取幂等Token，
     * 然后验证该Token在Redis中的存在性。如果存在，则删除该Token并允许业务逻辑执行；
     * 否则，抛出异常表示重复请求或无效Token。
     *
     * @param wrapper 幂等参数包装器
     */
    @Override
    public void handler(IdempotentParam wrapper) {
        // 获取当前请求上下文中的HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        // 从请求头或者请求参数中获取token对应的值
        String token = request.getHeader(KeyConstants.TOKEN_KEY);
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(KeyConstants.TOKEN_KEY);
            if (StrUtil.isBlank(token)) {
                // 如果请求中未找到Token，抛出异常
                throw new SSSException(ResultCodeEnum.IDEMPOTENT_TOKEN_NULL_ERROR);
            }
        }
        // 验证并删除Token
        Boolean tokenDelFlag = redisTemplate.delete(token);
        if (!tokenDelFlag) {
            // 删除失败，说明token对应的请求已经被执行过了，不能再执行了
            String errMsg = StrUtil.isNotBlank(wrapper.getIdempotent().message())
                    ? wrapper.getIdempotent().message()
                    : ResultCodeEnum.IDEMPOTENT_TOKEN_DELETE_ERROR.getMessage();
            throw new SSSException(ResultCodeEnum.IDEMPOTENT_TOKEN_DELETE_ERROR.getCode(), errMsg);
        }
    }
}
