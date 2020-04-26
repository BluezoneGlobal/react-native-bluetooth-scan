package com.scan;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AppScheduler {

    /**
     * Dat lich
     * @param context
     * @param intentAlarm
     * @param requestCode
     * @param intervalMillis
     */
    public static void schedule(Context context, Intent intentAlarm, int requestCode, long intervalMillis) {

        // Pending
        PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

        // Tao Alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Time
        long time = System.currentTimeMillis() + intervalMillis;

        if (Build.VERSION.SDK_INT >= 23) {
            // Schedule
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            // Schedule
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {
            // Schedule
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    /**
     * Dat lich vong lap, min cua moi vong lap la 60s
     * @param context
     * @param intentAlarm
     * @param requestCode
     * @param intervalMillis
     */
    public static void scheduleRepeating(Context context, Intent intentAlarm, int requestCode, long intervalMillis) {
        // Pending
        PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

        // Tao Alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        // Time
        long time = System.currentTimeMillis();

        // Schedule
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, intervalMillis, pendingIntent);
    }

    /**
     * Huy dat lich
     * @param context
     * @param intentAlarm
     * @param requestCode
     */
    public static void cancelScheduler(Context context, Intent intentAlarm, int requestCode) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

        // Tao va cancel lich
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
