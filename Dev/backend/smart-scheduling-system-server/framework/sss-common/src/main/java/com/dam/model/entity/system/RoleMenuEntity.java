package com.dam.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.dam.model.entity.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * role_menu中间表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-06 15:50:30
 */
@Data
@TableName("role_menu")
public class RoleMenuEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long roleId;
    /**
     * 菜单id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long menuId;

}
