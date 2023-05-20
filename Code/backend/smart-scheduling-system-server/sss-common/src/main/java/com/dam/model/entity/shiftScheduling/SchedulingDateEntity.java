package com.dam.model.entity.shiftScheduling;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.dam.model.entity.BaseEntity;
import lombok.Data;

/**
 * 排班日期表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-04 14:30:17
 */
@Data
@TableName("scheduling_date")
public class SchedulingDateEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设置主键自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Date updateTime;

    /**
     * 日期
     */
    private Date date;
    /**
     * 是否需要工作 0：休假 1：工作
     */
    private Integer isNeedWork;
    /**
     * 上班时间（8:00）
     */
    private String startWorkTime;
    /**
     * 下班时间（21:00）
     */
    private String endWorkTime;
    /**
     * 门店id
     */
    private Long storeId;
    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 当天是否含有班次
     */
    @TableField(exist = false)
    private Integer isHaveShift = 0;

}
