package com.dam.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.dam.model.entity.BaseEntity;
import lombok.Data;

/**
 * 角色表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-06 15:50:30
 */
@Data
@TableName("role")
public class RoleEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;


	/**
	 * 角色名
	 */
	private String name;
	/**
	 * 角色描述
	 */
	private String description;
	/**
	 * 角色类型（0：系统内角色 1：企业内角色 2：门店内角色）
	 * 系统管理员 管理 系统内角色，将其赋给 企业管理员
	 * 企业管理员 管理 企业内角色，将其赋给 门店管理员
	 * 系统管理员 管理 门店内角色，将其赋给 普通用户
	 */
	private Integer type;
	/**
	 * 企业id
	 */
	private Long enterpriseId;
	/**
	 * 门店id
	 */
	private Long storeId;
}
