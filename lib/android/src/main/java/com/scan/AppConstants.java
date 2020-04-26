package com.scan;

/**
 * @author khanhxu
 */
public class AppConstants {
    // Constant pre
    public static class Config {

        // File name config
        public static final String CONFIG_FILE_NAME = "bluezone_config.txt";

        // Index key, duration, interval
        public static final int CONFIG_FILE_INDEX_KEY = 0;
        public static final int CONFIG_FILE_INDEX_VALUE = 1;
        public static final int CONFIG_FILE_INDEX_DURATION = 1;
        public static final int CONFIG_FILE_INDEX_INTERVAL = 2;

        // Count
        public static final int CONFIG_FILE_DEFAULT_COUNT = 2;
        public static final int CONFIG_FILE_SCAN_COUNT = 3;

        // Value config
        public static final String CONFIG_FILE_VALUE_NOT = "0";
        public static final String CONFIG_FILE_VALUE_OK = "1";

        // Cau hinh log file
        public static final boolean IS_CONFIG_LOG_FILE = true;

        // Cau hinh log battery
        public static final boolean IS_CONFIG_LOG_BATTERY = false;

        // Cau hinh co chon log mac dinh hay khong
        public static final boolean IS_CONFIG_SCAN = false;

        // Default
        public static final long DEFAULT_BROADCAST_BLE_DURATION = 15 * 1000;
        public static final long DEFAULT_BROADCAST_BLE_INTERVAL = 15 * 1000;
        public static final long DEFAULT_SCAN_BLE_DURATION = 35 * 1000;
        public static final long DEFAULT_SCAN_BLE_INTERVAL = 85 * 1000;
        public static final long DEFAULT_SCAN_DEVICES_DURATION = 35 * 1000;
        public static final long DEFAULT_SCAN_DEVICES_INTERVAL = 85 * 1000;
        // Enable Bluetooth level
        public static final int DEFAULT_ENABLE_BLUETTOOTH_BATTERY_LEVEL = 15;

        // Time interval enable bluetooth
        public static final long DEFAULT_ENABLE_BLUETTOOTH_INTERVAL = 5 * 60 * 1000;

        // Time insert
        public static final long TIME_DELAY_INSERT = 5 * 10000;
        // Time backup
        public static final long BACKUP_INTERVAL = 1 * 60 * 60 * 1000;

        // ??
        public static long DATABASE_MAX_ROW = 100000;
        public static int DATABASE_MAX_DAY = 100000;

    }

    // UUID Cần phát và bắt
    public static final String BLE_UUID_IOS = "E20A39F4-73F5-4BC4-A12F-17D1AD07A667";
    public static final String BLE_UUID_ANDROID = "E20A39F4-73F5-4BC4-A12F-17D1AD07A889";
    public static final int BLE_ID = 8885; // XU

    // Notification
    public static final String NOTIFICATION_CHANNEL_ID = "BluezoneChannel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Bluezone channel";
    public static final int NOTIFICATION_CHANNEL_ID_CODE = 42020;
    public static final int NOTIFICATION_SERVICE_BLUE_ZONE_ID = 2020;

    // File
    public static final String PATH_APP = "/Bluezone/";
    public static final String LOG_FILE_NAME = "log.txt";
    public static final String LOG_FILE_NAME_BATTERY = "log_battery.txt";

    // UerID length
    public static final int USERID_LENGTH = 6;
    public static final int NOTIFY_SERVICE_NUMBER = 114;

    // Backup
    public static class Backup {
        public static final String ROOT_FOLDER = "backup";
        public static final String DATABASE_NAME = "app_db.db";         // File name DB trong điện thoại
        public static final String FILE_NAME_DB = ".app_backup.db";      // File name DB ngoài thẻ nhớ
        public static final String FILE_NAME_USER_ID = ".userid.txt";    // File name backup UserID
        public static final String KEY_USER_ID = "user_id";              // Key backup UserID
        public static final int DATA_USER_ID_COUNT = 2;
        public static final int DATA_USER_ID_INDEX_KEY = 0;
        public static final int DATA_USER_ID_INDEX_VALUE = 1;
    }
}
