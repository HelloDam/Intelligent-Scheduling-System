package com.dam.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.model.entity.enterprise.EnterpriseEntity;
import com.dam.model.entity.enterprise.PositionEntity;
import com.dam.model.entity.enterprise.UserPositionEntity;
import com.dam.model.result.R;
import com.dam.service.PositionService;
import com.dam.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import com.dam.service.UserPositionService;


/**
 * user_position中间表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 15:17:00
 */
@RestController
@RequestMapping("enterprise/userposition")
public class UserPositionController {
    @Autowired
    private UserPositionService userPositionService;
    @Autowired
    private PositionService positionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = userPositionService.queryPage(params);

        return R.ok().addData("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        UserPositionEntity userPosition = userPositionService.getById(id);

        return R.ok().addData("userPosition", userPosition);
    }

    /**
     * 根据企业id集合查询企业，并封装成字典
     *
     * @return
     */
    @PostMapping("/getUserIdAndPositionIdMapByUserIdList")
    public R getUserIdAndPositionIdMapByUserIdList(@RequestBody List<Long> userIdList) {
        System.out.println("getUserIdAndPositionIdMapByUserIdList，userIdList:" + userIdList.toString());
        List<UserPositionEntity> enterpriseEntityList = userPositionService.list(new QueryWrapper<UserPositionEntity>().in("user_id", userIdList).eq("is_deleted", 0));
        Map<Long, Long> userIdAndPositionIdMap = new HashMap<>();
        for (UserPositionEntity userPosition : enterpriseEntityList) {
            userIdAndPositionIdMap.put(userPosition.getUserId(), userPosition.getPositionId());
        }
        return R.ok().addData("userIdAndPositionIdMap", userIdAndPositionIdMap);
    }

    @RequestMapping("/listUserIdList")
    public List<Long> listUserIdList(@RequestParam("PositionIdList") List<Long> PositionIdList) {
        List<Long> readPositionIdSet = new ArrayList<>();
        for (Long positionId : PositionIdList) {
            //查询看看有没有子职位
            addPositionIdToSet(readPositionIdSet, positionId);
        }
        List<UserPositionEntity> userPositionEntityList = userPositionService.list(new QueryWrapper<UserPositionEntity>().in("position_id", new ArrayList<>(readPositionIdSet)));
        List<Long> userIdList = userPositionEntityList.stream().map(UserPositionEntity::getUserId).collect(Collectors.toList());
        return userIdList;
    }

    private void addPositionIdToSet(List<Long> readPositionIdSet, Long positionId) {
        List<PositionEntity> positionEntityList = positionService.list(new QueryWrapper<PositionEntity>().eq("parent_id", positionId));
        if (positionEntityList != null && positionEntityList.size() > 0) {
            for (PositionEntity positionEntity : positionEntityList) {
                addPositionIdToSet(readPositionIdSet, positionEntity.getId());
            }
        } else {
            readPositionIdSet.add(positionId);
        }
    }

    /**
     * 信息
     */
    @RequestMapping("/infoByUserId")
    public R infoByUserId(@RequestParam("userId") Long userId) {
        UserPositionEntity userPosition = userPositionService.infoByUserId(userId);

        return R.ok().addData("userPosition", userPosition);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody UserPositionEntity userPosition) {
        //先删除该用户之前的职位
        userPositionService.remove(new QueryWrapper<UserPositionEntity>().eq("user_id", userPosition.getUserId()));
        userPositionService.save(userPosition);
        return R.ok();
    }

    /**
     * 保存
     */
    @RequestMapping("/saveBatch")
    public R saveBatch(@RequestBody List<UserPositionEntity> userPositionList) {
        userPositionService.saveBatch(userPositionList);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody UserPositionEntity userPosition) {
        userPositionService.updateById(userPosition);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        userPositionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 根据用户id集合删除其职位
     */
    @RequestMapping("/deleteUserPositionByUserIdList")
    public R deleteUserPositionByUserIdList(@RequestBody List<Long> userIdList) {
        if (userIdList.size() > 0) {
            userPositionService.remove(new QueryWrapper<UserPositionEntity>().in("user_id", userIdList));
        }
        return R.ok();
    }

    /**
     * 保存职位所指定的成员列表
     *
     * @return
     */
    @PostMapping("/saveAppointMemberData")
    public R saveAppointMemberData(@RequestBody Map<String, Object> map) {
        Long positionId = Long.parseLong((String) map.get("positionId"));
        List<Long> userIdArr = JSON.parseObject(JSON.toJSONString(map.get("userIdArr")), new TypeReference<List<Long>>() {
        });
        userPositionService.saveAppointMemberData(positionId, userIdArr);
        return R.ok();
    }

}
