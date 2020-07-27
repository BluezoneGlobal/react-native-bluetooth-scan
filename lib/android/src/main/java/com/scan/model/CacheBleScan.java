package com.scan.model;

/**
 * Model cache scan
 * @author khanhxu
 */
public class CacheBleScan {
    private long mTime;
    private byte[] mBluezoneId;

    public CacheBleScan(byte[] bluezoneId, long time) {
        mBluezoneId = bluezoneId;
        mTime = time;
    }

    public byte[] getBluezoneId() {
        return mBluezoneId;
    }

    public long getTime() {
        return mTime;
    }

    public void setBluezoneId(byte[] bluezoneId) {
        mBluezoneId = bluezoneId;
    }

    public void setTime(long time) {
        mTime = time;
    }
}
