package com.dam.service;

import com.dam.model.dto.third_party.EmailDto;

public interface EmailService {

    /**
     * 发送邮件
     *
     * @param emailDto 邮箱列表
     */
    boolean send(EmailDto emailDto);

}
