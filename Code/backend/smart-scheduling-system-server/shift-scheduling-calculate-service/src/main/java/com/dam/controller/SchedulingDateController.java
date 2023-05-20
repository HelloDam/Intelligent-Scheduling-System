package com.dam.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.constant.RedisConstant;
import com.dam.feign.EnterpriseFeignService;
import com.dam.model.entity.shiftScheduling.SchedulingDateEntity;
import com.dam.model.entity.shiftScheduling.SchedulingTaskEntity;
import com.dam.model.result.R;
import com.dam.service.SchedulingShiftService;
import com.dam.service.SchedulingTaskService;
import com.dam.service.ShiftUserService;
import com.dam.utils.JwtUtil;
import com.dam.utils.PageUtils;
import com.dam.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import com.dam.service.SchedulingDateService;

import javax.servlet.http.HttpServletRequest;


/**
 * 排班日期表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-04 14:30:17
 */
@RestController
@RequestMapping("/shift-scheduling-calculate-service/schedulingdate")
public class SchedulingDateController {
    @Autowired
    private SchedulingDateService schedulingDateService;
    @Autowired
    private SchedulingShiftService schedulingShiftService;
    @Autowired
    private ShiftUserService shiftUserService;
    @Autowired
    private SchedulingTaskService schedulingTaskService;
    ////远程服务
    @Autowired
    private EnterpriseFeignService enterpriseFeignService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = schedulingDateService.queryPage(params);

        return R.ok().addData("page", page);
    }

    /**
     * 查询出有安排班次的date
     *
     * @return
     */
    @PostMapping("/listDateByCondition")
//    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_DATE}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]", sync = true)
    public R listDateByCondition(@RequestBody Map<String, Object> conditionMap, HttpServletRequest httpServletRequest) throws ParseException {
        long start = System.currentTimeMillis();
        Map<String, SchedulingDateEntity> dateAndDateEntityMap = null;

        ////查询条件
        Long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        //所查询的职位id
        List<Long> positionIdList = (List<Long>) conditionMap.get("positionIdList");
        //所查询的用户id
        List<Long> userIdList = (List<Long>) conditionMap.get("userIdList");
        //所查询的任务id
        Long taskId = null;
        if (conditionMap.containsKey("taskId")) {
            taskId = Long.parseLong(conditionMap.get("taskId").toString());
        }
        //查询未分配班次
        Boolean isSearchUnAssignedShifts = null;
        if (conditionMap.containsKey("isSearchUnAssignedShifts")) {
            isSearchUnAssignedShifts = Boolean.parseBoolean(conditionMap.get("isSearchUnAssignedShifts").toString());
        }


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/M/d");
        //当前日期
        String dayStr = (String) conditionMap.get("day");
        Date date = sdf.parse(dayStr);
        //获取所查看月份的第一天的日期
        Date firstDayOfOneMonth = TimeUtil.getFirstDayOfOneMonth(date.getTime());
        //获取所查看月份的最后一天的日期
        Date lastDayOfOneMonth = TimeUtil.getLastDayOfOneMonth(date.getTime());

        List<Long> realTaskIdList = new ArrayList<>();
        if (taskId == null) {
            //--if--如果没有指定要查询的任务id，查询出当前门店所有已经发布的真实任务的id
            realTaskIdList.addAll(schedulingTaskService.list(new QueryWrapper<SchedulingTaskEntity>().eq("type", 0).eq("store_id", storeId).eq("is_publish", 1))
                    .stream().map(SchedulingTaskEntity::getId).collect(Collectors.toList()));
        } else {
            //--if--如果携带了任务id，说明是查询任务的结构
            realTaskIdList.add(taskId);
        }

        if (realTaskIdList.size() == 0) {
            return R.ok().addData("dateEntityMap", new HashMap<>());
        }
        //查出当前月份的所有date
        QueryWrapper<SchedulingDateEntity> dateQueryWrapper = new QueryWrapper<SchedulingDateEntity>().eq("store_id", storeId)
                .ge("date", firstDayOfOneMonth).le("date", lastDayOfOneMonth)
                .in("task_id", realTaskIdList);
        List<SchedulingDateEntity> schedulingDateEntityList = schedulingDateService.list(dateQueryWrapper);

        //存储到字典中
        List<Long> curMonthDateIdList = new ArrayList<>();
        if (schedulingDateEntityList != null) {
            dateAndDateEntityMap = schedulingDateEntityList.stream().collect(Collectors.toMap(fieldError -> {
                curMonthDateIdList.add(fieldError.getId());
                //键
                return sdf1.format(fieldError.getDate());
            }, fieldError -> {
                //值
                return fieldError;
            }));
        }

        List<Long> dateIdList = null;
        if (isSearchUnAssignedShifts == null || isSearchUnAssignedShifts == false) {
            if (positionIdList != null && positionIdList.size() > 0) {
                //--if-- 前端所勾选的职位数量>0，查询出职位对应的班次所对应的date
                dateIdList = schedulingShiftService.listDateIdByPositionIdList(positionIdList, curMonthDateIdList);
            }
            if (userIdList != null && userIdList.size() > 0) {
                //--if-- 前端所勾选的用户数量>0，查询出职位对应的班次所对应的date
                dateIdList = schedulingShiftService.listDateIdByUserIdList(userIdList, curMonthDateIdList);
            }
        } else {
            //--if--前端勾选了 未分配班次
            dateIdList = schedulingShiftService.listDateIdWithUnAssignedShifts(curMonthDateIdList);
            System.out.println("查询未分配班次的日历：" + dateIdList.toString());
        }

        //给有班次的date设置haveShift
        if (dateIdList != null && dateIdList.size() > 0) {
            dateQueryWrapper = new QueryWrapper<SchedulingDateEntity>().eq("store_id", storeId).ge("date", firstDayOfOneMonth).le("date", lastDayOfOneMonth).in("id", dateIdList);
            List<SchedulingDateEntity> shiftDateEntityList = schedulingDateService.list(dateQueryWrapper);
            for (SchedulingDateEntity schedulingDateEntity : shiftDateEntityList) {
                //这些天存在班次
                dateAndDateEntityMap.get(sdf1.format(schedulingDateEntity.getDate())).setIsHaveShift(1);
            }
        }
        System.out.println("listDateByCondition 查询时间1：" + (System.currentTimeMillis() - start) + "ms");
        return R.ok().addData("dateEntityMap", dateAndDateEntityMap);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        SchedulingDateEntity schedulingDate = schedulingDateService.getById(id);

        return R.ok().addData("schedulingDate", schedulingDate);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SchedulingDateEntity schedulingDate) {
        schedulingDateService.save(schedulingDate);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SchedulingDateEntity schedulingDate) {
        schedulingDateService.updateById(schedulingDate);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        schedulingDateService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/judgeOneDateIsRest")
    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_DATE}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]", sync = true)
    public R judgeOneDateIsRest(@RequestBody Map<String, Object> paramMap) {
        System.out.println("date:" + paramMap.get("date").toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date workDate = null;
        try {
            workDate = sdf.parse(paramMap.get("date").toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Long storeId = Long.parseLong(paramMap.get("storeId").toString());
        Boolean isRest = schedulingDateService.judgeOneDateIsRest(storeId, workDate);
        return R.ok().addData("isRest", isRest);
    }

}
