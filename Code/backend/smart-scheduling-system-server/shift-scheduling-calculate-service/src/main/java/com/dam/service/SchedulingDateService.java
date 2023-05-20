package com.dam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.model.entity.shiftScheduling.SchedulingDateEntity;
import com.dam.utils.PageUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 排班日期表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-04 14:30:17
 */
public interface SchedulingDateService extends IService<SchedulingDateEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SchedulingDateEntity> listDateBetweenStartDateAndEndDate(Date startDate, Date endDate, long storeId);

    Boolean judgeOneDateIsRest(Long storeId, Date workDate);

    List<SchedulingDateEntity> getWorkDayList(Date startDate, Date endDate, Long storeId);
}

