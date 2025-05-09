package com.dam.utils.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(description = "分配菜单")
@Data
public class AssginRoleVo {

    @ApiModelProperty(value = "用户id")
    
    @JsonSerialize(using = ToStringSerializer.class)
private Long userId;

    @ApiModelProperty(value = "角色id列表")
    private List<Long> roleIdList;

}
