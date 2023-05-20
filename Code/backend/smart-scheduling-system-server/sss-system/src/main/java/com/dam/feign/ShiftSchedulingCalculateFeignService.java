package com.dam.feign;

import com.dam.config.feign.FeignConfig;
import com.dam.model.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient(value = "shift-scheduling-calculate-service",configuration = FeignConfig.class)
public interface ShiftSchedulingCalculateFeignService {

    /**
     * 查询指定时间段繁忙的用户id
     *
     * @param params
     * @return
     */
    @PostMapping("/shift-scheduling-calculate-service/shiftuser/listUserIdListIsBusy")
    public R listUserIdIsBusy(@RequestBody Map<String, Object> params);

    /**
     * 根据一个日期段内 所有需要工作的员工id
     *
     * @param paramMap
     * @return
     */
    @PostMapping("/shift-scheduling-calculate-service/shiftuser/listUserIdByDateSegment")
    public R listUserIdByDateSegment(@RequestBody Map<String, Object> paramMap);

    /**
     * 获取系统的用户数量
     *
     * @return int count
     */
    @RequestMapping("/shift-scheduling-calculate-service/schedulingTask/getAllTaskNum")
    public R getAllTaskNum();
}
