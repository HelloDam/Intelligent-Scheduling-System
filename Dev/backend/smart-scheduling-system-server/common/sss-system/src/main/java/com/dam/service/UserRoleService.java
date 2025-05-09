package com.dam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.utils.PageUtils;
import com.dam.model.entity.system.UserRoleEntity;

import java.util.Map;

/**
 * user_role中间表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-06 15:50:30
 */
public interface UserRoleService extends IService<UserRoleEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

