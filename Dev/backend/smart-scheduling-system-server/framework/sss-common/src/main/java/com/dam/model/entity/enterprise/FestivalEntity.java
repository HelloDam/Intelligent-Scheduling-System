package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dam.model.entity.BaseEntity;
import com.dam.valid.annotations.TypeAnno;
import com.dam.valid.groups.AddGroup;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 门店节日表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-13 16:42:08
 */
@Data
@TableName("festival")
@NoArgsConstructor
@AllArgsConstructor
public class FestivalEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 节日名称
     */
    @NotBlank(message = "节日名称不能为空")
    private String name;

    /**
     * 起始日期
     */
    @NotNull(message = "起始日期不能为空", groups = {AddGroup.class})
    private Date startDate;

    /**
     * 截止日期
     */
    @NotNull(message = "截止日期不能为空", groups = {AddGroup.class})
    private Date endDate;

    /**
     * 门店id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long storeId;

    /**
     * 0：农历 1：新历
     */
    @TypeAnno(values = {0, 1}, groups = {AddGroup.class}, message = "节日日期类型只能是农历（0）、新历（1）")
    private int type;
}
