package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.dam.model.entity.BaseEntity;
import lombok.Data;

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
	private Long userId;
	/**
	 * 职位id
	 */
	private Long positionId;

}
