package com.dam.feign;

import com.dam.config.feign.FeignConfig;
import com.dam.model.entity.enterprise.UserPositionEntity;
import com.dam.model.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "sss-enterprise", configuration = FeignConfig.class)
public interface EnterpriseFeignService {

    /**
     * 根据企业id查询企业信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/enterprise/enterprise/info/{id}")
    public R getEnterpriseEntityById(@PathVariable("id") Long id);

    /**
     * 获取系统的企业数量
     *
     * @return int count
     */
    @RequestMapping("/enterprise/enterprise/getAllEnterpriseNum")
    public R getAllEnterpriseNum();


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
     * 根据门店id查询门店信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/enterprise/store/info/{id}")
    public R getStoreEntityById(@PathVariable("id") Long id);

    /**
     * 获取系统的门店数量
     *
     * @return int count
     */
    @RequestMapping("/enterprise/store/getAllStoreNum")
    public R getAllStoreNum();

    /**
     * 根据企业id集合查询门店，并封装成字典
     *
     * @return
     */
    @PostMapping("/enterprise/store/getStoreMapByIdList")
    public R getStoreMapByIdList(@RequestBody List<Long> storeIdList);


    /**
     * 根据企业id集合查询企业，并封装成字典
     *
     * @return
     */
    @PostMapping("/enterprise/enterprise/getEnterpriseMapByIdList")
    public R getEnterpriseMapByIdList(@RequestBody List<Long> enterpriseIdList);

    /**
     * 根据职位id查询职位信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/enterprise/position/info/{id}")
    public R getPositionEntityById(@PathVariable("id") Long id);

    /**
     * 查询出所有叶子节点数据
     *
     * @return
     */
    @RequestMapping("/enterprise/position/listAllSonPosition")
    public R listAllSonPosition(@RequestParam("storeId") Long storeId);

    /**
     * 给定门店id集合，查询出每个门店的所有叶子节点数据
     *
     * @return Map<Long, List<PositionEntity>> storeIdAndPositionList
     */
    @RequestMapping("/enterprise/position/getStoreIdAndPositionList")
    public R getStoreIdAndPositionList(@RequestBody List<Long> storeIdList);

    /**
     * 根据企业id集合查询门店，并封装成字典
     *
     * @return
     */
    @PostMapping("/enterprise/position/getPositionMapByIdList")
    public R getPositionMapByIdList(@RequestBody List<Long> positionIdList);

    /**
     * 根据企业id集合查询企业，并封装成字典
     *
     * @return
     */
    @PostMapping("/enterprise/userposition/getUserIdAndPositionIdMapByUserIdList")
    public R getUserIdAndPositionIdMapByUserIdList(@RequestBody List<Long> userIdList);

    /**
     * 保存
     */
    @RequestMapping("/enterprise/userposition/saveBatch")
    public R saveUserPositionList(@RequestBody List<UserPositionEntity> userPositionList);

    /**
     * 根据用户id获取所匹配的职位绑定信息
     *
     * @param userId
     * @return
     */
    @RequestMapping("/enterprise/userposition/infoByUserId")
    public R infoUserPositionByUserId(@RequestParam("userId") Long userId);


    /**
     * 查询指定职位的用户id
     *
     * @param PositionIdList
     * @return
     */
    @RequestMapping("/enterprise/userposition/listUserIdList")
    public List<Long> listUserIdList(@RequestParam("PositionIdList") List<Long> PositionIdList);

    /**
     * 根据用户id集合删除其职位
     */
    @RequestMapping("/enterprise/userposition/deleteUserPositionByUserIdList")
    public R deleteUserPositionByUserIdList(@RequestBody List<Long> userIdList);


    /**
     * 新增定时任务
     *
     * @return ResultMap
     */
    @PostMapping(path = "/enterprise/quartz/addJob")
    @ResponseBody
    public R addJob(Map<String, Object> paramMap);

    /**
     * 删除任务
     *
     * @return ResultMap
     */
    @PostMapping(path = "/enterprise/quartz/deleteJob")
    @ResponseBody
    public R deleteJob(Map<String, Object> paramMap);
}
