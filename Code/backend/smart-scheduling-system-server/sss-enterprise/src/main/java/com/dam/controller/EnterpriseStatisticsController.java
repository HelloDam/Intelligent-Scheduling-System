package com.dam.controller;

import com.dam.model.result.R;
import com.dam.service.EnterpriseService;
import com.dam.service.EnterpriseStatisticsService;
import com.dam.utils.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/enterprise/statistics")
public class EnterpriseStatisticsController {
    @Autowired
    private EnterpriseStatisticsService enterpriseStatisticsService;
    @Autowired
    private EnterpriseService enterpriseService;

    /**
     * getRegisterUserNumOfYear
     *
     * @param year
     * @return
     */
    @GetMapping("/getRegisterEnterpriseNumOfYear")
    public R getRegisterEnterpriseNumOfYear(@RequestParam("year") Integer year) {
        List<String> monthNameList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            monthNameList.add(DateUtil.getMonthName(i));
        }
        List<Integer> registerEnterpriseNumList = enterpriseService.getRegisterEnterpriseNumOfYear(year);
        return R.ok().addData("monthNameList", monthNameList).addData("registerEnterpriseNumList", registerEnterpriseNumList);
    }
}
