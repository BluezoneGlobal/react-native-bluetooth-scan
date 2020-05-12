package com.scan.bluezoneid;

import android.util.Pair;

/**
 * @author khanhxu
 */
class BluezoneDailyKey extends Pair<byte[], BluezoneDate> {

    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    BluezoneDailyKey(byte[] first, BluezoneDate second) {
        super(first, second);
    }
}
