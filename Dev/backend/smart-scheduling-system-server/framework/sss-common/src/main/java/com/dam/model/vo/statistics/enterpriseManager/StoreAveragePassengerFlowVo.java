package com.dam.model.vo.statistics.enterpriseManager;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class StoreAveragePassengerFlowVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String storeName;
    /**
     * 日均客流量
     */
    private Double averagePassengerFlow;

}
