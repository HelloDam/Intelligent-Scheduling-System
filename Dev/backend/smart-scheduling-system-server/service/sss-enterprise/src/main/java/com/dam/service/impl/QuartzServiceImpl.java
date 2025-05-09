package com.dam.service.impl;

import com.dam.custom.quartz.DamJob;
import com.dam.dao.JobDetailMapper;
import com.dam.dto.JobAndTriggerDto;
import com.dam.service.QuartzService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class QuartzServiceImpl implements QuartzService {

    /**
     * 该类负责与数据库交互，管理作业详情。
     */
    @Autowired
    private JobDetailMapper jobDetailMapper;

    /**
     * Quartz Scheduler实例，负责调度、执行和管理作业与触发器。
     */
    @Autowired
    private Scheduler scheduler;

    /**
     * 分页查询定时任务
     *
     * @param pageNum  当前页码
     * @param pageSize 每页记录数
     * @return 包含分页信息的JobAndTriggerDto列表
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
    public void addJob(Map<String, Object> paramMap) {
        try {
            System.out.println("添加定时任务参数：" + paramMap);

            // 任务名称
            String jName = paramMap.get("jName").toString();
            // 任务组
            String jGroup = paramMap.get("jGroup").toString();
            // 触发器名称
            String tName = paramMap.get("tName").toString();
            // 触发器组
            String tGroup = paramMap.get("tGroup").toString();
            // cron表达式
            String cron = paramMap.get("cron").toString();

            // 删除同组同名的定时任务
            this.deleteJob(jName, jGroup);
            // 构建JobDetail对象，指定任务类为DamJob，并设置其身份（名称、组）
            JobDetail jobDetail = JobBuilder.newJob(DamJob.class)
                    .withIdentity(jName, jGroup)
                    .build();
            // 将传入参数存入JobDataMap中，以便在任务执行时使用
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                jobDetail.getJobDataMap().put(entry.getKey(), entry.getValue().toString());
            }
            // 按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(tName, tGroup)
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();
            // 启动调度器
            scheduler.start();
            // 将JobDetail与Trigger关联，并提交至调度器
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("添加定时任务成功");
        } catch (Exception e) {
            log.info("创建定时任务失败" + e);
        }
    }

    /**
     * 根据任务名、任务组 暂停任务
     *
     * @param jName
     * @param jGroup
     * @throws SchedulerException
     */
    @Override
    public void pauseJob(String jName, String jGroup) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(jName, jGroup));
    }

    /**
     * 根据任务名、任务组 恢复指定任务
     *
     * @param jName 任务名称
     * @param jGroup 任务组名
     * @throws SchedulerException 调度器操作异常
     */
    @Override
    public void resumeJob(String jName, String jGroup) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(jName, jGroup));
    }

    /**
     * 重新调度指定任务，更改其cron表达式
     *
     * @param jName 任务名称
     * @param jGroup 任务组名
     * @param cron 新的cron表达式
     * @throws SchedulerException 调度器操作异常
     */
    @Override
    public void rescheduleJob(String jName, String jGroup, String cron) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jName, jGroup);
        // 使用新的cron表达式，创建新的CronScheduleBuilder
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        // 获取原有的CronTrigger
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        // 按新的cronExpression表达式重新构建trigger
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        // 使用新的CronTrigger重新设置任务执行计划，并重启触发器
        scheduler.rescheduleJob(triggerKey, trigger);
    }

    /**
     * 删除指定任务及其关联触发器
     *
     * @param jName 任务名称
     * @param jGroup 任务组名
     * @throws SchedulerException 调度器操作异常
     */
    @Override
    public void deleteJob(String jName, String jGroup) throws SchedulerException {
        // 先暂停触发器
        scheduler.pauseTrigger(TriggerKey.triggerKey(jName, jGroup));
        // 移除触发器的调度计划
        scheduler.unscheduleJob(TriggerKey.triggerKey(jName, jGroup));
        // 删除任务本身
        scheduler.deleteJob(JobKey.jobKey(jName, jGroup));
    }
}

