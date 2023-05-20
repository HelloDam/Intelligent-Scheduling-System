package com.dam.utils.user_data_gererate;

import com.dam.model.dto.scheduling_calculate.Time;
import com.dam.model.dto.scheduling_calculate.TimeFrame;
import com.dam.model.entity.system.UserEntity;
import com.dam.service.impl.UserServiceImpl;
import com.dam.utils.user_data_gererate.name.BuildChineseNameUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class UserDataGenerateUtil {

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString().replace("-", "").substring(0, 6));
        for (int i = 0; i < 100; i++) {
            System.out.println(new Random().nextInt(2));
        }

    }

    public List<UserEntity> generateUserData(int num) {
        Random random = new Random();
        BuildChineseNameUtil buildChineseNameUtil = new BuildChineseNameUtil();
        List<UserEntity> userEntityList = new ArrayList<>();
        for (int i = 0; i < num; ) {
            UserEntity userEntity = new UserEntity();
            userEntity.setPhone("21323124145");
            userEntity.setMail("dasda@qq.com");
            userEntity.setEnterpriseId(7L);
            userEntity.setStoreId(4L);
            userEntity.setType(10);
            userEntity.setPassword("123456");
            userEntity.setQq("5456468");
            userEntity.setGender(random.nextInt(2));
            //18岁到25岁
            userEntity.setAge(userEntity.getGender() == 0 ? random.nextInt(60 - 18 + 1) + 18 : random.nextInt(50 - 18 + 1) + 18);
            userEntity.setAvatar("头像.png");
            userEntity.setStatus(0);

            String name = buildChineseNameUtil.randomName(userEntity.getGender());
            userEntity.setName(name);
            userEntity.setNickname(name);
            userEntity.setUsername(UUID.randomUUID().toString().replace("-", "").substring(0, 6));
            userEntity.setWorkDayPreference(UserDataGenerateUtil.preferDay());
            userEntity.setWorkTimePreference(UserDataGenerateUtil.TimePreference());
            userEntity.setShiftLengthPreferenceOneDay(UserDataGenerateUtil.TimePreferenceOneDay());
            userEntity.setShiftLengthPreferenceOneWeek(UserDataGenerateUtil.TimePreferenceOneWeek());

            if (judge(userEntity)) {
                i++;
                userEntityList.add(userEntity);
                System.out.println("userEntity-" + userEntityList.size() + ": " + userEntity);
            }

        }
        return userEntityList;
    }

    private boolean judge(UserEntity userEntity) {
        // 偏好工作日大于等于5天（根据劳动法，双休）
        if (userEntity.getWorkDayPreference().split("\\|").length >= 5) {
            String[] timeFrameStrArr = userEntity.getWorkTimePreference().split("\\|");
            int totalM = 0;
            for (int j = 0; j < timeFrameStrArr.length; j++) {
                String[] timeStrArr = timeFrameStrArr[j].split("~");
                totalM += new TimeFrame(parseStrToTime(timeStrArr[0]), parseStrToTime(timeStrArr[1])).getDuration();
            }
            // 每天偏好的时间段总时长大于等于4小时（）
            if (totalM >= 4 * 60) {
                Time time1 = parseStrToTime(userEntity.getShiftLengthPreferenceOneDay());
                Time time2 = parseStrToTime(userEntity.getShiftLengthPreferenceOneWeek());
                // 每天最大工作时间大于等于8小时
                if (time1.getHour() * 60 + time1.getMinute() >= 8 * 60) {
                    // 每周最大工作时间大于等于40小时
                    if (time2.getHour() * 60 + time2.getMinute() >= 8 * 5 * 60) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String preferDay() {
        Random random = new Random();
        int days = random.nextInt(7) + 1;
        List<Integer> preferDayList = new ArrayList<>();
        while (preferDayList.size() < days) {
            int day = random.nextInt(7) + 1;
            if (preferDayList.indexOf(day) == -1) {
                preferDayList.add(day);
            }
        }
        Collections.sort(preferDayList);
        String join = StringUtils.join(preferDayList, "|");
//        System.out.println(join);
        return join;
    }

    public static String TimePreferenceOneDay() {
        Time startTime = new Time(1, 0);
        Time endTime = new Time(10, 0);
        int duration = 30;

        int startMinute = getTotalMinuteNum(startTime);
        int endMinute = getTotalMinuteNum(endTime);

        int segmentNum = (endMinute - startMinute) / duration;

        Time start = new Time(startTime.getHour(), startTime.getMinute());

        List<Time> workTimePreference = new ArrayList<>();
        for (int j = 0; j < segmentNum; j++) {
            Time end = getTimeAfterAdd(start, duration);
            start = end;
            workTimePreference.add(end);
        }
        Random random = new Random();
        int n = random.nextInt(workTimePreference.size());
        Time time = workTimePreference.get(n);

        String stringTime = time.getHour() + ":" + time.getMinute();
        return stringTime;
    }

    public static String TimePreferenceOneWeek() {
        Time startTime = new Time(8, 0);
        Time endTime = new Time(50, 0);
        int duration = 30;

        int startMinute = getTotalMinuteNum(startTime);
        int endMinute = getTotalMinuteNum(endTime);

        int segmentNum = (endMinute - startMinute) / duration;

        Time start = new Time(startTime.getHour(), startTime.getMinute());

        List<Time> workTimePreference = new ArrayList<>();
        for (int j = 0; j < segmentNum; j++) {
            Time end = getTimeAfterAdd(start, duration);
            start = end;
            workTimePreference.add(end);
        }
        Random random = new Random();
        int n = random.nextInt(workTimePreference.size());
        Time time = workTimePreference.get(n);

        String stringTime = time.getHour() + ":" + time.getMinute();
        return stringTime;
    }

    public static String TimePreference() {
        Time startTime = new Time(6, 0);
        Time endTime = new Time(23, 0);
        int duration = 10;
        //最小时长
        int minMinute = duration * 12;
        //最大时长
        int maxMinute = duration * 24;

        int startMinute = getTotalMinuteNum(startTime);
        int endMinute = getTotalMinuteNum(endTime);
        int segmentNum = (endMinute - startMinute) / duration;
        Time start = new Time(startTime.getHour(), startTime.getMinute());

        List<Time> workTimePreference = new ArrayList<>();
        for (int j = 0; j < segmentNum; j++) {
            Time end = getTimeAfterAdd(start, duration);
            start = end;
            workTimePreference.add(end);
        }

        Random random = new Random();
        ArrayList<String> timeFrameStrArr = new ArrayList<>();
        //最小开始时间段的索引
        int min = 0;
        //最大开始时间段的索引
        int max = workTimePreference.size() - maxMinute / duration;
        while (min < max) {
            //随机一个时间段的开始索引
            int randomStartIndex = random.nextInt(max - min) + min;
            Time first = workTimePreference.get(randomStartIndex);
            //随机这个时间段的时长
            int randomMinute = random.nextInt((maxMinute - minMinute) / duration) * duration + minMinute;
            Time second = getTimeAdd(first, randomMinute);
            timeFrameStrArr.add(first.toFormatString() + "~" + second.toFormatString());
            min = randomStartIndex + randomMinute / duration;
        }
        String join = StringUtils.join(timeFrameStrArr, "|");
        return join;
    }


    /**
     * 计算Time有多少分钟
     *
     * @param time
     * @return
     */
    private static int getTotalMinuteNum(Time time) {
        return time.getHour() * 60 + time.getMinute();
    }

    /**
     * 将time加上4个小时，返回新的time
     *
     * @param time
     */
    private static Time getTimeAdd(Time time, int interval) {
        int totalMinute = getTotalMinuteNum(time) + interval;
        return new Time(totalMinute / 60, totalMinute % 60);
    }

    /**
     * 将start加上duration分钟，返回新的time
     *
     * @param start
     * @param duration
     * @return
     */
    private static Time getTimeAfterAdd(Time start, int duration) {
        int endMinute = getTotalMinuteNum(start) + duration;
        return new Time(endMinute / 60, endMinute % 60);
    }

    /**
     * 解析时间字符串为时间类型 8:00
     *
     * @param formatTime
     * @return
     */
    private Time parseStrToTime(String formatTime) {
        String[] split = formatTime.split(":");
        return new Time(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

}
