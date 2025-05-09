package com.dam.dao;

import com.dam.model.entity.enterprise.ProvinceCityRegionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 省-市-区表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
@Mapper
public interface ProvinceCityRegionDao extends BaseMapper<ProvinceCityRegionEntity> {
	
}
