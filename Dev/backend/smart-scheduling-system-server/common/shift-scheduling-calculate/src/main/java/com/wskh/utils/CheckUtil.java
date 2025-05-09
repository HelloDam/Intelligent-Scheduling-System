package com.wskh.utils;

import com.dam.model.dto.scheduling_calculate.Shift;
import com.dam.model.dto.scheduling_calculate.TimeFrame;
import org.apache.poi.hssf.usermodel.HSSFCell;

import java.util.List;

public class CheckUtil {
    public static void checkOneDayShiftIsMeetDemand(List<Shift> shiftList, int[] employeesRequiredArr, int[] lunchFrame, int[] dinnerFrame) {
        int[] employeesWorkingArr = new int[employeesRequiredArr.length];
        for (Shift shift : shiftList) {
            for (int j = shift.getHead(); j < shift.getHead() + shift.getLen(); j++) {
                if (!(shift.getMealType() != null && j >= shift.getMealHead() && j < shift.getMealHead() + shift.getMealLen())) {
                    employeesWorkingArr[j] += 1;
                }
            }
        }
        for (int i = 0; i < employeesWorkingArr.length; i++) {
            if (employeesWorkingArr[i] < employeesRequiredArr[i]) {
                throw new RuntimeException("ERROR: 班次不能覆盖需求");
            }
        }
    }
}
