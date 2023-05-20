package com.dam.model.vo.shiftScheduling;

import com.baomidou.mybatisplus.annotation.*;
import com.dam.utils.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 用来绘制甘特图
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-04 14:30:17
 */
@Data
@TableName("scheduling_shift")
public class GanttShiftVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设置主键自增
     */
    private String id;
    private String eid;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Date updateTime;
    /**
     * 班次开始时间 2023-02-27 07:00:00
     */
    private Date startDate;
    private Date start_date;
    private String startTime;
    /**
     * 班次结束时间 2023-02-27 10:30:00
     */
    private Date endDate;
    private Date end_date;
    private String endTime;
    /**
     * 对应排班工作日的id
     */
    private Long schedulingDateId;
    /**
     * 吃饭开始时间
     */
    private Date mealStartDate;
    /**
     * 吃饭结束时间
     */
    private Date mealEndDate;
    /**
     * 0：午餐 1：晚餐 2：不安排用餐
     */
    private Integer mealType;
    /**
     * 班次时间（分钟）
     */
    private Integer totalMinute;
    /**
     * 员工姓名集合
     */
    private List<String> staffNameList;
    /**
     * 员工姓名集合字符串
     */
    private String staffNameListStr;
    private String text;
    /**
     * 职位名称集合
     */
    private List<String> positionNameList;
    /**
     * 员工名称集合字符串
     */
    private String positionNameListStr;
    private String dep;
    private Double progress = 0.0;
    private String render;
    private String parent;
    /**
     * 1 深颜色 2 中等颜色 3浅颜色
     */
    private String priority;

    public void setId(String id) {
        this.id = id;
        this.eid = id + "";
    }

    public void setStaffNameList(List<String> staffNameList) {
        this.staffNameList = staffNameList;
        this.staffNameListStr = StringUtils.join(staffNameList, ",");
        this.text = this.staffNameListStr;
    }

    public void setPositionNameList(List<String> positionNameList) {
        this.positionNameList = positionNameList;
        this.positionNameListStr = StringUtils.join(positionNameList, ",");
        this.dep = this.positionNameListStr;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        this.start_date = startDate;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        this.startTime = sdf.format(startDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        this.end_date = endDate;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        this.endTime = sdf.format(endDate);
    }
}
