package com.dam.filter;


import com.alibaba.fastjson.JSON;
import com.dam.constant.RedisConstant;
import com.dam.custom.CustomUser;
import com.dam.exception.CaptchaExpiredException;
import com.dam.exception.CaptchaValidationException;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.system.LoginVo;
import com.dam.service.RecordLoginLogService;
import com.dam.utils.JwtUtil;
import com.dam.utils.ResponseUtil;
import com.dam.utils.ip.IpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.*;
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
 * 登录过滤器，继承UsernamePasswordAuthenticationFilter，对用户名密码进行登录校验
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private StringRedisTemplate redisTemplate;
    /**
     * 登录日志服务，用于记录用户的登录情况
     * 方便对系统的用户活跃情况进行统计
     */
    private RecordLoginLogService loginLogService;

    /**
     * 构造方法
     *
     * @param authenticationManager 认证管理器，负责实际的用户身份验证
     */
    public TokenLoginFilter(AuthenticationManager authenticationManager, StringRedisTemplate redisTemplate, RecordLoginLogService sysLoginLogService) {
//        System.out.println("登录验证过滤");
        this.setAuthenticationManager(authenticationManager);
        this.redisTemplate = redisTemplate;
        this.loginLogService = sysLoginLogService;
        // 不只是可以post
        this.setPostOnly(false);
        // 指定登录接口及提交方式，可以指定任意路径（我们默认的登陆路径）
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/system/login/login", "POST"));
    }

    /**
     * 登录认证，覆盖父类实现
     *
     * @param req HTTP请求对象
     * @param res HTTP响应对象
     * @return 认证后的Authentication对象
     * @throws AuthenticationException 认证过程中抛出的异常
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        System.out.println(">>>>> 调用 TokenLoginFilter 的 attemptAuthentication");
        System.out.println("进行登录认证-----------------------------------------------------------------------------------------");
        try {
            // 使用Jackson ObjectMapper从请求流中反序列化登录信息对象
            LoginVo loginVo = new ObjectMapper().readValue(req.getInputStream(), LoginVo.class);
            System.out.println("loginVo:" + JSON.toJSONString(loginVo));

            // 判断登录验证码是否正确
            String redisKey = RedisConstant.Verification_Code + loginVo.getUuid();
            String verificationCode = redisTemplate.opsForValue().get(redisKey);
            if (verificationCode == null) {
                throw new CaptchaExpiredException("验证码已经失效，请刷新之后再重新登录");
            }
            if (!verificationCode.toLowerCase().equals(loginVo.getVerificationCode().toLowerCase())) {
                throw new CaptchaValidationException("验证码输入不正确");
            }
            // 创建UsernamePasswordAuthenticationToken，封装登录信息
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(loginVo.getUsername(), loginVo.getPassword());
//            System.out.println("authenticationToken:" + authenticationToken.toString());
            // 调用父类的authenticate方法，通过认证管理器进行实际的身份验证，会判定登陆密码和数据库密码是否一致
            Authentication authenticate = this.getAuthenticationManager().authenticate(authenticationToken);
            System.out.println("登录验证成功");
            return authenticate;
        } catch (IOException e) {
            System.out.println("登录验证失败");
            throw new RuntimeException(e);
        }

    }

    /**
     * 登录成功后的处理方法，覆盖父类实现
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
                                            Authentication auth) {
        System.out.println(">>>>> 调用 TokenLoginFilter 的 successfulAuthentication");
        System.out.println("登录成功，生成token------------------------------------------------------------------------------------------");
        // 获取当前用户信息
        CustomUser customUser = (CustomUser) auth.getPrincipal();
        // 保存权限数据到redis
        String redisKey = RedisConstant.AUTHORITY_PERMISSION + customUser.getUsername();
        System.out.println("保存用户权限到redis中，redisKey：" + redisKey);
        // 设置缓存过期时间是十五天
        redisTemplate.opsForValue().set(redisKey,
                JSON.toJSONString(customUser.getAuthorities()),
                15,
                TimeUnit.DAYS);

        // 生成token
        UserEntity sysUser = customUser.getSysUser();
        String token = JwtUtil.createToken(sysUser.getId(), sysUser.getUsername(), sysUser.getEnterpriseId(), sysUser.getStoreId(), sysUser.getType());
        System.out.println("token:" + token);

        // 记录登录日志
        loginLogService.recordLoginLog(customUser.getUsername(), 0, IpUtil.getIpAddress(request), "登录成功", sysUser.getEnterpriseId(), sysUser.getStoreId());

        // 将token返回给前端
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        ResponseUtil.out(response, R.ok().addData("data", map));
    }

    /**
     * 登录失败后的处理方法，覆盖父类实现
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param e        认证失败异常
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException e) {
        System.out.println(">>>>> 调用 TokenLoginFilter 的 unsuccessfulAuthentication");
        System.out.println("登录失败------------------------------------------------------------------------------------------");
        System.out.println("失败原因：" + e.getMessage());
        // 分析具体失败原因并提供对应的错误信息
        String errorMessage;
        if (e instanceof BadCredentialsException) {
            errorMessage = "用户名或密码错误";
        } else if (e instanceof DisabledException) {
            errorMessage = "账户已被禁用，请联系管理员";
        } else if (e instanceof LockedException) {
            errorMessage = "账户已被锁定，请联系管理员";
        } else if (e instanceof CaptchaExpiredException) {
            errorMessage = e.getMessage();
        } else if (e instanceof CaptchaValidationException) {
            errorMessage = e.getMessage();
        } else if (e instanceof AuthenticationServiceException) {
            errorMessage = "认证服务异常，请稍后重试";
        } else {
            errorMessage = "登录失败";
        }
        ResponseUtil.out(response, R.error(ResultCodeEnum.DATA_ERROR.getCode(), errorMessage));
    }
}