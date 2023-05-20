package com.dam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("com.dam.dao")
@SpringBootApplication
//开始事务
@EnableTransactionManagement
//开启基于注解的缓存
@EnableCaching
public class ShiftSchedulingCalculateServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShiftSchedulingCalculateServerApplication.class, args);
    }
}
