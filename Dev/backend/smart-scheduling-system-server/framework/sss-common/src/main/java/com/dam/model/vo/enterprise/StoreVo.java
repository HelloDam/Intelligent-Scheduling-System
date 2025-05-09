package com.dam.model.vo.enterprise;

import com.dam.model.entity.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StoreVo extends BaseEntity {

    /**
     * 名称
     */
    private String name;
    /**
     * 省
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long provinceId;
    private String provinceName;
    /**
     * 市
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cityId;
    private String cityName;
    /**
     * 区
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long regionId;
    private String regionName;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 工作场所面积
     */
    private BigDecimal size;
    /**
     * 0：营业中 1：休息中（默认0）
     */
    private Integer status;

}
