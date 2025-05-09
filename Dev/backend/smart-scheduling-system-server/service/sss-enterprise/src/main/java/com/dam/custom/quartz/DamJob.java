package com.dam.custom.quartz;

import com.dam.ApplicationContextHolder;
import com.dam.model.enums.quartz.QuartzEnum;
import com.dam.service.QuartzNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

/**
 * 自定义工作类
 */
@Slf4j
public class DamJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("执行定时任务");
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        //任务名称
        String jName = jobDataMap.get("jName").toString();
        //任务组
        String jGroup = jobDataMap.get("jGroup").toString();
        //触发器名称
        String tName = jobDataMap.get("tName").toString();
        //触发器组
        String tGroup = jobDataMap.get("tGroup").toString();
        System.out.println("jName:" + jName);
        System.out.println("jGroup:" + jGroup);

        if (jGroup.equals(QuartzEnum.J_GROUP_WORK_NOTICE.getCode().toString())) {
            System.out.println("发送工作通知");
            //--if--工作通知
            Long storeId = Long.parseLong(jName);
            int workNoticeType = Integer.parseInt(jobDataMap.get("workNoticeType").toString());
            QuartzNoticeService quartzNoticeService = (QuartzNoticeService) ApplicationContextHolder.getBean("quartzNoticeServiceImpl");
            quartzNoticeService.sendWorkNotice(storeId, workNoticeType);
        }

        if (jGroup.equals(QuartzEnum.J_GROUP_REST_NOTICE.getCode().toString())) {
            System.out.println("发送休息通知");
            //--if--休息通知
            Long storeId = Long.parseLong(jName);
            int holidayNoticeType = Integer.parseInt(jobDataMap.get("holidayNoticeType").toString());
            QuartzNoticeService quartzNoticeService = (QuartzNoticeService) ApplicationContextHolder.getBean("quartzNoticeServiceImpl");
            quartzNoticeService.sendRestNotice(storeId, holidayNoticeType);
        }
    }
}
