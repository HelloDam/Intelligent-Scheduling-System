package com.dam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.utils.PageUtils;

import java.util.Map;

/**
 * 门店表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
public interface StoreService extends IService<StoreEntity> {

    PageUtils queryPage(Map<String, Object> params, QueryWrapper<StoreEntity> wrapper);
}

