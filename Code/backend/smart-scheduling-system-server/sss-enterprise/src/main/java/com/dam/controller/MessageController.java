package com.dam.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.annotation.OperationLog;
import com.dam.model.entity.enterprise.MessageEntity;
import com.dam.model.enums.log.BusinessTypeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.enterprise.MessageItemVo;
import com.dam.service.MessageService;
import com.dam.utils.JwtUtil;
import com.dam.utils.PageUtils;
import com.dam.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 发布信息
 */
@RestController
@RequestMapping("/enterprise/message")
public class MessageController {
    @Autowired
    private MessageService messageService;
    private static final String title = "消息管理";

    /**
     * 发送消息通知用户
     *
     * @return
     */
    @PostMapping("/sendMesToUserList")
    public R sendMesToUserList(@RequestBody Map<String, Object> paramMap) {
        //前端传过来的是int
        String userIdListJson = JSON.toJSONString(paramMap.get("userIdList"));
        List<Long> userIdList = JSON.parseObject(userIdListJson, new TypeReference<List<Long>>() {
        });
        String subject = paramMap.get("subject").toString();
        String message = paramMap.get("message").toString();
        Integer type = Integer.parseInt(paramMap.get("type").toString());

        messageService.sendMailMessage(userIdList, subject, message, type);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    @PreAuthorize("hasAuthority('bnt.message.list')")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("token");
        Long storeId = Long.parseLong(JwtUtil.getStoreId(token));

        QueryWrapper<MessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("store_id", storeId);
        if (params.containsKey("key")) {
            String key = params.get("key").toString();
            if (!StringUtils.isEmpty(key)) {
                queryWrapper.like("subject", "%" + key + "%").or().like("content", "%" + key + "%");
            }
        }
        if (params.containsKey("isPublish")) {
            String isPublishStr = params.get("isPublish").toString();
            if (!StringUtils.isEmpty(isPublishStr)) {
                queryWrapper.eq("is_publish", isPublishStr);
            }
        }
        queryWrapper.eq("type", 1);

        PageUtils page = messageService.queryPage(params, queryWrapper);

        return R.ok().addData("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @PreAuthorize("hasAuthority('bnt.message.list')")
    public R info(@PathVariable("id") Long id) {
        MessageEntity message = messageService.getById(id);

        return R.ok().addData("message", message);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @PreAuthorize("hasAuthority('bnt.message.add')")
    @OperationLog(title = MessageController.title, businessType = BusinessTypeEnum.INSERT, detail = "新增消息")
    public R save(@RequestBody MessageEntity message, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("token");
        Long enterpriseId = Long.parseLong(JwtUtil.getEnterpriseId(token));
        Long storeId = Long.parseLong(JwtUtil.getStoreId(token));
        message.setEnterpriseId(enterpriseId);
        message.setStoreId(storeId);
        message.setType(1);
        messageService.save(message);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @PreAuthorize("hasAuthority('bnt.message.update')")
    @OperationLog(title = MessageController.title, businessType = BusinessTypeEnum.UPDATE, detail = "修改消息")
    public R update(@RequestBody MessageEntity message) {
        messageService.updateById(message);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteBatch")
    @PreAuthorize("hasAuthority('bnt.message.delete')")
    @OperationLog(title = MessageController.title, businessType = BusinessTypeEnum.DELETE, detail = "删除消息")
    public R deleteBatch(@RequestBody Long[] ids) {
        messageService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 修改任务的发布状态
     *
     * @param paramMap
     * @return
     */
    @PostMapping("/updateMessagePublishStatus")
    @PreAuthorize("hasAuthority('bnt.message.updateMessagePublishStatus')")
    @OperationLog(title = MessageController.title, businessType = BusinessTypeEnum.STATUS, detail = "修改消息的发布状态")
    public R updateMessagePublishStatus(@RequestBody Map<String, Object> paramMap) {
        Long messageId = Long.parseLong(paramMap.get("messageId").toString());
        Integer isPublish = Integer.parseInt(paramMap.get("isPublish").toString());
        messageService.updateMessagePublishStatus(messageId, isPublish);
        return R.ok();
    }

    /**
     * 查询用户接受到的所有信息
     *
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/listMessageOfUser")
    public R listMessageOfUser(HttpServletRequest httpServletRequest) {
        Long userId = Long.parseLong(JwtUtil.getUserId(httpServletRequest.getHeader("token")));
        Long enterpriseId = Long.parseLong(JwtUtil.getEnterpriseId(httpServletRequest.getHeader("token")));
        Long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        List<MessageItemVo> messageList = messageService.listMessageOfUser(userId,enterpriseId,storeId);
        return R.ok().addData("messageList", messageList);
    }

}
