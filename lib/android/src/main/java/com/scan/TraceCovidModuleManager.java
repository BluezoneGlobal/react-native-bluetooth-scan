package com.scan;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.scan.apis.AsyncStorageApi;
import com.scan.notification.NotificationUtils;
import com.scan.preference.AppPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class TraceCovidModuleManager {
    public ReactApplicationContext reactContext;
    public TraceCovidModule traceCovidModule;
    public AsyncStorageApi storageApi;

    public TraceCovidModuleManager(ReactApplicationContext reactContext, TraceCovidModule traceCovidModule) {
        this.reactContext = reactContext;
        this.traceCovidModule = traceCovidModule;
        ServiceTraceCovid.moduleManager = this;
        storageApi = new AsyncStorageApi(reactContext);
    }

    public void startService(boolean scanFull) throws JSONException {
        Intent intent = new Intent(reactContext, ServiceTraceCovid.class);
        Log.e("Scan Full: ", scanFull ? "true" : "false");

        intent.putExtra(ServiceTraceCovid.EXTRA_SCHEDULER_TYPE, scanFull ? ServiceTraceCovid.TYPE_SCAN_FULL : ServiceTraceCovid.TYPE_APP_EXIT);

        // Đọc Config trong asyncConfig
        this.setConfigFromAsyncStorege();

        // Start service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            reactContext.startForegroundService(intent);
        } else {
            reactContext.startService(intent);
        }
    }

    public void setConfigFromAsyncStorege() throws JSONException {
        String strConfig = storageApi.getItem("Configuration");

        if(strConfig == null || strConfig.equals("")) {
            return;
        }

        try {
            JSONObject json = new JSONObject(strConfig);
            int timeScanBleRun = json.has("ScanBleRun") ? json.getInt("ScanBleRun") : -1;
            int timeScanBleSleep = json.has("ScanBleSleep") ? json.getInt("ScanBleSleep") : -1;
            int timeBroadcastBleRun = json.has("BroadcastBleRun") ? json.getInt("BroadcastBleRun") : -1;
            int timeBroadcastBleSleep = json.has("BroadcastBleSleep") ? json.getInt("BroadcastBleSleep") : -1;
            int timeScanDeviceRun = json.has("ScanDevicesRun") ? json.getInt("ScanDevicesRun") : -1;
            int timeScanDeviceSleep = json.has("ScanDevicesSleep") ? json.getInt("ScanDevicesSleep") : -1;
            int timeSaveLog = json.has("TimeSaveLog") ? json.getInt("TimeSaveLog") : -1;
            int dbMaxRow = json.has("DbMaxRow") ? json.getInt("DbMaxRow") : -1;
            int dbMaxDay = json.has("DbMaxDay") ? json.getInt("DbMaxDay") : -1;
            int timeBackup = json.has("TimeBackup") ? json.getInt("TimeBackup") : -1;

            int timeIntervalEnableBluetooth = json.has("TimeEnableBluetooth") ? json.getInt("TimeEnableBluetooth") : -1;
            int batteryLevelEnableBluetooth = json.has("BatteryEnableBluetooth") ? json.getInt("BatteryEnableBluetooth") : -1;
            int intervalRequestPermisson = json.has("IntervalRequestPermisson") ? json.getInt("IntervalRequestPermisson") : -1;
            int maxNumberSubKey = json.has("MaxNumberSubKeyPerDay") ? json.getInt("MaxNumberSubKeyPerDay") : -1;
            int timeAutoEnableBluetooth = json.has("TimeAutoEnableBluetooth") ? json.getInt("TimeAutoEnableBluetooth") : -1;

            AppConfig.setConfigs(
                    reactContext,
                    timeScanBleRun,
                    timeScanBleSleep,
                    timeBroadcastBleRun,
                    timeBroadcastBleSleep,
                    timeScanDeviceRun,
                    timeScanDeviceSleep,
                    timeSaveLog,
                    dbMaxRow,
                    dbMaxDay,
                    timeBackup,
                    timeIntervalEnableBluetooth,
                    batteryLevelEnableBluetooth,
                    intervalRequestPermisson,
                    maxNumberSubKey,
                    timeAutoEnableBluetooth
            );
        }
        catch (Exception e) {

        }
    }

    public static String[] getInfoNotificationMap(ReadableMap map, String[] keys) {
        String[] result = new String[keys.length];
        for(int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if(map.hasKey(key)) {
                result[i] = map.getType(key).name().equals("Array") ? Objects.requireNonNull(map.getArray(key)).toString() : map.getString(key);
            } else {
                result[i] = null;
            }
        }
        return result;
    }

    public void setConfig (ReadableMap configs) {
        int timeScanBleRun = configs.hasKey("ScanBleRun") ? configs.getInt("ScanBleRun") : -1;
        int timeScanBleSleep = configs.hasKey("ScanBleSleep") ? configs.getInt("ScanBleSleep") : -1;
        int timeBroadcastBleRun = configs.hasKey("BroadcastBleRun") ? configs.getInt("BroadcastBleRun") : -1;
        int timeBroadcastBleSleep = configs.hasKey("BroadcastBleSleep") ? configs.getInt("BroadcastBleSleep") : -1;
        int timeScanDeviceRun = configs.hasKey("ScanDevicesRun") ? configs.getInt("ScanDevicesRun") : -1;
        int timeScanDeviceSleep = configs.hasKey("ScanDevicesSleep") ? configs.getInt("ScanDevicesSleep") : -1;
        int timeSaveLog = configs.hasKey("TimeSaveLog") ? configs.getInt("TimeSaveLog") : -1;
        int dbMaxRow = configs.hasKey("DbMaxRow") ? configs.getInt("DbMaxRow") : -1;
        int dbMaxDay = configs.hasKey("DbMaxDay") ? configs.getInt("DbMaxDay") : -1;
        int timeBackup = configs.hasKey("TimeBackup") ? configs.getInt("TimeBackup") : -1;
        int timeIntervalEnableBluetooth = configs.hasKey("TimeEnableBluetooth") ? configs.getInt("TimeEnableBluetooth") : -1;
        int batteryLevelEnableBluetooth = configs.hasKey("BatteryEnableBluetooth") ? configs.getInt("BatteryEnableBluetooth") : -1;
        int intervalRequestPermisson = configs.hasKey("IntervalRequestPermisson") ? configs.getInt("IntervalRequestPermisson") : -1;
        int maxNumberSubKey = configs.hasKey("MaxNumberSubKeyPerDay") ? configs.getInt("MaxNumberSubKeyPerDay") : -1;
        int timeAutoEnableBluetooth = configs.hasKey("TimeAutoEnableBluetooth") ? configs.getInt("TimeAutoEnableBluetooth") : -1;

        AppConfig.setConfigs(
                reactContext,
                timeScanBleRun,
                timeScanBleSleep,
                timeBroadcastBleRun,
                timeBroadcastBleSleep,
                timeScanDeviceRun,
                timeScanDeviceSleep,
                timeSaveLog,
                dbMaxRow,
                dbMaxDay,
                timeBackup,
                timeIntervalEnableBluetooth,
                batteryLevelEnableBluetooth,
                intervalRequestPermisson,
                maxNumberSubKey,
                timeAutoEnableBluetooth
        );

        AppPreferenceManager preferenceManager = AppPreferenceManager.getInstance(reactContext);

        Map<String, String> oldScanNotificationConfig = preferenceManager.getScanNotification();
        Map<String, String> oldScheduleScanNotificationConfig = preferenceManager.getScheduleScanNotification();
        Map<String, String> oldEnableBluetoothNotificationConfig =  preferenceManager.getEnableBluetoothNotification();

        String[] keys = {"title", "titleEn", "bigText", "bigTextEn", "message", "messageEn", "subText", "subTextEn", "buttonText", "buttonTextEn", "itemRepeat"};

        ReadableMap scheduleScanNotification = configs.hasKey("AndroidScheduleScanNotification") ? configs.getMap("AndroidScheduleScanNotification") : null;
        if(scheduleScanNotification != null) {
            String[] scheduleScanInfo = getInfoNotificationMap(scheduleScanNotification, keys);
            preferenceManager.setScheduleScanNotification(
                    scheduleScanInfo[0],
                    scheduleScanInfo[1],
                    scheduleScanInfo[2],
                    scheduleScanInfo[3],
                    scheduleScanInfo[4],
                    scheduleScanInfo[5],
                    scheduleScanInfo[6],
                    scheduleScanInfo[7],
                    scheduleScanInfo[8],
                    scheduleScanInfo[9],
                    scheduleScanInfo[10]
            );
        }

        ReadableMap scanNotification = configs.hasKey("AndroidScanNotification") ? configs.getMap("AndroidScanNotification") : null;
        if(scanNotification != null) {
            String[] scanNotificationInfo = getInfoNotificationMap(scanNotification, keys);
            preferenceManager.setScanNotification(
                    scanNotificationInfo[0],
                    scanNotificationInfo[1],
                    scanNotificationInfo[2],
                    scanNotificationInfo[3],
                    scanNotificationInfo[4],
                    scanNotificationInfo[5],
                    scanNotificationInfo[6],
                    scanNotificationInfo[7],
                    scanNotificationInfo[8],
                    scanNotificationInfo[9]
            );
        }

        ReadableMap locationPermissonNotificationVersion2 = configs.hasKey("AndroidLocationPermissonVersion2") ? configs.getMap("AndroidLocationPermissonVersion2") : null;
        if(locationPermissonNotificationVersion2 != null) {
            String[] locationPermissonInfo = getInfoNotificationMap(locationPermissonNotificationVersion2, keys);
            preferenceManager.setLocationPermissonNotificationV2(
                    locationPermissonInfo[0],
                    locationPermissonInfo[1],
                    locationPermissonInfo[2],
                    locationPermissonInfo[3],
                    locationPermissonInfo[4],
                    locationPermissonInfo[5],
                    locationPermissonInfo[6],
                    locationPermissonInfo[7],
                    locationPermissonInfo[8],
                    locationPermissonInfo[9]
            );
        }

        ReadableMap scanNotificationVersion2 = configs.hasKey("AndroidScanNotificationVersion2") ? configs.getMap("AndroidScanNotificationVersion2") : null;
        if(scanNotificationVersion2 != null) {
            String[] scanVersion2Info = getInfoNotificationMap(scanNotificationVersion2, keys);
            preferenceManager.setScanNotificationV2(
                    scanVersion2Info[0],
                    scanVersion2Info[1],
                    scanVersion2Info[2],
                    scanVersion2Info[3],
                    scanVersion2Info[4],
                    scanVersion2Info[5],
                    scanVersion2Info[6],
                    scanVersion2Info[7],
                    scanVersion2Info[8],
                    scanVersion2Info[9]
            );
        }

        ReadableMap enableBluetoothNotification = configs.hasKey("AndroidEnableBluetoothNotification") ? configs.getMap("AndroidEnableBluetoothNotification") : null;
        if(enableBluetoothNotification != null) {
            String[] enableBluetoothInfo = getInfoNotificationMap(enableBluetoothNotification, keys);
            preferenceManager.setEnableBluetoothNotification(
                    enableBluetoothInfo[0],
                    enableBluetoothInfo[1],
                    enableBluetoothInfo[2],
                    enableBluetoothInfo[3],
                    enableBluetoothInfo[4],
                    enableBluetoothInfo[5],
                    enableBluetoothInfo[6],
                    enableBluetoothInfo[7],
                    enableBluetoothInfo[8],
                    enableBluetoothInfo[9]
            );
        }

//        NotificationUtils.scanNotificationChangeConfiguration(oldScanNotificationConfig, scanNotification);
        try {
            NotificationUtils.scheduleScanNotificationChangeConfiguration(reactContext, oldScheduleScanNotificationConfig, scheduleScanNotification);
        } catch(Exception e) {

        }
//        NotificationUtils.enableBluetoothNotificationChangeConfiguration(oldEnableBluetoothNotificationConfig, enableBluetoothNotification);
    }

