package com.dam.model.vo.statistics.systemManager;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemUseStatisticsVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long enterpriseNum;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long storeNum;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userNum;
    /**
     * 任务数量
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long calculatedTaskNum;
}
