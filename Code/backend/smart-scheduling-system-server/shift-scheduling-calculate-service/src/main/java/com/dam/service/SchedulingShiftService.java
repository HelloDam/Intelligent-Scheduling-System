package com.dam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.exception.SSSException;
import com.dam.model.entity.shiftScheduling.SchedulingShiftEntity;
import com.dam.model.vo.shiftScheduling.GanttShiftVo;
import com.dam.model.vo.shiftScheduling.GanttStatisticsVo;
import com.dam.model.vo.shiftScheduling.WeekViewVo;
import com.dam.utils.PageUtils;


import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 排班班次表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-04 14:30:17
 */
public interface SchedulingShiftService extends IService<SchedulingShiftEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<Long> listDateIdByPositionIdList(List<Long> positionIdList,List<Long> curMonthDateList);

    List<GanttShiftVo> listSchedulingShiftVoByDateId(Long dateId, List<Long> positionIdList,List<Long> userIdList,Boolean isSearchUnAssignedShifts);

    List<SchedulingShiftEntity> listShiftBetweenStartDateAndEndDate(Date startDate, Date endDate, long storeId);

    WeekViewVo listWeekViewShiftVoBetweenStartDateAndEndDate(Date startDate, Date endDate,
                                                             Integer realStartIndex, Integer realEndIndex ,
                                                             long storeId, Map<Long, List<SchedulingShiftEntity>> userIdAndShiftList);

    List<Long> listDateIdByUserIdList(List<Long> userIdList, List<Long> curMonthDateList);

    List<SchedulingShiftEntity> listShiftIdOfShift(Date shiftStartDate, Date shiftEndDate, Long storeId);

    List<Long> listDateIdWithUnAssignedShifts(List<Long> curMonthDateIdList);

    List<SchedulingShiftEntity> selectUnAssignedShiftsByDateId(Long dateId);

    GanttStatisticsVo getGanttStatisticsVo(List<GanttShiftVo> schedulingShiftVoList);

    Long getTotalShiftNumByEnterpriseId(Long enterpriseId, Date firstDateOfYear, Date endDateOfYear) throws SSSException;

    Long getTotalShiftNumByStoreId(Long storeId, Date firstDateOfYear, Date endDateOfYear);

    List<SchedulingShiftEntity> getShiftListOfDates(List<Long> dateIdList);

    /**
     * 查询某天某人的所有班次
     * @param date
     * @param userId
     * @return
     */
    List<SchedulingShiftEntity> getOneDayShiftListOfUser(Date date, Long userId);

    /**
     * 获取起止日期内，每天的班次集合
     * @param startDate
     * @param endDate
     * @param userId
     * @return
     */
    List<List<SchedulingShiftEntity>> getWeekShiftListOfUser(Date startDate, Date endDate, Long userId);
}

