package com.dam.config.quartz;

import org.jetbrains.annotations.NotNull;
import org.quartz.Scheduler;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

/**
 * 此配置类（QuartzConfig）负责设置并集成Quartz Scheduler到Spring框架中
 * 实现了{@link SchedulerFactoryBeanCustomizer}接口
 * 用于自定义Quartz {@link SchedulerFactoryBean}的默认行为
 *
 */
@Configuration
public class QuartzConfig implements SchedulerFactoryBeanCustomizer {

    /**
     * 此方法创建一个由Spring管理的Quartz属性Bean。
     *
     * @return 加载并初始化后的Quartz属性对象
     * @throws IOException 若在读取配置文件过程中发生IO异常
     */
    @Bean
    public Properties properties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        // 设置quartz配置文件所在路径
        propertiesFactoryBean.setLocation(new ClassPathResource("/myQuartz.properties"));
        // 在配置文件中的属性被读取并注入后 再 初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }


    /**
     * 创建并返回一个Spring管理的{@link SchedulerFactoryBean}实例。
     *
     * @return 配置好的{@link SchedulerFactoryBean}实例
     * @throws IOException 若在加载配置文件过程中发生IO异常
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        // 通过传入由上述 properties() 方法返回的Quartz属性对象，确保该工厂bean使用指定的配置信息初始化
        // 后续通过该工厂bean创建的Quartz调度器将遵循这些自定义属性设定
        schedulerFactoryBean.setQuartzProperties(properties());
        return schedulerFactoryBean;
    }

    /**
     * 通过SchedulerFactoryBean获取Scheduler的实例
     * 简化了从工厂Bean获取实际调度器实例的过程，方便在应用中直接注入和使用。
     *
     * @return 初始化并准备就绪的Quartz {@link Scheduler}实例
     * @throws IOException 若在加载配置文件过程中发生IO异常
     */
    @Bean
    public Scheduler scheduler() throws IOException {
        return schedulerFactoryBean().getScheduler();
    }

    /**
     * 重写{@link SchedulerFactoryBeanCustomizer#customize(SchedulerFactoryBean)}方法，
     * 以实现对{@link SchedulerFactoryBean}的自定义配置。
     * <p>
     * 在此方法中，我们设置如下Quartz调度器参数：
     *
     * @param schedulerFactoryBean 需要进行定制化的{@link SchedulerFactoryBean}实例
     */
    @Override
    public void customize(@NotNull SchedulerFactoryBean schedulerFactoryBean) {
        // 设置启动延迟为2秒，即在Spring容器启动后2秒开始初始化调度器
        schedulerFactoryBean.setStartupDelay(2);
        // 设置自动启动调度器，即在Spring容器启动时自动启动调度器
        schedulerFactoryBean.setAutoStartup(true);
        // 设置允许覆盖已存在的作业，当存在同名作业时，使用新配置覆盖旧配置
        schedulerFactoryBean.setOverwriteExistingJobs(true);
    }
}
