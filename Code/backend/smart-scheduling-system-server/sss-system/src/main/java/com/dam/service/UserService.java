package com.dam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.exception.SSSException;
import com.dam.model.vo.system.UserInfoVo;
import com.dam.utils.PageUtils;
import com.dam.model.entity.system.UserEntity;
import com.dam.utils.vo.SysUserQueryVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-06 15:50:29
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    UserEntity getUserInfoByUsername(String username);

    Map<String, Object> getUserInfo(String username);

    UserEntity getUserByOneCondition(Map<String, String> searchMap);

    UserEntity getUserInfoByMail(String mail);

    void updateStatus(String id, Integer status);

    PageUtils selectPage(Long page, Long limit, Long enterpriseId,Long storeId,int userType, SysUserQueryVo userQueryVo);

    UserInfoVo buildUserInfoVo(UserEntity user);

    List<UserInfoVo> buildUserInfoVoList(List<UserEntity> userEntityList);

    boolean changePassword(String oldEncryptPassword, String oldPassword, String newPassword) throws SSSException;

    List<UserEntity> getUserListWithoutPosition(long storeId);

    UserEntity getByOpenid(String openid);

    List<UserInfoVo> listUserInfoVoByUserIds(List<Long> userIds);

    List<String> listAllMailByUserIdList(List<Long> userIdList);

    HashMap<Long, Integer> getEnterpriseIdAndUserNumMap(List<Long> enterpriseIdList);

    void randomSetAvatar();

    void shuffleUserToDifferentStores(Long enterpriseId);

    List<UserEntity> listUserByUserIds(List<Long> userIds);

    List<Integer> getRegisterUserNumOfYear(Integer year);
}

