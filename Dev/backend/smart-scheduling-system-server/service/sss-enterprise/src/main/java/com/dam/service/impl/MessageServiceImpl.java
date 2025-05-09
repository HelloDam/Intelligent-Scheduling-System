package com.dam.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dam.dao.MessageDao;
import com.dam.feign.SystemFeignService;
import com.dam.feign.ThirdPartyFeignService;
import com.dam.model.dto.third_party.EmailDto;
import com.dam.model.entity.enterprise.MessageEntity;
import com.dam.model.entity.enterprise.UserMessageEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.enterprise.MessageItemVo;
import com.dam.service.MessageService;
import com.dam.service.UserMessageService;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;
import com.dam.utils.mail.MailUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("messageService")
public class MessageServiceImpl extends ServiceImpl<MessageDao, MessageEntity> implements MessageService {
    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;
    @Autowired
    private SystemFeignService systemFeignService;
    @Autowired
    private UserMessageService userMessageService;

    /**
     * 发送邮件消息
     *
     * @param userIdList
     * @param message
     */
    @Override
    public void sendMailMessage(List<Long> userIdList, String subject, String message, Integer type) {
        R r = systemFeignService.listAllMailByUserIdList(userIdList);
        if (r.getCode() != ResultCodeEnum.SUCCESS.getCode().intValue()) {
            return;
        }
        List<String> mailList = r.getData("mailList", new TypeReference<List<String>>() {
        });
        for (String mail : mailList) {
            if (MailUtil.judgeWhetherTheMailIsLegal(mail) == false) {
                continue;
            }

            EmailDto emailDto = new EmailDto();
            emailDto.setTo(mail);
            emailDto.setSubject(subject);
            emailDto.setContent(message);
            emailDto.setType(type);
            thirdPartyFeignService.send(emailDto);
        }
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, QueryWrapper<MessageEntity> queryWrapper) {

        IPage<MessageEntity> page = this.page(
                new Query<MessageEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void updateMessagePublishStatus(Long messageId, Integer isPublish) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setIsPublish(isPublish);
        if (isPublish == 1) {
            messageEntity.setPublishTime(new Date());
        } else {
            messageEntity.setPublishTime(null);
        }
        messageEntity.setId(messageId);
        baseMapper.updateById(messageEntity);
    }

    @Override
    public List<MessageItemVo> listMessageOfUser(Long userId, Long enterpriseId, Long storeId) {
        List<MessageEntity> messageEntityList = new ArrayList<>();

        //查询企业公开的消息
        messageEntityList.addAll(baseMapper.selectList(new QueryWrapper<MessageEntity>().eq("type", 0).eq("enterprise_id", enterpriseId).eq("is_publish", 1)));

        //查询门店公开的消息
        messageEntityList.addAll(baseMapper.selectList(new QueryWrapper<MessageEntity>().eq("type", 1).eq("store_id", storeId).eq("is_publish", 1)));

        //查询给用户看的消息
        List<UserMessageEntity> userMessageEntityList = userMessageService.list(new QueryWrapper<UserMessageEntity>().eq("user_id", userId));
        List<Long> messageIdList = userMessageEntityList.stream().map(UserMessageEntity::getMessageId).collect(Collectors.toList());
        if (messageIdList.size() > 0) {
            messageEntityList.addAll(baseMapper.selectList(new QueryWrapper<MessageEntity>().in("id", messageIdList)));
        }

        //对消息按照时间升序排序
        Collections.sort(messageEntityList, (o1, o2) -> {
            long o1Time = o1.getPublishTime().getTime();
            long o2Time = o2.getPublishTime().getTime();
            return Long.compare(o1Time, o2Time);
        });

        //封装vo
        List<MessageItemVo> messageItemVoList = this.buildMessageItemVoList(messageEntityList);

        return messageItemVoList;
    }

    /**
     * 封装信息vo
     *
     * @param messageEntityList
     * @return
     */
    @Override
    public List<MessageItemVo> buildMessageItemVoList(List<MessageEntity> messageEntityList) {
        HashSet<Long> userIdSet = new HashSet<>();
        List<MessageItemVo> messageItemVoList = new ArrayList<>();
        for (MessageEntity messageEntity : messageEntityList) {
            if (messageEntity.getCreateUserId()!=null&&messageEntity.getCreateUserId() != -1) {
                userIdSet.add(messageEntity.getCreateUserId());
            }
            if (messageEntity.getPublishUserId()!=null&&messageEntity.getPublishUserId() != -1) {
                userIdSet.add(messageEntity.getPublishUserId());
            }

            MessageItemVo messageItemVo = new MessageItemVo();
            BeanUtils.copyProperties(messageEntity, messageItemVo);
            messageItemVoList.add(messageItemVo);
        }
        R r = systemFeignService.getUserMapByIdList(new ArrayList<>(userIdSet));
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            Map<Long, UserEntity> idAndUserEntityMap = r.getData("idAndUserEntityMap", new TypeReference<Map<Long, UserEntity>>() {
            });
            for (MessageItemVo messageItemVo : messageItemVoList) {
                if (messageItemVo.getPublishUserId()!=null&&messageItemVo.getPublishUserId() != -1) {
                    UserEntity userEntity = idAndUserEntityMap.get(messageItemVo.getPublishUserId());
                    messageItemVo.setPublishUsername(userEntity.getUsername());
                    messageItemVo.setPublishName(userEntity.getName());
                    messageItemVo.setPublishAvatar(userEntity.getAvatar());
                } else {
                    messageItemVo.setPublishUsername("小智");
                    messageItemVo.setPublishName("小智");
                    messageItemVo.setPublishAvatar("https://smart-scheduling-system-13184.oss-cn-beijing.aliyuncs.com/2023-03-29/734f0211-9425-4e9e-8437-d864ffed4d5f_logo3_collapse.png");
                }
            }
        }
        return messageItemVoList;
    }
}
