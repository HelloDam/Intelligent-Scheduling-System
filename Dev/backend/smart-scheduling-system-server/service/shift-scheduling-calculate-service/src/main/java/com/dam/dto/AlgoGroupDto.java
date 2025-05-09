package com.dam.dto;

import com.wskh.enums.AlgoEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlgoGroupDto {
    private AlgoEnum.PhaseOne phaseOne;
    private AlgoEnum.PhaseTwo phaseTwo;
}
