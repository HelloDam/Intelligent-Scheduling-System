package com.dam.model.vo.statistics.storeManager;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MonthLunchNumAndDinnerNumVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String monthName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lunchNum;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dinnerNum;
}
