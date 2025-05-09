package com.dam.model.vo.enterprise;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class PositionVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String label;
    private List<PositionVo> children;

    public PositionVo(Long id, String label) {
        this.id = id;
        this.label = label;
    }
}
