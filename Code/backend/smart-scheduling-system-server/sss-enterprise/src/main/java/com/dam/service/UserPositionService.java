package com.dam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.model.entity.enterprise.UserPositionEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.vo.system.UserInfoVo;
import com.dam.utils.PageUtils;


import java.util.List;
import java.util.Map;

/**
 * user_position中间表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 15:17:00
 */
public interface UserPositionService extends IService<UserPositionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<UserInfoVo> getUserInfoListByPositionId(Long positionId);

    List<UserEntity> getUserListByPositionId(Long positionId);

    void saveAppointMemberData(Long positionId, List<Long> userIdArr);

    UserPositionEntity infoByUserId(Long userId);
}

