package com.dam.dao;

import com.dam.model.entity.system.UserRoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * user_role中间表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-06 15:50:30
 */
@Mapper
public interface UserRoleDao extends BaseMapper<UserRoleEntity> {
	
}
