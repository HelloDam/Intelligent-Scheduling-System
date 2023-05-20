package com.dam.filter;

import com.alibaba.fastjson.JSON;
import com.dam.constant.RedisConstant;
import com.dam.model.result.R;
import com.dam.model.enums.ResultCodeEnum;
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
 * <p>
 * 认证解析token过滤器
 * OncePerRequestFilter：每次请求都要过滤
 * </p>
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private StringRedisTemplate redisTemplate;

    public TokenAuthenticationFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("请求拦截，决定是否放行------------------------------------------------------------------------------------------");

//        logger.info("uri:" + request.getRequestURI());
//        System.out.println("request.getRequestURI():"+request.getRequestURI());
        //如果是登录接口，直接放行
        if ("/system/login/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (null != authentication) {
//            System.out.println("request:" + request.toString());
//            System.out.println("response:" + response.toString());
//            System.out.println("authentication:" + authentication.toString());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            //fixme 这里可能有问题
            ResponseUtil.out(response, R.ok().addData("data", ResultCodeEnum.PERMISSION));
        }
    }

    /**
     * 看看是否有token，根据token是否可以获取到用户，获取不到再进行账号密码登录
     * @param request
     * @return
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        System.out.println("获取权限信息------------------------------------------------------------------------------------------");

        // token置于header里
        String token = request.getHeader("token");
        logger.info("token:" + token);
        if (!StringUtils.isEmpty(token)) {
            String username = JwtUtil.getUsername(token);
            logger.info("username:" + username);
            if (!StringUtils.isEmpty(username)) {
                //获取授权信息
                String redisKey = RedisConstant.AUTHORITY_PERMISSION + username;
                String authoritiesString =  redisTemplate.opsForValue().get(redisKey);
                List<Map> mapList = JSON.parseArray(authoritiesString, Map.class);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                for (Map map : mapList) {
                    authorities.add(new SimpleGrantedAuthority((String) map.get("authority")));
                }
                return new UsernamePasswordAuthenticationToken(username, null, authorities);
            }
        }
        return null;
    }
}