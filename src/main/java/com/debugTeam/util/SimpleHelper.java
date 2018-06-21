package com.debugTeam.util;

import java.util.Calendar;
import java.util.Date;

public class SimpleHelper {

    //计算日期间隔
    public static int daysBetween(Date beginDate, Date endDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginDate);
        long beginTime = cal.getTimeInMillis();
        cal.setTime(endDate);
        long endTime = cal.getTimeInMillis();
        long betweenDays=(endTime-beginTime) / (1000*3600*24);
        return Integer.parseInt(String.valueOf(betweenDays));
    }

}
