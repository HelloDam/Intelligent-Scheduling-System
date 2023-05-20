package com.dam.service.impl;

import com.dam.model.entity.enterprise.FestivalEntity;
import com.dam.utils.PageUtils;
import com.dam.utils.Query;
import org.hothub.calendarist.Calendarist;
import org.hothub.calendarist.pojo.SolarDate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dam.dao.FestivalDao;
import com.dam.service.FestivalService;


@Service("festivalService")
public class FestivalServiceImpl extends ServiceImpl<FestivalDao, FestivalEntity> implements FestivalService {

    @Override
    public PageUtils queryPage(Map<String, Object> params, QueryWrapper<FestivalEntity> wrapper) {
        IPage<FestivalEntity> page = this.page(
                new Query<FestivalEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 添加法定节假日
     *
     * @param storeId
     */
    @Override
    public void addStatutoryHolidays(long storeId) {
        List<FestivalEntity> statutoryHolidays = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        try {
            statutoryHolidays.add(new FestivalEntity("元旦节", format.parse("12-31"), format.parse("01-02"), storeId, 1));
            statutoryHolidays.add(new FestivalEntity("劳动节", format.parse("04-29"), format.parse("05-03"), storeId, 1));
            statutoryHolidays.add(new FestivalEntity("国庆节", format.parse("10-01"), format.parse("10-07"), storeId, 1));
            statutoryHolidays.add(new FestivalEntity("春节", format.parse("12-30"), format.parse("01-06"), storeId, 0));
//            statutoryHolidays.add(new FestivalEntity("清明", format.parse("02-15"), format.parse("02-15"), storeId, 0));
            statutoryHolidays.add(new FestivalEntity("中秋节", format.parse("08-15"), format.parse("08-15"), storeId, 0));

            for (FestivalEntity statutoryHoliday : statutoryHolidays) {
                baseMapper.insert(statutoryHoliday);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     *
     * @param festivalEntity
     */
    /**
     * 修正日期 如果是新历，修改年份；如果是农历，先修改年份，再对应到新历
     *
     * @param festivalEntity
     * @param lead           提前量（年份）
     * @param recedingAmount 后退量（年份）
     * @return
     */
    @Override
    public List<FestivalEntity> correctionDate(FestivalEntity festivalEntity, int lead, int recedingAmount) {
        //存储前后几年的节日分别在什么时候
        List<FestivalEntity> festivalEntityList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        System.out.println("修正前---------------------------------------------");
//        System.out.println(sdf.format(festivalEntity.getStartDate()));
//        System.out.println(sdf.format(festivalEntity.getEndDate()));

        Calendar calendar = Calendar.getInstance();
        //获取当前年份
        int curYear = calendar.get(Calendar.YEAR);
        int startMonth = festivalEntity.getStartDate().getMonth() + 1;
        int startDate = festivalEntity.getStartDate().getDate();
        int endMonth = festivalEntity.getEndDate().getMonth() + 1;
        int endDate = festivalEntity.getEndDate().getDate();

        for (int year = curYear - lead; year <= curYear + recedingAmount; year++) {
            FestivalEntity newFestival = new FestivalEntity();
            BeanUtils.copyProperties(festivalEntity, newFestival);

            try {
                newFestival.setStartDate(sdf.parse(year + "-" + startMonth + "-" + startDate));
                if (endMonth < startMonth) {
                    //--if--如果结束日期的月份更小，肯定需要换一年
                    newFestival.setEndDate(sdf.parse(year + 1 + "-" + endMonth + "-" + endDate));
                } else {
                    newFestival.setEndDate(sdf.parse(year + "-" + endMonth + "-" + endDate));
                }

                //        System.out.println("修正中---------------------------------------------");
//        System.out.println(sdf.format(festivalEntity.getStartDate()));
//        System.out.println(sdf.format(festivalEntity.getEndDate()));

                //如果是旧历，还要对应到新历
                if (newFestival.getType() == 0) {
                    newFestival.setStartDate(lunarToSolar(newFestival.getStartDate()));
                    newFestival.setEndDate(lunarToSolar(newFestival.getEndDate()));
                }
                festivalEntityList.add(newFestival);

//                System.out.println("修正后---------------------------------------------");
//                System.out.println(sdf.format(festivalEntity.getStartDate()));
//                System.out.println(sdf.format(festivalEntity.getEndDate()));
//                System.out.println();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }




        return  festivalEntityList;
    }

    /**
     * 阴历转阳历
     *
     * @param date
     */
    private Date lunarToSolar(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        String format = sdf.format(date);
        String[] split = format.split("-");
        Calendarist calendarist = Calendarist.fromLunar(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        //转成阳历
        SolarDate solarDate = calendarist.toSolar();
        try {
            return sdf.parse(solarDate.getYear() + "-" + solarDate.getMonth() + "-" + solarDate.getDay());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}