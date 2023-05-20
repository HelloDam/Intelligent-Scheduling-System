package com.dam.feign;

import com.dam.config.feign.FeignConfig;
import com.dam.model.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "sss-enterprise",configuration = FeignConfig.class)
public interface EnterpriseFeignService {

    /**
     * 根据企业id查询出旗下所有门店
     *
     * @param enterpriseId
     * @return List<StoreEntity> list
     */
    @RequestMapping("/enterprise/store/listAllStoreByAppointEnterpriseId")
    @PreAuthorize("hasAuthority('bnt.store.list')")
    public R listAllStoreByAppointEnterpriseId(@RequestParam("enterpriseId") Long enterpriseId);

    /**
     * 根据门店id获取排班规则
     *
     * @param storeId
     * @return
     */
    @RequestMapping("/enterprise/schedulingrule/getSchedulingRuleVoByGiveStoreId")
    public R getSchedulingRuleVoByGiveStoreId(@RequestParam("storeId") Long storeId);

    /**
     * 将门店规则复制一遍
     */
    @RequestMapping("/enterprise/schedulingrule/copySchedulingRule")
    public R copySchedulingRule(@RequestParam("storeId") Long storeId);

    /**
     * 根据id获取排班规则
     *
     * @param ruleId
     * @return
     */
    @RequestMapping("/enterprise/schedulingrule/getSchedulingRuleVoById")
    public R getSchedulingRuleVoById(@RequestParam("ruleId") Long ruleId);

    /**
     * 根据门店id查询所有职位
     */
    @RequestMapping("/enterprise/position/listByStoreId")
    public R listByStoreId(@RequestParam("storeId") Long storeId);

    /**
     * 根据职位id集合查询职位集合
     *
     * @return List<PositionEntity>  positionEntityList
     */
    @PostMapping("/enterprise/position/listPositionListByPositionIdList")
    public R listPositionListByPositionIdList(@RequestBody List<Long> positionIdList);

    /**
     * 根据企业id集合查询企业，并封装成字典
     *
     * @return
     */
    @PostMapping("/enterprise/userposition/getUserIdAndPositionIdMapByUserIdList")
    public R getUserIdAndPositionIdMapByUserIdList(@RequestBody List<Long> userIdList);

    /**
     * 发送消息通知用户
     *
     * @return
     */
    @PostMapping("/enterprise/message/sendMesToUserList")
    public R sendMesToUserList(@RequestBody Map<String, Object> paramMap);

}
