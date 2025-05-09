package com.dam.model.vo.statistics.enterpriseManager;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class StatisticsVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 当年累计任务数
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long totalTaskInYear;
    /**
     * 当月累计任务数
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
    @JsonSerialize(using = ToStringSerializer.class)
    private Long totalShiftNumInYear;
    /**
     * 当月累计班次数
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long totalShiftNumInMonth;
}
