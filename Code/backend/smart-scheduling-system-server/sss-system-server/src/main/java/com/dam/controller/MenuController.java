package com.dam.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.annotation.OperationLog;
import com.dam.constant.RedisConstant;
import com.dam.model.entity.system.MenuEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.log.BusinessTypeEnum;
import com.dam.model.result.R;
import com.dam.service.MenuService;
import com.dam.service.UserService;
import com.dam.utils.JwtUtil;
import com.dam.utils.PageUtils;
import com.dam.utils.StringUtils;
import com.dam.utils.vo.AssginMenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author dam
 * @email 1782067308@qq.com
 * @date 2022-12-03 11:10:46
 */
@RestController
@RequestMapping("/system/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String title = "菜单管理";

    /**
     * 列表
     */
    @RequestMapping("/list")
    @PreAuthorize("hasAuthority('bnt.sysMenu.list')")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = menuService.queryPage(params);
        return R.ok().addData("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @PreAuthorize("hasAuthority('bnt.sysMenu.list')")
    public R info(@PathVariable("id") Long id) {
        MenuEntity menu = menuService.getById(id);

        return R.ok().addData("data", menu);
    }

    /**
     * 保存
     */
    @OperationLog(title = MenuController.title, businessType = BusinessTypeEnum.INSERT, detail = "新增菜单")
    @PreAuthorize("hasAuthority('bnt.sysMenu.add')")
    @RequestMapping("/save")
    public R save(@RequestBody MenuEntity menu) {
        menuService.save(menu);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @OperationLog(title = MenuController.title, businessType = BusinessTypeEnum.UPDATE, detail = "更新菜单")
    @PreAuthorize("hasAuthority('bnt.sysMenu.update')")
    public R update(@RequestBody MenuEntity menu) {
        menuService.updateById(menu);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete/{id}")
    @OperationLog(title = MenuController.title, businessType = BusinessTypeEnum.DELETE, detail = "删除菜单")
    @PreAuthorize("hasAuthority('bnt.sysMenu.delete')")
    public R delete(@PathVariable String id) {
        menuService.removeById(id);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteBatch")
    @OperationLog(title = MenuController.title, businessType = BusinessTypeEnum.DELETE, detail = "批量删除菜单")
    @PreAuthorize("hasAuthority('bnt.sysMenu.delete')")
    public R delete(@RequestBody Long[] ids) {
        menuService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 树形数据显示
     *
     * @return
     */
    @PostMapping("/findNodes")
    @PreAuthorize("hasAuthority('bnt.sysMenu.list')")
    public R findNodes(@RequestBody Map<String, Object> map) {
        QueryWrapper<MenuEntity> queryWrapper = new QueryWrapper<>();

        String menuName = (String) map.get("menuName");
        String status = (String) map.get("status");
        if (!StringUtils.isEmpty(menuName)) {
            queryWrapper.like("name", "%" + menuName + "%");
        }
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("status", status);
        }

        List<MenuEntity> list = menuService.findNodes(queryWrapper);
        return R.ok().addData("data", list);
    }

    /**
     * 根据角色id获取菜单列表（含有已经选中的菜单和未选中的菜单）
     *
     * @param roleId
     * @return
     */
    @GetMapping("/toAssign/{roleId}")
    public R toAssign(@PathVariable Long roleId, HttpServletRequest httpServletRequest) {
        Long userId = Long.parseLong(JwtUtil.getUserId(httpServletRequest.getHeader("token")));
        List<MenuEntity> list = menuService.findSysMenuByRoleId(roleId, userId);
        return R.ok().addData("data", list);
    }


    /**
     * 分配菜单
     *
     * @param assginMenuVo
     * @return
     */
    @PostMapping("/doAssign")
    @OperationLog(title = MenuController.title, businessType = BusinessTypeEnum.ASSGIN, detail = "给角色分配菜单")
    @PreAuthorize("hasAuthority('bnt.sysMenu.doAssign')")
    public R doAssign(@RequestBody AssginMenuVo assginMenuVo) {
        menuService.doAssign(assginMenuVo);
        return R.ok();
    }

    /**
     * 将权限信息存储到redis中
     *
     * @return
     */
    @PostMapping("/storeAuthoritiesToRedis")
    public R storeAuthoritiesToRedis(@RequestBody Map<String, Object> paramMap) {
        long userId = Long.parseLong(paramMap.get("userId").toString());
        String username = paramMap.get("username").toString();

        //根据userId查询操作权限
        List<String> userPermsList = menuService.getUserButtonList(userId);
        System.out.println("用户可操作按钮，userPermsList:" + userPermsList);
        //转化成security要求的格式数据
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String perm : userPermsList) {
            authorities.add(new SimpleGrantedAuthority(perm.trim()));
        }

        //将权限存储到redis中
        //保存权限数据到redis
        String redisKey = RedisConstant.AUTHORITY_PERMISSION + username;
//                    System.out.println("保存用户权限到redis中，redisKey：" + redisKey);
        //设置缓存过期时间是十五天
        redisTemplate.opsForValue().set(redisKey,
                JSON.toJSONString(authorities),
                15,
                TimeUnit.DAYS);
        return R.ok();
    }

}
