//package com.dam.config.mybatis;
//
//import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
//import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
//import org.apache.ibatis.reflection.MetaObject;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Date;
//
///**
// * create_time和update_time配置
// */
//@Configuration
//public class MybatisPlusConfig implements MetaObjectHandler {
//
//    /**
//     * 配置分页插件，获取total那些才不会出错
//     * @return
//     */
//    @Bean
//    public PaginationInterceptor paginationInterceptor() {
//        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
//        //当请求的页面大于最大页之后，如果设置为true，则调回首页再请求；如果设置为false，则继续请求
//        paginationInterceptor.setOverflow(true);
//        //设置单页的最大限制数量
//        paginationInterceptor.setLimit(1000);
//        return paginationInterceptor;
//    }
//
//    @Override
//    public void insertFill(MetaObject metaObject) {
//        setFieldValByName("createTime", new Date(), metaObject);
//    }
//
//    @Override
//    public void updateFill(MetaObject metaObject) {
//        setFieldValByName("updateTime", new Date(), metaObject);
//    }
//}