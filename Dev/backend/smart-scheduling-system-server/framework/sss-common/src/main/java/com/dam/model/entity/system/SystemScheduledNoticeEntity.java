package com.dam.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dam.model.entity.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统定时通知
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-21 20:45:41
 */
@Data
@TableName("system_scheduled_notice")
public class SystemScheduledNoticeEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 门店id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long storeId;

    /**
     * 门店是否启用上班通知提醒
     */
    private Integer workNoticeUse;

    /**
     * 上班通知提醒时间，如每天晚上八点提醒相关人员第二天是否需要上班
     */
    private Date workNoticeTime;

    /**
     * 工作通知方式 0：系统发送消息 1：发送邮件 2：系统发送消息及发送邮件
     */
    private Integer workNoticeType;

    /**
     * 门店是否启用休假通知提醒
     */
    private Integer holidayNoticeUse;

    /**
     * 休假通知提醒时间，如每天晚上八点提醒相关人员第二天是否需要上班
     */
    private Date holidayNoticeTime;

    /**
     * 休假通知方式 0：系统发送消息 1：发送邮件 2：系统发送消息及发送邮件
     */
    private Integer holidayNoticeType;
}