//    public WritableMap getConfig () {
//        WritableMap config = new WritableNativeMap();
//        config.putInt("timeScanBleRun", (int)AppConfig.TIME_SCAN_BLE);
//        config.putInt("timeScanBleSleep", (int)AppConfig.TIME_SLEEP_SCAN_BLE);
//        config.putInt("timeBroadcastBleRun", (int)AppConfig.TIME_BROADCAST_BLE);
//        config.putInt("timeBroadcastBleSleep", (int)AppConfig.TIME_SLEEP_BROADCAST_BLE);
//        config.putInt("timeScanDevicesRun", (int)AppConfig.TIME_SCAN_DEVICES);
//        config.putInt("timeScanDevicesSleep", (int)AppConfig.TIME_SLEEP_SCAN_DEVICES);
//        config.putInt("timeSaveLog", (int)AppConfig.TIME_SAVE_LOG);
//        return config;
//    }

//    public void stopService() {
//        // Start broadcast tắt service
//        try {
//            Intent broadCastIntent = new Intent();
//            broadCastIntent.setAction(ServiceTraceCovid.ACTION_RECEIVER_STOP);
//            reactContext.sendBroadcast(broadCastIntent);
//            Toast.makeText(reactContext, "Stop Service Success", Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void emit(String id, String name, String address, int rssi, String platform, int type) {
        WritableMap params = Arguments.createMap();
        params.putString("id", id);
        params.putString("name", name);
        params.putString("address", address);
        params.putInt("rssi", rssi);
        params.putString("platform", platform);
        params.putInt("typeScan", type);
        traceCovidModule.emitEvent("onScanResult", params);
    }

    public void emitBlueTooth(String id, String name, String address, int rssi, String platform, int type) {
        WritableMap params = Arguments.createMap();
        params.putString("id", id);
        params.putString("name", name);
        params.putString("address", address);
        params.putInt("rssi", rssi);
        params.putString("platform", platform);
        params.putInt("typeScan", type);
        traceCovidModule.emitEvent("onScanBlueToothResult", params);
    }

    public void emitBluezoneIdChange(String bluezoneId) {
        traceCovidModule.emitEvent("onBluezoneIdChange", bluezoneId);
    }

    public void setLanguage(String language) {
        AppPreferenceManager.getInstance(reactContext).setLanguage(language);
        NotificationUtils.changeLanguageNotification(reactContext, language);
    }

    public void setContentNotify(String title, String content) {
        NotificationUtils.displayServiceNotification(reactContext, R.mipmap.icon_bluezone_service, title, content, null);
    }
}
