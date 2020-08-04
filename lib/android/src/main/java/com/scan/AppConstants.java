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

        // Config log file
        public static final boolean IS_CONFIG_LOG_FILE = false;

        // Config log battery
        public static final boolean IS_CONFIG_LOG_BATTERY = false;

        // Config Scan Devices
        public static final boolean IS_CONFIG_SCAN_DEVICES = false;

        // Default
        public static final long DEFAULT_BROADCAST_BLE_DURATION = 15 * 1000;
        public static final long DEFAULT_BROADCAST_BLE_INTERVAL = 15 * 1000;
        public static final long DEFAULT_SCAN_BLE_DURATION = 40 * 1000;
        public static final long DEFAULT_SCAN_BLE_INTERVAL = 260 * 1000;
        public static final long DEFAULT_SCAN_DEVICES_DURATION = 40 * 1000;
        public static final long DEFAULT_SCAN_DEVICES_INTERVAL = 260 * 1000;
        public static final long DEFAULT_FULL_DURATION = (2 * 60 + 30) * 1000;
        public static final long DEFAULT_FULL_INTERVAL = 5 * 1000;
        public static final long DEFAULT_RESCAN_INTERVAL = 5 * 1000;

        // Enable Bluetooth level
        public static final int DEFAULT_ENABLE_BLUETTOOTH_BATTERY_LEVEL = 15;

        // Time interval enable bluetooth
        public static final long DEFAULT_ENABLE_BLUETTOOTH_INTERVAL = 5 * 60 * 1000;

        // Time insert
        public static final long TIME_DELAY_INSERT = 5 * 1000;
        // Time backup
        public static final long BACKUP_INTERVAL = 1 * 60 * 60 * 1000;

        // Timmer check permisson to create notification
        public static final long DEFAULT_INTERVAL_CHECK_PERMISSON = 2 * 60 * 1000;

        // Timeout connect
        public static final long TIMEOUT_CONNECT = 10 * 1000;

        // Time delay report scan
        public static final long TIME_SCAN_BLE_REPORT_DELAY = 10 * 1000;

        // ??
        public static long DATABASE_MAX_ROW = 100000;
        public static long DATABASE_MAX_DAY = 100000;

    }

    // UUID Cần phát và bắt
    public static final String BLUEZONE_UUID = "4E56"; // VN
    public static final int BLE_ID = 8885; // XU
    public static final String BLE_UUID_IOS = "0000" + BLUEZONE_UUID + "-73F5-4BC4-A12F-17D1AD07A667";
    public static final String BLE_UUID_ANDROID = "0000" + BLUEZONE_UUID + "-0000-1000-8000-00805F9B34FB";
    public static final String BLE_UUID_CHARECTIC = "0000" + BLUEZONE_UUID + "-73F5-4BC4-A12F-17D1AD07A689";
    public static final int DEFAUT_MANUFACTOR_IOS = 0x004c;
    public static final byte[] DEFAUT_MANUFACTOR_BYTE_IOS_X = new byte[] {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, 0, 0, 0, 0, 0};
    public static final byte[] DEFAUT_MANUFACTOR_BYTE_IOS = new byte[] {1, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static final byte[] BLUEZONE_BYTE_NONE = new byte[] {0};

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

    // PlatfromName
    public static final String PLATFORM_ANDROID = "Android";
    public static final String PLATFORM_IOS = "iOS";

    // Backup
    public static class Backup {
        public static final String ROOT_FOLDER = "backup";
        public static final String DATABASE_NAME = "app_db.db";         // File name DB trong điện thoại
        public static final String FILE_NAME_DB = ".app_backup.db";      // File name DB ngoài thẻ nhớ
        public static final String FILE_NAME_USER_ID = ".userid.txt";    // File name backup UserID
        public static final String FILE_NAME_DATA = "data_trace.txt";    // Data trace put server
        public static final String KEY_USER_ID = "user_id";              // Key backup UserID
        public static final int DATA_USER_ID_COUNT = 2;
        public static final int DATA_USER_ID_INDEX_KEY = 0;
        public static final int DATA_USER_ID_INDEX_VALUE = 1;
    }
}
