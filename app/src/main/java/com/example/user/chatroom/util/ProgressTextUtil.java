package com.example.user.chatroom.util;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class ProgressTextUtil {
    public static String getProgressText(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        double minute = calendar.get(Calendar.MINUTE);
        double second = calendar.get(Calendar.SECOND);
        DecimalFormat format = new DecimalFormat("00");
        return format.format(minute) + ":" + format.format(second);
    }
}
