package com.dam.model.vo.statistics.systemManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemUseStatisticsVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer enterpriseNum;
    private Integer storeNum;
    private Integer userNum;
    /**
     * 任务数量
     */
    private Integer calculatedTaskNum;
}
