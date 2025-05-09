package com.dam.filter;

import com.alibaba.fastjson.JSON;
import com.dam.constant.RedisConstant;
import com.dam.context.UserContext;
import com.dam.context.UserInfoDTO;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import com.dam.utils.JwtUtil;
import com.dam.utils.ResponseUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 认证解析token过滤器
 * OncePerRequestFilter：每次请求都要过滤
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private StringRedisTemplate redisTemplate;

    public TokenAuthenticationFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 重写OncePerRequestFilter的doFilterInternal方法，处理每个请求
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println(">>>>> 调用 TokenAuthenticationFilter 的 doFilterInternal");
        System.out.println("权限验证过滤");

//        logger.info("uri:" + request.getRequestURI());
//        System.out.println("request.getRequestURI():"+request.getRequestURI());
        //如果是登录接口，直接放行
        if ("/system/login/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        // 调用getAuthentication方法尝试从请求中获取有效的Token并解析出认证信息
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (null != authentication) {
            // --if--如果获取到有效的认证信息
//            System.out.println("request:" + request.toString());
//            System.out.println("response:" + response.toString());
//            System.out.println("authentication:" + authentication.toString());
            // 将认证信息放入Spring Security的SecurityContextHolder中，以便后续请求链中使用
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 继续执行过滤链中的其他过滤器和目标处理器
            chain.doFilter(request, response);
        } else {
            // 如果未能获取到有效的认证信息，返回失败响应
            ResponseUtil.out(response, R.ok().addData("data", ResultCodeEnum.PERMISSION));
        }
    }

    /**
     * 看看是否有token，根据token是否可以获取到用户，获取不到再进行账号密码登录
     *
     * @param request
     * @return
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        System.out.println(">>>>> 调用 TokenAuthenticationFilter 的 getAuthentication");
        System.out.println("获取权限信息------------------------------------------------------------------------------------------");

        // token置于header里
        String token = request.getHeader("token");
//        logger.info("token:" + token);
        if (!StringUtils.isEmpty(token)) {
            String username = JwtUtil.getUsername(token);
//            logger.info("username:" + username);
            if (!StringUtils.isEmpty(username)) {
                // 获取授权信息
                String redisKey = RedisConstant.AUTHORITY_PERMISSION + username;
                // 权限字符串
                String authoritiesString = redisTemplate.opsForValue().get(redisKey);
                if (authoritiesString == null) {
                    return null;
                }
                // 解析权限字符串为具体的权限集合
                List<Map> mapList = JSON.parseArray(authoritiesString, Map.class);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                for (Map map : mapList) {
                    authorities.add(new SimpleGrantedAuthority((String) map.get("authority")));
                }
                // 存储用户上下文信息
                String userId = JwtUtil.getUserId(token);
                UserContext.setUser(UserInfoDTO.builder().userId(userId).userName(username).build());

                // 构建并返回UsernamePasswordAuthenticationToken对象，包含用户名、空密码（此处无需密码，因为已通过Token验证）和权限列表
                return new UsernamePasswordAuthenticationToken(username, null, authorities);
            }
        }
        // 如果未能成功解析Token或获取权限信息，返回null
        return null;
    }
}