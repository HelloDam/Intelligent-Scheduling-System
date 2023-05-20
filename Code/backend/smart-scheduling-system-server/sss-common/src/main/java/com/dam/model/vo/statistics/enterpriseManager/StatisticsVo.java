package com.dam.model.vo.statistics.enterpriseManager;

import lombok.Data;

import java.io.Serializable;

@Data
public class StatisticsVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 当年累计任务数
     */
    private Long totalTaskInYear;
    /**
     * 当月累计任务数
     */
    private Long totalTaskInMonth;
    /**
     * 当年累计客流量
     */
    private Double totalPassengerFlowInYear;
    /**
     * 当月累计客流量
     */
    private Double totalPassengerFlowInMonth;
    /**
     * 当年累计班次数
     */
    private Long totalShiftNumInYear;
    /**
     * 当月累计班次数
     */
    private Long totalShiftNumInMonth;
}
