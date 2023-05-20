package com.dam.service.impl.security;


import com.dam.custom.CustomUser;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.system.UserCodeEnum;
import com.dam.service.MenuService;
import com.dam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
//@Service("systemUserDetailsServiceImpl")
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService sysUserService;
    @Autowired
    private MenuService menuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("根据用户名查询用户授权信息----------------------------------------------------------------------------");
        UserEntity sysUser = sysUserService.getUserInfoByUsername(username);
        if (null == sysUser) {
            throw new UsernameNotFoundException("用户名不存在，请检查输入是否错误");
        }
        if (sysUser.getStatus().intValue() == UserCodeEnum.STATUS_BAN.getCode().intValue()) {
            throw new RuntimeException("账号已被禁用，请咨询管理员");
        }
        //根据userId查询操作权限
        List<String> userPermsList = menuService.getUserButtonList(sysUser.getId());
        System.out.println("用户可操作按钮，userPermsList:" + userPermsList);
        //转化成security要求的格式数据
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String perm : userPermsList) {
            authorities.add(new SimpleGrantedAuthority(perm.trim()));
        }
//        System.out.println("authorities:" + authorities);
        return new CustomUser(sysUser, authorities);
    }

}
