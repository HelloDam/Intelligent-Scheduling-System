package com.dam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.exception.SSSException;
import com.dam.utils.PageUtils;
import com.dam.model.entity.system.RoleEntity;
import com.dam.utils.vo.AssginRoleVo;

import java.util.Map;

/**
 * 角色表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-06 15:50:30
 */
public interface RoleService extends IService<RoleEntity> {

    PageUtils queryPage(Map<String, Object> params, QueryWrapper<RoleEntity> wrapper);

//    IPage<RoleEntity> selectPage(Page<RoleEntity> pageParam, SysRoleQueryVo roleQueryVo);

    Map<String, Object> getRolesByUserId(Long userId, Long toUserId);

    void doAssign(AssginRoleVo assginRoleVo);


    void saveRole(RoleEntity role, Long userId, String enterpriseId, String storeId) throws SSSException;
}

