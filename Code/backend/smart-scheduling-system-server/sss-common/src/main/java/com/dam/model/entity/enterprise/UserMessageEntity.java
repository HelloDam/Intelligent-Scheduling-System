package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.dam.model.entity.BaseEntity;
import lombok.Data;

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
	private Long userId;
	/**
	 * 消息id
	 */
	private Long messageId;

}
