package com.scan;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.scan.apis.AsyncStorageApi;
import com.scan.preference.AppPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

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
//        intent.putExtra("language", language);

        // File dir = reactContext.getDatabasePath("app_db.db");

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

        if(strConfig == null) {
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
                    maxNumberSubKey
            );
        }
        catch (Exception e) {

        }
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
                maxNumberSubKey
        );

        ReadableMap notifyRequestBluetooth = configs.hasKey("NotificationRequestBluetooth") ? configs.getMap("NotificationRequestBluetooth") : null;
        if(notifyRequestBluetooth != null) {
            String itemRepeat = notifyRequestBluetooth.hasKey("itemRepeat") ? notifyRequestBluetooth.getArray("itemRepeat").toString() : "";
            String bigTextVi = notifyRequestBluetooth.hasKey("bigText") ? notifyRequestBluetooth.getString("bigText") : "";
            String bigTextEn = notifyRequestBluetooth.hasKey("bigText_en") ? notifyRequestBluetooth.getString("bigText_en") : "";
            String subTextVi = notifyRequestBluetooth.hasKey("subText") ? notifyRequestBluetooth.getString("subText") : "";
            String subTextEn = notifyRequestBluetooth.hasKey("subText_en") ? notifyRequestBluetooth.getString("subText_en") : "";
            String titleVi = notifyRequestBluetooth.hasKey("title") ? notifyRequestBluetooth.getString("title") : "";
            String titleEn = notifyRequestBluetooth.hasKey("title_en") ? notifyRequestBluetooth.getString("title_en") : "";
            String messageVi = notifyRequestBluetooth.hasKey("message") ? notifyRequestBluetooth.getString("message") : "";
            String messageEn = notifyRequestBluetooth.hasKey("message_en") ? notifyRequestBluetooth.getString("message_en") : "";
            AppConfig.setNotifyRequestBluContent(reactContext, itemRepeat, bigTextVi, bigTextEn, subTextVi, subTextEn, titleVi, titleEn, messageVi, messageEn);
        }

        ReadableMap notifyRequestLocation = configs.hasKey("NotificationRequestLocation") ? configs.getMap("NotificationRequestLocation") : null;
        if(notifyRequestLocation != null) {
            String itemRepeat = notifyRequestLocation.hasKey("itemRepeat") ? notifyRequestLocation.getArray("itemRepeat").toString() : "";
            String bigTextVi = notifyRequestLocation.hasKey("bigText") ? notifyRequestLocation.getString("bigText") : "";
            String bigTextEn = notifyRequestLocation.hasKey("bigText_en") ? notifyRequestLocation.getString("bigText_en") : "";
            String subTextVi = notifyRequestLocation.hasKey("subText") ? notifyRequestLocation.getString("subText") : "";
            String subTextEn = notifyRequestLocation.hasKey("subText_en") ? notifyRequestLocation.getString("subText_en") : "";
            String titleVi = notifyRequestLocation.hasKey("title") ? notifyRequestLocation.getString("title") : "";
            String titleEn = notifyRequestLocation.hasKey("title_en") ? notifyRequestLocation.getString("title_en") : "";
            String messageVi = notifyRequestLocation.hasKey("message") ? notifyRequestLocation.getString("message") : "";
            String messageEn = notifyRequestLocation.hasKey("message_en") ? notifyRequestLocation.getString("message_en") : "";
            AppConfig.setNotifyRequestLocationContent(reactContext, itemRepeat, bigTextVi, bigTextEn, subTextVi, subTextEn, titleVi, titleEn, messageVi, messageEn);
        }

        ReadableMap notifyRequestPermisson = configs.hasKey("NotificationRequestPermissonAndroid") ? configs.getMap("NotificationRequestPermissonAndroid") : null;
        if(notifyRequestLocation != null) {
            String itemRepeat = notifyRequestPermisson.hasKey("itemRepeat") ? notifyRequestPermisson.getArray("itemRepeat").toString() : "";
            String bigTextVi = notifyRequestPermisson.hasKey("bigText") ? notifyRequestPermisson.getString("bigText") : "";
            String bigTextEn = notifyRequestPermisson.hasKey("bigText_en") ? notifyRequestPermisson.getString("bigText_en") : "";
            String subTextVi = notifyRequestPermisson.hasKey("subText") ? notifyRequestPermisson.getString("subText") : "";
            String subTextEn = notifyRequestPermisson.hasKey("subText_en") ? notifyRequestPermisson.getString("subText_en") : "";
            String titleVi = notifyRequestPermisson.hasKey("title") ? notifyRequestPermisson.getString("title") : "";
            String titleEn = notifyRequestPermisson.hasKey("title_en") ? notifyRequestPermisson.getString("title_en") : "";
            String messageVi = notifyRequestPermisson.hasKey("message") ? notifyRequestPermisson.getString("message") : "";
            String messageEn = notifyRequestPermisson.hasKey("message_en") ? notifyRequestPermisson.getString("message_en") : "";

            AppPreferenceManager preferenceManager = AppPreferenceManager.getInstance(reactContext);
            preferenceManager.setNotifyRequestPermisson(
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
        AppUtils.changeLanguageNotification(reactContext, language);
    }

    public void setContentNotify(String title, String content) {
        AppUtils.changeNotification(reactContext, title, content);
    }
}
