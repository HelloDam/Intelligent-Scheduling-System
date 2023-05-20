package com.dam.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.dam.model.entity.BaseEntity;
import lombok.Data;

/**
 * user_role中间表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-06 15:50:30
 */
@Data
@TableName("user_role")
public class UserRoleEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 角色id
	 */
	private Long roleId;
	/**
	 * 用户id
	 */
	private Long userId;

}
