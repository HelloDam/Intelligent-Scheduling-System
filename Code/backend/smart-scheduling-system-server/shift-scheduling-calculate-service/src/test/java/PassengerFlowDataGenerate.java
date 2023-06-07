import com.dam.model.dto.scheduling_calculate.Time;
import com.dam.utils.PoiExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PassengerFlowDataGenerate {

    @Test
    public void randomWorkdayPre() {
        Random random = new Random();
        //生成一个随机数，代表有几天是偏好上班的，范围是[1,7]
        int days = random.nextInt(6) + 1;
        List<Integer> preferDayList = new ArrayList<>();
        while (preferDayList.size() < days) {
            int day = random.nextInt(6) + 1;
            if (preferDayList.indexOf(day) == -1) {
                preferDayList.add(day);
            }
        }
        Collections.sort(preferDayList);
        String join = StringUtils.join(preferDayList, "|");
        System.out.println(join);
    }

    /**
     * @return HashMap<int [ ], Integer> 键：数组（小时、分钟） 值：这一分钟的客流量
     * @throws Exception
     */
    public HashMap<String, Double> readData() throws Exception {
        String path = "D:\\WSKH\\MyData\\BachelorDegree\\大四下比赛或项目资料\\服务外包创新创业大赛\\OurGit\\smart-scheduling-system\\Code\\serve\\v1\\smart-scheduling-system-server\\file\\data\\购物量数据.xlsx";
        Workbook workbook = new XSSFWorkbook(path);
        Sheet sheet = workbook.getSheetAt(0);
        HashSet<String> daySet = new HashSet<>();
        HashMap<String, Double> timeStrNumMap = new HashMap<>();
        for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            String strDate = PoiExcelUtil.getCellValue(row.getCell(2));
            int buyNum = (int) Math.round(row.getCell(8).getNumericCellValue());
            Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(strDate);
            daySet.add(date.getYear() + "-" + date.getMonth() + "-" + date.getDay());
            int[] timeFrame = new int[]{date.getHours(), date.getMinutes()};
            timeStrNumMap.put(Arrays.toString(timeFrame), timeStrNumMap.getOrDefault(timeFrame, 0.0) + buyNum);
        }
        for (Map.Entry<String, Double> entry : timeStrNumMap.entrySet()) {
            timeStrNumMap.put(entry.getKey(), (double) entry.getValue() / daySet.size());
        }
//        for (Map.Entry<String, Double> entry : timeStrNumMap.entrySet()) {
//            System.out.println("key:"+entry.getKey()+",value:"+entry.getValue());
//        }
        workbook.close();
        return timeStrNumMap;
    }

    @Test
    public void generateData() throws Exception {
//        String startDateStr = "2023-1-1";
//        String endDateStr = "2023-1-31";

//        String startDateStr = "2023-2-1";
//        String endDateStr = "2023-2-28";

//        String startDateStr = "2023-3-1";
//        String endDateStr = "2023-3-31";

//        String startDateStr = "2023-4-1";
//        String endDateStr = "2023-4-30";

//        String startDateStr = "2023-5-1";
//        String endDateStr = "2023-5-31";
//
//        String startDateStr = "2023-6-1";
//        String endDateStr = "2023-6-30";
//
//        String startDateStr = "2023-7-1";
//        String endDateStr = "2023-7-31";
//
        String startDateStr = "2023-8-1";
        String endDateStr = "2023-8-31";
//
//        String startDateStr = "2023-9-1";
//        String endDateStr = "2023-9-30";
//
//        String startDateStr = "2023-10-1";
//        String endDateStr = "2023-10-31";
//
//        String startDateStr = "2023-11-1";
//        String endDateStr = "2023-11-30";
//
//        String startDateStr = "2023-12-1";
//        String endDateStr = "2023-12-31";

//        String startDateStr = "2023-3-1";
//        String endDateStr = "2023-3-2";

//        String fileName = "时尚鞋屋分店";
//        String fileName = "足迹鞋业分店";
//        String fileName = "绿意运动分店";
        String fileName = "爱鞋分店";

//        String filePath = "D:\\Projects\\damFile\\SmartSchedulingSystem\\smart-scheduling-system\\数据\\客流量数据\\" + fileName;
        //魏谦
        String filePath = "D:\\Projects\\intelligent-scheduling-system\\Code\\backend\\smart-scheduling-system-server\\file\\data\\客流量数据\\" + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String path = filePath + "\\" + startDateStr + " " + endDateStr + ".xlsx";
//        String path = "D:\\WSKH\\MyData\\BachelorDegree\\大四下比赛或项目资料\\服务外包创新创业大赛\\OurGit\\smart-scheduling-system\\数据\\客流量数据\\" + startDateStr + " " + endDateStr + ".xlsx";
        long dayNum;
//        Time startTime = new Time(8, 0);
//        Time endTime = new Time(23, 0);
        Time startTime = new Time(0, 0);
        Time endTime = new Time(24, 0);
        int duration = 30;
        //输入员工数
        double maxPassengerFlow = 354 * 1.0 / 2;
        long oneDateTime = 3600 * 1000 * 24;

        XSSFWorkbook workbook = new XSSFWorkbook();
        FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
        int curRow = 1;
        Random random = new Random();

        XSSFSheet sheet = workbook.createSheet();
        //创建标题行
        XSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("门店ID");
        row.createCell(1).setCellValue("日期");
        row.createCell(2).setCellValue("开始时间");
        row.createCell(3).setCellValue("结束时间");
        row.createCell(4).setCellValue("预测客流量");

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-M-d");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/M/d");
        Date startDate;
        Date endDate;
        try {
            startDate = sdf1.parse(startDateStr);
            endDate = sdf1.parse(endDateStr);
            //计算有多少天
            dayNum = (endDate.getTime() - startDate.getTime()) / oneDateTime;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        //获取别人数据的每分钟的购买量
//        HashMap<String, Double> timeNumMap = this.readData();
        for (int i = 0; i < dayNum; i++) {
            Date date = new Date(startDate.getTime() + oneDateTime * i);
            int startMinute = getTotalMinuteNum(startTime);
            int endMinute = getTotalMinuteNum(endTime);
            int segmentNum = (endMinute - startMinute) / duration;

            Time start = new Time(startTime.getHour(), startTime.getMinute());
            for (int j = 0; j < segmentNum; j++) {

                Time end = getTimeAfterAdd(start, duration);

                //获取这个时间段的购买量
                double totalBugNum = 0;
                for (int k = getTotalMinuteNum(start); k < getTotalMinuteNum(end); k++) {
//                    System.out.println();
                    int hour = k / 60;
                    int min = k % 60;
//                    Double num = timeNumMap.get(Arrays.toString(new int[]{hour, min}));
//                    totalBugNum += num == null ? 0 : num;
                }
                totalBugNum /= segmentNum;
//                System.out.println("totalBugNum:"+totalBugNum);

                XSSFRow passengerFlowVo = sheet.createRow(curRow++);
                passengerFlowVo.createCell(1).setCellValue(sdf2.format(date));
                passengerFlowVo.createCell(2).setCellValue(start.getHour() + ":" + start.getMinute());
                passengerFlowVo.createCell(3).setCellValue(end.getHour() + ":" + end.getMinute());
                passengerFlowVo.createCell(4).setCellValue(maxPassengerFlow * (random.nextDouble() * 0.4 + 0.6));
//                passengerFlowVo.createCell(4).setCellValue(maxPassengerFlow * totalBugNum*(random.nextDouble()*0.4+0.6));
//                passengerFlowVo.createCell(4).setCellValue(totalBugNum);
                start = end;
            }
        }

        try {
            workbook.write(fileOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算Time有多少分钟
     *
     * @param time
     * @return
     */
    private int getTotalMinuteNum(Time time) {
        return time.getHour() * 60 + time.getMinute();
    }

    /**
     * 将start加上duration分钟，返回新的time
     *
     * @param start
     * @param duration
     * @return
     */
    private Time getTimeAfterAdd(Time start, int duration) {
        int endMinute = getTotalMinuteNum(start) + duration;
        return new Time(endMinute / 60, endMinute % 60);
    }

}
