package com.zjrc.isale.client.util;

import java.util.Calendar;
import java.util.Date;

public class WeekDay {

    public WeekDay() {
        super();
    }

    public static String getWeekDayString()
    {
        String weekString = "";
        final String dayNames[] = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        weekString = dayNames[dayOfWeek - 1];
        return weekString;
    }

} 