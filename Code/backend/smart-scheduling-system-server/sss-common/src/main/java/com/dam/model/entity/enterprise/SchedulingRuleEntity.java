package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import com.dam.model.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 门店规则中间表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-23 21:34:22
 */
@Data
@TableName("scheduling_rule")
@AllArgsConstructor
@NoArgsConstructor
public class SchedulingRuleEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Long storeId;
    /**
     * 门店工作时间段
     */
    private String storeWorkTimeFrame;
    /**
     * 员工一天最多工作几小时
     */
    private BigDecimal mostWorkHourInOneDay;
    /**
     * 员工一周最多工作几小时
     */
    private BigDecimal mostWorkHourInOneWeek;
    /**
     * 一个班次的最少时间（分钟为单位）
     */
    private int minShiftMinute;
    /**
     * 一个班次的最大时间（分钟为单位）
     */
    private int maxShiftMinute;
    /**
     * 休息时间长度（分钟为单位）
     */
    private int restMinute;
    /**
     * 员工最长连续工作时间
     */
    private BigDecimal maximumContinuousWorkTime;
    /**
     * 开店规则
     */
    private String openStoreRule;
    /**
     * 关店规则
     */
    private String closeStoreRule;
    /**
     * 正常班规则
     */
    private String normalRule;
    /**
     * 无客流量值班规则
     */
    private String noPassengerRule;
    /**
     * 每天最少班次
     */
    private Integer minimumShiftNumInOneDay;
    /**
     * 正常班次规则
     */
    private String normalShiftRule;
    /**
     * 午餐时间
     */
    private String lunchTimeRule;
    /**
     * 晚餐时间
     */
    private String dinnerTimeRule;
    /**
     * 规则类型 0：主规则 1：从规则（和任务绑定）
     */
    private Integer ruleType;

    public SchedulingRuleEntity(Long storeId, String storeWorkTimeFrame, BigDecimal mostWorkHourInOneDay, BigDecimal mostWorkHourInOneWeek, int minShiftMinute, int maxShiftMinute, int restMinute, BigDecimal maximumContinuousWorkTime, String openStoreRule, String closeStoreRule, String normalRule, String noPassengerRule, Integer minimumShiftNumInOneDay, String normalShiftRule, String lunchTimeRule, String dinnerTimeRule, int type) {
        this.storeId = storeId;
        this.storeWorkTimeFrame = storeWorkTimeFrame;
        this.mostWorkHourInOneDay = mostWorkHourInOneDay;
        this.mostWorkHourInOneWeek = mostWorkHourInOneWeek;
        this.minShiftMinute = minShiftMinute;
        this.maxShiftMinute = maxShiftMinute;
        this.restMinute = restMinute;
        this.maximumContinuousWorkTime = maximumContinuousWorkTime;
        this.openStoreRule = openStoreRule;
        this.closeStoreRule = closeStoreRule;
        this.normalRule = normalRule;
        this.noPassengerRule = noPassengerRule;
        this.minimumShiftNumInOneDay = minimumShiftNumInOneDay;
        this.normalShiftRule = normalShiftRule;
        this.lunchTimeRule = lunchTimeRule;
        this.dinnerTimeRule = dinnerTimeRule;
        this.ruleType = type;
    }

    public SchedulingRuleEntity clone() {
        return new SchedulingRuleEntity(storeId, storeWorkTimeFrame, mostWorkHourInOneDay, mostWorkHourInOneWeek, minShiftMinute, maxShiftMinute, restMinute, maximumContinuousWorkTime, openStoreRule, closeStoreRule, normalRule, noPassengerRule, minimumShiftNumInOneDay, normalShiftRule, lunchTimeRule, dinnerTimeRule, ruleType);
    }
}
