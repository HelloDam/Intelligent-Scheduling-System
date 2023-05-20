package com.dam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.model.entity.system.LoginLogEntity;
import com.dam.utils.PageUtils;

import java.util.Map;

public interface LoginLogService extends IService<LoginLogEntity> {
    PageUtils queryPage(Map<String, Object> params, String token);
}
