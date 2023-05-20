package com.dam.dao;

import com.dam.model.entity.system.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-06 15:50:29
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

    List<UserEntity> getUserListWithoutPosition(@Param("storeId") long storeId);

    List<String> listAllMailByUserIdList(@Param("userIdList") List<Long> userIdList);
}
