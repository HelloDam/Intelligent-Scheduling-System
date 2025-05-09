package com.dam.model.vo.shiftScheduling;

import com.dam.model.entity.shiftScheduling.SchedulingDateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
public class WeekViewVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 周视图是否存在有效数据
     */
    private boolean isExist;
    private List<List<WeekViewShiftVo>> shiftListOfEachDay;
    /**
     * 每天的date，可以查看是否需要上班
     */
    private HashMap<Integer, SchedulingDateEntity> indexAndDateMap;
}
