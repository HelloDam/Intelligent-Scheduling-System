package com.dam.model.vo.statistics.enterpriseManager;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class StoreShiftNumAndAllocationRateVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String storeName;
    /**
     * 日均班次数量
     */
    private Double averageShiftNum;
    /**
     * 日均班次分配率
     */
    private Double averageShiftAllocationRate;
}
