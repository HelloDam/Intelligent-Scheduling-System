package com.dam.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dam.model.entity.shiftScheduling.SchedulingShiftEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 排班班次表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-04 14:30:17
 */
@Mapper
public interface SchedulingShiftDao extends BaseMapper<SchedulingShiftEntity> {

    List<SchedulingShiftEntity> listShiftBetweenStartDateAndEndDate(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("storeId") long storeId);

    List<Long> listShiftIdBetweenStartDateAndEndDate(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("storeId") long storeId);

    List<SchedulingShiftEntity> listShiftIdOfShift(@Param("shiftStartDate") Date shiftStartDate, @Param("shiftEndDate") Date shiftEndDate, @Param("storeId") Long storeId);

    List<Long> listDateIdWithUnAssignedShifts(@Param("curMonthDateIdList") List<Long> curMonthDateIdList);

    List<SchedulingShiftEntity> selectUnAssignedShiftsByDateId(@Param("dateId") Long dateId);

    List<SchedulingShiftEntity> getOneDayShiftListOfUser(@Param("date") Date date, @Param("userId") Long userId);
}
