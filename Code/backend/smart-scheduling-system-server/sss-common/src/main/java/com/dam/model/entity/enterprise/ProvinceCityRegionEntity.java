package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.dam.model.entity.BaseEntity;
import lombok.Data;

/**
 * 省-市-区表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
@Data
@TableName("province_city_region")
public class ProvinceCityRegionEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	private String name;
	/**
	 * 类型 0：省 1：市 2：区
	 */
	private Integer type;
	/**
	 * 没有父元素设置为-1
	 */
	private Long parentId;

}
