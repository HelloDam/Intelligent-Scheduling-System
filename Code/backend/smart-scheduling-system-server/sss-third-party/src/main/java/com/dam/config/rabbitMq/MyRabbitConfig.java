package com.dam.config.rabbitMq;

import com.dam.constant.RabbitMqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRabbitConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 使用JSON序列化机制，进行消息转换
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 邮件发送队列
     *
     * @return
     */
    @Bean
    public Queue mailSenderQueue() {
        Queue queue = new Queue(RabbitMqConstant.MAIL_SENDER_QUEUE, true, false, false);
        return queue;
    }

    /**
     * 邮件发送交换机
     * 创建交换机，由于只需要一个队列，创建direct交换机
     *
     * @return
     */
    @Bean
    public Exchange mailSenderExchange() {
        //durable：持久化
        return new DirectExchange(RabbitMqConstant.MAIL_SENDER_EXCHANGE, true, false);
    }

    /**
     * 创建邮件发送 交换机和队列的绑定关系
     *
     * @return
     */
    @Bean
    public Binding mailSenderBinding() {
        return new Binding(RabbitMqConstant.MAIL_SENDER_QUEUE,
                Binding.DestinationType.QUEUE,
                RabbitMqConstant.MAIL_SENDER_EXCHANGE,
                RabbitMqConstant.MAIL_SENDER_ROUTER_KEY,
                null);
    }

}