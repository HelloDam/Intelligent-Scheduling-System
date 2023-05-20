package com.dam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.model.entity.enterprise.EnterpriseEntity;
import com.dam.model.vo.enterprise.EnterpriseVo;
import com.dam.utils.PageUtils;
import com.dam.utils.vo.EnterpriseRegisterVo;

import java.util.List;
import java.util.Map;

/**
 * 企业表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-13 14:49:17
 */
public interface EnterpriseService extends IService<EnterpriseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Boolean passEnterpriseRegister(EnterpriseRegisterVo enterpriseRegisterVo);

    Boolean rejectEnterpriseRegister(EnterpriseRegisterVo enterpriseRegisterVo);

    List<EnterpriseVo> buildEnterpriseVoList(List<EnterpriseEntity> enterpriseEntityList);

    List<Integer> getRegisterEnterpriseNumOfYear(Integer year);
}

