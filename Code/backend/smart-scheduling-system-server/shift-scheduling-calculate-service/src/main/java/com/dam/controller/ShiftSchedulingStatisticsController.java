package com.dam.controller;

import com.dam.constant.RedisConstant;
import com.dam.model.result.R;
import com.dam.model.vo.statistics.enterpriseManager.*;
import com.dam.model.vo.statistics.storeManager.AverageWorkTimeVo;
import com.dam.model.vo.statistics.storeManager.MonthAverageStaffWorkTimeVo;
import com.dam.model.vo.statistics.storeManager.MonthLunchNumAndDinnerNumVo;
import com.dam.model.vo.statistics.storeManager.MonthShiftNumAndAllocationRateVo;
import com.dam.service.ShiftSchedulingStatisticsService;
import com.dam.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/shift-scheduling-calculate-service/statistics")
public class ShiftSchedulingStatisticsController {
    @Autowired
    private ShiftSchedulingStatisticsService shiftSchedulingStatisticsService;

    /**
     * 获取指定月份的各门店的员工日均工作时长 员工日均工作时长 = ∑(当天班次总工作时长/当天参与工作的员工数量)/该月工作日数量
     *
     * @param year
     * @param month
     * @return
     */
    @GetMapping("/getStoreAverageStaffWorkTime")
    public R getStoreAverageStaffWorkTime(@RequestParam("year") Integer year, @RequestParam("month") Integer month, HttpServletRequest httpServletRequest) {
        long start = System.currentTimeMillis();
        Long enterpriseId = Long.parseLong(JwtUtil.getEnterpriseId(httpServletRequest.getHeader("token")));
        List<StoreAverageStaffWorkTimeVo> storeAverageStaffWorkTimeVoList = shiftSchedulingStatisticsService.getStoreAverageStaffWorkTime(year, month, enterpriseId);
        List<String> storeNameList = new ArrayList<>();
        List<Double> averageStaffWorkTimeList = new ArrayList<>();
        for (StoreAverageStaffWorkTimeVo storeAverageStaffWorkTimeVo : storeAverageStaffWorkTimeVoList) {
            storeNameList.add(storeAverageStaffWorkTimeVo.getStoreName());
            averageStaffWorkTimeList.add(storeAverageStaffWorkTimeVo.getAverageStaffWorkTime());
        }
        System.out.println("getStoreAverageStaffWorkTime耗时："+(System.currentTimeMillis()-start)+"ms");
        return R.ok().addData("storeNameList", storeNameList).addData("averageStaffWorkTimeList", averageStaffWorkTimeList);
    }

