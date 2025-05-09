package com.dam.model.vo.enterprise;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 存储地址项 如广东省 茂名市 电白区
 */
@Data
@AllArgsConstructor
public class AreaItemVo {
    /**
     * 存储id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long value;
    private String label;
    private List<AreaItemVo> children;

    public AreaItemVo(Long value, String label) {
        this.value = value;
        this.label = label;
    }
}
