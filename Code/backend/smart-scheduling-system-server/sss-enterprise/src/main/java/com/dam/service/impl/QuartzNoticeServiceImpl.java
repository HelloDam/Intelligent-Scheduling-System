package com.dam.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.dam.constant.RabbitMqConstant;
import com.dam.feign.ShiftSchedulingCalculateFeignService;
import com.dam.feign.SystemFeignService;
import com.dam.feign.ThirdPartyFeignService;
import com.dam.model.dto.scheduling_calculate_service.StaffWorkDto;
import com.dam.model.dto.third_party.EmailDto;
import com.dam.model.entity.enterprise.EnterpriseEntity;
import com.dam.model.entity.enterprise.MessageEntity;
import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.model.entity.enterprise.UserMessageEntity;
import com.dam.model.entity.shiftScheduling.SchedulingShiftEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.system.UserInfoVo;
import com.dam.service.*;
import com.dam.utils.mail.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * 用来发送通知
 */
@Slf4j
@Service
public class QuartzNoticeServiceImpl implements QuartzNoticeService {
    @Autowired
    private ShiftSchedulingCalculateFeignService shiftSchedulingCalculateFeignService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;
    @Autowired
    private SystemFeignService systemFeignService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送工作通知
     *
     * @param storeId
     * @param workNoticeType 工作通知方式 0：系统发送消息 1：发送邮件 2：系统发送消息及发送邮件
     */
    @Override
    public void sendWorkNotice(Long storeId, Integer workNoticeType) {
        log.info("发送工作通知");
        ////查询出明天有工作的员工id
        //当天日期
        LocalDate today = LocalDate.now();
        LocalDate secondDay = today.plusDays(1);
        // 将LocalDate转换为Date
        Date secondDate = Date.from(secondDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("date", secondDate);
        paramMap.put("storeId", storeId);
//        System.out.println("paramMap:"+paramMap);

        ////先判断那天是否需要上班
        R r1 = shiftSchedulingCalculateFeignService.judgeOneDateIsRest(paramMap);
        Boolean isRest = r1.getData("isRest", new TypeReference<Boolean>() {
        });
        System.out.println("sendWorkNotice,isRest:" + isRest);
        System.out.println("sendWorkNotice,workNoticeType:" + workNoticeType);
        if (isRest == false) {
            ///查询出企业和门店信息
            StoreEntity store = storeService.getById(storeId);
            EnterpriseEntity enterprise = enterpriseService.getById(store.getEnterpriseId());

            ///查询出需要上班的员工及其上班时间
            R r2 = shiftSchedulingCalculateFeignService.listStaffWorkDtoByWorkDate(paramMap);
            List<StaffWorkDto> staffWorkDtoList = r2.getData("staffWorkDtoList", new TypeReference<List<StaffWorkDto>>() {
            });
//            System.out.println("需要工作的员工："+userIdList.toString());

            String subject = "智能排班系统——明日上班通知";
            String contentStart = "<html>\n" +
                    "<head>\n" +
                    "    <style>\n" +
                    "        /* Set background color */\n" +
                    "        body {\n" +
                    "            background-color: #f2f2f2;\n" +
                    "        }\n" +
                    "\n" +
                    "        /* Add background image */\n" +
                    "        /* .background-image {\n" +
                    "            background-image: url('https://example.com/background-image.jpg');\n" +
                    "            background-repeat: no-repeat;\n" +
                    "            background-position: center;\n" +
                    "            background-size: cover;\n" +
                    "            opacity: 0.7;\n" +
                    "            position: absolute;\n" +
                    "            top: 0;\n" +
                    "            left: 0;\n" +
                    "            width: 100%;\n" +
                    "            height: 100%;\n" +
                    "            z-index: -1;\n" +
                    "        } */\n" +
                    "\n" +
                    "        /* Style for the content */\n" +
                    "        h1 {\n" +
                    "            color: #0b0e67;\n" +
                    "            text-align: center;\n" +
                    "            margin-top: 50px;\n" +
                    "        }\n" +
                    "\n" +
                    "        p {\n" +
                    "            font-size: 16px;\n" +
                    "            line-height: 1.5;\n" +
                    "            text-align: justify;\n" +
                    "            margin: 20px auto;\n" +
                    "            max-width: 700px;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <!-- Add a background image div -->\n" +
                    "    <div class=\"background-image\"></div>\n" +
                    "    <h2>门店上班通知</h2>\n" +
                    "    <div style=\"display: flex;justify-content: center;\">\n" +
                    "        <div>\n" +
                    "            <p>尊敬的员工：</p>\n" +
                    "            <p>&emsp;&emsp;您好！明天是我们门店的营业日，您被排班上班。我们特此通知您，希望您今晚早点休息，保持良好的精神状态，明天按时上班。\n" +
                    "                请您在明天早上提前留出足够的时间，为自己安排好交通和出行计划，避免迟到的情况发生。为了保证门店正常的运营和服务，我们希望您能够严格遵守上班时间和规定。\n" +
                    "                我们希望您能够认真对待自己的工作，为门店的发展和顾客的服务贡献自己的力量。如果您有任何疑问或需要帮助，请随时联系门店管理员。谢谢您的支持和合作！具体排班信息如下：</p>\n";

            String contentEnd = "            <p style=\"text-align: right;\">祝好！</p>\n" +
                    "            <p style=\"text-align: right;\">" + enterprise.getName() + "——" + store.getName() + "</p>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>\n";

            List<String> shiftMessageList = new ArrayList<>();
            List<Long> userIdList = new ArrayList<>();
            Map<Long, String> userIdAndMessageMap = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            for (int i = 0; i < staffWorkDtoList.size(); i++) {
                userIdList.add(staffWorkDtoList.get(i).getUserId());
                StringBuilder stringBuilder = new StringBuilder();
                for (SchedulingShiftEntity shift : staffWorkDtoList.get(i).getShiftEntityList()) {
                    String str = "";
                    if (shift.getMealType() == 0) {
                        str = "安排午餐";
                    } else if (shift.getMealType() == 0) {
                        str = "安排晚餐";
                    }
                    stringBuilder.append("<p>上班时间：" + sdf.format(shift.getStartDate()) + "至" + sdf.format(shift.getEndDate()) + str + "</p>\n");
                }
                shiftMessageList.add(stringBuilder.toString());
                userIdAndMessageMap.put(staffWorkDtoList.get(i).getUserId(), stringBuilder.toString());
            }
//            System.out.println("userIdList:" + userIdList.toString());

            ///系统发送消息
            if (workNoticeType == 0 || workNoticeType == 2) {
                System.out.println("sendWorkNotice，系统发送消息");

                //存储用户和消息的绑定关系
                List<UserMessageEntity> userMessageEntityList = new ArrayList<>();
                for (int i = 0; i < staffWorkDtoList.size(); i++) {
                    //存储消息
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setType(2);
                    messageEntity.setSubject(subject);
                    messageEntity.setContent(contentStart + shiftMessageList.get(i) + contentEnd);
                    messageEntity.setStoreId(storeId);
                    messageEntity.setEnterpriseId(null);
                    messageEntity.setIsPublish(1);
                    messageEntity.setPublishTime(new Date());
                    messageService.save(messageEntity);

                    UserMessageEntity userMessageEntity = new UserMessageEntity();
                    userMessageEntity.setUserId(staffWorkDtoList.get(i).getUserId());
                    userMessageEntity.setMessageId(messageEntity.getId());
                    userMessageEntityList.add(userMessageEntity);
                }
                userMessageService.saveBatch(userMessageEntityList);
            }

            ///发送邮件
            if (workNoticeType == 1 || workNoticeType == 2) {
                System.out.println("sendWorkNotice，发送邮件");
                R r3 = systemFeignService.getUserIdAndMailMapByUserIdList(userIdList);
                Map<Long, String> userIdAndMailMap = r3.getData("userIdAndMailMap", new TypeReference<Map<Long, String>>() {
                });
//                System.out.println("userIdAndMailMap:" + userIdAndMailMap.toString());
                HashSet<String> mailSet = new HashSet<>();
//                System.out.println("mailList:" + mailList.toString());
                for (Long userId : userIdList) {
                    String mail = userIdAndMailMap.get(userId);
                    if (MailUtil.judgeWhetherTheMailIsLegal(mail) == false || mailSet.contains(mail)) {
                        continue;
                    }
                    mailSet.add(mail);
                    System.out.println("正在给" + mail + "发送邮件");
                    EmailDto emailDto = new EmailDto();
                    emailDto.setTo(mail);
                    emailDto.setSubject(subject);
                    emailDto.setContent(contentStart + userIdAndMessageMap.get(userId) + contentEnd);
                    emailDto.setType(1);
                    //发送消息到邮件发送
                    rabbitTemplate.convertAndSend(RabbitMqConstant.MAIL_SENDER_EXCHANGE, RabbitMqConstant.MAIL_SENDER_ROUTER_KEY, emailDto);
                }
            }
        }
    }

    /**
     * 发送消息通知
     *
     * @param storeId
     * @param workNoticeType
     */
    @Override
    public void sendRestNotice(Long storeId, Integer workNoticeType) {
        log.info("发送休息通知");
        ////查询出明天有工作的员工id
        //当天日期
        LocalDate today = LocalDate.now();
        LocalDate secondDay = today.plusDays(1);
        // 将LocalDate转换为Date
        Date secondDate = Date.from(secondDay.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("date", secondDate);
        paramMap.put("storeId", storeId);

        ////判断明天是否休息
        R r1 = shiftSchedulingCalculateFeignService.judgeOneDateIsRest(paramMap);
        Boolean isRest = r1.getData("isRest", new TypeReference<Boolean>() {
        });

        if (isRest == true) {
            ///查询出企业和门店信息
            StoreEntity store = storeService.getById(storeId);
            EnterpriseEntity enterprise = enterpriseService.getById(store.getEnterpriseId());

            String subject = "智能排班系统——明日休息通知";
            String content = "<html>\n" +
                    "<head>\n" +
                    "    <style>\n" +
                    "        /* Set background color */\n" +
                    "        body {\n" +
                    "            background-color: #f2f2f2;\n" +
                    "        }\n" +
                    "\n" +
                    "        /* Style for the content */\n" +
                    "        h1 {\n" +
                    "            color: #0b0e67;\n" +
                    "            text-align: center;\n" +
                    "            margin-top: 50px;\n" +
                    "        }\n" +
                    "\n" +
                    "        p {\n" +
                    "            font-size: 16px;\n" +
                    "            line-height: 1.5;\n" +
                    "            text-align: justify;\n" +
                    "            margin: 20px auto;\n" +
                    "            max-width: 700px;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <!-- Add a background image div -->\n" +
                    "    <div class=\"background-image\"></div>\n" +
                    "    <h2>门店休息通知</h2>\n" +
                    "    <div style=\"display: flex;justify-content: center;\">\n" +
                    "        <div>\n" +
                    "            <p>尊敬的员工：</p>\n" +
                    "            <p>&emsp;&emsp;        您好！明天我们门店将休息一天，为了让大家更好地调整状态和享受生活，我们特此通知您好好休息，做自己喜欢做的事情，放松心情，愉快度过这个美好的假期。\n" +
                    "            <p style=\"text-align: right;\">祝好！</p>\n" +
                    "            <p style=\"text-align: right;\">" + enterprise.getName() + "——" + store.getName() + "</p>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>\n";

            ///查出门店的所有员工
            R r2 = systemFeignService.listUserEntityByStoreId(storeId);
            if (r2.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                List<UserEntity> userList = r2.getData("userList", new TypeReference<List<UserEntity>>() {
                });
                List<Long> userIdList = new ArrayList<>();
                List<String> mailList = new ArrayList<>();
                for (UserEntity userInfoVo : userList) {
                    userIdList.add(userInfoVo.getId());
                    mailList.add(userInfoVo.getMail());
                }
                ///系统发送消息
                if (workNoticeType == 0 || workNoticeType == 2) {
                    ///存储消息
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setType(2);
                    messageEntity.setSubject(subject);
                    messageEntity.setContent(content);
                    messageEntity.setStoreId(storeId);
                    messageEntity.setEnterpriseId(null);
                    messageEntity.setIsPublish(1);
                    messageEntity.setPublishTime(new Date());
                    messageService.save(messageEntity);
                    ///存储用户和消息的绑定关系
                    List<UserMessageEntity> userMessageEntityList = new ArrayList<>();
                    for (Long userId : userIdList) {
                        UserMessageEntity userMessageEntity = new UserMessageEntity();
                        userMessageEntity.setUserId(userId);
                        userMessageEntity.setMessageId(messageEntity.getId());
                        userMessageEntityList.add(userMessageEntity);
                    }
                    userMessageService.saveBatch(userMessageEntityList);
                }

                ///发送邮件
                if (workNoticeType == 1 || workNoticeType == 2) {
                    HashSet<String> mailSet=new HashSet<>();
                    for (String mail : mailList) {
                        mailSet.add(mail);
                    }
                    for (String mail : mailSet) {
                        if (MailUtil.judgeWhetherTheMailIsLegal(mail) == false) {
                            continue;
                        }
                        EmailDto emailDto = new EmailDto();
                        emailDto.setTo(mail);
                        emailDto.setSubject(subject);
                        emailDto.setContent(content);
                        emailDto.setType(1);
                        //发送消息到邮件发送
                        rabbitTemplate.convertAndSend(RabbitMqConstant.MAIL_SENDER_EXCHANGE, RabbitMqConstant.MAIL_SENDER_ROUTER_KEY, emailDto);
                    }
                }
            }
        }
    }
}
