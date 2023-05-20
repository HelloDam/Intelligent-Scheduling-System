package com.dam.dao;

import com.dam.model.entity.system.MenuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-06 15:50:30
 */
@Mapper
public interface MenuDao extends BaseMapper<MenuEntity> {

    List<MenuEntity> findListByUserId(@Param("userId") Long userId);
    
}
