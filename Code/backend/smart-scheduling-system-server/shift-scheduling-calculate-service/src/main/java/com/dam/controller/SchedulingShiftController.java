package com.dam.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.constant.RedisConstant;
import com.dam.exception.SSSException;
import com.dam.model.entity.shiftScheduling.SchedulingDateEntity;
import com.dam.model.entity.shiftScheduling.SchedulingShiftEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.shiftScheduling.GanttShiftVo;
import com.dam.model.vo.shiftScheduling.GanttStatisticsVo;
import com.dam.model.vo.shiftScheduling.WeekViewShiftVo;
import com.dam.model.vo.shiftScheduling.WeekViewVo;
import com.dam.model.vo.shiftScheduling.applet.WeeklyTaskVo;
import com.dam.model.vo.shiftScheduling.applet.WeeklyViewDayVo;
import com.dam.utils.JwtUtil;
import com.dam.utils.PageUtils;
import com.dam.utils.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;


import com.dam.service.SchedulingShiftService;

import javax.servlet.http.HttpServletRequest;


/**
 * 排班班次表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-04 14:30:17
 */
@RestController
@RequestMapping("/shift-scheduling-calculate-service/schedulingshift")
public class SchedulingShiftController {
    @Autowired
    private SchedulingShiftService schedulingShiftService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = schedulingShiftService.queryPage(params);

        return R.ok().addData("page", page);
    }

    /**
     * 根据日期id获取当天的班次信息
     * 如果选择了职位，只查询职位所绑定的工作人员的班次
     *
     * @return
     */
    @PostMapping("listSchedulingShiftVoByDateId")
