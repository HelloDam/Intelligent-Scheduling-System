package com.dam.service;


import com.dam.model.entity.system.LoginLogEntity;
import com.dam.model.vo.system.LoginLogQueryVo;
import com.dam.utils.PageUtils;

import java.util.Map;

public interface RecordLoginLogService {

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param ipaddr   ip
     * @param message  消息内容
     * @return
     */
    void recordLoginLog(String username, Integer status, String ipaddr, String message,Long enterpriseId,Long storeId);

    com.baomidou.mybatisplus.core.metadata.IPage<LoginLogEntity> selectPage(com.baomidou.mybatisplus.extension.plugins.pagination.Page<LoginLogEntity> pageParam, LoginLogQueryVo sysLoginLogQueryVo);

    LoginLogEntity getById(Long id);

//    PageUtils queryPage(Map<String, Object> params);
}
