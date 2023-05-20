package com.dam.filter;


import com.alibaba.fastjson.JSON;
import com.dam.constant.RedisConstant;
import com.dam.custom.CustomUser;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.result.R;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.vo.system.LoginVo;
import com.dam.service.RecordLoginLogService;
import com.dam.utils.IpUtil;
import com.dam.utils.JwtUtil;
import com.dam.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 登录过滤器，继承UsernamePasswordAuthenticationFilter，对用户名密码进行登录校验
 * </p>
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private StringRedisTemplate redisTemplate;
    private RecordLoginLogService loginLogService;

    /**
     * 构造方法
     *
     * @param authenticationManager
     */
    public TokenLoginFilter(AuthenticationManager authenticationManager, StringRedisTemplate redisTemplate, RecordLoginLogService sysLoginLogService) {
        System.out.println("指定登录请求路径------------------------------------------------------------------------------------------");

        this.setAuthenticationManager(authenticationManager);
        //不只是可以post
        this.setPostOnly(false);
        //指定登录接口及提交方式，可以指定任意路径（我们默认的登陆路径）
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/system/login/login", "POST"));
        this.redisTemplate = redisTemplate;
        this.loginLogService = sysLoginLogService;
    }

    /**
     * 登录认证
     *
     * @param req
     * @param res
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        System.out.println("进行登录认证-----------------------------------------------------------------------------------------");

        try {
            //流的方式获取对象
            LoginVo loginVo = new ObjectMapper().readValue(req.getInputStream(), LoginVo.class);

            //判断登录验证码是否正确
            String redisKey = RedisConstant.Verification_Code + loginVo.getUuid();
            String verificationCode = redisTemplate.opsForValue().get(redisKey);
            if (verificationCode == null) {
                throw new AuthenticationServiceException("验证码已经失效，请刷新之后再重新登录");
            }
            if (!verificationCode.toLowerCase().equals(loginVo.getVerificationCode().toLowerCase())) {
                throw new AuthenticationServiceException("验证码输入不正确");
            }
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(loginVo.getUsername(), loginVo.getPassword());
            System.out.println("authenticationToken:" + authenticationToken.toString());
            Authentication authenticate = this.getAuthenticationManager().authenticate(authenticationToken);
            System.out.println("登录验证成功");
            return authenticate;
        } catch (IOException e) {
            System.out.println("登录验证失败");
            throw new RuntimeException(e);
        }

    }

    /**
     * 登录成功之后要做什么
     *
     * @param request
     * @param response
     * @param chain
     * @param auth     当前验证对象
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        System.out.println("登录成功，生成token------------------------------------------------------------------------------------------");
        //获取当前用户信息
        CustomUser customUser = (CustomUser) auth.getPrincipal();
        //保存权限数据到redis
        String redisKey = RedisConstant.AUTHORITY_PERMISSION + customUser.getUsername();
        System.out.println("保存用户权限到redis中，redisKey：" + redisKey);
        //设置缓存过期时间是十五天
        redisTemplate.opsForValue().set(redisKey,
                JSON.toJSONString(customUser.getAuthorities()),
                15,
                TimeUnit.DAYS);
        //生成token
        UserEntity sysUser = customUser.getSysUser();
        String token = JwtUtil.createToken(sysUser.getId(), sysUser.getUsername(), sysUser.getEnterpriseId(), sysUser.getStoreId(), sysUser.getType());
        //记录登录日志
        loginLogService.recordLoginLog(customUser.getUsername(), 0, IpUtil.getIpAddress(request), "登录成功", sysUser.getEnterpriseId(), sysUser.getStoreId());

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        System.out.println("token:" + token);
        //fixme 这里可能有问题
        ResponseUtil.out(response, R.ok().addData("data", map));
    }

    /**
     * 登录失败之后要做什么
     *
     * @param request
     * @param response
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException e) throws IOException, ServletException {
        System.out.println("登录失败------------------------------------------------------------------------------------------");
        System.out.println("失败原因：" + e.getMessage());
        if (e.getCause() instanceof RuntimeException) {
            //fixme 可能有问题
            ResponseUtil.out(response, R.error(ResultCodeEnum.DATA_ERROR.getCode(), e.getMessage()));
        } else {
            ResponseUtil.out(response, R.error(ResultCodeEnum.DATA_ERROR.getCode(), e.getMessage()));
        }
    }
}