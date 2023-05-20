package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.dam.model.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 门店节日表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-13 16:42:08
 */
@Data
@TableName("festival")
@NoArgsConstructor
public class FestivalEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 节日名称
	 */
	private String name;
	/**
	 * 起始日期
	 */
	private Date startDate;
	/**
	 * 截止日期
	 */
	private Date endDate;
	/**
	 * 门店id
	 */
	private Long storeId;
	/**
	 * 0：农历 1：新历
	 */
	private int type;

	public FestivalEntity(String name, Date startDate, Date endDate, Long storeId, int type) {
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.storeId = storeId;
		this.type = type;
	}


}