//    @Cacheable(value = {RedisConstant.MODULE_SHIFT_SCHEDULING_CALCULATE_SHIFT}, key = "#root.targetClass+'-'+#root.method.name+'-'+#root.args[0]", sync = true)
    public R listSchedulingShiftVoByDateId(@RequestBody Map<String, Object> map) {
        Long dateId = Long.parseLong(map.get("dateId").toString());
        List<Long> positionIdList = (List<Long>) map.get("positionIdArr");
        List<Long> userIdList = (List<Long>) map.get("userIdList");
        Boolean isSearchUnAssignedShifts = null;
        if (map.containsKey("isSearchUnAssignedShifts")) {
            isSearchUnAssignedShifts = Boolean.parseBoolean(map.get("isSearchUnAssignedShifts").toString());
        }
        List<GanttShiftVo> schedulingShiftVoList = schedulingShiftService.listSchedulingShiftVoByDateId(dateId, positionIdList, userIdList, isSearchUnAssignedShifts);
        GanttStatisticsVo ganttStatisticsVo = schedulingShiftService.getGanttStatisticsVo(schedulingShiftVoList);
        return R.ok()
                .addData("schedulingShiftVoList", schedulingShiftVoList)
                .addData("ganttStatisticsVo", ganttStatisticsVo);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        SchedulingShiftEntity schedulingShift = schedulingShiftService.getById(id);

        return R.ok().addData("schedulingShift", schedulingShift);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SchedulingShiftEntity schedulingShift) {
        schedulingShiftService.save(schedulingShift);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SchedulingShiftEntity schedulingShift) {
        schedulingShiftService.updateById(schedulingShift);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        schedulingShiftService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 获取周视图数据
     *
     * @param params startDate：一周的开始日期 endDate：一周的结束日期
     * @return
     */
    @PostMapping("/getWeekViewData")
    public R getWeekViewData(@RequestBody Map<String, String> params, HttpServletRequest httpServletRequest) {
        long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = sdf.parse(params.get("startDate"));
            Date endDate = sdf.parse(params.get("endDate"));
            Integer realStartIndex = null;
            Integer realEndIndex = null;
            if (params.containsKey("realStartIndex")) {
                realStartIndex = Integer.parseInt(params.get("realStartIndex"));
            }
            if (params.containsKey("realEndIndex")) {
                realEndIndex = Integer.parseInt(params.get("realEndIndex"));
            }
            Map<Long, List<SchedulingShiftEntity>> userIdAndShiftList = new HashMap<>();
            WeekViewVo weekViewVo = schedulingShiftService.listWeekViewShiftVoBetweenStartDateAndEndDate(
                    startDate,
                    endDate,
                    realStartIndex,
                    realEndIndex,
                    storeId,
                    userIdAndShiftList);

            return R.ok().addData("shiftListOfEachDay", weekViewVo.getShiftListOfEachDay())
                    .addData("indexAndDateMap", weekViewVo.getIndexAndDateMap() != null && weekViewVo.getIndexAndDateMap().size() > 0 ? weekViewVo.getIndexAndDateMap() : null)
                    .addData("userIdAndShiftList", userIdAndShiftList);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取用户指定日期的班次
     *
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/getTodayShiftListOfUser")
    public R getTodayShiftListOfUser(HttpServletRequest httpServletRequest) throws ParseException {
        String token = httpServletRequest.getHeader("token");
        Long userId = Long.parseLong(JwtUtil.getUserId(token));
        Date date = new Date();
        DateUtil.DateEntity dateEntity = DateUtil.parseDate(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = sdf.parse(dateEntity.getYear() + "-" + dateEntity.getMonth() + "-" + dateEntity.getDay());
        //查询某天某人的所有班次
        List<SchedulingShiftEntity> shiftList = schedulingShiftService.getOneDayShiftListOfUser(parse, userId);
        return R.ok().addData("shiftList", shiftList);
    }

    /**
     * 获取用户从起始日期到截止日期中，每天的班次信息
     *
     * @param startDateStr       格式 yyyy-MM-dd
     * @param endDateStr
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/getWeekShiftListOfUser")
    public R getWeekShiftListOfUser(@RequestParam("startDateStr") String startDateStr, @RequestParam("endDateStr") String endDateStr, HttpServletRequest httpServletRequest) throws SSSException {
        ////日期解析
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdf.parse(startDateStr);
            endDate = sdf.parse(endDateStr);
        } catch (ParseException e) {
            throw new SSSException(ResultCodeEnum.FAIL.getCode(), e.getMessage());
        }

        ////查询每一天的班次数据
        String token = httpServletRequest.getHeader("token");
        Long userId = Long.parseLong(JwtUtil.getUserId(token));
        //获取起止日期内，每天的班次集合
        List<List<SchedulingShiftEntity>> shiftListList = schedulingShiftService.getWeekShiftListOfUser(startDate, endDate, userId);

        ////类课程表数据封装
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
        List<WeeklyViewDayVo> classTime = new ArrayList<>();
        //该周的最早上班时间（需要的是整时，即8:35也变成8:30）
        String earliestStartTime = "";
        Integer earliestStartTimeMinute = Integer.MAX_VALUE;
        String latestEndTime = "";
        Integer latestEndTimeMinute = 0;
        for (int i = 0; i < shiftListList.size(); i++) {
            WeeklyViewDayVo weeklyViewDayVo = new WeeklyViewDayVo();
            weeklyViewDayVo.setDayOfWeek(i + 1);
            List<SchedulingShiftEntity> shiftList = shiftListList.get(i);
            List<WeeklyTaskVo> taskVoList = new ArrayList<>();
            Long lastTaskEndMinute = -1L;
            if (shiftList.size() > 0) {
                //找出最早开始时间
                SchedulingShiftEntity earlyShift = shiftList.get(0);
                String[] formatTimeArr = sdf1.format(earlyShift.getStartDate()).split(":");
                int hour = Integer.parseInt(formatTimeArr[0]);
                int minute = Integer.parseInt(formatTimeArr[1]);
                int curMinute = hour * 60;
                if (curMinute < earliestStartTimeMinute) {
                    earliestStartTimeMinute = curMinute;
                    earliestStartTime = sdf1.format(earlyShift.getStartDate()).split(":")[0] + ":00";
                }
                //找出最晚开始时间
                SchedulingShiftEntity latestShift = shiftList.get(shiftList.size() - 1);
                String[] formatTimeArr1 = sdf1.format(latestShift.getEndDate()).split(":");
                int hour1 = Integer.parseInt(formatTimeArr1[0]);
                int minute1 = Integer.parseInt(formatTimeArr1[1]);
                int hourPlus1 = hour1 + 1;
                int curMinute2 = minute1 > 0 ? hourPlus1* 60 : hour1 * 60;
                if (curMinute2 > latestEndTimeMinute) {
                    latestEndTimeMinute = curMinute2;
                    if (minute1 > 0) {
                        latestEndTime = (hourPlus1 + "") + ":00";
                    } else {
                        latestEndTime = (hour1 + "") + ":00";
                    }

                }

                for (int j = 0; j < shiftList.size(); j++) {
                    SchedulingShiftEntity shift = shiftList.get(j);
                    Date shiftStartDate = shift.getStartDate();
                    long startMinute = shiftStartDate.getTime() / (1000 * 60);
                    Date shiftEndDate = shift.getEndDate();
                    long endMinute = shiftEndDate.getTime() / (1000 * 60);
                    WeeklyTaskVo weeklyTaskVo = new WeeklyTaskVo();
                    weeklyTaskVo.setTimeStart(sdf1.format(shiftStartDate));
                    weeklyTaskVo.setTimeEnd(sdf1.format(shiftEndDate));
                    weeklyTaskVo.setTimeStartToLast(lastTaskEndMinute == -1 ? 0 : startMinute - lastTaskEndMinute);
                    weeklyTaskVo.setTimeDifference(endMinute - startMinute);
                    lastTaskEndMinute = endMinute;
                    taskVoList.add(weeklyTaskVo);
                }
            }
            weeklyViewDayVo.setTask(taskVoList);
            classTime.add(weeklyViewDayVo);
        }
        ///修改每天的第一个班次的timeStartToLast
        for (WeeklyViewDayVo weeklyViewDayVo : classTime) {
            if (weeklyViewDayVo.getTask().size() == 0) {
                continue;
            }
            WeeklyTaskVo firstWeeklyTaskVo = weeklyViewDayVo.getTask().get(0);
            String timeStart = firstWeeklyTaskVo.getTimeStart();
            String[] formatTimeArr = timeStart.split(":");
            int hour = Integer.parseInt(formatTimeArr[0]);
            int minute = Integer.parseInt(formatTimeArr[1]);
            long curMinute = hour * 60 + minute;
            firstWeeklyTaskVo.setTimeStartToLast(curMinute - earliestStartTimeMinute);
        }

        return R.ok().addData("classTime", classTime)
                .addData("earliestStartTime", earliestStartTime)
                .addData("latestEndTime", latestEndTime)
                .addData("earliestAndLatestOffset", latestEndTimeMinute - earliestStartTimeMinute);
    }


}
