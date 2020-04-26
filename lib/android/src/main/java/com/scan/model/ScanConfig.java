package com.scan.model;

/**
 * Model config thực hiện việc interval and duration
 * @author khanhxu
 */

public class ScanConfig {
    private long mInterval;
    private long mDuration;

    public ScanConfig(long duration, long interval) {
        mInterval = interval;
        mDuration = duration;
    }

    public long getDuration() {
        return mDuration;
    }

    public long getInterval() {
        return mInterval;
    }

    public void setDuration(long timeSleep) {
        mDuration = timeSleep;
    }

    public void setInterval(long tineScan) {
        mInterval = tineScan;
    }
}
