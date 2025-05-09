package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dam.model.entity.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 省-市-区表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
@Data
@TableName("province_city_region")
public class ProvinceCityRegionEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name;
    /**
     * 类型 0：省 1：市 2：区
     */
    private Integer type;
    /**
     * 没有父元素设置为-1
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

}
