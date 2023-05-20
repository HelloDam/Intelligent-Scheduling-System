package com.dam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.exception.SSSException;
import com.dam.model.entity.shiftScheduling.ShiftUserEntity;
import com.dam.utils.PageUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 班次_用户中间表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-04 14:30:17
 */
public interface ShiftUserService extends IService<ShiftUserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询当前门店在该时间段所有繁忙的用户
     * @param shiftStartDate
     * @param shiftEndDate
     * @param storeId
     * @return
     */
    List<Long> listUserIdIsBusy(Date shiftStartDate, Date shiftEndDate, Long storeId);

    void replaceOrAddMembersForShift(Date shiftStartDate, Date shiftEndDate, Integer appointType, List<Long> userIdList, Long storeId, Long shiftId) throws SSSException;

    List<Long> listRelevantUserId(Long taskId);

    List<Long> listUserIdByWorkDate(Date workDate, Long storeId);

    List<Long> listUserIdByDateSegment(Date startDate, Date endDate, Long storeId);

    List<Long> listUserIdByDateSegmentAndTaskId(Date startDate, Date endDate, Long storeId, Long taskId);

    List<Long> listUnAssignedShiftIdByDateId(Long dateId);

    List<ShiftUserEntity> listStaffWorkDtoByWorkDate(Date workDate, Long storeId);

    /**
     * 查询出班次对应的员工id集合
     * @param shiftIdList
     * @return
     */
    List<Long> listUserIdByShiftIdList(List<Long> shiftIdList);

    int getAssignedNum(List<Long> shiftIdList);


}

