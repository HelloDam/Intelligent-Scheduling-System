package com.dam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.model.entity.system.OperationLogEntity;
import com.dam.utils.PageUtils;

import java.util.Map;

/**
 * 操作日志表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-13 16:42:08
 */
public interface OperationLogService extends IService<OperationLogEntity> {

    PageUtils queryPage(Map<String, Object> params, String token);

    OperationLogEntity getById(Long id);
}

