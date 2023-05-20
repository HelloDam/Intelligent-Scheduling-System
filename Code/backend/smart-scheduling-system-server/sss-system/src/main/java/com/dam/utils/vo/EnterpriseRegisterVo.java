package com.dam.utils.vo;

import lombok.Data;

@Data
public class EnterpriseRegisterVo {
    private String uuid;
    private String name;
    private String detail;
    private String mail;
    private String code;
    private String logo;
    /**
     * 拒绝申请原因
     */
    private String rejectReason;
}
