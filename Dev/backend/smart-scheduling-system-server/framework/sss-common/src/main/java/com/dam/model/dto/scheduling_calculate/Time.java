package com.dam.model.dto.scheduling_calculate;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

/**
 * @Author：WSKH
 * @ClassName：Time
 * @ClassType：
 * @Description：
 * @Date：2023/1/7/15:30
 * @Email：1187560563@qq.com
 * @Blog：https://blog.csdn.net/weixin_51545953?type=blog
 */
@Data
public class Time {

    /**
     * 小时
     **/
    int hour;
    /**
     * 分钟
     **/
    int minute;

    public Time(int hour, int minute) {
//        if (hour >= 24 || hour < 0) {
//            throw new RuntimeException("hour must belong to the interval [0,24) !");
//        }
//        if (minute < 0 || minute >= 60) {
//            throw new RuntimeException("minute must belong to the interval [0,60) !");
//        }
        this.hour = hour;
        this.minute = minute;
    }

    // 比较函数
    public int compareTo(Time time) {
        int c = Integer.compare(hour, time.hour);
        return c == 0 ? Integer.compare(minute, time.minute) : c;
    }

    // 相减函数（返回相差的分钟数）
    public int diff(Time time) {
        return (hour - time.hour) * 60 + (minute - time.minute);
    }

    // 相差函数
    public int absDiff(Time time) {
        return Math.abs((hour - time.hour) * 60 + (minute - time.minute));
    }

    // 返回当前时间增加了minute后的Time
    public static Time add(Time time, int minute) {
        if (minute < 0) {
            throw new RuntimeException("minute must > 0 !");
        }
        int h = time.hour;
        int m = time.minute + minute;
        if (m >= 60) {
            m = m % 60;
            h += 1;
            if (h >= 24) {
                h = h % 24;
            }
        }
        return new Time(h, m);
    }

    public static Time copy(Time t) {
        if (t == null) {
            return null;
        }
        return new Time(t.hour, t.minute);
    }

    @Override
    public String toString() {
        return hour + " h " + minute + " min";
    }

    /**
     * 输出结果 21:00
     * @return
     */
    public String toFormatString() {
        String hourStr = hour / 10 >= 1 ? "" + hour : "0" + hour;
        String minuteStr = minute / 10 >= 1 ? "" + minute : "0" + minute;
        return hourStr + ":" + minuteStr;
    }
}
