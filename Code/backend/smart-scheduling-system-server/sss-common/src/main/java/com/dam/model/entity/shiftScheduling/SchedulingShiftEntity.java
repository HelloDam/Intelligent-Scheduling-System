package com.dam.model.entity.shiftScheduling;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.dam.model.entity.BaseEntity;
import lombok.Data;
import lombok.ToString;

/**
 * 排班班次表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-04 14:30:17
 */
@Data
@TableName("scheduling_shift")
@ToString
public class SchedulingShiftEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 设置主键自增
	 */
	@TableId(value = "id",type = IdType.AUTO)
	private Long id;

	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private Date createTime;

	@TableField(value = "update_time", fill = FieldFill.UPDATE)
	private Date updateTime;


	/**
	 * 班次开始时间 2023-02-27 07:00:00
	 */
	private Date startDate;
	/**
	 * 班次结束时间 2023-02-27 10:30:00
	 */
	private Date endDate;
	/**
	 * 对应排班工作日的id
	 */
	private Long schedulingDateId;
	/**
	 * 吃饭开始时间
	 */
	private Date mealStartDate;
	/**
	 * 吃饭结束时间
	 */
	private Date mealEndDate;
	/**
	 * 0：午餐 1：晚餐 2：不安排用餐
	 */
	private Integer mealType;
	/**
	 * 班次时间（分钟）
	 */
	private Integer totalMinute;
	/**
	 * 班次类型 0：正常班 1：开店 2：收尾
	 */
	private Integer shiftType;

}
