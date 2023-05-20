package com.dam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {

    /**
     * 设置主键自增
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 是否删除 0：未删除 1：已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted = 0;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Date updateTime;
}
