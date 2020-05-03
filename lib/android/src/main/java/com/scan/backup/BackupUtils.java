package com.scan.backup;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.text.TextUtils;

import com.scan.AppConstants;
import com.scan.AppUtils;
import com.scan.database.AppDatabaseHelper;
import com.scan.database.ExternalDatabaseHelper;
import com.scan.preference.AppPreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Class thực hiện các vấn đề backup của app
 * @author khanhxu
 */
public class BackupUtils {
    /**
     * Get app root path
     */
    public static File getFileBackup(Context context) {
        // return
        File fileBackupDir = null;

        try {
            // Check permission
            if (AppUtils.hasPermissionsExternalStorage(context)) {
                // Get Path
                String folderPath = AppUtils.getAppPath(context);

                // File backip
                fileBackupDir = new File(folderPath, AppConstants.Backup.ROOT_FOLDER);

                // Check ton tai
                if (!fileBackupDir.exists()) {
                    fileBackupDir.mkdirs();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileBackupDir;
    }

    /**
     * Get app root path
     */
    public static File getDbBackup(Context context) {
        // return
        File fileBackup = null;

        try {
            // Backup
            File fileBackupDir = getFileBackup(context);

            // Check
            if (fileBackupDir != null) {
                // Backup
                fileBackup = new File(fileBackupDir, AppConstants.Backup.FILE_NAME_DB);

                // Check
                if (fileBackup != null && !fileBackup.exists()) {
                    fileBackup.createNewFile();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return fileBackup;
    }


    /**
     * Backup userId
     * @param context
     * @param userId
     * @return
     */
    public static boolean backupUserId(Context context, String userId) {
        boolean ret = true;
        try {
            // Backup
            File fileBackupDir = getFileBackup(context);

            // Check
            if (fileBackupDir != null) {
                // doc
                RandomAccessFile fi = null;
                try {
                    // Log
                    fi = new RandomAccessFile(fileBackupDir.getAbsolutePath() + AppConstants.Backup.FILE_NAME_USER_ID, "rw");
                    File fileLog = new File(fileBackupDir, AppConstants.Backup.FILE_NAME_USER_ID);

                    // Ve cuoi
                    fi.seek(fileLog.length());
                    String tmp = AppConstants.Backup.KEY_USER_ID + ":" + userId + "\n";
                    fi.write(tmp.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();

                    ret = false;
                } finally {
                    if (fi != null) {
                        try {
                            fi.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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
     * Lay UserId tu backup
     * @param context
     * @return
     */
    public static String getBackupUserId(Context context) {
        String ret = "";
        try {
            // Backup
            File fileBackupDir = getFileBackup(context);

            // Check
            if (fileBackupDir != null) {
                // File name
                File file = new File(fileBackupDir, AppConstants.Backup.FILE_NAME_USER_ID);

                // Doc
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line = "";

                // Doc tung dong
                while ((line = bufferedReader.readLine()) != null) {
                    // slip
                    String[] configsData = line.split(":");

                    // check
                    if (configsData != null && configsData.length == AppConstants.Backup.DATA_USER_ID_COUNT &&
                            configsData[AppConstants.Backup.DATA_USER_ID_INDEX_KEY].equals(AppConstants.Backup.KEY_USER_ID) &&
                            configsData[AppConstants.Backup.DATA_USER_ID_INDEX_VALUE].length() == AppConstants.USERID_LENGTH) {
                        // Doc
                        ret = configsData[AppConstants.Backup.DATA_USER_ID_INDEX_KEY];
                        break;
                    }
                }

                // check
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Restore Database from External storage
     * @param context
     */
    public static void restoreDatabaseFromExternalStorage(Context context) {
        // Doc du lieu tu file
        try {
            // check mount
            if (AppUtils.hasPermissionsExternalStorage(context) &&
                    Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // File db
                File fileDb = new File(Environment.getDataDirectory() + "/data/" +
                        context.getPackageName() + "/databases/" + AppConstants.Backup.DATABASE_NAME);

                // Check ton tai
                if (!fileDb.exists()) {
                    // Tao file
                    fileDb.createNewFile();
                }

                // File Backup
                File fileBackup = getDbBackup(context);

                // check
                if (fileBackup != null && fileBackup.exists()) {
                    // Copy file
                    AppUtils.copyFile(fileBackup, fileDb);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ham check va thuc hien viec backup Database
     * @param context
     */
    public static void backupDatabase(Context context) {
        // check
        long timeCurrent = System.currentTimeMillis();

        // Check
        if (AppUtils.hasPermissionsExternalStorage(context) &&
                timeCurrent - AppPreferenceManager.getInstance(context).getLastBackup() > AppConstants.Config.BACKUP_INTERVAL) {
            Intent intentService = new Intent(context, BackupDatabaseService.class);
            context.startService(intentService);
        }
    }

    /**
     * Backup DB ra bộ nhớ ngoài
     * @param context
     * @return
     */
    public static boolean backupDbToExternalStorage(Context context) {
        boolean ret = false;
        try {
            // check mount
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // File db
                File file = new File(Environment.getDataDirectory() + "/data/" +
                        context.getPackageName() + "/databases/" + AppConstants.Backup.DATABASE_NAME);

                // Check file backup
                if (file.exists()) {
                    // File Backup
                    File fileBackup = getDbBackup(context);

                    // check
                    if (fileBackup != null) {
                        // Check ton tai
                        if (!fileBackup.exists()) {
                            // Tao file
                            fileBackup.createNewFile();
                        }

                        // Copy file
                        ret = AppUtils.copyFile(file, fileBackup);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Insert External DB
     * @param context
     * @param userId
     * @param rssi
     */
    public static void insertExternalDatabase(Context context, String userId, int rssi) {
        try {
            // File Backup
            File fileBackup = getDbBackup(context);

            // check
            if (fileBackup != null && fileBackup.exists()) {
                ExternalDatabaseHelper.getInstance(context, fileBackup.getAbsolutePath()).insertUserIdTrace(userId, rssi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert External DB
     * @param context
     * @param macId
     * @param rssi
     */
    public static void insertExternalDatabase(Context context, String macId, String devices, int rssi) {
        try {
            // File Backup
            File fileBackup = getDbBackup(context);

            // check
            if (fileBackup != null && fileBackup.exists()) {
                ExternalDatabaseHelper.getInstance(context, fileBackup.getAbsolutePath()).insertMacIdTrace(macId, devices, rssi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Backup data file
     * @param context
     * @param listBlid
     * @return
     */
    public static String backupFileData(Context context, String[] listBlid) {
        String ret = "";

        // Backup
        File fileBackupDir = BackupUtils.getFileBackup(context);

        // blid current
        String blidCurrent = AppPreferenceManager.getInstance(context).getBlid();

        // Check
        if (fileBackupDir != null && !TextUtils.isEmpty(blidCurrent)) {
            // doc
            RandomAccessFile fi = null;
            try {

                // File db
                File fileTrace = new File(Environment.getDataDirectory() + "/data/" +
                        context.getPackageName(), AppConstants.Backup.FILE_NAME_DATA);

                // delete old
                if (fileTrace.exists()) {
                    fileTrace.delete();
                }

                // create file
                if (fileTrace.createNewFile()) {
                    // Accessfile
                    fi = new RandomAccessFile(fileTrace.getAbsolutePath(), "rw");

                    // Get all cursor
                    Cursor cursor = AppDatabaseHelper.getInstance(context).getCursorData(listBlid);

                    // Check
                    if (cursor != null) {
                        // Read
                        while (cursor.moveToNext()) {
                            // Get info
                            String blid = cursor.getString(AppDatabaseHelper.COLUMN_INDEX_BLID);
                            String userId = cursor.getString(AppDatabaseHelper.COLUMN_INDEX_USER_ID);
                            String macId = cursor.getString(AppDatabaseHelper.COLUMN_INDEX_MAC_ID);
                            String devices = cursor.getString(AppDatabaseHelper.COLUMN_INDEX_DEVICES);

                            // Data
                            String retItem = (blid != null ? blid : blidCurrent) + "\t" +
                                    (userId != null ? userId : "") + "\t" +
                                    (macId != null ? macId : "") + "\t" +
                                    (devices != null ? devices : "") + "\t" +
                                    cursor.getInt(AppDatabaseHelper.COLUMN_INDEX_RSSI) + "\t" +
                                    cursor.getInt(AppDatabaseHelper.COLUMN_INDEX_TX_POWER) + "\t" +
                                    cursor.getInt(AppDatabaseHelper.COLUMN_INDEX_STATE) + "\t" +
                                    cursor.getLong(AppDatabaseHelper.COLUMN_INDEX_TIME) +"\n";

                            // Write data
                            fi.write(retItem.getBytes());
                        }

                        // File backup
                        ret = fileTrace.getAbsolutePath();

                        // Close cusor
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

                    }
                }
            }
        }

        return ret;
    }
}
