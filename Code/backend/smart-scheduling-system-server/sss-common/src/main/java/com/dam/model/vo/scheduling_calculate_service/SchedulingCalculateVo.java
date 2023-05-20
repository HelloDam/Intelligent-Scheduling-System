package com.dam.model.vo.scheduling_calculate_service;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SchedulingCalculateVo implements Serializable {
    private Long taskId;
    private Long schedulingRuleId;
    /**
     * 工作日
     */
    private List<DateVo> dateVoList;
    /**
     * 以多少分钟为一段
     */
    private int duration = 30;
    /**
     * 以多少个段为基准去排班
     */
    private int intervalC = 2;
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
}
