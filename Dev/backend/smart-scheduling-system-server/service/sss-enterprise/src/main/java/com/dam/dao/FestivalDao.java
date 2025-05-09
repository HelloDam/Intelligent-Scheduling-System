package com.dam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dam.model.entity.enterprise.FestivalEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门店节日表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-13 16:42:08
 */
@Mapper
public interface FestivalDao extends BaseMapper<FestivalEntity> {
	
}
