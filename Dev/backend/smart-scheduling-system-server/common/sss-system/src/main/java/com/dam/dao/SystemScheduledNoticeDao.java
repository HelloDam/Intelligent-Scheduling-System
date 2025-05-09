package com.dam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dam.model.entity.system.SystemScheduledNoticeEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统定时通知
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-21 20:45:41
 */
@Mapper
public interface SystemScheduledNoticeDao extends BaseMapper<SystemScheduledNoticeEntity> {
	
}
