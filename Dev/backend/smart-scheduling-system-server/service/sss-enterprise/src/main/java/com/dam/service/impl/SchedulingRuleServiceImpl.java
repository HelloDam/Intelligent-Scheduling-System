package com.dam.service.impl;

import com.dam.constant.RedissonLockConstant;
import com.dam.exception.SSSException;
import com.dam.model.entity.enterprise.SchedulingRuleEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.vo.enterprise.SchedulingRuleVo;
import com.dam.service.PositionService;
import com.dam.service.StoreService;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.dam.dao.SchedulingRuleDao;
import com.dam.service.SchedulingRuleService;

import javax.annotation.Resource;


@Service("schedulingRuleService")
public class SchedulingRuleServiceImpl extends ServiceImpl<SchedulingRuleDao, SchedulingRuleEntity> implements SchedulingRuleService {

    @Autowired
    private SchedulingRuleDao schedulingRuleDao;
    @Autowired
    private StoreService storeService;
    @Autowired
    private PositionService positionService;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SchedulingRuleEntity> page = this.page(
                new Query<SchedulingRuleEntity>().getPage(params),
                new QueryWrapper<SchedulingRuleEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public SchedulingRuleVo getSchedulingRuleVoByStoreId(long storeId) {
        SchedulingRuleEntity schedulingRuleEntity = schedulingRuleDao.selectOne(new QueryWrapper<SchedulingRuleEntity>().eq("store_id", storeId).eq("rule_type", 0));
        SchedulingRuleVo schedulingRuleVo = new SchedulingRuleVo();
        if (schedulingRuleEntity != null) {
            BeanUtils.copyProperties(schedulingRuleEntity, schedulingRuleVo);
        }

        ///设置门店面积
        schedulingRuleVo.setStoreSize(storeService.getById(storeId).getSize());

        ///设置职位选择树
        schedulingRuleVo.setPositionSelectTree(positionService.buildTree(storeId));

        return schedulingRuleVo;
    }

    @Override
    public SchedulingRuleEntity getSchedulingRuleVoByStoreIdAndType(Long storeId, int type) {
        return schedulingRuleDao.selectOne(new QueryWrapper<SchedulingRuleEntity>().eq("store_id", storeId).eq("rule_type", type));
    }

    /**
     * 根据id查询规则
     *
     * @param ruleId
     * @return
     */
    @Override
    public SchedulingRuleVo getSchedulingRuleVoByRuleId(Long ruleId) {
        SchedulingRuleEntity schedulingRuleEntity = schedulingRuleDao.selectById(ruleId);
        SchedulingRuleVo schedulingRuleVo = new SchedulingRuleVo();
        if (schedulingRuleEntity != null) {
            BeanUtils.copyProperties(schedulingRuleEntity, schedulingRuleVo);
        }

        ///设置门店面积
        schedulingRuleVo.setStoreSize(storeService.getById(schedulingRuleEntity.getStoreId()).getSize());

        ///设置职位选择树
        schedulingRuleVo.setPositionSelectTree(positionService.buildTree(schedulingRuleEntity.getStoreId()));

        return schedulingRuleVo;
    }

    @Override
    public void saveRule(SchedulingRuleEntity schedulingRule, Long storeId) {
        RLock lock = redissonClient.getLock(RedissonLockConstant.MODULE_ENTERPRISE + ":update:" + storeId);
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                if (baseMapper.selectCount(new QueryWrapper<SchedulingRuleEntity>().eq("store_id", storeId)) > 0) {
                    throw new SSSException(ResultCodeEnum.FAIL.getCode(),"门店已经设置规则，无法重复设置");
                } else {
                    schedulingRule.setStoreId(storeId);
                    schedulingRule.setRuleType(0);
                    baseMapper.insert(schedulingRule);
                }
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}