package com.dam.model.vo.shiftScheduling;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 将任务以 年 月 区分开来
 */
@Data
public class TaskCreateTimeTreeItemVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long value;
    private String label;
    private List<TaskCreateTimeTreeItemVo> children;

    public TaskCreateTimeTreeItemVo(Long value, String label) {
        this.value = value;
        this.label = label;
    }
}
