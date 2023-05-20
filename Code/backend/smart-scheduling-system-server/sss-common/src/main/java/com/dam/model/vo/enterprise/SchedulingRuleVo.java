package com.dam.model.vo.enterprise;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 规则Vo
 */
@Data
public class SchedulingRuleVo {
    private Long id;
    private Long storeId;
    /**
     * 工作场所面积
     */
    private BigDecimal storeSize;
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
    private int minimumShiftNumInOneDay;
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
     * 班次时间范围 一个班次的时间在 2*60到4*60 分钟之间
     */
    private String shiftTimeFrameRule;
    /**
     * 职位选择树
     */
    private List<PositionVo> positionSelectTree;
    /**
     * 规则类型 0：主规则 1：从规则（和任务绑定）
     */
    private Integer ruleType;
}
