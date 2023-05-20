package com.dam.listener;

import com.dam.constant.RabbitMqConstant;
import com.dam.model.dto.third_party.EmailDto;
import com.dam.service.EmailService;
import com.dam.utils.vo.EnterpriseRegisterVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
//监听邮件发送队列
@RabbitListener(queues = RabbitMqConstant.MAIL_SENDER_QUEUE)
public class MailSenderListener {
    @Autowired
    private EmailService emailService;

    @RabbitHandler//注意，类上面需要RabbitListener注解
    public void handleStockLockedRelease(EmailDto emailDto, Message message, Channel channel) throws IOException {
        try {
//            System.out.println("收到消息:" + emailDto.toString());
            emailService.send(emailDto);
            //解锁成功，手动确认，消息才从MQ中删除
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //只要有异常，拒绝消息，让消息重新返回队列，让别的消费者继续解锁
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }

}
