package com.dam.model.vo.enterprise;

import lombok.Data;

import java.util.List;

@Data
public class PositionVo {
    private Long id;
    private String label;
    private List<PositionVo> children;

    public PositionVo(Long id, String label) {
        this.id = id;
        this.label = label;
    }
}
