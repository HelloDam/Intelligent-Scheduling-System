package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dam.model.entity.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 职位表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
@Data
@TableName("position")
public class PositionEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name;
    /**
     * 职位描述
     */
    private String description;
    /**
     * 所有关联的门店,每个门店所设置的职位不一定一样
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long storeId;
    /**
     * 没有父元素设置为-1
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    /**
     * 下级列表
     */
    @TableField(exist = false)
    private List<PositionEntity> children;
    /**
     * 是否选中
     */
    @TableField(exist = false)
    private boolean isSelect;

}
