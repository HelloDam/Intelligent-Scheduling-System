package com.dam.service.impl;

import com.cronutils.builder.CronBuilder;
import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.dam.feign.EnterpriseFeignService;
import com.dam.model.entity.system.SystemScheduledNoticeEntity;
import com.dam.model.enums.quartz.QuartzEnum;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;
import com.dam.utils.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dam.dao.SystemScheduledNoticeDao;
import com.dam.service.SystemScheduledNoticeService;

import static com.cronutils.model.field.expression.FieldExpression.questionMark;
import static com.cronutils.model.field.expression.FieldExpressionFactory.always;
import static com.cronutils.model.field.expression.FieldExpressionFactory.on;


@Service("systemScheduledNoticeService")
public class SystemScheduledNoticeServiceImpl extends ServiceImpl<SystemScheduledNoticeDao, SystemScheduledNoticeEntity> implements SystemScheduledNoticeService {

    @Autowired
    private EnterpriseFeignService enterpriseFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SystemScheduledNoticeEntity> page = this.page(
                new Query<SystemScheduledNoticeEntity>().getPage(params),
                new QueryWrapper<SystemScheduledNoticeEntity>()
        );

        return new PageUtils(page);
    }

    public void addJob(SystemScheduledNoticeEntity systemScheduledNotice, Long storeId) {
        ////添加工作提醒定时任务
        ///生成cron
        Integer workNoticeUse = systemScheduledNotice.getWorkNoticeUse();
        Date workNoticeTime = systemScheduledNotice.getWorkNoticeTime();
        Integer workNoticeType = systemScheduledNotice.getWorkNoticeType();
        DateUtil.DateEntity dateEntity = DateUtil.parseDate(workNoticeTime);
        if (workNoticeUse == 1) {
            //每天的这个时、分、秒 执行任务
            Cron cron = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                    //年
                    .withYear(always())
                    ///一个月中的第几天
                    //每年的每个月的 1 到 3 号
//                .withDoM(between(SpecialChar.L, 3))
                    //questionMark()相当于"?"，withDoM和withDoW不能同时设置questionMark()
                    .withDoM(questionMark())
                    //月
                    .withMonth(always())
                    //一周的第几天
                    .withDoW(always())
                    //时
                    .withHour(on(dateEntity.getHour()))
                    //分
                    .withMinute(on(dateEntity.getMinute()))
                    //秒
                    .withSecond(on(dateEntity.getSecond()))
                    .instance();
            //获取cron表达式
            String cronAsString = cron.asString();
            System.out.println("上班通知cronAsString:"+cronAsString);
            ///翻译cron表达式
            CronDescriptor descriptor = CronDescriptor.instance(Locale.UK);
            String description = descriptor.describe(cron);
            System.out.println("上班通知cron表达式翻译："+description);
            //构建参数字典
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("workNoticeType", workNoticeType);
            //任务名称
            paramMap.put("jName", storeId.toString());
            //任务组
            paramMap.put("jGroup", QuartzEnum.J_GROUP_WORK_NOTICE.getCode().toString());
            //触发器名称
            paramMap.put("tName", QuartzEnum.T_NAME_SEND_WORK_NOTICE.getCode().toString());
            //触发器组
            paramMap.put("tGroup", QuartzEnum.T_GROUP_DAILY_NOTICE.getCode().toString());
            paramMap.put("cron", cronAsString);
            //添加到定时任务
            enterpriseFeignService.addJob(
                    paramMap
            );
        }

        ////添加休息提醒定时任务
        ///生成cron
        Integer holidayNoticeUse = systemScheduledNotice.getHolidayNoticeUse();
        Date holidayNoticeTime = systemScheduledNotice.getHolidayNoticeTime();
        Integer holidayNoticeType = systemScheduledNotice.getHolidayNoticeType();
        DateUtil.DateEntity dateEntity1 = DateUtil.parseDate(holidayNoticeTime);
        if (holidayNoticeUse == 1) {
            //每天的这个时、分、秒 执行任务
            Cron cron = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                    //年
                    .withYear(always())
                    ///一个月中的第几天
                    //每年的每个月的 1 到 3 号
//                .withDoM(between(SpecialChar.L, 3))
                    //questionMark()相当于"?"，withDoM和withDoW不能同时设置questionMark()
                    .withDoM(questionMark())
                    //月
                    .withMonth(always())
                    //一周的第几天
                    .withDoW(always())
                    //时
                    .withHour(on(dateEntity1.getHour()))
                    //分
                    .withMinute(on(dateEntity1.getMinute()))
                    //秒
                    .withSecond(on(dateEntity1.getSecond()))
                    .instance();
            //获取cron表达式
            String cronAsString = cron.asString();
            System.out.println("休息通知cronAsString:"+cronAsString);
            ///翻译cron表达式
            CronDescriptor descriptor = CronDescriptor.instance(Locale.UK);
            String description = descriptor.describe(cron);
            System.out.println("休息通知cron表达式翻译："+description);
            //构建参数字典
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("holidayNoticeType", holidayNoticeType);
            //任务名称
            paramMap.put("jName", storeId.toString());
            //任务组
            paramMap.put("jGroup", QuartzEnum.J_GROUP_REST_NOTICE.getCode().toString());
            //触发器名称
            paramMap.put("tName", QuartzEnum.T_NAME_SEND_REST_NOTICE.getCode().toString());
            //触发器组
            paramMap.put("tGroup", QuartzEnum.T_GROUP_DAILY_NOTICE.getCode().toString());
            paramMap.put("cron", cronAsString);
            //添加到定时任务
            enterpriseFeignService.addJob(
                    paramMap
            );
        }
    }

    @Override
    public void deleteJob(SystemScheduledNoticeEntity systemScheduledNotice, Long storeId) {
        //构建参数字典
        HashMap<String, Object> paramMap = new HashMap<>();
        //任务名称
        paramMap.put("jName", storeId.toString());
        //任务组
        paramMap.put("jGroup", QuartzEnum.J_GROUP_WORK_NOTICE.getCode().toString());
        enterpriseFeignService.deleteJob(paramMap);
    }

}