package com.dam.utils;


import com.dam.model.entity.system.MenuEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuHelper {

    /**
     * 使用递归方法建菜单
     *
     * @param MenuEntityList
     * @return
     */
    public static List<MenuEntity> buildTree(List<MenuEntity> MenuEntityList) {
        List<MenuEntity> trees = new ArrayList<>();
        for (com.dam.model.entity.system.MenuEntity MenuEntity : MenuEntityList) {
            //对根节点，找子节点
            if (MenuEntity.getParentId() == 0) {
                trees.add(findChildren(MenuEntity, MenuEntityList));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     *
     * @param treeNodes
     * @return
     */
    public static MenuEntity findChildren(MenuEntity MenuEntity, List<MenuEntity> treeNodes) {
        MenuEntity.setChildren(new ArrayList<com.dam.model.entity.system.MenuEntity>());

        for (com.dam.model.entity.system.MenuEntity it : treeNodes) {
            if (MenuEntity.getId().equals(it.getParentId())) {
                if (MenuEntity.getChildren() == null) {
                    MenuEntity.setChildren(new ArrayList<>());
                }
                MenuEntity.getChildren().add(findChildren(it, treeNodes));
            }
        }
        //按照sort字段递增排序
        Collections.sort(MenuEntity.getChildren(), ((o1, o2) -> {
            return Integer.compare(o1.getSort(), o2.getSort());
        }));
        return MenuEntity;
    }
}
