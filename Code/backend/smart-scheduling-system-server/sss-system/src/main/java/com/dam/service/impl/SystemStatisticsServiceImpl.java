package com.dam.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.feign.EnterpriseFeignService;
import com.dam.feign.ShiftSchedulingCalculateFeignService;
import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.statistics.systemManager.SystemUseStatisticsVo;
import com.dam.service.SystemStatisticsService;
import com.dam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class SystemStatisticsServiceImpl implements SystemStatisticsService {
    @Autowired
    private UserService userService;
    @Autowired
    private ShiftSchedulingCalculateFeignService shiftSchedulingCalculateFeignService;
    @Autowired
    private EnterpriseFeignService enterpriseFeignService;
    @Autowired
    private ThreadPoolExecutor executor;


    @Override
    public void shellSort(List<Integer> userNumList, List<String> storeNameList) {
        // 增量gap, 并逐步的缩小增量
        for (int gap = userNumList.size() / 2; gap > 0; gap /= 2) {
            // 从第gap个元素，逐个对其所在的组进行直接插入排序
            for (int i = gap; i < userNumList.size(); i++) {
                int j = i;
                int temp = userNumList.get(j);
                String tempStoreName = storeNameList.get(j);
                if (userNumList.get(j) < userNumList.get(j - gap)) {
                    while (j - gap >= 0 && temp < userNumList.get(j - gap)) {
                        //移动，后移
                        userNumList.set(j, userNumList.get(j - gap));
                        storeNameList.set(j, storeNameList.get(j - gap));
                        j -= gap;
                    }
                    //当退出while后，就给temp找到插入的位置
                    userNumList.set(j, temp);
                    storeNameList.set(j, tempStoreName);
                }

            }
        }
    }

    @Override
    public SystemUseStatisticsVo getSystemUseStatisticsVo() {
        SystemUseStatisticsVo systemUseStatisticsVo = new SystemUseStatisticsVo();

        // 1.获取之前的请求头数据
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        //统计计算任务数量
        CompletableFuture<Void> taskFuture = CompletableFuture.runAsync(() -> {
            //2.每一个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R r1 = shiftSchedulingCalculateFeignService.getAllTaskNum();
            if (r1.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                systemUseStatisticsVo.setCalculatedTaskNum(r1.getData("count", new TypeReference<Integer>() {
                }));
            }
        }, executor);

        //统计企业数量
        CompletableFuture<Void> enterpriseFuture = CompletableFuture.runAsync(() -> {
            //2.每一个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R r2 = enterpriseFeignService.getAllEnterpriseNum();
            if (r2.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                systemUseStatisticsVo.setEnterpriseNum(r2.getData("count", new TypeReference<Integer>() {
                }));
            }
        }, executor);

        //统计门店数量
        CompletableFuture<Void> storeFuture = CompletableFuture.runAsync(() -> {
            //2.每一个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R r3 = enterpriseFeignService.getAllEnterpriseNum();
            if (r3.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                systemUseStatisticsVo.setStoreNum(r3.getData("count", new TypeReference<Integer>() {
                }));
            }
        }, executor);

        //统计用户数量
        CompletableFuture<Void> userFuture = CompletableFuture.runAsync(() -> {
            //2.每一个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            int userNum = userService.count(new QueryWrapper<UserEntity>().eq("is_deleted", 0));
            systemUseStatisticsVo.setUserNum(userNum);
        }, executor);

        try {
            CompletableFuture.allOf(taskFuture, enterpriseFuture, storeFuture, userFuture).get();
            return systemUseStatisticsVo;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
