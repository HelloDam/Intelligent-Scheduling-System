package com.dam.controller;

import com.dam.model.entity.system.UserRoleEntity;
import com.dam.model.result.R;
import com.dam.service.UserRoleService;
import com.dam.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2022-12-03 21:21:06
 */
@RestController
@RequestMapping("system/userrole")
public class UserRoleController {
    @Autowired
    private UserRoleService userRoleService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userRoleService.queryPage(params);

        return R.ok().addData("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		UserRoleEntity userRole = userRoleService.getById(id);

        return R.ok().addData("userRole", userRole);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody UserRoleEntity userRole){
		userRoleService.save(userRole);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody UserRoleEntity userRole){
		userRoleService.updateById(userRole);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		userRoleService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
