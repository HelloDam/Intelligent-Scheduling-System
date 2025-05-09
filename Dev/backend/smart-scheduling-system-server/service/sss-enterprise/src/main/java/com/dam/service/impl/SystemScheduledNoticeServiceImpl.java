package com.dam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cronutils.builder.CronBuilder;
import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.dam.dao.SystemScheduledNoticeDao;
import com.dam.feign.EnterpriseFeignService;
import com.dam.model.entity.system.SystemScheduledNoticeEntity;
import com.dam.model.enums.quartz.QuartzEnum;
import com.dam.service.SystemScheduledNoticeService;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;
import com.dam.utils.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.cronutils.model.field.expression.FieldExpression.questionMark;
import static com.cronutils.model.field.expression.FieldExpressionFactory.always;
import static com.cronutils.model.field.expression.FieldExpressionFactory.on;


@Service("systemScheduledNoticeService")
public class SystemScheduledNoticeServiceImpl extends ServiceImpl<SystemScheduledNoticeDao, SystemScheduledNoticeEntity> implements SystemScheduledNoticeService {

    @Autowired
    private EnterpriseFeignService enterpriseFeignService;

    /**
     * 分页查询
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SystemScheduledNoticeEntity> page = this.page(
                new Query<SystemScheduledNoticeEntity>().getPage(params),
                new QueryWrapper<SystemScheduledNoticeEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * 添加定时任务
     *
     * @param systemScheduledNotice
     * @param storeId
     */
    public void addJob(SystemScheduledNoticeEntity systemScheduledNotice, Long storeId) {
        //// 添加工作提醒定时任务
        if (systemScheduledNotice.getWorkNoticeUse() == 1) {
            // 上班通知时间
            Date workNoticeTime = systemScheduledNotice.getWorkNoticeTime();
            // 工作通知方式 0：系统发送消息 1：发送邮件 2：系统发送消息及发送邮件
            Integer workNoticeType = systemScheduledNotice.getWorkNoticeType();
            // 解析日期时间，获取小时、分钟、秒信息
            DateUtil.DateEntity dateEntity = DateUtil.parseDate(workNoticeTime);

            String cronAsString = getCronAsString(dateEntity);
            /// 构建参数字典
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("workNoticeType", workNoticeType);
            // 任务名称
            paramMap.put("jName", storeId.toString());
            // 任务组
            paramMap.put("jGroup", QuartzEnum.J_GROUP_WORK_NOTICE.getCode().toString());
            // 触发器名称
            paramMap.put("tName", QuartzEnum.T_NAME_SEND_WORK_NOTICE.getCode().toString());
            // 触发器组
            paramMap.put("tGroup", QuartzEnum.T_GROUP_DAILY_NOTICE.getCode().toString());
            paramMap.put("cron", cronAsString);
            /// 添加到定时任务
            enterpriseFeignService.addJob(
                    paramMap
            );
        }

        //// 添加休息提醒定时任务
        /// 生成cron
        Integer holidayNoticeUse = systemScheduledNotice.getHolidayNoticeUse();
        Date holidayNoticeTime = systemScheduledNotice.getHolidayNoticeTime();
        Integer holidayNoticeType = systemScheduledNotice.getHolidayNoticeType();
        DateUtil.DateEntity dateEntity1 = DateUtil.parseDate(holidayNoticeTime);
        if (holidayNoticeUse == 1) {

            // 获取cron表达式
            String cronAsString = getCronAsString(dateEntity1);

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

    /**
     * 根据日期实体，获取cron表达式
     * @param dateEntity
     * @return
     */
    private static String getCronAsString(DateUtil.DateEntity dateEntity) {
        /// 生成cron
        // 每天的这个时、分、秒 执行任务
        Cron cron = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                // 年，设置为always()，表示每一年
                .withYear(always())
                // 月
                .withMonth(always())
                // 一个月的第几天
                // questionMark()相当于在 Cron 表达式中使用字符 '?'，注意：withDoM和withDoW不能同时设置questionMark()
                .withDoM(questionMark())
                // 一周的第几天
                .withDoW(always())
                // 时
                .withHour(on(dateEntity.getHour()))
                // 分
                .withMinute(on(dateEntity.getMinute()))
                // 秒
                .withSecond(on(dateEntity.getSecond()))
                .instance();
        // 获取cron表达式
        String cronAsString = cron.asString();
        // System.out.println("cronAsString:" + cronAsString);
        // 翻译cron表达式，Locale.UK设置用英文描述（这段代码可以删除）
        CronDescriptor descriptor = CronDescriptor.instance(Locale.UK);
        String description = descriptor.describe(cron);
        System.out.println("上班通知cron表达式翻译：" + description);
        return cronAsString;
    }

    @Override
    public void deleteJob(SystemScheduledNoticeEntity systemScheduledNotice, Long storeId) {
        // 构建参数字典
        HashMap<String, Object> paramMap = new HashMap<>();
        // 任务名称
        paramMap.put("jName", storeId.toString());
        // 任务组
        paramMap.put("jGroup", QuartzEnum.J_GROUP_WORK_NOTICE.getCode().toString());
        enterpriseFeignService.deleteJob(paramMap);
    }

}