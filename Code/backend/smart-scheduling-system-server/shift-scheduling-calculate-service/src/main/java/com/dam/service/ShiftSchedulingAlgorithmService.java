package com.dam.service;

import com.dam.dto.AlgoGroupDto;
import com.dam.exception.SSSException;
import com.dam.model.dto.scheduling_calculate.Instance;
import com.dam.model.dto.scheduling_calculate.Solution;
import com.dam.model.vo.scheduling_calculate_service.SchedulingCalculateVo;
import com.wskh.enums.AlgoEnum;

import java.util.List;
import java.util.Map;

public interface ShiftSchedulingAlgorithmService {
    void caculate(SchedulingCalculateVo schedulingCalculateVo, Instance instance, Long storeId, String token, Boolean isSendMessage) throws SSSException;

    /**
     * 删除任务相关数据，班次、工作日、useshift
     *
     * @param taskId
     */
    public void deleteRelevantDataOfTask(Long taskId);

    /**
     * 删除任务集合的所有相关数据，班次、工作日、user_shift
     *
     * @param taskIdList
     */
    public void deleteRelevantDataOfTaskList(List<Long> taskIdList);

    void multiAlgorithmSolve(List<Instance> instanceList, List<SchedulingCalculateVo> schedulingCalculateVoList, Long taskId, Long storeId, String token) throws SSSException;

    public Map<String, Object> getPhaseTwoParameter(SchedulingCalculateVo schedulingCalculateVo, AlgoEnum.PhaseTwo phaseTwo);

    public void saveSolutionToDatabase(Long storeId, SchedulingCalculateVo schedulingCalculateVo, Instance instance, Solution solution, long calculateTime);

    void overlayResult(Long virtualTaskId);

    /**
     * 判断vo是否有效
     *
     * @param schedulingCalculateVo
     * @return
     */
    public boolean judgeWhetherSchedulingCalculateVoEffective(SchedulingCalculateVo schedulingCalculateVo) throws SSSException;

    public Instance buildInstance(SchedulingCalculateVo schedulingCalculateVo, Long storeId) throws SSSException;

    /**
     * 准备多算法计算的数据
     *
     * @param algoGroupDtoList
     * @param taskId
     * @param storeId
     * @param token
     * @return
     */
    public void multiAlgorithmInstancePrepare(List<AlgoGroupDto> algoGroupDtoList, List<Instance> instanceList, List<SchedulingCalculateVo> schedulingCalculateVoList, Long taskId, Long storeId, String token) throws SSSException;
}
