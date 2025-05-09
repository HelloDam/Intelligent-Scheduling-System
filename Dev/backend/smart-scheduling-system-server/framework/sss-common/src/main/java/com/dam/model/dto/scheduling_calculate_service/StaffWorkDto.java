package com.dam.model.dto.scheduling_calculate_service;

import com.dam.model.entity.shiftScheduling.SchedulingShiftEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class StaffWorkDto {
    /**
     * 员工id
     */
    
    @JsonSerialize(using = ToStringSerializer.class)
private Long userId;
    /**
     * 员工所负责的班次
     */
    private List<SchedulingShiftEntity> shiftEntityList;
}
