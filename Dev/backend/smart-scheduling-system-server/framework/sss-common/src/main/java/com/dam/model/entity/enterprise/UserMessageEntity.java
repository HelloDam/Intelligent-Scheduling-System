package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dam.model.entity.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户-消息中间表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-20 15:43:46
 */
@Data
@TableName("user_message")
public class UserMessageEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 消息id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long messageId;

}
