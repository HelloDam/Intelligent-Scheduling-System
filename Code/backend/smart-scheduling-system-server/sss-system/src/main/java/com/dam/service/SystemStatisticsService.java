package com.dam.service;

import com.dam.model.vo.statistics.systemManager.SystemUseStatisticsVo;

import java.util.List;

public interface SystemStatisticsService {
    void shellSort(List<Integer> userNumList, List<String> storeNameList);


    /**
     * 统计系统使用情况
     * @return
     */
    SystemUseStatisticsVo getSystemUseStatisticsVo();

}
