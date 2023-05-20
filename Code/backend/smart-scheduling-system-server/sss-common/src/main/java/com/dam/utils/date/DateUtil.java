package com.dam.utils.date;

import com.dam.utils.TimeUtil;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * 日期工具类
 */
public class DateUtil {

    /**
     * 转化UTC时间为北京时间
     *
     * @param utcTimeString UTC时间
     * @return 北京时间
     */
    public static String convertUTCToBeijing(String utcTimeString) {
        // 将输入的UTC时间字符串解析为Instant对象
        Instant utcInstant = Instant.parse(utcTimeString);

        // 创建时区对象
        ZoneId beijingZone = ZoneId.of("Asia/Shanghai");

        // 将Instant对象转换为ZonedDateTime对象，使用北京时区
        ZonedDateTime beijingTime = ZonedDateTime.ofInstant(utcInstant, beijingZone);

        // 将北京时间格式化为字符串，输出yyyy-MM-dd格式
        String beijingTimeString = beijingTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return beijingTimeString;
    }

    /**
     * 获取一个月的起始日期和结束日期
     *
     * @param year
     * @param month
     * @return
     */
    public static Date[] getStartAndEndDateOfMonth(Integer year, Integer month) {
        Date[] dateArr = new Date[2];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        try {
            Date date = sdf.parse(year + "-" + month + "-1");
            //获取所查看月份的第一天的日期
            dateArr[0] = TimeUtil.getFirstDayOfOneMonth(date.getTime());
            //获取所查看月份的最后一天的日期
            dateArr[1] = TimeUtil.getLastDayOfOneMonth(date.getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return dateArr;
    }

    public static String getMonthName(int month) {
        switch (month) {
            case 1:
                return "一月";
            case 2:
                return "二月";
            case 3:
                return "三月";
            case 4:
                return "四月";
            case 5:
                return "五月";
            case 6:
                return "六月";
            case 7:
                return "七月";
            case 8:
                return "八月";
            case 9:
                return "九月";
            case 10:
                return "十月";
            case 11:
                return "十一月";
            case 12:
                return "十二月";
            default:
                return "";
        }
    }

    /**
     * 将Date类解析出 年 月 日 时 分 秒
     * @param date
     * @return
     */
    public static DateUtil.DateEntity parseDate(Date date) {
        // 创建一个Calendar对象
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setTime(date);

        // 获取北京时间的年、月、日、时、分、秒
        int year = calendar.get(Calendar.YEAR);
        // 注意月份从0开始，所以要加1
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        DateUtil.DateEntity dateEntity = new DateUtil.DateEntity(year, month, day, hour, minute, second);
        return dateEntity;
    }

    @Data
    public static class DateEntity {
        private Integer year;
        private Integer month;
        private Integer day;
        private Integer hour;
        private Integer minute;
        private Integer second;

        public DateEntity(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }
    }

}
