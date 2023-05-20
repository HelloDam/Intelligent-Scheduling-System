package com.dam.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.annotation.OperationLog;
import com.dam.exception.SSSException;
import com.dam.feign.ShiftSchedulingCalculateFeignService;
import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.entity.system.UserRoleEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.enums.log.BusinessTypeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.system.UserInfoVo;
import com.dam.service.UserRoleService;
import com.dam.service.UserService;
import com.dam.utils.EncryptionUtil;
import com.dam.utils.JwtUtil;
import com.dam.utils.PageUtils;
import com.dam.utils.user_data_gererate.UserDataGenerateUtil;
import com.dam.utils.vo.SysUserQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author dam
 * @email 1782067308@qq.com
 * @date 2022-12-03 11:10:46
 */
@RestController
@RequestMapping("/system/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private ShiftSchedulingCalculateFeignService shiftSchedulingCalculateFeignService;
    private static final String title = "用户管理";

    @PostMapping("/{page}/{limit}")
    @PreAuthorize("hasAnyAuthority('bnt.user.list','bnt.storeUser.list')")
    public R index(
            @PathVariable Long page,
            @PathVariable Long limit,
            @RequestBody SysUserQueryVo userQueryVo,
            HttpServletRequest httpServletRequest) {
        Long enterpriseId = Long.parseLong(JwtUtil.getEnterpriseId(httpServletRequest.getHeader("token")));
        Long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        int userType = Integer.parseInt(JwtUtil.getUserType(httpServletRequest.getHeader("token")));

        PageUtils pageModel = userService.selectPage(page, limit, enterpriseId, storeId, userType, userQueryVo);

        ///封装vo数据
        List<UserEntity> userEntityList = (List<UserEntity>) pageModel.getList();
        List<Long> userIdList = new ArrayList<>();
        List<UserInfoVo> userInfoVoList = userService.buildUserInfoVoList(userEntityList);
        for (UserEntity userEntity : userEntityList) {
            userIdList.add(userEntity.getId());
        }

        ///查询用户的繁忙状态
        if (userQueryVo.getBusyStatus() == null) {
            if (userQueryVo.getIsNeedSearchBusyStatus() != null && userQueryVo.getIsNeedSearchBusyStatus() == true) {
                //--if--需要查询用户在当前班次时间段内是否繁忙
                Date shiftStartDate = userQueryVo.getShiftStartDate();
                Date shiftEndDate = userQueryVo.getShiftEndDate();
                Map<String, Object> param = new HashMap<>();
                param.put("shiftStartDate", shiftStartDate);
                param.put("shiftEndDate", shiftEndDate);
                param.put("storeId", storeId);
                R r = shiftSchedulingCalculateFeignService.listUserIdIsBusy(param);
                if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                    List<Long> userIdListIsBusy = r.getData("userIdListIsBusy", new TypeReference<List<Long>>() {
                    });
                    for (UserInfoVo userInfoVo : userInfoVoList) {
                        if (userIdListIsBusy.indexOf(userInfoVo.getId()) != -1) {
                            //--if--用户繁忙
                            userInfoVo.setIsBusy(true);
                        } else {
                            userInfoVo.setIsBusy(false);
                        }
                    }
                }
            }
        } else {
            if (userQueryVo.getBusyStatus() == 0) {
                for (UserInfoVo userInfoVo : userInfoVoList) {
                    //--if--查询的数据都是空闲状态的
                    userInfoVo.setIsBusy(false);
                }
            } else {
                for (UserInfoVo userInfoVo : userInfoVoList) {
                    userInfoVo.setIsBusy(true);
                }
            }
        }

        pageModel.setList(userInfoVoList);

        return R.ok().addData("page", pageModel);
    }

    @GetMapping("/listUserByStoreId")
//    @PreAuthorize("hasAnyAuthority('bnt.user.list','bnt.storeUser.list')")
    public R listUserByStoreId(@RequestParam("storeId") Long storeId) {
        long start = System.currentTimeMillis();
        List<UserEntity> userList = userService.list(new QueryWrapper<UserEntity>().eq("store_id", storeId).eq("is_deleted", 0));
//        System.out.println("0：" + (System.currentTimeMillis() - start) + "ms");
        List<UserInfoVo> userInfoVoList = userService.buildUserInfoVoList(userList);
//        System.out.println("1：" + (System.currentTimeMillis() - start) + "ms");
        return R.ok().addData("userInfoVoList", userInfoVoList);
    }

    @GetMapping("/listUserEntityByStoreId")
