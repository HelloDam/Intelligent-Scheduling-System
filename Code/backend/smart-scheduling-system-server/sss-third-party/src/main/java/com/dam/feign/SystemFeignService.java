package com.dam.feign;

import com.dam.config.feign.FeignConfig;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "sss-system",configuration = FeignConfig.class)
public interface SystemFeignService {


    /**
     * 根据微信openId查询用户
     *
     * @param openid
     * @return
     */
    @RequestMapping("/system/user/getByOpenid")
    public R getByOpenid(@RequestParam("openid") String openid);

    @RequestMapping("/system/user/update")
    public R update(@RequestBody UserEntity user);

    @RequestMapping("/system/user/bindWechat")
    public R bindWechat(@RequestBody UserEntity user);

    @RequestMapping("/system/user/getUserEntityByToken")
    public R getUserEntityByToken(@RequestParam("token") String token);

    /**
     * 将权限信息存储到redis中
     *
     * @return
     */
    @PostMapping("/system/menu/storeAuthoritiesToRedis")
    public R storeAuthoritiesToRedis(@RequestBody Map<String, Object> paramMap);
}
