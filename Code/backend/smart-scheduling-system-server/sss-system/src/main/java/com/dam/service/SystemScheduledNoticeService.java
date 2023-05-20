package com.dam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.model.entity.system.SystemScheduledNoticeEntity;
import com.dam.utils.PageUtils;

import java.util.Map;

/**
 * 系统定时通知
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-21 20:45:41
 */
public interface SystemScheduledNoticeService extends IService<SystemScheduledNoticeEntity> {

    PageUtils queryPage(Map<String, Object> params);

    public void addJob(SystemScheduledNoticeEntity systemScheduledNotice, Long storeId);

    void deleteJob(SystemScheduledNoticeEntity systemScheduledNotice, Long storeId);
}

