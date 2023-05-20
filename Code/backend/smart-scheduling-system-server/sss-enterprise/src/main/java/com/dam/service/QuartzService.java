package com.dam.service;


import com.dam.dto.JobAndTriggerDto;
import com.github.pagehelper.PageInfo;
import org.quartz.SchedulerException;

import java.util.Map;

public interface QuartzService {

    PageInfo<JobAndTriggerDto> getJobAndTriggerDetails(Integer pageNum, Integer pageSize);

    void addJob(Map<String,Object> paramMap);

    void pauseJob(String jName, String jGroupe) throws SchedulerException;

    void resumeJob(String jName, String jGroup) throws SchedulerException;

    void rescheduleJob(String jName, String jGroup, String cron) throws SchedulerException;

    void deleteJob(String jName, String jGroup) throws SchedulerException;
}
