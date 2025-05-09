package com.dam.model.vo.statistics.storeManager;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AverageWorkTimeVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 员工名
     */
    private String staffName;
    /**
     * 日均工作时长（h）
     */
    private Double averageWorkTime;

}
