package com.dam.config.Mybatis;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;

import com.dam.toolkit.SnowflakeIdUtil;
import org.springframework.stereotype.Component;

/**
 * @Author dam
 * @create 2024/1/19 17:43
 */
@Component
public class CustomIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        return SnowflakeIdUtil.nextId();
    }

}
