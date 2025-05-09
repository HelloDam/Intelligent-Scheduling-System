package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dam.model.entity.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * user_position中间表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 15:17:00
 */
@Data
@TableName("user_position")
public class UserPositionEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 职位id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long positionId;

}
