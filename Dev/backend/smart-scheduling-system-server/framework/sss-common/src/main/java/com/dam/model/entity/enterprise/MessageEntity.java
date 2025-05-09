package com.dam.model.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dam.model.entity.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 通知表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-20 15:43:46
 */
@Data
@TableName("message")
public class MessageEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 通知类型(0-企业公开,1-门店公开，2-指定用户可以看)
     */
    private Integer type;
    /**
     * 通知主题
     */
    private String subject;
    /**
     * 通知内容
     */
    private String content;
    /**
     * 门店id
     */

    @JsonSerialize(using = ToStringSerializer.class)
    private Long storeId;
    /**
     * 企业id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long enterpriseId;
    /**
     * 是否发布（0：未发布；1：已发布）
     */
    private Integer isPublish;
    /**
     * 发布时间
     */
    private Date publishTime;
    /**
     * 创建人id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;
    /**
     * 创建人id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long publishUserId;

}
