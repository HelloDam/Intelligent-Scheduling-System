package com.dam.service;

import com.dam.model.vo.statistics.enterpriseManager.*;
import com.dam.model.vo.statistics.storeManager.AverageWorkTimeVo;
import com.dam.model.vo.statistics.storeManager.MonthAverageStaffWorkTimeVo;
import com.dam.model.vo.statistics.storeManager.MonthLunchNumAndDinnerNumVo;
import com.dam.model.vo.statistics.storeManager.MonthShiftNumAndAllocationRateVo;

import java.util.List;

public interface ShiftSchedulingStatisticsService {
    List<StoreAverageStaffWorkTimeVo> getStoreAverageStaffWorkTime(Integer year, Integer month, Long enterpriseId);

    /**
     * 查询指定月份的各门店的日均班次数量及日均分配率
     * @param year
     * @param month
     * @param enterpriseId
     * @return
     */
    List<StoreShiftNumAndAllocationRateVo> getStoreShiftNumAndAllocationRate(Integer year, Integer month, Long enterpriseId);

    /**
     * 获取指定月份各门店的日均客流量
     * @param year
     * @param month
     * @param enterpriseId
     * @return
     */
    List<StoreAveragePassengerFlowVo> getAveragePassengerFlow(Integer year, Integer month, Long enterpriseId);

    /**
     * 获取指定月份的午餐数量和晚餐数量
     * @param year
     * @param month
     * @param enterpriseId
     * @return
     */
    List<TotalLunchNumAndDinnerNumVo> getTotalLunchNumAndDinnerNum(Integer year, Integer month, Long enterpriseId);

    /**
     * 获取统计数据
     * @param enterpriseId
     * @return
     */
    StatisticsVo getStatisticsVoByEnterpriseId(Long enterpriseId);

    StatisticsVo getStatisticsVoByStoreId(Long storeId);

    /**
     * 获取当前门店指定年 各月份的员工日均工作时长 员工日均工作时长 = ∑(当天班次总工作时长/当天参与工作的员工数量)/该月工作日数量
     * @param year
     * @param storeId
     * @return
     */
    List<MonthAverageStaffWorkTimeVo> getMonthAverageStaffWorkTime(Integer year, Long storeId);

    /**
     * 获取指定年份 各月的午餐数量和晚餐数量
     * @param year
     * @param storeId
     * @return
     */
    List<MonthLunchNumAndDinnerNumVo> getMonthTotalLunchNumAndDinnerNum(Integer year, Long storeId);

    /**
     * 查询指定年份，每月的各门店的日均班次数量及日均分配率
     * @param year
     * @param storeId
     * @return
     */
    List<MonthShiftNumAndAllocationRateVo> getMonthShiftNumAndAllocationRate(Integer year, Long storeId);

    /**
     * 获取前n名日均工作时间最长或者最短的员工
     * @param year
     * @param month
     * @param storeId
     * @param type 0:最大 1:最小
     * @return
     */
    List<AverageWorkTimeVo> getAverageUserWorkTime(Integer year, Integer month, Long storeId, int type,int n);


}
