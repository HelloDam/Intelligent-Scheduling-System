package com.dam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.utils.PageUtils;
import com.dam.model.entity.enterprise.RuleEntity;

import java.util.Map;

/**
 * 规则表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
public interface RuleService extends IService<RuleEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

