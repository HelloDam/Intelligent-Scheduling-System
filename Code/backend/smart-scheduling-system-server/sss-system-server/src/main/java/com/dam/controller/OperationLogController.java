package com.dam.controller;

import java.util.*;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.annotation.OperationLog;
import com.dam.feign.EnterpriseFeignService;
import com.dam.model.entity.enterprise.EnterpriseEntity;
import com.dam.model.entity.enterprise.StoreEntity;
import com.dam.model.entity.system.OperationLogEntity;
import com.dam.model.entity.system.UserEntity;
import com.dam.model.enums.ResultCodeEnum;
import com.dam.model.enums.log.BusinessTypeEnum;
import com.dam.model.result.R;
import com.dam.model.vo.system.OperationLogVo;
import com.dam.utils.JwtUtil;
import com.dam.utils.PageUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dam.service.OperationLogService;

import javax.servlet.http.HttpServletRequest;


/**
 * 操作日志表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-13 16:42:08
 */
@RestController
@RequestMapping("system/operationLog")
public class OperationLogController {
    @Autowired
    private OperationLogService operationLogService;
    @Autowired
    private EnterpriseFeignService enterpriseFeignService;
    private static final String title = "操作日志管理";

    /**
     * 列表
     */
    @RequestMapping("/list")
    @PreAuthorize("hasAuthority('bnt.operLog.list')")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest httpRequest) {

        String token = httpRequest.getHeader("token");


        ////查询数据
        PageUtils page = operationLogService.queryPage(params,token);

        ////封装数据给前端展示
        List<OperationLogEntity> operationLogEntityList = (List<OperationLogEntity>) page.getList();
        List<OperationLogVo> operationLogVoList = new ArrayList<>();
        Set<Long> storeIdList = new HashSet<>();
        Set<Long> enterpriseIdList = new HashSet<>();
        for (OperationLogEntity operationLogEntity : operationLogEntityList) {
            OperationLogVo operationLogVo = new OperationLogVo();
            BeanUtils.copyProperties(operationLogEntity, operationLogVo);
            if (operationLogEntity.getStoreId() != null) {
                enterpriseIdList.add(operationLogEntity.getEnterpriseId());
                storeIdList.add(operationLogEntity.getStoreId());
                operationLogVoList.add(operationLogVo);
            }
        }
        //设置企业名称
        R r1 = enterpriseFeignService.getEnterpriseMapByIdList(new ArrayList<>(enterpriseIdList));
        if (r1.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            Map<Long, EnterpriseEntity> idAndEnterpriseEntityMap = r1.getData("idAndEnterpriseEntityMap",
                    new TypeReference<Map<Long, EnterpriseEntity>>() {
                    });
            for (OperationLogVo operationLogVo : operationLogVoList) {
                Long enterpriseId = operationLogVo.getEnterpriseId();
                operationLogVo.setEnterpriseName(idAndEnterpriseEntityMap.get(enterpriseId).getName());
            }
        }
        //设置门店名称
        R r2 = enterpriseFeignService.getStoreMapByIdList(new ArrayList<>(storeIdList));
        if (r2.getCode() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            Map<Long, StoreEntity> idAndStoreEntityMap = r2.getData("idAndStoreEntityMap",
                    new TypeReference<Map<Long, StoreEntity>>() {
                    });
            for (OperationLogVo operationLogVo : operationLogVoList) {
                Long storeId = operationLogVo.getStoreId();
                operationLogVo.setStoreName(idAndStoreEntityMap.get(storeId).getName());
            }
        }
        page.setList(operationLogVoList);

        return R.ok().addData("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @PreAuthorize("hasAuthority('bnt.operLog.list')")
    public R info(@PathVariable("id") Long id) {
        OperationLogEntity operationLog = operationLogService.getById(id);

        return R.ok().addData("operationLog", operationLog);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @PreAuthorize("hasAuthority('bnt.operLog.add')")
    public R save(@RequestBody OperationLogEntity operationLog) {
        operationLogService.save(operationLog);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @PreAuthorize("hasAuthority('bnt.operLog.update')")
    public R update(@RequestBody OperationLogEntity operationLog) {
        operationLogService.updateById(operationLog);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteBatch")
    @PreAuthorize("hasAuthority('bnt.operLog.delete')")
    @OperationLog(title = OperationLogController.title, businessType = BusinessTypeEnum.DELETE, detail = "删除操作日志")
    public R delete(@RequestBody Long[] ids) {
        operationLogService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
