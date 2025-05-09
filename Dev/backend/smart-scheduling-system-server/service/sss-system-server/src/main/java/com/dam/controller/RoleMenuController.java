package com.dam.controller;

import com.dam.model.entity.system.RoleMenuEntity;
import com.dam.model.result.R;
import com.dam.service.RoleMenuService;
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
@RequestMapping("system/rolemenu")
public class RoleMenuController {
    @Autowired
    private RoleMenuService roleMenuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = roleMenuService.queryPage(params);

        return R.ok().addData("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		RoleMenuEntity roleMenu = roleMenuService.getById(id);

        return R.ok().addData("roleMenu", roleMenu);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody RoleMenuEntity roleMenu){
		roleMenuService.save(roleMenu);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody RoleMenuEntity roleMenu){
		roleMenuService.updateById(roleMenu);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		roleMenuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
