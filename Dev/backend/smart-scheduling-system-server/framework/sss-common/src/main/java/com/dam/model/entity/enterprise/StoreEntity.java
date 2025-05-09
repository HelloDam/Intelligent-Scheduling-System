package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dam.model.entity.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 门店表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
@Data
@TableName("store")
public class StoreEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name;
    /**
     * 企业id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long enterpriseId;
    /**
     * 省
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long provinceId;
    /**
     * 市
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cityId;
    /**
     * 区
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long regionId;
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
