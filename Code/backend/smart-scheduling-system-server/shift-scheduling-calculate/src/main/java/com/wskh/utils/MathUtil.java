package com.wskh.utils;

public class MathUtil {

    public final static double ERROR = 1e-06;

    public static int compareDouble(double d1, double d2) {
        if (Math.abs(d1 - d2) < ERROR) {
            return 0;
        } else {
            return d1 > d2 ? 1 : -1;
        }
    }

}
