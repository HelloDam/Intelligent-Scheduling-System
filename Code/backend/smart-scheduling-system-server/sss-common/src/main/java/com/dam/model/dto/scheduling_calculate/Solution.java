package com.dam.model.dto.scheduling_calculate;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Solution {
    /**
     * 所有班次的总时间（分钟）
     **/
    int totalMinute;
    /**
     * 已分配班次的总时间（分钟）
     **/
    int totalAssignedMinute;
    /**
     * 分配比率
     **/
    double allocationRatio;
    /**
     * 每一天的班次
     **/
    List<List<Shift>> shiftListList;
    /**
     * 每一天未分配的班次
     **/
    List<List<Shift>> unabsorbedShiftListList;
    /**
     * 每一天的班次安排计划
     **/
    List<List<ShiftPlanning>> shiftPlanningListList = new ArrayList<>();
    /**
     * 每个员工的班次安排计划的班次安排计划
     **/
    EmployeePlan[] employeePlans;
    /**
     * 一阶段算法用时(ms)
     */
    long phaseOneUseTime;
    /**
     * 二阶段算法用时(ms)
     */
    long phaseTwoUseTime;
}
