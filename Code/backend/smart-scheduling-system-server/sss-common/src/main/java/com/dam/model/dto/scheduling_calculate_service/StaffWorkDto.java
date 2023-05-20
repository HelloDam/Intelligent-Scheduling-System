package com.dam.model.dto.scheduling_calculate_service;

import com.dam.model.entity.shiftScheduling.SchedulingShiftEntity;
import lombok.Data;

import java.util.List;

@Data
public class StaffWorkDto {
    /**
     * 员工id
     */
    private Long userId;
    /**
     * 员工所负责的班次
     */
    private List<SchedulingShiftEntity> shiftEntityList;
}
