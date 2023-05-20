package com.dam.service.impl;

import com.dam.model.entity.system.OperationLogEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.system.RoleCodeEnum;
import com.dam.model.enums.system.UserCodeEnum;
import com.dam.service.UserService;
import com.dam.utils.JwtUtil;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dam.dao.OperationLogDao;
import com.dam.service.OperationLogService;


@Service("operationLogService")

class OperationLogServiceImpl extends ServiceImpl<OperationLogDao, OperationLogEntity> implements OperationLogService {
    @Autowired
    private OperationLogDao OperationLogDao;
    @Autowired
    private UserService userService;

    @Override
    public PageUtils queryPage(Map<String, Object> params, String token) {
        Long userId = Long.parseLong(JwtUtil.getUserId(token));
        UserEntity user = userService.getById(userId);
        String enterpriseId = JwtUtil.getEnterpriseId(token);
        String storeId = JwtUtil.getStoreId(token);
        QueryWrapper<OperationLogEntity> queryWrapper = new QueryWrapper<>();

        if (user.getType() == UserCodeEnum.TYPE_SYSTEM_MANAGER.getCode().intValue()) {
            //--if--系统管理员，可以查询所有日志
        } else if (user.getType() == UserCodeEnum.TYPE_ENTERPRISE_MANAGER.getCode().intValue()) {
            //--if--企业管理员，只能查询企业的日志
            queryWrapper.eq("enterprise_id", enterpriseId);
        } else if (user.getType() == UserCodeEnum.TYPE_STORE_MANAGER.getCode().intValue()) {
            //--if--门店管理员，只能查询门店的日志
            queryWrapper.eq("store_id", storeId);
        }else {
            //--if--普通用户，什么都查不出来
            queryWrapper.eq("id", -1);
        }

        queryWrapper.orderByDesc("create_time");
        IPage<OperationLogEntity> page = this.page(
                new Query<OperationLogEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }
    @Override
    public OperationLogEntity getById(Long id) {
        return OperationLogDao.selectById(id);
    }

}