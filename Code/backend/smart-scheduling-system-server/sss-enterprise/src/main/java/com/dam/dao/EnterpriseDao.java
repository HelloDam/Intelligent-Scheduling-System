package com.dam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dam.model.entity.enterprise.EnterpriseEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-13 14:49:17
 */
@Mapper
public interface EnterpriseDao extends BaseMapper<EnterpriseEntity> {
	
}
