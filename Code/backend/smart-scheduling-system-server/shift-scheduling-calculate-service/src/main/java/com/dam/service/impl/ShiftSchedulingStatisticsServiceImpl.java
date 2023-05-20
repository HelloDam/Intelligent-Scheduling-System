package com.dam.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.constant.RedisConstant;
import com.dam.exception.SSSException;
import com.dam.feign.SystemFeignService;
import com.dam.model.entity.shiftScheduling.ShiftUserEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.vo.scheduling_calculate_service.DateVo;
import com.dam.model.vo.scheduling_calculate_service.PassengerFlowVo;
import com.dam.feign.EnterpriseFeignService;
import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.model.entity.shiftScheduling.SchedulingDateEntity;
import com.dam.model.entity.shiftScheduling.SchedulingShiftEntity;
import com.dam.model.entity.shiftScheduling.SchedulingTaskEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.statistics.enterpriseManager.*;
import com.dam.model.vo.statistics.storeManager.AverageWorkTimeVo;
import com.dam.model.vo.statistics.storeManager.MonthAverageStaffWorkTimeVo;
import com.dam.model.vo.statistics.storeManager.MonthLunchNumAndDinnerNumVo;
import com.dam.model.vo.statistics.storeManager.MonthShiftNumAndAllocationRateVo;
import com.dam.service.*;
import com.dam.utils.TimeUtil;
import com.dam.utils.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShiftSchedulingStatisticsServiceImpl implements ShiftSchedulingStatisticsService {
    @Autowired
    private EnterpriseFeignService enterpriseFeignService;
    @Autowired
    private SchedulingTaskService schedulingTaskService;
    @Autowired
    private SchedulingDateService schedulingDateService;
    @Autowired
    private SchedulingShiftService shiftService;
    @Autowired
    private ShiftUserService shiftUserService;
    @Autowired
    private SystemFeignService systemFeignService;

    /**
     * 员工日均工作时长 = ∑(当天班次总工作时长/当天参与工作的员工数量)/该月工作日数量
     *
     * @param year
     * @param month
     * @param enterpriseId
     * @return
     */
    @Override
    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_ENTERPRISE_STATISTIC}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]+'-'+#root.args[1]+'-'+#root.args[2]", sync = true)
    public List<StoreAverageStaffWorkTimeVo> getStoreAverageStaffWorkTime(Integer year, Integer month, Long enterpriseId) {

        List<StoreAverageStaffWorkTimeVo> storeAverageStaffWorkTimeVoList = new ArrayList<>();
        Date[] dateArr = DateUtil.getStartAndEndDateOfMonth(year, month);

        R r = enterpriseFeignService.listAllStoreByAppointEnterpriseId(enterpriseId);
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            List<StoreEntity> storeEntityList = r.getData("list", new TypeReference<List<StoreEntity>>() {
            });
            for (StoreEntity storeEntity : storeEntityList) {
                ////查询指定月份、指定门店的所有班次
                List<SchedulingDateEntity> shiftDateEntityList = schedulingDateService.getWorkDayList(dateArr[0], dateArr[1], storeEntity.getId());
                List<Long> dateIdList = shiftDateEntityList.stream().map(SchedulingDateEntity::getId).collect(Collectors.toList());
                double totalAverageMinute = 0;
                for (Long dateId : dateIdList) {
                    //查询出当天的所有班次
                    List<SchedulingShiftEntity> shiftEntityList = shiftService.getShiftListOfDates(Arrays.asList(new Long[]{dateId}));
                    //查询这些班次对应的所有员工
                    List<Long> shiftIdList = shiftEntityList.stream().map(SchedulingShiftEntity::getId).collect(Collectors.toList());
                    List<Long> userIdList = new ArrayList<>();
                    if (shiftIdList.size() > 0) {
                        userIdList.addAll(shiftUserService.listUserIdByShiftIdList(shiftIdList));
                    }
                    //计算改日员工平均工作时长
                    Long workMinute = 0L;
                    for (SchedulingShiftEntity shift : shiftEntityList) {
                        workMinute += shift.getTotalMinute();
                    }
                    totalAverageMinute += workMinute * 1.0 / userIdList.size();
                }
                double averageWorkTime = totalAverageMinute / (dateIdList.size() * 60);
                StoreAverageStaffWorkTimeVo storeAverageStaffWorkTimeVo = new StoreAverageStaffWorkTimeVo(storeEntity.getName(), averageWorkTime);
                storeAverageStaffWorkTimeVoList.add(storeAverageStaffWorkTimeVo);
            }
        }

        return storeAverageStaffWorkTimeVoList;
    }

    /**
     * 查询指定月份的各门店的日均班次数量及日均分配率
     *
     * @param year
     * @param month
     * @param enterpriseId
     * @return
     */
    @Override
    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_ENTERPRISE_STATISTIC}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]+'-'+#root.args[1]+'-'+#root.args[2]", sync = true)
    public List<StoreShiftNumAndAllocationRateVo> getStoreShiftNumAndAllocationRate(Integer year, Integer month, Long enterpriseId) {
        List<StoreShiftNumAndAllocationRateVo> storeShiftNumAndAllocationRateVoList = new ArrayList<>();
        Date[] dateArr = DateUtil.getStartAndEndDateOfMonth(year, month);

        R r = enterpriseFeignService.listAllStoreByAppointEnterpriseId(enterpriseId);
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            List<StoreEntity> storeEntityList = r.getData("list", new TypeReference<List<StoreEntity>>() {
            });
            for (StoreEntity storeEntity : storeEntityList) {
                ////查询指定月份、指定门店的所有班次
                List<SchedulingDateEntity> shiftDateEntityList = schedulingDateService.getWorkDayList(dateArr[0], dateArr[1], storeEntity.getId());
                List<Long> dateIdList = shiftDateEntityList.stream().map(SchedulingDateEntity::getId).collect(Collectors.toList());
                ///查询出所有工作日的班次
                List<SchedulingShiftEntity> shiftEntityList = shiftService.getShiftListOfDates(dateIdList);
                ///获取已分配的班次数量
                List<Long> shiftIdList = shiftEntityList.stream().map(SchedulingShiftEntity::getId).collect(Collectors.toList());
                int assignedNum = 0;
                if (shiftIdList.size() > 0) {
                    //查询班次中已分配数量
                    assignedNum = shiftUserService.getAssignedNum(shiftIdList);
                }
                double averageShiftNum = shiftDateEntityList.size() == 0 ? 0 : (shiftIdList.size() * 1.0) / shiftDateEntityList.size();
                double averageShiftAllocationRate = shiftIdList.size() == 0 ? 0 : assignedNum * 100.0 / shiftIdList.size();
                StoreShiftNumAndAllocationRateVo storeShiftNumAndAllocationRateVo = new StoreShiftNumAndAllocationRateVo(storeEntity.getName(), averageShiftNum, averageShiftAllocationRate);
                storeShiftNumAndAllocationRateVoList.add(storeShiftNumAndAllocationRateVo);
            }
        }
        return storeShiftNumAndAllocationRateVoList;
    }

    /**
     * 获取指定月份各门店的日均客流量
     *
     * @param year
     * @param month
     * @param enterpriseId
     * @return
     */
    @Override
    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_ENTERPRISE_STATISTIC}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]+'-'+#root.args[1]+'-'+#root.args[2]", sync = true)
    public List<StoreAveragePassengerFlowVo> getAveragePassengerFlow(Integer year, Integer month, Long enterpriseId) {
        List<StoreAveragePassengerFlowVo> storeAveragePassengerFlowVoList = new ArrayList<>();
        Date[] dateArr = DateUtil.getStartAndEndDateOfMonth(year, month);

        R r = enterpriseFeignService.listAllStoreByAppointEnterpriseId(enterpriseId);
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            List<StoreEntity> storeEntityList = r.getData("list", new TypeReference<List<StoreEntity>>() {
            });
            for (StoreEntity storeEntity : storeEntityList) {
                ////查询出当前门店中时间段和当前月时间段有相交的真实任务（不需要发布）
                List<SchedulingTaskEntity> taskList = schedulingTaskService.getRealTaskListBetweenDateFrame(dateArr[0], dateArr[1], storeEntity.getId());
                ////获取工作日数量以及客流量总和
                double totalPassengerFlow = 0;
                int workDayNum = 0;
                for (SchedulingTaskEntity task : taskList) {
                    List<DateVo> dateVoList = JSON.parseObject(task.getDatevolist(), new TypeReference<List<DateVo>>() {
                    });
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");
                    if (dateVoList != null) {
                        for (DateVo dateVo : dateVoList) {
                            try {
                                Date date = sdf.parse(dateVo.getDate());
                                if (date.getTime() > dateArr[1].getTime() || date.getTime() < dateArr[0].getTime()) {
                                    //该天不在日期范围内
                                    continue;
                                }
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            workDayNum += dateVo.getIsNeedWork() == true ? 1 : 0;
                            for (PassengerFlowVo passengerFlowVo : dateVo.getPassengerFlowVoList()) {
                                totalPassengerFlow += passengerFlowVo.getPassengerFlow();
                            }
                        }
                    }
                }
                StoreAveragePassengerFlowVo storeShiftNumAndAllocationRateVo = new StoreAveragePassengerFlowVo(storeEntity.getName(), totalPassengerFlow / workDayNum);
                storeAveragePassengerFlowVoList.add(storeShiftNumAndAllocationRateVo);
            }
        }
        return storeAveragePassengerFlowVoList;
    }


    /**
     * 获取指定月份的午餐数量和晚餐数量
     *
     * @param year
     * @param month
     * @param enterpriseId
     * @return
     */
    @Override
    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_ENTERPRISE_STATISTIC}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]+'-'+#root.args[1]+'-'+#root.args[2]", sync = true)
    public List<TotalLunchNumAndDinnerNumVo> getTotalLunchNumAndDinnerNum(Integer year, Integer month, Long enterpriseId) {
        List<TotalLunchNumAndDinnerNumVo> totalLunchNumAndDinnerNumVoList = new ArrayList<>();
        Date[] dateArr = DateUtil.getStartAndEndDateOfMonth(year, month);

        R r = enterpriseFeignService.listAllStoreByAppointEnterpriseId(enterpriseId);
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            List<StoreEntity> storeEntityList = r.getData("list", new TypeReference<List<StoreEntity>>() {
            });
            for (StoreEntity storeEntity : storeEntityList) {
                ////查询指定月份、指定门店的所有班次
                List<SchedulingDateEntity> shiftDateEntityList = schedulingDateService.getWorkDayList(dateArr[0], dateArr[1], storeEntity.getId());
                List<Long> dateIdList = shiftDateEntityList.stream().map(SchedulingDateEntity::getId).collect(Collectors.toList());
                ///查询出所有工作日的班次
                List<SchedulingShiftEntity> shiftEntityList = shiftService.getShiftListOfDates(dateIdList);
                Long totalLunchNum = 0L;
                Long totalDinnerNum = 0L;
                for (SchedulingShiftEntity shift : shiftEntityList) {
                    if (shift.getMealType() == 0) {
                        totalLunchNum++;
                    } else if (shift.getMealType() == 1) {
                        totalDinnerNum++;
                    }
                }
                TotalLunchNumAndDinnerNumVo totalLunchNumAndDinnerNumVo = new TotalLunchNumAndDinnerNumVo(storeEntity.getName(), totalLunchNum, totalDinnerNum);
                totalLunchNumAndDinnerNumVoList.add(totalLunchNumAndDinnerNumVo);
            }
        }

        return totalLunchNumAndDinnerNumVoList;
    }

    /**
     * 获取统计数据
     *
     * @param enterpriseId
     * @return
     */
    @Override
    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_ENTERPRISE_STATISTIC}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]", sync = true)
    public StatisticsVo getStatisticsVoByEnterpriseId(Long enterpriseId) {
        Date date = new Date();
        DateUtil.DateEntity dateEntity = DateUtil.parseDate(date);
        Integer year = dateEntity.getYear();
        Integer month = dateEntity.getMonth();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        Date firstDateOfYear = null;
        Date endDateOfYear = null;
        Date firstDateOfMonth = null;
        Date endDateOfMonth = null;
        try {
            firstDateOfYear = sdf.parse(year + "-1-1");
            endDateOfYear = sdf.parse(year + "-12-30");
            Date[] dateArr = DateUtil.getStartAndEndDateOfMonth(year, month);
            firstDateOfMonth = dateArr[0];
            endDateOfMonth = dateArr[1];
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        StatisticsVo statisticsVo = new StatisticsVo();

        try {

            statisticsVo.setTotalTaskInYear(schedulingTaskService.getTotalTaskByEnterpriseId(enterpriseId, firstDateOfYear, endDateOfYear));
            statisticsVo.setTotalTaskInMonth(schedulingTaskService.getTotalTaskByEnterpriseId(enterpriseId, firstDateOfMonth, endDateOfMonth));
            statisticsVo.setTotalPassengerFlowInYear(schedulingTaskService.getTotalPassengerFlowByEnterpriseId(enterpriseId, firstDateOfYear, endDateOfYear));
            statisticsVo.setTotalPassengerFlowInMonth(schedulingTaskService.getTotalPassengerFlowByEnterpriseId(enterpriseId, firstDateOfMonth, endDateOfMonth));
            statisticsVo.setTotalShiftNumInYear(shiftService.getTotalShiftNumByEnterpriseId(enterpriseId, firstDateOfYear, endDateOfYear));
            statisticsVo.setTotalShiftNumInMonth(shiftService.getTotalShiftNumByEnterpriseId(enterpriseId, firstDateOfMonth, endDateOfMonth));
        } catch (SSSException e) {
            throw new RuntimeException(e);
        }

        return statisticsVo;
    }

    @Override
    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_STORE_STATISTIC}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]", sync = true)
    public StatisticsVo getStatisticsVoByStoreId(Long storeId) {
        Date date = new Date();
        DateUtil.DateEntity dateEntity = DateUtil.parseDate(date);
        Integer year = dateEntity.getYear();
        Integer month = dateEntity.getMonth();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        Date firstDateOfYear = null;
        Date endDateOfYear = null;
        Date firstDateOfMonth = null;
        Date endDateOfMonth = null;
        try {
            firstDateOfYear = sdf.parse(year + "-1-1");
            endDateOfYear = sdf.parse(year + "-12-30");
            Date[] dateArr = DateUtil.getStartAndEndDateOfMonth(year, month);
            firstDateOfMonth = dateArr[0];
            endDateOfMonth = dateArr[1];
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        StatisticsVo statisticsVo = new StatisticsVo();

            statisticsVo.setTotalTaskInYear(schedulingTaskService.getTotalTaskByStoreId(storeId, firstDateOfYear, endDateOfYear));
            statisticsVo.setTotalTaskInMonth(schedulingTaskService.getTotalTaskByStoreId(storeId, firstDateOfMonth, endDateOfMonth));
            statisticsVo.setTotalPassengerFlowInYear(schedulingTaskService.getTotalPassengerFlowByStoreId(storeId, firstDateOfYear, endDateOfYear));
            statisticsVo.setTotalPassengerFlowInMonth(schedulingTaskService.getTotalPassengerFlowByStoreId(storeId, firstDateOfMonth, endDateOfMonth));
            statisticsVo.setTotalShiftNumInYear(shiftService.getTotalShiftNumByStoreId(storeId, firstDateOfYear, endDateOfYear));
            statisticsVo.setTotalShiftNumInMonth(shiftService.getTotalShiftNumByStoreId(storeId, firstDateOfMonth, endDateOfMonth));


        return statisticsVo;
    }

    @Override
    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_STORE_STATISTIC}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]+'-'+#root.args[1]", sync = true)
    public List<MonthAverageStaffWorkTimeVo> getMonthAverageStaffWorkTime(Integer year, Long storeId) {
        List<MonthAverageStaffWorkTimeVo> monthAverageStaffWorkTimeVoList = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            Date[] dateArr = DateUtil.getStartAndEndDateOfMonth(year, month);
            ////查询指定月份、指定门店的所有班次
            List<SchedulingDateEntity> shiftDateEntityList = schedulingDateService.getWorkDayList(dateArr[0], dateArr[1], storeId);
            List<Long> dateIdList = shiftDateEntityList.stream().map(SchedulingDateEntity::getId).collect(Collectors.toList());
            double totalAverageMinute = 0;
            for (Long dateId : dateIdList) {
                //查询出当天的所有班次
                List<SchedulingShiftEntity> shiftEntityList = shiftService.getShiftListOfDates(Arrays.asList(new Long[]{dateId}));
                //查询这些班次对应的所有员工
                List<Long> shiftIdList = shiftEntityList.stream().map(SchedulingShiftEntity::getId).collect(Collectors.toList());
                List<Long> userIdList = new ArrayList<>();
                if (shiftIdList.size() > 0) {
                    userIdList.addAll(shiftUserService.listUserIdByShiftIdList(shiftIdList));
                }
                //计算改日员工平均工作时长
                Long workMinute = 0L;
                for (SchedulingShiftEntity shift : shiftEntityList) {
                    workMinute += shift.getTotalMinute();
                }
                totalAverageMinute += workMinute * 1.0 / userIdList.size();
            }
            double averageWorkTime = totalAverageMinute / (dateIdList.size() * 60);
            MonthAverageStaffWorkTimeVo monthAverageStaffWorkTimeVo = new MonthAverageStaffWorkTimeVo(DateUtil.getMonthName(month), averageWorkTime);
            monthAverageStaffWorkTimeVoList.add(monthAverageStaffWorkTimeVo);
        }
        return monthAverageStaffWorkTimeVoList;
    }

    /**
     * 获取指定年份 各月的午餐数量和晚餐数量
     *
     * @param year
     * @param storeId
     * @return
     */
    @Override
    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_STORE_STATISTIC}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]+'-'+#root.args[1]", sync = true)
    public List<MonthLunchNumAndDinnerNumVo> getMonthTotalLunchNumAndDinnerNum(Integer year, Long storeId) {
        List<MonthLunchNumAndDinnerNumVo> totalLunchNumAndDinnerNumVoList = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            Date[] dateArr = DateUtil.getStartAndEndDateOfMonth(year, month);
            ////查询指定月份、指定门店的所有班次
            List<SchedulingDateEntity> shiftDateEntityList = schedulingDateService.getWorkDayList(dateArr[0], dateArr[1], storeId);
            List<Long> dateIdList = shiftDateEntityList.stream().map(SchedulingDateEntity::getId).collect(Collectors.toList());
            ///查询出所有工作日的班次
            List<SchedulingShiftEntity> shiftEntityList = shiftService.getShiftListOfDates(dateIdList);
            Long totalLunchNum = 0L;
            Long totalDinnerNum = 0L;
            for (SchedulingShiftEntity shift : shiftEntityList) {
                if (shift.getMealType() == 0) {
                    totalLunchNum++;
                } else if (shift.getMealType() == 1) {
                    totalDinnerNum++;
                }
            }
            MonthLunchNumAndDinnerNumVo totalLunchNumAndDinnerNumVo = new MonthLunchNumAndDinnerNumVo(DateUtil.getMonthName(month), totalLunchNum, totalDinnerNum);
            totalLunchNumAndDinnerNumVoList.add(totalLunchNumAndDinnerNumVo);
        }

        return totalLunchNumAndDinnerNumVoList;
    }

    /**
     * 查询指定年份，每月的各门店的日均班次数量及日均分配率
     *
     * @param year
     * @param storeId
     * @return
     */
    @Override
    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_STORE_STATISTIC}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]+'-'+#root.args[1]", sync = true)
    public List<MonthShiftNumAndAllocationRateVo> getMonthShiftNumAndAllocationRate(Integer year, Long storeId) {
        List<MonthShiftNumAndAllocationRateVo> storeShiftNumAndAllocationRateVoList = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            Date[] dateArr = DateUtil.getStartAndEndDateOfMonth(year, month);
            ////查询指定月份、指定门店的所有班次
            List<SchedulingDateEntity> shiftDateEntityList = schedulingDateService.getWorkDayList(dateArr[0], dateArr[1], storeId);
            List<Long> dateIdList = shiftDateEntityList.stream().map(SchedulingDateEntity::getId).collect(Collectors.toList());
            ///查询出所有工作日的班次
            List<SchedulingShiftEntity> shiftEntityList = shiftService.getShiftListOfDates(dateIdList);
            ///获取已分配的班次数量
            List<Long> shiftIdList = shiftEntityList.stream().map(SchedulingShiftEntity::getId).collect(Collectors.toList());
            int assignedNum = 0;
            if (shiftIdList.size() > 0) {
                //查询班次中已分配数量
                assignedNum = shiftUserService.getAssignedNum(shiftIdList);
            }
            double averageShiftNum = shiftDateEntityList.size() == 0 ? 0 : (shiftIdList.size() * 1.0) / shiftDateEntityList.size();
            double averageShiftAllocationRate = shiftIdList.size() == 0 ? 0 : assignedNum * 100.0 / shiftIdList.size();
            MonthShiftNumAndAllocationRateVo storeShiftNumAndAllocationRateVo = new MonthShiftNumAndAllocationRateVo(DateUtil.getMonthName(month), averageShiftNum, averageShiftAllocationRate);
            storeShiftNumAndAllocationRateVoList.add(storeShiftNumAndAllocationRateVo);
        }

        return storeShiftNumAndAllocationRateVoList;
    }

    /**
     * 获取前n名日均工作时间最长或者最短的员工
     *
     * @param year
     * @param month
     * @param storeId
     * @param type    0:最大 1:最小
     * @return
     */
    @Override
    public List<AverageWorkTimeVo> getAverageUserWorkTime(Integer year, Integer month, Long storeId, int type,int n) {

        Date[] dateArr = DateUtil.getStartAndEndDateOfMonth(year, month);
        List<SchedulingDateEntity> shiftDateEntityList = schedulingDateService.getWorkDayList(dateArr[0], dateArr[1], storeId);
        List<Long> dateIdList = shiftDateEntityList.stream().map(SchedulingDateEntity::getId).collect(Collectors.toList());
        //查询出当月的所有班次
        List<SchedulingShiftEntity> shiftListInAppointMonth = shiftService.getShiftListOfDates(dateIdList);
        Map<Long, Integer> shiftIdAndWorkMinuteMap = new HashMap<>();
        List<Long> shiftIdList = new ArrayList<>();
        for (SchedulingShiftEntity shift : shiftListInAppointMonth) {
            shiftIdAndWorkMinuteMap.put(shift.getId(), shift.getTotalMinute());
            shiftIdList.add(shift.getId());
        }
        //统计每个员工的工作时间
        Map<Long, Integer> userIdAndWorkMinuteMap = new HashMap<>();
        if (shiftIdList.size() > 0) {
            List<ShiftUserEntity> shiftUserEntityList = shiftUserService.list(new QueryWrapper<ShiftUserEntity>().in("shift_id", shiftIdList));
            for (ShiftUserEntity shiftUserEntity : shiftUserEntityList) {
                Integer workMinute = shiftIdAndWorkMinuteMap.get(shiftUserEntity.getShiftId());
                if (!userIdAndWorkMinuteMap.containsKey(shiftUserEntity.getUserId())) {
                    userIdAndWorkMinuteMap.put(shiftUserEntity.getUserId(), workMinute);
                } else {
                    Integer curWorkMinute = userIdAndWorkMinuteMap.get(shiftUserEntity.getUserId());
                    userIdAndWorkMinuteMap.put(shiftUserEntity.getUserId(), curWorkMinute + workMinute);
                }
            }
        }
        //封装对象集合
        List<AverageWorkTimeVo> averageWorkTimeVoList = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : userIdAndWorkMinuteMap.entrySet()) {
            averageWorkTimeVoList.add(new AverageWorkTimeVo(entry.getKey(), "", entry.getValue() * 1.0 /(shiftDateEntityList.size()*60) ));
        }
        //集合排序
        if (type == 0) {
            //--if--求最大工作时间的前几名员工，按照工作时间降序排序
            Collections.sort(averageWorkTimeVoList, ((o1, o2) -> {
                return -Double.compare(o1.getAverageWorkTime(), o2.getAverageWorkTime());
            }));
        } else {
            Collections.sort(averageWorkTimeVoList, ((o1, o2) -> {
                return Double.compare(o1.getAverageWorkTime(), o2.getAverageWorkTime());
            }));
        }
        //截取前n名员工
        List<AverageWorkTimeVo> frontNAverageWorkTimeVoList = new ArrayList<>();
        List<Long> frontNUserIdList = new ArrayList<>();
        for (int i = 0; i < averageWorkTimeVoList.size() && i < n; i++) {
            frontNAverageWorkTimeVoList.add(averageWorkTimeVoList.get(i));
            frontNUserIdList.add(averageWorkTimeVoList.get(i).getUserId());
        }
        //查询前n名员工的真实姓名
        R userMapByIdList = systemFeignService.getUserMapByIdList(frontNUserIdList);
        if (userMapByIdList.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            Map<Long, UserEntity> idAndUserEntityMap = userMapByIdList.getData("idAndUserEntityMap", new TypeReference<Map<Long, UserEntity>>() {
            });
            for (AverageWorkTimeVo averageWorkTimeVo : frontNAverageWorkTimeVoList) {
                averageWorkTimeVo.setStaffName(idAndUserEntityMap.get(averageWorkTimeVo.getUserId()).getName());
            }
        }

        //反转一下前端显示才正确
        Collections.reverse(frontNAverageWorkTimeVoList);
        return frontNAverageWorkTimeVoList;
    }


}
