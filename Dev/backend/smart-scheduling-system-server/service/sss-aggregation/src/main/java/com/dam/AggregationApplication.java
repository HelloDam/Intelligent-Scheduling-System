package com.dam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author dam
 * @create 2024/3/4 18:57
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(value = {
        "com.dam.dao"
})
public class AggregationApplication {
    public static void main(String[] args) {
        SpringApplication.run(AggregationApplication.class, args);
    }
}