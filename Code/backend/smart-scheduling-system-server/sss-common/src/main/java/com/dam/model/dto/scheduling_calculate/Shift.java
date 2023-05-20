package com.dam.model.dto.scheduling_calculate;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class Shift {
    /**
     * 班次的唯一Key：班次所在天索引-班次在当天的索引
     **/
    String key;
    /**
     * 班次的班次所在天索引
     **/
    int dayIndex;
    /**
     * 班次在当天的索引
     **/
    int shiftIndex;
    /**
     * 班次时间（分钟）
     **/
    int totalMinute;
    /**
     * 班次的开始时间
     **/
    int head;
    /**
     * 班次的长度
     **/
    int len;
    /**
     * 午餐或者晚餐时间的开始时间
     **/
    int mealHead;
    /**
     * 午餐或者晚餐时间的长度
     **/
    int mealLen;
    /**
     * null代表没有进行午餐晚餐，0表示午餐，1表示晚餐
     **/
    Short mealType;

    public static Shift copy(Shift source) {
        if (source == null) {
            return null;
        }
        return new Shift(source.key, source.dayIndex, source.shiftIndex, source.totalMinute, source.head, source.len, source.mealHead, source.mealLen, source.mealType);
    }

    public static List<Shift> copyList(List<Shift> source) {
        if (source == null) {
            return null;
        }
        List<Shift> res = new ArrayList<>();
        for (Shift shift : source) {
            res.add(copy(shift));
        }
        return res;
    }

    public static List<List<Shift>> copyListList(List<List<Shift>> source) {
        if (source == null) {
            return null;
        }
        List<List<Shift>> res = new ArrayList<>();
        for (List<Shift> shiftList : source) {
            res.add(copyList(shiftList));
        }
        return res;
    }

    public int get01(int i) {
        if (head <= i && i <= head + len - 1) {
            if (mealType == null || (i < mealHead || i >= mealHead + mealLen)) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return dayIndex == shift.dayIndex && shiftIndex == shift.shiftIndex && totalMinute == shift.totalMinute && head == shift.head && len == shift.len && mealHead == shift.mealHead && mealLen == shift.mealLen && Objects.equals(key, shift.key) && Objects.equals(mealType, shift.mealType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, dayIndex, shiftIndex, totalMinute, head, len, mealHead, mealLen, mealType);
    }

    @Override
    public String toString() {
        return "Shift{" +
                "key = " + key +
                " , dayIndex = " + dayIndex +
                " , shiftIndex = " + shiftIndex +
                " , totalMinute = " + totalMinute +
                " , head = " + head +
                " , len = " + len +
                " , mealHead = " + mealHead +
                " , mealLen = " + mealLen +
                " , mealType = " + mealType +
                '}';
    }
}
