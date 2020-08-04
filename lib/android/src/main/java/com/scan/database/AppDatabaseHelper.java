package com.scan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.scan.AppConstants;
import com.scan.AppUtils;
import com.scan.bluezoneid.BluezoneIdGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author khanhxu
 */
public class AppDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_db_2.db";
    public static final int DATABASE_VERSION = 1;

    // Table trace
    public static final String TABLE_NAME = "trace_info";
    public static final String COLUMN_BLID = "blid"; // User Code
    public static final String COLUMN_BLID_CONTACT = "blid_contact"; // Bluezone Id scan
    public static final String COLUMN_MAC_ID = "macid"; // MacId scan
    public static final String COLUMN_RSSI = "rssi";
    public static final String COLUMN_TX_POWER = "tx_power";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_TIME = "timestamp";

    // Index
    public static final int COLUMN_INDEX_BLID = 1;
    public static final int COLUMN_INDEX_BLID_CONTACT  = 2;
    public static final int COLUMN_INDEX_MAC_ID = 3;
    public static final int COLUMN_INDEX_RSSI = 4;
    public static final int COLUMN_INDEX_TX_POWER = 5;
    public static final int COLUMN_INDEX_STATE = 6;
    public static final int COLUMN_INDEX_TIME = 7;

    private static SQLiteDatabase mDatabase;
    private static Context mContext;

    // HashMap cache
    private static HashMap<String, Long> sCacheInfo = new HashMap<>();


    /**
     * @param context
     */
    private AppDatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static AppDatabaseHelper sHelper;

    public static AppDatabaseHelper getInstance(Context context) {
        mContext = context;

        // check
        if (sHelper == null) {
            sHelper = new AppDatabaseHelper(context);
        }

        return sHelper;
    }

    /**
     * Khởi tạo đối tượng mDatabase
     */
    private static void setDbInstance() {
        mDatabase = Holder.mInstance.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            // Create table
            String createSessionTable = "create table if not exists " +
                    TABLE_NAME + " (" + BaseColumns._ID + " integer primary key autoincrement,"
                    + COLUMN_BLID + " BLOB,"
                    + COLUMN_BLID_CONTACT + " BLOB,"
                    + COLUMN_MAC_ID + " text,"
                    + COLUMN_RSSI + " integer,"
                    + COLUMN_TX_POWER + " integer,"
                    + COLUMN_STATE + " integer,"
                    + COLUMN_TIME + " long);";
            sqLiteDatabase.execSQL(createSessionTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Mo DB
     * @return
     */
    public boolean openDatabase() {
        synchronized (this) {
            try {
                if ((mDatabase == null) || (!mDatabase.isOpen())) {
                    setDbInstance();
                }

                return true;
            } catch (Exception e) {

                return false;
            }
        }
    }

    /**
     * Add trace info
     * @param blidContact
     * @param rssi
     * @param txPower
     * @return
     */
    public long insertInfoTrace(byte[] blidContact, int rssi, int txPower) {
        long ret = -1;

        // Check
        if (blidContact != null && isInsert(AppUtils.convertBytesToHex(blidContact))) {

            // Open
            openDatabase();

            try {
                long time = System.currentTimeMillis();

                // Data insert
                final ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_BLID, BluezoneIdGenerator.getInstance(mContext).getBluezoneId());
                contentValues.put(COLUMN_BLID_CONTACT, blidContact);
                contentValues.put(COLUMN_RSSI, rssi);
                contentValues.put(COLUMN_TX_POWER, txPower);
                contentValues.put(COLUMN_TIME, time);

                // insert
                ret = mDatabase.insertOrThrow(TABLE_NAME, null, contentValues);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Insert DB ngoai
            // BackupUtils.insertExternalDatabase(mContext, userId, rssi);

            // Check backup
            // BackupUtils.backupDatabase(mContext);
        }

        return ret;
    }

    /**
     * Them trace info
     * @param macId
     * @param rssi
     * @param txPower
     * @return
     */
    public long insertMacIdTrace(String macId, int rssi, int txPower) {
        long ret = -1;

        // Check
        if (isInsert(macId)) {

            // Open
            openDatabase();

            try {
                long time = System.currentTimeMillis();

                // Data insert
                final ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_BLID, BluezoneIdGenerator.getInstance(mContext).getBluezoneId());
                contentValues.put(COLUMN_TIME, time);
                contentValues.put(COLUMN_MAC_ID, macId);
                contentValues.put(COLUMN_RSSI, rssi);
                contentValues.put(COLUMN_TX_POWER, txPower);

                // insert
                ret = mDatabase.insertOrThrow(TABLE_NAME, null, contentValues);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Insert DB ngoai
            // BackupUtils.insertExternalDatabase(mContext, macId, devices, rssi);

            // Check backup
            // BackupUtils.backupDatabase(mContext);
        }

        return ret;
    }

    /**
     * Is Expried
     * @param info
     */
    private static boolean isInsert(String info) {
        boolean ret = true;
        try {
            // Check
            if (!TextUtils.isEmpty(info)) {
                // Get current time
                long time = System.currentTimeMillis();

                // Check mapp
                Set<String> setRemove = new HashSet<>();

                // Loop
                for (Map.Entry<String, Long> entry : sCacheInfo.entrySet()) {
                    // Check time
                    if ((time - entry.getValue() >= AppConstants.Config.TIME_DELAY_INSERT)) {

                        // List remove
                        setRemove.add(entry.getKey());
                    } else if (info.equals(entry.getKey())) {
                        // Kho them vao
                        ret = false;
                    }
                }

                // Remove time >
                if (setRemove.size() > 0) {
                    sCacheInfo.keySet().removeAll(setRemove);
                }

                // check insert
                if (ret) {
                    // Them vao
                    sCacheInfo.put(info, time);
                }
            } else {
                ret = false;
            }
        } catch (Exception e) {
            e.printStackTrace();

            ret = false;
        }

        return ret;
    }

    /**
     * Đếm số lượng bản ghi
     *
     * @return số lượng thôi ^^
     */
    public int countItems() {
        Cursor cursor = null;

        // Open
        openDatabase();

        int ret = 0;
        try {
            cursor = mDatabase.rawQuery("select count(*) from " + TABLE_NAME, null);
            cursor.moveToFirst();
            ret = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return ret;
    }

    /**
     * Get all data
     * @param listBlid
     * @return
     */
    public Cursor getCursorData(String[] listBlid) {
        // ret
        Cursor cursor = null;

        // open
        openDatabase();

        // Check
        if (listBlid == null || listBlid.length == 0) {
            cursor = mDatabase.query(TABLE_NAME, null, null,
                    null, null, null, null, null);
        } else {
            cursor = mDatabase.query(TABLE_NAME, null,
                    COLUMN_BLID_CONTACT + " IN('" + TextUtils.join("', '", listBlid) + "')",
                    null, null, null, null, null);
        }

        return cursor;
    }

    /**
     * is Trace
     * @param bluezoneId
     * @return
     */
    public boolean isTrace(byte[] bluezoneId, long timeStart, long timeEnd) {

        boolean ret = false;

        // ret
        Cursor cursor = null;

        // open
        openDatabase();

        try {
            cursor = mDatabase.query(TABLE_NAME, null, "hex( + " + COLUMN_BLID_CONTACT + ") = ? and " +
                            COLUMN_TIME + " >= ? and " + COLUMN_TIME + " <= ?",
                    new String[]{AppUtils.convertBytesToHex(bluezoneId), String.valueOf(timeStart), String.valueOf(timeEnd)},
                    null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                ret = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return ret;
    }

    /**
     * Get all data
     * @return
     */
    public Cursor getCursorData() {
        // ret
        Cursor cursor = null;

        // open
        openDatabase();

        cursor = mDatabase.query(TABLE_NAME, null, null,
                null, null, null, null, null);
        return cursor;
    }

    /**
     * Get all data
     * @return
     */
    public Cursor getCursorData(long timeStart) {
        // ret
        Cursor cursor = null;

        // open
        openDatabase();

        cursor = mDatabase.query(TABLE_NAME, null, COLUMN_TIME + " >= ? ",
                new String[]{String.valueOf(timeStart)},
                null, null, null, null);
        return cursor;
    }

    @Override
    protected void finalize() throws Throwable {
        if (Holder.mInstance != null) {
            Holder.mInstance.close();
        }

        super.finalize();
    }

    private static class Holder {
        static final AppDatabaseHelper mInstance = new AppDatabaseHelper(mContext);
    }
}