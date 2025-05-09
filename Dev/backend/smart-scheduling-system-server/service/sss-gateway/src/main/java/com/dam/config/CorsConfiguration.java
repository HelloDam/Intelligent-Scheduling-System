package com.dam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration//说明该类为配置类
public class CorsConfiguration {

    @Bean//加入到容器
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
        ///配置跨域
        //允许所有请求头进行跨域
        corsConfiguration.addAllowedHeader("*");
        //允许所有请求方法进行跨域(get/post)
        corsConfiguration.addAllowedMethod("*");
        //允许所有请求来源进行跨域
        corsConfiguration.addAllowedOrigin("*");
        //是否允许携带cookie进来
        corsConfiguration.setAllowCredentials(true);

        //对所有路径进行跨域
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }

}
