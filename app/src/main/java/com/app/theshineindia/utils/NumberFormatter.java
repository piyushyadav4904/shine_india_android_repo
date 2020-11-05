package com.app.theshineindia.utils;

import java.text.DecimalFormat;

public class NumberFormatter {

    public static String getRoundedString(String value_str) {
        try {
            value_str = new DecimalFormat(".##").format(value_str);
            double value_double = Double.parseDouble(value_str);
            int value_int = (int) Math.round(value_double);
            return String.valueOf(value_int);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRoundedString(double value_double) {
        try {
            int value_int = (int) Math.round(value_double);
            return String.valueOf(value_int);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String clearLastDigit(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
}