//    @PreAuthorize("hasAnyAuthority('bnt.user.list','bnt.storeUser.list')")
    public R listUserEntityByStoreId(@RequestParam("storeId") Long storeId) {
        long start = System.currentTimeMillis();
        List<UserEntity> userList = userService.list(new QueryWrapper<UserEntity>().eq("store_id", storeId).eq("is_deleted", 0));
        return R.ok().addData("userList", userList);
    }

    /**
     * 根据企业id集合查询用户，并封装成字典
     *
     * @return Map<Long, UserEntity> idAndUserEntityMap
     */
    @PostMapping("/getUserMapByIdList")
    public R getUserMapByIdList(@RequestBody List<Long> userIdList) {
        Map<Long, UserEntity> idAndUserEntityMap = new HashMap<>();
        if (userIdList.size() > 0) {
            List<UserEntity> userEntityList = userService.list(new QueryWrapper<UserEntity>().in("id", userIdList).eq("is_deleted", 0));
            for (UserEntity user : userEntityList) {
                idAndUserEntityMap.put(user.getId(), user);
            }
        }
        return R.ok().addData("idAndUserEntityMap", idAndUserEntityMap);
    }

    /**
     * 列表
     */
/*    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = userService.queryPage(params);

        return R.ok().addData("page", page);
    }*/

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @PreAuthorize("hasAnyAuthority('bnt.user.list','bnt.storeUser.list')")
    public R info(@PathVariable("id") Long id) {
        UserEntity user = userService.getById(id);
        return R.ok().addData("data", user);
    }

    /**
     * 信息
     */
    @RequestMapping("/getUserById")
    @PreAuthorize("hasAnyAuthority('bnt.user.list','bnt.storeUser.list')")
    public R getUserById(@RequestParam("id") Long id) {
        UserEntity user = userService.getById(id);
        return R.ok().addData("user", user);
    }

    /**
     * 根据微信openId查询用户
     *
     * @param openid
     * @return
     */
    @RequestMapping("/getByOpenid")
    public R getByOpenid(@RequestParam("openid") String openid) {
//        System.out.println("openid:" + openid);
        UserEntity user = userService.getByOpenid(openid);
//        System.out.println("user:" + user);
        return R.ok().addData("user", user);
    }

    @RequestMapping("/userInfoVo")
    public R userInfoVo(@RequestParam("id") Long id) {
        UserEntity user = userService.getById(id);
        UserInfoVo userInfoVo = null;
        if (user != null) {
            userInfoVo = userService.buildUserInfoVo(user);
        } else {
            System.out.println("id:" + id + "对应的用户为空");
        }
        return R.ok().addData("data", userInfoVo);
    }

    /**
     * 通过用户id集合查询相关用户信息
     *
     * @param userIds
     * @return
     */
    @PostMapping("/listUserInfoVoByUserIds")
    public R listUserInfoVoByUserIds(@RequestBody List<Long> userIds) {
        long start = System.currentTimeMillis();
        List<UserInfoVo> userInfoVoList = userService.listUserInfoVoByUserIds(userIds);
//        System.out.println("listUserInfoVoByUserIds：" + (System.currentTimeMillis() - start) + "ms");
        return R.ok().addData("userInfoVoList", userInfoVoList);
    }

    /**
     * 通过用户id集合查询相关用户信息
     *
     * @param userIds
     * @return List<UserEntity> userList
     */
    @PostMapping("/listUserByUserIds")
    public R listUserByUserIds(@RequestBody List<Long> userIds) {
        List<UserEntity> userList = userService.listUserByUserIds(userIds);
        return R.ok().addData("userList", userList);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @PreAuthorize("hasAnyAuthority('bnt.user.add','bnt.storeUser.add')")
    @OperationLog(title = UserController.title, businessType = BusinessTypeEnum.INSERT, detail = "新增用户")
    public R save(@RequestBody UserEntity user) {
        user.setPassword(EncryptionUtil.saltMd5Encrypt(user.getPassword()));
        user.setAvatar("https://smart-scheduling-system-13184.oss-cn-beijing.aliyuncs.com/2023-02-14/338fc2a2-62d4-4196-85f3-c479acf28ff4_头像.png");
        int count = userService.count(new QueryWrapper<UserEntity>().eq("username", user.getUsername()));
        if (count > 0) {
            return R.error(ResultCodeEnum.FAIL.getCode(),"该用户名已经存在，请更换用户名");
        } else {
            userService.save(user);
            return R.ok();
        }
    }

    /**
     * 直接保存用户
     */
    @RequestMapping("/directSave")
    @PreAuthorize("hasAnyAuthority('bnt.user.add','bnt.storeUser.add')")
    @OperationLog(title = UserController.title, businessType = BusinessTypeEnum.INSERT, detail = "直接新增用户")
    public R directSave(@RequestBody UserEntity user) {
        userService.save(user);
        ///赋予用户角色
        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setRoleId(3L);
        userRoleEntity.setUserId(user.getId());
        userRoleService.save(userRoleEntity);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
//    @PreAuthorize("hasAnyAuthority('bnt.user.update','bnt.storeUser.update')")
    @OperationLog(title = UserController.title, businessType = BusinessTypeEnum.UPDATE, detail = "修改用户")
    public R update(@RequestBody UserEntity user) {
//        System.out.println("save->user:" + user.toString());
        boolean b = userService.updateById(user);
//        System.out.println("更新是否成功：" + b);

        return R.ok();
    }

    @PostMapping("/bindWechat")
    public R bindWechat(@RequestBody UserEntity user) {
//        System.out.println("save->user:" + user.toString());
        boolean b = userService.updateById(user);
//        System.out.println("更新是否成功：" + b);

        return R.ok();
    }

    /**
     * 获取每个企业及其用户数量
     *
     * @param enterpriseIdList
     * @return
     */
    @PostMapping("/getEnterpriseIdAndUserNumMap")
    public R getEnterpriseIdAndUserNumMap(@RequestBody List<Long> enterpriseIdList) {
        HashMap<Long, Integer> enterpriseIdAndUserNumMap = userService.getEnterpriseIdAndUserNumMap(enterpriseIdList);
        return R.ok().addData("enterpriseIdAndUserNumMap", enterpriseIdAndUserNumMap);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete/{useId}")
    @PreAuthorize("hasAnyAuthority('bnt.user.delete','bnt.storeUser.delete')")
    @OperationLog(title = UserController.title, businessType = BusinessTypeEnum.DELETE, detail = "删除用户")
    public R delete(@PathVariable Long useId) {
        userService.removeById(useId);

        return R.ok();
    }


    /**
     * 删除
     */
    @PostMapping("/deleteBatch")
    @PreAuthorize("hasAnyAuthority('bnt.user.delete','bnt.storeUser.delete')")
    @OperationLog(title = UserController.title, businessType = BusinessTypeEnum.DELETE, detail = "批量删除用户")
    public R deleteBatch(@RequestBody Long[] ids) {
        userService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 修改用户状态
     *
     * @param id
     * @param status
     * @return
     */
    @GetMapping("updateStatus/{id}/{status}")
    @PreAuthorize("hasAnyAuthority('bnt.user.update','bnt.storeUser.update')")
    @OperationLog(title = UserController.title, businessType = BusinessTypeEnum.UPDATE, detail = "修改用户状态")
    public R updateStatus(@PathVariable String id, @PathVariable Integer status) {
        userService.updateStatus(id, status);
        return R.ok();
    }

    /**
     * 根据token获取用户信息
     *
     * @param request
     * @return
     */
    @RequestMapping("getUserInfoVoByToken")
    public R getUserInfoVoByToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        //调用jwt工具类方法，根据request对象获取头信息，返回用户id
        String username = JwtUtil.getUsername(token);
        UserEntity user = userService.getUserInfoByUsername(username);
        UserInfoVo userInfoVo = userService.buildUserInfoVo(user);
        return R.ok().addData("userInfoVo", userInfoVo);
    }

    /**
     * 根据token获取用户信息
     *
     * @param
     * @return
     */
    @RequestMapping("getUserEntityByToken")
    public R getUserEntityByToken(@RequestParam("token") String token) {
        System.out.println("getUserEntityByToken,token:" + token);
        //调用jwt工具类方法，根据request对象获取头信息，返回用户id
        String username = JwtUtil.getUsername(token);
        System.out.println("getUserEntityByToken,username:" + username);
        UserEntity user = userService.getUserInfoByUsername(username);
        System.out.println("getUserEntityByToken,user:" + JSON.toJSONString(user));
        return R.ok().addData("user", user);
    }

    @RequestMapping("changePassword")
    public R changePassword(@RequestParam("oldPassword") String oldPassword,
                            @RequestParam("newPassword") String newPassword,
                            HttpServletRequest request) {
        String token = request.getHeader("token");

        try {
            userService.changePassword(token, oldPassword, newPassword);
            return R.ok();
        } catch (SSSException e) {
            e.printStackTrace();
            return R.error(e.getCode(), e.getMessage());
        }
    }

    /**
     * 获取门店中还没有被分配职位的用户列表
     *
     * @return
     */
    @GetMapping("getUserInfoVoListWithoutPosition")
    public R getUserInfoVoListWithoutPosition(@RequestParam("token") String token) {
        long storeId = Long.parseLong(JwtUtil.getStoreId(token));
        List<UserEntity> userEntityList = userService.getUserListWithoutPosition(storeId);
        List<UserInfoVo> userInfoVoList = userService.buildUserInfoVoList(userEntityList);
        return R.ok().addData("data", userInfoVoList);
    }

    /**
     * 获取门店中还没有被分配职位的用户列表
     *
     * @return List<UserEntity> userEntityList
     */
    @GetMapping("getUserListWithoutPosition")
    public R getUserListWithoutPosition(@RequestParam("token") String token) {
        long storeId = Long.parseLong(JwtUtil.getStoreId(token));
        List<UserEntity> userEntityList = userService.getUserListWithoutPosition(storeId);
        return R.ok().addData("userEntityList", userEntityList);
    }

    /**
     * 随机生成用户数据
     *
     * @return
     */
    @GetMapping("generateUserData")
    @OperationLog(title = UserController.title, businessType = BusinessTypeEnum.INSERT, detail = "随机生成用户数据")
    public R generateUserData() {
        List<UserEntity> userEntityList = new UserDataGenerateUtil().generateUserData(2000);
        for (UserEntity userEntity : userEntityList) {
//            System.out.println("userEntity:" + userEntity);
            userEntity.setPassword(EncryptionUtil.saltMd5Encrypt(userEntity.getPassword()));
            userService.save(userEntity);
        }
        return R.ok();
    }


    @PostMapping("listAllMailByUserIdList")
    public R listAllMailByUserIdList(@RequestBody List<Long> userIdList) {
//        System.out.println("根据用户id集合查询邮箱集合");
//        System.out.println("userIdList:"+userIdList.toString());
        List<String> mailList = userService.listAllMailByUserIdList(userIdList);
//        System.out.println("mailList:"+mailList.toString());
        return R.ok().addData("mailList", mailList);
    }

    @PostMapping("getUserIdAndMailMapByUserIdList")
    public R getUserIdAndMailMapByUserIdList(@RequestBody List<Long> userIdList) {
        System.out.println("getUserIdAndMailMapByUserIdList，userIdList：" + userIdList.toString());
        List<UserEntity> userEntityList = userService.list(new QueryWrapper<UserEntity>().in("id", userIdList));
        Map<Long, String> userIdAndMailMap = new HashMap<>();
        for (UserEntity userEntity : userEntityList) {
            userIdAndMailMap.put(userEntity.getId(), userEntity.getMail());
        }
        System.out.println("userIdAndMailMap:" + userIdAndMailMap.toString());
        return R.ok().addData("userIdAndMailMap", userIdAndMailMap);
    }

    /**
     * 给用户随机设置头像
     *
     * @return
     */
    @GetMapping("randomSetAvatar")
    public R randomSetAvatar() {
        userService.randomSetAvatar();
        return R.ok();
    }

    @GetMapping("shuffleUserToDifferentStores")
    public R shuffleUserToDifferentStores(@RequestParam("enterpriseId") Long enterpriseId) {
        userService.shuffleUserToDifferentStores(enterpriseId);
        return R.ok();
    }
}
