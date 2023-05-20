package com.dam.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.List;

import com.dam.model.entity.BaseEntity;
import lombok.Data;

/**
 * 菜单表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-06 15:50:30
 */
@Data
@TableName("menu")
public class MenuEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;


	/**
	 * 父id
	 */
	private Long parentId;
	/**
	 * 菜单名称
	 */
	private String name;
	/**
	 * 菜单类型
	 */
	private Integer type;
	/**
	 * 路由地址
	 */
	private String path;
	/**
	 * 组件路径
	 */
	private String component;
	/**
	 * 权限标识
	 */
	private String perms;
	/**
	 * 图标
	 */
	private String icon;
	/**
	 * 排序字段
	 */
	private Integer sort;
	/**
	 *  0:正常 1:禁用
	 */
	private Integer status;
	/**
	 * 下级列表
	 */
	@TableField(exist = false)
	private List<MenuEntity> children;
	/**
	 * 是否选中
	 */
	@TableField(exist = false)
	private boolean isSelect;

}
