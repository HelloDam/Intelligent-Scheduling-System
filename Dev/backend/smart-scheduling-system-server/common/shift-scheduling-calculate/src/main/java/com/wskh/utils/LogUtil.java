package com.wskh.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    protected static String after = "\033[0;29m";
    public static boolean canBePrint = true;


    public static void common(String message) {
        if (canBePrint) {
            StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
            String classMsg = stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "." + stackTraceElement.getLineNumber();
            System.out.format("\033[0;29m");
            System.out.printf("%-8s", "COMMON");
            System.out.printf("%-26s", "[" + simpleDateFormat.format(new Date()) + "]");
            System.out.printf("%-1s", "[" + classMsg + "]");
            System.out.print(" : " + message + "\n");
            after();
        }
    }

    public static void debug(String message) {
        if (canBePrint) {
            StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
            String classMsg = stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "." + stackTraceElement.getLineNumber();
            System.out.format("\033[0;35m");
            System.out.printf("%-8s", "DEBUG");
            System.out.printf("%-26s", "[" + simpleDateFormat.format(new Date()) + "]");
            System.out.printf("%-1s", "[" + classMsg + "]");
            System.out.print(" : " + message + "\n");
            after();
        }
    }

    public static void info(String message) {
        if (canBePrint) {
            StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
            String classMsg = stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "." + stackTraceElement.getLineNumber();
            System.out.format("\033[0;34m");
            System.out.printf("%-8s", "INFO");
            System.out.printf("%-26s", "[" + simpleDateFormat.format(new Date()) + "]");
            System.out.printf("%-1s", "[" + classMsg + "]");
            System.out.print(" : " + message + "\n");
            after();
        }
    }

    public static void error(String message) {
        if (canBePrint) {
            StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
            String classMsg = stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "." + stackTraceElement.getLineNumber();
            System.out.format("\033[0;31m");
            System.out.printf("%-8s", "ERROR");
            System.out.printf("%-26s", "[" + simpleDateFormat.format(new Date()) + "]");
            System.out.printf("%-1s", "[" + classMsg + "]");
            System.out.print(" : " + message + "\n");
            after();
        }
    }

    public static void success(String message) {
        if (canBePrint) {
            StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
            String classMsg = stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "." + stackTraceElement.getLineNumber();
            System.out.format("\033[0;32m");
            System.out.printf("%-8s", "SUCCESS");
            System.out.printf("%-26s", "[" + simpleDateFormat.format(new Date()) + "]");
            System.out.printf("%-1s", "[" + classMsg + "]");
            System.out.print(" : " + message + "\n");
            after();
        }
    }

    public static void warning(String message) {
        if (canBePrint) {
            StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
            String classMsg = stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "." + stackTraceElement.getLineNumber();
            System.out.format("\033[0;33m");
            System.out.printf("%-8s", "WARNING");
            System.out.printf("%-26s", "[" + simpleDateFormat.format(new Date()) + "]");
            System.out.printf("%-1s", "[" + classMsg + "]");
            System.out.print(" : " + message + "\n");
            after();
        }
    }

    public static void after() {
        System.out.format(after);
    }
}
