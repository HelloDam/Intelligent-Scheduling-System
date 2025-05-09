package com.dam.filter;

import com.dam.config.IpFlowControlConfiguration;
import com.dam.utils.FlowLimitUtil;
import com.dam.utils.ip.IpUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author dam
 * @create 2024/3/15 19:29
 */
@Slf4j
@RequiredArgsConstructor
public class IpFlowLimitFilter extends OncePerRequestFilter {
    /**
     * Spring Data Redis模板，用于执行IP限流相关的Redis操作。
     */
    private final StringRedisTemplate stringRedisTemplate;
    /**
     * IP访问频率控制配置，包含时间窗口、最大访问次数等限流规则。
     */
    private final IpFlowControlConfiguration ipFlowControlConfiguration;

    /**
     * 实现父类OncePerRequestFilter的doFilterInternal方法，对请求进行IP限流检查。
     * 如果IP访问频率未超过限制，则放行请求；否则，返回错误响应并终止过滤链。
     *
     * @param request     当前HTTP请求
     * @param response    当前HTTP响应
     * @param filterChain 过滤链对象，用于继续传递请求到后续过滤器或目标处理器
     * @throws ServletException 如果在过滤过程中抛出Servlet相关异常
     * @throws IOException      如果在过滤过程中抛出IO相关异常
     */
    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        System.out.println("IP限流过滤");
        // 获取用户ip
        String ipAddr = IpUtils.getIpAddr(request);
        // 执行IP访问频率检查，如果超过限制则直接返回错误响应，否则继续处理请求
        if (!FlowLimitUtil.ipFlowLimit(ipAddr, response, stringRedisTemplate, ipFlowControlConfiguration, "interface")) {
            // IP访问频率已超过限制，已返回错误响应，无需继续过滤链
            return;
        }
        // 如果上面没有报错，放行
        filterChain.doFilter(request, response);
    }

}
