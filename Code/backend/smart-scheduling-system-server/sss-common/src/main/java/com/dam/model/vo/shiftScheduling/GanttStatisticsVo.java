package com.dam.model.vo.shiftScheduling;

import lombok.Data;

import java.io.Serializable;

/**
 * 甘特图统计数据
 */
@Data
public class GanttStatisticsVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 班次总数量
     */
    private Integer totalShiftNum;
    /**
     * 班次总时长
     */
    private Long totalShiftMinute;
    /**
     * 参与员工数量
     */
    private Integer staffNum;
    /**
     * 员工平均工作时长
     */
    private Double averageWorkMinute;
    /**
     * 吃午饭人数
     */
    private Integer lunchPersonNum;
    /**
     * 吃晚饭人数
     */
    private Integer dinnerPersonNum;
}
