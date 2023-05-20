package com.dam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dam.model.entity.enterprise.MessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知表
 * 
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-20 15:43:46
 */
@Mapper
public interface MessageDao extends BaseMapper<MessageEntity> {
	
}
