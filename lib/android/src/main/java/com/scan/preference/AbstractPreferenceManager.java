package com.scan.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Set;

public abstract class AbstractPreferenceManager {

    /**
     * The SharePreferences
     */
    private final SharedPreferences mPref;

    /**
     * Ham khoi tao
     *
     * @param context
     */
    public AbstractPreferenceManager(Context context) {
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Cac ham put - luu tru du lieu theo key value
    public void putInt(String KEY, int value) {
        mPref.edit().putInt(KEY, value).commit();
    }

    public void putLong(String KEY, long value) {
        mPref.edit().putLong(KEY, value).commit();
    }

    public void putFloat(String KEY, float value) {
        mPref.edit().putFloat(KEY, value).commit();
    }

    public void putBoolean(String KEY, boolean value) {
        mPref.edit().putBoolean(KEY, value).commit();
    }

    public void putString(String KEY, String value) {
        mPref.edit().putString(KEY, value).commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void putStringSet(String KEY, Set<String> value) {
        mPref.edit().putStringSet(KEY, value).commit();
    }

    // cac ham get, lay du lieu voi gia tri mac dinh
    public int getInt(String KEY, int defaultVlue) {
        return mPref.getInt(KEY, defaultVlue);
    }

    public long getLong(String KEY, long defaultVlue) {
        return mPref.getLong(KEY, defaultVlue);
    }

    public float getFloat(String KEY, float defaultVlue) {
        return mPref.getFloat(KEY, defaultVlue);
    }

    public boolean getBoolean(String KEY, boolean defaultVlue) {
        return mPref.getBoolean(KEY, defaultVlue);
    }

    public String getString(String KEY, String defaultVlue) {
        return mPref.getString(KEY, defaultVlue);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(String KEY, Set<String> defaultVlue) {
        return mPref.getStringSet(KEY, defaultVlue);
    }

    public void remove(String key) {
        mPref.edit().remove(key).apply();
    }

    public boolean clear() {
        return mPref.edit().clear().commit();
    }

    public boolean contain(String key) {
        return mPref.contains(key);
    }
}
