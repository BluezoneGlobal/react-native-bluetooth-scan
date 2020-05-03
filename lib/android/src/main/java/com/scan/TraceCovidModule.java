package com.scan;

import android.os.Build;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.scan.backup.BackupUtils;
import com.scan.preference.AppPreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class TraceCovidModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    private Callback onScanResult;
    private TraceCovidModuleManager manager;
    @NonNull
    @Override
    public String getName() {
        return "TraceCovid";
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TraceCovidModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
        ServiceTraceCovid.reactContext = context;
        this.manager = new TraceCovidModuleManager(context, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @ReactMethod
    public void startService(boolean scanFull) throws JSONException {
        this.manager.startService(scanFull);
    }

//    @ReactMethod
//    public void setConfig(ReadableMap configs, Callback callback) {
//        try {
//            this.manager.setConfig(configs);
//            callback.invoke(true);
//        } catch (Exception e) {
//            callback.invoke(false);
//        }
//    }

    @ReactMethod
    public void setConfig(ReadableMap configs) {
        this.manager.setConfig(configs);
    }

//    @ReactMethod
//    public void getConfig(Promise promise) {
//        promise.resolve(this.manager.getConfig());
//    }

//    @ReactMethod
//    public void stopService() {
//        this.manager.stopService();
//    }

//    @ReactMethod
//    public void setId(String id, Callback callback) {
//        try {
//            AppPreferenceManager.getInstance(reactContext).setPhoneNumber(id);
//            callback.invoke(true);
//        } catch (Exception e) {
//            callback.invoke(false);
//        }
//    }
    @ReactMethod
    public void setLanguage(String language) {
        this.manager.setLanguage(language);
    }

    @ReactMethod
    public void setId(String id) {
        AppPreferenceManager.getInstance(reactContext).setPhoneNumber(id);
    }

    @ReactMethod
    public void restoreDb() {
        // Restore DB tu External storage
        BackupUtils.restoreDatabaseFromExternalStorage(reactContext);
    }

    @ReactMethod
    public void generatorBluezoneId(Promise promise) {
        String bluezoneId = BluezonerIdGenerator.createBluezonerId(6);
        promise.resolve(bluezoneId);
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("NOTIFY_SERVICE_NUMBER", 114);
        return constants;
    }

    public void emitEvent(String eventName, WritableMap params) {
        sendEvent(reactContext, eventName, params);
    }

    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}
