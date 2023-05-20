package com.wskh.algo;

import com.dam.exception.SSSException;
import com.dam.model.dto.scheduling_calculate.Instance;
import com.dam.model.dto.scheduling_calculate.Shift;
import com.dam.model.dto.scheduling_calculate.ShiftPlanning;
import com.dam.model.dto.scheduling_calculate.Solution;
import com.wskh.algo.personnel_scheduling.others.PS_EASA;
import com.wskh.algo.personnel_scheduling.others.PS_SAEA;
import com.wskh.algo.shift_generation.heu.SG_GOA;
import com.wskh.enums.AlgoEnum;
import com.wskh.utils.LogUtil;

import java.io.IOException;
import java.util.*;

public class IntelligentSchedulingPureAlgo {
    public Solution solve(Instance instance, AlgoEnum.PhaseOne phaseOne, AlgoEnum.PhaseTwo phaseTwo, Map<String, Object> parameter) throws IOException, SSSException {
        LogUtil.success("------------------------------------ 开始调用智能排班算法 ------------------------------------");
        // 初始化 solution
        Solution solution = new Solution();
        // 班次生成阶段
        LogUtil.canBePrint = false;
        long phaseOneStartTime = System.currentTimeMillis();
        int totalMinute = 0;
        List<List<Shift>> shiftListList = new ArrayList<>();
        int[] shiftNumArr = new int[instance.getTimeFramesEachDay().size()];
        int[] shiftMinuteArr = new int[instance.getTimeFramesEachDay().size()];
        for (int i = 0; i < instance.getTimeFramesEachDay().size(); i++) {
            List<Shift> shiftList = new ArrayList<>();
            if (instance.getTimeFramesEachDay().get(i) != null) {
                if (phaseOne.equals(AlgoEnum.PhaseOne.GOA)) {
                    shiftList = new SG_GOA(instance.getTimeFramesEachDay().get(i), // 时间段集合，例如：TimeFrame{earliestTime = 8 h 0 min , latestTime = 8 h 30 min , duration = 30.0 min}
                            instance.getEmployeesRequiredArrEachDay().get(i), // 每个时间段需要的员工数：employeesRequiredArr.len = timeFrames.len
                            instance.getEmployees().length, // 员工数
                            instance.getMinC(), // 最小班次时间的时间段数，例如：以30分钟为一段，最小班次为2小时，那么这个值就是4
                            instance.getMaxC(), // 最大班次时间的时间段数，例如：以30分钟为一段，最大班次为4小时，那么这个值就是8
                            instance.getIntervalC(), // 以多少个段为基准去排班，例如：以30分钟为一段，如果这个值设置为2，就代表每个班次的时长必须是2*30=60分钟的整数倍
                            instance.getMinuteEachC(), // 以多少分钟为一段，设置为30，就是以30分钟为一段
                            instance.getLunchFrames().get(i), // 午餐时间范围，在timeFrames中的索引范围
                            instance.getDinnerFrames().get(i), // 晚餐时间范围，在timeFrames中的索引范围
                            instance.getLunchC(), // 午餐时间占多少段，例如：以30分钟为一段，这个值如果是1，就代表午餐时间为30分钟
                            instance.getDinnerC() // 晚餐时间占多少段，例如：以30分钟为一段，这个值如果是1，就代表晚餐时间为30分钟
                    ).solve();
                } else {
                    throw new RuntimeException("【班次生成阶段】无法识别的算法: " + phaseOne);
                }
                shiftList.sort(new Comparator<Shift>() {
                    @Override
                    public int compare(Shift o1, Shift o2) {
                        return Integer.compare(o1.getHead(), o2.getHead());
                    }
                });
            }
            int curDayTotalMinute = 0;
            for (int j = 0; j < shiftList.size(); j++) {
                curDayTotalMinute += shiftList.get(j).getTotalMinute();
                shiftList.get(j).setKey(i + "-" + j);
                shiftList.get(j).setDayIndex(i);
                shiftList.get(j).setShiftIndex(j);
            }
            totalMinute += curDayTotalMinute;
            shiftNumArr[i] = shiftList.size();
            shiftMinuteArr[i] = curDayTotalMinute;
            shiftListList.add(shiftList);
        }
        long phaseOneUseTime = System.currentTimeMillis() - phaseOneStartTime;
        solution.setPhaseOneUseTime(phaseOneUseTime);
        LogUtil.canBePrint = true;
        solution.setTotalMinute(totalMinute);
        solution.setShiftListList(Shift.copyListList(shiftListList));
        LogUtil.debug("------------------------------------ 第一阶段结束( " + phaseOne + " ) : 用时 " + (phaseOneUseTime) + " ms ------------------------------------");

        // 班次分配阶段
        LogUtil.canBePrint = false;
        long phaseTwoStartTime = System.currentTimeMillis();
        if (phaseTwo.equals(AlgoEnum.PhaseTwo.SAEA)) {
            new PS_SAEA(solution, instance.getRestC(), instance.getMaxWorkCEachDay(), instance.getMaxWorkCEachWeek(), instance.getMaxContinuousWorkC(), instance.getTimeFramesEachDay(), instance.getWeekArr(), instance.getEmployees(), instance.getPositionConstraintArr(), instance.getDoubleShiftTimeFramesEachDay(), shiftListList).solve();
        } else if (phaseTwo.equals(AlgoEnum.PhaseTwo.EASA)) {
            new PS_EASA(solution, instance.getRestC(), instance.getMaxWorkCEachDay(), instance.getMaxWorkCEachWeek(), instance.getMaxContinuousWorkC(), instance.getTimeFramesEachDay(), instance.getWeekArr(), instance.getEmployees(), instance.getPositionConstraintArr(), instance.getDoubleShiftTimeFramesEachDay(), shiftListList).solve();
        }else {
            throw new RuntimeException("【班次分配阶段】无法识别的算法: " + phaseOne);
        }
        long phaseTwoUseTime = System.currentTimeMillis() - phaseTwoStartTime;
        solution.setPhaseTwoUseTime(phaseTwoUseTime);
        LogUtil.canBePrint = true;
        LogUtil.debug("------------------------------------ 第二阶段结束( " + phaseTwo + " ) : 用时 " + (phaseTwoUseTime) + " ms ------------------------------------");
        // 输出结果
        int totalM = 0;
        HashSet<String> hashSet = new HashSet<>();
        solution.setUnabsorbedShiftListList(new ArrayList<>());
        for (int i = 0; i < solution.getShiftPlanningListList().size(); i++) {
            if (solution.getUnabsorbedShiftListList().size() <= i) {
                solution.getUnabsorbedShiftListList().add(new ArrayList<>());
            }
            int m = 0; // 已分配班次的时长
            for (ShiftPlanning shiftPlanning : solution.getShiftPlanningListList().get(i)) {
                m += shiftPlanning.getShift().getTotalMinute();
                if (!hashSet.add(shiftPlanning.getShift().getKey())) {
                    throw new RuntimeException("重复的班次: " + shiftPlanning.getShift());
                }
            }
            totalM += m;
        }
        // 识别未分配的班次
        int unabsorbedShiftTotalMinute = 0;
        for (int i = 0; i < solution.getShiftListList().size(); i++) {
            for (Shift shift : solution.getShiftListList().get(i)) {
                if (!hashSet.contains(shift.getKey())) {
                    unabsorbedShiftTotalMinute += shift.getTotalMinute();
                    solution.getUnabsorbedShiftListList().get(i).add(Shift.copy(shift));
                }
            }
        }
        solution.setTotalAssignedMinute(totalM);
        solution.setAllocationRatio(totalMinute == 0 ? 0 : (double) totalM / totalMinute);
        LogUtil.common("未分配班次总时间: " + unabsorbedShiftTotalMinute + " min");
        LogUtil.common("已分配班次总时间: " + totalM + " min");
        LogUtil.common("所有班次总时间:   " + totalMinute + " min");
        LogUtil.common("总分配比率:       " + String.format("%.2f", 100d * solution.getAllocationRatio()) + "%");
        LogUtil.success("------------------------------------ 结束调用智能排班算法 : 总用时 " + phaseOneUseTime + " + " + phaseTwoUseTime + " = " + (phaseOneUseTime + phaseTwoUseTime) + " ms ------------------------------------");
        // 求解完毕，返回 solution
        return solution;
    }

}
