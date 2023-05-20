package com.dam.controller;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.annotation.OperationLog;
import com.dam.exception.SSSException;
import com.dam.feign.SystemFeignService;
import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.model.entity.enterprise.UserPositionEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.enums.log.BusinessTypeEnum;
import com.dam.model.vo.enterprise.PositionVo;
import com.dam.model.vo.system.UserInfoVo;
import com.dam.service.UserPositionService;
import com.dam.utils.JwtUtil;
import org.apache.catalina.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.dam.model.entity.enterprise.PositionEntity;
import com.dam.service.PositionService;
import com.dam.utils.PageUtils;
import com.dam.model.result.R;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;


/**
 * 职位表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
@RestController
@RequestMapping("enterprise/position")
public class PositionController {
    @Autowired
    private PositionService positionService;
    @Autowired
    private UserPositionService userPositionService;
    @Autowired
    private SystemFeignService systemFeignService;
    @Autowired
    private ThreadPoolExecutor executor;
    private static final String title = "职位管理";

    /**
     * 查询当前职位已指定的成员和可以指定的成员
     *
     * @return
     */
    @GetMapping("/listMemberListAndAppointMemberIdList/{positionId}")
    public R listMemberListAndAppointMemberIdList(@PathVariable("positionId") Long positionId, HttpServletRequest httpServletRequest) throws ExecutionException, InterruptedException {
        ////声明变量
        //存储成员列表
        List<UserEntity> userInfoVoList = new ArrayList<>();
        //存储返回结果
        HashMap<String, Object> resultMap = new HashMap<>();
        List<UserEntity> userListWithoutPosition = new ArrayList<>();
        List<UserEntity> appointUserList = new ArrayList<>();

        // 1.获取之前的请求头数据
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        ////查询职位可以选择的成员列表，即该职位已分配成员以及未分配的成员
        CompletableFuture<Void> appointUserIdListFuture = CompletableFuture.runAsync(() -> {
            //2.每一个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            ///获取该职位已经绑定的成员列表
            for (UserEntity user : userPositionService.getUserListByPositionId(positionId)) {
                if (user != null) {
                    appointUserList.add(user);
                }
            }
            //存储已经被当前职位指定的成员的id
            List<Long> appointUserIdList = appointUserList.stream().map(item -> {
                return item.getId();
            }).collect(Collectors.toList());
            resultMap.put("appointUserIdList", appointUserIdList);
        }, executor);

        ////查询还没有被分配职位的成员
        CompletableFuture<Void> userListWithoutPositionFuture = CompletableFuture.runAsync(() -> {
            //2.每一个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            ///获取还没有被分配职位的成员列表
            R r = systemFeignService.getUserListWithoutPosition(httpServletRequest.getHeader("token"));
            if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                userListWithoutPosition.addAll(r.getData("userEntityList", new TypeReference<List<UserEntity> >() {
                }));
            } else {
                try {
                    throw new SSSException(ResultCodeEnum.Feign_ERROR);
                } catch (SSSException e) {
                    throw new RuntimeException(e);
                }
            }
        }, executor);

        ///合并集合
        CompletableFuture.allOf(appointUserIdListFuture,
                userListWithoutPositionFuture).get();
        userInfoVoList.addAll(appointUserList);
        userInfoVoList.addAll(userListWithoutPosition);
        resultMap.put("userInfoVoList", userInfoVoList);

        return R.ok().addData("data", resultMap);
    }

    /**
     * 给没有职位的用户随机分配职位
     *
     * @return
     */
    @RequestMapping("/randomAppointPosition")
    public R randomAppointPosition(HttpServletRequest httpServletRequest) {
        List<UserInfoVo> userListWithoutPosition = new ArrayList<>();
        ///获取还没有被分配职位的成员列表
        R r = systemFeignService.getUserInfoVoListWithoutPosition(httpServletRequest.getHeader("token"));
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            userListWithoutPosition.addAll(r.getData("data", new TypeReference<List<UserInfoVo>>() {
            }));
        } else {
            try {
                throw new SSSException(ResultCodeEnum.Feign_ERROR);
            } catch (SSSException e) {
                throw new RuntimeException(e);
            }
        }
        ///获取门店的职位
        long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        List<PositionEntity> sonPositionList = positionService.listAllSonPosition(storeId);
        Random random = new Random();
        for (UserInfoVo userInfoVo : userListWithoutPosition) {
            UserPositionEntity userPositionEntity = new UserPositionEntity();
            userPositionEntity.setUserId(userInfoVo.getId());
            userPositionEntity.setPositionId(sonPositionList.get(random.nextInt(sonPositionList.size())).getId());
            userPositionService.save(userPositionEntity);
        }
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest httpServletRequest) {
        //构建查询wrapper
        QueryWrapper<PositionEntity> wrapper = new QueryWrapper<>();
        String storeId = JwtUtil.getStoreId(httpServletRequest.getHeader("token"));
        wrapper.eq("store_id", storeId);

        PageUtils page = positionService.queryPage(params, wrapper);

        return R.ok().addData("page", page);
    }

    /**
     * 根据职位id集合查询职位集合
     *
     * @return List<PositionEntity>  positionEntityList
     */
    @PostMapping("/listPositionListByPositionIdList")
    public R listPositionListByPositionIdList(@RequestBody List<Long> positionIdList) {
        List<PositionEntity> positionEntityList = positionService.listPositionListByPositionIdList(positionIdList);
        return R.ok().addData("positionEntityList", positionEntityList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        PositionEntity position = positionService.getById(id);

        return R.ok().addData("position", position);
    }

    /**
     * 根据企业id集合查询门店，并封装成字典
     *
     * @return
     */
    @PostMapping("/getPositionMapByIdList")
    public R getPositionMapByIdList(@RequestBody List<Long> positionIdList) {
        if (positionIdList.size() > 0) {
            List<PositionEntity> positionEntityList = positionService.list(new QueryWrapper<PositionEntity>().in("id", positionIdList).eq("is_deleted", 0));
            Map<Long, PositionEntity> idAndPositionEntityMap = new HashMap<>();
            for (PositionEntity positionEntity : positionEntityList) {
                idAndPositionEntityMap.put(positionEntity.getId(), positionEntity);
            }
            return R.ok().addData("idAndPositionEntityMap", idAndPositionEntityMap);
        } else {
            return R.error(ResultCodeEnum.FAIL.getCode(), "positionIdList长度为零");
        }

    }

    /**
     * 根据门店id查询所有职位
     */
    @RequestMapping("/listByStoreId")
    public R listByStoreId(@RequestParam("storeId") Long storeId) {
        List<PositionEntity> positionEntityList = positionService.list(new QueryWrapper<PositionEntity>().eq("store_id", storeId));

        return R.ok().addData("positionEntityList", positionEntityList);
    }

    /**
     * 生成职位选择树
     */
    @RequestMapping("/getPositionSelectTree")
    public R getPositionSelectTree(HttpServletRequest httpServletRequest) {
        List<PositionVo> positionTree = positionService.buildTree(Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token"))));

        return R.ok().addData("positionTree", positionTree);
    }

    /**
     * 查询出所有叶子节点数据
     *
     * @return
     */
    @RequestMapping("/listAllSonPosition")
    public R listAllSonPosition(@RequestParam("storeId") Long storeId) {
        List<PositionEntity> sonPositionList = positionService.listAllSonPosition(storeId);
        return R.ok().addData("sonPositionList", sonPositionList);
    }

    /**
     * 给定门店id集合，查询出每个门店的所有叶子节点数据
     *
     * @return
     */
    @RequestMapping("/getStoreIdAndPositionList")
    public R getStoreIdAndPositionList(@RequestBody List<Long> storeIdList) {
        Map<Long, List<PositionEntity>> storeIdAndPositionList = new HashMap<>();
        for (Long storeId : storeIdList) {
            storeIdAndPositionList.put(storeId, positionService.listAllSonPosition(storeId));
        }
        return R.ok().addData("storeIdAndPositionList", storeIdAndPositionList);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @OperationLog(title = PositionController.title, businessType = BusinessTypeEnum.INSERT, detail = "新增职位")
    public R save(@RequestBody PositionEntity position, HttpServletRequest httpServletRequest) {
        position.setStoreId(Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token"))));
        positionService.save(position);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @OperationLog(title = PositionController.title, businessType = BusinessTypeEnum.UPDATE, detail = "修改职位")
    public R update(@RequestBody PositionEntity position) {
        positionService.updateById(position);

        return R.ok();
    }

    /**
     * 批量删除
     */
    @RequestMapping("/deleteBatch")
    @OperationLog(title = PositionController.title, businessType = BusinessTypeEnum.DELETE, detail = "批量删除职位")
    public R delete(@RequestBody Long[] ids) {
        positionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete/{id}")
    @OperationLog(title = PositionController.title, businessType = BusinessTypeEnum.DELETE, detail = "删除职位")
    public R delete(@PathVariable Long id) {
        positionService.removeById(id);

        return R.ok();
    }

    /**
     * 树形数据显示
     *
     * @param paramMap 查询传参
     * @return
     */
    @PostMapping("/findNodes")
    public R findNodes(HttpServletRequest httpServletRequest,
                       @RequestBody Map<String, Object> paramMap) {
        String name = null;
        String detail = null;
        if (paramMap != null) {
            if (paramMap.containsKey("name")) {
                name = paramMap.get("name").toString();
            }
            if (paramMap.containsKey("detail")) {
                detail = paramMap.get("detail").toString();
            }
        }

        Long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        List<PositionEntity> list = positionService.findNodes(storeId, name, detail);
        return R.ok().addData("data", list);
    }

    /**
     * 将一个门店的规则复制给同企业的其他门店
     *
     * @param storeId
     * @return
     */
    @PostMapping("/copyPositionToOtherStore")
    public R copyPositionToOtherStore(@RequestParam("storeId") Long storeId) {
        positionService.copyPositionToOtherStore(storeId);
        return R.ok();
    }

}
