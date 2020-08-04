package com.scan.bluezoneid;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.text.TextUtils;

import com.scan.AppUtils;
import com.scan.database.AppDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Class Trace Fx
 * @author khanhxu
 */
public class BluezoneIdTrace {

    /**
     * Get Path Data
     * @param context
     * @return
     */
    public static File getPathTraceData(Context context, String nameFile) {
        return new File(Environment.getDataDirectory() + "/data/" +
                context.getPackageName(), nameFile);
    }

    /**
     * Get Bluezoner Info
     * @param context
     * @param dayStartTrace
     * @return
     */
    public static String getBluezoneIdInfo(Context context, int dayStartTrace) {
        String ret = "";
        BluezoneDailyKey bluezoneDailyKey = BluezoneIdGenerator.getInstance(context).getBluezoneBaseId();
        if (bluezoneDailyKey != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(BluezoneIdConstants.TraceInfo.JSON_BLUEZONE_BASE_ID, AppUtils.convertBytesToHex(bluezoneDailyKey.first));
                jsonObject.put(BluezoneIdConstants.TraceInfo.JSON_BLUEZONE_BASE_ID_TIME, bluezoneDailyKey.second.getTimeStart() / 1000);

                // get info day trace
                BluezoneDailyKey bluezoneDailyKeyDx = BluezoneIdGenerator.getInstance(context).createBluezoneDailyKey(dayStartTrace);
                if (bluezoneDailyKeyDx != null) {
                    jsonObject.put(BluezoneIdConstants.TraceInfo.JSON_F0_DAILY_KEY, AppUtils.convertBytesToHex(bluezoneDailyKeyDx.first));
                    jsonObject.put(BluezoneIdConstants.TraceInfo.JSON_F0_TIME_DK, bluezoneDailyKeyDx.second.getTimeStart() / 1000);
                }

                ret = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    /**
     * Export Data
     * @param context
     * @return
     */
    public static String exportTraceData(Context context) {
        String ret = "";

        File fileTrace = getPathTraceData(context, BluezoneIdConstants.TraceInfo.FILE_NAME_TRACE_DATA);
        if (fileTrace != null) {
            // delete old
            if (fileTrace.exists()) {
                fileTrace.delete();
            }

            RandomAccessFile fi = null;

            try {
                // create file
                if (fileTrace.createNewFile()) {
                    fi = new RandomAccessFile(fileTrace.getAbsolutePath(), "rw");

                    // Get all cursor
                    Cursor cursor = AppDatabaseHelper.getInstance(context).getCursorData();
                    if (cursor != null) {
                        // Read
                        while (cursor.moveToNext()) {
                            try {
                                String retItem = AppUtils.convertBytesToHex(cursor.getBlob(AppDatabaseHelper.COLUMN_INDEX_BLID)) + "\t" +
                                        AppUtils.convertBytesToHex(cursor.getBlob(AppDatabaseHelper.COLUMN_INDEX_BLID_CONTACT)) + "\t" +
                                        cursor.getInt(AppDatabaseHelper.COLUMN_INDEX_RSSI) + "\t" +
                                        cursor.getLong(AppDatabaseHelper.COLUMN_INDEX_TIME) + "\n";

                                fi.write(retItem.getBytes());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        ret = fileTrace.getAbsolutePath();
                        cursor.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fi != null) {
                    try {
                        fi.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Export Data
     * @param context
     * @param dayStartTrace
     * @return
     */
    public static String exportTraceData(Context context, int dayStartTrace) {
        String ret = "";
        File fileTrace = getPathTraceData(context, BluezoneIdConstants.TraceInfo.FILE_NAME_TRACE_DATA);

        if (fileTrace != null) {
            // delete old
            if (fileTrace.exists()) {
                fileTrace.delete();
            }

            RandomAccessFile fi = null;

            try {
                // create file
                if (fileTrace.createNewFile()) {
                    fi = new RandomAccessFile(fileTrace.getAbsolutePath(), "rw");

                    // Call Time
                    long timeEnd = System.currentTimeMillis() - (dayStartTrace * BluezoneIdConstants.DAY_MILLISECONDS);

                    // Get all cursor
                    Cursor cursor = AppDatabaseHelper.getInstance(context).getCursorData(timeEnd);
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            try {
                                String retItem = AppUtils.convertBytesToHex(cursor.getBlob(AppDatabaseHelper.COLUMN_INDEX_BLID)) + "\t" +
                                        AppUtils.convertBytesToHex(cursor.getBlob(AppDatabaseHelper.COLUMN_INDEX_BLID_CONTACT)) + "\t" +
                                        cursor.getInt(AppDatabaseHelper.COLUMN_INDEX_RSSI) + "\t" +
                                        cursor.getLong(AppDatabaseHelper.COLUMN_INDEX_TIME) + "\n";

                                fi.write(retItem.getBytes());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        ret = fileTrace.getAbsolutePath();
                        cursor.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fi != null) {
                    try {
                        fi.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Check Contact F
     * @param context
     * @param dataF0
     * @return
     */
    public static boolean isContactF(Context context, String dataF0) {
        boolean ret = false;
        if (!TextUtils.isEmpty(dataF0)) {
            try {
                JSONObject jsonDataF0 = new JSONObject(dataF0);
                JSONArray jsonArray = jsonDataF0.getJSONArray(BluezoneIdConstants.TraceInfo.JSON_F0_DATA);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String dailyKey = jsonObject.getString(BluezoneIdConstants.TraceInfo.JSON_F0_DAILY_KEY);
                    long timeDk = jsonObject.getLong(BluezoneIdConstants.TraceInfo.JSON_F0_TIME_DK);
                    int maxRoll = jsonObject.getInt(BluezoneIdConstants.TraceInfo.JSON_F0_MAX_ROLL);
                    long timeTe = jsonObject.getLong(BluezoneIdConstants.TraceInfo.JSON_F0_TIME_END);

                    // Check trace
                    if (checkContactF(context, dailyKey, timeDk, maxRoll, timeTe)) {
                        ret = true;
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    /**
     * Get Trace list
     * @param context
     * @param dataF0
     * @return
     */
        public static ArrayList<String> getTraceF(Context context, String dataF0) {
        ArrayList<String> ret = new ArrayList<>();
        // Phân tích Data F0
        if (!TextUtils.isEmpty(dataF0)) {
            try {
                JSONObject jsonDataF0 = new JSONObject(dataF0);
                JSONArray jsonArray = jsonDataF0.getJSONArray(BluezoneIdConstants.TraceInfo.JSON_F0_DATA);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String dailyKey = jsonObject.getString(BluezoneIdConstants.TraceInfo.JSON_F0_DAILY_KEY);
                    long timeDk = jsonObject.getLong(BluezoneIdConstants.TraceInfo.JSON_F0_TIME_DK);
                    int maxRoll = jsonObject.getInt(BluezoneIdConstants.TraceInfo.JSON_F0_MAX_ROLL);
                    long timeTe = jsonObject.getLong(BluezoneIdConstants.TraceInfo.JSON_F0_TIME_END);

                    // Check trace
                    if (checkContactF(context, dailyKey, timeDk, maxRoll, timeTe)) {
                        ret.add(dailyKey);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    /**
     * Check Contact
     * @param context
     * @param bluezoneDailyKey DaiLy Key
     * @param timeDk Thời gian tạo
     * @param max Số lần rolling
     * @param timeTe Thời gian kết thúc truy vết
     * @return
     */
    public static boolean checkContactF(Context context, String bluezoneDailyKey, long timeDk, int max, long timeTe) {
        boolean ret = false;
        timeDk = timeDk * 1000;
        timeTe = timeTe * 1000;

        // Convert Bytes -> hex
        byte[] bluezoneDailyKeyk = AppUtils.convertHexToBytes(bluezoneDailyKey);
        if (bluezoneDailyKeyk.length == BluezoneIdConstants.Config.LENGTH_DAILY_KEY) {
            //  Create first SubKey
            byte[] dataCreateFirstSubKey = bluezoneDailyKeyk;
            byte[] bluezoneSubKey = BluezoneIdUtils.addByteArrays(dataCreateFirstSubKey, BluezoneIdConstants.Config.SALT_SUB_KEY_DAILY);
            long timeNext = timeDk;
            long delta = BluezoneIdConstants.DAY_MILLISECONDS / max;
            int i = 0;

            // Find Fx
            while (timeNext <= timeTe) {
                bluezoneSubKey = BluezoneIdUtils.sha256(bluezoneSubKey);
                byte[] bluezoneId = BluezoneIdGenerator.convertBluezoneSubKeyToBluezoneId(bluezoneSubKey);
                long timeStart = timeNext;
                timeNext = timeNext + delta;
                long timeEnd = timeNext;

                // Check
                if (AppDatabaseHelper.getInstance(context).isTrace(bluezoneId, timeStart, timeEnd)) {
                    ret = true;
                    break;
                }

                i++;

                // Check nextday and create Subkey
                if (i == max) {
                    dataCreateFirstSubKey = BluezoneIdUtils.sha256(dataCreateFirstSubKey);
                    bluezoneSubKey = BluezoneIdUtils.addByteArrays(dataCreateFirstSubKey, BluezoneIdConstants.Config.SALT_SUB_KEY_DAILY);
                    i = 0;
                }
            }
        }

        return ret;
    }
}
