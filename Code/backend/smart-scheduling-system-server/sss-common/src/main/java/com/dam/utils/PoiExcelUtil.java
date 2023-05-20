package com.dam.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PoiExcelUtil {

    /**
     * 读取日期格式数据
     *
     * @param cell
     * @return
     */
    public static String readDate(Cell cell) {
        if (cell == null) {
            throw new RuntimeException("单元格为空");
        } else {
            if (cell.getCellType() == CellType.NUMERIC && HSSFDateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                String value = sdf.format(date);
                return value;
            } else {
                throw new RuntimeException("单元格数据并非日期格式");
            }
        }
    }

    /**
     * 获取单元格的值
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        // 判断数据的类型
        switch (cell.getCellType()) {
            // 数字
            case NUMERIC:
                //处理yyyy年m月d日,h时mm分,yyyy年m月,m月d日等含文字的日期格式
                //判断cell.getCellStyle().getDataFormat()值，解析数值格式
                /*
                    yyyy-MM-dd----- 14
                    HH:mm:ss ---------21
                    yyyy-MM-dd HH:mm:ss ---------22
                    yyyy年m月d日--- 31
                    h时mm分 ------- 32
                    yyyy年m月------- 57
                    m月d日 ---------- 58
                    HH:mm----------- 20
                */
                short format = cell.getCellStyle().getDataFormat();
                if (format == 14) {
                    Date date = cell.getDateCellValue();
                    cellValue = DateFormatUtils.format(date, "yyyy/M/d");
                } else if (format == 31) {
                    Date date = cell.getDateCellValue();
                    cellValue = DateFormatUtils.format(date, "yyyy年M月d日");
                } else if (format == 57) {
                    Date date = cell.getDateCellValue();
                    cellValue = DateFormatUtils.format(date, "yyyy年M月");
                } else if (format == 58) {
                    Date date = cell.getDateCellValue();
                    cellValue = DateFormatUtils.format(date, "M月d日");
                } else if (format == 20) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    double value = cell.getNumericCellValue();
                    Date date = DateUtil.getJavaDate(value);
                    cellValue = sdf.format(date);
                } else if (format == 32) {
                    SimpleDateFormat sdf = new SimpleDateFormat("H时mm分");
                    double value = cell.getNumericCellValue();
                    Date date = DateUtil.getJavaDate(value);
                    cellValue = sdf.format(date);
                } else if (format == 21) {
                    Date date = cell.getDateCellValue();
                    cellValue = DateFormatUtils.format(date, "HH:mm:ss");
                } else if (format == 22) {
                    Date date = cell.getDateCellValue();
                    cellValue = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
                } else if (cell.getCellStyle().getDataFormat() == 0) {//处理数值格式
                    cell.setCellType(CellType.STRING);
                    cellValue = String.valueOf(cell.getRichStringCellValue().getString());
                } else if (cell.toString().indexOf("%") != -1) {
                    // 判断是否是百分数类型
                    // cellValue = cell.getNumericCellValue() * 100 + "%";
                    cellValue = cell.getNumericCellValue() * 100 + "";
                }else if(format == 176){
                    Date date = cell.getDateCellValue();
                    cellValue = DateFormatUtils.format(date, "yyyy/MM/dd HH:mm");
                }else {
                    cellValue = cell.toString();
                }
                break;
            case STRING: // 字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case BOOLEAN: // Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA: // 公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case BLANK: // 空值
                cellValue = null;
                break;
            case ERROR: // 故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        if (StringUtils.isNotEmpty(cellValue)) {
            return cellValue.trim();
        }
        return cellValue;
    }

}
