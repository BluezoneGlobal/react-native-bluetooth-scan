package com.scan.notification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.facebook.react.bridge.ReadableMap;
import com.scan.AppConstants;
import com.scan.AppUtils;
import com.scan.R;
import com.scan.preference.AppPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class NotificationUtils {
    private static NotificationManager notificationManager;

    private static NotificationCompat.Builder notificationBuider;

    private static Notification notification;

    private static final String ENABLE_BLUETOOTH_ACTION = "enableBluetooth";

    private static final String VI = "vi";

    public static final int MILISECOND_OF_DAY = 86400000;

    public static final String[] regExs = {
            "<b>(.*)</b>",
            "<!b>(.*)</!b>",
            "<l>(.*)</l>",
            "<!l>(.*)</!l>",
            "<p>(.*)</p>",
            "<!p>(.*)</!p>",
            "<bl>(.*)</bl>",
            "<!bl>(.*)</!bl>",
            "<b!l>(.*)</b!l>",
            "<!(bl)>(.*)</!(bl)>",
            "<lp>(.*)</lp>",
            "<!lp>(.*)</!lp>",
            "<l!p>(.*)</l!p>",
            "<!(lp)>(.*)</!(lp)>",
            "<bp>(.*)</bp>",
            "<!bp>(.*)</!bp>",
            "<b!p>(.*)</b!p>",
            "<!(bp)>(.*)</!(bp)>",
            "<blp>(.*)</blp>",
            "<!(blp)>(.*)</!(blp)>",
    };

    /**
     * Tao notify chanel cho app
     * @param context
     */
    public static void startNotification(Service service, Context context) {
        // Check SDK
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Tao notifi
        createNotificationBluezone(context);

        // check and start
        if (notification != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            service.startForeground(AppConstants.NOTIFICATION_SERVICE_BLUE_ZONE_ID, notification);
        }
//        }
    }

    /**
     * Tạp notification cho app
     * @param context
     */
    public static void createNotificationBluezone(Context context) {
        try {
            // Tạo channel
            createNotificationChannel(context);
            createNotification(context);
//            notification = notificationBuider.build();
//            notificationManager.notify(AppConstants.NOTIFICATION_SERVICE_BLUE_ZONE_ID, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tao notify chanel cho app
     * @param context
     */
    public static void createNotificationChannel(Context context) {
        // create
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager = context.getSystemService(NotificationManager.class);
        } else {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        // Check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    AppConstants.NOTIFICATION_CHANNEL_ID,
                    AppConstants.NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);

            notificationManager.createNotificationChannel(serviceChannel);
        }
    }

    public static void createNotification(Context context) throws JSONException {
        Intent notificationIntent = new Intent(context, AppUtils.getMainActivityClass(context));
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                AppConstants.NOTIFICATION_CHANNEL_ID_CODE, notificationIntent, 0);
        notificationBuider = new NotificationCompat.Builder(context, AppConstants.NOTIFICATION_CHANNEL_ID)
                .setPriority(PRIORITY_MIN)
                .setContentIntent(pendingIntent);
        scanServiceChange(context, AppUtils.isBluetoothEnable(), AppUtils.isLocationEnable(context), AppUtils.isPermissonLocation(context));
    }

    public static long getTimeStartToday () {
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static boolean canAutoEnableBluetooth() {
        String[] MANUFACTURERS = {"oppo", "realme"};
        int i = 0;
        while(i < MANUFACTURERS.length) {
            if(Build.MANUFACTURER.contains(MANUFACTURERS[i]) || Build.BRAND.contains(MANUFACTURERS[i])) {
                return false;
            }
            i++;
        }
        return true;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String[] getInfoNotificationMap(Map<String, String> notifyInfoMap, String language) {
        String suffixes = language.equals(VI) ? "" : "En";
        String[] keys = {"title", "bigText", "message", "subText", "buttonText"};
        String[] result = new String[keys.length];
        for(int i = 0; i < keys.length; i++) {
            String key = keys[i] + suffixes;
            result[i] = notifyInfoMap.get(key);
        }
        return result;
    }

    public static boolean[] getConditionRegEx(boolean bluetooth, boolean location, boolean locationPermisson) {
        boolean inBluetooth = !bluetooth;
        boolean inLocation = !location;
        boolean inPermisson = !locationPermisson;

        return new boolean[] {
                inBluetooth,
                !inBluetooth,
                inLocation,
                !inLocation,
                inPermisson,
                !inPermisson,
                inBluetooth && inLocation,
                !inBluetooth && inLocation,
                inBluetooth && !inLocation,
                !inBluetooth && !inLocation,
                inLocation && inPermisson,
                !inLocation && inPermisson,
                inLocation && !inPermisson,
                !inLocation && !inPermisson,
                inBluetooth && inPermisson,
                !inBluetooth && inPermisson,
                inBluetooth && !inPermisson,
                !inBluetooth && !inPermisson,
                inBluetooth && inLocation && inPermisson,
                !inBluetooth && !inLocation && !inPermisson,
        };
    }

    public static void changeLanguageNotification(Context context, String language) {
        String content = !isNullOrEmpty(language) && language.compareTo("en") == 0  ? context.getString(R.string.notification_content_en) : context.getString(R.string.notification_content);
        String title = !isNullOrEmpty(language) && language.compareTo("en") == 0  ? context.getString(R.string.notification_title_en) : context.getString(R.string.notification_title);
        displayServiceNotification(context, R.mipmap.icon_bluezone_service, title, content, null);
    }

    static void createScheduleNotification(Context context, String type, Map<String, String> notifyInfoMap) throws JSONException {
        // Create notify repeat
        String strItemRepeat = notifyInfoMap.get("itemRepeat");
        if(strItemRepeat == null || strItemRepeat.length() == 0) {
            return;
        }
        JSONArray itemRepeatArray = null;
        try {
            itemRepeatArray = new JSONArray(strItemRepeat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        long now = System.currentTimeMillis();
        long timeStartToday = getTimeStartToday();
        for (int i = 0; i < itemRepeatArray.length(); i++) {
            JSONObject item = itemRepeatArray.getJSONObject(i);
            if(!item.has("id")) {
                break;
            }
            int notificationId = item.getInt("id");
            int dayStartTime = item.getInt("dayStartTime");
            int repeatTime = item.getInt("repeatTime");

            long iTime = timeStartToday + dayStartTime;
            if (iTime < now) {
                iTime += MILISECOND_OF_DAY;
            }

            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("id", notificationId);
            intent.putExtra("type", type);
            PendingIntent pending = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Schdedule notification bluetooth
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, iTime, repeatTime, pending);
        }
    }

    static void clearScheduleNotification(Context context, Map<String, String> notifyInfoMap) throws JSONException {
        String strItemRepeat = notifyInfoMap.get("itemRepeat");
        if(strItemRepeat == null || strItemRepeat.length() == 0) {
            return;
        }
        JSONArray itemRepeatArray = null;
        try {
            itemRepeatArray = new JSONArray(strItemRepeat);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(notificationManager == null) {
            return;
        }

        for (int i = 0; i < itemRepeatArray.length(); i++) {
            JSONObject item = itemRepeatArray.getJSONObject(i);
            if(!item.has("id")) {
                break;
            }
            int notificationId = item.getInt("id");
            Intent notificationIntent = new Intent(context, NotificationReceiver.class);
            notificationIntent.putExtra("id", notificationId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            notificationManager.cancel(notificationId);
        }
    }

    private static NotificationCompat.Builder createNotificationConfigBuider(Context context, Map<String, String> configNotification, PendingIntent pendingIntent) {
        String language = AppPreferenceManager.getInstance(context).getLanguage();

        String[] notificationInfo = NotificationUtils.getInfoNotificationMap(configNotification, language);
        String title = notificationInfo[0];
        String bigText = notificationInfo[1];
        String message = notificationInfo[2];
        String subText = notificationInfo[3];

        if(title == null || title.equals("") || bigText == null || bigText.equals("")) {
            return null ;
        }

        return new NotificationCompat.Builder(context, AppConstants.NOTIFICATION_CHANNEL_ID)
                .setPriority(PRIORITY_MIN)
                .setSmallIcon(R.mipmap.icon_bluezone_service)
                .setSubText(subText) // Sub text
                .setContentTitle(title) // title
                .setContentText(message) // bigText
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setContentIntent(pendingIntent);
    }

    private static void displayScanNotification(Context context) {
        String language = AppPreferenceManager.getInstance(context).getLanguage();
        Map<String, String> scanNotification = AppPreferenceManager.getInstance(context).getScanNotification();

        Intent notificationIntent = new Intent(context, AppUtils.getMainActivityClass(context));
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                AppConstants.NOTIFICATION_CHANNEL_ID_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder scanNotificationBuider = createNotificationConfigBuider(context, scanNotification, pendingIntent);
        if(scanNotificationBuider == null) {
            return;
        }

        String buttonText = language.equals(VI) ? scanNotification.get("buttonText") : scanNotification.get("buttonTextEn");
        if(buttonText != null && !buttonText.equals("")) {
            scanNotificationBuider.addAction(R.mipmap.icon_bluezone_service, buttonText, pendingIntent);
        }

        Notification notificationOpen = scanNotificationBuider.build();
        notificationOpen.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(AppConstants.NOTIFICATION_SCAN_ID, notificationOpen);
    }

    private static void displayEnableBluetoothNotification(Context context) {
        String language = AppPreferenceManager.getInstance(context).getLanguage();
        Map<String, String> scanNotification = AppPreferenceManager.getInstance(context).getEnableBluetoothNotification();

        Intent notificationIntent = new Intent(context, AppUtils.getMainActivityClass(context));
        notificationIntent.setAction(ENABLE_BLUETOOTH_ACTION);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, AppConstants.NOTIFICATION_CHANNEL_ID_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder enableBluetoothNotificationBuider = createNotificationConfigBuider(context, scanNotification, pendingIntent);

        if(enableBluetoothNotificationBuider == null) {
            return;
        }

        String buttonText = language.equals(VI) ? scanNotification.get("buttonText") : scanNotification.get("buttonTextEn");
        if(buttonText != null && !buttonText.equals("")) {
            Intent actionIntent = new Intent(context, NotificationReceiver.class);
            actionIntent.setAction(ENABLE_BLUETOOTH_ACTION);
            actionIntent.putExtra("notificationId", AppConstants.NOTIFICATION_ENABLE_BLUETOOTH_ID);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            enableBluetoothNotificationBuider.addAction(R.mipmap.icon_bluezone_service, buttonText, pendingIntent1);
        }

        Notification notificationOpen = enableBluetoothNotificationBuider.build();
        notificationOpen.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(AppConstants.NOTIFICATION_ENABLE_BLUETOOTH_ID, notificationOpen);
    }

    @SuppressLint("RestrictedApi")
    public static void displayServiceNotification(Context context, int icon, String title, String content, String buttonText) {
        if(notificationBuider != null && notificationManager != null) {
            notificationBuider
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setContentText(content)
                    .mActions.clear();
            if(buttonText != null && !buttonText.equals("")) {
                Intent notificationIntent = new Intent(context, AppUtils.getMainActivityClass(context));
                notificationIntent.setAction(ENABLE_BLUETOOTH_ACTION);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, AppConstants.NOTIFICATION_CHANNEL_ID_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuider.addAction(R.mipmap.icon_bluezone_service, buttonText, pendingIntent);
            }

            Notification notificationBuild = notificationBuider.build();
            if(notification == null) {
                notification = notificationBuild;
            }

            notificationManager.notify(AppConstants.NOTIFICATION_SERVICE_BLUE_ZONE_ID, notificationBuild);
        }
    }

    public static void resetServiceNotification(Context context) {
        String language = AppPreferenceManager.getInstance(context).getLanguage();
        changeLanguageNotification(context, language);
    }

    public static void scanServiceInactive(Context context) throws JSONException {
        createScheduleNotification(context, "schedule", AppPreferenceManager.getInstance(context).getScheduleScanNotification());

        // Thay đổi notification service sang thông báo lỗi (bản cũ)
//        Map<String, String> scanNotification = AppPreferenceManager.getInstance(context).getScanNotification();
//        String language = AppPreferenceManager.getInstance(context).getLanguage();
//        String title = language.equals(VI) ? scanNotification.get("title") : scanNotification.get("titleEn");
//        String bigText = language.equals(VI) ? scanNotification.get("bigText") : scanNotification.get("bigTextEn");
//        if(title != null && !title.equals("") && bigText != null && !bigText.equals("")) {
//            displayServiceNotification(context, R.mipmap.icon_error, title, bigText, null);
//        }
    }

    public static void scanServiceActive(Context context) throws JSONException {
        clearScheduleNotification(context, AppPreferenceManager.getInstance(context).getScheduleScanNotification());
        notificationManager.cancel(AppConstants.NOTIFICATION_SCAN_ID);
    }

    public static void scanServiceChange(Context context, boolean bluetooth, boolean location, boolean locationPermisson) {
        boolean scanServiceActive = bluetooth && location && locationPermisson;
        if(scanServiceActive) {
            resetServiceNotification(context);
            return;
        }
        String language = AppPreferenceManager.getInstance(context).getLanguage();
        Map<String, String> mapNotification;
        String title = null;
        String bigText = null;
        String buttonText = null;
        if(!locationPermisson) {
            // Ưu tiên thong bao cấp quyền vi tri
            mapNotification = AppPreferenceManager.getInstance(context).getLocationPermissonNotificationV2();
            String[] notificationInfo = NotificationUtils.getInfoNotificationMap(mapNotification, language);
            title = notificationInfo[0];
            bigText = notificationInfo[1];
            buttonText = notificationInfo[4];

        }

        if(title == null || bigText == null) {
            mapNotification = AppPreferenceManager.getInstance(context).getScanNotificationV2();
            String[] notificationInfo = NotificationUtils.getInfoNotificationMap(mapNotification, language);
            title = notificationInfo[0];
            bigText = notificationInfo[1];

            if(bluetooth || !location || !locationPermisson || !canAutoEnableBluetooth()) {
                buttonText = notificationInfo[4];
            }
        }

        if(title == null || bigText == null) { return; }

        boolean[] conditions = getConditionRegEx(bluetooth, location, locationPermisson);

        for(int i = 0; i < regExs.length; i++) {
            String replacement = conditions[i] ? "$1" : "";
            title = title.replaceAll(regExs[i], replacement);
            bigText = bigText.replaceAll(regExs[i], replacement);
        }

        if(!title.equals("") && !bigText.equals("")) {
            displayServiceNotification(context, R.mipmap.icon_error, title, bigText, buttonText);
        }
    }

    public static void bluetoothChange(Context context, boolean enable) {
        if(!canAutoEnableBluetooth()) {
            return;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(ENABLE_BLUETOOTH_ACTION);
        if(!enable) {
            displayEnableBluetoothNotification(context);
            long timeAutoEnableBLuetooth = AppPreferenceManager.getInstance(context).getTimeAutoEnableBluetooth();
            PendingIntent pending = PendingIntent.getBroadcast(context, AppConstants.ALARM_AUTOMATIC_ENABLE_BLUETOOTH, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeAutoEnableBLuetooth, pending);
        } else {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, AppConstants.ALARM_AUTOMATIC_ENABLE_BLUETOOTH, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
            notificationManager.cancel(AppConstants.NOTIFICATION_ENABLE_BLUETOOTH_ID);
        }
    }

    public static void scheduleScanNotificationChangeConfiguration(Context context, Map<String, String> oldConfig, ReadableMap newConfig) throws JSONException {
        String strItemRepeatOld = oldConfig.get("itemRepeat");
        String strItemRepeatNew = newConfig.hasKey("itemRepeat") ? Objects.requireNonNull(newConfig.getArray("itemRepeat")).toString() : "";

        JSONArray itemRepeatOld = null;
        JSONArray itemRepeatNew = null;
        try {
            itemRepeatOld = new JSONArray(strItemRepeatOld);
            itemRepeatNew = new JSONArray(strItemRepeatNew);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(itemRepeatNew == null) {
            return;
        }

        for (int i = 0; i < itemRepeatOld.length(); i++) {
            int oldNotificationId = itemRepeatOld.getJSONObject(i).getInt("id");
            boolean delete = true;
            for (int j = 0; j < itemRepeatNew.length(); j++) {
                if(itemRepeatNew.getJSONObject(i).getInt("id") == oldNotificationId) {
                    delete = false;
                    break;
                }
            }

            if(delete) {
                // Xóa notification
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent notificationIntent = new Intent(context, NotificationReceiver.class);
                notificationIntent.putExtra("id", oldNotificationId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, oldNotificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
                notificationManager.cancel(oldNotificationId);
            }
        }

        if(AppUtils.isBluetoothEnable() && AppUtils.isLocationEnable(context) && AppUtils.isPermissonLocation(context)) {
            return;
        }

        long now = System.currentTimeMillis();
        long timeStartToday = getTimeStartToday();
        for (int i = 0; i < itemRepeatNew.length(); i++) {
            int newNotificationId = itemRepeatNew.getJSONObject(i).getInt("id");
            boolean add = true;
            for (int j = 0; j < itemRepeatOld.length(); j++) {
                if(itemRepeatNew.getJSONObject(i).getInt("id") == newNotificationId) {
                    add = false;
                }
            }
            if(add) {
                JSONObject item = itemRepeatNew.getJSONObject(i);
                if(!item.has("id")) {
                    break;
                }
                int notificationId = item.getInt("id");
                int dayStartTime = item.getInt("dayStartTime");
                int repeatTime = item.getInt("repeatTime");
                long iTime = timeStartToday + dayStartTime;
                if (iTime < now) {
                    iTime += MILISECOND_OF_DAY;
                }
                Intent intent = new Intent(context, NotificationReceiver.class);
                intent.putExtra("id", notificationId);
                intent.putExtra("type", "schedule");
                PendingIntent pending = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                // Thêm schedule notification
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, iTime, repeatTime, pending);
            }
        }
    }

    public static void scanNotificationChangeConfiguration(Map<String, String> oldConfig, ReadableMap newConfig) {
        // ...
    }

    public static void enableBluetoothNotificationChangeConfiguration(Map<String, String> oldConfig, ReadableMap newConfig) {
        // ...
    }
}
