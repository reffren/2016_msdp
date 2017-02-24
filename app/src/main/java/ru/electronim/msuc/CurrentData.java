package ru.electronim.msuc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tim on 28.02.2016.
 */
public class CurrentData {

    public static String y="y";
    public static String m= "m";

    public String nameForTableWork() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String curMonth = sdf.format(new Date());
        String cur = y+curMonth+m;
        return cur;
    }
    public String yearMonthDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String yearMotnhDay = sdf.format(new Date());
        return yearMotnhDay;
    }

    public String yearMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String yearMotnhDay = sdf.format(new Date());
        return yearMotnhDay;
    }
    public String currentTime() {
        String currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        return currentTime;
    }
}
