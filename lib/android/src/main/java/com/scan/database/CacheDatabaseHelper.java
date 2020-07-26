package com.scan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.scan.bluezoneid.BluezoneIdUtils;
import com.scan.model.CacheBleScan;

/**
 * @author khanhxu
 */
public class CacheDatabaseHelper extends SQLiteOpenHelper {

    // Name db
    private static final String DATABASE_NAME = "app_cache_2.db";
    private static final int DATABASE_VERSION = 1;

    // Table
    private static final String TABLE_NAME = "connect_info";
    private static final String COLUMN_CONNECT_BLID_ID = "blid_id";     // user_id
    private static final String COLUMN_CONNECT_MAC_ID = "mac_id";       // mac
    private static final String COLUMN_CONNECT_TIME = "time";           // connect time

    // Index
    private static final int COLUMN_INDEX_BLID_ID = 1;
    private static final int COLUMN_INDEX_CONNECT_MAC_ID  = 2;
    private static final int COLUMN_INDEX_CONNECT_TIME = 3;

    // Sql
    private static SQLiteDatabase mDatabase;
    private static Context mContext;

    /**
     * @param context
     */
    private CacheDatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static CacheDatabaseHelper sHelper;

    /**
     * init
     * @param context
     * @return
     */
    public static CacheDatabaseHelper getInstance(Context context) {
        mContext = context;

        // check
        if (sHelper == null) {
            sHelper = new CacheDatabaseHelper(context);
        }

        return sHelper;
    }

    /**
     * init mDatabase
     */
    private static void setDbInstance() {
        mDatabase = Holder.mInstance.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            // Bang tin tuong
            String createSessionTable = "create table if not exists " +
                    TABLE_NAME + " (" + BaseColumns._ID + " integer primary key autoincrement,"
                    + COLUMN_CONNECT_BLID_ID + " BLOB,"
                    + COLUMN_CONNECT_MAC_ID + " text,"
                    + COLUMN_CONNECT_TIME + " long);";
            sqLiteDatabase.execSQL(createSessionTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Open DB
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
     * Add conneted
     * @param blid
     * @param macId
     * @return
     */
    public long insertConnected(byte[] blid, String macId) {
        long ret = -1;

        // Open
        openDatabase();

        try {
            // Data insert
            final ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_CONNECT_BLID_ID, blid);
            contentValues.put(COLUMN_CONNECT_MAC_ID, macId);
            contentValues.put(COLUMN_CONNECT_TIME, System.currentTimeMillis());

            // insert
            ret = mDatabase.insertOrThrow(TABLE_NAME, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Get blid
     * @param macId
     * @return
     */
    public byte[] getBluezoneId(String macId) {
        byte[] ret = null;

        // Open
        openDatabase();

        // Cursor
        Cursor cursor = null;
        try {
            // Lay thong tin
            cursor = mDatabase.query(TABLE_NAME, null, COLUMN_CONNECT_MAC_ID + "= ?",
                    new String[]{macId}, null, null, null, null);

            // Check
            if (cursor != null && cursor.moveToFirst()) {
                long time = cursor.getLong(COLUMN_INDEX_CONNECT_TIME);
                if (!BluezoneIdUtils.isBluezoneIdChanged(mContext, time, System.currentTimeMillis())) {
                    ret = cursor.getBlob(COLUMN_INDEX_BLID_ID);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            // Close
            if (cursor != null) {
                cursor.close();
            }
        }

        return ret;
    }

    /**
     * Get blid
     * @param macId
     * @return
     */
    public CacheBleScan getCacheBleScan(String macId) {
        CacheBleScan ret = null;

        // Open
        openDatabase();

        // Cursor
        Cursor cursor = null;
        try {
            // Lay thong tin
            cursor = mDatabase.query(TABLE_NAME, null, COLUMN_CONNECT_MAC_ID + "= ?",
                    new String[]{macId}, null, null, null, null);

            // Check
            if (cursor != null && cursor.moveToFirst()) {
                ret = new CacheBleScan(cursor.getBlob(COLUMN_INDEX_BLID_ID), cursor.getLong(COLUMN_INDEX_CONNECT_TIME));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            // Close
            if (cursor != null) {
                cursor.close();
            }
        }

        return ret;
    }

    @Override
    protected void finalize() throws Throwable {
        if (Holder.mInstance != null) {
            Holder.mInstance.close();
        }

        super.finalize();
    }

    private static class Holder {
        public static final CacheDatabaseHelper mInstance = new CacheDatabaseHelper(mContext);
    }
}