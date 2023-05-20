//
//
package com.dam.utils.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户查询实体
 * </p>
 */
@Data
public class SysUserQueryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 查询关键词
     */
    private String keyword;
    /**
     * 用户类型
     */
    private Integer userType;
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 创建开始时间
     */
    private String createTimeBegin;
    /**
     * 创建结束时间
     */
    private String createTimeEnd;
    /**
     * 查询的角色id
     */
    private Long roleId;
    /**
     * 选中的职位
     */
    private List<Long> positionIdArr;
    ////查询用户是否在一个时间段内是否繁忙
    /**
     * 是否需要查询用户有没有繁忙（如果在同一时间有其他班次的工作，则是繁忙状态）
     */
    private Boolean isNeedSearchBusyStatus;
    /**
     * 班次的开始时间
     */
    private Date shiftStartDate;
    /**
     * 班次的结束时间
     */
    private Date shiftEndDate;
    /**
     * 0：空闲 1：繁忙
     */
    private Integer busyStatus;
    ////查询日期段内有工作班次安排的用户
    private String startDate;
    private String endDate;
    private Long taskId;
    /**
     * 需要查询的门店id
     */
    private Long searchStoreId;
    /**
     * 需要查询的用户类型
     */
    private Integer searchUserType;

}

