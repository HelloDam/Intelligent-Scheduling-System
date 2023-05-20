package com.dam.custom;

import com.dam.utils.EncryptionUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 自定义密码组件
 */
@Component//交给spring管理
public class CustomMd5PasswordEncoder implements PasswordEncoder {
    /**
     * 指定密码的加密方式
     *
     * @param rawPassword
     * @return
     */
    @Override
    public String encode(CharSequence rawPassword) {
        return EncryptionUtil.saltMd5Encrypt(rawPassword.toString());
    }

    /**
     * 判断用户所输入的密码和加密之后的密码是否相同
     *
     * @param rawPassword
     * @param encodedPassword
     * @return
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        boolean equals = EncryptionUtil.isSaltMd5Match(rawPassword.toString(), encodedPassword);
        if (equals == true) {
            return true;
        } else {
            System.out.println("登录密码验证不通过，密码错误");
//            System.out.println("原密码，rawPassword：" + rawPassword);
//            System.out.println("原密码加密：" + encrypt);
//            System.out.println("数据库中已加密的密码，encodedPassword：" + encodedPassword);
            return false;
        }
    }
}
