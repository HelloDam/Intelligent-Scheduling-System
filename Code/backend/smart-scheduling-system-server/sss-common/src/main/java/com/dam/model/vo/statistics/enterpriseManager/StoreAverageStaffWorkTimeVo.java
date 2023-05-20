package com.dam.model.vo.statistics.enterpriseManager;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class StoreAverageStaffWorkTimeVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String storeName;
    /**
     * 日均工作时长 h
     */
    private Double averageStaffWorkTime;
}
