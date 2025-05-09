package com.dam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dam.model.entity.enterprise.UserPositionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * user_position中间表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 15:17:00
 */
@Mapper
public interface UserPositionDao extends BaseMapper<UserPositionEntity> {
	
}
