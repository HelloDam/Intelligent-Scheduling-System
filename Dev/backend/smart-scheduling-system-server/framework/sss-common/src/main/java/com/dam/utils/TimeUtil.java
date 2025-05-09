package com.dam.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeUtil {

    public static final long ONEDAYMILLIS = 24 * 60 * 60 * 1000;
    public static final String Y_M_D = "yyyy-MM-dd";
    public static final String YMD = "yyyyMMdd";
    public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 获取当天的00：00：00的时间戳
     *
     * @return
     */
    public static long getBeginMillisOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取某天的00：00：00的时间戳
     *
     * @param millis 毫秒时间戳
     * @return
     */
    public static long getBeginMillisOfDay(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(millis));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取时间戳戳是星期几
     *
     * @param date
     * @return 当前日期是星期几
     */
    public String getWeekOfDate(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
//        log.info("{} {} {} ", cal.get(Calendar.DAY_OF_WEEK), w, weekDays[w]);
        return weekDays[w];
    }

    /**
     * 获取时间戳是星期几
     *
     * @param
     * @return
     */
    public static int getIntWeekOfDate(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(millis));
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取时间戳属于当月的哪天
     *
     * @param millis
     * @return
     */
    public static Integer getDayOfMonth(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(millis));
        int w = cal.get(Calendar.DAY_OF_MONTH);
        return w;
    }


    /**
     * 获取年月日
     *
     * @param epochSecond
     * @return
     */
    public static String getYmd(Long epochSecond) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date(epochSecond));
    }

    /**
     * 获取年月
     *
     * @param millis
     * @return
     */
    public static String getYm(Long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        return sdf.format(new Date(millis));
    }

    /**
     * 获取上月月末的时间戳
     *
     * @param epochSecond
     * @return
     */
    public static long getLastMonthMills(Long epochSecond) {
        SimpleDateFormat sdf = new SimpleDateFormat(Y_M_D);
        Calendar cale = Calendar.getInstance();
        if (epochSecond != null && epochSecond != 0L) {
            Date date = new Date();
            date.setTime(epochSecond);
            cale.setTime(date);
        }

        int firstDay = cale.getActualMinimum(Calendar.DAY_OF_MONTH);//获取月最小天数
        cale.set(Calendar.DAY_OF_MONTH, firstDay);//设置日历中月份的最小天数
        return cale.getTimeInMillis() - ONEDAYMILLIS;
    }

    /**
     * 获取某个月的月初日期
     *
     * @param epochSecond
     * @return
     */
    public static String getMonthFirstDay(Long epochSecond) {
        SimpleDateFormat sdf = new SimpleDateFormat(Y_M_D);
        Calendar cale = Calendar.getInstance();
        if (epochSecond != null && epochSecond != 0L) {
            Date date = new Date();
            date.setTime(epochSecond);
            cale.setTime(date);
        }

        int firstDay = cale.getActualMinimum(Calendar.DAY_OF_MONTH);//获取月最小天数
        cale.set(Calendar.DAY_OF_MONTH, firstDay);//设置日历中月份的最小天数
        return sdf.format(cale.getTime());
    }

    /**
     * 获取某个月的月末日期
     *
     * @param epochSecond
     * @return
     */
    public static String getMonthLastDay(Long epochSecond) {
        SimpleDateFormat sdf = new SimpleDateFormat(Y_M_D);
        Calendar cale = Calendar.getInstance();
        if (epochSecond != null && epochSecond != 0L) {
            Date date = new Date();
            date.setTime(epochSecond);
            cale.setTime(date);
        }

        int lastDay = cale.getActualMaximum(Calendar.DAY_OF_MONTH);//获取月最大天数
        cale.set(Calendar.DAY_OF_MONTH, lastDay);//设置日历中月份的最大天数
        return sdf.format(cale.getTime());
    }

    /**
     * 获取day所在月，月初日期
     *
     * @param day
     * @return
     */
    public static String getMonthFirstDay(String day, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar cale = Calendar.getInstance();
        Date date = sdf.parse(day);
        cale.setTime(date);

        int firstDay = cale.getActualMinimum(Calendar.DAY_OF_MONTH);//获取月最小天数
        cale.set(Calendar.DAY_OF_MONTH, firstDay);//设置日历中月份的最小天数
        return sdf.format(cale.getTime());
    }

    /**
     * 获取day所在月，月末日期
     *
     * @param day
     * @return
     */
    public static String getMonthLastDay(String day, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar cale = Calendar.getInstance();
        Date date = sdf.parse(day);
        cale.setTime(date);
        int lastDay = cale.getActualMaximum(Calendar.DAY_OF_MONTH);//获取月最大天数
        cale.set(Calendar.DAY_OF_MONTH, lastDay);//设置日历中月份的最大天数
        return sdf.format(cale.getTime());
    }

    /**
     * 根据星期获取日期
     *
     * @param week 星期几 1代码星期日，2代表星期一。。。7代表星期六
     * @return
     */
    public static Date getDateByWeek(int week) {
        int targetWeek = week;
        Calendar c = Calendar.getInstance();
        // 当前日期星期数
        int currWeek = c.get(Calendar.DAY_OF_WEEK);
        do {
            if (currWeek == targetWeek) {
                // 如果所给星期数和当前日期星期数相等则向后推7天
                c.add(Calendar.DAY_OF_MONTH, 7);
            } else if (currWeek < week) {
                currWeek--;
            }
        } while (currWeek != targetWeek);
        return c.getTime();
    }

    /**
     * 根据年月获取月末最后一天日期
     *
     * @param year
     * @param month
     * @return
     */
    public static String getLastDayOfOneMonth(int year, int month, String format) {
        Calendar cale = Calendar.getInstance();

        cale.set(Calendar.YEAR, year);//赋值年份
        cale.set(Calendar.MONTH, month - 1);//赋值月份
        int lastDay = cale.getActualMaximum(Calendar.DAY_OF_MONTH);//获取月最大天数
        cale.set(Calendar.DAY_OF_MONTH, lastDay);//设置日历中月份的最大天数
        SimpleDateFormat sdf = new SimpleDateFormat(format);    //格式化日期yyyy-MM-dd
        String lastDayOfMonth = sdf.format(cale.getTime());
        return lastDayOfMonth;
    }

    /**
     * 获取某月的第一天
     *
     * @param timeSecs 时间戳13位ms
     * @return
     */
    public static Date getFirstDayOfOneMonth(Long timeSecs) {
        Calendar cale = Calendar.getInstance();
        if (timeSecs != null && timeSecs != 0L) {
            Date date = new Date();
            date.setTime(timeSecs);
            cale.setTime(date);
        }
        int lastDay = cale.getActualMinimum(Calendar.DAY_OF_MONTH);//获取月最大天数
        cale.set(Calendar.DAY_OF_MONTH, lastDay);//设置日历中月份的最大天数
        return cale.getTime();
    }

    /**
     * 获取某月的第一天
     *
     * @param timeSecs 时间戳13位ms
     * @param format   格式化字符串
     * @return
     */
    public static String getFirstDayOfOneMonth(Long timeSecs, String format) {
        Calendar cale = Calendar.getInstance();
        if (timeSecs != null && timeSecs != 0L) {
            Date date = new Date();
            date.setTime(timeSecs);
            cale.setTime(date);
        }
        int lastDay = cale.getActualMinimum(Calendar.DAY_OF_MONTH);//获取月最大天数
        cale.set(Calendar.DAY_OF_MONTH, lastDay);//设置日历中月份的最大天数
        SimpleDateFormat sdf = new SimpleDateFormat(format);//格式化日期yyyy-MM-dd
        String lastDayOfMonth = sdf.format(cale.getTime());
        return lastDayOfMonth;
    }

    /**
     * 根据年月获取月初第一天日期
     *
     * @param year   年
     * @param month  月
     * @param format 格式化日期
     * @return
     */
    public static String getFirstDayOfOneMonth(int year, int month, String format) {
        Calendar cale = Calendar.getInstance();

        cale.set(Calendar.YEAR, year);    //赋值年份
        cale.set(Calendar.MONTH, month - 1);//赋值月份
        int lastDay = cale.getActualMinimum(Calendar.DAY_OF_MONTH);//获取月最大天数
        cale.set(Calendar.DAY_OF_MONTH, lastDay);//设置日历中月份的最大天数
        SimpleDateFormat sdf = new SimpleDateFormat(format);//格式化日期yyyy-MM-dd
        String lastDayOfMonth = sdf.format(cale.getTime());
        return lastDayOfMonth;
    }

    /**
     * 获取某月的最后一天
     *
     * @param timeSecs 时间戳12位毫秒
     * @param format   格式化日期
     * @return
     */
    public static String getLastDayOfOneMonth(Long timeSecs, String format) {
        Calendar cale = Calendar.getInstance();
        if (timeSecs != null && timeSecs != 0L) {
            Date date = new Date();
            date.setTime(timeSecs);
            cale.setTime(date);
        }

        int lastDay = cale.getActualMaximum(Calendar.DAY_OF_MONTH);//获取月最大天数
        cale.set(Calendar.DAY_OF_MONTH, lastDay);//设置日历中月份的最大天数
        SimpleDateFormat sdf = new SimpleDateFormat(format);//格式化日期yyyy-MM-dd
        String lastDayOfMonth = sdf.format(cale.getTime());
        return lastDayOfMonth;
    }

    /**
     * 获取某月的最后一天
     *
     * @param timeSecs 时间戳12位毫秒
     * @return
     */
    public static Date getLastDayOfOneMonth(Long timeSecs) {
        Calendar cale = Calendar.getInstance();
        if (timeSecs != null && timeSecs != 0L) {
            Date date = new Date();
            date.setTime(timeSecs);
            cale.setTime(date);
        }

        int lastDay = cale.getActualMaximum(Calendar.DAY_OF_MONTH);//获取月最大天数
        cale.set(Calendar.DAY_OF_MONTH, lastDay);//设置日历中月份的最大天数

        return cale.getTime();
    }

    /**
     * 根据日期获取周
     *
     * @param date
     * @return 45
     */
    public static int getWeek(Date date) {
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(date);
        return g.get(Calendar.WEEK_OF_YEAR); //获得周数
    }

    /**
     * 根据日期获取年+周
     *
     * @param day 20221103
     * @return 202245
     */
    public static String getWeek(String day) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YMD);
        Date date = simpleDateFormat.parse(day);
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(date);
//        g.get(Calendar.WEEK_OF_YEAR); //获得周数
        return day.substring(0, 4) + g.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 日期格式化
     */
    public static String format(Calendar c, String pattern) {
        Calendar calendar = null;
        if (c != null) {
            calendar = c;
        } else {
            calendar = Calendar.getInstance();
        }
        if (pattern == null || pattern.equals("")) {
            pattern = YMD;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(calendar.getTime());
    }

    /**
     * 获得day所在的周，星期一的日期
     *
     * @param dateStr 年月日
     * @return 根据pattern决定格式
     */
    public static String getWeekFirstDay(String dateStr, String pattern) throws ParseException {
        Calendar strDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YMD);
        Date date = simpleDateFormat.parse(dateStr);
        strDate.setTime(date);
        int day = getIntWeekOfDate(strDate.getTimeInMillis());
        strDate.add(Calendar.DATE, -(day - 2));
        return format(strDate, pattern);
    }

    /**
     * 获得day所在的周，星期日的日期
     *
     * @param dateStr 年月日
     * @return 根据pattern决定格式
     */
    public static String getWeekLastDay(String dateStr, String pattern) throws ParseException {
        Calendar strDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YMD);
        Date date = simpleDateFormat.parse(dateStr);
        strDate.setTime(date);
        int day = getIntWeekOfDate(strDate.getTimeInMillis());
        strDate.add(Calendar.DATE, 8 - day);
        return format(strDate, pattern);
    }

    /**
     * 获取指定周的第一天
     *
     * @param year 年
     * @param week 月
     * @return
     */
    public static Date getFirstDayOfWeek(int year, int week) {
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置周
        cal.set(Calendar.WEEK_OF_YEAR, week + 1);
        // 设置该周第一天为星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 设置该周第一天为星期一
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }

    /**
     * 获取指定周的最后一天
     *
     * @param year
     * @param week
     * @return
     */
    public static Date getLastDayOfWeek(int year, int week) {
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置周
        cal.set(Calendar.WEEK_OF_YEAR, week + 1);
        // 设置该周第一天为星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 设置最后一天是星期日
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // Sunday
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        return cal.getTime();
    }


}
