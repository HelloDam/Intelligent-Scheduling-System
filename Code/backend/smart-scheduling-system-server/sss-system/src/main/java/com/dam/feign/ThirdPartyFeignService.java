package com.dam.feign;

import com.dam.config.feign.FeignConfig;
import com.dam.model.dto.third_party.EmailDto;
import com.dam.model.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "sss-third-party", configuration = FeignConfig.class)
public interface ThirdPartyFeignService {

    @PostMapping("/thirdParty/mail/send")
    public R send(@RequestBody EmailDto emailDto);

}
