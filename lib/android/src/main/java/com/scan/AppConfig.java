package com.scan;

import android.content.Context;

import com.scan.bluezoneid.BluezoneIdGenerator;
import com.scan.preference.AppPreferenceManager;

public class AppConfig {
    public static void setConfigs(
            Context context,
            long timeScanBle,
            long timeSleepScanBle,
            long timeBroadcast,
            long timeSleepBroadcast,
            long timeScanDevices,
            long timeSleepScanDevices,
            long timeSaveLog,
            long dbMaxRow,
            long dbMaxDay,
            long timeBackup,
            long timeIntervalEnableBluetooth,
            int batteryLevelEnableBluetooth,
            long intervalRequestPermisson,
            int maxNumberSubKey
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

        if (batteryLevelEnableBluetooth > 10 * 1000) {
            preferenceManager.setConfigCheckIntervalRequestPermission(intervalRequestPermisson);
        }

        if(maxNumberSubKey > 0) {
            BluezoneIdGenerator.getInstance(context).setMaxNumberSubKey(maxNumberSubKey);
        }
    }

    public static void setNotifyRequestBluContent(
            Context context,
            String itemRepeat,
            String bigTextVi,
            String bigTextEn,
            String subTextVi,
            String subTextEn,
            String titleVi,
            String titleEn,
            String messageVi,
            String messageEn
    ) {
        AppPreferenceManager preferenceManager = AppPreferenceManager.getInstance(context);
        preferenceManager.setNotifyRequestBlu(
                bigTextVi,
                bigTextEn,
                subTextVi,
                subTextEn,
                titleVi,
                titleEn,
                messageVi,
                messageEn,
                itemRepeat
        );
    }

    public static void setNotifyRequestLocationContent(
            Context context,
            String itemRepeat,
            String bigTextVi,
            String bigTextEn,
            String subTextVi,
            String subTextEn,
            String titleVi,
            String titleEn,
            String messageVi,
            String messageEn
    ) {
        AppPreferenceManager preferenceManager = AppPreferenceManager.getInstance(context);
        preferenceManager.setNotifyRequestLocation(
                bigTextVi,
                bigTextEn,
                subTextVi,
                subTextEn,
                titleVi,
                titleEn,
                messageVi,
                messageEn,
                itemRepeat
        );
    }
}