    /**
     * 查询指定月份的各门店的日均班次数量及日均分配率
     *
     * @param year
     * @param month
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/getStoreShiftNumAndAllocationRate")
    public R getStoreShiftNumAndAllocationRate(@RequestParam("year") Integer year, @RequestParam("month") Integer month, HttpServletRequest httpServletRequest) {
        long start = System.currentTimeMillis();
        Long enterpriseId = Long.parseLong(JwtUtil.getEnterpriseId(httpServletRequest.getHeader("token")));
        List<StoreShiftNumAndAllocationRateVo> storeShiftNumAndAllocationRateVoList = shiftSchedulingStatisticsService.getStoreShiftNumAndAllocationRate(year, month, enterpriseId);
        List<String> storeNameList = new ArrayList<>();
        List<Double> averageShiftNumList = new ArrayList<>();
        List<Double> averageShiftAllocationRateList = new ArrayList<>();
        for (StoreShiftNumAndAllocationRateVo storeAverageStaffWorkTimeVo : storeShiftNumAndAllocationRateVoList) {
            storeNameList.add(storeAverageStaffWorkTimeVo.getStoreName());
            averageShiftNumList.add(storeAverageStaffWorkTimeVo.getAverageShiftNum());
            averageShiftAllocationRateList.add(storeAverageStaffWorkTimeVo.getAverageShiftAllocationRate());
        }
        System.out.println("getStoreShiftNumAndAllocationRate耗时："+(System.currentTimeMillis()-start)+"ms");
        return R.ok().addData("storeNameList", storeNameList)
                .addData("averageShiftNumList", averageShiftNumList)
                .addData("averageShiftAllocationRateList", averageShiftAllocationRateList);
    }

    /**
     * 获取指定月份各门店的日均客流量
     *
     * @param year
     * @param month
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/getAveragePassengerFlow")
    public R getAveragePassengerFlow(@RequestParam("year") Integer year, @RequestParam("month") Integer month, HttpServletRequest httpServletRequest) {
        long start = System.currentTimeMillis();
        Long enterpriseId = Long.parseLong(JwtUtil.getEnterpriseId(httpServletRequest.getHeader("token")));
        List<StoreAveragePassengerFlowVo> storeAveragePassengerFlowVoList = shiftSchedulingStatisticsService.getAveragePassengerFlow(year, month, enterpriseId);
        List<String> storeNameList = new ArrayList<>();
        List<Double> averagePassengerFlowList = new ArrayList<>();
        for (StoreAveragePassengerFlowVo storeAveragePassengerFlowVo : storeAveragePassengerFlowVoList) {
            storeNameList.add(storeAveragePassengerFlowVo.getStoreName());
            averagePassengerFlowList.add(storeAveragePassengerFlowVo.getAveragePassengerFlow());
        }
        System.out.println("getAveragePassengerFlow耗时："+(System.currentTimeMillis()-start)+"ms");
        return R.ok().addData("storeNameList", storeNameList)
                .addData("averagePassengerFlowList", averagePassengerFlowList);
    }

    /**
     * 获取指定月份的午餐数量和晚餐数量
     *
     * @param year
     * @param month
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/getTotalLunchNumAndDinnerNum")
    public R getTotalLunchNumAndDinnerNum(@RequestParam("year") Integer year, @RequestParam("month") Integer month, HttpServletRequest httpServletRequest) {
        long start = System.currentTimeMillis();
        Long enterpriseId = Long.parseLong(JwtUtil.getEnterpriseId(httpServletRequest.getHeader("token")));
        List<TotalLunchNumAndDinnerNumVo> totalLunchNumAndDinnerNumVoList = shiftSchedulingStatisticsService.getTotalLunchNumAndDinnerNum(year, month, enterpriseId);
        List<String> storeNameList = new ArrayList<>();
        List<Long> totalLunchNumList = new ArrayList<>();
        List<Long> totalDinnerNumList = new ArrayList<>();
        for (TotalLunchNumAndDinnerNumVo storeAveragePassengerFlowVo : totalLunchNumAndDinnerNumVoList) {
            storeNameList.add(storeAveragePassengerFlowVo.getStoreName());
            totalLunchNumList.add(storeAveragePassengerFlowVo.getLunchNum());
            totalDinnerNumList.add(storeAveragePassengerFlowVo.getDinnerNum());
        }
        System.out.println("getTotalLunchNumAndDinnerNum耗时："+(System.currentTimeMillis()-start)+"ms");
        return R.ok().addData("storeNameList", storeNameList)
                .addData("totalLunchNumList", totalLunchNumList)
                .addData("totalDinnerNumList", totalDinnerNumList);
    }

    /**
     * 获取企业统计数据
     *
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/getStatisticsVoByEnterpriseId")
    public R getStatisticsVoByEnterpriseId(HttpServletRequest httpServletRequest) {
        long start = System.currentTimeMillis();
        Long enterpriseId = Long.parseLong(JwtUtil.getEnterpriseId(httpServletRequest.getHeader("token")));
        StatisticsVo statisticsVo = shiftSchedulingStatisticsService.getStatisticsVoByEnterpriseId(enterpriseId);
        System.out.println("getStatisticsVoByEnterpriseId耗时："+(System.currentTimeMillis()-start)+"ms");
        return R.ok().addData("statisticsVo", statisticsVo);
    }

    /**
     * 获取门店统计数据
     *
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/getStatisticsVoByStoreId")
    public R getStatisticsVoByStoreId(HttpServletRequest httpServletRequest) {
        long start = System.currentTimeMillis();
        Long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        StatisticsVo statisticsVo = shiftSchedulingStatisticsService.getStatisticsVoByStoreId(storeId);
        System.out.println("getStatisticsVoByStoreId耗时："+(System.currentTimeMillis()-start)+"ms");
        return R.ok().addData("statisticsVo", statisticsVo);
    }

    /**
     * 获取当前门店指定年 各月份的员工日均工作时长 员工日均工作时长 = ∑(当天班次总工作时长/当天参与工作的员工数量)/该月工作日数量
     *
     * @param year
     * @return
     */
    @GetMapping("/getMonthAverageStaffWorkTime")
    public R getMonthAverageStaffWorkTime(@RequestParam("year") Integer year, HttpServletRequest httpServletRequest) {
        long start = System.currentTimeMillis();
        Long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        List<MonthAverageStaffWorkTimeVo> storeAverageStaffWorkTimeVoList = shiftSchedulingStatisticsService.getMonthAverageStaffWorkTime(year, storeId);
        List<String> monthNameList = new ArrayList<>();
        List<Double> averageStaffWorkTimeList = new ArrayList<>();
        for (MonthAverageStaffWorkTimeVo storeAverageStaffWorkTimeVo : storeAverageStaffWorkTimeVoList) {
            monthNameList.add(storeAverageStaffWorkTimeVo.getMonthName());
            averageStaffWorkTimeList.add(storeAverageStaffWorkTimeVo.getAverageStaffWorkTime());
        }
        System.out.println("getMonthAverageStaffWorkTime耗时："+(System.currentTimeMillis()-start)+"ms");
        return R.ok().addData("monthNameList", monthNameList).addData("averageStaffWorkTimeList", averageStaffWorkTimeList);
    }

