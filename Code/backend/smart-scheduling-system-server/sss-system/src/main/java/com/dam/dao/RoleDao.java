package com.dam.dao;

import com.dam.model.entity.system.RoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-06 15:50:30
 */
@Mapper
public interface RoleDao extends BaseMapper<RoleEntity> {
//    IPage<RoleEntity> selectPage(@Param("pageParam") Page<RoleEntity> pageParam, @Param("roleQueryVo") SysRoleQueryVo roleQueryVo);
}
