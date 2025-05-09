package com.dam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.model.entity.system.MenuEntity;
import com.dam.utils.PageUtils;
import com.dam.utils.vo.AssginMenuVo;
import com.dam.utils.vo.RouterVo;

import java.util.List;
import java.util.Map;

/**
 * 菜单表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-06 15:50:30
 */
public interface MenuService extends IService<MenuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<RouterVo> getUserMenuList(Long id);

    List<String> getUserButtonList(Long id);

    List<MenuEntity> findNodes(QueryWrapper<MenuEntity> queryWrapper);


    List<MenuEntity> findSysMenuByRoleId(Long roleId,Long userId);

    void doAssign(AssginMenuVo assginMenuVo);

}

