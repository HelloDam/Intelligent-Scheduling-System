package com.dam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dam.model.vo.enterprise.AreaItemVo;
import com.dam.utils.PageUtils;
import com.dam.model.entity.enterprise.ProvinceCityRegionEntity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 省-市-区表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
public interface ProvinceCityRegionService extends IService<ProvinceCityRegionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAreaDataToDatabase() throws IOException;

    List<AreaItemVo> getAreaTree();

}

