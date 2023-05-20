package com.dam.feign;

import com.dam.config.feign.FeignConfig;
import com.dam.model.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "sss-system",configuration = FeignConfig.class)
public interface SystemFeignService {

    @GetMapping("/system/user/listUserByStoreId")
    public R listUserByStoreId(@RequestParam("storeId") Long storeId);

    /**
     * 通过用户id集合查询相关用户信息
     * @param userIds
     * @return userInfoVoList
     */
    @PostMapping("/system/user/listUserInfoVoByUserIds")
    public R listUserInfoVoByUserIds(@RequestBody List<Long> userIds);

    /**
     * 根据企业id集合查询用户，并封装成字典
     *
     * @return Map<Long, UserEntity> idAndUserEntityMap
     */
    @PostMapping("/system/user/getUserMapByIdList")
    public R getUserMapByIdList(@RequestBody List<Long> userIdList);

}
