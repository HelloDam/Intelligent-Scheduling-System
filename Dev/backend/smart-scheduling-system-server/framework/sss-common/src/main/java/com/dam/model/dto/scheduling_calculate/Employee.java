package com.dam.model.dto.scheduling_calculate;

import lombok.Data;

import java.util.Arrays;

@Data
public class Employee {
    /**
     * 员工id
     **/
    String id;
    /**
     * 员工职位
     **/
    String position;
    /**
     * 工作日偏好，如 [0,1,1,0,1,0,1]代表员工偏好星期2、3、5、7工作。如果员工不设置就为null
     **/
    short[] workDayPreference;
    /**
     * 工作时间偏好，如 [8:00,11:00],[13:00,18:00] 代表员工偏好在一天中的8:00到11:00和13:00到18:00工作。如果员工不设置就为null
     **/
    TimeFrame[] workTimePreference;
    /**
     * 班次时长偏好，每天时长不超过多少段。如果员工不设置就为null
     **/
    Integer shiftTimePreference_maxWorkTimeEachDay;
    /**
     * 班次时长偏好，每周时长不超过多少段。如果员工不设置就为null
     **/
    Integer shiftTimePreference_maxWorkTimeEachWeek;

    public static Employee copy(Employee e) {
        if (e == null) {
            return null;
        }
        Employee employee = new Employee();
        employee.setId(e.getId());
        employee.setPosition(e.getPosition());
        employee.setWorkDayPreference(e.getWorkDayPreference().clone());
        employee.setWorkTimePreference(TimeFrame.copy(e.getWorkTimePreference()));
        employee.setShiftTimePreference_maxWorkTimeEachDay(e.getShiftTimePreference_maxWorkTimeEachDay());
        employee.setShiftTimePreference_maxWorkTimeEachWeek(e.getShiftTimePreference_maxWorkTimeEachWeek());
        return employee;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", position='" + position + '\'' +
                ", workDayPreference=" + Arrays.toString(workDayPreference) +
                ", workTimePreference=" + Arrays.toString(workTimePreference) +
                ", shiftTimePreference_maxWorkTimeEachDay=" + shiftTimePreference_maxWorkTimeEachDay +
                ", shiftTimePreference_maxWorkTimeEachWeek=" + shiftTimePreference_maxWorkTimeEachWeek +
                '}';
    }
}
