package com.dam.service.impl;

import com.dam.model.entity.system.RoleMenuEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.entity.system.UserRoleEntity;
import com.dam.model.enums.system.MenuCodeEnum;
import com.dam.model.enums.system.UserCodeEnum;
import com.dam.service.RoleMenuService;
import com.dam.service.UserRoleService;
import com.dam.service.UserService;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;
import com.dam.utils.MenuHelper;
import com.dam.utils.RouterHelper;
import com.dam.utils.vo.AssginMenuVo;
import com.dam.utils.vo.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.dam.dao.MenuDao;
import com.dam.model.entity.system.MenuEntity;
import com.dam.service.MenuService;
import org.springframework.util.CollectionUtils;


@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuDao, MenuEntity> implements MenuService {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private UserRoleService userRoleService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MenuEntity> page = this.page(
                new Query<MenuEntity>().getPage(params),
                new QueryWrapper<MenuEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * 查询用户的菜单，构建路由
     *
     * @param userId
     * @return
     */
    @Override
    public List<RouterVo> getUserMenuList(Long userId) {
        //超级管理员admin账号id为：1
        List<MenuEntity> sysMenuList;
        UserEntity userEntity = userService.getById(userId);
        if (userEntity.getType() == 0) {
            //超级管理员，查出所有权限
            sysMenuList = baseMapper.selectList(new QueryWrapper<MenuEntity>().eq("status", MenuCodeEnum.STATUS_NORMAL.getCode()).orderByAsc("sort"));
        } else {
            //其他类型的用户
            sysMenuList = baseMapper.findListByUserId(userId);
        }
        //构建树形数据
        List<MenuEntity> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);

        //构建路由（按照router里面的js需要的数据格式来存储）
        List<RouterVo> routerVoList = RouterHelper.buildRouters(sysMenuTreeList);
        return routerVoList;
    }

    @Override
    public List<String> getUserButtonList(Long id) {
        //超级管理员admin账号id为：1
        List<MenuEntity> sysMenuList;
        UserEntity userEntity = userService.getById(id);
        if (userEntity.getType().equals(UserCodeEnum.TYPE_SYSTEM_MANAGER.getCode())) {
            //超级管理员，查出所有权限
            sysMenuList = baseMapper.selectList(new QueryWrapper<MenuEntity>().eq("status", MenuCodeEnum.STATUS_NORMAL.getCode()).orderByAsc("sort"));
        } else {
            //其他类型的用户
            sysMenuList = baseMapper.findListByUserId(id);
        }
        //获取type为2的
        List<String> permissionList = sysMenuList.stream().filter(item -> {
            //如果type是2，则是按钮
            int type = item.getType();
            return 2 == type;
        }).map(item -> {
            return item.getPerms();
        }).collect(Collectors.toList());

        return permissionList;
    }

    @Override
    public List<MenuEntity> findNodes(QueryWrapper<MenuEntity> queryWrapper) {
        //获取所有的菜单
        List<MenuEntity> sysMenuList = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(sysMenuList)) {
            return null;
        }

        //构建树形数据
        List<MenuEntity> result = MenuHelper.buildTree(sysMenuList);
        return result;
    }

    @Override
    public List<MenuEntity> findSysMenuByRoleId(Long roleId, Long userId) {
        UserEntity curUser = userService.getById(userId);
        Integer type = curUser.getType();
        QueryWrapper<MenuEntity> wrapper = new QueryWrapper<>();
        if (type == UserCodeEnum.TYPE_SYSTEM_MANAGER.getCode().intValue()) {
            //--if--如果是系统管理员，查询出所有菜单（包括正常、停用状态的）
        } else if (type == UserCodeEnum.TYPE_ENTERPRISE_MANAGER.getCode().intValue()) {
            //--if--如果是企业管理员，查询可以查看的菜单
            //查询正常使用的菜单
            wrapper.eq("status", MenuCodeEnum.STATUS_NORMAL.getCode());
            ///查询当前用户所能操作的菜单
            //查询当前用户的角色
            UserRoleEntity userRoleEntity = userRoleService.getOne(new QueryWrapper<UserRoleEntity>().eq("user_id", curUser.getId()));
            //查询当前角色的菜单
            List<RoleMenuEntity> roleMenus = roleMenuService.list(new QueryWrapper<RoleMenuEntity>().eq("role_id", userRoleEntity.getRoleId()));
            List<Long> menuIdList = roleMenus.stream().map(RoleMenuEntity::getMenuId).collect(Collectors.toList());
            //只查询当前用户所能使用菜单
            if (menuIdList.size() > 0) {
                wrapper.in("id", menuIdList);
            }
        }
        //获取菜单列表
        List<MenuEntity> menuList = baseMapper.selectList(wrapper);
        //根据角色id获取角色权限
        List<RoleMenuEntity> roleMenus = roleMenuService.list(new QueryWrapper<RoleMenuEntity>().eq("role_id", roleId));
        //获取该角色已分配的所有权限id
        List<Long> roleMenuIds = roleMenus.stream().map(item -> item.getMenuId()).collect(Collectors.toList());
        //遍历所有权限列表 判断菜单是否被选中
        for (MenuEntity sysMenu : menuList) {
            if (roleMenuIds.contains(sysMenu.getId())) {
                //设置该权限已被分配
                sysMenu.setSelect(true);
            } else {
                sysMenu.setSelect(false);
            }
        }
        //将权限列表转换为权限树
        List<MenuEntity> sysMenus = MenuHelper.buildTree(menuList);
        return sysMenus;
    }

    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {
        //删除已分配的权限
        roleMenuService.remove(new QueryWrapper<RoleMenuEntity>().eq("role_id", assginMenuVo.getRoleId()));
        //遍历所有已选择的权限id，将角色id-权限id 加入到表中
        for (Long menuId : assginMenuVo.getMenuIdList()) {
            if (menuId != null) {
                //创建SysRoleMenu对象
                RoleMenuEntity sysRoleMenu = new RoleMenuEntity();
                sysRoleMenu.setMenuId(menuId);
                sysRoleMenu.setRoleId(assginMenuVo.getRoleId());
                //添加新权限
                roleMenuService.save(sysRoleMenu);
            }
        }
    }

}