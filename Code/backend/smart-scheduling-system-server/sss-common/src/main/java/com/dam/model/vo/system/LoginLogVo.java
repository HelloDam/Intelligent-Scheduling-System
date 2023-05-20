package com.dam.model.vo.system;

import com.dam.model.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class LoginLogVo extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private String username;
    private String ipaddr;
    private Integer status;
    private String msg;
    private String browser;
    private String os;
    private Date access_time;
    /**
     * 企业id
     */
    private Long enterpriseId;
    /**
     * 企业名称
     */
    private String enterpriseName;
    /**
     * 门店id
     */
    private Long storeId;
    /**
     * 门店名称
     */
    private String storeName;
}
