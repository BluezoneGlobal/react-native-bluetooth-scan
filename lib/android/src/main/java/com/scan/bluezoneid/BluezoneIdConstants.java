package com.scan.bluezoneid;

/**
 * @author khanhxu
 */
public class BluezoneIdConstants {

    static final long DAY_MILLISECONDS = 24 * 60 * 60 * 1000;

    // Constant config
    public static class Config {
        // Rolling ID
        static final boolean IS_ROLLING_ID = true;

        // Length Blid
        static final int LENGTH_BLID = 6;

        // Length Byte
        public static final int LENGTH_BYTE = 12;

        // Length daily key
        public static final int LENGTH_DAILY_KEY = 32;

        // Char random
        static final String CHAR_RANDOM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        // Salt Subkey
        static final byte[] SALT_SUB_KEY_DAILY = "bluzonesubkey".getBytes();

        // Max number subkey daily
        static final int MAX_NUMBER_SUB_KEY_PER_DAY = 24 * 4;
    }

    // Constant Preference
    static class Preference {
        // Save bluezone base id
        static final String BLUEZONE_BASE_ID = "pre_bluezone_base_id";

        // Save bluezone daily id
        static final String BLUEZONE_DAILY_ID = "pre_bluezone_daily_id";

        // Save max number subkey per day
        static final String MAX_NUMBER_SUB_KEY_PER_DAY = "pre_max_number_sub_key_per_day";
    }

    static class TraceInfo {
        public static final String JSON_BLUEZONE_BASE_ID = "base_id";           // BluzoneBaseID
        public static final String JSON_BLUEZONE_BASE_ID_TIME = "time";         // Time create Bluezone base ID
        public static final String JSON_F0_DATA = "data";                       // Data array F0
        public static final String JSON_F0_DAILY_KEY = "daily_key";             // F0 Bluezone base ID
        public static final String JSON_F0_TIME_DK = "time_start";              // F0 Time Create F0
        public static final String JSON_F0_MAX_ROLL = "max";                    // Max Rolling
        public static final String JSON_F0_TIME_END = "time_end";               // Max Rolling

        public static final String FILE_NAME_TRACE_DATA = "data_trace.txt";    // File save Data trace
    }
}
