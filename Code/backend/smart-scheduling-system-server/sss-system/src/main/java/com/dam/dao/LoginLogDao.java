package com.dam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dam.model.entity.system.LoginLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogDao extends BaseMapper<LoginLogEntity> {
}
