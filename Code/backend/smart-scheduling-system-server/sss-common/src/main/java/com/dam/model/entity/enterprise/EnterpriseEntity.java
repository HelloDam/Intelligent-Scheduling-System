package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.dam.model.entity.BaseEntity;
import lombok.Data;

/**
 * 企业表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-13 14:49:17
 */
@Data
@TableName("enterprise")
public class EnterpriseEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 企业详情
	 */
	private String detail;
	/**
	 * 企业logo
	 */
	private String logo;

}
