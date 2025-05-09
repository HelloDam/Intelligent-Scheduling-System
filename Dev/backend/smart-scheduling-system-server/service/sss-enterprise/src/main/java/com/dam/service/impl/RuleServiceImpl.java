package com.dam.service.impl;

import com.dam.utils.PageUtils;
import com.dam.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dam.dao.RuleDao;
import com.dam.model.entity.enterprise.RuleEntity;
import com.dam.service.RuleService;


@Service("ruleService")
public class RuleServiceImpl extends ServiceImpl<RuleDao, RuleEntity> implements RuleService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<RuleEntity> page = this.page(
                new Query<RuleEntity>().getPage(params),
                new QueryWrapper<RuleEntity>()
        );

        return new PageUtils(page);
    }

}