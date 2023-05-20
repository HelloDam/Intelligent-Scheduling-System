package com.dam.model.vo.enterprise;

import com.dam.model.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class EnterpriseVo extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 名称
     */
    private String name;
    /**
     * 企业详情
     */
    private String detail;
    /**
     * 企业logo
     */
    private String logo;
    /**
     * 门店数量
     */
    private Integer storeNum;
    /**
     * 用户人数
     */
    private Integer userNum;
}
