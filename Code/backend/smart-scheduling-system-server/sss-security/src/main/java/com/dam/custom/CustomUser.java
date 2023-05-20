package com.dam.custom;

import com.dam.model.entity.system.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


/**
 * 自定义用户对象
 */
public class CustomUser extends User {

    /**
     * 我们自己的用户实体对象，要调取用户信息时直接获取这个实体对象
     */
    private UserEntity sysUser;

    public CustomUser(UserEntity sysUser, Collection<? extends GrantedAuthority> authorities) {
        super(sysUser.getUsername(), sysUser.getPassword(), authorities);
        this.sysUser = sysUser;
    }

    public UserEntity getSysUser() {
        return sysUser;
    }

    public void setSysUser(UserEntity sysUser) {
        this.sysUser = sysUser;
    }

}


