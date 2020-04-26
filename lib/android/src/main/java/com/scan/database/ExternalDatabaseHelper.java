package com.scan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class thuc hien viec doc, ghi lieu DB tu ben ngoai the nho vao
 */
public class ExternalDatabaseHelper extends SQLiteOpenHelper {
    private static ExternalDatabaseHelper mInstance = null;

    // DB
    private static SQLiteDatabase mSqliteDatabase;

    /**
     * Khoi tao singliton
     * @param context
     * @param fileBackup
     * @return
     */
    public static synchronized ExternalDatabaseHelper getInstance(Context context, String fileBackup) {
        // check
        if (mInstance == null) {
            mInstance = new ExternalDatabaseHelper(context, fileBackup);
        }

        return mInstance;
    }

    /**
     * Tao Db Ngoai the nho
     * @param context
     */
    private ExternalDatabaseHelper(Context context, String fileBackup) {
        super(context, fileBackup, null, AppDatabaseHelper.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Open DB
     * @return
     */
    public SQLiteDatabase openDatabase() {
        if ((mSqliteDatabase == null) || (!mSqliteDatabase.isOpen())) {
            mSqliteDatabase = this.getWritableDatabase();
        }

        return mSqliteDatabase;
    }

    @Override
    public void close() {
        super.close();

        // Close
        if (mSqliteDatabase != null) {
            mSqliteDatabase.close();
            mSqliteDatabase = null;
        }
    }

    /**
     * Them trace info
     * @param userId
     * @param rssi
     * @return
     */
    public long insertUserIdTrace(String userId, int rssi) {
        long ret = -1;

        // Open
        openDatabase();

        try {
            long time = System.currentTimeMillis();

            // Data insert
            final ContentValues contentValues = new ContentValues();
            contentValues.put(AppDatabaseHelper.COLUMN_NAMES_TIME, time);
            contentValues.put(AppDatabaseHelper.COLUMN_NAMES_USER_ID, userId);
            contentValues.put(AppDatabaseHelper.COLUMN_NAMES_RSSI, rssi);

            // insert
            ret = mSqliteDatabase.insertOrThrow(AppDatabaseHelper.TABLE_NAME, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Them trace info
     * @param macId
     * @param rssi
     * @return
     */
    public long insertMacIdTrace(String macId, String devices, int rssi) {
        long ret = -1;

        // Open
        openDatabase();

        try {
            long time = System.currentTimeMillis();

            // Data insert
            final ContentValues contentValues = new ContentValues();
            contentValues.put(AppDatabaseHelper.COLUMN_NAMES_TIME, time);
            contentValues.put(AppDatabaseHelper.COLUMN_NAMES_MAC_ID, macId);
            contentValues.put(AppDatabaseHelper.COLUMN_NAMES_RSSI, rssi);
            contentValues.put(AppDatabaseHelper.COLUMN_NAMES_DEVICES, devices);

            // insert
            ret = mSqliteDatabase.insertOrThrow(AppDatabaseHelper.TABLE_NAME, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
