package com.dam.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.dam.model.entity.enterprise.UserPositionEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.exception.MpcException;
import com.dam.model.result.R;
import com.dam.model.vo.system.UserInfoVo;
import com.dam.feign.SystemFeignService;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dam.dao.UserPositionDao;
import com.dam.service.UserPositionService;


@Service("userPositionService")
public class UserPositionServiceImpl extends ServiceImpl<UserPositionDao, UserPositionEntity> implements UserPositionService {

    @Autowired
    private SystemFeignService systemFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserPositionEntity> page = this.page(
                new Query<UserPositionEntity>().getPage(params),
                new QueryWrapper<UserPositionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据职位查询相关的用户列表
     *
     * @param positionId
     * @return
     */
    @Override
    public List<UserInfoVo> getUserInfoListByPositionId(Long positionId) {
        List<UserPositionEntity> userPositionEntityList = baseMapper.selectList(new QueryWrapper<UserPositionEntity>().eq("position_id", positionId).eq("is_deleted", 0));
        List<Long> userIdList = userPositionEntityList.stream().map(UserPositionEntity::getUserId).collect(Collectors.toList());
        R r = systemFeignService.listUserInfoVoByUserIds(userIdList);
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            return r.getData("userInfoVoList", new TypeReference<List<UserInfoVo>>() {
            });
        } else {
            //远程服务调用失败
            throw new MpcException(ResultCodeEnum.Feign_ERROR);
        }
    }

    /**
     * 根据职位查询相关的用户列表
     *
     * @param positionId
     * @return
     */
    public List<UserEntity> getUserListByPositionId(Long positionId) {
        List<UserPositionEntity> userPositionEntityList = baseMapper.selectList(new QueryWrapper<UserPositionEntity>().eq("position_id", positionId).eq("is_deleted", 0));
        List<Long> userIdList = userPositionEntityList.stream().map(UserPositionEntity::getUserId).collect(Collectors.toList());
        R r = systemFeignService.listUserByUserIds(userIdList);
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            return r.getData("userList", new TypeReference<List<UserEntity>>() {
            });
        } else {
            //远程服务调用失败
            throw new MpcException(ResultCodeEnum.Feign_ERROR);
        }
    }


    @Override
    public void saveAppointMemberData(Long positionId, List<Long> userIdArr) {
        //删除所有该职位所指定的成员
        baseMapper.delete(new QueryWrapper<UserPositionEntity>().eq("position_id", positionId));

        //保存职位所指定的成员信息
        for (Long userId : userIdArr) {
            UserPositionEntity userPositionEntity = new UserPositionEntity();
            userPositionEntity.setPositionId(positionId);
            userPositionEntity.setUserId(userId);
            baseMapper.insert(userPositionEntity);
        }
    }

    @Override
    public UserPositionEntity infoByUserId(Long userId) {
        return baseMapper.selectOne(new QueryWrapper<UserPositionEntity>().eq("user_id", userId).eq("is_deleted", 0));
    }

}