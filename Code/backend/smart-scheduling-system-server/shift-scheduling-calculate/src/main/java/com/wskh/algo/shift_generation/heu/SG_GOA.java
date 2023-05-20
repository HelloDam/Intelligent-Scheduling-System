package com.wskh.algo.shift_generation.heu;

import com.dam.exception.SSSException;
import com.dam.model.dto.scheduling_calculate.Shift;
import com.dam.model.dto.scheduling_calculate.TimeFrame;
import com.dam.model.enums.ResultCodeEnum;
import com.wskh.utils.PlotUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SG_GOA {

    /**
     * 一天的每个时间段
     **/
    TimeFrame[] timeFrames;
    /**
     * 每个时间段中，每分钟所需要的员工数
     **/
    int[] employeesRequiredArr;
    /**
     * 每个时间段中，正在工作的员工
     **/
    int[] employeesWorkingArr;
    /**
     * 最大连续段数和最小连续段数
     **/
    int minC;
    int maxC;
    /**
     * 间隔段数（呈倍数）
     **/
    int intervalC;
    /**
     * 每一段代表多少分钟
     **/
    int minuteEachC;
    /**
     * 午餐、晚餐范围
     **/
    int[] lunchFrame;
    int[] dinnerFrame;
    /**
     * 午餐、晚餐需要占用的段数
     **/
    int lunchC;
    int dinnerC;
    /**
     * 员工总数
     **/
    int employeesNum;
    List<Shift> shiftList;
    ;

    // 构造函数
    public SG_GOA(TimeFrame[] timeFrames, int[] employeesRequiredArr, int employeesNum, int minC, int maxC, int intervalC, int minuteEachC, int[] lunchFrame, int[] dinnerFrame, int lunchC, int dinnerC) {
        this.timeFrames = timeFrames;
        this.employeesRequiredArr = employeesRequiredArr;
        this.employeesNum = employeesNum;
        this.minC = minC;
        this.maxC = maxC;
        this.intervalC = intervalC;
        this.minuteEachC = minuteEachC;
        this.lunchFrame = lunchFrame;
        this.dinnerFrame = dinnerFrame;
        this.lunchC = lunchC;
        this.dinnerC = dinnerC;
    }

    // 求解主函数
    public List<Shift> solve() throws SSSException {
        // 初始化班次列表
        shiftList = new ArrayList<>();
        // 初始化employeesWorkingArr
        employeesWorkingArr = new int[employeesRequiredArr.length];
        // 记录上一次的午餐晚餐时间
        int[] lastMealTime = null;
        // 遍历每个时间段，看看有没有没被覆盖的
        for (int i = 0; i < employeesRequiredArr.length; i++) {
            if (employeesWorkingArr[i] < employeesRequiredArr[i]) {
                // 当前时间段还没有完全被覆盖
                // 有两种方法覆盖它：（1）将旧的班次延长（2）新增班次
                // 首先尝试将旧的班次延长
                boolean b = true;
                for (int j = shiftList.size() - 1; j >= 0; j--) {
                    // 如果旧班次本来旧覆盖了当前段，那就不能延长
                    if (shiftList.get(j).getHead() <= i && shiftList.get(j).getHead() + shiftList.get(j).getLen() > i) {
                        continue;
                    }
                    // 如果可以整除那就延长（因为班次的时长要求是intervalC的整数倍）
                    int len = shiftList.get(j).getLen() + intervalC;
                    if (len <= maxC && shiftList.get(j).getHead() + len < employeesRequiredArr.length && len % intervalC == 0 && i >= shiftList.get(j).getHead() && i < shiftList.get(j).getHead() + len) {
                        changeEmployeesWorkingArr(shiftList.get(j).getHead() + shiftList.get(j).getLen(), intervalC, 1);
                        shiftList.get(j).setLen(len);
                        // 延长之后，就要判断其是否覆盖午餐晚餐时间了
                        if (shiftList.get(j).getMealType() == null) {
                            int mealHead = -1;
                            int mealLen = -1;
                            Short mealType = null;
                            if (isCoverLunch(shiftList.get(j).getHead(), shiftList.get(j).getLen())) {
                                mealType = 0;
                                mealLen = lunchC;
                                if (lastMealTime == null || lastMealTime[2] == 1) {
                                    mealHead = lunchFrame[0];
                                } else {
                                    mealHead = lastMealTime[0] + 1;
                                    if (mealHead + mealLen - 1 > lunchFrame[1]) {
                                        mealHead = lunchFrame[0];
                                    }
                                }
                            } else if (isCoverDinner(shiftList.get(j).getHead(), shiftList.get(j).getLen())) {
                                mealType = 1;
                                mealLen = dinnerC;
                                if (lastMealTime == null || lastMealTime[2] == 0) {
                                    mealHead = dinnerFrame[0];
                                } else {
                                    mealHead = lastMealTime[0] + 1;
                                    if (mealHead + mealLen - 1 > dinnerFrame[1]) {
                                        mealHead = dinnerFrame[0];
                                    }
                                }
                            }
                            if (mealType != null) {
                                changeEmployeesWorkingArr(mealHead, mealLen, -1);
//                                if (lastMealTime != null && lastMealTime[0] == mealHead && lastMealTime[1] == mealLen) {
//                                    throw new RuntimeException("此题无解");
//                                }
                                shiftList.get(j).setMealHead(mealHead);
                                shiftList.get(j).setMealLen(mealLen);
                                shiftList.get(j).setMealType(mealType);
//                                if (mealHead - 1 < i) {
//                                    i = mealHead - 1;
//                                    b = false;
//                                } else {
//                                    for (int k = 0; k < shiftList.size(); k++) {
//                                        System.out.println(k + " : " + shiftList.get(k));
//                                    }
//                                    System.out.println(j);
//                                    System.out.println(Arrays.toString(lunchFrame));
//                                    System.out.println(Arrays.toString(dinnerFrame));
//                                    throw new RuntimeException((mealHead - 1) + " , " + i + " , " + (lastMealTime == null ? "NULL" : Arrays.toString(lastMealTime)) + " , " + shiftList.get(j));
//                                }
                                i = Math.min(i, mealHead - 1);
                                b = false;
                                lastMealTime = new int[]{mealHead, mealLen, mealType};
//                                break;
                            }
                        }
                        shiftList.get(j).setTotalMinute(calcTotalMinute(shiftList.get(j).getLen(), shiftList.get(j).getMealLen(), shiftList.get(j).getMealType()));
                        if (employeesWorkingArr[i] >= employeesRequiredArr[i]) {
                            break;
                        }
                    }
                }
                // 如果延长完毕后，还没有完全覆盖
                while (b && employeesWorkingArr[i] < employeesRequiredArr[i]) {
                    // 那就新增班次（按照最小长度的班次去新增）
                    int head = i;
                    int len = minC;
                    if (head + minC >= timeFrames.length) {
                        head = timeFrames.length - minC;
                    }
                    int mealHead = -1;
                    int mealLen = -1;
                    Short mealType = null;
                    if (isCoverLunch(head, len)) {
                        mealType = 0;
                        mealLen = lunchC;
                        if (lastMealTime == null || lastMealTime[2] == 1) {
                            mealHead = lunchFrame[0];
                        } else {
                            mealHead = lastMealTime[0] + 1;
                            if (mealHead + mealLen - 1 > lunchFrame[1]) {
                                mealHead = lunchFrame[0];
                            }
                        }
                    } else if (isCoverDinner(head, len)) {
                        mealType = 1;
                        mealLen = dinnerC;
                        if (lastMealTime == null || lastMealTime[2] == 0) {
                            mealHead = dinnerFrame[0];
                        } else {
                            mealHead = lastMealTime[0] + 1;
                            if (mealHead + mealLen - 1 > dinnerFrame[1]) {
                                mealHead = dinnerFrame[0];
                            }
                        }
                    }
                    changeEmployeesWorkingArr(head, len, 1);
                    if (mealType != null) {
                        changeEmployeesWorkingArr(mealHead, mealLen, -1);
                        if (lastMealTime != null && lastMealTime[0] == mealHead && lastMealTime[1] == mealLen && lastMealTime[2] == mealType) {
                            System.out.println(Arrays.toString(lastMealTime) + " , " + mealHead + " , " + mealLen + " , " + mealType + " , " + Arrays.toString(dinnerFrame));
                            if(mealType==0){
                                throw new RuntimeException("请适当缩小午餐时间或者增大午餐范围");
                            }else if(mealType==1){
                                throw new RuntimeException("请适当缩小晚餐时间或者增大晚餐范围");
                            }else{
                                throw new RuntimeException("你妹的");
                            }
                        }
                        lastMealTime = new int[]{mealHead, mealLen, mealType};
                    }
                    shiftList.add(new Shift("", 0, 0, calcTotalMinute(len, mealLen, mealType), head, len, mealHead, mealLen, mealType));
                }
            }
        }
//        System.out.println("求解用时: " + (System.currentTimeMillis() - start) + " ms");
//        System.out.println("每个时间段中,所需要的员工  : " + Arrays.toString(employeesRequiredArr));
//        System.out.println("每个时间段中,正在工作的员工: " + Arrays.toString(employeesWorkingArr));
        return shiftList;
    }

    // 判断是否覆盖午餐时间
    private boolean isCoverLunch(int head, int len) {
        return head <= lunchFrame[0] && head + len - 1 >= lunchFrame[1];
    }

    // 判断是否在午餐时间内
    private boolean isInLunch(int head, int len) {
        return head >= lunchFrame[0] && head + len - 1 <= lunchFrame[1];
    }

    // 判断是否覆盖晚餐时间
    private boolean isCoverDinner(int head, int len) {
        return head <= dinnerFrame[0] && head + len - 1 >= dinnerFrame[1];
    }

    // 判断是否在晚餐时间内
    private boolean isInDinner(int head, int len) {
        return head >= dinnerFrame[0] && head + len - 1 <= dinnerFrame[1];
    }

    // 批量修改employeesWorkingArr
    private void changeEmployeesWorkingArr(int head, int len, int changeNum) throws SSSException {
        for (int i = head; i < head + len; i++) {
            employeesWorkingArr[i] += changeNum;
            if (employeesWorkingArr[i] > employeesNum) {
//                PlotUtil.plotShiftToExcel("D:\\WSKH\\MyData\\BachelorDegree\\大四下比赛或项目资料\\服务外包创新创业大赛\\OurGit\\smart-scheduling-system\\Code\\serve\\v1\\smart-scheduling-system-server\\shift-scheduling-calculate\\src\\main\\java\\com\\wskh\\output\\result.xls", shiftList, timeFrames, employeesRequiredArr, lunchFrame, dinnerFrame);
//                PlotUtil.plotShiftToExcel("D:\\Desktop\\result.xlsx", shiftList, timeFrames, employeesRequiredArr, lunchFrame, dinnerFrame);
                System.err.println("此题无解: " + employeesWorkingArr[i] + " > " + employeesNum);

                throw new SSSException(ResultCodeEnum.FAIL.getCode(), "客流量过大，无法排出合理的班次");
            }
        }
    }

    // 计算班次的totalMinute
    private int calcTotalMinute(int len, int mealLen, Short mealType) {
        int totalMinute = len * minuteEachC;
        if (mealType != null) {
            totalMinute -= (mealLen * minuteEachC);
        }
        return totalMinute;
    }

}
