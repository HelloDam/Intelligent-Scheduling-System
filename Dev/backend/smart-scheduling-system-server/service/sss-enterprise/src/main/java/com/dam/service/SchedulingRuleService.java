package com.dam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.model.entity.enterprise.SchedulingRuleEntity;
import com.dam.model.vo.enterprise.SchedulingRuleVo;
import com.dam.utils.PageUtils;

import java.util.Map;

/**
 * 门店规则中间表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-23 21:34:22
 */
public interface SchedulingRuleService extends IService<SchedulingRuleEntity> {

    PageUtils queryPage(Map<String, Object> params);

    SchedulingRuleVo getSchedulingRuleVoByStoreId(long storeId);

    SchedulingRuleEntity getSchedulingRuleVoByStoreIdAndType(Long storeId, int type);

    SchedulingRuleVo getSchedulingRuleVoByRuleId(Long ruleId);

    void saveRule(SchedulingRuleEntity schedulingRule, Long storeId);
}

