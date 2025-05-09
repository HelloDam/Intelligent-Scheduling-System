package com.dam.service;

import com.dam.utils.vo.RegistVo;

/**
 * @Author dam
 * @create 2024/3/14 21:39
 */
public interface LoginService {
    void userRegister(RegistVo registerVo);

    boolean isUserNameExit(String username);

    String generateVerificationCode(String uuid);
}
