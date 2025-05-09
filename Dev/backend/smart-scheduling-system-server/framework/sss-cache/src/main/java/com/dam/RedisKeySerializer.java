package com.dam;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

/**
 * 自定义的 Redis Key 序列化器。
 * 此类实现了 RedisSerializer 接口，用于序列化和反序列化 Redis 中的键。
 * 它允许为所有键添加一个公共前缀，并支持指定字符集进行编码和解码。
 */
@RequiredArgsConstructor
public class RedisKeySerializer implements InitializingBean, RedisSerializer<String> {

    // Redis 键的前缀
    private final String keyPrefix;

    // 字符集名称
    private final String charsetName;

    // 实际使用的字符集对象
    private Charset charset;

    /**
     * 序列化方法，将字符串键转换为字节数组。
     *
     * @param key 要序列化的字符串键
     * @return 字节数组形式的键
     * @throws SerializationException 如果序列化过程中发生错误
     */
    @Override
    public byte[] serialize(String key) throws SerializationException {
        // 构建带前缀的键
        String builderKey = keyPrefix + key;
        // 将字符串键转换为字节数组
//        return builderKey.getBytes();
        return builderKey.getBytes(Charset.defaultCharset()); // 注意这里应该使用 charset 变量
    }

    /**
     * 反序列化方法，将字节数组转换回字符串键。
     *
     * @param bytes 包含键信息的字节数组
     * @return 字符串形式的键
     * @throws SerializationException 如果反序列化过程中发生错误
     */
    @Override
    public String deserialize(byte[] bytes) throws SerializationException {
        // 使用指定的字符集将字节数组转换为字符串
        return new String(bytes, charset);
    }

    /**
     * 在所有属性被设置之后调用此方法。
     * 用于完成初始化工作，如创建字符集对象。
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 根据字符集名称创建字符集对象
        charset = Charset.forName(charsetName);
    }
}
