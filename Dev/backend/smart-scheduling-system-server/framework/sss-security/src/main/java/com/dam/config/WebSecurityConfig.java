package com.dam.config;

import com.dam.custom.CustomAccessDeniedHandler;
import com.dam.custom.CustomMd5PasswordEncoder;
import com.dam.filter.IpFlowLimitFilter;
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
@EnableWebSecurity //开启SpringSecurity的默认行为
@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启方法级别的安全注解（如@PreAuthorize, @PostAuthorize）
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
    @Autowired
    private IpFlowControlConfiguration ipFlowControlConfiguration;

    /**
     * 创建并返回一个AuthenticationManager实例，此方法由父类WebSecurityConfigurerAdapter提供
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 这是配置的关键，决定哪些接口开启防护，哪些接口绕过防护
        http
                // 自定义没有权限时的报错信息
                .exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler)

                // 关闭CSRF（跨站请求伪造）防护
                .and().csrf().disable()

                // 开启跨域以便前端调用接口（网关已经做了全局跨域）
                //.cors().and()

                // 设置访问控制规则
                .authorizeRequests()
                // 指定某些接口不需要通过验证即可访问。登陆接口肯定是不需要认证的（在下面统一配置了）
                //.antMatchers("/system/login/login").permitAll()
                // 这里意思是其它所有接口需要认证才能访问
                .anyRequest().authenticated()

                // 添加自定义过滤器，按照顺序依次执行
                .and()
                // TokenAuthenticationFilter放到UsernamePasswordAuthenticationFilter的前面，这样做就是为了除了登录的时候去查询数据库外，其他时候都用token进行认证。
                .addFilterBefore(new TokenAuthenticationFilter(redisTemplate), UsernamePasswordAuthenticationFilter.class)
                // IpFlowLimitFilter：在TokenAuthenticationFilter之前，进行IP流量限制
                .addFilterBefore(new IpFlowLimitFilter(redisTemplate, ipFlowControlConfiguration), TokenAuthenticationFilter.class)
                // TokenLoginFilter：处理登录请求，使用AuthenticationManager进行认证，并记录登录日志
                .addFilter(new TokenLoginFilter(authenticationManager(), redisTemplate, loginLogService));

        // 禁用session （采用无状态会话管理，适用于基于Token的身份验证）
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 指定 UserDetailService 和 加密器
        auth.userDetailsService(userDetailsService).passwordEncoder(customMd5PasswordEncoder);
    }

    /**
     * 配置哪些请求不拦截
     * 重写configure(WebSecurity)方法
     * 使用web.ignoring().antMatchers(...)指定一系列接口路径，
     * 这些路径的请求将不会经过Spring Security的过滤链，即不受安全约束。
     *
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/favicon.ico",
                "/swagger-resources/**",
                "/webjars/**", "/v2/**",
                "/swagger-ui.html/**",
                "/doc.html",

                "/system/login/sendMailCode",
                "/system/login/usernameCheck/**",
                "/system/login/regist",
                "/system/login/enterpriseRegister",
                "/system/user/getByOpenid",
                "/system/user/getUserEntityByToken",
                "/system/user/bindWechat",
                "/system/login/generateVerificationCode",

                "/api/ucenter/wx/callback",

                "/scheduling/imserver/**",
                //定时任务需要访问
                "/scheduling/schedulingdate/judgeOneDateIsRest",
                "/scheduling/shiftuser/listStaffWorkDtoByWorkDate",
                "/system/user/getUserIdAndMailMapByUserIdList",
                "/system/user/listUserEntityByStoreId",

                "/system/menu/storeAuthoritiesToRedis",

                "/thirdParty/oss/policy",
                "/thirdParty/mail/send",
                "/thirdParty/wx/**"
        );
    }
}