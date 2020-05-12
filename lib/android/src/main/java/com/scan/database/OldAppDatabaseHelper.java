package com.scan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.scan.AppConstants;
import com.scan.preference.AppPreferenceManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author khanhxu
 */
public class OldAppDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_db.db";
    public static final int DATABASE_VERSION = 3;

    // Table trace
    public static final String TABLE_NAME = "trace_info";
    public static final String COLUMN_NAMES_TIME = "timestamp";     // timestamp
    public static final String COLUMN_NAMES_USER_ID = "userid";     // user_id
    public static final String COLUMN_NAMES_MAC_ID = "macid";       // mac
    public static final String COLUMN_NAMES_RSSI = "rssi";          // rssi
    public static final String COLUMN_NAMES_DEVICES = "devices";    // devices
    public static final String COLUMN_NAMES_BLID = "blid";          // blid => UserCode
    public static final String COLUMN_NAMES_TX_POWER = "tx_power";  // tx_power
    public static final String COLUMN_NAMES_STATE = "state";        // state

    // Index
    public static final int COLUMN_INDEX_TIME = 1;
    public static final int COLUMN_INDEX_USER_ID = 2;
    public static final int COLUMN_INDEX_MAC_ID = 3;
    public static final int COLUMN_INDEX_RSSI = 4;
    public static final int COLUMN_INDEX_DEVICES = 5;
    public static final int COLUMN_INDEX_BLID = 6;
    public static final int COLUMN_INDEX_TX_POWER = 7;
    public static final int COLUMN_INDEX_STATE = 8;

    private static SQLiteDatabase mDatabase;
    private static Context mContext;

    // HashMap cache
    private static HashMap<String, Long> sCacheInfo = new HashMap<>();


    /**
     * @param context
     */
    private OldAppDatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static OldAppDatabaseHelper sHelper;

    public static OldAppDatabaseHelper getInstance(Context context) {
        mContext = context;

        // check
        if (sHelper == null) {
            sHelper = new OldAppDatabaseHelper(context);
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
                    + COLUMN_NAMES_TIME + " long,"
                    + COLUMN_NAMES_USER_ID + " text,"
                    + COLUMN_NAMES_MAC_ID + " text,"
                    + COLUMN_NAMES_RSSI + " integer,"
                    + COLUMN_NAMES_DEVICES + " text,"
                    + COLUMN_NAMES_BLID + " text,"
                    + COLUMN_NAMES_TX_POWER + " integer,"
                    + COLUMN_NAMES_STATE + " integer);";
            sqLiteDatabase.execSQL(createSessionTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add Column
        if (newVersion == 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_NAMES_DEVICES + " text;");
        } else if (newVersion == 3) { //  Add column
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_NAMES_BLID + " text;");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_NAMES_TX_POWER + " integer;");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_NAMES_STATE + " integer;");
        }
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
     * Them trace info
     * @param userId
     * @param rssi
     * @return
     */
    public long insertTrace(String userId, String macId, String devices, int rssi) {
        long ret = -1;

        // Check
        if (isInsert(userId)) {

            // Open
            openDatabase();

            try {
                long time = System.currentTimeMillis();

                // Data insert
                final ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_NAMES_TIME, time);
                contentValues.put(COLUMN_NAMES_USER_ID, userId);
                contentValues.put(COLUMN_NAMES_MAC_ID, macId);
                contentValues.put(COLUMN_NAMES_RSSI, rssi);
                contentValues.put(COLUMN_NAMES_DEVICES, devices);

                // insert
                ret = mDatabase.insertOrThrow(TABLE_NAME, null, contentValues);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    /**
     * Them trace info
     * @param userId
     * @param rssi
     * @param txPower
     * @return
     */
    public long insertUserIdTrace(String userId, int rssi, int txPower) {
        long ret = -1;

        // Check
        if (isInsert(userId)) {

            // Open
            openDatabase();

            try {
                long time = System.currentTimeMillis();

                // Data insert
                final ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_NAMES_TIME, time);
                contentValues.put(COLUMN_NAMES_USER_ID, userId);
                contentValues.put(COLUMN_NAMES_RSSI, rssi);
                contentValues.put(COLUMN_NAMES_BLID, AppPreferenceManager.getInstance(mContext).getBlid());
                contentValues.put(COLUMN_NAMES_TX_POWER, txPower);

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
    public long insertMacIdTrace(String macId, String devices, int rssi, int txPower) {
        long ret = -1;

        // Check
        if (isInsert(macId)) {

            // Open
            openDatabase();

            try {
                long time = System.currentTimeMillis();

                // Data insert
                final ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_NAMES_TIME, time);
                contentValues.put(COLUMN_NAMES_MAC_ID, macId);
                contentValues.put(COLUMN_NAMES_RSSI, rssi);
                contentValues.put(COLUMN_NAMES_DEVICES, devices);
                contentValues.put(COLUMN_NAMES_BLID, AppPreferenceManager.getInstance(mContext).getBlid());
                contentValues.put(COLUMN_NAMES_TX_POWER, txPower);

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
     * Check thoi gian thuc hien
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
                    COLUMN_NAMES_USER_ID + " IN('" + TextUtils.join("', '", listBlid) + "')",
                    null, null, null, null, null);
        }

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
        static final OldAppDatabaseHelper mInstance = new OldAppDatabaseHelper(mContext);
    }
}