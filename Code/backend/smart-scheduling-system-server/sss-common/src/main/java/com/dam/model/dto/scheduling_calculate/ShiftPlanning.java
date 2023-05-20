package com.dam.model.dto.scheduling_calculate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShiftPlanning {
    /**
     * 员工在员工数组中的索引
     **/
    int employeeIndex;
    /**
     * 班次
     **/
    Shift shift;
}
