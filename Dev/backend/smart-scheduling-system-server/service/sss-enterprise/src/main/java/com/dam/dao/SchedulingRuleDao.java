package com.dam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dam.model.entity.enterprise.SchedulingRuleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门店规则中间表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-23 21:34:22
 */
@Mapper
public interface SchedulingRuleDao extends BaseMapper<SchedulingRuleEntity> {
	
}
