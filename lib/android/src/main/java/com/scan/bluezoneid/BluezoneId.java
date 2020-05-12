package com.scan.bluezoneid;

import android.util.Pair;

/**
 * @author khanhxu
 */
class BluezoneId extends Pair<byte[], Long> {

    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    BluezoneId(byte[] first, Long second) {
        super(first, second);
    }
}
