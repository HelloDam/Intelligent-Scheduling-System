package com.dam.controller;

import java.util.Arrays;
import java.util.Map;

import com.dam.model.entity.enterprise.UserMessageEntity;
import com.dam.model.result.R;
import com.dam.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dam.service.UserMessageService;



/**
 * 用户-消息中间表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-20 15:43:46
 */
@RestController
@RequestMapping("/enterprise/userMessage")
public class UserMessageController {
    @Autowired
    private UserMessageService userMessageService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userMessageService.queryPage(params);

        return R.ok().addData("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		UserMessageEntity userMessage = userMessageService.getById(id);

        return R.ok().addData("userMessage", userMessage);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody UserMessageEntity userMessage){
		userMessageService.save(userMessage);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody UserMessageEntity userMessage){
		userMessageService.updateById(userMessage);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteBatch")
    public R deleteBatch(@RequestBody Long[] ids){
		userMessageService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
