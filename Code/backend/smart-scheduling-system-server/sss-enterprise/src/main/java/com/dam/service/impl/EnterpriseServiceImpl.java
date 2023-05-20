package com.dam.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.dam.constant.RabbitMqConstant;
import com.dam.feign.SystemFeignService;
import com.dam.model.dto.third_party.EmailDto;
import com.dam.model.entity.enterprise.EnterpriseEntity;
import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.entity.system.UserRoleEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.enums.system.UserCodeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.enterprise.EnterpriseVo;
import com.dam.service.StoreService;
import com.dam.utils.EncryptionUtil;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;
import com.dam.utils.date.DateUtil;
import com.dam.utils.password.PasswordUtil;
import com.dam.utils.username.UsernameUtil;
import com.dam.utils.vo.EnterpriseRegisterVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dam.dao.EnterpriseDao;
import com.dam.service.EnterpriseService;
import org.springframework.transaction.annotation.Transactional;


@Service("enterpriseService")
public class EnterpriseServiceImpl extends ServiceImpl<EnterpriseDao, EnterpriseEntity> implements EnterpriseService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SystemFeignService systemFeignService;
    @Autowired
    private StoreService storeService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EnterpriseEntity> page = this.page(
                new Query<EnterpriseEntity>().getPage(params),
                new QueryWrapper<EnterpriseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public Boolean passEnterpriseRegister(EnterpriseRegisterVo enterpriseRegisterVo) {
        ////保存企业数据
        EnterpriseEntity enterpriseEntity = new EnterpriseEntity();
        enterpriseEntity.setName(enterpriseRegisterVo.getName());
        enterpriseEntity.setDetail(enterpriseRegisterVo.getDetail());
        enterpriseEntity.setLogo(enterpriseRegisterVo.getLogo());
        baseMapper.insert(enterpriseEntity);

        ////创建企业用户
        UserEntity userEntity = new UserEntity();
        userEntity.setName(enterpriseRegisterVo.getName() + "管理员");
        userEntity.setMail(enterpriseRegisterVo.getMail());
        userEntity.setEnterpriseId(enterpriseEntity.getId());
        userEntity.setType(UserCodeEnum.TYPE_ENTERPRISE_MANAGER.getCode());
        String username = UsernameUtil.generateUsername();
        userEntity.setUsername(username);
        String password = PasswordUtil.generatePassword();
        userEntity.setPassword(EncryptionUtil.saltMd5Encrypt(password));
        userEntity.setNickname(enterpriseRegisterVo.getName() + "管理员");
        userEntity.setAvatar(enterpriseRegisterVo.getLogo());
        userEntity.setStatus(0);
        R r = systemFeignService.directSave(userEntity);

        ////发送邮件通知企业申请负责人
        String emailContent = "<html><head><meta charset=\"UTF-8\"><title>企业注册成功通知</title><style type=\"text/css\">"
                + "body {font-family: Arial, sans-serif;font-size: 14px;line-height: 1.5;color: #333333;background-color: #f5f5f5;padding: 20px;}"
                + "h1 {font-size: 20px;color: #333333;margin-top: 0;margin-bottom: 20px;}"
                + "p {margin-top: 0;margin-bottom: 20px;}"
                + ".btn {display: inline-block;background-color: #4CAF50;color: white;padding: 10px 20px;text-align: center;text-decoration: none;font-size: 16px;border-radius: 5px;border: none;}"
                + ".btn:hover {background-color: #3e8e41;}"
                + "</style></head><body>"
                + "<div style=\"display: flex;justify-content: center;\">\n"
                + "<div>\n"
                + "<h1>智能排班系统 - 企业注册成功</h1>"
                + "<p>尊敬的用户：</p>"
                + "<div>&emsp;&emsp;恭喜您的企业已经成功注册使用智能排班系统，请保护好您的账号信息，不要泄露出去，如有其他使用疑问，请发送邮件咨询管理员。以下是您的企业管理账号信息：</div>"
                + "<div style=' display: flex; justify-content: center;'><ul><li>用户名：<strong>" + username + "</strong></li><li>密码：<strong>" + password + "</strong></li></ul></div>"
//                + "<p><a href=\"#\" class=\"btn\">登录系统</a></p>"
                + "</div>\n"
                + "</div>\n"
                + "</body></html>";
        EmailDto emailDto = new EmailDto();
        emailDto.setSubject("智能排班系统 - 企业注册成功");
        emailDto.setTo(enterpriseRegisterVo.getMail());
        emailDto.setContent(emailContent);
        emailDto.setType(1);

        //发送消息到邮件发送
        rabbitTemplate.convertAndSend(RabbitMqConstant.MAIL_SENDER_EXCHANGE, RabbitMqConstant.MAIL_SENDER_ROUTER_KEY, emailDto);

        return true;
    }

    @Override
    public Boolean rejectEnterpriseRegister(EnterpriseRegisterVo enterpriseRegisterVo) {

        ////发送邮件通知企业申请负责人
        String reason = enterpriseRegisterVo.getRejectReason() != null ? "注册失败的原因是：" + enterpriseRegisterVo.getRejectReason() + '。' : "";
        String emailContent = "<html><head><meta charset=\"UTF-8\"><title>企业注册成功通知</title><style type=\"text/css\">"
                + "body {font-family: Arial, sans-serif;font-size: 14px;line-height: 1.5;color: #333333;background-color: #f5f5f5;padding: 20px;}"
                + "h1 {font-size: 20px;color: #333333;margin-top: 0;margin-bottom: 20px;}"
                + "p {margin-top: 0;margin-bottom: 20px;}"
                + ".btn {display: inline-block;background-color: #4CAF50;color: white;padding: 10px 20px;text-align: center;text-decoration: none;font-size: 16px;border-radius: 5px;border: none;}"
                + ".btn:hover {background-color: #3e8e41;}"
                + "</style></head><body>"
                + "<div style=\"display: flex;justify-content: center;\">\n"
                + "<div style='max-width=600px'>\n"
                + "<h1>智能排班系统 - 企业注册失败</h1>"
                + "<p>尊敬的用户：</p>"
                + "<div>&emsp;&emsp;尊敬的用户，我们非常抱歉地通知您，您的企业注册智能排班系统未能成功。我们了解您的期望，也感到非常遗憾无法为您提供该服务。"
                + reason
                + "如果您有任何疑问或需要进一步的帮助，请不要犹豫，随时联系我们。您可以发送电子邮件给我们的管理员，我们将非常乐意为您提供帮助。再次感谢您的关注和支持！</div>"
                + "</div>\n"
                + "</div>\n"
                + "</body></html>";
        EmailDto emailDto = new EmailDto();
        emailDto.setSubject("智能排班系统 - 企业注册失败");
        emailDto.setTo(enterpriseRegisterVo.getMail());
        emailDto.setContent(emailContent);
        emailDto.setType(1);

        //发送消息到邮件发送
        rabbitTemplate.convertAndSend(RabbitMqConstant.MAIL_SENDER_EXCHANGE, RabbitMqConstant.MAIL_SENDER_ROUTER_KEY, emailDto);

        return true;
    }

    @Override
    public List<EnterpriseVo> buildEnterpriseVoList(List<EnterpriseEntity> enterpriseEntityList) {
        List<EnterpriseVo> enterpriseVoList = new ArrayList<>();
        List<Long> enterpriseIdList = new ArrayList<>();
        for (EnterpriseEntity enterpriseEntity : enterpriseEntityList) {
            EnterpriseVo enterpriseVo = new EnterpriseVo();
            BeanUtils.copyProperties(enterpriseEntity, enterpriseVo);
            //查询企业的门店数量
            enterpriseVo.setStoreNum(storeService.count(new QueryWrapper<StoreEntity>().eq("enterprise_id", enterpriseEntity.getId())));
            //查询企业的用户数量
            enterpriseIdList.add(enterpriseEntity.getId());
            enterpriseVoList.add(enterpriseVo);
        }
        R r = systemFeignService.getEnterpriseIdAndUserNumMap(enterpriseIdList);
        if (r.getCode()==ResultCodeEnum.SUCCESS.getCode().intValue()){
            HashMap<Long, Integer> enterpriseIdAndUserNumMap = r.getData("enterpriseIdAndUserNumMap", new TypeReference<HashMap<Long, Integer>>() {
            });
            for (EnterpriseVo enterpriseVo : enterpriseVoList) {
                enterpriseVo.setUserNum(enterpriseIdAndUserNumMap.get(enterpriseVo.getId()));
            }
        }

        return enterpriseVoList;
    }

    /**
     * 获取指定年份每个月的企业注册数量
     * @param year
     * @return
     */
    @Override
    public List<Integer> getRegisterEnterpriseNumOfYear(Integer year) {
        List<Integer> enterpriseRegisterNumList = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Date[] dateArr = DateUtil.getStartAndEndDateOfMonth(year, month);
            if (dateArr[0].getTime() >System.currentTimeMillis()) {
                //--if--如果月份的起始日期已经大于当前日期，那肯定是没有任务了
                enterpriseRegisterNumList.add(0);
            } else {
                enterpriseRegisterNumList.add(baseMapper.selectCount(new QueryWrapper<EnterpriseEntity>().ge("create_time", dateArr[0]).le("create_time", dateArr[1])));
            }
        }
        return enterpriseRegisterNumList;
    }


}