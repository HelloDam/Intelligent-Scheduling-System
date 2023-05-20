package com.wskh.utils;

import com.dam.model.dto.scheduling_calculate.Shift;
import com.dam.model.dto.scheduling_calculate.TimeFrame;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class PlotUtil {
    public static void plotShiftToExcel(String path, List<Shift> shiftList, TimeFrame[] timeFrames, int[] employeesRequiredArr, int[] lunchFrame, int[] dinnerFrame) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();
            int width = 2;
            shiftList.sort(new Comparator<Shift>() {
                @Override
                public int compare(Shift o1, Shift o2) {
                    int compare = Integer.compare(o1.getHead(), o2.getHead());
                    return compare != 0 ? compare : -Integer.compare(o1.getLen(), o2.getLen());
                }
            });
            CellStyle style1 = style1(workbook);
            CellStyle style2 = style2(workbook);
            CellStyle style3 = style3(workbook);
            CellStyle style4 = style4(workbook);
            XSSFRow[] rows = new XSSFRow[employeesRequiredArr.length + 1];
            for (int i = 0; i <= employeesRequiredArr.length; i++) {
                rows[i] = sheet.createRow(i);
            }
            sheet.setColumnWidth(0, 252 * 30 + 323);
            for (int i = 0; i < timeFrames.length; i++) {
                rows[i + 1].createCell(0).setCellValue(timeFrames[i].getEarliestTime() + " ~ " + timeFrames[i].getLatestTime());
                if ((i >= lunchFrame[0] && i <= lunchFrame[1]) || (i >= dinnerFrame[0] && i <= dinnerFrame[1])) {
                    rows[i + 1].getCell(0).setCellStyle(style4);
                } else {
                    rows[i + 1].getCell(0).setCellStyle(style3);
                }
            }
            int[] employeesWorkingArr = new int[employeesRequiredArr.length];
            for (int i = 0; i < shiftList.size(); i++) {
                sheet.setColumnWidth(i + 1, 252 * width + 323);
                Shift shift = shiftList.get(i);
                for (int j = shift.getHead(); j < shift.getHead() + shift.getLen(); j++) {
                    XSSFCell cell = rows[j + 1].createCell(i + 1);
                    cell.setCellStyle(style1);
                    if (shift.getMealType() != null && j >= shift.getMealHead() && j < shift.getMealHead() + shift.getMealLen()) {
                        cell.setCellValue(shift.getMealType() == 0 ? "午" : "晚");
                    } else {
                        employeesWorkingArr[j] += 1;
                    }
                }
            }
            sheet.setColumnWidth(shiftList.size() + 1, 252 * width + 323);
            sheet.setColumnWidth(shiftList.size() + 2, 252 * width + 323);
            sheet.setColumnWidth(shiftList.size() + 3, 252 * width + 323);
            sheet.setColumnWidth(shiftList.size() + 4, 252 * width + 323);
            rows[0].createCell(0).setCellValue("Time Frame");
            rows[0].createCell(1).setCellValue("Gantt Chart");
            rows[0].createCell(shiftList.size() + 1).setCellValue("W");
            rows[0].createCell(shiftList.size() + 2).setCellValue("R");
            rows[0].createCell(shiftList.size() + 3).setCellValue("D");
            rows[0].createCell(shiftList.size() + 4).setCellValue("C");
            rows[0].getCell(0).setCellStyle(style2);
            rows[0].getCell(1).setCellStyle(style2);
            rows[0].getCell(shiftList.size() + 1).setCellStyle(style2);
            rows[0].getCell(shiftList.size() + 2).setCellStyle(style2);
            rows[0].getCell(shiftList.size() + 3).setCellStyle(style2);
            rows[0].getCell(shiftList.size() + 4).setCellStyle(style2);
            for (int i = 1; i <= employeesWorkingArr.length; i++) {
                rows[i].createCell(shiftList.size() + 1).setCellValue(employeesWorkingArr[i - 1]);
                rows[i].createCell(shiftList.size() + 2).setCellValue(employeesRequiredArr[i - 1]);
                rows[i].createCell(shiftList.size() + 3).setCellValue(employeesWorkingArr[i - 1] - employeesRequiredArr[i - 1]);
                rows[i].createCell(shiftList.size() + 4).setCellValue(employeesWorkingArr[i - 1] - employeesRequiredArr[i - 1] < 0 ? "❌" : "✔️");
                rows[i].getCell(shiftList.size() + 1).setCellStyle(style3);
                rows[i].getCell(shiftList.size() + 2).setCellStyle(style3);
                rows[i].getCell(shiftList.size() + 3).setCellStyle(style3);
                rows[i].getCell(shiftList.size() + 4).setCellStyle(style3);
            }
            // 合并单元格
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, shiftList.size()));
            workbook.write(Files.newOutputStream(Paths.get(path)));
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static CellStyle style1(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font0 = workbook.createFont();
        font0.setColor(IndexedColors.BLACK.getIndex()); // 红色
        font0.setBold(true); // 加粗
        style.setFont(font0);
        // 背景色
        style.setFillPattern(FillPatternType.BIG_SPOTS);
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());
        style.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private static CellStyle style2(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font0 = workbook.createFont();
        font0.setColor(IndexedColors.WHITE.getIndex()); // 红色
        font0.setBold(true); // 加粗
        style.setFont(font0);
        // 背景色
        style.setFillPattern(FillPatternType.BIG_SPOTS);
        //
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        style.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private static CellStyle style3(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font0 = workbook.createFont();
        font0.setColor(IndexedColors.BLACK.getIndex()); // 红色
        font0.setBold(true); // 加粗
        style.setFont(font0);
        // 背景色
        style.setFillPattern(FillPatternType.BIG_SPOTS);
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_TURQUOISE.getIndex());
        style.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.LIGHT_TURQUOISE.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private static CellStyle style4(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font0 = workbook.createFont();
        font0.setColor(IndexedColors.BLACK.getIndex()); // 红色
        font0.setBold(true); // 加粗
        style.setFont(font0);
        // 背景色
        style.setFillPattern(FillPatternType.BIG_SPOTS);
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        style.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

}
