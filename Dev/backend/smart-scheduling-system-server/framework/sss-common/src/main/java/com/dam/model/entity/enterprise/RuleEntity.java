package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dam.model.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 规则表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
@Data
@TableName("rule")
public class RuleEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 规则名称
	 */
	private String name;
	/**
	 * 值样式
	 */
	private String valueStyle;
	/**
	 * 规则说明
	 */
	private String detail;
	/**
	 *  0:启用 1:禁用
	 */
	private Integer status;

}
