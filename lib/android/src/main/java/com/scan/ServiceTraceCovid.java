package com.scan;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReactApplicationContext;
import com.scan.database.AppDatabaseHelper;
import com.scan.database.CacheDatabaseHelper;
import com.scan.model.ScanConfig;
import com.scan.preference.AppPreferenceManager;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class service thực hiện viẹc phát và bắt các kết nối
 * @author khanhxu
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ServiceTraceCovid extends Service {
    // Context
    public static ReactApplicationContext reactContext;
    public static TraceCovidModuleManager moduleManager;

    // Stop service
    public static final String ACTION_RECEIVER_STOP = "action_service_trace_stop";

    // Loai scheduler
    public static final String EXTRA_SCHEDULER_TYPE = "extra_scheduler_type";

    // Type
    public static final int TYPE_SCHEDULER_NONE = 0;
    public static final int TYPE_SCHEDULER_SCAN_BLE = 1;
    public static final int TYPE_SCHEDULER_SCAN_BLE_STOP = 2;
    public static final int TYPE_SCHEDULER_BROADCAST_BLE = 3;
    public static final int TYPE_SCHEDULER_BROADCAST_BLE_STOP = 4;
    public static final int TYPE_SCHEDULER_SCAN_DEVICES = 5;
    public static final int TYPE_SCHEDULER_SCAN_DEVICES_STOP = 6;
    public static final int TYPE_SCHEDULER_SCAN_FULL = 7;
    public static final int TYPE_SCHEDULER_SCAN_FULL_STOP = 8;
    public static final int TYPE_SCHEDULER_ENABLE_BLUETOOTH = 10;
    public static final int TYPE_SCAN_FULL = 90;
    public static final int TYPE_APP_EXIT = 91;

    // Services callback
    private BatteryReceiver mReceiverBattery;
    private BluetoothChangedReceiver mReceiverBluetoothChanged;

    // Status Scan
    public static final int STATUS_SCAN_FINISH = 0;
    public static final int STATUS_SCAN_SETUP = 1;
    public static final int STATUS_SCANNING = 2;

    // Bluetooth scan devices
    private BluetoothScanBroadCast mScanDevicesReceiver;
    private BluetoothAdapter mBluetoothAdapter;
    private int mStatusScanDevices = STATUS_SCAN_FINISH;

    // Bluetooth scan LE
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanCallback mScanCallback;
    private int mStatusScanBle = STATUS_SCAN_FINISH;

    // Bluetooth broadcast LE
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private AdvertiseCallback mAdvertiseCallback;
    private int mStatusAdvertising = STATUS_SCAN_FINISH;

    // Time Scan
    private ScanConfig mScanConfigBroadcastBle;
    private ScanConfig mScanConfigDevices;
    private ScanConfig mScanConfigBle;

    // Config log
    private boolean isConfigLog = AppConstants.Config.IS_CONFIG_LOG_FILE;
    private boolean isConfigLogBattery = AppConstants.Config.IS_CONFIG_LOG_BATTERY;

    // Vả Scan
    private int mModeScan = MODE_SCAN_SCHEDULER;

    // cac mode
    public static final int MODE_SCAN_FULL = 1;
    public static final int MODE_SCAN_SCHEDULER = 2;

    // Scan
    List<ScanResult> mScanResultList;
    List<String> mMacConnectList;

    // Bluetooth Gatt
    BluetoothGatt mBluetoothGatt;
    ConnectTask mConnectTask;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Log
        writeLog("Start ServiceTraceCovid");

        // Khoi tao notification
        AppUtils.startNotification(this, getApplicationContext());

        // Khoi tao Bluetooth
        initBluetooth();

        // Init status
        initStatus();

        // Array
        mScanResultList = new ArrayList<>();
        mMacConnectList = new ArrayList<>();
    }

    /**
     * Khoi tao cac bien
     */
    private void initBluetooth() {
        // Init
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        mBluetoothLeAdvertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
    }

    /**
     * Init status
     */
    private void initStatus() {
        // Flag
        mStatusScanBle = STATUS_SCAN_FINISH;
        mStatusAdvertising = STATUS_SCAN_FINISH;
        mStatusScanDevices = STATUS_SCAN_FINISH;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Init log config
        initConfigLog();

        // Check scheduler
        initScheduler(intent);

        // Check
        if (!AppUtils.enableBluetooth()) {
            // Start tat ca
            startAll();
        }

        // Khởi tao receiver nhan su kien ket thuc service
        registerReceiverStopService();

        // Lang nghe su kien thay doi pin
        registerReceiverBattery();

        return START_STICKY;
    }

    /**
     * Khoi tao cac bien
     */
    private void initConfigLog() {
        // Lay cofig
        isConfigLog = AppUtils.getConfigLogFile(getApplicationContext());
        isConfigLogBattery = AppUtils.getConfigLogBattery(getApplicationContext());

        // Lay cac bien thoi gian
        mScanConfigBle = AppUtils.getConfigScan(reactContext, AppPreferenceManager.PreferenceConstants.CONFIG_SCAN_BLE);
        mScanConfigBroadcastBle = AppUtils.getConfigScan(reactContext, AppPreferenceManager.PreferenceConstants.CONFIG_BROADCAST_BLE);
        mScanConfigDevices = AppUtils.getConfigScan(reactContext, AppPreferenceManager.PreferenceConstants.CONFIG_SCAN_DEVICES);

        writeLog("mScanConfigBle: " + mScanConfigBle.getDuration() + ":" + mScanConfigBle.getInterval());
        writeLog("mScanConfigBroadcastBle: " + mScanConfigBroadcastBle.getDuration() + ":" + mScanConfigBroadcastBle.getInterval());
        writeLog("mScanConfigDevices: " + mScanConfigDevices.getDuration() + ":" + mScanConfigDevices.getInterval());
    }

    /**
     * Khoi tao cac bien
     */
    private void initScheduler(Intent intent) {
        // check
        if (intent != null) {
            int typeScheduler = intent.getIntExtra(EXTRA_SCHEDULER_TYPE, TYPE_SCHEDULER_NONE);

            // Check type
            switch (typeScheduler) {
                case TYPE_SCHEDULER_SCAN_BLE:
                    // Check
                    if (mModeScan != MODE_SCAN_FULL) {
                        // Reset bien Status
                        mStatusScanBle = STATUS_SCAN_FINISH;

                        // Scan lai ble
                        startScanBle();
                    }
                    break;
                case TYPE_SCHEDULER_SCAN_BLE_STOP:
                    writeLog("startScanBle: stop");

                    // Check
                    if (mModeScan != MODE_SCAN_FULL) {
                        // Stop scan
                        stopScanBle();

                        // Dat lịch scan
                        callAlarmTimer(TYPE_SCHEDULER_SCAN_BLE);
                    }
                    break;
                case TYPE_SCHEDULER_BROADCAST_BLE:
                    // Check
                    if (mModeScan != MODE_SCAN_FULL) {
                        // Reset bien
                        mStatusAdvertising = STATUS_SCAN_FINISH;

                        // Start lai ble
                        startBroadCastBle();
                    }
                    break;
                case TYPE_SCHEDULER_BROADCAST_BLE_STOP:
                    writeLog("startBroadCastBle: stop");

                    // Check
                    if (mModeScan != MODE_SCAN_FULL) {
                        // Stop
                        stopBroadcastBle();

                        // Đặt lịch phát
                        callAlarmTimer(TYPE_SCHEDULER_BROADCAST_BLE);
                    }
                    break;
                case TYPE_SCHEDULER_SCAN_DEVICES:
                    // Check
                    if (mModeScan != MODE_SCAN_FULL) {
                        // Reset bien
                        mStatusScanDevices = STATUS_SCAN_FINISH;

                        // Start scan lai
                        scanDevicesBluetooth();
                    }
                    break;
                case TYPE_SCHEDULER_SCAN_DEVICES_STOP:
                    // Check
                    if (mModeScan != MODE_SCAN_FULL) {
                        writeLog("scanDevicesBluetooth: Stop");

                        // Huy dang ki
                        unregisterReceiverScanDevices();

                        // Đặt lịch scan
                        callAlarmTimer(TYPE_SCHEDULER_SCAN_DEVICES);
                    }
                    break;
                case TYPE_SCAN_FULL:
                    // Check
                    if (mModeScan != MODE_SCAN_FULL) {
                        // Huy tat ca
                        writeLog("TYPE_SCAN_FULL");

                        // Check scan
                        if (mStatusScanBle != STATUS_SCAN_FINISH) {
                            // Reset
                            mStatusScanBle = STATUS_SCAN_FINISH;

                            // Stop scan
                            stopScanBle();
                        }

                        // Check broadcast
                        if (mStatusAdvertising != STATUS_SCAN_FINISH) {
                            // reset
                            mStatusAdvertising = STATUS_SCAN_FINISH;

                            // Huy phat
                            stopBroadcastBle();
                        }

                        // Check scan devices
                        if (mStatusScanDevices != STATUS_SCAN_FINISH) {
                            // reset
                            mStatusScanDevices = STATUS_SCAN_FINISH;

                            // Huy dang ki
                            unregisterReceiverScanDevices();
                        }

                        // Huy timer
                        cancelAlarmTimerAll();

                        // Scan lai tu dau
                        mModeScan = MODE_SCAN_FULL;

                        // Đặt lịch stop
                        callAlarmTimer(TYPE_SCHEDULER_SCAN_FULL_STOP);
                    }

                    break;
                case TYPE_APP_EXIT:
                    // Check
                    if (mModeScan != MODE_SCAN_SCHEDULER) {
                        // Huy tat ca
                        writeLog("TYPE_APP_EXIT");

                        // Scan lai tu dau
                        mModeScan = MODE_SCAN_SCHEDULER;

                        // Dat lich stop
                        callAlarmTimer(TYPE_SCHEDULER_SCAN_BLE_STOP);
                        callAlarmTimer(TYPE_SCHEDULER_BROADCAST_BLE_STOP);
                        callAlarmTimer(TYPE_SCHEDULER_SCAN_DEVICES_STOP);
                    }
                    break;
                case TYPE_SCHEDULER_ENABLE_BLUETOOTH:
                    // Get battery level
                    int batteryLevel = AppUtils.getBatteryLevel(getApplicationContext());

                    // Bat Bluettooth
                    if (!AppUtils.enableBluetooth(getApplicationContext(), batteryLevel)) {
                        // Đặt lịch bật lại
                        callAlarmTimer(TYPE_SCHEDULER_ENABLE_BLUETOOTH);
                    }
                    break;
                case TYPE_SCHEDULER_SCAN_FULL:
                    // Check
                    if (mModeScan == MODE_SCAN_FULL) {
                        writeLog("scheduler scan full");

                        // Huy dang ki
                        initStatus();

                        // Đặt lịch scan full
                        callAlarmTimer(TYPE_SCHEDULER_SCAN_FULL_STOP);
                    }
                    break;

                case TYPE_SCHEDULER_SCAN_FULL_STOP:
                    // Check
                    if (mModeScan == MODE_SCAN_FULL) {
                        writeLog("scheduler scan full: Stop");

                        // Huy dang ki
                        stopBluetoothFeature();

                        // Đặt lịch scan full
                        callAlarmTimer(TYPE_SCHEDULER_SCAN_FULL);
                    }
                    break;
            }
        }
    }

    /**
     * Dat lich
     * @param typeScheduler
     * @param requestCode
     */
    private void initAlarmTimer(int typeScheduler, int requestCode, long intervalMillis) {
        Intent intentAlarm = new Intent(getApplicationContext(), ServiceTraceCovid.class);
        intentAlarm.putExtra(EXTRA_SCHEDULER_TYPE, typeScheduler);

        // Dat lich
        AppScheduler.schedule(getApplicationContext(), intentAlarm, requestCode, intervalMillis);
    }

    /**
     * Gọi đặt lịch
     * @param typeScheduler
     */
    private void callAlarmTimer(int typeScheduler) {
        if (mModeScan == MODE_SCAN_SCHEDULER) {
            // Duyet
            switch (typeScheduler) {
                case TYPE_SCHEDULER_SCAN_BLE:
                    // Dat lich scan
                    if (mScanConfigBle != null && mScanConfigBle.getDuration() > 0) {
                        // Dat timer tat
                        initAlarmTimer(TYPE_SCHEDULER_SCAN_BLE, TYPE_SCHEDULER_SCAN_BLE, mScanConfigBle.getInterval());
                    }
                    break;
                case TYPE_SCHEDULER_SCAN_BLE_STOP:
                    // Dat lich tat
                    if (mScanConfigBle != null && mScanConfigBle.getDuration() > 0) {
                        // Dat timer tat
                        initAlarmTimer(TYPE_SCHEDULER_SCAN_BLE_STOP, TYPE_SCHEDULER_SCAN_BLE_STOP, mScanConfigBle.getDuration());
                    }
                    break;
                case TYPE_SCHEDULER_BROADCAST_BLE:
                    if (mScanConfigBroadcastBle != null && mScanConfigBroadcastBle.getDuration() > 0) {
                        // Dat timer tat
                        initAlarmTimer(TYPE_SCHEDULER_BROADCAST_BLE, TYPE_SCHEDULER_BROADCAST_BLE, mScanConfigBroadcastBle.getInterval());
                    }
                    break;
                case TYPE_SCHEDULER_BROADCAST_BLE_STOP:
                    if (mScanConfigBroadcastBle != null && mScanConfigBroadcastBle.getDuration() > 0) {
                        // Dat timer tat
                        initAlarmTimer(TYPE_SCHEDULER_BROADCAST_BLE_STOP, TYPE_SCHEDULER_BROADCAST_BLE_STOP, mScanConfigBroadcastBle.getDuration());
                    }
                    break;
                case TYPE_SCHEDULER_SCAN_DEVICES:
                    if (mScanConfigDevices != null && mScanConfigDevices.getDuration() > 0) {
                        // Dat timer tat
                        initAlarmTimer(TYPE_SCHEDULER_SCAN_DEVICES, TYPE_SCHEDULER_SCAN_DEVICES, mScanConfigDevices.getInterval());
                    }
                    break;
                case TYPE_SCHEDULER_SCAN_DEVICES_STOP:
                    if (mScanConfigDevices != null && mScanConfigDevices.getDuration() > 0) {
                        // Dat timer tat
                        initAlarmTimer(TYPE_SCHEDULER_SCAN_DEVICES_STOP, TYPE_SCHEDULER_SCAN_DEVICES_STOP, mScanConfigDevices.getDuration());
                    }
                    break;
            }
        } else {
            switch (typeScheduler) {
                case TYPE_SCHEDULER_SCAN_FULL:
                    // Dat timer
                    initAlarmTimer(TYPE_SCHEDULER_SCAN_FULL, TYPE_SCHEDULER_SCAN_FULL, AppConstants.Config.DEFAULT_FULL_INTERVAL);
                    break;
                case TYPE_SCHEDULER_SCAN_FULL_STOP:
                    // Dat timer
                    initAlarmTimer(TYPE_SCHEDULER_SCAN_FULL_STOP, TYPE_SCHEDULER_SCAN_FULL_STOP, AppConstants.Config.DEFAULT_FULL_DURATION);
                    break;
                case TYPE_SCHEDULER_ENABLE_BLUETOOTH:
                    // Dat timer enable bluetooth
                    initAlarmTimer(TYPE_SCHEDULER_ENABLE_BLUETOOTH, TYPE_SCHEDULER_ENABLE_BLUETOOTH,
                            AppPreferenceManager.getInstance(getApplicationContext()).getConfigEnableBluetoothInterval(AppConstants.Config.DEFAULT_ENABLE_BLUETTOOTH_INTERVAL));
                    break;
            }
        }
    }

    /**
     * Cancel all
     */
    private void cancelAlarmTimerAll() {
        // Xóa hết timer
        cancelAlarmTimer(TYPE_SCHEDULER_SCAN_BLE, TYPE_SCHEDULER_SCAN_BLE);
        cancelAlarmTimer(TYPE_SCHEDULER_SCAN_BLE_STOP, TYPE_SCHEDULER_SCAN_BLE_STOP);
        cancelAlarmTimer(TYPE_SCHEDULER_BROADCAST_BLE, TYPE_SCHEDULER_BROADCAST_BLE);
        cancelAlarmTimer(TYPE_SCHEDULER_BROADCAST_BLE_STOP, TYPE_SCHEDULER_BROADCAST_BLE_STOP);
        cancelAlarmTimer(TYPE_SCHEDULER_SCAN_DEVICES, TYPE_SCHEDULER_SCAN_DEVICES);
        cancelAlarmTimer(TYPE_SCHEDULER_SCAN_DEVICES_STOP, TYPE_SCHEDULER_SCAN_DEVICES_STOP);
    }

    /**
     * Cancel dat lich
     * @param typeScheduler
     * @param requestCode
     */
    private void cancelAlarmTimer(int typeScheduler, int requestCode) {
        Intent intentAlarm = new Intent(getApplicationContext(), ServiceTraceCovid.class);
        intentAlarm.putExtra(EXTRA_SCHEDULER_TYPE, typeScheduler);

        AppScheduler.cancelScheduler(getApplicationContext(), intentAlarm, requestCode);
    }

    /**
     * Bat dau tat ca
     */
    private void startAll() {
        // Start phat ra broacast cho máy khác tim kiếm
        startBroadCastBle();

        // Start scan ble
        startScanBle();

        // Start bluetooth
        scanDevicesBluetooth();
    }

    /**
     * Phat song BLE
     */
    public void startBroadCastBle() {
        try {
            // Check
            if (mBluetoothLeAdvertiser != null && mStatusAdvertising == STATUS_SCAN_FINISH) {
                // Set lai status
                mStatusAdvertising = STATUS_SCAN_SETUP;

                // Log
                writeLog("startBroadCastBle setup");

                // Advertise build
                AdvertiseSettings.Builder advertiseSettings = new AdvertiseSettings.Builder();

                // Che do low power
                advertiseSettings.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
                advertiseSettings.setConnectable(true);

                // data advertise BLE
                AdvertiseData.Builder builder = new AdvertiseData.Builder();
                builder.setIncludeDeviceName(false);
                builder.setIncludeTxPowerLevel(false);

                // ghi ten vao manufacture
                builder.addManufacturerData(AppConstants.BLE_ID,
                        AppPreferenceManager.getInstance(getApplicationContext()).getPhoneNumber().getBytes(Charset.forName("UTF-8")));
                builder.addServiceUuid(AppUtils.BLE_UUID_ANDROID);

                // Callback start
                mAdvertiseCallback = new AdvertiseCallback() {
                    @Override
                    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                        super.onStartSuccess(settingsInEffect);

                        // Set bien
                        mStatusAdvertising = STATUS_SCANNING;

                        // Log
                        writeLog("Start: BluetoothLeAdvertiser : start : success");
                    }

                    @Override
                    public void onStartFailure(int errorCode) {
                        super.onStartFailure(errorCode);

                        // Set bien
                        mStatusAdvertising = STATUS_SCAN_FINISH;

                        // Log
                        writeLog("Start: BluetoothLeAdvertiser : start : fail : Code: " + errorCode);
                    }
                };

                // Start broadCast ble
                mBluetoothLeAdvertiser.startAdvertising(advertiseSettings.build(), builder.build(), mAdvertiseCallback);

                // Đăt lịch stop
                callAlarmTimer(TYPE_SCHEDULER_BROADCAST_BLE_STOP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop broadcast ble
     */
    private void stopBroadcastBle() {
        try {
            // Check phat va stop
            if (mBluetoothLeAdvertiser != null && mAdvertiseCallback != null) {
                mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Scan BLE
     */
    public void startScanBle() {
        try {
            // Check
            if (mBluetoothLeScanner != null && mStatusScanBle == STATUS_SCAN_FINISH) {
                // set status
                mStatusScanBle = STATUS_SCAN_SETUP;

                // Log
                writeLog("startScanBle setup");

                // Callback khi scan bluetooth
                mScanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        // Status
                        mStatusScanBle = STATUS_SCANNING;

                        // log
                        // writeLog("startScanBle : onScanResult");

                        try {
                            // Check du lieu
                            if (result != null) {
                                // Value
                                String userId = "";
                                String platform = "";
                                int typeScan = 0;

                                // Check uuid
                                if (result.getScanRecord() != null && result.getScanRecord().getServiceUuids() != null) {
                                    // Lay ParcelUuid so sanh ios, thi lay phan ten
                                    if (result.getScanRecord().getServiceUuids().contains(AppUtils.BLE_UUID_IOS)) {
                                        // Convert name
                                        userId = AppUtils.convertUserId(result.getScanRecord().getDeviceName());

                                        // ios
                                        platform = "ios";
                                        typeScan = 1;

                                        // check
                                        if (TextUtils.isEmpty(userId)) {
                                            userId = result.getScanRecord().getDeviceName();
                                            typeScan = 2;
                                        }
                                    } else {
                                        // Lay data
                                        byte[] data = result.getScanRecord().getManufacturerSpecificData(AppConstants.BLE_ID);

                                        // Check
                                        if (data != null && data.length > 0) {
                                            // Lay ten tu Manufacturer
                                            userId = new String(data, Charset.forName("UTF-8"));
                                            typeScan = 3;
                                        }
                                    }
                                } else if (result.getDevice() != null) { // check manufactor
                                    // Get Address
                                    String addressMac = result.getDevice().getAddress();

                                    // check
                                    if (!TextUtils.isEmpty(addressMac)) {
                                        // check thong tin blid tu mac
                                        userId = CacheDatabaseHelper.getInstance(getApplicationContext()).getBlid(addressMac);

                                        // check
                                        if (!TextUtils.isEmpty(userId)) {
                                            // ios
                                            platform = "ios";
                                            typeScan = 1;
                                        } else {
                                            // Add list
                                            if (!mMacConnectList.contains(addressMac)) {
                                                mScanResultList.add(result);
                                                mMacConnectList.add(addressMac);
                                            }

                                            // Check
                                            if (mScanResultList.size() > 0) {
                                                // Check running
                                                if (mConnectTask == null) {
                                                    mConnectTask = new ConnectTask();
                                                    mConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                                } else if (mConnectTask.getStatus() == AsyncTask.Status.FINISHED) {
                                                    mConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                                }
                                            }
                                        }
                                    }
                                }

                                // check
                                if (!TextUtils.isEmpty(userId)) {
                                    // Log
                                    // writeLog("startScanBle : data = " + userId);

                                    // Insert db
                                    AppDatabaseHelper.getInstance(getApplicationContext()).insertUserIdTrace(userId, result.getRssi());

                                    moduleManager.emit(userId, "", "", result.getRssi(), platform, typeScan);
                                }
                            }

//                            else {
//                                Log.e("Khanhvd", "other");
//                                byte[] data = result.getScanRecord().getManufacturerSpecificData(AppConstants.DEFAUT_MANUFACTOR_IOS);
//                                String log = "";
//                                // Check
//                                if (data != null && data.length > 0) {
//                                    // Lay ten tu Manufacturer
//                                    log = new String(data, Charset.forName("UTF-8"));
//                                }
//
//                                Log.e("Khanhvd", log);
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
                        super.onBatchScanResults(results);

                        // Status
                        mStatusScanBle = STATUS_SCANNING;

                        // Log
                        writeLog("startScanBle : onBatchScanResults");
                    }

                    @Override
                    public void onScanFailed(int errorCode) {
                        super.onScanFailed(errorCode);
                        // Status
                        mStatusScanBle = STATUS_SCAN_FINISH;

                        // Log
                        writeLog("startScanBle: fail : Code: " + errorCode);
                    }
                };

                // Build scan setting
                ScanSettings.Builder scanSettings = new ScanSettings.Builder();
                scanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

                // Build filter ios
                ScanFilter.Builder scanFilterIos = new ScanFilter.Builder();
                scanFilterIos.setServiceUuid(AppUtils.BLE_UUID_IOS);

                // Build filter ios manufactor
                ScanFilter.Builder scanFilterIosManu = new ScanFilter.Builder();
                scanFilterIosManu.setManufacturerData(AppConstants.DEFAUT_MANUFACTOR_IOS, AppConstants.DEFAUT_MANUFACTOR_BYTE_IOS);

                // Build filter ios manufactor
                ScanFilter.Builder scanFilterIosManuX = new ScanFilter.Builder();
                scanFilterIosManuX.setManufacturerData(AppConstants.DEFAUT_MANUFACTOR_IOS, AppConstants.DEFAUT_MANUFACTOR_BYTE_IOS_X);

                // Build filter Android
                ScanFilter.Builder scanFilterAndroid = new ScanFilter.Builder();
                scanFilterAndroid.setServiceUuid(AppUtils.BLE_UUID_ANDROID);

                // Add filter
                List<ScanFilter> listFilter = new ArrayList<>();
                listFilter.add(scanFilterIos.build());
                listFilter.add(scanFilterIosManu.build());
                listFilter.add(scanFilterIosManuX.build());
                listFilter.add(scanFilterAndroid.build());

                // Start scan
                mBluetoothLeScanner.startScan(listFilter, scanSettings.build(), mScanCallback);

                // Status
                mStatusScanBle = STATUS_SCANNING;

                // Đăt lịch stop
                callAlarmTimer(TYPE_SCHEDULER_SCAN_BLE_STOP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop scan ble
     */
    private void stopScanBle() {
        try {
            // Check scan va stop
            if (mBluetoothLeScanner != null && mScanCallback != null) {
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * khoi tao va scan normal bluetooth
     */
    public void scanDevicesBluetooth() {
        try {
            // Check
            if (mBluetoothAdapter != null && mStatusScanDevices == STATUS_SCAN_FINISH) {
                // Set status
                mStatusScanDevices = STATUS_SCAN_SETUP;

                // Log
                writeLog("scanDevicesBluetooth setup");

                // Dang ki receiver
                registerReceiverScanDevices();

                // bắt đầu quét.
                if (mBluetoothAdapter.startDiscovery()) {
                    // Set status
                    mStatusScanDevices = STATUS_SCANNING;

                    // Đăt lịch stop
                    callAlarmTimer(TYPE_SCHEDULER_SCAN_DEVICES_STOP);
                } else {
                    // Set status
                    mStatusScanDevices = STATUS_SCAN_FINISH;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Khoi tao receiver lang nghe su kien scan devices
     */
    private void registerReceiverScanDevices() {
        try {
            if (mScanDevicesReceiver == null) {
                // intent filter
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

                // broadcast
                mScanDevicesReceiver = new BluetoothScanBroadCast();
                registerReceiver(mScanDevicesReceiver, intentFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Top receiver
     */
    private void unregisterReceiverScanDevices() {
        try {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.cancelDiscovery();
            }

            if (mScanDevicesReceiver != null) {
                unregisterReceiver(mScanDevicesReceiver);
                mScanDevicesReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcast để nhận các bluetooth bình thường đã quét đc thiết bị điện thoại và các thiết bị ngoại vi, ...
     */
    class BluetoothScanBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                // nếu tìm thấy
                if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {

                    // lấy dữ liệu
                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                    // Lay ten
                    String name = bluetoothDevice.getName();
                    String address = bluetoothDevice.getAddress();
                    String userIdRN = "";
                    String nameRN = "";
                    String platform = "";
                    int type = 0;

                    // Check neu la LE
                    if (!TextUtils.isEmpty(name) && bluetoothDevice.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                        // Convert
                        String userId = AppUtils.convertUserId(name);

                        // check
                        if(!TextUtils.isEmpty(userId)) {
                            // Log
                            writeLog("Devices - BLE: " + userId + " - RSSI: " + rssi);

                            AppDatabaseHelper.getInstance(getApplicationContext()).insertUserIdTrace(userId, rssi);

                            address = "";
                            userIdRN = userId;
                            platform = "ios";
                            type = 4;
                        }

                    } else if (bluetoothDevice.getType() == BluetoothDevice.DEVICE_TYPE_CLASSIC && !TextUtils.isEmpty(address)) {
                        // Check rong
                        if (TextUtils.isEmpty(name)) {
                            // Log
                            writeLog("Devices - Device - Noname: " + address  + " - RSSI: " + rssi);

                            // Gi DB
                            AppDatabaseHelper.getInstance(getApplicationContext()).insertMacIdTrace(address, "", rssi);
                            nameRN = "No name";
                            type = 5;
                        } else {
                            // Log
                            writeLog("Devices - Device: " + name + " : " + address  + " - RSSI: " + rssi);

                            // Ghi DB
                            AppDatabaseHelper.getInstance(getApplicationContext()).insertMacIdTrace(address, name, rssi);
                            nameRN = name;
                            type = 6;
                        }
                    }

                    // check
                    if (!TextUtils.isEmpty(nameRN) || !TextUtils.isEmpty(userIdRN)) {
                        // React native
                        moduleManager.emitBlueTooth(userIdRN, nameRN, address, rssi, platform, type);
                    }
                }

                // Check su kien ket thuc
                if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    // Log
                    writeLog("Stop Discovery and restart discovery");

                    // Start
                    // bắt đầu quét.
                    if (!mBluetoothAdapter.startDiscovery()) {

                        // Set bien
                        mStatusScanDevices = STATUS_SCAN_FINISH;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Khoi tao receiver lang nghe su kien stop service
     */
    private void registerReceiverStopService() {
        try {
            // Check
            if (mReceiverBluetoothChanged == null) {
                mReceiverBluetoothChanged = new BluetoothChangedReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ACTION_RECEIVER_STOP);
                intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                registerReceiver(mReceiverBluetoothChanged, intentFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Top receiver
     */
    private void unregisterReceiverStopService() {
        try {
            // Check
            if (mReceiverBluetoothChanged != null) {
                unregisterReceiver(mReceiverBluetoothChanged);
                mReceiverBluetoothChanged = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Class
    class BluetoothChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // check
            if (intent != null) {
                // Action
                String action = intent.getAction();

                // Check
                if (!TextUtils.isEmpty(action)) {
                    //
                    if (action.equals(ACTION_RECEIVER_STOP)) {
                        stopSelf();
                    } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        switch(state) {
                            case BluetoothAdapter.STATE_OFF:
                                // Log
                                writeLog("Bluetooth : OFF");

                                // Timer enable bluetooth
                                callAlarmTimer(TYPE_SCHEDULER_ENABLE_BLUETOOTH);
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                // Log
                                writeLog("Bluetooth : OFF ing");
                                // Stop tat ca
                                // init scan
                                initStatus();

                                // Stop
                                stopBluetoothFeature();
                                break;
                            case BluetoothAdapter.STATE_ON:
                                // Log
                                writeLog("Bluetooth : ON");

                                // init bluetooth
                                initBluetooth();

                                // Start service
                                startAll();
                                break;
                            case BluetoothAdapter.STATE_TURNING_ON:
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * THực hiện viẹc ghi log
     * @param log
     */
    private void writeLog(String log) {
        // Log
        Log.e("Bluezone", log);
        // Check
        if (isConfigLog) {
            AppUtils.writeLog(getApplicationContext(), log);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // log
        writeLog("stopService");

        // Stop all
        stopAllScan();
    }

    /**
     * Stop all
     */
    private void stopAllScan() {
        // Reset flag
        initStatus();

        // Stop
        stopBluetoothFeature();

        // Huy receiver stop services
        unregisterReceiverStopService();

        // Lang nghe Baterry
        unregisterReceiverBattery();
    }

    /**
     * Stop nhung cai dat lien quan den bluetooth
     */
    private void stopBluetoothFeature() {
        // Check phat va stop
        stopBroadcastBle();

        // Check scan va stop
        stopScanBle();

        // Huỷ receiver scan devices
        unregisterReceiverScanDevices();
    }

    /**
     * Khoi tao receiver lang nghe su kien stop service
     */
    private void registerReceiverBattery() {
        try {
            // Check
            if (isConfigLogBattery && mReceiverBattery == null) {
                mReceiverBattery = new BatteryReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
                registerReceiver(mReceiverBattery, intentFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Top receiver
     */
    private void unregisterReceiverBattery() {
        try {
            if (mReceiverBattery != null) {
                unregisterReceiver(mReceiverBattery);
                mReceiverBattery = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Class
    class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // check
            if (intent != null && isConfigLogBattery) {
                // Get battery level
                int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

                // Bat Bluettooth
                // AppUtils.enableBluetooth(getApplicationContext(), batteryLevel);

                // ghi log pin
                if (isConfigLogBattery) {
                    AppUtils.writeLogBattery(context, AppUtils.getDateCurrent() + " level : " + batteryLevel + "% ");
                }
            }
        }
    }

    /**
     * Asynctask connect
     */
    class ConnectTask extends AsyncTask {
        // RSSI connect
        private int mRssiConnect;

        // Flag connect
        private boolean mIsConnect = false;

        @Override
        protected Object doInBackground(Object[] objects) {
            // flad
            mIsConnect = false;

            // Check
            while (mScanResultList.size() > 0) {
                // Check connect
                mIsConnect = true;

                // Time
                long now = System.currentTimeMillis();

                // Connect
                mBluetoothGatt = mScanResultList.get(0).getDevice().connectGatt(getApplicationContext(), false,
                        new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        // Check status
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            // Bao cho ham ServicesDiscovered
                            gatt.discoverServices();
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                            // Log
                            writeLog("Disconnected from GATT server.");
                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        super.onServicesDiscovered(gatt, status);

                        // Check status
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            // Read gattservice
                            getGattService(gatt.getServices());
                        } else {
                            // Log
                            writeLog("onServicesDiscovered received: " + status);
                        }
                    }

                    @Override
                    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        super.onCharacteristicRead(gatt, characteristic, status);
                        try {
                            // Read Blid
                            String blid = AppUtils.convertUserId(readValuesCharactic(characteristic));

                            // check
                            if (!TextUtils.isEmpty(blid)) {

                                // Insert db
                                AppDatabaseHelper.getInstance(getApplicationContext()).insertUserIdTrace(blid, -70);

                                // Ghi ban len he thong
                                moduleManager.emit(blid, "", "", -70, "ios", 1);

                                // insert cache
                                CacheDatabaseHelper.getInstance(getApplicationContext()).insertConnected(blid, gatt.getDevice().getAddress());

                                // out
                                mIsConnect = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                        super.onReadRemoteRssi(gatt, rssi, status);
                        // lay rssi
                        mRssiConnect = rssi;
                    }
                });

                // Timeout connect
                while ((System.currentTimeMillis() - now) < AppConstants.TIMEOUT_CONNECT && mIsConnect && mBluetoothGatt != null) {
                    SystemClock.sleep(1000);
                }

                // Remove connect
                mScanResultList.remove(0);
                mMacConnectList.remove(0);

                // close connect
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.close();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

        /**
         * dannvb: Lay list gattService
         * @param gattServices
         */
        private void getGattService(List<BluetoothGattService> gattServices) {
            // Check init
            if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                writeLog("BluetoothAdapter not initialized");
                return;
            }

            // Check init
            if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                writeLog("BluetoothAdapter not initialized");
                return;
            }

            // Loops GATT Services.
            for (BluetoothGattService gattService : gattServices) {

                // Get uuid
                String uuid = gattService.getUuid().toString();

                // Check uuid cua ios
                if (!TextUtils.isEmpty(uuid) && uuid.equals(AppConstants.BLE_UUID_IOS.toLowerCase())) {
                    // Get Characteristics info
                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

                    // for
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        // Get uuid cua characteristic
                        uuid = gattCharacteristic.getUuid().toString();

                        // Check
                        if (!TextUtils.isEmpty(uuid) && uuid.equals(AppConstants.BLE_UUID_CHARECTIC.toLowerCase())) {
//                        // Check service
//                        BluetoothGattService mCustomService = mBluetoothGatt.getService(UUID.fromString(AppConstants.BLE_UUID_IOS));
//
//                        // Check service BLE
//                        if (mCustomService == null){
//                            writeLog("Custom BLE Service not found");
//                            return;
//                        }
                            try {
                                // Read characteris
                                mBluetoothGatt.readCharacteristic(gattCharacteristic);

//                                // Read Blid
//                                String blid = AppUtils.convertUserId(readValuesCharactic(gattCharacteristic));
//
//                                // check
//                                if (!TextUtils.isEmpty(blid)) {
//
//                                    // Insert db
//                                    AppDatabaseHelper.getInstance(getApplicationContext()).insertUserIdTrace(blid, mRssiConnect);
//
//                                    // Ghi ban len he thong
//                                    moduleManager.emit(blid, "", "", mRssiConnect, "ios", 1);
//
//                                    // insert cache
//                                    // CacheDatabaseHelper.getInstance(getApplicationContext()).insertConnected(blid, gattService.get);
//
//                                    // out
//                                    mIsConnect = false;
//                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        /**
         * Read values characteristic
         * @param characteristic
         * @return
         */
        public String readValuesCharactic(BluetoothGattCharacteristic characteristic) {
            // Read and convert.
            final byte[] data = characteristic.getValue();

            // Check
            if (data != null && data.length > 0) {
                //
//                final StringBuilder stringBuilder = new StringBuilder(data.length);
//
//                // For
//                for (byte byteChar : data) {
//                    stringBuilder.append(String.format("%02X ", byteChar));
//                }
//            String s =  new String(data) + "\n" + stringBuilder.toString();
//            Log.d("dannvb read values", s);
                return new String(data);
            }

            return null;
        }
    }
}
