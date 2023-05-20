package com.dam.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dam.model.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("login_log")
public class LoginLogEntity extends BaseEntity {
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
     * 门店名称
     */
    private Long storeId;
}
