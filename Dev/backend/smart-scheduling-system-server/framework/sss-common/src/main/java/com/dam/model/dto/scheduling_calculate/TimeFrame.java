package com.dam.model.dto.scheduling_calculate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author：WSKH
 * @ClassName：TimeFrame
 * @ClassType：
 * @Description：
 * @Date：2023/1/7/15:53
 * @Email：1187560563@qq.com
 * @Blog：https://blog.csdn.net/weixin_51545953?type=blog
 */
@Data
@NoArgsConstructor
public class TimeFrame {

    /**
     * 最早时间
     **/
    Time earliestTime;
    /**
     * 最晚时间
     **/
    Time latestTime;
    /**
     * 这个范围的时长(h)
     **/
    double duration;

    public TimeFrame(Time earliestTime, Time latestTime) {
        if (latestTime.compareTo(earliestTime) < 0) {
            throw new RuntimeException("TimeFrame Init Error: latestTime < earliestTime ! " + latestTime + " < " + earliestTime);
        }
        this.earliestTime = earliestTime;
        this.latestTime = latestTime;
        this.duration = latestTime.diff(earliestTime);
    }

    public static TimeFrame copy(TimeFrame t) {
        TimeFrame timeFrame = new TimeFrame();
        timeFrame.duration = t.duration;
        timeFrame.earliestTime = Time.copy(t.earliestTime);
        timeFrame.latestTime = Time.copy(t.latestTime);
        return timeFrame;
    }

    public static TimeFrame[] copy(TimeFrame[] ts) {
        if (ts == null) {
            return null;
        }
        TimeFrame[] timeFrames = new TimeFrame[ts.length];
        for (int i = 0; i < timeFrames.length; i++) {
            timeFrames[i] = TimeFrame.copy(ts[i]);
        }
        return timeFrames;
    }

    @Override
    public String toString() {
        return "TimeFrame{" +
                "earliestTime = " + earliestTime +
                " , latestTime = " + latestTime +
                " , duration = " + duration + " min" +
                '}';
    }
}
