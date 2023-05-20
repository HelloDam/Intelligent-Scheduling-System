package com.dam.model.vo.system;

import com.dam.model.entity.BaseEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class UserInfoVo extends BaseEntity {
    /**
     * 姓名
     */
    private String name;
    /**
     * 职位
     */
    private Long positionId;
    /**
     * 职位名称
     */
    private String positionName;
    /**
     * 电话
     */
    private String phone;
    /**
     * 邮箱
     */
    private String mail;
    /**
     * 企业id
     */
    private Long enterpriseId;
    /**
     * 企业名称
     */
    private String enterpriseName;
    /**
     * 门店id
     */
    private Long storeId;
    /**
     * 门店名称
     */
    private String storeName;
    /**
     * 用户类型 0：系统管理员 1：企业管理员 2：门店管理员 10：普通用户
     */
    private Integer type;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
//    private String password;
    /**
     * 微信登录所提供的id
     */
    private String openid;
    /**
     * 微信名
     */
    private String wechatName;
    /**
     * 微信头像
     */
    private String wechatAvatar;
    /**
     * 用户所绑定的qq
     */
    private String qq;
    /**
     * 昵称（注册时默认设置用户名）
     */
    private String nickname;
    /**
     * 性别 0：男 1：女
     */
    private Integer gender;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 头像（用户不设置就给一个默认头像）
     */
    private String avatar;
    /**
     * 用户状态 0：正常状态 1：封禁状态
     */
    private Integer status;
    /**
     * 工作日偏好（喜欢星期几工作1|3|4喜欢星期一、三、四工作），缺省为全部
     */
    private List<Integer> workDayPreferenceList;
    /**
     * 工作时间偏好（1:00~3.00|5.00~8.00|17.00~21.00），缺省为全部）
     */
    private String workTimePreference;
    /**
     * 班次时长偏好（3|38：三小时38分钟）
     */
    /**
     * 每天班次时长偏好
     */
    private String shiftLengthPreferenceOneDay;
    /**
     * 每周班次时长偏好
     */
    private String shiftLengthPreferenceOneWeek;
    /**
     * 用户是否繁忙
     */
    private Boolean isBusy;
}
