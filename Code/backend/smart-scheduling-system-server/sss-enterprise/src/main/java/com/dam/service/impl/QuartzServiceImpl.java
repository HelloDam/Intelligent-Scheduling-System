package com.dam.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dam.custom.quartz.DamJob;
import com.dam.dao.JobDetailMapper;
import com.dam.dto.JobAndTriggerDto;
import com.dam.model.entity.enterprise.UserPositionEntity;
import com.dam.service.QuartzService;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
//@Service("quartzServiceImpl")//定义实现类的Bean名称
@Service
public class QuartzServiceImpl implements QuartzService {

    @Autowired
    private JobDetailMapper jobDetailMapper;

    @Autowired
    private Scheduler scheduler;

    /**
     * 分页查询定时任务
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<JobAndTriggerDto> getJobAndTriggerDetails(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<JobAndTriggerDto> list = jobDetailMapper.getJobAndTriggerDetails();
        PageInfo<JobAndTriggerDto> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }


    /**
     * 新增定时任务
     */
    @Override
    public void addJob(Map<String,Object> paramMap) {
        try {
            System.out.println("添加定时任务参数："+paramMap);

            //任务名称
            String jName = paramMap.get("jName").toString();
            //任务组
            String jGroup = paramMap.get("jGroup").toString();
            //触发器名称
            String tName = paramMap.get("tName").toString();
            //触发器组
            String tGroup = paramMap.get("tGroup").toString();
            String cron = paramMap.get("cron").toString();
            //删除原本的定时任务
            this.deleteJob(jName,jGroup);
            // 构建JobDetail
            JobDetail jobDetail = JobBuilder.newJob(DamJob.class)
                    .withIdentity(jName, jGroup)
                    .build();
            //给Job传参
//            jobDetail.getJobDataMap().put("jName", jName);
//            jobDetail.getJobDataMap().put("jGroup", jGroup);
//            jobDetail.getJobDataMap().put("tName", tName);
//            jobDetail.getJobDataMap().put("tGroup", tGroup);
            //paramMap参数保存
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                jobDetail.getJobDataMap().put(entry.getKey(),entry.getValue().toString());
            }
            // 按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(tName, tGroup)
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();
            // 启动调度器
            scheduler.start();
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("添加定时任务成功");
        } catch (Exception e) {
            log.info("创建定时任务失败" + e);
        }
    }

    @Override
    public void pauseJob(String jName, String jGroup) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(jName, jGroup));
    }

    @Override
    public void resumeJob(String jName, String jGroup) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(jName, jGroup));
    }

    @Override
    public void rescheduleJob(String jName, String jGroup, String cron) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jName, jGroup);
        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        // 按新的cronExpression表达式重新构建trigger
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        // 按新的trigger重新设置job执行，重启触发器
        scheduler.rescheduleJob(triggerKey, trigger);
    }

    @Override
    public void deleteJob(String jName, String jGroup) throws SchedulerException {
        scheduler.pauseTrigger(TriggerKey.triggerKey(jName, jGroup));
        scheduler.unscheduleJob(TriggerKey.triggerKey(jName, jGroup));
        scheduler.deleteJob(JobKey.jobKey(jName, jGroup));
    }
}

