package com.dam.service.impl;

import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.model.entity.shiftScheduling.SchedulingDateEntity;
import com.dam.model.entity.shiftScheduling.SchedulingTaskEntity;
import com.dam.service.SchedulingTaskService;
import com.dam.service.ShiftSchedulingAlgorithmService;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dam.dao.SchedulingDateDao;
import com.dam.service.SchedulingDateService;
import org.springframework.transaction.annotation.Transactional;


@Service("schedulingDateService")
public class SchedulingDateServiceImpl extends ServiceImpl<SchedulingDateDao, SchedulingDateEntity> implements SchedulingDateService {
    @Autowired
    private ShiftSchedulingAlgorithmService shiftSchedulingAlgorithmService;
    @Autowired
    private SchedulingTaskService schedulingTaskService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SchedulingDateEntity> page = this.page(
                new Query<SchedulingDateEntity>().getPage(params),
                new QueryWrapper<SchedulingDateEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SchedulingDateEntity> listDateBetweenStartDateAndEndDate(Date startDate, Date endDate, long storeId) {
        return baseMapper.listDateBetweenStartDateAndEndDate(startDate, endDate, storeId);
    }

    @Override
    public Boolean judgeOneDateIsRest(Long storeId, Date workDate) {
        Integer isNeedWork = baseMapper.judgeOneDateIsRest(storeId, workDate);
        return isNeedWork == 0 ? true : false;
    }

    /**
     * 获取门店指定时间段的工作日
     * @param startDate
     * @param endDate
     * @param storeId
     * @return
     */
    @Override
    public List<SchedulingDateEntity> getWorkDayList(Date startDate, Date endDate, Long storeId) {
        long start = System.currentTimeMillis();
        ///查询出改门店的所有真实任务
        QueryWrapper<SchedulingTaskEntity> taskQueryWrapper = new QueryWrapper<SchedulingTaskEntity>().eq("store_id",storeId).eq("is_deleted", 0).eq("is_publish", 1).eq("type", 0);
        List<Long> realTaskIdList = schedulingTaskService.list(taskQueryWrapper).stream().map(SchedulingTaskEntity::getId).collect(Collectors.toList());
        ///查询出所有指定月份、指定门店的所有工作日
        QueryWrapper dateQueryWrapper = new QueryWrapper<SchedulingDateEntity>()
                .eq("store_id", storeId)
                .eq("is_deleted", 0)
                .eq("is_need_work", 1)
                .ge("date", startDate).le("date", endDate);
        if (realTaskIdList.size() > 0) {
            dateQueryWrapper.in("task_id", realTaskIdList);
        } else {
            //--if--肯定查不到数据
            dateQueryWrapper.eq("id", -1);
        }
        List<SchedulingDateEntity> shiftDateEntityList = baseMapper.selectList(dateQueryWrapper);
//        System.out.println("getWorkDayList时间："+(System.currentTimeMillis()-start)+"ms");
        return shiftDateEntityList;
    }


}