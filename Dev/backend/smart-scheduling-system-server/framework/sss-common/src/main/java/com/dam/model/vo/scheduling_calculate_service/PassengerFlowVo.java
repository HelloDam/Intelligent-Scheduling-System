package com.dam.model.vo.scheduling_calculate_service;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 客流量
 */
@Data
@ToString
public class PassengerFlowVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String date;
    private String startTime;
    private String endTime;
    /**
     * 客流量
     */
    private double passengerFlow;
}
