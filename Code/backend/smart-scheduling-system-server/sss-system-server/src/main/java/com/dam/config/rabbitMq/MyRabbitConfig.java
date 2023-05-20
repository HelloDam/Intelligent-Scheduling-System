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
import org.springframework.context.annotation.Lazy;

import javax.xml.ws.handler.MessageContext;

@Configuration
public class MyRabbitConfig {
    @Lazy
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
     * 创建普通队列
     *
     * @return
     */
    @Bean
    public Queue enterPriseRegistQueue() {
        Queue queue = new Queue(RabbitMqConstant.ENTERPRISE_REGISTER_QUEUE, true, false, false);
        return queue;
    }

    /**
     * 创建交换机，由于只需要一个队列，创建direct交换机
     *
     * @return
     */
    @Bean
    public Exchange orderEventExchange() {
        //durable：持久化
        return new DirectExchange(RabbitMqConstant.ENTERPRISE_REGISTER_EXCHANGE, true, false);
    }

    /**
     * 创建交换机和队列的绑定关系
     *
     * @return
     */
    @Bean
    public Binding orderCreateOrderBinding() {
        return new Binding(RabbitMqConstant.ENTERPRISE_REGISTER_QUEUE,
                Binding.DestinationType.QUEUE,
                RabbitMqConstant.ENTERPRISE_REGISTER_EXCHANGE,
                RabbitMqConstant.ENTERPRISE_REGISTER_ROUTER_KEY,
                null);
    }
}