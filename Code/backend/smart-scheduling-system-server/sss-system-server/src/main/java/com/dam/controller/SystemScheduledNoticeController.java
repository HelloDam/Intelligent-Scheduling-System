package com.dam.controller;

import java.util.Arrays;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.cronutils.builder.CronBuilder;
//import com.cronutils.model.Cron;
//import com.cronutils.model.CronType;
//import com.cronutils.model.definition.CronDefinitionBuilder;
//import com.cronutils.model.field.value.SpecialChar;

import com.dam.feign.EnterpriseFeignService;
import com.dam.model.entity.system.SystemScheduledNoticeEntity;
import com.dam.model.result.R;
import com.dam.utils.JwtUtil;
import com.dam.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dam.service.SystemScheduledNoticeService;

import javax.servlet.http.HttpServletRequest;


/**
 * 系统定时通知
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-21 20:45:41
 */
@RestController
@RequestMapping("system/systemScheduledNotice")
public class SystemScheduledNoticeController {
    @Autowired
    private SystemScheduledNoticeService systemScheduledNoticeService;
    @Autowired
    private EnterpriseFeignService enterpriseFeignService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = systemScheduledNoticeService.queryPage(params);

        return R.ok().addData("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        SystemScheduledNoticeEntity systemScheduledNotice = systemScheduledNoticeService.getById(id);

        return R.ok().addData("systemScheduledNotice", systemScheduledNotice);
    }

    /**
     * 信息
     */
    @RequestMapping("/infoByToken")
    public R infoByToken(HttpServletRequest httpServletRequest) {
        Long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        SystemScheduledNoticeEntity systemScheduledNotice = systemScheduledNoticeService.getOne(new QueryWrapper<SystemScheduledNoticeEntity>().eq("store_id", storeId));

        return R.ok().addData("systemScheduledNotice", systemScheduledNotice);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SystemScheduledNoticeEntity systemScheduledNotice, HttpServletRequest httpServletRequest) {
        Long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        systemScheduledNotice.setStoreId(storeId);
        systemScheduledNoticeService.save(systemScheduledNotice);
        //添加定时任务
        systemScheduledNoticeService.addJob(systemScheduledNotice,storeId);

        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SystemScheduledNoticeEntity systemScheduledNotice, HttpServletRequest httpServletRequest) {
        Long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        systemScheduledNoticeService.updateById(systemScheduledNotice);
        //删除之前的定时任务
        systemScheduledNoticeService.deleteJob(systemScheduledNotice,storeId);
        //添加定时任务
        systemScheduledNoticeService.addJob(systemScheduledNotice,storeId);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteBatch")
    public R deleteBatch(@RequestBody Long[] ids) {
        systemScheduledNoticeService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