    /**
     * 获取指定年份 各月的午餐数量和晚餐数量
     *
     * @param year
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/getMonthTotalLunchNumAndDinnerNum")
    public R getMonthTotalLunchNumAndDinnerNum(@RequestParam("year") Integer year, HttpServletRequest httpServletRequest) {
        long start = System.currentTimeMillis();
        Long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        List<MonthLunchNumAndDinnerNumVo> totalLunchNumAndDinnerNumVoList = shiftSchedulingStatisticsService.getMonthTotalLunchNumAndDinnerNum(year, storeId);
        List<String> monthNameList = new ArrayList<>();
        List<Long> totalLunchNumList = new ArrayList<>();
        List<Long> totalDinnerNumList = new ArrayList<>();
        for (MonthLunchNumAndDinnerNumVo monthLunchNumAndDinnerNumVo : totalLunchNumAndDinnerNumVoList) {
            monthNameList.add(monthLunchNumAndDinnerNumVo.getMonthName());
            totalLunchNumList.add(monthLunchNumAndDinnerNumVo.getLunchNum());
            totalDinnerNumList.add(monthLunchNumAndDinnerNumVo.getDinnerNum());
        }
        System.out.println("getMonthTotalLunchNumAndDinnerNum耗时："+(System.currentTimeMillis()-start)+"ms");
        return R.ok().addData("monthNameList", monthNameList)
                .addData("totalLunchNumList", totalLunchNumList)
                .addData("totalDinnerNumList", totalDinnerNumList);
    }

    /**
     * 查询指定年份，每月的各门店的日均班次数量及日均分配率
     *
     * @param year
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/getMonthShiftNumAndAllocationRate")
    public R getMonthShiftNumAndAllocationRate(@RequestParam("year") Integer year, HttpServletRequest httpServletRequest) {
        long start = System.currentTimeMillis();
        Long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        List<MonthShiftNumAndAllocationRateVo> storeShiftNumAndAllocationRateVoList = shiftSchedulingStatisticsService.getMonthShiftNumAndAllocationRate(year, storeId);
        List<String> monthNameList = new ArrayList<>();
        List<Double> averageShiftNumList = new ArrayList<>();
        List<Double> averageShiftAllocationRateList = new ArrayList<>();
        for (MonthShiftNumAndAllocationRateVo storeAverageStaffWorkTimeVo : storeShiftNumAndAllocationRateVoList) {
            monthNameList.add(storeAverageStaffWorkTimeVo.getMonthName());
            averageShiftNumList.add(storeAverageStaffWorkTimeVo.getAverageShiftNum());
            averageShiftAllocationRateList.add(storeAverageStaffWorkTimeVo.getAverageShiftAllocationRate());
        }
        System.out.println("getMonthShiftNumAndAllocationRate耗时："+(System.currentTimeMillis()-start)+"ms");
        return R.ok().addData("monthNameList", monthNameList)
                .addData("averageShiftNumList", averageShiftNumList)
                .addData("averageShiftAllocationRateList", averageShiftAllocationRateList);
    }

    /**
     * 获取指定月份 日均工作时间最长/最短的前 n 名员工
     *
     * @param year
     * @param month
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/getUserWorkTime")
    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_STORE_STATISTIC}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]+'-'+#root.args[1]+'-'+#root.args[2]+'-'+#root.args[3]", sync = true)
    public R getUserWorkTime(@RequestParam("year") Integer year, @RequestParam("month") Integer month, @RequestParam("type") Integer type, @RequestParam("num")Integer num, HttpServletRequest httpServletRequest) {
        long start = System.currentTimeMillis();
        Long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        List<AverageWorkTimeVo> averageWorkTimeVoList = shiftSchedulingStatisticsService.getAverageUserWorkTime(year, month, storeId, type,num);
        List<String> staffNameList = new ArrayList<>();
        List<Double> averageWorkTimeList = new ArrayList<>();
        for (AverageWorkTimeVo averageWorkTimeVo : averageWorkTimeVoList) {
            staffNameList.add(averageWorkTimeVo.getStaffName());
            averageWorkTimeList.add(averageWorkTimeVo.getAverageWorkTime());
        }
        System.out.println("getUserWorkTime耗时："+(System.currentTimeMillis()-start)+"ms");
        return R.ok().addData("staffNameList", staffNameList).addData("averageWorkTimeList", averageWorkTimeList);
    }

}
