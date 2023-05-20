package com.dam.service.impl;

import com.dam.config.mail.MailConfig;
import com.dam.model.dto.third_party.EmailDto;
import com.dam.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    @Autowired
    private MailConfig mailConfig;
    @Autowired
    private JavaMailSenderImpl mailSender;

    @Override
    public boolean send(EmailDto emailDto) {
        String fromEmail = mailConfig.getUsername();


        try {
            if (emailDto.getType() == 0) {
                //定制纯文本邮件信息SimpleMailMessage
                SimpleMailMessage message = new SimpleMailMessage();
                //设置发件箱
                message.setFrom(fromEmail);
                //设置收件箱
                message.setTo(emailDto.getTo());
                //设置邮件主题
                message.setSubject(emailDto.getSubject());
                //设置邮件内容
                message.setText(emailDto.getContent());
                //调用Java封装好的发送方法
                mailSender.send(message);
                return true;
            } else if (emailDto.getType() == 1) {
                //--if--发送html格式的邮件
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(fromEmail);
                helper.setTo(emailDto.getTo());
                helper.setSubject(emailDto.getSubject());
                helper.setText(emailDto.getContent(), true);
                mailSender.send(message);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("邮件发送失败，原因：" + e.getMessage());
            return false;
        }

    }
}