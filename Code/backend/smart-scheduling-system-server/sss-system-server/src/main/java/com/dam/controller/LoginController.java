package com.dam.controller;

import com.alibaba.fastjson.JSON;
import com.dam.config.register.RegisterConfig;
import com.dam.constant.RedisConstant;
import com.dam.feign.ThirdPartyFeignService;
import com.dam.model.dto.third_party.EmailDto;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.result.R;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.service.UserService;
import com.dam.utils.*;
import com.dam.utils.vo.EnterpriseRegisterVo;
import com.dam.utils.vo.RegistVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/system/login")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RegisterConfig registerConfig;
    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 使用SpringSecurity之后不会调用该方法了
     *
     * @return
     */
/*    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginVo) {
        System.out.println("login----------------------------------------------------------------------------------------------");

        //判断登录验证码是否正确
        String redisKey = RedisConstant.Verification_Code + loginVo.getUuid();
//        System.out.println("登录，redisKey:" + redisKey);
        String verificationCode = redisTemplate.opsForValue().get(redisKey);
//        System.out.println("redis verificationCode:" + verificationCode);
//        System.out.println("用户 verificationCode:" + loginVo.getVerificationCode());
        if (verificationCode == null) {
            return R.error(ResultCodeEnum.FAIL.getCode(), "验证码已经失效，请刷新之后再重新登录");
        }
        if (!verificationCode.toLowerCase().equals(loginVo.getVerificationCode().toLowerCase()) ) {
            return R.error(ResultCodeEnum.CODE_ERROR);
        }

        //根据用户名查询数据
        UserEntity sysUser = userService.getUserInfoByUsername(loginVo.getUsername());
        if (null == sysUser) {
            return R.error(ResultCodeEnum.ACCOUNT_ERROR.getCode(), ResultCodeEnum.ACCOUNT_ERROR.getMessage());
        }
        //将密码加密之后和数据库的密码比对
        if (!EncryptionUtil.isSaltMd5Match(loginVo.getPassword(), sysUser.getPassword())) {
            return R.error(ResultCodeEnum.PASSWORD_ERROR.getCode(), ResultCodeEnum.PASSWORD_ERROR.getMessage());
        }
        //如果说用户被禁用的话，也不可用
        if (sysUser.getStatus().intValue() == UserCodeEnum.STATUS_BAN.getCode()) {
            throw new MpcException(ResultCodeEnum.ACCOUNT_STOP);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("token", JwtUtil.createToken(sysUser.getId(), sysUser.getUsername(), sysUser.getEnterpriseId(), sysUser.getStoreId(), sysUser.getType()));
//        System.out.println("map:"+map.toString());
        return R.ok().addData("data", map);
    }*/

    /**
     * 用户注册
     *
     * @return
     */
    @PostMapping("/regist")
    public R regist(@RequestBody RegistVo registerVo) {
        ////企业编码验证
       /* String enterpriseId = registVo.getEnterpriseId();
        String enterpriseCode = registVo.getEnterpriseCode();
        R r = enterpriseFeignService.info(enterpriseId);
        if (r.getCode().equals(ResultCodeEnum.SUCCESS.getCode())) {
            EnterpriseEntity enterpriseEntity = r.getData("data", new TypeReference<EnterpriseEntity>() {
            });
            if (!enterpriseCode.equals(enterpriseEntity.getCode())) {
                return R.error(ResultCodeEnum.FAIL.getCode(), "公司代码输入错误");
            }
        } else {
            return R.error(ResultCodeEnum.Feign_ERROR.getCode(), ResultCodeEnum.Feign_ERROR.getMessage());
        }*/
        ////用户注册
        if (StringUtils.isEmpty(registerVo.getUsername())) {
            return R.error(ResultCodeEnum.ACCOUNT_ERROR);
        }
        if (StringUtils.isEmpty(registerVo.getPassword())) {
            return R.error(ResultCodeEnum.PASSWORD_ERROR);
        }
        if (!CheckUtil.passwordCheck(registerVo.getPassword())) {
            return R.error(ResultCodeEnum.PASSWORD_WRONGFUL);
        }
        if (StringUtils.isEmpty(registerVo.getMail())) {
            return R.error(ResultCodeEnum.FAIL.getCode(), "邮箱为空");
        }
        if (StringUtils.isEmpty(registerVo.getCode())) {
            return R.error(ResultCodeEnum.FAIL.getCode(), "验证码为空");
        }

        UserEntity sysUserByUsername = userService.getUserInfoByUsername(registerVo.getUsername());
        if (null == sysUserByUsername) {
            UserEntity sysUserByMail = userService.getUserInfoByMail(registerVo.getMail());
            if (null == sysUserByMail) {
                //判断redis中的验证码是否正确
                String code = redisTemplate.opsForValue().get(RedisConstant.Mail_Code + registerVo.getMail());
                if (code != null && !StringUtils.isEmpty(code) && registerVo.getCode().equals(code)) {
                    UserEntity userEntity = new UserEntity();
                    BeanUtils.copyProperties(registerVo, userEntity);
                    //加密密码
                    userEntity.setPassword(EncryptionUtil.saltMd5Encrypt(registerVo.getPassword()));
                    userEntity.setType(10);
                    userService.save(userEntity);
                    return R.ok();
                } else {
                    return R.error(ResultCodeEnum.MAIL_EXIST_ERROR.getCode(), "验证码错误或者失效，请重新发送");
                }
            } else {
                //邮箱已经被注册
                return R.error(ResultCodeEnum.MAIL_EXIST_ERROR.getCode(), ResultCodeEnum.MAIL_EXIST_ERROR.getMessage());
            }
        } else {
            //用户名已经存在
            return R.error(ResultCodeEnum.ACCOUNT_EXIST_ERROR.getCode(), ResultCodeEnum.ACCOUNT_EXIST_ERROR.getMessage());
        }
    }

    /**
     * 企业注册
     *
     * @return
     */
    @PostMapping("/enterpriseRegister")
    public R enterpriseRegister(@RequestBody EnterpriseRegisterVo registerVo) {
        ////企业编码验证
       /* String enterpriseId = registVo.getEnterpriseId();
        String enterpriseCode = registVo.getEnterpriseCode();
        R r = enterpriseFeignService.info(enterpriseId);
        if (r.getCode().equals(ResultCodeEnum.SUCCESS.getCode())) {
            EnterpriseEntity enterpriseEntity = r.getData("data", new TypeReference<EnterpriseEntity>() {
            });
            if (!enterpriseCode.equals(enterpriseEntity.getCode())) {
                return R.error(ResultCodeEnum.FAIL.getCode(), "公司代码输入错误");
            }
        } else {
            return R.error(ResultCodeEnum.Feign_ERROR.getCode(), ResultCodeEnum.Feign_ERROR.getMessage());
        }*/
        ////用户注册
        if (StringUtils.isEmpty(registerVo.getName())) {
            return R.error(ResultCodeEnum.FAIL.getCode(), "企业名称为空");
        }
        if (StringUtils.isEmpty(registerVo.getDetail())) {
            return R.error(ResultCodeEnum.FAIL.getCode(), "企业描述为空");
        }
        if (StringUtils.isEmpty(registerVo.getMail())) {
            return R.error(ResultCodeEnum.FAIL.getCode(), "邮箱为空");
        }
        if (StringUtils.isEmpty(registerVo.getCode())) {
            return R.error(ResultCodeEnum.FAIL.getCode(), "验证码为空");
        }
        if (StringUtils.isEmpty(registerVo.getLogo())) {
            return R.error(ResultCodeEnum.FAIL.getCode(), "企业logo为空");
        }
        System.out.println("企业注册1");

        //判断redis中的验证码是否正确
        String code = redisTemplate.opsForValue().get(RedisConstant.Mail_Code + registerVo.getMail());
        if (code != null && !StringUtils.isEmpty(code) && registerVo.getCode().equals(code)) {
            System.out.println("企业注册2");
            //--if--通过验证码验证，给服务sss-enterprise发送消息
            //给MQ发送消息
//            rabbitTemplate.convertAndSend(RabbitMqConstant.ENTERPRISE_REGISTER_EXCHANGE, RabbitMqConstant.ENTERPRISE_REGISTER_ROUTER_KEY, registerVo);
            //将注册信息存储到redis中
//            redisTemplate.boundSetOps(RedisConstant.ENTERPRISE_REGISTER).add(JSON.toJSONString(registerVo));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            registerVo.setUuid(uuid);
            redisTemplate.boundHashOps(RedisConstant.ENTERPRISE_REGISTER).put(uuid, JSON.toJSONString(registerVo));
            return R.ok();
        } else {
            return R.error(ResultCodeEnum.MAIL_EXIST_ERROR.getCode(), "验证码错误或者失效，请重新发送");
        }

    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("/info")
    public R info(HttpServletRequest request, @RequestParam("token") String token) {

        //从请求里面获取token数据
//        String token = request.getHeader("token");
        if (token == null || StringUtils.isEmpty(token)) {
            return R.error(ResultCodeEnum.ARGUMENT_VALID_ERROR.getCode(), "传入token为空，请注意");
        }
        //获取用户名
        String username = JwtUtil.getUsername(token);
        //根据用户名来获取用户信息
        Map<String, Object> map = userService.getUserInfo(username);

        return R.ok().addData("data", map);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public R logout(HttpServletRequest httpServletRequest) {
        String username = JwtUtil.getUsername(httpServletRequest.getHeader("token"));//保存权限数据到redis
        String redisKey = RedisConstant.AUTHORITY_PERMISSION + username;
        System.out.println("用户登出，删除相关权限，redisKey：" + redisKey);
        //设置缓存过期时间是十五天
        redisTemplate.delete(redisKey);
        return R.ok();
    }

    /**
     * 判断用户名是否已经存在
     *
     * @param username
     * @return
     */
    @GetMapping("/usernameCheck/{username}")
    public R usernameCheck(@PathVariable String username) {
        UserEntity userInfoByUsername = userService.getUserInfoByUsername(username);
        boolean isExist = userInfoByUsername == null ? false : true;
        return R.ok().addData("isExist", isExist);
    }

    /**
     * 发送邮箱验证码
     *
     * @return
     */
    @PostMapping("/sendMailCode")
    public R sendMailCode(@RequestBody Map<String, Object> paramMap) {
        String mail = paramMap.get("mail").toString();
        //0：用户注册 1：企业注册
        Integer type = Integer.parseInt(paramMap.get("type").toString());

        String redisCode = redisTemplate.opsForValue().get(mail);
        if (redisCode != null) {
            return R.error(ResultCodeEnum.MAIL_SEND_ERROR.getCode(), "请勿频繁使用当前邮箱发送验证码");
        }

        String code = RandomCodeUtil.getFourBitRandom();

        ////发送邮件验证码
        EmailDto emailDto = new EmailDto();
        //设置收信人
        emailDto.setTo(mail);
        //设置邮件主题
        emailDto.setSubject("智能排班系统——注册邮件验证码");
        //设置邮件内容
//        emailDto.setContent("您好，您的邮件验证码是： " + code + " ，请在" + registerConfig.getCodeExpiration() + "秒之内输入并进行注册");

        String htmlContent = "";
        if (type == 0) {
            htmlContent = "<html>"
                    + "<head>"
                    + "<style>"
                    + "body {font-family: Arial, sans-serif; font-size: 16px; color: #333333; background-color: #FFFFFF; line-height: 1.5;}"
                    + "h1 {font-size: 24px; color: #007FFF; margin-bottom: 10px;}"
                    + "p {margin-bottom: 10px;}"
                    + ".code {font-weight: bold; color: #007FFF;}"
                    + ".wrapper {max-width: 600px; margin: 0 auto;}"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"wrapper\">"
                    + "<h2>欢迎进行用户注册</h2>"
                    + "<p>您好，</p>"
                    + "<p>&emsp;&emsp;您的邮件验证码是：<span class=\"code\">" + code + "</span>，请在 " + registerConfig.getCodeExpiration() + " 秒之内输入并进行注册。</p>"
                    + "<p>谢谢。</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";
        } else if (type == 1) {
            htmlContent = "<html>"
                    + "<head>"
                    + "<style>"
                    + "body {font-family: Arial, sans-serif; font-size: 16px; color: #333333; background-color: #FFFFFF; line-height: 1.5;}"
                    + "h1 {font-size: 24px; color: #007FFF; margin-bottom: 10px;}"
                    + "p {margin-bottom: 10px;}"
                    + ".code {font-weight: bold; color: #007FFF;}"
                    + ".wrapper {max-width: 600px; margin: 0 auto;}"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"wrapper\">"
                    + "<h2>欢迎进行企业注册</h2>"
                    + "<p>您好，</p>"
                    + "<p>&emsp;&emsp;您的邮件验证码是：<span class=\"code\">" + code + "</span>，请在 " + registerConfig.getCodeExpiration() + " 秒之内输入并进行注册。</p>"
                    + "<p>谢谢。</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";
        }

        emailDto.setContent(htmlContent);
        emailDto.setType(1);


        R r = thirdPartyFeignService.send(emailDto);

        ////将验证码存储到redis中，并设置过期时间
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            redisTemplate.opsForValue().set(RedisConstant.Mail_Code + mail, code, Integer.parseInt(registerConfig.getCodeExpiration()), TimeUnit.SECONDS);
            //设置
            return R.ok();
        } else {
            //设置
            return R.error(ResultCodeEnum.MAIL_SEND_ERROR.getCode(), ResultCodeEnum.MAIL_SEND_ERROR.getMessage());
        }
    }

    @GetMapping("/getUserByToken")
    public R getUserByToken(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("token");
//        String username = JwtHelper.getUsername(token);
        String userId = JwtUtil.getUserId(token);
        UserEntity userEntity = userService.getById(userId);
        System.out.println("userEntity:" + userEntity.toString());
        return R.ok().addData("data", userEntity);
    }

    /**
     * 生成验证码
     *
     * @return
     */
    @GetMapping("/generateVerificationCode")
    public R generateVerificationCode() {
        VerificationCodeGenerateUtil codeGenerateUtil = new VerificationCodeGenerateUtil(120, 60);
        //验证码
        String verificationCode = codeGenerateUtil.getCode();
        //图片
        String image = null;
        try {
            image = codeGenerateUtil.convertToBase64(codeGenerateUtil.getImage(), "png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //将验证码存储到redis中
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String redisKey = RedisConstant.Verification_Code + uuid;
//        System.out.println("redisKey:" + redisKey);
        redisTemplate.opsForValue().set(redisKey, verificationCode, 60, TimeUnit.SECONDS);

        return R.ok().addData("uuid", uuid).addData("image", image);
    }
}
