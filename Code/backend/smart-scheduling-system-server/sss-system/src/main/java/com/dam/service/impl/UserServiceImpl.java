package com.dam.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dam.exception.SSSException;
import com.dam.feign.EnterpriseFeignService;
import com.dam.feign.ShiftSchedulingCalculateFeignService;
import com.dam.model.entity.enterprise.EnterpriseEntity;
import com.dam.model.entity.enterprise.PositionEntity;
import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.model.entity.enterprise.UserPositionEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.enums.system.UserCodeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.system.UserInfoVo;
import com.dam.service.MenuService;
import com.dam.utils.*;
import com.dam.utils.date.DateUtil;
import com.dam.utils.vo.RouterVo;
import com.dam.utils.vo.SysUserQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dam.dao.UserDao;
import com.dam.model.entity.system.UserEntity;
import com.dam.service.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Autowired
    private MenuService menuService;
    @Autowired
    private EnterpriseFeignService enterpriseFeignService;
    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ShiftSchedulingCalculateFeignService shiftSchedulingCalculateFeignService;
    @Autowired
    private UserService userService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserEntity> page = this.page(
                new Query<UserEntity>().getPage(params),
                new QueryWrapper<UserEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public UserEntity getUserInfoByUsername(String username) {
        UserEntity userEntity = baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("username", username));
        return userEntity;
    }

    @Override
    public Map<String, Object> getUserInfo(String username) {
        System.out.println("getUserInfo----------------------------------------------------------------------------------------");
        //根据username查询用户基本信息
        UserEntity user = this.getUserInfoByUsername(username);
        //根据用户名查询菜单权限值
        List<RouterVo> routerVoList = menuService.getUserMenuList(user.getId());
        //根据userId查询按钮权限值
        List<String> buttons = menuService.getUserButtonList(user.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("name", username);
        result.put("avatar", user.getAvatar());
        result.put("roles", "[admin]");
        result.put("userType", user.getType());
        result.put("enterpriseId", user.getEnterpriseId());
        result.put("storeId", user.getStoreId());
        //菜单权限数据
        result.put("routers", routerVoList);
        //按钮权限数据
        result.put("buttons", buttons);
        return result;
    }

    /**
     * 如果其中一个条件相同，就将用户查出来
     *
     * @param searchMap
     * @return
     */
    @Override
    public UserEntity getUserByOneCondition(Map<String, String> searchMap) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        int count = 0;
        for (Map.Entry<String, String> entry : searchMap.entrySet()) {
            if (count == 0) {
                queryWrapper.eq(entry.getKey(), entry.getValue());
                count++;
            } else {
                queryWrapper.or().eq(entry.getKey(), entry.getValue());
            }
        }
        return null;
    }

    @Override
    public UserEntity getUserInfoByMail(String mail) {
        return baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("mail", mail));
    }

    @Override
    public void updateStatus(String id, Integer status) {
        UserEntity userEntity = baseMapper.selectById(id);
        userEntity.setStatus(status);
        baseMapper.updateById(userEntity);
    }

    /**
     * @param page
     * @param limit
     * @param enterpriseId 当前用户所在企业id
     * @param storeId      当前用户所在门店id
     * @param userQueryVo
     * @return
     */
    @Override
    public PageUtils selectPage(Long page, Long limit, Long enterpriseId, Long storeId, int userType, SysUserQueryVo userQueryVo) {
        Page<UserEntity> pageMps = new Page<>(page, limit);

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        if (userType == UserCodeEnum.TYPE_SYSTEM_MANAGER.getCode()) {
            //--if--如果用户是系统管理员，只查询企业管理员
            queryWrapper.eq("type", UserCodeEnum.TYPE_ENTERPRISE_MANAGER.getCode());
        } else if (userType == UserCodeEnum.TYPE_ENTERPRISE_MANAGER.getCode()) {
            //--if--如果用户是企业管理员，查询企业的所有用户
            queryWrapper.eq("enterprise_id", enterpriseId)
                    //不能查询企业管理员
                    .ne("type", UserCodeEnum.TYPE_ENTERPRISE_MANAGER.getCode())
                    //不能查询系统管理员
                    .ne("type", UserCodeEnum.TYPE_SYSTEM_MANAGER.getCode());
        } else if (userType == UserCodeEnum.TYPE_STORE_MANAGER.getCode()) {
            //--if--如果用户是企业管理员，查询门店的所有用户
            queryWrapper.eq("store_id", storeId)
                    //不能查询门店管理员
                    .ne("type", UserCodeEnum.TYPE_STORE_MANAGER.getCode())
                    //不能查询企业管理员
                    .ne("type", UserCodeEnum.TYPE_ENTERPRISE_MANAGER.getCode())
                    //不能查询系统管理员
                    .ne("type", UserCodeEnum.TYPE_SYSTEM_MANAGER.getCode());
        }
//        System.out.println("userQueryVo.getPositionIdArr():"+userQueryVo.getPositionIdArr());
        if (userQueryVo.getPositionIdArr() == null) {

        } else if (userQueryVo.getPositionIdArr().size() == 0) {
            //职位为空，一个用户都不用查
            queryWrapper.eq("id", -1);
        } else {
            //查出职位对应的所有用户Id
            List<Long> userIdList = enterpriseFeignService.listUserIdList(userQueryVo.getPositionIdArr());
            if (userIdList.size() > 0) {
                queryWrapper.in("id", userIdList);
            } else {
                queryWrapper.eq("id", -1);
            }
        }
        if (userQueryVo.getSearchStoreId() != null) {
            queryWrapper.in("store_id", userQueryVo.getSearchStoreId());
        }
        if (userQueryVo.getSearchUserType() != null) {
            queryWrapper.in("type", userQueryVo.getSearchUserType());
        }

        queryWrapper.orderByDesc("create_time");

        String keyword = userQueryVo.getKeyword();
        if (!StringUtils.isEmpty(keyword)) {
            queryWrapper.like("username", "%" + keyword + "%").or()
                    .like("name", "%" + keyword + "%").or()
                    .like("phone", "%" + keyword + "%").or()
                    .like("mail", "%" + keyword + "%");
        }

        ///查询用户的繁忙状态
        if (userQueryVo.getIsNeedSearchBusyStatus() != null && userQueryVo.getIsNeedSearchBusyStatus() == true && userQueryVo.getBusyStatus() != null) {
            //--if--需要查询用书在当前班次时间段内是否繁忙
            Date shiftStartDate = userQueryVo.getShiftStartDate();
            Date shiftEndDate = userQueryVo.getShiftEndDate();
            Map<String, Object> param = new HashMap<>();
            param.put("shiftStartDate", shiftStartDate);
            param.put("shiftEndDate", shiftEndDate);
            param.put("storeId", storeId);
            R r = shiftSchedulingCalculateFeignService.listUserIdIsBusy(param);
            if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                List<Long> userIdListIsBusy = r.getData("userIdListIsBusy", new TypeReference<List<Long>>() {
                });
                if (userQueryVo.getBusyStatus() == 0) {
                    //--if--需要查询空闲的用户
                    queryWrapper.notIn("id", userIdListIsBusy);
                } else {
                    //--if--需要查询繁忙的用户
                    queryWrapper.in("id", userIdListIsBusy);
                }
            }
        }

        ///查询日期段内有班次安排的员工
        if (userQueryVo.getStartDate() != null && userQueryVo.getEndDate() != null) {
            //查询日期段内有班次安排的员工id
            HashMap<String, Object> param = new HashMap<>();
            param.put("storeId", storeId);
            param.put("startDate", userQueryVo.getStartDate().toString());
            param.put("endDate", userQueryVo.getEndDate().toString());
            if (userQueryVo.getTaskId() != null) {
                param.put("taskId", userQueryVo.getTaskId());
            }
            R r = shiftSchedulingCalculateFeignService.listUserIdByDateSegment(param);
            if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                List<Long> userIdList = r.getData("userIdList", new TypeReference<List<Long>>() {
                });
                if (userIdList.size() > 0) {
                    queryWrapper.in("id", userIdList);
                }
            }
        }

        baseMapper.selectPage(pageMps, queryWrapper);
        return new PageUtils(pageMps);
    }

    /**
     * 根据userEntity构建出userInfoVo
     *
     * @param user
     * @return
     */
    @Override
    public UserInfoVo buildUserInfoVo(UserEntity user) {
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(user, userInfoVo);

        // 1.获取之前的请求头数据
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        //查询企业名称
        CompletableFuture<Void> enterpriseCompletableFuture = CompletableFuture.runAsync(() -> {
            //2.每一个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            if (user.getEnterpriseId() != null) {
                R r = enterpriseFeignService.getEnterpriseEntityById(user.getEnterpriseId());
                if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                    EnterpriseEntity enterprise = r.getData("enterprise", new TypeReference<EnterpriseEntity>() {
                    });
                    userInfoVo.setEnterpriseName(enterprise.getName());
                }
            }
        }, executor);

        //查询门店名称
        CompletableFuture<Void> storeCompletableFuture = CompletableFuture.runAsync(() -> {
            //2.每一个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            if (user.getStoreId() != null) {
                R r = enterpriseFeignService.getStoreEntityById(user.getStoreId());
                if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                    StoreEntity store = r.getData("store", new TypeReference<StoreEntity>() {
                    });
                    userInfoVo.setStoreName(store.getName());
                }
            }
        }, executor);


        //查询职位名称
        CompletableFuture<Void> positionCompletableFuture = CompletableFuture.runAsync(() -> {
            //2.每一个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R r1 = enterpriseFeignService.infoUserPositionByUserId(user.getId());
            if (r1.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                UserPositionEntity UserPositionEntity = r1.getData("userPosition", new TypeReference<UserPositionEntity>() {
                });
                if (UserPositionEntity != null) {
                    R r2 = enterpriseFeignService.getPositionEntityById(UserPositionEntity.getPositionId());
                    if (r2.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                        PositionEntity position = r2.getData("position", new TypeReference<PositionEntity>() {
                        });
                        userInfoVo.setPositionName(position.getName());
                        userInfoVo.setPositionId(position.getId());
                    }
                }
            }
        }, executor);

        //构建偏好数据
        CompletableFuture<Void> preferenceCompletableFuture = CompletableFuture.runAsync(() -> {
            //2.每一个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<Integer> workDayPreferenceList = new ArrayList<>();
            ///工作日偏好
            if (!StringUtils.isEmpty(user.getWorkDayPreference())) {
                String[] wordDayList = user.getWorkDayPreference().split("\\|");
                for (String s : wordDayList) {
                    workDayPreferenceList.add(Integer.parseInt(s));
                }
            }
            userInfoVo.setWorkDayPreferenceList(workDayPreferenceList);
        }, executor);

        try {
            CompletableFuture.allOf(enterpriseCompletableFuture,
                    storeCompletableFuture,
                    positionCompletableFuture,
                    preferenceCompletableFuture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return userInfoVo;
    }

    @Override
    public List<UserInfoVo> buildUserInfoVoList(List<UserEntity> userEntityList) {
        ////声明变量
        //存储所有企业Id
        HashSet<Long> enterpriseIdSet = new HashSet<>();
        //存储所有门店Id
        HashSet<Long> storeIdSet = new HashSet<>();
        //存储所有用户Id
        List<Long> userIdList = new ArrayList<>();
        //存储封装的用户信息集合
        List<UserInfoVo> userInfoVoList = new ArrayList<>();

        ////初始化数据
        for (UserEntity userEntity : userEntityList) {
//            if (userEntity.getEnterpriseId() == null || userEntity.getStoreId() == null) {
//                System.out.println("userEntity:"+userEntity.toString());
//                continue;
//            }
            enterpriseIdSet.add(userEntity.getEnterpriseId());
            userIdList.add(userEntity.getId());
            storeIdSet.add(userEntity.getStoreId());
            //复制基本信息
            UserInfoVo userInfoVo = new UserInfoVo();
            BeanUtils.copyProperties(userEntity, userInfoVo);
            //工作日偏好数据处理
            List<Integer> workDayPreferenceList = new ArrayList<>();
            if (!StringUtils.isEmpty(userEntity.getWorkDayPreference())) {
                String[] wordDayList = userEntity.getWorkDayPreference().split("\\|");
                for (String s : wordDayList) {
                    workDayPreferenceList.add(Integer.parseInt(s));
                }
            }

            userInfoVo.setWorkDayPreferenceList(workDayPreferenceList);
            userInfoVoList.add(userInfoVo);
        }

        // 1.获取之前的请求头数据
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ////查询所有用户所对应的企业名称
        CompletableFuture<Void> enterpriseCompletableFuture = CompletableFuture.runAsync(() -> {
            //2.每一个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            if (enterpriseIdSet.size() > 0) {
                System.out.println("查询所有用户所对应的企业名称");
                R r = enterpriseFeignService.getEnterpriseMapByIdList(new ArrayList<>(enterpriseIdSet));
                if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                    //存储企业id及其对应的企业
                    Map<Long, EnterpriseEntity> idAndEnterpriseEntityMap = r.getData("idAndEnterpriseEntityMap", new TypeReference<Map<Long, EnterpriseEntity>>() {
                    });
                    for (UserInfoVo userInfoVo : userInfoVoList) {
                        if (userInfoVo.getEnterpriseId() != null && idAndEnterpriseEntityMap.get(userInfoVo.getEnterpriseId()) != null) {
                            userInfoVo.setEnterpriseName(idAndEnterpriseEntityMap.get(userInfoVo.getEnterpriseId()).getName());
                        } else {
                            userInfoVo.setEnterpriseName(null);
                        }
                    }

                }

            }
        }, executor);

        ////查询门店信息
        //查询门店名称
        CompletableFuture<Void> storeCompletableFuture = CompletableFuture.runAsync(() -> {
            //2.每一个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            if (storeIdSet.size() > 0) {
                System.out.println("查询门店信息");
                R r = enterpriseFeignService.getStoreMapByIdList(new ArrayList<>(storeIdSet));
                if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                    Map<Long, EnterpriseEntity> idAndStoreEntityMap = r.getData("idAndStoreEntityMap", new TypeReference<Map<Long, EnterpriseEntity>>() {
                    });
                    for (UserInfoVo userInfoVo : userInfoVoList) {
                        if (userInfoVo.getStoreId() != null && idAndStoreEntityMap.get(userInfoVo.getStoreId()) != null) {
                            userInfoVo.setStoreName(idAndStoreEntityMap.get(userInfoVo.getStoreId()).getName());
                        } else {
                            userInfoVo.setStoreName(null);
                        }
                    }
                }
            }
        }, executor);

        ////查询职位名称
        CompletableFuture<Void> positionCompletableFuture = CompletableFuture.runAsync(() -> {
            //2.每一个线程都共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            System.out.println("查询职位名称");
            if (userIdList.size() > 0) {
                R r = enterpriseFeignService.getUserIdAndPositionIdMapByUserIdList(userIdList);
                if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                    Map<Long, Long> userIdAndPositionIdMap = r.getData("userIdAndPositionIdMap", new TypeReference<Map<Long, Long>>() {
                    });

                    List<Long> positionIdList = new ArrayList<>();
                    for (Map.Entry<Long, Long> entry : userIdAndPositionIdMap.entrySet()) {
                        positionIdList.add(entry.getValue());
                    }

                    R r2 = enterpriseFeignService.getPositionMapByIdList(positionIdList);
                    if (r2.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
                        Map<Long, PositionEntity> idAndPositionEntityMap = r2.getData("idAndPositionEntityMap", new TypeReference<Map<Long, PositionEntity>>() {
                        });
//                        List<UserInfoVo> userInfoVoListWhoHaveNotPosition = new ArrayList<>();
                        for (UserInfoVo userInfoVo : userInfoVoList) {
                            if (userIdAndPositionIdMap.get(userInfoVo.getId()) != null) {
                                userInfoVo.setPositionId(userIdAndPositionIdMap.get(userInfoVo.getId()));
                                userInfoVo.setPositionName(idAndPositionEntityMap.get(userInfoVo.getPositionId()).getName());
                            } else {
                                userInfoVo.setPositionId(null);
                                userInfoVo.setPositionName(null);
//                                userInfoVoListWhoHaveNotPosition.add(userInfoVo);
                            }
                        }
                        //移除没有职位的员工，因为没办法分配工作
//                        userInfoVoList.removeAll(userInfoVoListWhoHaveNotPosition);
                    }
                }
            }

        }, executor);

        try {
            CompletableFuture.allOf(enterpriseCompletableFuture,
                    storeCompletableFuture,
                    positionCompletableFuture).get();
            return userInfoVoList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean changePassword(String token, String oldPassword, String newPassword) throws SSSException {
        String username = JwtUtil.getUsername(token);
        UserEntity user = this.getUserInfoByUsername(username);
        String oldEncryptPassword = user.getPassword();

        //判断旧密码是否正确
        if (!EncryptionUtil.isSaltMd5Match(oldPassword, oldEncryptPassword)) {
            throw new SSSException(ResultCodeEnum.FAIL.getCode(), "原密码不合法");
        }

        //存储新密码
        if (CheckUtil.passwordCheck(newPassword)) {
            user.setPassword(EncryptionUtil.saltMd5Encrypt(newPassword));
            baseMapper.updateById(user);
            return true;
        } else {
            throw new SSSException(ResultCodeEnum.FAIL.getCode(), "新密码不合法");
        }
    }

    /**
     * 获取门店中还没有被分配职位的用户列表
     *
     * @param storeId
     * @return
     */
    @Override
    public List<UserEntity> getUserListWithoutPosition(long storeId) {
        return baseMapper.getUserListWithoutPosition(storeId);
    }

    @Override
    public UserEntity getByOpenid(String openid) {
        return userDao.selectOne(new QueryWrapper<UserEntity>().eq("openid", openid));
    }

    @Override
    public List<UserInfoVo> listUserInfoVoByUserIds(List<Long> userIds) {
        List<UserEntity> userEntityList = new ArrayList<>();
        if (userIds.size() > 0) {
            userEntityList.addAll(baseMapper.selectList(new QueryWrapper<UserEntity>().in("id", userIds)));
        }
        return this.buildUserInfoVoList(userEntityList);
    }

    @Override
    public List<UserEntity> listUserByUserIds(List<Long> userIds) {
        List<UserEntity> userEntityList = new ArrayList<>();
        if (userIds.size() > 0) {
            userEntityList.addAll(baseMapper.selectList(new QueryWrapper<UserEntity>().in("id", userIds)));
        }
        return userEntityList;
    }

    /**
     * 获取指定年份每个月的用户注册人数
     *
     * @param year
     * @return
     */
    @Override
    public List<Integer> getRegisterUserNumOfYear(Integer year) {
        List<Integer> userRegisterNumList = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Date[] dateArr = DateUtil.getStartAndEndDateOfMonth(year, month);
            if (dateArr[0].getTime() > System.currentTimeMillis()) {
                //--if--如果月份的起始日期已经大于当前日期，那肯定是没有任务了
                userRegisterNumList.add(0);
            } else {
                userRegisterNumList.add(baseMapper.selectCount(new QueryWrapper<UserEntity>().ge("create_time", dateArr[0]).le("create_time", dateArr[1])));
            }
        }
        return userRegisterNumList;
    }

    @Override
    public List<String> listAllMailByUserIdList(List<Long> userIdList) {
        return baseMapper.listAllMailByUserIdList(userIdList);
    }

    @Override
    public HashMap<Long, Integer> getEnterpriseIdAndUserNumMap(List<Long> enterpriseIdList) {
        HashMap<Long, Integer> enterpriseIdAndUserNumMap = new HashMap<>();
        for (Long enterpriseId : enterpriseIdList) {
            int count = userService.count(new QueryWrapper<UserEntity>().eq("enterprise_id", enterpriseId));
            enterpriseIdAndUserNumMap.put(enterpriseId, count);
        }
        return enterpriseIdAndUserNumMap;
    }

    @Override
    public void randomSetAvatar() {
        try {
            String lineStr = TxtUtil.read(new File("file/data/oss图片数据/20230328.csv"), "utf-8");
            String[] lineStrList = lineStr.split("\n");
            List<String> urlList = new ArrayList<>();
            for (String line : lineStrList) {
                urlList.add(line.split(",")[1]);
            }
            //移除标题
            urlList.remove(0);
            Random random = new Random();
            List<UserEntity> userEntityList = baseMapper.selectList(new QueryWrapper<UserEntity>());
            for (UserEntity userEntity : userEntityList) {
                userEntity.setAvatar(urlList.get(random.nextInt(urlList.size())));
//                System.out.println("更换头像");
                baseMapper.updateById(userEntity);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 打乱企业的用户到不同的门店
     *
     * @param enterpriseId
     */
    @Override
    public void shuffleUserToDifferentStores(Long enterpriseId) {
        Random random = new Random();
        ///查询企业的所有普通用户
        List<UserEntity> allUserList = baseMapper.selectList(new QueryWrapper<UserEntity>()
                .eq("enterprise_id", enterpriseId)
                .eq("type", UserCodeEnum.TYPE_ORDINARY_USER.getCode()));
        ///随机分配门店
        List<Long> storeIdList = null;
        R r = enterpriseFeignService.listAllStoreByAppointEnterpriseId(enterpriseId);
        if (r.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            List<StoreEntity> storeEntityList = r.getData("list", new TypeReference<List<StoreEntity>>() {
            });
            storeIdList = storeEntityList.stream().map(StoreEntity::getId).collect(Collectors.toList());
            for (UserEntity userEntity : allUserList) {
                userEntity.setStoreId(storeEntityList.get(random.nextInt(storeEntityList.size())).getId());
            }
        }
        ///查询出每个门店所拥有的positionIdList
        R r2 = enterpriseFeignService.getStoreIdAndPositionList(storeIdList);
        Map<Long, List<PositionEntity>> storeIdAndPositionList = null;
        if (r2.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            storeIdAndPositionList = r2.getData("storeIdAndPositionList", new TypeReference<Map<Long, List<PositionEntity>>>() {
            });
        }

        ///随机分配职位
        //将用户原本的职位删除
        List<Long> userIdList = allUserList.stream().map(UserEntity::getId).collect(Collectors.toList());
        R r1 = enterpriseFeignService.deleteUserPositionByUserIdList(userIdList);
        if (r1.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            ///分配新的职位
            List<UserPositionEntity> newUserPositionList = new ArrayList<>();
            for (UserEntity userEntity : allUserList) {
                UserPositionEntity userPositionEntity = new UserPositionEntity();
                userPositionEntity.setUserId(userEntity.getId());
                List<PositionEntity> positionEntityList = storeIdAndPositionList.get(userEntity.getStoreId());
                userPositionEntity.setPositionId(positionEntityList.get(random.nextInt(positionEntityList.size())).getId());
                newUserPositionList.add(userPositionEntity);
            }
            enterpriseFeignService.saveUserPositionList(newUserPositionList);
        }

        ///修改用户
        this.updateBatchById(allUserList);
    }


}