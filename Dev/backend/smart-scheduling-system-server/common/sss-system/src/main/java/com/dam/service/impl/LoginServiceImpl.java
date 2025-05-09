package com.dam.service.impl;

import com.dam.constant.RedisConstant;
import com.dam.exception.SSSException;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.service.LoginService;
import com.dam.service.UserService;
import com.dam.utils.CheckUtil;
import com.dam.utils.EncryptionUtil;
import com.dam.utils.VerificationCodeGenerateUtil;
import com.dam.utils.vo.RegistVo;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author dam
 * @create 2024/3/14 21:40
 */
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserService userService;
    private final StringRedisTemplate redisTemplate;
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    /**
     * 用户注册
     *
     * @param registerVo
     */
    @Override
    public void userRegister(RegistVo registerVo) {
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
            throw new SSSException(ResultCodeEnum.ACCOUNT_ERROR);
        }
        if (StringUtils.isEmpty(registerVo.getPassword())) {
            throw new SSSException(ResultCodeEnum.PASSWORD_ERROR);
        }
        if (!CheckUtil.passwordCheck(registerVo.getPassword())) {
            throw new SSSException(ResultCodeEnum.PASSWORD_WRONGFUL);
        }
        if (StringUtils.isEmpty(registerVo.getMail())) {
            throw new SSSException(ResultCodeEnum.FAIL.getCode(), "邮箱为空");
        }
        if (StringUtils.isEmpty(registerVo.getCode())) {
            throw new SSSException(ResultCodeEnum.FAIL.getCode(), "验证码为空");
        }

        // 先验证验证码是否正确先，来拦截一些请求，因为查缓存比较快
        String code = redisTemplate.opsForValue().get(RedisConstant.Mail_Code + registerVo.getMail());
        if (code == null || StringUtils.isEmpty(code) || !registerVo.getCode().equals(code)) {
            throw new SSSException(ResultCodeEnum.MAIL_EXIST_ERROR.getCode(), "验证码错误或者失效，请重新发送");
        }

        // 根据用户名查询
        UserEntity sysUserByUsername = userService.getUserInfoByUsername(registerVo.getUsername());
        if (null != sysUserByUsername) {
            // 用户名已经存在
            throw new SSSException(ResultCodeEnum.ACCOUNT_EXIST_ERROR.getCode(), ResultCodeEnum.ACCOUNT_EXIST_ERROR.getMessage());
        }

        // 根据邮箱查询
        UserEntity sysUserByMail = userService.getUserInfoByMail(registerVo.getMail());
        if (null != sysUserByMail) {
            // 邮箱已经被注册
            throw new SSSException(ResultCodeEnum.MAIL_EXIST_ERROR.getCode(), ResultCodeEnum.MAIL_EXIST_ERROR.getMessage());
        }

        // 创建用户
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(registerVo, userEntity);
        // 加密密码
        userEntity.setPassword(EncryptionUtil.saltMd5Encrypt(registerVo.getPassword()));
        userEntity.setType(10);
        userService.save(userEntity);
        // 将用户名存入布隆过滤器
        userRegisterCachePenetrationBloomFilter.add(userEntity.getUsername());
    }

    /**
     * 判断用户名是否存在
     *
     * @param username
     * @return
     */
    @Override
    public boolean isUserNameExit(String username) {
        boolean isExist = userRegisterCachePenetrationBloomFilter.contains(username);
        if (isExist == false) {
            // --if-- 布隆过滤器中没有，肯定没有
        } else {
            // --if-- 布隆过滤器中有，需要进一步查看数据库
            UserEntity userInfoByUsername = userService.getUserInfoByUsername(username);
            isExist = userInfoByUsername == null ? false : true;
        }
        return isExist;
    }

    @Override
    public String generateVerificationCode(String uuid) {
        VerificationCodeGenerateUtil codeGenerateUtil = new VerificationCodeGenerateUtil(120, 60);
        //验证码
        String verificationCode = codeGenerateUtil.getCode();
        //图片
        String image;
        try {
            image = codeGenerateUtil.convertToBase64(codeGenerateUtil.getImage(), "png");
        } catch (IOException e) {
            throw new SSSException(ResultCodeEnum.FAIL.getCode(), "验证码生成失败");
        }
        String redisKey = RedisConstant.Verification_Code + uuid;
        redisTemplate.opsForValue().set(redisKey, verificationCode, 60, TimeUnit.SECONDS);
        return image;
    }
}
