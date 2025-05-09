package com.dam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.model.entity.enterprise.FestivalEntity;
import com.dam.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 门店节日表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-13 16:42:08
 */
public interface FestivalService extends IService<FestivalEntity> {

    PageUtils queryPage(Map<String, Object> params, QueryWrapper<FestivalEntity> wrapper);

    void addStatutoryHolidays(long storeId);

    List<FestivalEntity> correctionDate(FestivalEntity festivalEntity,int lead,int recedingAmount);
}

