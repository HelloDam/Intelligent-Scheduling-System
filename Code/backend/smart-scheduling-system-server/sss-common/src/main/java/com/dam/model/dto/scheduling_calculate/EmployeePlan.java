package com.dam.model.dto.scheduling_calculate;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class EmployeePlan {
    // 员工总工作时间
    public int totalWorkMinute;
    public List<List<Shift>> shiftListList = new ArrayList<>();
    // 记录员工每天工作时间（段数）
    public int[] workTimeEachDay;

    public EmployeePlan(int dayNum) {
        for (int i = 0; i < dayNum; i++) {
            shiftListList.add(new ArrayList<>());
        }
        this.workTimeEachDay = new int[dayNum];
    }

    public static EmployeePlan copy(EmployeePlan employeePlan) {
        EmployeePlan res = new EmployeePlan(employeePlan.shiftListList.size());
        res.totalWorkMinute = employeePlan.totalWorkMinute;
        res.workTimeEachDay = employeePlan.workTimeEachDay.clone();
        for (int i = 0; i < employeePlan.shiftListList.size(); i++) {
            res.shiftListList.set(i, new ArrayList<>(employeePlan.shiftListList.get(i)));
        }
        return res;
    }

    public static EmployeePlan[] copy(EmployeePlan[] employeePlans) {
        EmployeePlan[] copy = new EmployeePlan[employeePlans.length];
        for (int i = 0; i < employeePlans.length; i++) {
            copy[i] = copy(employeePlans[i]);
        }
        return copy;
    }

}
