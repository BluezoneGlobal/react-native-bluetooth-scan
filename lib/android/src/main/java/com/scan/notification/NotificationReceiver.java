package com.scan.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.scan.AppConstants;
import com.scan.AppUtils;
import com.scan.R;
import com.scan.preference.AppPreferenceManager;

import org.json.JSONException;

import java.util.Map;
import java.util.Objects;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.equals(intent.getAction(), "enableBluetooth")) {
            boolean enableSuccess = AppUtils.enableBluetoothFinal();
            if(!enableSuccess) {
                Intent startIntent = context
                        .getPackageManager()
                        .getLaunchIntentForPackage(context.getPackageName());
                context.startActivity(startIntent);
            }
            return;
        }
        if(Objects.equals(intent.getAction(), "checkApp")) {
            boolean enableSuccess = AppUtils.enableBluetoothFinal();
            if(!enableSuccess) {
                Intent startIntent = context
                        .getPackageManager()
                        .getLaunchIntentForPackage(context.getPackageName());
                context.startActivity(startIntent);
            }
            return;
        }

        String language = AppPreferenceManager.getInstance(context).getLanguage();
        Map<String, String> notifyMap = AppPreferenceManager.getInstance(context).getScheduleScanNotification();
        int id = intent.getIntExtra("id", 0);

        if(notifyMap == null || id == 0) {
            return;
        }

        String[] notificationInfo = NotificationUtils.getInfoNotificationMap(notifyMap, language);

        String title = notificationInfo[0];
        String bigText = notificationInfo[1];
        String message = notificationInfo[2];
        String subText = notificationInfo[3];
        String buttonText = notificationInfo[4];

        if(title == null || bigText == null) { return; }

        if (bigText.length() == 0) {
            bigText = message;
        }

        boolean bluetooth = AppUtils.isBluetoothEnable();
        boolean location = AppUtils.isLocationEnable(context);
        boolean locationPermisson = AppUtils.isPermissonLocation(context);

        if( bluetooth && location && locationPermisson) {
            try {
                NotificationUtils.scanServiceActive(context);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        boolean[] conditions = NotificationUtils.getConditionRegEx(bluetooth, location, locationPermisson);

        for(int i = 0; i < NotificationUtils.regExs.length; i++) {
            String replacement = conditions[i] ? "$1" : "";
            title = title.replaceAll(NotificationUtils.regExs[i], replacement);
            assert bigText != null;
            bigText = bigText.replaceAll(NotificationUtils.regExs[i], replacement);
            assert message != null;
            message = message.replaceAll(NotificationUtils.regExs[i], replacement);
        }

        Intent notificationIntent = new Intent(context, AppUtils.getMainActivityClass(context));
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                AppConstants.NOTIFICATION_CHANNEL_ID_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder buider = new NotificationCompat.Builder(context, AppConstants.NOTIFICATION_CHANNEL_ID)
                .setPriority(PRIORITY_MIN)
                .setSmallIcon(R.mipmap.icon_bluezone_service)
                .setSubText(subText) // Sub text
                .setContentTitle(title) // title
                .setContentText(message) // bigText
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setContentIntent(pendingIntent);

        assert buttonText != null;
        if(!buttonText.equals("")) {
            buider.addAction(R.mipmap.icon_bluezone_service, buttonText, pendingIntent);
        }
        Notification notification = buider.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        // Show notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notification);
    }
}
