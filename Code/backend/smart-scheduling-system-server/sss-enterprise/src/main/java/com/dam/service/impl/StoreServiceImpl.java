package com.dam.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;

import com.dam.dao.StoreDao;
import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.service.StoreService;


@Service("storeService")
public class StoreServiceImpl extends ServiceImpl<StoreDao, StoreEntity> implements StoreService {

    @Override
    public PageUtils queryPage(Map<String, Object> params, QueryWrapper<StoreEntity> wrapper) {
        IPage<StoreEntity> page = this.page(
                new Query<StoreEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}