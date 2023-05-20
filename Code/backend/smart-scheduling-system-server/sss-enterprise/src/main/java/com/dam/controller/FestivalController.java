package com.dam.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dam.annotation.OperationLog;
import com.dam.model.entity.enterprise.FestivalEntity;
import com.dam.model.enums.log.BusinessTypeEnum;
import com.dam.model.result.R;
import com.dam.utils.JwtUtil;
import com.dam.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dam.service.FestivalService;

import javax.servlet.http.HttpServletRequest;


/**
 * 门店节日表
 *
 * @author dam
 * @email 1782067308@qq.com
 * @date 2023-03-13 16:42:08
 */
@RestController
@RequestMapping("enterprise/festival")
public class FestivalController {
    @Autowired
    private FestivalService festivalService;
    private static final String title = "节日管理";

    /**
     * 列表
     */
    @RequestMapping("/list")
    @PreAuthorize("hasAuthority('bnt.festival.list')")
    public R list(@RequestParam Map<String, Object> params,HttpServletRequest httpServletRequest) {
        long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        QueryWrapper<FestivalEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("store_id",storeId).eq("is_deleted",0);
        PageUtils page = festivalService.queryPage(params,wrapper);

        return R.ok().addData("page", page);
    }

    /**
     * 查询一个门店的所有节假日
     */
    @RequestMapping("/listAllHolidaysOfOneStore")
    public R listAllHolidaysOfOneStore(HttpServletRequest httpServletRequest) {
        long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        List<FestivalEntity> festivalList = festivalService.list(new QueryWrapper<FestivalEntity>().eq("store_id", storeId).eq("is_deleted", 0));
        List<FestivalEntity> newFestivalList = new ArrayList<>();
        for (FestivalEntity festivalEntity : festivalList) {
            newFestivalList.addAll(festivalService.correctionDate(festivalEntity, 5, 5));
        }
        return R.ok().addData("festivalList", newFestivalList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @PreAuthorize("hasAuthority('bnt.festival.list')")
    public R info(@PathVariable("id") Long id) {
        FestivalEntity festival = festivalService.getById(id);

        return R.ok().addData("festival", festival);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @OperationLog(title = FestivalController.title, businessType = BusinessTypeEnum.INSERT, detail = "新增节日")
    @PreAuthorize("hasAuthority('bnt.festival.add')")
    public R save(@RequestBody FestivalEntity festival, HttpServletRequest httpServletRequest) {
        long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        festival.setStoreId(storeId);
        festivalService.save(festival);

        return R.ok();
    }

    /**
     * 添加法定节假日
     */
    @RequestMapping("/addStatutoryHolidays")
    @PreAuthorize("hasAuthority('bnt.festival.addStatutoryHolidays')")
    public R addStatutoryHolidays(HttpServletRequest httpServletRequest) {
        long storeId = Long.parseLong(JwtUtil.getStoreId(httpServletRequest.getHeader("token")));
        festivalService.addStatutoryHolidays(storeId);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @OperationLog(title = FestivalController.title, businessType = BusinessTypeEnum.UPDATE, detail = "修改节日")
    @PreAuthorize("hasAuthority('bnt.festival.update')")
    public R update(@RequestBody FestivalEntity festival) {
        festivalService.updateById(festival);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteBatch")
    @OperationLog(title = FestivalController.title, businessType = BusinessTypeEnum.DELETE, detail = "删除节日")
    @PreAuthorize("hasAuthority('bnt.festival.delete')")
    public R delete(@RequestBody Long[] ids) {
        festivalService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
