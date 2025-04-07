package com.dfn.lsf.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.dfn.lsf.model.RemainTime;


public class LSFUtils {
    public static String getCurrentMiliSecondAsString() {
        return Long.toString(System.currentTimeMillis());
    }

    public static RemainTime getRemainTimeForGracePrd(Date inputDate, int gracePeriodMinutes) {
        if (inputDate == null) {
            return new RemainTime(0, 0, 0);
        }
        
        try {
            long currentTimeMillis = System.currentTimeMillis();
            long durationMinutes = TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis - inputDate.getTime());
            int remainingMinutes = gracePeriodMinutes - (int)durationMinutes;
            
            if (remainingMinutes <= 0) {
                return new RemainTime(0, 0, 0);
            }
            
            int days = remainingMinutes / (24 * 60);
            int hours = (remainingMinutes % (24 * 60)) / 60;
            int minutes = remainingMinutes % 60;
            
            return new RemainTime(days, hours, minutes);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating remaining time: " + e.getMessage());
        }
    }
}
