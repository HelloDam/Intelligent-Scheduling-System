package com.dam.config;

//import com.dam.custom.CustomVerificationCodeFilter;

import com.dam.custom.CustomAccessDeniedHandler;
import com.dam.custom.CustomMd5PasswordEncoder;
import com.dam.filter.TokenAuthenticationFilter;
import com.dam.filter.TokenLoginFilter;
import com.dam.service.RecordLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity //@EnableWebSecurity是开启SpringSecurity的默认行为
@EnableGlobalMethodSecurity(prePostEnabled = true)//开启注解功能，默认禁用注解
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
//    @Qualifier("systemUserDetailsServiceImpl") // 指定实现类
    private UserDetailsService userDetailsService;
    @Autowired
    private CustomMd5PasswordEncoder customMd5PasswordEncoder;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
//    @Qualifier("systemRecordLoginLogServiceImpl") // 指定实现类
    private RecordLoginLogService loginLogService;
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 这是配置的关键，决定哪些接口开启防护，哪些接口绕过防护
        http
                //自定义没有权限时的报错信息
                .exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler)
                //关闭csrf
                .and().csrf().disable()
                //开启跨域以便前端调用接口
//                .cors().and()
                .authorizeRequests()
                //指定某些接口不需要通过验证即可访问。登陆接口肯定是不需要认证的
//                .antMatchers("/system/login/login").permitAll()
                //这里意思是其它所有接口需要认证才能访问
                .anyRequest().authenticated()
                .and()
                //TokenAuthenticationFilter放到UsernamePasswordAuthenticationFilter的前面，这样做就是为了除了登录的时候去查询数据库外，其他时候都用token进行认证。
                .addFilterBefore(new TokenAuthenticationFilter(redisTemplate), UsernamePasswordAuthenticationFilter.class)
                .addFilter(new TokenLoginFilter(authenticationManager(), redisTemplate, loginLogService));

        //禁用session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 指定UserDetailService和加密器
        auth.userDetailsService(userDetailsService).passwordEncoder(customMd5PasswordEncoder);
    }

    /**
     * 配置哪些请求不拦截
     * 排除swagger相关请求
     *
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/favicon.ico",
                "/swagger-resources/**",
                "/webjars/**", "/v2/**",
                "/swagger-ui.html/**",
                "/doc.html",

                "/system/login/sendMailCode",
                "/system/login/usernameCheck/**",
                "/system/login/regist",
                "/system/login/enterpriseRegister",
//                "/system/user/getUserListWithoutPosition",
//                "/system/user/userInfoVo",
                "/system/user/getByOpenid",
//                "/system/user/update",
                "/system/user/getUserEntityByToken",
                "/system/user/bindWechat",
//                "/system/user/listUserByStoreId",
//                "/system/user/listUserInfoVoByUserIds",
//                "/system/user/generateUserData",
                "/system/login/generateVerificationCode",
//                "/system/user/listAllMailByUserIdList",
//                "/system/user/getUserIdAndMailMapByUserIdList",
//                "/system/menu/storeAuthoritiesToRedis",
//                "/system/message/sendMesToUserList",

                "/api/ucenter/wx/callback",

                "/shift-scheduling-calculate-service/imserver/**",
                //定时任务需要访问
                "/shift-scheduling-calculate-service/schedulingdate/judgeOneDateIsRest",
                "/shift-scheduling-calculate-service/shiftuser/listStaffWorkDtoByWorkDate",
                "/system/user/getUserIdAndMailMapByUserIdList",
                "/system/user/listUserEntityByStoreId",

//                "/enterprise/userposition/infoByUserId",
//                "/enterprise/schedulingrule/copySchedulingRule",
//                "/enterprise/enterprise/info",
//                "/enterprise/store/info",

                "/system/menu/storeAuthoritiesToRedis",

                "/thirdParty/oss/policy",
                "/thirdParty/mail/send",
                "/api/ucenter/wx/**",
                "/thirdParty/mail/send"
        );
    }
}