package com.dam.dao;

import com.dam.model.entity.enterprise.PositionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 职位表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
@Mapper
public interface PositionDao extends BaseMapper<PositionEntity> {

    List<PositionEntity> listAllSonPosition(@Param("storeId") Long storeId);
}
