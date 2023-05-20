package com.dam.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.annotation.OperationLog;
import com.dam.model.entity.enterprise.EnterpriseEntity;
import com.dam.model.enums.log.BusinessTypeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.enterprise.StoreVo;
import com.dam.service.ProvinceCityRegionService;
import com.dam.utils.JwtUtil;
import com.dam.utils.PageUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.service.StoreService;

import javax.servlet.http.HttpServletRequest;


/**
 * 门店表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
@RestController
@RequestMapping("enterprise/store")
public class StoreController {
    @Autowired
    private StoreService storeService;
    @Autowired
    private ProvinceCityRegionService provinceCityRegionService;
    private static final String title = "门店管理";

    /**
     * 根据企业id查询出旗下所有门店
     *
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/listAllStoreByEnterpriseId")
    @PreAuthorize("hasAuthority('bnt.store.list')")
    public R listAllStoreByEnterpriseId(HttpServletRequest httpServletRequest) {
        QueryWrapper<StoreEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("enterprise_id", JwtUtil.getEnterpriseId(httpServletRequest.getHeader("token")));
        List<StoreEntity> list = storeService.list(wrapper);

        return R.ok().addData("list", list);
    }

    /**
     * 根据企业id查询出旗下所有门店
     *
     * @param enterpriseId
     * @return List<StoreEntity> list
     */
    @RequestMapping("/listAllStoreByAppointEnterpriseId")
    @PreAuthorize("hasAuthority('bnt.store.list')")
    public R listAllStoreByAppointEnterpriseId(@RequestParam("enterpriseId") Long enterpriseId) {
        QueryWrapper<StoreEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("enterprise_id", enterpriseId);
        List<StoreEntity> list = storeService.list(wrapper);

        return R.ok().addData("list", list);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    @PreAuthorize("hasAuthority('bnt.store.list')")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest httpServletRequest) {
        //构建查询wrapper
        QueryWrapper<StoreEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("enterprise_id", JwtUtil.getEnterpriseId(httpServletRequest.getHeader("token")));

        PageUtils page = storeService.queryPage(params, wrapper);

        //封装vo数据
        List<?> list = page.getList();
        List<StoreVo> storeVoList = list.stream().map(item -> {
            String json = JSON.toJSONString(item);
            StoreEntity storeEntity = JSON.parseObject(json, StoreEntity.class);
            StoreVo storeVo = new StoreVo();
            BeanUtils.copyProperties(storeEntity, storeVo);
            //查询名字省市区的名字
            storeVo.setProvinceName(provinceCityRegionService.getById(storeEntity.getProvinceId()).getName());
            storeVo.setCityName(provinceCityRegionService.getById(storeEntity.getCityId()).getName());
            storeVo.setRegionName(provinceCityRegionService.getById(storeEntity.getRegionId()).getName());
            return storeVo;
        }).collect(Collectors.toList());
        page.setList(storeVoList);
        return R.ok().addData("page", page);
    }

    /**
     * 获取系统的门店数量
     *
     * @return int count
     */
    @RequestMapping("/getAllStoreNum")
    public R getAllStoreNum() {
        int count = storeService.count(new QueryWrapper<StoreEntity>().eq("is_deleted", 0));
        return R.ok().addData("count", count);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
//    @PreAuthorize("hasAuthority('bnt.store.list')")
    //用户需要查看自己的门店信息（分配权限的时候分好也行，这里为了方便直接不拦截了）
    public R info(@PathVariable("id") Long id) {
        StoreEntity store = storeService.getById(id);

        return R.ok().addData("store", store);
    }

    /**
     * 根据企业id集合查询门店，并封装成字典
     *
     * @return
     */
    @PostMapping("/getStoreMapByIdList")
    public R getStoreMapByIdList(@RequestBody List<Long> storeIdList) {
        System.out.println("getStoreMapByIdList，参数storeIdList：" + storeIdList.toString());
        Map<Long, StoreEntity> idAndStoreEntityMap = new HashMap<>();
        if (storeIdList.size() > 0) {
            List<StoreEntity> storeEntityList = storeService.list(new QueryWrapper<StoreEntity>().in("id", storeIdList).eq("is_deleted", 0));
            for (StoreEntity enterpriseEntity : storeEntityList) {
                idAndStoreEntityMap.put(enterpriseEntity.getId(), enterpriseEntity);
            }
        }
        System.out.println("idAndStoreEntityMap，参数idAndStoreEntityMap：" + idAndStoreEntityMap.toString());
        return R.ok().addData("idAndStoreEntityMap", idAndStoreEntityMap);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @PreAuthorize("hasAuthority('bnt.store.add')")
    @OperationLog(title = StoreController.title, businessType = BusinessTypeEnum.INSERT, detail = "新增门店")
    public R save(@RequestBody StoreEntity store, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("token");
        store.setEnterpriseId(Long.parseLong(JwtUtil.getEnterpriseId(token)));
        storeService.save(store);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @PreAuthorize("hasAuthority('bnt.store.update')")
    @OperationLog(title = StoreController.title, businessType = BusinessTypeEnum.UPDATE, detail = "修改门店信息")
    public R update(@RequestBody StoreEntity store) {
        storeService.updateById(store);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteBatch")
    @PreAuthorize("hasAuthority('bnt.store.delete')")
    @OperationLog(title = StoreController.title, businessType = BusinessTypeEnum.UPDATE, detail = "删除门店")
    public R delete(@RequestBody Long[] ids) {
        storeService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
