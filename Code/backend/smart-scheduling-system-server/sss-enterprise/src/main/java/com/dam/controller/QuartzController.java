package com.dam.controller;

import com.dam.annotation.OperationLog;
import com.dam.dto.JobAndTriggerDto;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.enums.log.BusinessTypeEnum;
import com.dam.service.QuartzService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import com.dam.model.result.R;

@Slf4j
@Controller
@RequestMapping(path = "/enterprise/quartz")
public class QuartzController {
    private static final String title = "定时任务管理";
    @Autowired
    private QuartzService quartzService;

    /**
     * 新增定时任务
     *
     * @return ResultMap
     */
    @PostMapping(path = "/addJob")
    @ResponseBody
    @OperationLog(title = QuartzController.title, businessType = BusinessTypeEnum.INSERT, detail = "添加定时任务")
    @PreAuthorize("hasAuthority('bnt.quartz.addJob')")
    public R addJob(@RequestBody Map<String, Object> paramMap) {
        try {
            quartzService.addJob(paramMap);
            return R.ok().addData("message", "添加任务成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(ResultCodeEnum.FAIL.getCode(), "添加任务失败");
        }
    }

    /**
     * 暂停任务
     *
     * @param jName  任务名称
     * @param jGroup 任务组
     * @return ResultMap
     */
    @PostMapping(path = "/pauseJob")
    @ResponseBody
    @PreAuthorize("hasAuthority('bnt.quartz.pauseJob')")
    public R pauseJob(String jName, String jGroup) {
        try {
            quartzService.pauseJob(jName, jGroup);
            return R.ok().addData("message", "添加任务成功");
        } catch (SchedulerException e) {
            e.printStackTrace();
            return R.error(ResultCodeEnum.FAIL.getCode(), "暂停任务失败");
        }
    }

    /**
     * 恢复任务
     *
     * @param jName  任务名称
     * @param jGroup 任务组
     * @return ResultMap
     */
    @PostMapping(path = "/restoreJob")
    @ResponseBody
    @PreAuthorize("hasAuthority('bnt.quartz.restoreJob')")
    public R restoreJob(String jName, String jGroup) {
        try {
            quartzService.resumeJob(jName, jGroup);
            return R.ok().addData("message", "恢复任务成功");
        } catch (SchedulerException e) {
            e.printStackTrace();
            return R.error(ResultCodeEnum.FAIL.getCode(), "恢复任务失败");
        }
    }

    /**
     * 重启任务
     *
     * @param jName  任务名称
     * @param jGroup 任务组
     * @param cron   cron表达式
     * @return ResultMap
     */
    @PostMapping(path = "/rescheduleJob")
    @ResponseBody
    @PreAuthorize("hasAuthority('bnt.quartz.rescheduleJob')")
    public R rescheduleJob(String jName, String jGroup, String cron) {
        try {
            quartzService.rescheduleJob(jName, jGroup, cron);
            return R.ok().addData("message", "重启任务成功");
        } catch (SchedulerException e) {
            e.printStackTrace();
            return R.error(ResultCodeEnum.FAIL.getCode(), "重启任务失败");
        }
    }

    /**
     * 删除任务
     *
     * @return ResultMap
     */
    @PostMapping(path = "/deleteJob")
    @ResponseBody
    @OperationLog(title = QuartzController.title, businessType = BusinessTypeEnum.DELETE, detail = "删除定时任务")
    @PreAuthorize("hasAuthority('bnt.quartz.deleteJob')")
    public R deleteJob(@RequestBody Map<String, Object> paramMap) {
        //任务名称
        String jName = paramMap.get("jName").toString();
        //任务组
        String jGroup = paramMap.get("jGroup").toString();
        try {
            quartzService.deleteJob(jName, jGroup);
            return R.ok().addData("message", "删除任务成功");
        } catch (SchedulerException e) {
            e.printStackTrace();
            return R.error(ResultCodeEnum.FAIL.getCode(), "删除任务失败");
        }
    }

    /**
     * 查询任务
     *
     * @param pageNum  页码
     * @param pageSize 每页显示多少条数据
     * @return Map
     */
    @GetMapping(path = "/queryJob")
    @ResponseBody
    @PreAuthorize("hasAuthority('bnt.quartz.queryJob')")
    public R queryJob(Integer pageNum, Integer pageSize) {
        PageInfo<JobAndTriggerDto> pageInfo = quartzService.getJobAndTriggerDetails(pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        if (!StringUtils.isEmpty(pageInfo.getTotal())) {
            map.put("JobAndTrigger", pageInfo);
            map.put("number", pageInfo.getTotal());
            return R.ok().addData("map", map).addData("message", "查询任务成功");
        }
        return R.error(ResultCodeEnum.FAIL.getCode(), "查询任务成功失败，没有数据");
    }
}
