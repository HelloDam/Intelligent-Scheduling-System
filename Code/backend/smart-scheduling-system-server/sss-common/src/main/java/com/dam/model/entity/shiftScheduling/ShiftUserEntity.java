package com.dam.model.entity.shiftScheduling;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.dam.model.entity.BaseEntity;
import lombok.Data;

/**
 * 班次_用户中间表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-04 14:30:17
 */
@Data
@TableName("shift_user")
public class ShiftUserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设置主键自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Date updateTime;

    /**
     * 班次id
     */
    private Long shiftId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 记录用户当时的职位，可能后面升职了
     */
    private Long positionId;

}
