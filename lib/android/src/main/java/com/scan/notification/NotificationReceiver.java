package com.scan.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.scan.AppConstants;
import com.scan.R;
import com.scan.preference.AppPreferenceManager;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Build notification based on Intent
        String language = AppPreferenceManager.getInstance(context).getLanguage();

        String subText = "";
        String message = "";
        String title = "";
        String bigText = "";
        if(language.equals("vi")) {
            subText = intent.getStringExtra("subText");
            message = intent.getStringExtra("message");
            bigText = intent.getStringExtra("bigText");
            title = intent.getStringExtra("title");
        } else {
            subText = intent.getStringExtra("subText_en");
            message = intent.getStringExtra("message");
            bigText = intent.getStringExtra("bigText_en");
            title = intent.getStringExtra("title_en");
        }

        if (bigText == null) {
            bigText = message;
        }

        Notification notification = new NotificationCompat.Builder(context, AppConstants.NOTIFICATION_CHANNEL_ID)
                .setPriority(PRIORITY_MIN)
                .setSmallIcon(R.mipmap.icon_bluezone_service)
                .setSubText(subText) // Sub text
                .setContentTitle(title) // title
                .setContentText(message) // bigText
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .build();
        // Show notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(intent.getIntExtra("id", 1), notification);
    }
}
