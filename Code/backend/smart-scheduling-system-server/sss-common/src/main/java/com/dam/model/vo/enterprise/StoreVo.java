package com.dam.model.vo.enterprise;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.dam.model.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StoreVo extends BaseEntity {

    /**
     * 名称
     */
    private String name;
    /**
     * 省
     */
    private Long provinceId;
    private String provinceName;
    /**
     * 市
     */
    private Long cityId;
    private String cityName;
    /**
     * 区
     */
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
