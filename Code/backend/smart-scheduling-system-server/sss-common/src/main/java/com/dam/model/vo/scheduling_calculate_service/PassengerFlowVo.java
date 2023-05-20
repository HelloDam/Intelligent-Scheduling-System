package com.dam.model.vo.scheduling_calculate_service;

import com.dam.model.dto.scheduling_calculate.Time;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 客流量
 */
@Data
@ToString
public class PassengerFlowVo implements Serializable {
    private String date;
    private String startTime;
    private String endTime;
    /**
     * 客流量
     */
    private double passengerFlow;
}
