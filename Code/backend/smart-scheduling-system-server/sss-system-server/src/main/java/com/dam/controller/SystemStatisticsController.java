package com.dam.controller;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.feign.EnterpriseFeignService;
import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.statistics.systemManager.SystemUseStatisticsVo;
import com.dam.service.SystemStatisticsService;
import com.dam.service.UserService;
import com.dam.utils.JwtUtil;
import com.dam.utils.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/statistics")
public class SystemStatisticsController {
    @Autowired
    private SystemStatisticsService systemStatisticsService;
    @Autowired
    private EnterpriseFeignService enterpriseFeignService;
    @Autowired
    private UserService userService;

    /**
     * 统计企业每个门店的用户人数
     *
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/statisticsStoreUserNum")
    public R statisticsStoreUserNum(HttpServletRequest httpServletRequest) {
        Long enterpriseId = Long.parseLong(JwtUtil.getEnterpriseId(httpServletRequest.getHeader("token")));
        R r = enterpriseFeignService.listAllStoreByAppointEnterpriseId(enterpriseId);
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            List<StoreEntity> storeEntityList = r.getData("list", new TypeReference<List<StoreEntity>>() {
            });
            List<String> storeNameList = new ArrayList<>();
            List<Integer> userNumList = new ArrayList<>();
            for (StoreEntity storeEntity : storeEntityList) {
                userNumList.add(userService.count(new QueryWrapper<UserEntity>().eq("store_id", storeEntity.getId())));
                storeNameList.add(storeEntity.getName());
            }
            systemStatisticsService.shellSort(userNumList, storeNameList);
            return R.ok().addData("storeNameList", storeNameList).addData("userNumList", userNumList);
        } else {
            return R.error(ResultCodeEnum.Feign_ERROR);
        }

    }

    /**
     * 统计系统使用情况
     *
     * @return
     */
    @GetMapping("/getSystemUseStatisticsVo")
    public R getSystemUseStatisticsVo() {
        SystemUseStatisticsVo systemUseStatisticsVo = systemStatisticsService.getSystemUseStatisticsVo();
        return R.ok().addData("systemUseStatisticsVo", systemUseStatisticsVo);
    }

    /**
     * getRegisterUserNumOfYear
     *
     * @param year
     * @return
     */
    @GetMapping("/getRegisterUserNumOfYear")
    public R getRegisterUserNumOfYear(@RequestParam("year") Integer year) {
        List<String> monthNameList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            monthNameList.add(DateUtil.getMonthName(i));
        }
        List<Integer> registerUserNumList = userService.getRegisterUserNumOfYear(year);
        return R.ok().addData("monthNameList", monthNameList).addData("registerUserNumList", registerUserNumList);
    }

}
