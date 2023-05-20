package com.dam.controller;

import com.dam.model.dto.third_party.EmailDto;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import com.dam.service.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping("/thirdParty/mail")
@Api(tags = "邮件服务")
public class MailController {
    @Autowired
    private EmailService emailService;

    @ApiOperation(value = "发送邮件")
    @PostMapping("/send")
    public R send(@RequestBody EmailDto emailDto) {
        boolean isSuccess = emailService.send(emailDto);
        if (isSuccess) {
            return R.ok();
        } else {
            return R.error(ResultCodeEnum.FAIL);
        }
    }
}
