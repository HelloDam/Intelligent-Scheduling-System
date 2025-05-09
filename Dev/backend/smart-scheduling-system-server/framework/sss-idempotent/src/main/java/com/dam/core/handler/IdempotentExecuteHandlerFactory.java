package com.dam.core.handler;

import com.dam.ApplicationContextHolder;
import com.dam.core.handler.param.IdempotentParamService;
import com.dam.core.handler.spel.mq.IdempotentSpELByMQExecuteHandler;
import com.dam.core.handler.spel.restful.IdempotentSpELByRestAPIExecuteHandler;
import com.dam.core.handler.token.IdempotentTokenService;
import com.dam.enums.IdempotentSceneEnum;
import com.dam.enums.IdempotentTypeEnum;

/**
 * 幂等执行处理器工厂
 * 简单工厂模式
 */
public final class IdempotentExecuteHandlerFactory {

    /**
     * 根据枚举参数获取对应的幂等执行处理器handler
     *
     * @param scene 指定幂等验证场景类型
     * @param type  指定幂等处理类型
     * @return 幂等执行处理器
     */
    public static IdempotentExecuteHandler getInstance(IdempotentSceneEnum scene, IdempotentTypeEnum type) {
        IdempotentExecuteHandler result = null;
        switch (scene) {
            case RESTAPI: {
                switch (type) {
                    case PARAM:
                        result = ApplicationContextHolder.getBean(IdempotentParamService.class);
                        break;
                    case TOKEN:
                        result = ApplicationContextHolder.getBean(IdempotentTokenService.class);
                        break;
                    case SPEL:
                        result = ApplicationContextHolder.getBean(IdempotentSpELByRestAPIExecuteHandler.class);
                        break;
                    default: {
                    }
                }
                break;
            }
            case MQ:
                result = ApplicationContextHolder.getBean(IdempotentSpELByMQExecuteHandler.class);
                break;
            default: {
            }
        }
        return result;
    }
}
