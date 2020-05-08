package com.scan;

import android.os.Build;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.scan.backup.BackupUtils;
import com.scan.preference.AppPreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;

public class TraceCovidModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    private Callback onScanResult;
    private TraceCovidModuleManager manager;
    @NonNull
    @Override
    public String getName() {
        return "TraceCovid";
    }

    public TraceCovidModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
        ServiceTraceCovid.reactContext = context;
        this.manager = new TraceCovidModuleManager(context, this);
    }

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
        AppPreferenceManager.getInstance(reactContext).setBlid(id);
    }

    @ReactMethod
    public void restoreDb() {
        // Restore DB tu External storage
        BackupUtils.restoreDatabaseFromExternalStorage(reactContext);
    }

    @ReactMethod
    public void checkContact(ReadableArray ids, Promise promise) {
        try {
            int length = 0;
            if(ids != null) {
                length = ids.size();
            } else {
                promise.reject("Error", "Don't find id");
            }
            if(length > 2) {
                promise.resolve(true);
            } else {
                promise.resolve(false);
            }
        }  catch (IllegalViewOperationException e) {
            promise.reject("Error", "An error occurred");
        }
    }

    @ReactMethod
    public void writeHistoryContact(ReadableArray ids, Promise promise) {
        try {
            String arrId[];
            int length = 0;
            if(ids != null) {
                length = ids.size();
                arrId = new String[length];
            } else {
                arrId = null;
            }
            for(int i = 0; i < length; i++) {
                arrId[i] = ids.getString(i);
            }

            String uri = BackupUtils.backupFileData(reactContext, arrId);
            promise.resolve(uri);
        } catch (IllegalViewOperationException e) {
            promise.reject("Error", "An error occurred");
        }
    }

    @ReactMethod
    public void generatorBluezoneId(Promise promise) {
        String bluezoneId = BluezonerIdGenerator.createBluezonerId(6);
        promise.resolve(bluezoneId);
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
