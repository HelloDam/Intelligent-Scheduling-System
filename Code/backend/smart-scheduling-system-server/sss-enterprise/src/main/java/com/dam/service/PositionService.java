package com.dam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.model.vo.enterprise.PositionVo;
import com.dam.utils.PageUtils;
import com.dam.model.entity.enterprise.PositionEntity;

import java.util.List;
import java.util.Map;

/**
 * 职位表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
public interface PositionService extends IService<PositionEntity> {

    PageUtils queryPage(Map<String, Object> params, QueryWrapper<PositionEntity> wrapper);

    List<PositionEntity> findNodes(Long storeId,String name,String detail );

    List<PositionVo> buildTree(long storeId);

    List<PositionEntity> listPositionListByPositionIdList(List<Long> positionIdList);

    List<PositionEntity> listAllSonPosition(Long storeId);

    void copyPositionToOtherStore(Long storeId);
}

