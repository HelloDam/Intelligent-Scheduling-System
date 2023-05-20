package com.dam.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.annotation.OperationLog;
import com.dam.exception.SSSException;
import com.dam.model.entity.system.RoleEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.enums.log.BusinessTypeEnum;
import com.dam.model.enums.system.RoleCodeEnum;
import com.dam.model.enums.system.UserCodeEnum;
import com.dam.model.result.R;
import com.dam.service.RoleService;
import com.dam.service.UserService;
import com.dam.utils.JwtUtil;
import com.dam.utils.PageUtils;
import com.dam.utils.vo.AssginRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;


/**
 * @author dam
 * @email 1782067308@qq.com
 * @date 2022-12-03 11:10:46
 */
@RestController
@RequestMapping("/system/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;
    private static final String title = "角色管理";

    /**
     * 列表
     */
    @RequestMapping("/list")
    @PreAuthorize("hasAuthority('bnt.role.list')")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest httpRequest) {

        //构建查询wrapper
        QueryWrapper<RoleEntity> wrapper = new QueryWrapper<>();
        Long userId = Long.parseLong(JwtUtil.getUserId(httpRequest.getHeader("token")));
        UserEntity user = userService.getById(userId);
        String enterpriseId = JwtUtil.getEnterpriseId(httpRequest.getHeader("token"));
        String storeId = JwtUtil.getStoreId(httpRequest.getHeader("token"));

        if (user.getType() == UserCodeEnum.TYPE_SYSTEM_MANAGER.getCode().intValue()) {
            //--if--系统管理员
            wrapper.eq("type", RoleCodeEnum.TYPE_SYSTEM_ROLE.getCode());
        } else if (user.getType() == UserCodeEnum.TYPE_ENTERPRISE_MANAGER.getCode().intValue()) {
            //--if--企业管理员
            wrapper.eq("enterprise_id", enterpriseId);
        } else if (user.getType() == UserCodeEnum.TYPE_STORE_MANAGER.getCode().intValue()) {
            //--if--门店管理员，给普通用户分配
            wrapper.eq("store_id", storeId);
        }

        PageUtils page = roleService.queryPage(params, wrapper);

        return R.ok().addData("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @PreAuthorize("hasAuthority('bnt.role.list')")
    public R info(@PathVariable("id") Long id) {
        RoleEntity role = roleService.getById(id);

        return R.ok().addData("data", role);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @PreAuthorize("hasAuthority('bnt.role.add')")
    @OperationLog(title = RoleController.title, businessType = BusinessTypeEnum.INSERT, detail = "新增角色")
    public R save(@RequestBody RoleEntity role, HttpServletRequest httpRequest) {
        Long userId = Long.parseLong(JwtUtil.getUserId(httpRequest.getHeader("token")));
        String enterpriseId = JwtUtil.getEnterpriseId(httpRequest.getHeader("token"));
        String storeId = JwtUtil.getStoreId(httpRequest.getHeader("token"));
        try {
            roleService.saveRole(role, userId, enterpriseId, storeId);
            return R.ok();
        } catch (SSSException e) {
            e.printStackTrace();
            return R.error(e.getCode(), e.getMessage());
        }
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @PreAuthorize("hasAuthority('bnt.role.update')")
    @OperationLog(title = RoleController.title, businessType = BusinessTypeEnum.UPDATE, detail = "修改角色")
    public R update(@RequestBody RoleEntity role) {
        roleService.updateById(role);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('bnt.role.delete')")
    @OperationLog(title = RoleController.title, businessType = BusinessTypeEnum.DELETE, detail = "删除角色")
    public R delete(@PathVariable String id) {
        roleService.removeById(id);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteBatch")
    @PreAuthorize("hasAuthority('bnt.role.delete')")
    @OperationLog(title = RoleController.title, businessType = BusinessTypeEnum.DELETE, detail = "删除角色")
    public R deleteBatch(@RequestBody Long[] ids) {
        roleService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    /**
     * 根据用户获取角色数据
     *
     * @param toUserId
     * @return
     */
    @GetMapping("/toAssign/{toUserId}")
    public R toAssign(@PathVariable Long toUserId, HttpServletRequest httpRequest) {
        Long userId = Long.parseLong(JwtUtil.getUserId(httpRequest.getHeader("token")));
        Map<String, Object> roleMap = roleService.getRolesByUserId(userId, toUserId);
        return R.ok().addData("data", roleMap);
    }

    /**
     * 根据用户分配角色
     *
     * @param assginRoleVo
     * @return
     */
    @PostMapping("/doAssign")
    @PreAuthorize("hasAuthority('bnt.role.assignAuth')")
    @OperationLog(title = RoleController.title, businessType = BusinessTypeEnum.ASSGIN, detail = "根据用户分配角色")
    public R doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        roleService.doAssign(assginRoleVo);
        return R.ok();
    }

}
