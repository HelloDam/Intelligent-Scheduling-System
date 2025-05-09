package com.dam.model.dto.third_party;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public
class EmailDto {

    /**
     * 接收邮箱
     */
    private String to;

    /**
     * 主题
     */
    private String subject;

    /**
     * 内容
     */
    private String content;

    /**
     * 邮件类型 0：简单邮件 1：html格式的邮件
     */
    private Integer type = 0;

}
