package com.dam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dam.model.entity.shiftScheduling.SchedulingTaskEntity;
import com.dam.model.vo.DateVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 排班任务表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-01 13:58:33
 */
@Mapper
public interface SchedulingTaskDao extends BaseMapper<SchedulingTaskEntity> {
    List<DateVo> listDataVo(@Param("storeId") long storeId);

    SchedulingTaskEntity selectMaxEndDate(@Param("storeId") long storeId);
}
