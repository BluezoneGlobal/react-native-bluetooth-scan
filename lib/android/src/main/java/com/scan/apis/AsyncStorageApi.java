package com.scan.apis;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.storage.AsyncLocalStorageUtil;
import com.facebook.react.modules.storage.ReactDatabaseSupplier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class AsyncStorageApi {
    private ReactApplicationContext mContext;
    private final String TABLE_CATALYST = "catalystLocalStorage";
    private final String KEY_COLUMN = "key";
    private final String VALUE_COLUMN = "value";

    public static JSONObject convertStringToJsonObject(String str) {
        if(str != null && str instanceof String) {
            try {
                return new JSONObject(str);
            } catch (Exception e) {
                return null;
            }
        }
        // TODO by Me: Exception?
        return null;
    }

    public static WritableArray convertJsonToArray(JSONArray jsonArray) throws JSONException {
        WritableArray array = new WritableNativeArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                array.pushMap(convertJsonToMap((JSONObject) value));
            } else if (value instanceof  JSONArray) {
                array.pushArray(convertJsonToArray((JSONArray) value));
            } else if (value instanceof  Boolean) {
                array.pushBoolean((Boolean) value);
            } else if (value instanceof  Integer) {
                array.pushInt((Integer) value);
            } else if (value instanceof  Double) {
                array.pushDouble((Double) value);
            } else if (value instanceof String)  {
                array.pushString((String) value);
            } else {
                array.pushString(value.toString());
            }
        }
        return array;
    }

    public static WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {
        WritableMap map = new WritableNativeMap();

        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                map.putMap(key, convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                map.putArray(key, convertJsonToArray((JSONArray) value));
            } else if (value instanceof Boolean) {
                map.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                map.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                map.putDouble(key, (Double) value);
            } else if (value instanceof String) {
                map.putString(key, (String) value);
            } else {
                map.putString(key, value.toString());
            }
        }
        return map;
    }

    public AsyncStorageApi(ReactApplicationContext _mContext) {
        mContext = _mContext;
    }

    public String getItem(String keyValue) throws JSONException {
        SQLiteDatabase db = ReactDatabaseSupplier.getInstance(mContext).getReadableDatabase();
        if (db == null) {
            return null;
        }
        return AsyncLocalStorageUtil.getItemImpl(db, keyValue);
    }

    public void setItem(String key, String value) throws JSONException {
        SQLiteDatabase db = ReactDatabaseSupplier.getInstance(mContext).getReadableDatabase();
        if (db == null) {
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_COLUMN, key);
        contentValues.put(VALUE_COLUMN, value);

        db.insertWithOnConflict(
            TABLE_CATALYST,
            null,
            contentValues,
            SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public void saveObject(JSONObject jsonObject, String stateKey) throws JSONException {
        String states = getItem(stateKey);
        JSONObject data = convertStringToJsonObject(states);
        if (data != null) {
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject jsonNew = jsonObject.getJSONObject(key);

                data.put(key, jsonNew);
            }
            setItem(stateKey, data.toString());
        }

    }

    public void saveEdges(JSONObject jsonObject, String stateKey) throws JSONException {
        String states = getItem(stateKey);
        JSONObject data = convertStringToJsonObject(states);
        System.out.println(data);
        if(data != null) {
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONObject jsonNew = data.getJSONObject(key);
                JSONArray jsonArrayNew = jsonObject.getJSONObject(key).getJSONArray("itemIds");
                String maxScore = jsonObject.getJSONObject(key).getString("maxScore");
                JSONArray jsonArrayOld = data.getJSONObject(key).getJSONArray("itemIds");
                for (int i = jsonArrayNew.length() - 1; i >=0; i--) {
                    jsonArrayOld.put(0, jsonArrayNew.get(i));
                }
                data.getJSONObject(key).put("minScore", maxScore);
            }
            setItem(stateKey, data.toString());
        }

        System.out.println(data);
    }

    public void saveEdges(JSONObject jsonObject, String stateKey, String key1) throws JSONException {
        String states = getItem(stateKey);
        JSONObject data = convertStringToJsonObject(states);
        System.out.println(data);
        if(data != null) {
            Iterator<String> keys = data.keys();
            JSONArray jsonArrayNew = jsonObject.getJSONObject(key1).getJSONArray("itemIds");
            String maxScore = jsonObject.getJSONObject(key1).getString("maxScore");
            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONArray jsonArrayOld = data.getJSONObject(key).getJSONArray("itemIds");
                for (int i = jsonArrayNew.length() - 1; i >=0; i--) {
                    jsonArrayOld.put(0, jsonArrayNew.get(i));
                }
                data.getJSONObject(key).put("minScore", maxScore);
            }
            setItem(stateKey, data.toString());
        }

        System.out.println(data);
    }
}

