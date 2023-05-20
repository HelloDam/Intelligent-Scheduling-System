package com.dam.service.impl;

import com.dam.exception.SSSException;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.entity.system.UserRoleEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.enums.system.RoleCodeEnum;
import com.dam.model.enums.system.UserCodeEnum;
import com.dam.model.result.R;
import com.dam.service.UserRoleService;
import com.dam.service.UserService;
import com.dam.utils.vo.AssginRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;

import com.dam.dao.RoleDao;
import com.dam.model.entity.system.RoleEntity;
import com.dam.service.RoleService;
import org.springframework.util.StringUtils;


@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleDao, RoleEntity> implements RoleService {

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserService userService;


    @Override
    public PageUtils queryPage(Map<String, Object> params, QueryWrapper<RoleEntity> wrapper) {
        IPage<RoleEntity> page = this.page(
                new Query<RoleEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

  /*  @Override
    public IPage<RoleEntity> selectPage(Page<RoleEntity> pageParam, SysRoleQueryVo roleQueryVo) {
        return baseMapper.selectPage(pageParam, roleQueryVo);
    }*/

    /**
     * 根据用户获取分配的角色
     *
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> getRolesByUserId(Long userId, Long toUserId) {
        //获取可分配的角色
        UserEntity user = userService.getById(userId);
        UserEntity toUser = userService.getById(toUserId);
        QueryWrapper<RoleEntity> queryWrapper = new QueryWrapper<>();
        if (user.getType() == UserCodeEnum.TYPE_SYSTEM_MANAGER.getCode().intValue()) {
            //--if--系统管理员
            queryWrapper.eq("type", RoleCodeEnum.TYPE_SYSTEM_ROLE.getCode());
        } else if (user.getType() == UserCodeEnum.TYPE_ENTERPRISE_MANAGER.getCode().intValue()) {
            //--if--企业管理员
            if (toUser.getType() == UserCodeEnum.TYPE_STORE_MANAGER.getCode().intValue()) {
                //--if--给门店管理员分配
                queryWrapper.eq("type", RoleCodeEnum.TYPE_ENTERPRISE_ROLE.getCode());
                queryWrapper.eq("enterprise_id", toUser.getEnterpriseId());
            } else if (toUser.getType() == UserCodeEnum.TYPE_ORDINARY_USER.getCode().intValue()) {
                //--if--给普通用户分配
                queryWrapper.eq("type", RoleCodeEnum.TYPE_STORE_ROLE.getCode());
                queryWrapper.eq("store_id", toUser.getStoreId());
            }
        } else if (user.getType() == UserCodeEnum.TYPE_STORE_MANAGER.getCode().intValue()) {
            //--if--门店管理员，给普通用户分配
            queryWrapper.eq("type", RoleCodeEnum.TYPE_STORE_ROLE.getCode());
            queryWrapper.eq("store_id", toUser.getStoreId());
        }
        List<RoleEntity> roles = baseMapper.selectList(queryWrapper);
        //获取用户已分配的角色
        List<UserRoleEntity> userRoles = userRoleService.list(new QueryWrapper<UserRoleEntity>().eq("user_id", toUserId));
        //获取用户已分配的角色id
        List<Long> userRoleIds = new ArrayList<>();
        for (UserRoleEntity userRole : userRoles) {
            userRoleIds.add(userRole.getRoleId());
        }
        //创建返回的Map
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("allRoles", roles);
        returnMap.put("userRoleIds", userRoleIds);
        return returnMap;
    }

    /**
     * 给用户分配角色
     *
     * @param assginRoleVo
     */
    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        //根据用户id删除原来分配的角色
        QueryWrapper<UserRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", assginRoleVo.getUserId());
        userRoleService.remove(queryWrapper);
        //获取所有的角色id
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        for (Long roleId : roleIdList) {
            if (roleId != null) {
                UserRoleEntity sysUserRole = new UserRoleEntity();
                sysUserRole.setUserId(assginRoleVo.getUserId());
                sysUserRole.setRoleId(roleId);
                //保存
                userRoleService.save(sysUserRole);
            }
        }
    }

    /**
     * 保存角色
     *
     * @param role
     * @param userId
     * @param enterpriseId
     * @param storeId
     * @throws SSSException
     */
    @Override
    public void saveRole(RoleEntity role, Long userId, String enterpriseId, String storeId) throws SSSException {
        UserEntity user = userService.getById(userId);
        if (user.getType() == UserCodeEnum.TYPE_SYSTEM_MANAGER.getCode().intValue()) {
            //--if--系统管理员
            role.setType(RoleCodeEnum.TYPE_SYSTEM_ROLE.getCode());
        } else if (user.getType() == UserCodeEnum.TYPE_ENTERPRISE_MANAGER.getCode().intValue()) {
            //--if--企业管理员
            role.setType(RoleCodeEnum.TYPE_ENTERPRISE_ROLE.getCode());
            if (StringUtils.isEmpty(enterpriseId)) {
                throw new SSSException(ResultCodeEnum.FAIL.getCode(), "企业id为空");
            } else {
                role.setEnterpriseId(Long.parseLong(enterpriseId));
            }
        } else if (user.getType() == UserCodeEnum.TYPE_STORE_MANAGER.getCode().intValue()) {
            //--if--门店管理员
            role.setType(RoleCodeEnum.TYPE_STORE_ROLE.getCode());
            if (StringUtils.isEmpty(storeId)) {
                throw new SSSException(ResultCodeEnum.FAIL.getCode(), "门店id为空");
            } else {
                role.setEnterpriseId(Long.parseLong(enterpriseId));
                role.setStoreId(Long.parseLong(storeId));
            }
        }
        baseMapper.insert(role);
    }

}