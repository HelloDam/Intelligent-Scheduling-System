package com.dam.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.dam.model.vo.enterprise.AreaItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.dam.model.entity.enterprise.ProvinceCityRegionEntity;
import com.dam.service.ProvinceCityRegionService;
import com.dam.utils.PageUtils;
import com.dam.model.result.R;


/**
 * 省-市-区表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-02-09 11:17:26
 */
@RestController
@RequestMapping("enterprise/provincecityregion")
public class ProvinceCityRegionController {
    @Autowired
    private ProvinceCityRegionService provinceCityRegionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = provinceCityRegionService.queryPage(params);

        return R.ok().addData("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        ProvinceCityRegionEntity provinceCityRegion = provinceCityRegionService.getById(id);

        return R.ok().addData("provinceCityRegion", provinceCityRegion);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ProvinceCityRegionEntity provinceCityRegion) {
        provinceCityRegionService.save(provinceCityRegion);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody ProvinceCityRegionEntity provinceCityRegion) {
        provinceCityRegionService.updateById(provinceCityRegion);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        provinceCityRegionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @GetMapping("/saveDataToDatabase")
    public R saveDataToDatabase() throws IOException {
        provinceCityRegionService.saveAreaDataToDatabase();
        return R.ok();
    }

    /**
     * 获取省市区的树形结构数据
     * @return
     */
    @GetMapping("/getAreaTree")
    public R getAreaTree() {
        List<AreaItemVo> areaItemVoList = provinceCityRegionService.getAreaTree();
        return R.ok().addData("areaItemVoList", areaItemVoList);
    }

}
