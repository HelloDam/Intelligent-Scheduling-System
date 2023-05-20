package com.dam.feign;

import com.dam.annotation.OperationLog;
import com.dam.config.feign.FeignConfig;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.entity.system.UserRoleEntity;
import com.dam.model.enums.log.BusinessTypeEnum;
import com.dam.model.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "sss-system", configuration = FeignConfig.class)
public interface SystemFeignService {
    /**
     * 根据id查询用户信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/system/user/userInfoVo")
    public R getUserInfoVoById(@RequestParam("id") Long id);

    /**
     * 根据id查询用户信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/system/user/getUserById")
    public R getUserById(@RequestParam("id") Long id);

    /**
     * 根据企业id集合查询用户，并封装成字典
     *
     * @return Map<Long, UserEntity> idAndUserEntityMap
     */
    @PostMapping("/system/user/getUserMapByIdList")
    public R getUserMapByIdList(@RequestBody List<Long> userIdList);

    /**
     * 直接保存用户
     */
    @RequestMapping("/system/user/directSave")
    public R directSave(@RequestBody UserEntity user);

    /**
     * 保存
     */
    @RequestMapping("/system/userrole/save")
    public R saveUserRole(@RequestBody UserRoleEntity userRole);

    /**
     * 获取每个企业及其用户数量
     *
     * @param enterpriseIdList
     * @return
     */
    @PostMapping("/system/user/getEnterpriseIdAndUserNumMap")
    public R getEnterpriseIdAndUserNumMap(@RequestBody List<Long> enterpriseIdList);

    /**
     * 通过用户id集合查询相关用户信息
     *
     * @param userIds
     * @return
     */
    @PostMapping("/system/user/listUserInfoVoByUserIds")
    public R listUserInfoVoByUserIds(@RequestBody List<Long> userIds);

    /**
     * 通过用户id集合查询相关用户信息
     *
     * @param userIds
     * @return List<UserEntity> userList
     */
    @PostMapping("/system/user/listUserByUserIds")
    public R listUserByUserIds(@RequestBody List<Long> userIds);

    /**
     * 获取门店中还没有被分配职位的用户列表
     *
     * @return List<UserEntity> userEntityList
     */
    @GetMapping("/system/user/getUserListWithoutPosition")
    public R getUserListWithoutPosition(@RequestParam("token") String token);

    /**
     * 获取门店中还没有被分配职位的用户列表
     *
     * @return
     */
    @GetMapping("/system/user/getUserInfoVoListWithoutPosition")
    public R getUserInfoVoListWithoutPosition(@RequestParam("token") String token);

    @PostMapping("/system/user/listAllMailByUserIdList")
    public R listAllMailByUserIdList(@RequestBody List<Long> userIdList);

    @PostMapping("/system/user/getUserIdAndMailMapByUserIdList")
    public R getUserIdAndMailMapByUserIdList(@RequestBody List<Long> userIdList);

    @GetMapping("/system/user/listUserByStoreId")
    public R listUserByStoreId(@RequestParam("storeId") Long storeId);


    @GetMapping("/system/user/listUserEntityByStoreId")
    public R listUserEntityByStoreId(@RequestParam("storeId") Long storeId);
}
