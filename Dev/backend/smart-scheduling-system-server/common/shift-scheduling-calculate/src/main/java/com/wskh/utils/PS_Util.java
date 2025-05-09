package com.wskh.utils;

import com.dam.model.dto.scheduling_calculate.Employee;
import com.dam.model.dto.scheduling_calculate.Shift;
import com.dam.model.dto.scheduling_calculate.Time;
import com.dam.model.dto.scheduling_calculate.TimeFrame;
import com.dam.model.dto.scheduling_calculate.EmployeePlan;

import java.util.*;

public class PS_Util {

    // 判断员工是否可以和该班次匹配
    // 判断员工是否可以和该班次匹配
    public static boolean judgeFeasible(Shift shift, Employee employee, EmployeePlan employeePlan, List<int[]> doubleShiftTimeFramesEachDay, HashSet<String>[] positionConstraintArr,
                                        List<TimeFrame[]> timeFramesEachDay, int[] weekArr, int restC, int maxWorkCEachDay, int maxWorkCEachWeek, int maxContinuousWorkC) {
        int curDayIndex = shift.getDayIndex();
        // 1. 检验该员工是否满足该shift的职位约束
        int type = 1; // 默认和清扫阶段、收尾阶段没有交叉
        if (shift.getHead() < doubleShiftTimeFramesEachDay.get(curDayIndex)[0]) {
            type = 0; // 和清扫阶段有交叉
        } else if (shift.getHead() + shift.getLen() - 1 > doubleShiftTimeFramesEachDay.get(curDayIndex)[1]) {
            type = 2; // 和收尾阶段有交叉
        }
        if (positionConstraintArr != null && !positionConstraintArr[type].contains(employee.getPosition())) {
            // 不满足职位约束，直接返回false
//            System.out.println("不满足职位约束，直接返回false");
            return false;
        }
        // 2. 检验当前班次和员工已经被分配的班次有没有时间上的重叠 和 是否满足休息时间约束
        for (Shift employeePlanShift : employeePlan.shiftListList.get(curDayIndex)) {
            if (!(employeePlanShift.getHead() > shift.getHead() + shift.getLen() - 1 || shift.getHead() > employeePlanShift.getHead() + employeePlanShift.getLen() - 1)) {
//                System.out.println("班次时间重叠，返回false: " + employeePlanShift + " , " + shift);
                return false;
            } else {
                int diffC = 0;
                if (employeePlanShift.getHead() > shift.getHead()) {
                    diffC = shift.getHead() - (employeePlanShift.getHead() + employeePlanShift.getLen() - 1);
                } else {
                    diffC = employeePlanShift.getHead() - (shift.getHead() + shift.getLen() - 1);
                }
                if (diffC > 1 && diffC - 1 < restC) {
                    System.out.println("不满足休息时间约束，返回false");
                    return false;
                }
            }
        }
        // 3. 检验shift是否符合员工的偏好
        // 3.1 工作日偏好
        if (employee.getWorkDayPreference() != null && employee.getWorkDayPreference()[weekArr[curDayIndex] - 1] == 0) {
//            System.out.println("不满足工作日偏好，返回false");
            return false;
        }
        // 3.2 工作时间偏好
        if (employee.getWorkTimePreference() != null) {
            Time startTime = timeFramesEachDay.get(curDayIndex)[shift.getHead()].getEarliestTime();
            Time endTime = timeFramesEachDay.get(curDayIndex)[shift.getHead() + shift.getLen() - 1].getLatestTime();
            boolean b = false;
            for (TimeFrame timeFrame : employee.getWorkTimePreference()) {
                if (startTime.compareTo(timeFrame.getEarliestTime()) != -1 && endTime.compareTo(timeFrame.getLatestTime()) != 1) {
                    b = true;
                    break;
                }
            }
            if (!b) {
//                System.out.println("不满足工作时间偏好，返回false");
                return false;
            }
        }
        // 3.3 班次时长偏好，每天时长不超过多少段
        int workTime = (shift.getLen() - (shift.getMealType() == null ? 0 : shift.getMealLen()));
        if (employeePlan.workTimeEachDay[curDayIndex] + workTime > maxWorkCEachDay) {
//            System.out.println("不满足每天工作时长偏好，返回false");
            return false;
        }
        if (employee.getShiftTimePreference_maxWorkTimeEachDay() != null && employeePlan.workTimeEachDay[curDayIndex] + workTime > employee.getShiftTimePreference_maxWorkTimeEachDay()) {
//            System.out.println("不满足每天工作时长偏好，返回false");
            return false;
        }
        // 3.4 班次时长偏好，每周时长不超过多少段
        int weekTime = 0;
        int head = Math.max(0, curDayIndex - 7 + 1);
        for (int i = head; i < timeFramesEachDay.size() && i < curDayIndex + 7; i++) {
            int curWorkTime = employeePlan.workTimeEachDay[i] + (i == curDayIndex ? workTime : 0);
            if (i < head + 7) {
                weekTime += curWorkTime;
            } else {
                weekTime = weekTime + curWorkTime - employeePlan.workTimeEachDay[i - 6];
            }
            if (employee.getShiftTimePreference_maxWorkTimeEachWeek() != null && weekTime > employee.getShiftTimePreference_maxWorkTimeEachWeek()) {
//                System.out.println("不满足每周工作时长偏好，返回false: " + weekTime+" , ShiftTimePreference_maxWorkTimeEachWeek: "+employee.getShiftTimePreference_maxWorkTimeEachWeek());
                return false;
            }
            if (weekTime > maxWorkCEachWeek) {
//                System.out.println("不满足每周工作时长偏好，返回false");
                return false;
            }
        }
        // 4. 检验是否满足连续工作时间限制
        if (!employeePlan.shiftListList.get(curDayIndex).isEmpty()) {
            List<Shift> shiftList = new ArrayList<>(employeePlan.shiftListList.get(curDayIndex));
            shiftList.add(shift);
            shiftList.sort(new Comparator<Shift>() {
                @Override
                public int compare(Shift o1, Shift o2) {
                    return Integer.compare(o1.getHead(), o2.getHead());
                }
            });
            Shift firstShift = shiftList.get(0);
            int continueWorkTime = firstShift.getLen() - (firstShift.getMealType() == null ? 0 : firstShift.getMealLen());
            for (int i = 1; i < shiftList.size(); i++) {
                Shift lastShift = shiftList.get(i - 1);
                Shift curShift = shiftList.get(i);
                int curWorkTime = curShift.getLen() - (curShift.getMealType() == null ? 0 : curShift.getMealLen());
                if (curShift.getHead() == lastShift.getHead() + lastShift.getLen()) {
                    continueWorkTime += curWorkTime;
                    if (continueWorkTime > maxContinuousWorkC) {
                        return false;
                    }
                } else {
                    continueWorkTime = curWorkTime;
                }
            }
//            Shift lastShift = employeePlan.shiftListList.get(curDayIndex).get(employeePlan.shiftListList.get(curDayIndex).size() - 1);
//            if (shift.getHead() == lastShift.getHead() + lastShift.getLen()) {
//                int curWorkTime = shift.getLen() - (shift.getMealType() == null ? 0 : shift.getMealLen());
//                continueWorkTime += curWorkTime;
//                if (continueWorkTime > maxContinuousWorkC) {
//                    return false;
//                }
//            }
        }
        // 走到这就通过检验了！
        return true;
    }

    // 找到班次最多的一天Definitely索引
    public static int findMaxShiftNumDayIndex(short[] finishArr, List<List<Shift>> shiftListList) {
        int res = -1;
        for (int i = 0; i < shiftListList.size(); i++) {
            if (finishArr[i] == 0) {
                if (res == -1 || shiftListList.get(i).size() > shiftListList.get(res).size()) {
                    res = i;
                }
            }
        }
        return res;
    }

}
