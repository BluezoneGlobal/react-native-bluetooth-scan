package com.scan.bluezoneid;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.scan.AppUtils;
import com.scan.preference.AppPreferenceManager;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Class Create BluezoneId
 * @author khanhxu
 */
public class BluezoneIdGenerator {

    private static BluezoneIdGenerator sInstance;
    private AppPreferenceManager mPreferenceManager;

    /**
     * init
     * @param context
     * @return
     */
    public static BluezoneIdGenerator getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BluezoneIdGenerator();
            sInstance.mPreferenceManager = AppPreferenceManager.getInstance(context);
        }

        return sInstance;
    }

    /**
     * Get Bluezone Id
     * @return
     */
    public byte[] getBluezoneId() {
        if (BluezoneIdConstants.Config.IS_ROLLING_ID) {
            return rollingBluezoneId();
        }

        return randomBluezoneId().getBytes();
    }

    /**
     * Get bluezone id with time
     * @return
     */
    private byte[] rollingBluezoneId() {
        byte[] bluezoneId = null;

        // Current
        long now = System.currentTimeMillis();
        BluezoneDate bluezoneDateNow = new BluezoneDate(now);

        // Get list BluezoneDailyId
        BluezoneDailyId bluezoneDailyId = getBluezoneDailyId();
        if (bluezoneDailyId != null && bluezoneDailyId.second.getTimeStart() >= bluezoneDateNow.getTimeStart()) {
            int index = getIndexSubKey(now);
            if (index >= 0) {
                BluezoneId bluezoneIdSave = bluezoneDailyId.first.get(index);
                if (bluezoneIdSave != null) {
                    bluezoneId = bluezoneIdSave.first;
                }
            }
        } else {
            // Create BluezoneDailyId
            BluezoneDailyKey bluezoneDailyKey = getBluezoneBaseId();
            if (bluezoneDailyKey != null) {
                // Other date
                bluezoneId = createListBluezoneDailyId(createBluezoneDailyKey(bluezoneDailyKey, bluezoneDateNow.getTimeStart()));
            } else {
                // New create
                bluezoneId = createBluezoneBaseId();
            }
        }

        return bluezoneId;
    }

    /**
     * Create bluezoneId base Id
     * @return blid create
     */
    private byte[] createBluezoneBaseId() {
        byte[] bluezoneDailyKeyNow = null;

        // Create random bluezone base id
        byte[] bluezoneBaseId = initRandomBluezoneBaseId();
        if (bluezoneBaseId != null) {
            BluezoneDailyKey bluezoneBaseIdData = new BluezoneDailyKey(bluezoneBaseId, new BluezoneDate(System.currentTimeMillis()));
            saveBluezoneBaseId(BluezoneIdUtils.objectToJson(bluezoneBaseIdData));

            // Create BluezoneId
            bluezoneDailyKeyNow = createListBluezoneDailyId(bluezoneBaseId);
        }

        return bluezoneDailyKeyNow;
    }

    /**
     * Create BluezoneBaseId
     * @return
     */
    private byte[] initRandomBluezoneBaseId() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create BluezoneDailyKey
     * @param bluezoneDailyKey
     * @param timeEnd
     * @return
     */
    private byte[] createBluezoneDailyKey(BluezoneDailyKey bluezoneDailyKey, long timeEnd) {
        byte[] bluezoneDailyKeyNow = null;

        // Create hash sha256 to Dn-1
        if (bluezoneDailyKey != null) {
            // Default
            bluezoneDailyKeyNow = bluezoneDailyKey.first;

            int times = (int) ((timeEnd - bluezoneDailyKey.second.getTimeStart()) / BluezoneIdConstants.DAY_MILLISECONDS);
            if (times > 0) {
                for (int i = 0; i < times; i++) {
                    bluezoneDailyKeyNow = BluezoneIdUtils.sha256(bluezoneDailyKeyNow);
                }
            }
        }

        return bluezoneDailyKeyNow;
    }

    /**
     * Create BluezoneDailyKey
     * @param dayStart
     * @return
     */
    public BluezoneDailyKey createBluezoneDailyKey(int dayStart) {
        BluezoneDailyKey ret = null;

        BluezoneDailyKey bluezoneDailyKey = getBluezoneBaseId();
        // Create hash sha256 to Dn-1
        if (bluezoneDailyKey != null) {
            // Call Time
            long timeEnd = System.currentTimeMillis() - (dayStart * BluezoneIdConstants.DAY_MILLISECONDS);

            // Default
            byte[] bluezoneDailyKeyDx = bluezoneDailyKey.first;

            int times = (int) ((timeEnd - bluezoneDailyKey.second.getTimeStart()) / BluezoneIdConstants.DAY_MILLISECONDS);
            long timeStart = bluezoneDailyKey.second.getTimeStart();
            if (times > 0) {
                for (int i = 0; i < times; i++) {
                    bluezoneDailyKeyDx = BluezoneIdUtils.sha256(bluezoneDailyKeyDx);
                    timeStart = timeStart + BluezoneIdConstants.DAY_MILLISECONDS;
                }
            }

            ret = new BluezoneDailyKey(bluezoneDailyKeyDx, new BluezoneDate(timeStart));
        }

        return ret;
    }

    /**
     * Create list BluezoneDailyId and save
     * @param bluezoneDailyKey
     * @return
     */
    private byte[] createListBluezoneDailyId(byte[] bluezoneDailyKey) {
        byte[] bluezoneDailyKeyNow = null;

        try {
            // check
            if (bluezoneDailyKey != null && bluezoneDailyKey.length == BluezoneIdConstants.Config.LENGTH_DAILY_KEY) {
                long now = System.currentTimeMillis();

                // Get max sub sub key per day
                int maxSubKey = getMaxNumberSubKey();

                // Data for subkey = bluezoneDailyKey + salt sub key
                byte[] dataCreateSubKey = BluezoneIdUtils.addByteArrays(bluezoneDailyKey, BluezoneIdConstants.Config.SALT_SUB_KEY_DAILY);
                byte[] bluezoneSubKey = BluezoneIdUtils.sha256(dataCreateSubKey);

                // Date subkey
                BluezoneDateSubKey dateSubKey = new BluezoneDateSubKey(now, maxSubKey);

                int indexSubKey = dateSubKey.getIndexSubKey();

                // List BluezoneId
                ArrayList<BluezoneId> listBluezoneId = new ArrayList<>();

                // Create list
                for (int i = 0; i < maxSubKey; i++) {
                    byte[] bluezoneId = convertBluezoneSubKeyToBluezoneId(bluezoneSubKey);
                    if (bluezoneId.length == BluezoneIdConstants.Config.LENGTH_BYTE) {
                        long timeBluezone = dateSubKey.nextTimeSubKey();
                        listBluezoneId.add(new BluezoneId(bluezoneId, timeBluezone));

                        // Check index subkey
                        if (indexSubKey == i) {
                            bluezoneDailyKeyNow = bluezoneId;
                        }
                    }

                    bluezoneSubKey = BluezoneIdUtils.sha256(bluezoneSubKey);
                }

                // Save list daily id
                saveBluezoneDailyId(BluezoneIdUtils.objectToJson(new BluezoneDailyId(listBluezoneId, new BluezoneDate(now))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bluezoneDailyKeyNow;
    }

    /**
     * Convert BluezoneSubKey to BluezoneId
     * @param bluezoneSubKey
     * @return
     */
    public static byte[] convertBluezoneSubKeyToBluezoneId(byte[] bluezoneSubKey) {
        byte[] bluezoneDailyId = new byte[BluezoneIdConstants.Config.LENGTH_BYTE];

        if (bluezoneSubKey != null && bluezoneSubKey.length == BluezoneIdConstants.Config.LENGTH_DAILY_KEY) {
            // 12 Byte: 4 x 4 bytes + 2 x 8 bytes
            for (int i = 0; i < BluezoneIdConstants.Config.LENGTH_BYTE; i++) {
                int start = i * 4;

                // Check 2 end
                if (i > 3) {
                    start = (i - 4) * 2 +  16;
                }

                bluezoneDailyId[i] = bluezoneSubKey[start];
            }
        }

        return bluezoneDailyId;
    }

    /**
     * Get Index Sub Key
     * @return
     */
    private int getIndexSubKey(long time) {
        // Get max sub sub key per day
        int maxSubKey = getMaxNumberSubKey();

        // Index
        return (int) ((time - (new BluezoneDate(time)).getTimeStart()) / (BluezoneIdConstants.DAY_MILLISECONDS / maxSubKey));
    }

    /**
     * Create random bluezoneId
     * @return
     */
    private String randomBluezoneId() {
        String randomBluezoneId = getRadomBluezoneId();
        if (TextUtils.isEmpty(randomBluezoneId)) {
            StringBuilder blidBuilder = new StringBuilder();

            // SecureRandom
            SecureRandom secureRandom = new SecureRandom();

            // Create id
            for (int i = 0; i < BluezoneIdConstants.Config.LENGTH_BLID; i++) {
                // Index Random
                int index = secureRandom.nextInt(BluezoneIdConstants.Config.CHAR_RANDOM.length());

                // Add
                blidBuilder.append(BluezoneIdConstants.Config.CHAR_RANDOM.charAt(index));
            }

            randomBluezoneId = blidBuilder.toString();
            saveBluezoneDailyId(randomBluezoneId);
        }

        return randomBluezoneId;
    }

    /**
     * Save Bluezone base Id
     * @param bluezoneBaseId
     */
    private void saveBluezoneBaseId(String bluezoneBaseId) {
        mPreferenceManager.putString(BluezoneIdConstants.Preference.BLUEZONE_BASE_ID, bluezoneBaseId);
    }

    /**
     * Get Bluezone base Id
     * @return
     */
    public BluezoneDailyKey getBluezoneBaseId() {
        String jsonBluezoneBaseId = mPreferenceManager.getString(BluezoneIdConstants.Preference.BLUEZONE_BASE_ID, "");
        if (!TextUtils.isEmpty(jsonBluezoneBaseId)) {
            return BluezoneIdUtils.jsonToObject(jsonBluezoneBaseId, BluezoneDailyKey.class);
        }

        return null;
    }

    /**
     * Save Bluezone Daily Id
     * @param bluezoneDailyId
     */
    private void saveBluezoneDailyId(String bluezoneDailyId) {
        mPreferenceManager.putString(BluezoneIdConstants.Preference.BLUEZONE_DAILY_ID, bluezoneDailyId);
    }

    /**
     * Get Bluezone Daily Id
     * @return
     */
    private BluezoneDailyId getBluezoneDailyId() {
        String jsonBluezoneDailyId = mPreferenceManager.getString(BluezoneIdConstants.Preference.BLUEZONE_DAILY_ID, "");
        if (!TextUtils.isEmpty(jsonBluezoneDailyId)) {
            return BluezoneIdUtils.jsonToObject(jsonBluezoneDailyId, BluezoneDailyId.class);
        }

        return null;
    }

    /**
     * Get Bluezone Id if Bluezone is Random
     * @return
     */
    private String getRadomBluezoneId() {
        return mPreferenceManager.getString(BluezoneIdConstants.Preference.BLUEZONE_DAILY_ID, "");
    }

    /**
     * Set Number sub key in day
     * @return
     */
    public void setMaxNumberSubKey(int maxNumber) {
        mPreferenceManager.putInt(BluezoneIdConstants.Preference.MAX_NUMBER_SUB_KEY_PER_DAY, maxNumber);
    }

    /**
     * Get Number sub key in day
     * @return
     */
    private int getMaxNumberSubKey() {
        int maxSubKey = mPreferenceManager.getInt(BluezoneIdConstants.Preference.MAX_NUMBER_SUB_KEY_PER_DAY, BluezoneIdConstants.Config.MAX_NUMBER_SUB_KEY_PER_DAY);
        if (maxSubKey < 1) {
            maxSubKey = BluezoneIdConstants.Config.MAX_NUMBER_SUB_KEY_PER_DAY;
        }

        return maxSubKey;
    }
}