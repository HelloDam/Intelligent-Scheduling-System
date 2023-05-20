package com.dam.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.dam.constant.AlgoEnumConstant;
import com.dam.exception.SSSException;
import com.dam.feign.EnterpriseFeignService;
import com.dam.feign.SystemFeignService;
import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.model.entity.shiftScheduling.SchedulingTaskEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.DateVo;
import com.dam.model.vo.scheduling_calculate_service.PassengerFlowVo;
import com.dam.model.vo.shiftScheduling.TaskCreateTimeTreeItemVo;
import com.dam.service.ShiftSchedulingAlgorithmService;
import com.dam.service.ShiftUserService;
import com.dam.utils.JsonUtil;
import com.dam.utils.PageUtils;
import com.dam.utils.PoiExcelUtil;
import com.dam.utils.Query;
import com.dam.utils.date.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.dam.dao.SchedulingTaskDao;
import com.dam.service.SchedulingTaskService;
import org.springframework.transaction.annotation.Transactional;


@Service("schedulingTaskService")
public class SchedulingTaskServiceImpl extends ServiceImpl<SchedulingTaskDao, SchedulingTaskEntity> implements SchedulingTaskService {
    @Autowired
    private ShiftSchedulingAlgorithmService shiftSchedulingAlgorithmService;
    @Autowired
    private ShiftUserService shiftUserService;
    @Autowired
    private SystemFeignService systemFeignService;
    @Autowired
    private EnterpriseFeignService enterpriseFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params, QueryWrapper<SchedulingTaskEntity> wrapper) {
        IPage<SchedulingTaskEntity> page = this.page(
                new Query<SchedulingTaskEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils listVirtualTask(Map<String, Object> params, Long taskId) {
        QueryWrapper<SchedulingTaskEntity> wrapper = new QueryWrapper<SchedulingTaskEntity>()
                .select(
                        "id",
                        "total_minute",
                        "total_assigned_minute",
                        "calculate_time",
                        "status",
                        "allocation_ratio",
                        "step_one_alg",
                        "step_two_alg")
                .eq("is_deleted", 0)
                .eq("parent_id", taskId)
                .eq("status", 2).or().eq("status", 3);

        int orderType = Integer.parseInt(params.get("orderType").toString());
        switch (orderType) {
            case 0:
                //按照总班次时长升序排序
                wrapper.orderByAsc("total_minute");
                break;
            case 1:
                //按照分配率降序排序
                wrapper.orderByDesc("allocation_ratio");
                break;
            case 2:
                //按照计算时间升序排序
                wrapper.orderByAsc("calculate_time");
                break;
        }

        IPage<SchedulingTaskEntity> page = this.page(
                new Query<SchedulingTaskEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<TaskCreateTimeTreeItemVo> listAllDate(long storeId) {
        List<TaskCreateTimeTreeItemVo> createTimeTreeItemVoList = new ArrayList<>();
        List<DateVo> dateVoList = baseMapper.listDataVo(storeId);
        long id = 0;

        //将所有数据拿出来，并存储到map中
        HashMap<Integer, List<Integer>> yearAndMothListMap = new HashMap<>();
        for (DateVo dateVo : dateVoList) {
            if (yearAndMothListMap.containsKey(dateVo.getYear())) {
                if (!yearAndMothListMap.get(dateVo.getYear()).contains(dateVo.getMonth())) {
                    yearAndMothListMap.get(dateVo.getYear()).add(dateVo.getMonth());
                }
            } else {
                List<Integer> monthList = new ArrayList<>();
                monthList.add(dateVo.getMonth());
                yearAndMothListMap.put(dateVo.getYear(), monthList);
            }
        }

        for (Map.Entry<Integer, List<Integer>> entry : yearAndMothListMap.entrySet()) {
            Integer year = entry.getKey();
            TaskCreateTimeTreeItemVo yearItem = new TaskCreateTimeTreeItemVo(id++, year + " 年");

            List<Integer> monthList = entry.getValue();
            List<TaskCreateTimeTreeItemVo> children = new ArrayList<>();
            for (Integer month : monthList) {
                children.add(new TaskCreateTimeTreeItemVo(id++, month + " 月"));
            }
            yearItem.setChildren(children);
            createTimeTreeItemVoList.add(yearItem);
        }

        return createTimeTreeItemVoList;
    }

    /**
     * 从Excel工作簿中读取客流量信息
     *
     * @param workbook
     * @return
     */
    @Override
    public List<PassengerFlowVo> readPassengerFlowFromWorkbook(Workbook workbook) {
        List<PassengerFlowVo> passengerFlowVoList = new ArrayList<>();

        Sheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 1; i < lastRowNum; i++) {
            Row row = sheet.getRow(i);
            PassengerFlowVo passengerFlowVo = new PassengerFlowVo();
//            System.out.println("PoiExcelUtil.getCellValue(row.getCell(1)):"+PoiExcelUtil.getCellValue(row.getCell(1)));
            passengerFlowVo.setDate(PoiExcelUtil.getCellValue(row.getCell(1)));
            passengerFlowVo.setStartTime(PoiExcelUtil.getCellValue(row.getCell(2)));
            passengerFlowVo.setEndTime(PoiExcelUtil.getCellValue(row.getCell(3)));
            passengerFlowVo.setPassengerFlow(row.getCell(4).getNumericCellValue());
            passengerFlowVoList.add(passengerFlowVo);
        }

        return passengerFlowVoList;
    }

    /**
     * 保存客流量数据
     *
     * @param taskId
     * @param passengerFlowVoList
     * @return
     */
    @Override
    public List<String> savePassengerFlowVoList(Long taskId, List<PassengerFlowVo> passengerFlowVoList) throws SSSException {
        List<String> unIncludeDateList = new ArrayList<>();
        SchedulingTaskEntity schedulingTaskEntity = baseMapper.selectById(taskId);
        String datevolistStr = schedulingTaskEntity.getDatevolist();
        List<com.dam.model.vo.scheduling_calculate_service.DateVo> dateVoList = JSON.parseObject(datevolistStr, new TypeReference<List<com.dam.model.vo.scheduling_calculate_service.DateVo>>() {
        });
        Map<String, List<PassengerFlowVo>> dateKeyAndPassengerFlowVoListMap = new HashMap<>();
        for (PassengerFlowVo passengerFlowVo : passengerFlowVoList) {
            if (!dateKeyAndPassengerFlowVoListMap.containsKey(passengerFlowVo.getDate())) {
                List<PassengerFlowVo> passengerFlowVos = new ArrayList<>();
                passengerFlowVos.add(passengerFlowVo);
                dateKeyAndPassengerFlowVoListMap.put(passengerFlowVo.getDate(), passengerFlowVos);
            } else {
                dateKeyAndPassengerFlowVoListMap.get(passengerFlowVo.getDate()).add(passengerFlowVo);
            }
        }
        if (dateVoList == null) {
            throw new SSSException(ResultCodeEnum.FAIL.getCode(), "请先设置工作日再导入客流量数据");
        }
        for (com.dam.model.vo.scheduling_calculate_service.DateVo dateVo : dateVoList) {
            if (dateKeyAndPassengerFlowVoListMap.containsKey(dateVo.getDate())) {
                dateVo.setPassengerFlowVoList(dateKeyAndPassengerFlowVoListMap.get(dateVo.getDate()));
                dateKeyAndPassengerFlowVoListMap.remove(dateVo.getDate());
            }
        }
        //存储不在工作起始日期之内的客流量日期
        for (String dateKey : dateKeyAndPassengerFlowVoListMap.keySet()) {
            unIncludeDateList.add(dateKey);
        }
        //保存新数据
        String jsonStr = JSON.toJSONString(dateVoList);
        schedulingTaskEntity.setDatevolist(JsonUtil.frontEndAndBackEntJsonEdit(jsonStr));
        //删除任务的所有计算结果
        deleteAllResultOfTask(taskId);

        baseMapper.updateById(schedulingTaskEntity);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Collections.sort(unIncludeDateList,((o1, o2) -> {
            try {
                Date o1Date = sdf.parse(o1);
                Date o2Date = sdf.parse(o2);
                return Long.compare(o1Date.getTime(),o2Date.getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }));

        return unIncludeDateList;
    }

    /**
     * 删除任务的所有结果
     *
     * @param taskId
     */
    public void deleteAllResultOfTask(Long taskId) {
        //修改任务为未计算状态
        SchedulingTaskEntity taskEntity = baseMapper.selectById(taskId);
        taskEntity.setStatus(0);
        baseMapper.updateById(taskEntity);
        //删除任务的计算结果
        shiftSchedulingAlgorithmService.deleteRelevantDataOfTask(taskId);
        //删除虚拟任务的所有计算结果
        this.deleteAllVirtualTask(taskId);
    }

    @Override
    public Long getTotalTaskByEnterpriseId(Long enterpriseId, Date firstDate, Date endDate) throws SSSException {
        R r = enterpriseFeignService.listAllStoreByAppointEnterpriseId(enterpriseId);
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            List<StoreEntity> storeEntityList = r.getData("list", new TypeReference<List<StoreEntity>>() {
            });
            List<Long> storeIdList = storeEntityList.stream().map(StoreEntity::getId).collect(Collectors.toList());
            QueryWrapper<SchedulingTaskEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("store_id", storeIdList).eq("type", 0).eq("is_deleted", 0).ge("create_time", firstDate).le("create_time", endDate);
            long num = (long) baseMapper.selectCount(queryWrapper);
            return num;
        } else {
            throw new SSSException(ResultCodeEnum.Feign_ERROR);
        }
    }

    @Override
    public Long getTotalTaskByStoreId(Long storeId, Date firstDate, Date endDate) {
        List<Long> storeIdList = Arrays.asList(new Long[]{storeId});
        QueryWrapper<SchedulingTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("store_id", storeIdList).eq("type", 0).eq("is_deleted", 0).ge("create_time", firstDate).le("create_time", endDate);
        long num = (long) baseMapper.selectCount(queryWrapper);
        return num;
    }

    @Override
    public Double getTotalPassengerFlowByEnterpriseId(Long enterpriseId, Date firstDateOfYear, Date endDateOfYear) throws SSSException {
        R r = enterpriseFeignService.listAllStoreByAppointEnterpriseId(enterpriseId);
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            List<StoreEntity> storeEntityList = r.getData("list", new TypeReference<List<StoreEntity>>() {
            });
            double totalPassengerFlow = 0;
            for (StoreEntity storeEntity : storeEntityList) {
                List<SchedulingTaskEntity> taskList = this.getRealTaskListBetweenDateFrame(firstDateOfYear, endDateOfYear, storeEntity.getId());
                for (SchedulingTaskEntity task : taskList) {
                    List<com.dam.model.vo.scheduling_calculate_service.DateVo> dateVoList = JSON.parseObject(task.getDatevolist(), new TypeReference<List<com.dam.model.vo.scheduling_calculate_service.DateVo>>() {
                    });
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");
                    if (dateVoList == null) {
                        continue;
                    }
                    for (com.dam.model.vo.scheduling_calculate_service.DateVo dateVo : dateVoList) {
                        try {
                            Date date = sdf.parse(dateVo.getDate());
                            if (date.getTime() > endDateOfYear.getTime() || date.getTime() < firstDateOfYear.getTime()) {
                                //该天不在日期范围内
                                continue;
                            }
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        for (PassengerFlowVo passengerFlowVo : dateVo.getPassengerFlowVoList()) {
                            totalPassengerFlow += passengerFlowVo.getPassengerFlow();
                        }
                    }
                }
            }
            return totalPassengerFlow;
        } else {
            throw new SSSException(ResultCodeEnum.Feign_ERROR);
        }
    }

    @Override
    public Double getTotalPassengerFlowByStoreId(Long storeId, Date firstDateOfYear, Date endDateOfYear) {
        double totalPassengerFlow = 0;

        List<SchedulingTaskEntity> taskList = this.getRealTaskListBetweenDateFrame(firstDateOfYear, endDateOfYear, storeId);
        for (SchedulingTaskEntity task : taskList) {
            List<com.dam.model.vo.scheduling_calculate_service.DateVo> dateVoList = JSON.parseObject(task.getDatevolist(), new TypeReference<List<com.dam.model.vo.scheduling_calculate_service.DateVo>>() {
            });
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");
            if (dateVoList == null) {
                continue;
            }
            for (com.dam.model.vo.scheduling_calculate_service.DateVo dateVo : dateVoList) {
                try {
                    Date date = sdf.parse(dateVo.getDate());
                    if (date.getTime() > endDateOfYear.getTime() || date.getTime() < firstDateOfYear.getTime()) {
                        //该天不在日期范围内
                        continue;
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                for (PassengerFlowVo passengerFlowVo : dateVo.getPassengerFlowVoList()) {
                    totalPassengerFlow += passengerFlowVo.getPassengerFlow();
                }
            }
        }
        return totalPassengerFlow;
    }

    /**
     * 获取门店一段时间内真实任务
     *
     * @param startDate
     * @param endDate
     * @param storeId
     * @return
     */
    @Override
    public List<SchedulingTaskEntity> getRealTaskListBetweenDateFrame(Date startDate, Date endDate, Long storeId) {
        QueryWrapper<SchedulingTaskEntity> taskQueryWrapper = new QueryWrapper<SchedulingTaskEntity>().eq("store_id", storeId).eq("is_deleted", 0).eq("type", 0);
        //查询出和当前月份时间段有相交的任务
        taskQueryWrapper.ge("end_date", startDate).le("start_date", endDate);
        List<SchedulingTaskEntity> taskList = baseMapper.selectList(taskQueryWrapper);
        return taskList;
    }

    @Transactional
    @Override
    public void removeByIdArr(List<Long> taskIdList) {
        for (Long taskId : taskIdList) {
            //删除任务所绑定的相关数据
            shiftSchedulingAlgorithmService.deleteRelevantDataOfTask(taskId);
        }
        //删除任务
        baseMapper.deleteBatchIds(taskIdList);
    }

    @Override
    public void deleteAllVirtualTask(Long taskId) {
        //查询出所有虚拟任务
        List<SchedulingTaskEntity> taskEntityList = baseMapper.selectList(new QueryWrapper<SchedulingTaskEntity>().eq("parent_id", taskId));
        List<Long> taskIdList = taskEntityList.stream().map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        if (taskIdList.size() > 0) {
            //删除每个虚拟任务的所有相关数据
            shiftSchedulingAlgorithmService.deleteRelevantDataOfTaskList(taskIdList);
            //删除所有虚拟任务
            baseMapper.deleteBatchIds(taskIdList);
        }

    }

    @Override
    public int countVirtualTask(String stepOneAlg, String stepTwoAlg, Long taskId) {
        return baseMapper.selectCount(new QueryWrapper<SchedulingTaskEntity>()
                .eq("step_one_alg", stepOneAlg)
                .eq("step_two_alg", stepTwoAlg)
                .eq("parent_id", taskId));
    }

    /**
     * 查询已经计算的算法组合
     *
     * @param taskId
     * @return
     */
    @Override
    public List<String> listHaveCalculateAlgoGroup(Long taskId) {
        List<SchedulingTaskEntity> schedulingTaskEntityList = baseMapper.selectList(new QueryWrapper<SchedulingTaskEntity>().eq("parent_id", taskId).eq("is_deleted", 0));
        return schedulingTaskEntityList.stream().map(item -> {
            JSONArray arr = JSON.parseArray(item.getStepTwoAlg());
            return item.getStepOneAlg() + AlgoEnumConstant.splitStr + arr.get(1);
        }).collect(Collectors.toList());
    }

    @Override
    public Date getMaxEndDate(long storeId) {
        SchedulingTaskEntity schedulingTask = baseMapper.selectMaxEndDate(storeId);
        if (schedulingTask != null) {
            String beijingDateStr = DateUtil.convertUTCToBeijing(schedulingTask.getEndDate());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                return sdf.parse(beijingDateStr);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            //--if--如果数据库查询出来的日期为空，那就直接创建一个早期的时间

            // 创建一个SimpleDateFormat对象，用于解析日期字符串
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // 使用SimpleDateFormat对象解析"1970-01-01"日期字符串，并设置时间为0:00:00
            Date date1970 = null;

            try {
                date1970 = sdf.parse("1970-01-01");
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return new Date();
        }

    }

    @Override
    @Transactional
    public void updateTaskPublishStatus(Long taskId, Integer isPublish) {
        ////修改任务状态
        SchedulingTaskEntity taskEntity = new SchedulingTaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setIsPublish(isPublish);
        baseMapper.updateById(taskEntity);
        ////发布信息通知相关员工
        ///找出相关任务中被安排了班次的相关员工id
        List<Long> userIdList = shiftUserService.listRelevantUserId(taskId);
        ///编辑消息
        StringBuilder messageStrBuilder = new StringBuilder();
        SchedulingTaskEntity task = baseMapper.selectById(taskId);
        ///发送消息通知员工
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("userIdList", userIdList);
        if (isPublish == 1) {
            String startDate = DateUtil.convertUTCToBeijing(task.getStartDate());
            String endDate = DateUtil.convertUTCToBeijing(task.getEndDate());
            String htmlContent = "<html>"
                    + "<head>"
                    + "<style>"
                    + "body {font-family: Arial, sans-serif; font-size: 16px; color: #333333; background-color: #FFFFFF; line-height: 1.5;}"
                    + "h1 {font-size: 24px; color: #007FFF; margin-bottom: 10px;}"
                    + "p {margin-bottom: 10px;}"
                    + ".date {font-weight: bold; color: #007FFF;}"
                    + ".wrapper {max-width: 600px; margin: 0 auto;}"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"wrapper\">"
                    + "<h2>排班计划发布</h2>"
                    + "<p>尊敬的员工，</p>"
                    + "<p>&emsp;&emsp;管理员刚刚发布了新的排班计划，排班时间范围：<span class=\"date\">" + startDate
                    + "</span>至<span class=\"date\">" + endDate
                    + "</span>，请及时登录系统查看排班日历，明确自己的工作时间。</p>"
                    + "<p>谢谢。</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";
            messageStrBuilder.append(htmlContent);
            paramMap.put("subject", "智能排班系统——排班任务发布通知");
            paramMap.put("message", messageStrBuilder.toString());
            paramMap.put("type", 1);
        } else {
            String startDate = DateUtil.convertUTCToBeijing(task.getStartDate());
            String endDate = DateUtil.convertUTCToBeijing(task.getEndDate());

            String htmlContent = "<html>"
                    + "<head>"
                    + "<style>"
                    + "body {font-family: Arial, sans-serif; font-size: 16px; color: #333333; background-color: #FFFFFF; line-height: 1.5;}"
                    + "h1 {font-size: 24px; color: #007FFF; margin-bottom: 10px;}"
                    + "p {margin-bottom: 10px;}"
                    + ".date {font-weight: bold; color: #007FFF;}"
                    + ".wrapper {max-width: 600px; margin: 0 auto;}"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"wrapper\">"
                    + "<h2>排班计划撤回</h2>"
                    + "<p>尊敬的员工，</p>"
                    + "<p>&emsp;&emsp;管理员刚刚撤回了部分排班计划，排班时间范围：<span class=\"date\">" + startDate
                    + "</span>至<span class=\"date\">" + endDate
                    + "</span>，可登录系统查看具体的排班日历。</p>"
                    + "<p>谢谢。</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";
            messageStrBuilder.append(htmlContent);
            paramMap.put("subject", "智能排班系统——排班任务撤回通知");
            paramMap.put("message", messageStrBuilder.toString());
            paramMap.put("type", 1);

        }
        enterpriseFeignService.sendMesToUserList(paramMap);

    }


}