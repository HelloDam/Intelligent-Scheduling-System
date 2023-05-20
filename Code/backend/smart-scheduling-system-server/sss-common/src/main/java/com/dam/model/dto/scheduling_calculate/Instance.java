package com.dam.model.dto.scheduling_calculate;

import lombok.Data;

import java.util.HashSet;
import java.util.List;

@Data
public class Instance {
    /**
     * [天数][时间段数]
     * 时间段集合，例如：TimeFrame{earliestTime = 8 h 0 min , latestTime = 8 h 30 min , duration = 30.0 min}
     **/
    List<TimeFrame[]> timeFramesEachDay;
    /**
     * [天数][2]
     * 一天中两头班(清扫和收尾)的时间段索引数组 [清扫的结束时间段索引，收尾的开始时间段索引]
     * 例如：假设 doubleShiftTimeFramesEachDay[25][0] = 4 , 代表第26天的清扫时间范围是那一天的 0 ~ 4 段
     * 例如：假设 doubleShiftTimeFramesEachDay[25][1] = 18 , 代表第26天的收尾时间范围是那一天的 18 ~ timeFramesEachDay[25].length-1 段
     **/
    List<int[]> doubleShiftTimeFramesEachDay;
    /**
     * [天数][时间段数]
     * 每一天每个时间段需要的员工数：employeesRequiredArrEachDay.len = timeFramesEachDay.len
     **/
    List<int[]> employeesRequiredArrEachDay;
    /**
     * 长度为3
     * 清扫段的职位约束，中间段的职位约束，收尾段的职位约束
     **/
    HashSet<String>[] positionConstraintArr;
    /**
     * 每一天是星期几，例如：[1、2、3、4、5、6、7、1、2]代表第一天是星期1，第二天是星期2...，第8天又回到星期1
     **/
    int[] weekArr;
    /**
     * 员工数组
     **/
    Employee[] employees;
    /**
     * 最小班次时间的时间段数，例如：以30分钟为一段，最小班次为2小时，那么这个值就是4
     **/
    int minC = 4;
    /**
     * 最大班次时间的时间段数，例如：以30分钟为一段，最大班次为4小时，那么这个值就是8
     **/
    int maxC = 8;
    /**
     * 以多少个段为基准去排班，例如：以30分钟为一段，如果这个值设置为2，就代表每个班次的时长必须是2*30=60分钟的整数倍
     **/
    int intervalC = 2;
    /**
     * 以多少分钟为一段，设置为30，就是以30分钟为一段
     **/
    int minuteEachC = 30;
    /**
     * 午餐时间范围，在timeFrames中的索引范围
     **/
    List<int[]> lunchFrames;
    /**
     * 晚餐时间范围，在timeFrames中的索引范围
     **/
    List<int[]> dinnerFrames;
    /**
     * 午餐时间占多少段，例如：以30分钟为一段，这个值如果是1，就代表午餐时间为30分钟
     **/
    int lunchC = 1;
    /**
     * 晚餐时间占多少段，例如：以30分钟为一段，这个值如果是1，就代表晚餐时间为30分钟
     **/
    int dinnerC = 1;
    /**
     * 休息时间占多少段，例如：以30分钟为一段，这个值如果是1，就代表休息时间为30分钟
     **/
    int restC = 1;
    /**
     * 员工每天最多工作多少段
     **/
    int maxWorkCEachDay = 16;
    /**
     * 员工每周最多工作多少段
     **/
    int maxWorkCEachWeek = 80;
    /**
     * 员工最长连续工作时间段数
     **/
    int maxContinuousWorkC = 8;
}
