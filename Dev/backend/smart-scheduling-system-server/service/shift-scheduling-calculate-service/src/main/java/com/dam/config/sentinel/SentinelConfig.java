package com.dam.config.sentinel;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义流量控制所返回的错误内容
 */
@Configuration
public class SentinelConfig {

    public SentinelConfig() {
        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
            @Override
            public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse response, BlockException e) throws IOException {
                R error = R.error(ResultCodeEnum.TOO_MANY_REQUEST);
                response.setCharacterEncoding("UTF-8");
                //配置返回内容的格式
                response.setContentType("application/json");
                response.getWriter().write(JSON.toJSONString(error));
            }
        });
    }

}
