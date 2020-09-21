package com.scan.preference;

import android.content.Context;

import com.scan.AppConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * class dung de luu tru cac du lieu can thiet nhat cua ung dung duoc goi chung boi ta cac module.
 */
public class AppPreferenceManager extends AbstractPreferenceManager {
    // Instance
    private static AppPreferenceManager sInstance;

    /**
     * Contractor
     *
     * @param context
     */
    private AppPreferenceManager(Context context) {
        super(context);
    }

    /**
     * Use to initialization object AppPreferenceManager
     *
     * @param context app Context
     */
    private static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppPreferenceManager(context);
        }
    }

    /**
     * Use to initialization and get object AppPreferenceManager
     *
     * @param context app context
     */
    public static synchronized AppPreferenceManager getInstance(Context context) {
        if (sInstance == null) {
            initializeInstance(context);
        }

        return sInstance;
    }

    /**
     * Set blid
     * @param blid
     */
    public void setBlid(String blid) {
        putString(PreferenceConstants.BLID, blid);
    }

    /**
     * Get blid
     */
    public String getBlid() {
        return getString(PreferenceConstants.BLID, "Bluezoner");
    }

    /**
     * Set config scan ble
     * @param time
     */
    public void setConfigScanBleDuration(long time) {
        putLong(PreferenceConstants.CONFIG_SEVER_SCAN_BLE_DURATION, time);
    }

    /**
     * Get config scan ble
     */
    public long getConfigScanBleDuration(long timeDefault) {
        return getLong(PreferenceConstants.CONFIG_SEVER_SCAN_BLE_DURATION, timeDefault);
    }

    /**
     * Set config scan ble
     * @param time
     */
    public void setConfigScanBleInterval(long time) {
        putLong(PreferenceConstants.CONFIG_SERVER_SCAN_BLE_INTERVAL, time);
    }

    /**
     * Get config scan ble
     */
    public long getConfigScanBleInterval(long timeDefault) {
        return getLong(PreferenceConstants.CONFIG_SERVER_SCAN_BLE_INTERVAL, timeDefault);
    }

    /**
     * Set config broadcast ble
     * @param time
     */
    public void setConfigBroadcastBleDuration(long time) {
        putLong(PreferenceConstants.CONFIG_SERVER_BROADCAST_BLE_DURATION, time);
    }

    /**
     * Get config broadcast ble
     */
    public long getConfigBroadcasBleDuration(long timeDefault) {
        return getLong(PreferenceConstants.CONFIG_SERVER_BROADCAST_BLE_DURATION, timeDefault);
    }

    /**
     * Set config broadcast ble
     * @param time
     */
    public void setConfigBroadcastBleInterval(long time) {
        putLong(PreferenceConstants.CONFIG_SERVER_BROADCAST_BLE_INTERVAL, time);
    }

    /**
     * Get config broadcast ble
     */
    public long getConfigBroadcasBleInterval(long timeDefault) {
        return getLong(PreferenceConstants.CONFIG_SERVER_BROADCAST_BLE_INTERVAL, timeDefault);
    }

    /**
     * Set config scan devices
     * @param time
     */
    public void setConfigScanDevicesDuration(long time) {
        putLong(PreferenceConstants.CONFIG_SERVER_SCAN_DEVICES_DURATION, time);
    }

    /**
     * Get config scan devices
     */
    public long getConfigScanDevicesDuration(long timeDefault) {
        return getLong(PreferenceConstants.CONFIG_SERVER_SCAN_DEVICES_DURATION, timeDefault);
    }

    /**
     * Set config scan devices
     * @param time
     */
    public void setConfigScanDevicesInterval(long time) {
        putLong(PreferenceConstants.CONFIG_SERVER_SCAN_DEVICES_INTERVAL, time);
    }

    /**
     * Get config scan devices
     */
    public long getConfigScanDevicesInterval(long timeDefault) {
        return getLong(PreferenceConstants.CONFIG_SERVER_SCAN_DEVICES_INTERVAL, timeDefault);
    }

    /**
     * Set config interval insert db
     * @param time
     */
    public void setConfigInsertDb(long time) {
        putLong(PreferenceConstants.CONFIG_SERVER_INSERT_DB, time);
    }

    /**
     * Get config interval insert db
     */
    public long getConfigInsertDb(long timeDefault) {
        return getLong(PreferenceConstants.CONFIG_SERVER_INSERT_DB, timeDefault);
    }

    /**
     * Set config interval insert db
     * @param time
     */
    public void setConfigBackupDb(long time) {
        putLong(PreferenceConstants.CONFIG_SERVER_BACKUP_DB, time);
    }

    /**
     * Get config interval insert db
     */
    public long getConfigBackupDb(long timeDefault) {
        return getLong(PreferenceConstants.CONFIG_SERVER_BACKUP_DB, timeDefault);
    }

    /**
     * Set config log file from server
     * @param isLog
     */
    public void setConfigLogFile(boolean isLog) {
        putBoolean(PreferenceConstants.CONFIG_LOG_FILE, isLog);
    }

    /**
     * Get config log file from server
     */
    public boolean getConfigLogFile() {
        return getBoolean(PreferenceConstants.CONFIG_LOG_FILE, false);
    }

    /**
     * Set config log battery from server
     * @param isLog
     */
    public void setConfigLogBattery(boolean isLog) {
        putBoolean(PreferenceConstants.CONFIG_LOG_BATTERY, isLog);
    }

    /**
     * Get config log battery from server
     */
    public boolean getConfigLogBattery() {
        return getBoolean(PreferenceConstants.CONFIG_LOG_BATTERY, false);
    }

    /**
     * Set time last backup
     * @param time
     */
    public void setLastBackup(long time) {
        putLong(PreferenceConstants.TIME_LAST_BACKUP, time);
    }

    /**
     * Get time last backup
     */
    public long getLastBackup() {
        return getLong(PreferenceConstants.TIME_LAST_BACKUP, 0);
    }

    /**
     * Set config enable bluetooth interval
     * @param time
     */
    public void setConfigEnableBluetoothInterval(long time) {
        putLong(PreferenceConstants.CONFIG_SERVER_ENABLE_BLUETOOTH_INTERVAL, time);
    }

    /**
     * Get config enable bluetooth interval
     */
    public long getConfigEnableBluetoothInterval(long timeDefault) {
        return getLong(PreferenceConstants.CONFIG_SERVER_ENABLE_BLUETOOTH_INTERVAL, timeDefault);
    }

    public void setConfigCheckIntervalRequestPermission(long time) {
        putLong(PreferenceConstants.CONFIG_SERVER_CHECK_NOTIFY_REQUEST_PERMISSON, time);
    }

    public long getConfigCheckIntervalRequestPermission(long timeDefault) {
        return getLong(PreferenceConstants.CONFIG_SERVER_CHECK_NOTIFY_REQUEST_PERMISSON, timeDefault);
    }

    /**
     * Set config enable bluetooth battery level
     * @param time
     */
    public void setConfigEnableBluetoothBatteryLevel(int time) {
        putLong(PreferenceConstants.CONFIG_SERVER_ENABLE_BLUETOOTH_BATTERY_LEVEL, time);
    }

    /**
     * Get config enable bluetooth battery level
     */
    public long getConfigEnableBluetoothBatteryLevel(int levelDefault) {
        return getInt(PreferenceConstants.CONFIG_SERVER_ENABLE_BLUETOOTH_BATTERY_LEVEL, levelDefault);
    }

    /**
     * Set config scan devices
     * @param isScan
     */
    public void setConfigScanDevices(boolean isScan) {
        putBoolean(PreferenceConstants.CONFIG_SERVER_SCAN_DEVICES, isScan);
    }

    /**
     * Get config scan devices
     */
    public boolean getConfigScanDevices() {
        return getBoolean(PreferenceConstants.CONFIG_SERVER_SCAN_DEVICES, AppConstants.Config.IS_CONFIG_SCAN_DEVICES);
    }

    /**
     * Set language
     * @param language
     */
    public void setLanguage(String language) {
        putString(PreferenceConstants.LANGUAGE, language);
    }

    /**
     * Set language
     */
    public String getLanguage() {
        return getString(PreferenceConstants.LANGUAGE, "vi");
    }
    
    public void setScanNotification(
            String bigTextVi,
            String bigTextEn,
            String subTextVi,
            String subTextEn,
            String titleVi,
            String titleEn,
            String messageVi,
            String messageEn,
            String buttonTextVi,
            String buttonTextEn
    ) {
        putString(PreferenceConstants.SCAN_NOTIFICATION_BIG_TEXT_VI, bigTextVi);
        putString(PreferenceConstants.SCAN_NOTIFICATION_BIG_TEXT_EN, bigTextEn);
        putString(PreferenceConstants.SCAN_NOTIFICATION_SUB_TEXT_VI, subTextVi);
        putString(PreferenceConstants.SCAN_NOTIFICATION_SUB_TEXT_EN, subTextEn);
        putString(PreferenceConstants.SCAN_NOTIFICATION_TITLE_VI, titleVi);
        putString(PreferenceConstants.SCAN_NOTIFICATION_TITLE_EN, titleEn);
        putString(PreferenceConstants.SCAN_NOTIFICATION_MESSAGE_VI, messageVi);
        putString(PreferenceConstants.SCAN_NOTIFICATION_MESSAGE_EN, messageEn);
        putString(PreferenceConstants.SCAN_NOTIFICATION_BUTTON_VI, buttonTextVi);
        putString(PreferenceConstants.SCAN_NOTIFICATION_BUTTON_EN, buttonTextEn);
    }

    public Map<String, String> getScanNotification() {
        Map<String, String> result = new HashMap<>();
        result.put("bigText", getString(PreferenceConstants.SCAN_NOTIFICATION_BIG_TEXT_VI, null));
        result.put("bigTextEn", getString(PreferenceConstants.SCAN_NOTIFICATION_BIG_TEXT_VI, null));
        result.put("subText", getString(PreferenceConstants.SCAN_NOTIFICATION_SUB_TEXT_VI, null));
        result.put("subTextEn", getString(PreferenceConstants.SCAN_NOTIFICATION_SUB_TEXT_EN, null));
        result.put("title", getString(PreferenceConstants.SCAN_NOTIFICATION_TITLE_VI, null));
        result.put("titleEn", getString(PreferenceConstants.SCAN_NOTIFICATION_TITLE_EN, null));
        result.put("message", getString(PreferenceConstants.SCAN_NOTIFICATION_MESSAGE_VI, null));
        result.put("messageEn", getString(PreferenceConstants.SCAN_NOTIFICATION_MESSAGE_EN, null));
        result.put("buttonText", getString(PreferenceConstants.SCAN_NOTIFICATION_BUTTON_VI, null));
        result.put("buttonTextEn", getString(PreferenceConstants.SCAN_NOTIFICATION_BUTTON_EN, null));
        return result;
    }

    public void setScheduleScanNotification(
            String bigTextVi,
            String bigTextEn,
            String subTextVi,
            String subTextEn,
            String titleVi,
            String titleEn,
            String messageVi,
            String messageEn,
            String buttonTextVi,
            String buttonTextEn,
            String itemRepeat
    ) {
        putString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_BIG_TEXT_VI, bigTextVi);
        putString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_BIG_TEXT_EN, bigTextEn);
        putString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_SUB_TEXT_VI, subTextVi);
        putString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_SUB_TEXT_EN, subTextEn);
        putString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_TITLE_VI, titleVi);
        putString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_TITLE_EN, titleEn);
        putString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_MESSAGE_VI, messageVi);
        putString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_MESSAGE_EN, messageEn);
        putString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_BUTTON_VI, buttonTextVi);
        putString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_BUTTON_EN, buttonTextEn);
        putString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_REPEAT, itemRepeat);
    }

    public Map<String, String> getScheduleScanNotification() {
        Map<String, String> result = new HashMap<>();
        result.put("itemRepeat", getString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_REPEAT, null));
        result.put("bigText", getString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_BIG_TEXT_VI, null));
        result.put("bigTextEn", getString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_BIG_TEXT_VI, null));
        result.put("subText", getString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_SUB_TEXT_VI, null));
        result.put("subTextEn", getString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_SUB_TEXT_EN, null));
        result.put("title", getString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_TITLE_VI, null));
        result.put("titleEn", getString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_TITLE_EN, null));
        result.put("message", getString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_MESSAGE_VI, null));
        result.put("messageEn", getString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_MESSAGE_EN, null));
        result.put("buttonText", getString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_BUTTON_VI, null));
        result.put("buttonTextEn", getString(PreferenceConstants.SCHEDULE_SCAN_NOTIFICATION_BUTTON_EN, null));
        return result;
    }

    public void setEnableBluetoothNotification(
            String bigTextVi,
            String bigTextEn,
            String subTextVi,
            String subTextEn,
            String titleVi,
            String titleEn,
            String messageVi,
            String messageEn,
            String buttonTextVi,
            String buttonTextEn
    ) {
        putString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_BIG_TEXT_VI, bigTextVi);
        putString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_BIG_TEXT_EN, bigTextEn);
        putString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_SUB_TEXT_VI, subTextVi);
        putString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_SUB_TEXT_EN, subTextEn);
        putString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_TITLE_VI, titleVi);
        putString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_TITLE_EN, titleEn);
        putString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_MESSAGE_VI, messageVi);
        putString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_MESSAGE_EN, messageEn);
        putString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_BUTTON_VI, buttonTextVi);
        putString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_BUTTON_EN, buttonTextEn);
    }

    public Map<String, String> getEnableBluetoothNotification() {
        Map<String, String> result = new HashMap<>();
        result.put("bigText", getString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_BIG_TEXT_VI, null));
        result.put("bigTextEn", getString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_BIG_TEXT_EN, null));
        result.put("subText", getString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_SUB_TEXT_VI, null));
        result.put("subTextEn", getString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_SUB_TEXT_EN, null));
        result.put("title", getString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_TITLE_VI, null));
        result.put("titleEn", getString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_TITLE_EN, null));
        result.put("message", getString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_MESSAGE_VI, null));
        result.put("messageEn", getString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_MESSAGE_EN, null));
        result.put("buttonText", getString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_BUTTON_VI, null));
        result.put("buttonTextEn", getString(PreferenceConstants.ENABLE_BLUETOOTH_NOTIFICATION_BUTTON_EN, null));
        return result;
    }

    public boolean getClearScheduleNotification() {
        return getBoolean(PreferenceConstants.CLEAR_SCHEDULE_NOTIFICATION, false);
    }

    public void setClearScheduleNotification(boolean value) {
        putBoolean(PreferenceConstants.CLEAR_SCHEDULE_NOTIFICATION, value);
    }

    public long getTimeAutoEnableBluetooth() {
        return getLong(PreferenceConstants.TIME_AUTOMACTIC_ENABLE_BLUETOOTH, AppConstants.Config.TIME_AUTOMATIC_ENABLE_BLUETOOTH);
    }

    public void setTimeAutoEnableBluetooth(long value) {
        putLong(PreferenceConstants.TIME_AUTOMACTIC_ENABLE_BLUETOOTH, value);
    }

    // Constant pre
    public static class PreferenceConstants {
        // Name
        public static final String BLID = "user_id";
        public static final String LANGUAGE = "app_language";

        // config
        public static final String CONFIG_SCAN_BLE = "scan_ble";
        public static final String CONFIG_BROADCAST_BLE = "broadcast_ble";
        public static final String CONFIG_SCAN_DEVICES = "scan_devices";
        public static final String CONFIG_SEVER_SCAN_BLE_DURATION = "server_scan_ble_duration";
        public static final String CONFIG_SERVER_SCAN_BLE_INTERVAL = "server_scan_ble_interval";
        public static final String CONFIG_SERVER_BROADCAST_BLE_DURATION  = "server_broadcast_ble_duration";
        public static final String CONFIG_SERVER_BROADCAST_BLE_INTERVAL  = "server_broadcast_ble_interval";
        public static final String CONFIG_SERVER_SCAN_DEVICES_DURATION = "server_scan_devices_duration";
        public static final String CONFIG_SERVER_SCAN_DEVICES_INTERVAL = "server_scan_devices_interval";
        public static final String CONFIG_SERVER_ENABLE_BLUETOOTH_BATTERY_LEVEL = "severEnable_bluetooth_battery_level";
        public static final String CONFIG_SERVER_ENABLE_BLUETOOTH_INTERVAL = "severEnable_bluetooth_interval";
        public static final String CONFIG_SERVER_SCAN_DEVICES = "sever_is_scan_devices";
        public static final String CONFIG_SERVER_INSERT_DB = "server_insert_db";
        public static final String CONFIG_SERVER_BACKUP_DB = "server_backup_db";
        public static final String CONFIG_SERVER_CHECK_NOTIFY_REQUEST_PERMISSON = "server_check_notify_request_";

        // config log file
        public static final String CONFIG_LOG_FILE = "log";
        public static final String CONFIG_LOG_BATTERY= "log_battery";

        // Time last backup
        public static final String TIME_LAST_BACKUP = "time_last_backup";

        // clear schedule notification
        public static final String CLEAR_SCHEDULE_NOTIFICATION = "clear_schedule_notification";

        public static final String TIME_AUTOMACTIC_ENABLE_BLUETOOTH = "time_automatic_enable_bluetooth";

        // Schedule Notification Content
        public static final String SCHEDULE_SCAN_NOTIFICATION_BIG_TEXT_VI = "schedule_scan_notification_big_text_vi";
        public static final String SCHEDULE_SCAN_NOTIFICATION_BIG_TEXT_EN = "schedule_scan_notification_big_text_en";
        public static final String SCHEDULE_SCAN_NOTIFICATION_SUB_TEXT_VI = "schedule_scan_notification_sub_text_vi";
        public static final String SCHEDULE_SCAN_NOTIFICATION_SUB_TEXT_EN = "schedule_scan_notification_sub_text_en";
        public static final String SCHEDULE_SCAN_NOTIFICATION_TITLE_VI = "schedule_scan_notification_title_text_vi";
        public static final String SCHEDULE_SCAN_NOTIFICATION_TITLE_EN = "schedule_scan_notification_title_text_en";
        public static final String SCHEDULE_SCAN_NOTIFICATION_MESSAGE_VI = "schedule_scan_notification_message_vi";
        public static final String SCHEDULE_SCAN_NOTIFICATION_MESSAGE_EN = "schedule_scan_notification_message_en";
        public static final String SCHEDULE_SCAN_NOTIFICATION_REPEAT = "schedule_scan_notification_repeat";
        public static final String SCHEDULE_SCAN_NOTIFICATION_BUTTON_VI = "schedule_scan_notification_button_vi";
        public static final String SCHEDULE_SCAN_NOTIFICATION_BUTTON_EN = "schedule_scan_notification_button_en";

        // Notification Content
        public static final String SCAN_NOTIFICATION_BIG_TEXT_VI = "scan_notification_big_text_vi";
        public static final String SCAN_NOTIFICATION_BIG_TEXT_EN = "scan_notification_big_text_en";
        public static final String SCAN_NOTIFICATION_SUB_TEXT_VI = "scan_notification_sub_text_vi";
        public static final String SCAN_NOTIFICATION_SUB_TEXT_EN = "scan_notification_sub_text_en";
        public static final String SCAN_NOTIFICATION_TITLE_VI = "scan_notification_title_text_vi";
        public static final String SCAN_NOTIFICATION_TITLE_EN = "scan_notification_title_text_en";
        public static final String SCAN_NOTIFICATION_MESSAGE_VI = "scan_notification_message_vi";
        public static final String SCAN_NOTIFICATION_MESSAGE_EN = "scan_notification_message_en";
        public static final String SCAN_NOTIFICATION_BUTTON_VI = "scan_notification_button_vi";
        public static final String SCAN_NOTIFICATION_BUTTON_EN = "scan_notification_button_en";

        // Notification auto enable bluetooth
        public static final String ENABLE_BLUETOOTH_NOTIFICATION_BIG_TEXT_VI = "enable_bluetooth_notification_big_text_vi";
        public static final String ENABLE_BLUETOOTH_NOTIFICATION_BIG_TEXT_EN = "enable_bluetooth_notification_big_text_en";
        public static final String ENABLE_BLUETOOTH_NOTIFICATION_SUB_TEXT_VI = "enable_bluetooth_notification_sub_text_vi";
        public static final String ENABLE_BLUETOOTH_NOTIFICATION_SUB_TEXT_EN = "enable_bluetooth_notification_sub_text_en";
        public static final String ENABLE_BLUETOOTH_NOTIFICATION_TITLE_VI = "enable_bluetooth_notification_title_text_vi";
        public static final String ENABLE_BLUETOOTH_NOTIFICATION_TITLE_EN = "enable_bluetooth_notification_title_text_en";
        public static final String ENABLE_BLUETOOTH_NOTIFICATION_MESSAGE_VI = "enable_bluetooth_notification_message_vi";
        public static final String ENABLE_BLUETOOTH_NOTIFICATION_MESSAGE_EN = "enable_bluetooth_notification_message_en";
        public static final String ENABLE_BLUETOOTH_NOTIFICATION_BUTTON_VI = "enable_bluetooth_notification_button_vi";
        public static final String ENABLE_BLUETOOTH_NOTIFICATION_BUTTON_EN = "enable_bluetooth_notification_button_en";
    }
}
