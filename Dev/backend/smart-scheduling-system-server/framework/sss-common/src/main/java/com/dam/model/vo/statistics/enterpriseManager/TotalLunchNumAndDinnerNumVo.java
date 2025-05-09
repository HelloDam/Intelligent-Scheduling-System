package com.dam.model.vo.statistics.enterpriseManager;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TotalLunchNumAndDinnerNumVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String storeName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lunchNum;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dinnerNum;
}
