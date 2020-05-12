package com.scan.bluezoneid;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Convert time to UTC
 * @author khanhxu
 */
class BluezoneDate {

    private long mTime;

    /**
     * Init
     * @param time
     */
    BluezoneDate(long time) {
        mTime = convertTimeZoneUtc(time);
    }

    /**
     * Convert Time Zone UTC
     * @param time
     * @return
     */
    private long convertTimeZoneUtc(long time) {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Get time start day
     * @return
     */
    long getTimeStart() {
        return mTime;
    }
}
