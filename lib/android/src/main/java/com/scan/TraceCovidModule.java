package com.scan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.scan.bluezoneid.BluezoneIdTrace;
import com.scan.bluezoneid.BluezoneIdUtils;
import com.scan.preference.AppPreferenceManager;

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
    public void checkContactF(String data, Promise promise) {
        try {
            promise.resolve(BluezoneIdTrace.isContactF(reactContext, data));
        }  catch (IllegalViewOperationException e) {
            promise.reject("Error", "An error occurred");
        }
    }

    @ReactMethod
    public void getBluezoneIdInfo(int dayStartTrace, Promise promise) {
        try {
            String uri = BluezoneIdTrace.getBluezoneIdInfo(reactContext, dayStartTrace);
            promise.resolve(uri);
        } catch (IllegalViewOperationException e) {
            promise.reject("Error", "An error occurred");
        }
    }

    @ReactMethod
    public void writeHistoryContact(int dayStartTrace, Promise promise) {
        try {
            String uri = BluezoneIdTrace.exportTraceData(reactContext, dayStartTrace);
            promise.resolve(uri);
        } catch (IllegalViewOperationException e) {
            promise.reject("Error", "An error occurred");
        }
    }

    @ReactMethod
    public void getBluezoneId(Promise promise) {
        String bzId = BluezoneIdUtils.getHexBluezoneId(reactContext);
        promise.resolve(bzId);
    }

    @ReactMethod
    public void setContentNotify(String title, String content) {
        this.manager.setContentNotify(title, content);
    }

    public void emitEvent(String eventName, @Nullable Object data) {
        sendEvent(reactContext, eventName, data);
    }

    private void sendEvent(ReactContext reactContext, String eventName, @Nullable Object data) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, data);
    }
}
