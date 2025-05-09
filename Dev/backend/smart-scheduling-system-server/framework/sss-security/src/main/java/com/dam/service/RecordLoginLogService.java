package com.dam.service;

import com.dam.model.entity.system.LoginLogEntity;

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

    LoginLogEntity getById(Long id);

}
