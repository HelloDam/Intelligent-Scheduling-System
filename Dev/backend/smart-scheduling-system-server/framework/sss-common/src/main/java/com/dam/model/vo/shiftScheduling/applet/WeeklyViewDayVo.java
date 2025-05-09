package com.dam.model.vo.shiftScheduling.applet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyViewDayVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 一周的第几天 从1开始
     */
    private Integer dayOfWeek;
    /**
     * 当天的任务
     */
    private List<WeeklyTaskVo> task;
}
