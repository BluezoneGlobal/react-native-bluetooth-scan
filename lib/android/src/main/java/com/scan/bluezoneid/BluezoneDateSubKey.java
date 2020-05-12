package com.scan.bluezoneid;

/**
 * Create times init BluezoneID
 * @author khanhxu
 */
public class BluezoneDateSubKey extends BluezoneDate {

    private int mNextSubKey = 0;
    private long mDetal = 0;
    private long mTimeNext = 0;
    private long mTimeCurrent;
    private int mMaxSubKey;

    /**
     * Init
     *
     * @param time
     */
    BluezoneDateSubKey(long time, int maxSubKey) throws Exception {
        super(time);
        mTimeCurrent = time;
        if (maxSubKey > 1) {
            mNextSubKey = 0;
            mMaxSubKey = maxSubKey;
            mDetal = BluezoneIdConstants.DAY_MILLISECONDS / maxSubKey;
        } else {
            throw new Exception("maxSubKey > 0");
        }
    }

    /**
     * Init max sub key
     * @param maxSubKey
     */
    public void setMaxSubKey(int maxSubKey) {
        if (maxSubKey > 1) {
            mNextSubKey = 0;
            mMaxSubKey = maxSubKey;
            mDetal = BluezoneIdConstants.DAY_MILLISECONDS / maxSubKey;
        } else {
            throw new IllegalArgumentException("maxSubKey > 0");
        }
    }

    /**
     * Reset getnext subkey
     */
    public void ressetNextSubKey() {
        mNextSubKey = 0;
    }

    /**
     * Call next subtime
     * @return
     */
    long nextTimeSubKey() {
        mTimeNext = getTimeStart() + (mNextSubKey * mDetal);
        mNextSubKey++;
        return mTimeNext;
    }

    /**
     * Check bluezone id
     * @return
     */
    boolean isBluezoneNow() {
        if (mTimeCurrent <= mTimeNext && (mTimeNext >= mTimeNext - mDetal)) {
            return true;
        }

        return false;
    }

    /**
     * Get Index Sub Key
     * @return
     */
    int getIndexSubKey() {
        return (int) ((mTimeCurrent - getTimeStart()) / (BluezoneIdConstants.DAY_MILLISECONDS / mMaxSubKey));
    }
}
