package com.dam.model.vo.statistics.storeManager;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MonthLunchNumAndDinnerNumVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String monthName;
    private Long lunchNum;
    private Long dinnerNum;
}
