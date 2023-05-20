package com.dam.model.vo.scheduling_calculate_service;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class DateVo implements Serializable {
    /**
     * 日期
     */
    private String date;
    /**
     * 客流量
     */
    private List<PassengerFlowVo> passengerFlowVoList;
    /**
     * 是否需要工作
     */
    private Boolean isNeedWork;
    /**
     * 当天的开始工作时间
     */
    private String startWorkTime;
    /**
     * 当天的结束工作时间
     */
    private String endWorkTime;
    /**
     * 记录每段segment的收尾时间 2022-01-27 09:00:00,2022-01-27 09:30:00
     */
    private String[] timeFrameArr;
}
