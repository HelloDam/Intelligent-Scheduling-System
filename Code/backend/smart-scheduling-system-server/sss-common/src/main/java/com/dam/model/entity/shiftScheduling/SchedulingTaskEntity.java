package com.dam.model.entity.shiftScheduling;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.dam.model.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排班任务表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-01 13:58:33
 */
@Data
@TableName("scheduling_task")
@NoArgsConstructor
public class SchedulingTaskEntity implements Serializable {
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
     * 任务名
     */
    private String name;
    /**
     * 任务总时长（分钟）
     */
    private Integer totalMinute;
    /**
     * 已分配班次的总时间（分钟）
     **/
    private Integer totalAssignedMinute;
    /**
     * 分配比率
     **/
    private BigDecimal allocationRatio;
    /**
     * 任务状态 0：新创建 1：计算中 2：计算完成 3：计算失败
     */
    private Integer status;
    /**
     * 排班规则id
     */
    private Long schedulingRuleId;
    /**
     * 门店id
     */
    private Long storeId;
    /**
     * 以多少分钟为一段
     */
    private Integer duration;
    /**
     * 以多少段为一个时间单位来进行排班
     */
    private Integer intervalc;
    /**
     * 排班工作日及其客流量
     */
    private String datevolist;
    /**
     * 计算结果
     */
    private BigDecimal calculateTime;
    /**
     * 排班起始日期
     */
    private String startDate;
    /**
     * 排班结束日期
     */
    private String endDate;
    /**
     * 第一阶段算法
     */
    private String stepOneAlg;
    /**
     * 第二阶段算法
     */
    private String stepTwoAlg;
    /**
     * 第二阶段算法参数
     */
    private String stepTwoAlgParam;
    /**
     * 任务类型 0：真实任务 1：虚拟任务
     */
    private Integer type;
    /**
     * 父任务id 虚拟任务才有父id
     */
    private Long parentId;
    /**
     * 是否发布任务 0：未发布 1：已经发布
     */
    private Integer isPublish;

    public SchedulingTaskEntity(String name, Integer totalMinute, Integer totalAssignedMinute, BigDecimal allocationRatio,
                                Integer status, Long schedulingRuleId, Long storeId, Integer duration, Integer intervalc,
                                String datevolist, BigDecimal calculateTime, String startDate, String endDate,
                                String stepOneAlg, String stepTwoAlg, String stepTwoAlgParam, Integer type,
                                Long parentId,Integer isPublish) {
        this.id = id;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.name = name;
        this.totalMinute = totalMinute;
        this.totalAssignedMinute = totalAssignedMinute;
        this.allocationRatio = allocationRatio;
        this.status = status;
        this.schedulingRuleId = schedulingRuleId;
        this.storeId = storeId;
        this.duration = duration;
        this.intervalc = intervalc;
        this.datevolist = datevolist;
        this.calculateTime = calculateTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.stepOneAlg = stepOneAlg;
        this.stepTwoAlg = stepTwoAlg;
        this.stepTwoAlgParam = stepTwoAlgParam;
        this.type = type;
        this.parentId = parentId;
        this.isPublish=isPublish;
    }

    public SchedulingTaskEntity clone() {
        return new SchedulingTaskEntity(name,
                totalMinute,
                totalAssignedMinute,
                allocationRatio,
                status,
                schedulingRuleId,
                storeId,
                duration,
                intervalc,
                datevolist,
                calculateTime,
                startDate,
                endDate,
                stepOneAlg,
                stepTwoAlg,
                stepTwoAlgParam,
                type,
                parentId,
                isPublish);
    }

}
