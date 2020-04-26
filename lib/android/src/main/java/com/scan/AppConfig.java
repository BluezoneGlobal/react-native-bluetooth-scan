package com.scan;

import android.content.Context;

import com.scan.preference.AppPreferenceManager;

public class AppConfig {
    public static void setConfigs(
            Context context,
            int timeScanBle,
            int timeSleepScanBle,
            int timeBroadcast,
            int timeSleepBroadcast,
            int timeScanDevices,
            int timeSleepScanDevices,
            int timeSaveLog,
            int dbMaxRow,
            int dbMaxDay,
            int timeBackup,
            int timeIntervalEnableBluetooth,
            int batteryLevelEnableBluetooth
    ) {
        AppPreferenceManager preferenceManager = AppPreferenceManager.getInstance(context);

        if (timeBroadcast > -1) {
            preferenceManager.setConfigBroadcastBleDuration(timeBroadcast);
        }

        if (timeSleepBroadcast > -1) {
            preferenceManager.setConfigBroadcastBleInterval(timeSleepBroadcast);
        }

        if (timeScanBle > -1) {
            preferenceManager.setConfigScanBleDuration(timeScanBle);
        }

        if (timeSleepScanBle > -1) {
            preferenceManager.setConfigScanBleInterval(timeSleepScanBle);
        }

        if (timeScanDevices > -1) {
            preferenceManager.setConfigScanDevicesDuration(timeScanDevices);
        }

        if (timeSleepScanDevices > -1) {
            preferenceManager.setConfigScanDevicesInterval(timeSleepScanDevices);
        }

        if (timeSaveLog > -1) {
            preferenceManager.setConfigInsertDb(timeSaveLog);
        }

        if (dbMaxRow > -1) {
            AppConstants.Config.DATABASE_MAX_ROW = dbMaxRow;
        }

        if (dbMaxDay > -1) {
            AppConstants.Config.DATABASE_MAX_DAY = dbMaxDay;
        }

        if (timeBackup > 60 * 1000) {
            preferenceManager.setConfigBackupDb(timeBackup);
        }

        if (timeIntervalEnableBluetooth > -1) {
            preferenceManager.setConfigEnableBluetoothInterval(timeIntervalEnableBluetooth);
        }

        // Auto anable bluetooth
        if (batteryLevelEnableBluetooth > -1) {
            preferenceManager.setConfigEnableBluetoothBatteryLevel(batteryLevelEnableBluetooth);
        }
    }
}
