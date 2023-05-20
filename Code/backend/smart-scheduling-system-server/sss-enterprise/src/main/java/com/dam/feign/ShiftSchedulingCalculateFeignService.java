package com.dam.feign;

import com.dam.config.feign.FeignConfig;
import com.dam.model.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "shift-scheduling-calculate-service",configuration = FeignConfig.class)
public interface ShiftSchedulingCalculateFeignService {

    /**
     * 根据工作日查询出所有需要工作的员工id
     *
     * @param paramMap
     * @return
     */
    @PostMapping("/shift-scheduling-calculate-service/shiftuser/listUserIdByWorkDate")
    public R listUserIdByWorkDate(@RequestBody Map<String, Object> paramMap);

    /**
     * 根据工作日查询出所有需要工作的员工id，及其所负责的班次
     *
     * @param paramMap
     * @return
     */
    @PostMapping("/shift-scheduling-calculate-service/shiftuser/listStaffWorkDtoByWorkDate")
//    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_SHIFT}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]", sync = true)
    public R listStaffWorkDtoByWorkDate(@RequestBody Map<String, Object> paramMap);

    @PostMapping("/shift-scheduling-calculate-service/schedulingdate/judgeOneDateIsRest")
    public R judgeOneDateIsRest(@RequestBody Map<String, Object> paramMap);

}
