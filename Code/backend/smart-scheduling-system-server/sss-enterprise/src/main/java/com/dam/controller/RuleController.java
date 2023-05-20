package com.dam.controller;

import java.util.Arrays;
import java.util.Map;

import com.dam.annotation.OperationLog;
import com.dam.model.enums.log.BusinessTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dam.model.entity.enterprise.RuleEntity;
import com.dam.service.RuleService;
import com.dam.utils.PageUtils;
import com.dam.model.result.R;



/**
 * 规则表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
@RestController
@RequestMapping("enterprise/rule")
public class RuleController {
    @Autowired
    private RuleService ruleService;
    private static final String title = "规则管理";

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = ruleService.queryPage(params);

        return R.ok().addData("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		RuleEntity rule = ruleService.getById(id);

        return R.ok().addData("rule", rule);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @OperationLog(title = RuleController.title, businessType = BusinessTypeEnum.INSERT, detail = "增加规则")
    public R save(@RequestBody RuleEntity rule){
		ruleService.save(rule);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @OperationLog(title = RuleController.title, businessType = BusinessTypeEnum.UPDATE, detail = "修改规则")
    public R update(@RequestBody RuleEntity rule){
		ruleService.updateById(rule);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteBatch")
    @OperationLog(title = RuleController.title, businessType = BusinessTypeEnum.DELETE, detail = "删除规则")
    public R delete(@RequestBody Long[] ids){
		ruleService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
