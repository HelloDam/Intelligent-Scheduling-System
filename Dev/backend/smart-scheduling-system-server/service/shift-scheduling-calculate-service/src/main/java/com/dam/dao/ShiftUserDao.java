package com.dam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dam.model.entity.shiftScheduling.ShiftUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 班次_用户中间表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-04 14:30:17
 */
@Mapper
public interface ShiftUserDao extends BaseMapper<ShiftUserEntity> {

    List<Long> listUserIdIsBusy(@Param("shiftStartDate") Date shiftStartDate, @Param("shiftEndDate") Date shiftEndDate, @Param("storeId") Long storeId);

    List<Long> listRelevantUserId(@Param("taskId") Long taskId);

    List<Long> listUserIdByWorkDate(@Param("workDate") Date workDate, @Param("storeId") Long storeId);
    List<ShiftUserEntity> listStaffWorkDtoByWorkDate(@Param("workDate") Date workDate, @Param("storeId") Long storeId);

    List<Long> listUserIdByDateSegment(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("storeId") Long storeId);

    List<Long> listUnAssignedShiftIdByDateId(@Param("dateId") Long dateId);


    List<Long> listUserIdByShiftIdList(@Param("shiftIdList") List<Long> shiftIdList);

    int getAssignedNum(@Param("shiftIdList") List<Long> shiftIdList);

    List<Long> listUserIdByDateSegmentAndTaskId(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("storeId") Long storeId, @Param("taskId") Long taskId);
}
