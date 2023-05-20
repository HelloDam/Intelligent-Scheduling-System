//package com.dam.custom;
//
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.util.StringUtils;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//public class CustomVerificationCodeFilter extends UsernamePasswordAuthenticationFilter {
//
//    private StringRedisTemplate redisTemplate;
//
//    public CustomVerificationCodeFilter(StringRedisTemplate redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        // 获取验证码
//        String inputCode = request.getParameter("code");
//        // 获取 session 中的验证码
//        String verifyCode = (String) request.getSession().getAttribute("verifyCode");
//        // 判断验证码是否正确
//        if (StringUtils.isEmpty(inputCode) || StringUtils.isEmpty(verifyCode) || !inputCode.equals(verifyCode)) {
//            throw new AuthenticationServiceException("验证码不正确");
//        }
//        // 调用父类的 attemptAuthentication() 方法进行登录验证
//        return super.attemptAuthentication(request, response);
//    }
//
//}
