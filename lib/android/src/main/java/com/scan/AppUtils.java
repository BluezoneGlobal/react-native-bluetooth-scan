package com.scan;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.scan.apis.AsyncStorageApi;
import com.scan.model.ScanConfig;
import com.scan.notification.NotificationReceiver;
import com.scan.preference.AppPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class AppUtils {

    // UUID cua BLE
    public static final ParcelUuid BLE_UUID_IOS = new ParcelUuid(UUID.fromString(AppConstants.BLE_UUID_IOS));
    public static final ParcelUuid BLE_UUID_ANDROID = new ParcelUuid(UUID.fromString(AppConstants.BLE_UUID_ANDROID));
    public static final UUID BLE_UUID_CHARECTIC = UUID.fromString(AppConstants.BLE_UUID_CHARECTIC);
    public static final int MILISECOND_OF_DAY = 86400000;

    /**
     * check xem máy có support BLE hay không
     * @param context
     */
    public static boolean checkBleSupport(Context context) {
        boolean ret = false;
        try {
            // Check xem phan cung co ho tro hay khong
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                ret = true;
            } else {
                AppUtils.writeLog(context, "Not Support BLE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Enable bluetooth with battery level
     * @param context
     * @param batteryLevel
     */
    public static boolean enableBluetooth(Context context, int batteryLevel) {
        boolean ret = false;

        // check
        if (batteryLevel >= AppPreferenceManager.getInstance(context)
                .getConfigEnableBluetoothBatteryLevel(AppConstants.Config.DEFAULT_ENABLE_BLUETTOOTH_BATTERY_LEVEL)) {
            // Bat Bluettooth
            AppUtils.enableBluetooth();

            // ret
            ret = true;
        }

        return ret;
    }

    /**
     * Check enable
     * @return
     */
    public static boolean enableBluetooth() {
        boolean ret = false;

        // check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Lay doi tuong
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // Check
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
//                bluetoothAdapter.enable();

                // return
                ret = true;
            }
        }

        return ret;
    }

    public static boolean enableBluetoothFinal() {
        boolean ret = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null && bluetoothAdapter.enable()) {
                ret = true;
            }
        }

        return ret;
    }

    public static boolean isBluetoothEnable() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            return true;
        }
        return false;
    }

    public static boolean isLocationEnable(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static boolean isPermissonLocation(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check permission granted
     * @param context
     * @param permissions
     * @return
     */
    public static boolean checkSelfPermission(Context context, String... permissions) {
        boolean ret = true;

        // Check
        if (context != null && permissions != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Loop
            for (String permission : permissions) {
                // Check quyen
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    ret = false;
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * Check permission external storage granted
     * @param context
     * @return
     */
    public static boolean hasPermissionsExternalStorage(Context context) {

//        String[] storagePermissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};

        // Check
//        return checkSelfPermission(context, storagePermissions);
        return false;
    }

    /**
     * Remove file
     * @param filePath
     */
    public static boolean removeFile(String filePath) {
        boolean ret = true;
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file != null && file.exists()) {
                ret = file.delete();
            }
        }

        return ret;
    }

    private static NotificationManager notificationManager;
    private static NotificationCompat.Builder notificationBuider;
    private static Notification notification;
    /**
     * Tao notify chanel cho app
     * @param context
     */
    public static void startNotification(Service service, Context context) {
        // Check SDK
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Tao notifi
            AppUtils.createNotificationBluezone(context);

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
            notification = notificationBuider.build();
            notificationManager.notify(AppConstants.NOTIFICATION_SERVICE_BLUE_ZONE_ID, notification);
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
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(serviceChannel);
        }
    }

    public static void createNotification(Context context) {
        Intent notificationIntent = new Intent(context, getMainActivityClass(context));
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                AppConstants.NOTIFICATION_CHANNEL_ID_CODE, notificationIntent, 0);

        String language = AppPreferenceManager.getInstance(context).getLanguage();

        String content = !isNullOrEmpty(language) && language.compareTo("en") == 0  ? context.getString(R.string.notification_content_en) : context.getString(R.string.notification_content);
        String title = !isNullOrEmpty(language) && language.compareTo("en") == 0  ? context.getString(R.string.notification_title_en) : context.getString(R.string.notification_title);
        // Tao thong bao
        notificationBuider = new NotificationCompat.Builder(context, AppConstants.NOTIFICATION_CHANNEL_ID)
                .setPriority(PRIORITY_MIN)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setContentText(content)
                .setSmallIcon(R.mipmap.icon_bluezone_service)
                .setContentIntent(pendingIntent);
    }

    /*
     * Tao class
     */
    public static Class getMainActivityClass(Context context) {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert lai ten cua thiet bi
     * @return
     */
    public static String convertUserId(String name) {
        String ret = "";
        try {
            if (!TextUtils.isEmpty(name) && name.length() == 10) {
                String sub = name.substring(1, 9);
                // check
                int start = Integer.parseInt(name.substring(0, 1));
                int end = Integer.parseInt(name.substring(9, 10));

                // min max
                int tmp = end;
                if (start > end) {
                    end = start;
                    start = tmp;
                }

                // Check
                String start1 = sub.substring(start, start + 1);
                String start2 = sub.substring(end + 1, end + 2);

                sub = sub.substring(0, start) + sub.substring(start + 1, end + 1) + sub.substring(end + 2);

                // Check
                if (sub.substring(start - 1, start).equals(start2) && sub.substring(end - 1, end).equals(start1)) {
                    ret = sub;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Tra ve thoi gian hien tai
     * @return
     */
    public static String getDateCurrent() {
        try {
            // time
            DateFormat df = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
            return df.format(System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Get app root path
     */
    public static String getAppPath(Context context) {
        String folderPath = "";

        try {
            folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + AppConstants.PATH_APP;
            // Check
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasPermissionsExternalStorage(context)) {
                // Check folder
                File blueDir = new File(folderPath);

                // Check ton tai
                if (!blueDir.exists()) {
                    // Tao thu muc
                    if (!blueDir.mkdirs()) {

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return folderPath;
    }

    /**
     * Write log chung
     *
     * @param log the log
     */
    public static synchronized void writeLog(Context context, String log) {
        // Get Path
        String folderPath = getAppPath(context);

        // Check
        if (!TextUtils.isEmpty(folderPath)) {
            RandomAccessFile fi = null;
            try {
                // Log
                fi = new RandomAccessFile(folderPath + AppConstants.LOG_FILE_NAME, "rw");
                File fileLog = new File(folderPath, AppConstants.LOG_FILE_NAME);
                fi.seek(fileLog.length());
                String tmp = AppUtils.getDateCurrent() + ":" + log + "\n";
                fi.write(tmp.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fi != null) {
                    try {
                        fi.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Write log chung
     *
     * @param log the log
     */
    public static synchronized void writeLogBattery(Context context, String log) {
        // Log
        Log.d("Bluezone", log);

        // Get Path
        String folderPath = getAppPath(context);

        // Check
        if (!TextUtils.isEmpty(folderPath)) {
            RandomAccessFile fi = null;
            try {
                // Log
                fi = new RandomAccessFile(folderPath + AppConstants.LOG_FILE_NAME_BATTERY, "rw");
                File fileLog = new File(folderPath, AppConstants.LOG_FILE_NAME_BATTERY);
                fi.seek(fileLog.length());
                String tmp = AppUtils.getDateCurrent() + ":" + log + "\n";
                fi.write(tmp.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fi != null) {
                    try {
                        fi.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Get config scan
     * @return ScanConfig
     */
    public static ScanConfig getConfigScan(Context context, String keyName) {
        // ret
        ScanConfig ret = null;

        // Lay thong tin, đọc config từ file
        try {
            // Get Path
            String folderPath = getAppPath(context);

            // Check
            if (!TextUtils.isEmpty(folderPath)) {

                // File name
                File file = new File(folderPath, AppConstants.Config.CONFIG_FILE_NAME);

                // check
                if (file != null && file.exists()) {
                    // Doc
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    String line = "";

                    // Doc tung dong
                    while ((line = bufferedReader.readLine()) != null) {
                        // slip
                        String[] configsData = line.split(":");

                        // check
                        if (configsData != null && configsData.length == AppConstants.Config.CONFIG_FILE_SCAN_COUNT &&
                                configsData[AppConstants.Config.CONFIG_FILE_INDEX_KEY].equals(keyName)) {
                            // Doc
                            ret = new ScanConfig(Long.parseLong(configsData[AppConstants.Config.CONFIG_FILE_INDEX_DURATION]),
                                    Long.parseLong(configsData[AppConstants.Config.CONFIG_FILE_INDEX_INTERVAL]));
                            break;
                        }
                    }

                    // check
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lay tu config tu file khong co
        if (ret == null) {
            // Lay thong tin tu server
            ret = getConfigScanFromServer(context, keyName);

            // Neu khong co tren server tro ve thi lay ma dinh
            if (ret == null) {
                // Lay cac config default
                ret = getConfigScanDefault(keyName);
            }
        }

        return ret;
    }

    /**
     * Lấy config default
     * @param keyName
     * @return
     */
    public static ScanConfig getConfigScanFromServer(Context context, String keyName) {
        // ret
        ScanConfig ret = null;

        // Pre
        AppPreferenceManager preferenceManager = AppPreferenceManager.getInstance(context);

        // Lay thong tin
        long timeDuration = 0;
        long timeInterval = 0;

        // check
        switch (keyName) {
            case AppPreferenceManager.PreferenceConstants.CONFIG_BROADCAST_BLE:
                // Lay thong tin
                timeDuration = preferenceManager.getConfigBroadcasBleDuration(0);
                timeInterval = preferenceManager.getConfigBroadcasBleInterval(0);
                break;
            case AppPreferenceManager.PreferenceConstants.CONFIG_SCAN_BLE:
                // Lay thong tin
                timeDuration = preferenceManager.getConfigScanBleDuration(0);
                timeInterval = preferenceManager.getConfigScanBleInterval(0);
                break;
            case AppPreferenceManager.PreferenceConstants.CONFIG_SCAN_DEVICES:
                // Lay thong tin
                timeDuration = preferenceManager.getConfigScanDevicesDuration(0);
                timeInterval = preferenceManager.getConfigScanDevicesInterval(0);
                break;
        }

        // Check
        if (timeDuration > 0 && timeInterval > 0) {
            ret = new ScanConfig(timeDuration, timeInterval);
        }

        // return
        return ret;
    }

    /**
     * Lấy config default
     * @param keyName
     * @return
     */
    public static ScanConfig getConfigScanDefault(String keyName) {
        // ret
        ScanConfig ret = null;

        // check
        switch (keyName) {
            case AppPreferenceManager.PreferenceConstants.CONFIG_BROADCAST_BLE:
                ret = new ScanConfig(AppConstants.Config.DEFAULT_BROADCAST_BLE_DURATION, AppConstants.Config.DEFAULT_BROADCAST_BLE_INTERVAL);
                break;
            case AppPreferenceManager.PreferenceConstants.CONFIG_SCAN_BLE:
                ret = new ScanConfig(AppConstants.Config.DEFAULT_SCAN_BLE_DURATION, AppConstants.Config.DEFAULT_SCAN_BLE_INTERVAL);
                break;
            case AppPreferenceManager.PreferenceConstants.CONFIG_SCAN_DEVICES:
                ret = new ScanConfig(AppConstants.Config.DEFAULT_SCAN_DEVICES_DURATION, AppConstants.Config.DEFAULT_SCAN_DEVICES_INTERVAL);
                break;
        }

        // return
        return ret;
    }

    /**
     * Get thong tin log file
     * @return
     */
    public static boolean getConfigLogFile(Context context) {
        // Check
        if (AppConstants.Config.IS_CONFIG_LOG_FILE ||
                getConfig(context, AppPreferenceManager.PreferenceConstants.CONFIG_LOG_FILE) ||
                AppPreferenceManager.getInstance(context).getConfigLogFile()) {
            return true;
        }

        return false;
    }

    /**
     * Get thong tin log Battery
     * @return
     */
    public static boolean getConfigLogBattery(Context context) {
        // Check
        if (getConfig(context, AppPreferenceManager.PreferenceConstants.CONFIG_LOG_BATTERY) ||
                AppPreferenceManager.getInstance(context).getConfigLogBattery()) {
            return true;
        }

        return false;
    }

    /**
     * Get config other
     * Fomat "name:value_config"
     * @return ScanConfig
     */
    public static boolean getConfig(Context context, String keyName) {
        // ret
        boolean ret = false;

        // Lay thong tin, đọc config từ file
        try {
            // Get Path
            String folderPath = getAppPath(context);

            // Check
            if (!TextUtils.isEmpty(folderPath)) {

                // File name
                File file = new File(folderPath, AppConstants.Config.CONFIG_FILE_NAME);

                // Check
                if (file != null && file.exists()) {
                    // Doc
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    String line = "";

                    // Doc tung dong
                    while ((line = bufferedReader.readLine()) != null) {
                        // slip
                        String[] configsData = line.split(":");

                        // check
                        if (configsData != null && configsData.length == AppConstants.Config.CONFIG_FILE_DEFAULT_COUNT &&
                                configsData[AppConstants.Config.CONFIG_FILE_INDEX_KEY].equals(keyName) &&
                                configsData[AppConstants.Config.CONFIG_FILE_INDEX_VALUE].equals(AppConstants.Config.CONFIG_FILE_VALUE_OK)) {
                            // Doc
                            ret = true;
                            break;
                        }
                    }

                    // check
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // check
        if (!ret) {
            // Lay cac config default
            ret = getConfigDefault(keyName);
        }

        return ret;
    }

    /**
     * Lấy config default
     * @param keyName
     * @return
     */
    public static boolean getConfigDefault(String keyName) {
        // ret
        boolean ret = false;

        // check
        switch (keyName) {
            case AppPreferenceManager.PreferenceConstants.CONFIG_LOG_FILE:
                ret = AppConstants.Config.IS_CONFIG_LOG_FILE;
                break;
            case AppPreferenceManager.PreferenceConstants.CONFIG_LOG_BATTERY:
                ret = AppConstants.Config.IS_CONFIG_LOG_BATTERY;
                break;
        }

        // return
        return ret;
    }

    /**
     * Copy file
     * @param source
     * @param dest
     * @return
     */
    public static boolean copyFile(File source, File dest) {
        boolean ret = true;

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(source));
            bos = new BufferedOutputStream(new FileOutputStream(dest, false));

            // Doc
            byte[] buf = new byte[1024];

            // Do va ghi
            while (bis.read(buf) != -1) {
                bos.write(buf);
            }
        } catch (IOException e) {
            e.printStackTrace();
            ret = false;
        } finally {
            try {
                // Close
                if (bis != null) {
                    bis.close();
                }

                // Close
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                ret = false;
            }
        }

        return ret;
    }

    /**
     * Get battery level
     * @param context
     * @return
     */
    public static int getBatteryLevel(Context context) {
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);

        // Check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }

        return 0;
    }

    private static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;
        return true;
    }

    /**
     * Convert bytes to hex
     * @param bytes
     * @return
     */
    public static String convertBytesToHex(byte[] bytes) {
        StringBuilder ret = new StringBuilder();
        for (byte temp : bytes) {
            ret.append(String.format("%02X", temp));
        }

        return ret.toString();
    }

    /**
     * Convert hex to bytes
     * @param hex
     * @return
     */
    public static byte[] convertHexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }

        return data;
    }

//    /**
//     * Convert Time Zone UTC
//     * @param time
//     * @return
//     */
//    public static long convertTimeZoneUtc(long time) {
//        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
//        calendar.setTimeInMillis(time);
//        return calendar.getTimeInMillis();
//    }

    public static void changeLanguageNotification(Context context, String language) {
        String content = !isNullOrEmpty(language) && language.compareTo("en") == 0  ? context.getString(R.string.notification_content_en) : context.getString(R.string.notification_content);
        String title = !isNullOrEmpty(language) && language.compareTo("en") == 0  ? context.getString(R.string.notification_title_en) : context.getString(R.string.notification_title);
        changeServiceNotification(R.mipmap.icon_bluezone_service, title, content);
    }

    static long getTimeStartToday () {
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
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
        String subText;
        String message;
        String title;
        String bigText;
        if(language.equals("vi")) {
            subText = configNotification.get("subText");
            message = configNotification.get("message");
            bigText = configNotification.get("bigText");
            title = configNotification.get("title");
        } else {
            subText = configNotification.get("subTextEn");
            message = configNotification.get("messageEn");
            bigText = configNotification.get("bigTextEn");
            title = configNotification.get("titleEn");
        }

        if(title == null || title.equals("")) {
            return null ;
        }

        if(bigText == null || bigText.equals("")) {
            return null;
        }

        NotificationCompat.Builder buider = new NotificationCompat.Builder(context, AppConstants.NOTIFICATION_CHANNEL_ID)
                .setPriority(PRIORITY_MIN)
                .setSmallIcon(R.mipmap.icon_bluezone_service)
                .setSubText(subText) // Sub text
                .setContentTitle(title) // title
                .setContentText(message) // bigText
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setContentIntent(pendingIntent);
        return buider;
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

        String buttonText = language.equals("vi") ? scanNotification.get("buttonText") : scanNotification.get("buttonTextEn");
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
        notificationIntent.setAction("enableBluetooth");
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                AppConstants.NOTIFICATION_CHANNEL_ID_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder enableBluetoothNotificationBuider = createNotificationConfigBuider(context, scanNotification, pendingIntent);

        if(enableBluetoothNotificationBuider == null) {
            return;
        }

        String buttonText = language.equals("vi") ? scanNotification.get("buttonText") : scanNotification.get("buttonTextEn");
        if(buttonText != null && !buttonText.equals("")) {
            Intent actionIntent = new Intent(context, NotificationReceiver.class);
            actionIntent.setAction("enableBluetooth");
            actionIntent.putExtra("notificationId", AppConstants.NOTIFICATION_ENABLE_BLUETOOTH_ID);
            PendingIntent pendingIntent1 =
                    PendingIntent.getBroadcast(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            enableBluetoothNotificationBuider.addAction(R.mipmap.icon_bluezone_service, buttonText, pendingIntent1);
        }

        Notification notificationOpen = enableBluetoothNotificationBuider.build();
        notificationOpen.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(AppConstants.NOTIFICATION_ENABLE_BLUETOOTH_ID, notificationOpen);
    }

    public static void changeServiceNotification(int icon, String title, String content) {
        if(notificationBuider != null && notificationManager != null) {
            notificationBuider.setSmallIcon(icon);
            notificationBuider.setContentTitle(title);
            notificationBuider.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
            notificationBuider.setContentText(content);
            notificationManager.notify(AppConstants.NOTIFICATION_SERVICE_BLUE_ZONE_ID, notificationBuider.build());
        }
    }

    public static void scanStatusInactive(Context context) throws JSONException {
        createScheduleNotification(context, "schedule", AppPreferenceManager.getInstance(context).getScheduleScanNotification());

        // Thay đổi notification service sang thông báo lỗi
        Map<String, String> scanNotification = AppPreferenceManager.getInstance(context).getScanNotification();
        String language = AppPreferenceManager.getInstance(context).getLanguage();
        String title = language.equals("vi") ? scanNotification.get("title") : scanNotification.get("titleEn");
        String bigText = language.equals("vi") ? scanNotification.get("bigText") : scanNotification.get("bigTextEn");
        if(title != null && !title.equals("") && bigText != null && !bigText.equals("")) {
            changeServiceNotification(R.mipmap.icon_error, title, bigText);
        }
    }

    public static void scanStatusActive(Context context) throws JSONException {
        clearScheduleNotification(context, AppPreferenceManager.getInstance(context).getScheduleScanNotification());
        notificationManager.cancel(AppConstants.NOTIFICATION_SCAN_ID);

        // Reset notification về trạng thái ban đầu
        String language = AppPreferenceManager.getInstance(context).getLanguage();
        changeLanguageNotification(context, language);
    }

    public static void bluetoothChange(Context context, boolean enable) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction("enableBluetooth");
        if(!enable) {
            displayEnableBluetoothNotification(context);
            // Automatic enable bluetooth after a period of time

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
        String strItemRepeatNew = newConfig.hasKey("itemRepeat") ? newConfig.getArray("itemRepeat").toString() : "";

        JSONArray itemRepeatOld = null;
        try {
            itemRepeatOld = new JSONArray(strItemRepeatOld);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray itemRepeatNew = null;
        try {
            itemRepeatNew = new JSONArray(strItemRepeatNew);
        } catch (JSONException e) {
            e.printStackTrace();
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

        if(AppUtils.isBluetoothEnable() && AppUtils.isLocationEnable(context) && AppUtils.isPermissonLocation(context)
        ) {
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
