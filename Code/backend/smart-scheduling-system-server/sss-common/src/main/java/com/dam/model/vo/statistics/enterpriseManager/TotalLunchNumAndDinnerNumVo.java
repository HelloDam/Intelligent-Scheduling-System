package com.dam.model.vo.statistics.enterpriseManager;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TotalLunchNumAndDinnerNumVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String storeName;
    private Long lunchNum;
    private Long dinnerNum;
}
