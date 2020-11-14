package com.scan;

import android.Manifest;
import android.annotation.SuppressLint;
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

    public static boolean isBluetoothEnable() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            return true;
        }
        return false;
    }

    public static boolean isLocationEnable(Context context) {
        int locationMode;
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
}
