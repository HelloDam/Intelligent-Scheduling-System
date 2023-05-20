package com.dam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.model.entity.enterprise.MessageEntity;
import com.dam.model.vo.enterprise.MessageItemVo;
import com.dam.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 消息发送服务
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-06 15:50:30
 */
public interface MessageService extends IService<MessageEntity> {

    /**
     * 发送邮件消息
     * @param userIdList
     * @param message
     */
    void sendMailMessage(List<Long> userIdList,String subject, String message, Integer type);

    PageUtils queryPage(Map<String, Object> params, QueryWrapper<MessageEntity> queryWrapper);

    void updateMessagePublishStatus(Long messageId, Integer isPublish);

    List<MessageItemVo> listMessageOfUser(Long userId, Long enterpriseId, Long storeId);

    List<MessageItemVo> buildMessageItemVoList(List<MessageEntity> messageEntityList);
}

