package com.dam.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.annotation.OperationLog;
import com.dam.constant.RedissonLockConstant;
import com.dam.model.entity.enterprise.SchedulingRuleEntity;
import com.dam.model.enums.log.BusinessTypeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.enterprise.SchedulingRuleVo;
import com.dam.service.StoreService;
import com.dam.utils.JwtUtil;
import com.dam.utils.PageUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.dam.service.SchedulingRuleService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * 门店规则中间表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-23 21:34:22
 */
@RestController
@RequestMapping("enterprise/schedulingrule")
public class SchedulingRuleController {
    @Autowired
    private SchedulingRuleService schedulingRuleService;
    @Autowired
    private StoreService storeService;
    private static final String title = "规则管理";


    /**
     * 列表
     */
    @RequestMapping("/list")
    @PreAuthorize("hasAuthority('bnt.ruleSet.list')")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = schedulingRuleService.queryPage(params);

        return R.ok().addData("page", page);
    }


    /**
     * 查询门店规则
     */
    @RequestMapping("/info/{id}")
    @PreAuthorize("hasAuthority('bnt.ruleSet.list')")
    public R info(@PathVariable("id") Long id) {
        SchedulingRuleEntity schedulingRuleEntity = schedulingRuleService.getById(id);

        return R.ok().addData("schedulingRuleEntity", schedulingRuleEntity);
    }

    /**
     * 将门店规则复制一遍
     */
    @RequestMapping("/copySchedulingRule")
    public R copySchedulingRule(@RequestParam("storeId") Long storeId) {
        SchedulingRuleEntity schedulingRuleEntity = schedulingRuleService.getSchedulingRuleVoByStoreIdAndType(storeId, 0);
        //复制
        SchedulingRuleEntity schedulingRuleEntityClone = schedulingRuleEntity.clone();
        schedulingRuleEntityClone.setRuleType(1);
        schedulingRuleService.save(schedulingRuleEntityClone);
        return R.ok().addData("ruleId", schedulingRuleEntityClone.getId());
    }

    /**
     * 获取门店的排班规则
     */
    @RequestMapping("/getSchedulingRuleVoByStoreId")
    @PreAuthorize("hasAuthority('bnt.ruleSet.list')")
    public R getSchedulingRuleVoByStoreId(HttpServletRequest httpServletRequest) {
        SchedulingRuleVo schedulingRuleVo = schedulingRuleService.getSchedulingRuleVoByStoreId(Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token"))));

        return R.ok().addData("schedulingRuleVo", schedulingRuleVo);
    }

    /**
     * 获取门店的排班规则
     */
    @RequestMapping("/getSchedulingRuleVoByGiveStoreId")
    @PreAuthorize("hasAuthority('bnt.ruleSet.list')")
    public R getSchedulingRuleVoByStoreId(@RequestParam("storeId") Long storeId) {
//        System.out.println("storeId:" + storeId);
        SchedulingRuleVo schedulingRuleVo = schedulingRuleService.getSchedulingRuleVoByStoreId(storeId);

        return R.ok().addData("schedulingRuleVo", schedulingRuleVo);
    }

    /**
     * 根据id获取排班规则
     */
    @RequestMapping("/getSchedulingRuleVoById")
    @PreAuthorize("hasAuthority('bnt.ruleSet.list')")
    public R getSchedulingRuleVoById(@RequestParam("ruleId") Long ruleId) {
        SchedulingRuleVo schedulingRuleVo = schedulingRuleService.getSchedulingRuleVoByRuleId(ruleId);

        return R.ok().addData("schedulingRuleVo", schedulingRuleVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @PreAuthorize("hasAuthority('bnt.ruleSet.add')")
    @OperationLog(title = SchedulingRuleController.title, businessType = BusinessTypeEnum.UPDATE, detail = "新增门店排班规则")
    //todo 幂等性
    public R save(@RequestBody SchedulingRuleEntity schedulingRule, HttpServletRequest httpServletRequest) {
        Long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));

        schedulingRuleService.saveRule( schedulingRule, storeId);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @PreAuthorize("hasAuthority('bnt.ruleSet.update')")
    @OperationLog(title = SchedulingRuleController.title, businessType = BusinessTypeEnum.UPDATE, detail = "修改门店排班规则")
    public R update(@RequestBody SchedulingRuleEntity schedulingRule) {
        schedulingRuleService.updateById(schedulingRule);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @PreAuthorize("hasAuthority('bnt.ruleSet.delete')")
    @OperationLog(title = SchedulingRuleController.title, businessType = BusinessTypeEnum.UPDATE, detail = "删除门店排班规则")
    public R delete(@RequestBody Long[] ids) {
        schedulingRuleService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
