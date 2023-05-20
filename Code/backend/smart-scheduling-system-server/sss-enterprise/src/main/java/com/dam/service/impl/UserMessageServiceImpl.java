package com.dam.service.impl;

import com.dam.dao.UserMessageDao;
import com.dam.model.entity.enterprise.UserMessageEntity;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.dam.service.UserMessageService;


@Service("userMessageService")
public class UserMessageServiceImpl extends ServiceImpl<UserMessageDao, UserMessageEntity> implements UserMessageService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserMessageEntity> page = this.page(
                new Query<UserMessageEntity>().getPage(params),
                new QueryWrapper<UserMessageEntity>()
        );

        return new PageUtils(page);
    }



}