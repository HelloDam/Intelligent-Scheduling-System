package com.dam.utils;


import com.dam.model.entity.enterprise.PositionEntity;

import java.util.ArrayList;
import java.util.List;

public class PositionHelper {

    /**
     * 使用递归方法建菜单
     *
     * @param MenuEntityList
     * @return
     */
    public static List<PositionEntity> buildTree(List<PositionEntity> MenuEntityList) {
        List<PositionEntity> trees = new ArrayList<>();
        for (PositionEntity positionEntity : MenuEntityList) {
            //对根节点，找子节点
            if (positionEntity.getParentId() == 0) {
                trees.add(findChildren(positionEntity, MenuEntityList));
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
    public static PositionEntity findChildren(PositionEntity positionEntity, List<PositionEntity> treeNodes) {
        positionEntity.setChildren(new ArrayList<PositionEntity>());

        for (PositionEntity it : treeNodes) {
            if (positionEntity.getId().equals(it.getParentId())) {
                if (positionEntity.getChildren() == null) {
                    positionEntity.setChildren(new ArrayList<>());
                }
                positionEntity.getChildren().add(findChildren(it, treeNodes));
            }
        }
        return positionEntity;
    }
}
