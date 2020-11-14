package com.scan;

import android.content.Context;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

public class IbeaconUtils {
    private static final String UUID_IBEACON = "C7AFEF7B-1788-4F4A-A87D-691D53BF21C4";
    private static final int MANUFACTURNER = 0x004c; // IOS manufac
    private static final int TX_POWER = -59; // tx_power mac dinh dung tinh toan khoang cach
    private static final String BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"; // Beacon_layout cua ibeacon
    private static final String ID1_DEFAULT = "20"; // ID Default
    private static final String ID2_DEFAULT = "20"; // ID Default
    private static BeaconTransmitter sBeaconTransmitter;

    /**
     * Start broadcast Ibeacon
     * @param context
     */
    public static void startIbeacon(Context context) {
        BeaconParser beaconParser = new BeaconParser().setBeaconLayout(BEACON_LAYOUT);
        Beacon beacon = createBeacon(ID1_DEFAULT, ID2_DEFAULT);
        sBeaconTransmitter = new BeaconTransmitter(context, beaconParser);
        sBeaconTransmitter.startAdvertising(beacon);
    }

    /**
     * Start broadcast Ibeacon
     * @param context
     * @param id2
     * @param id3
     */
    public static void startIbeacon(Context context, String id2, String id3) {
        BeaconParser beaconParser = new BeaconParser().setBeaconLayout(BEACON_LAYOUT);
        Beacon beacon = createBeacon(id2, id3);
        sBeaconTransmitter = new BeaconTransmitter(context, beaconParser);
        sBeaconTransmitter.startAdvertising(beacon);
    }

    /**
     * Stop Ibeacon
     */
    public static void stopIbeacon() {
        if (sBeaconTransmitter != null && sBeaconTransmitter.isStarted()) {
            sBeaconTransmitter.stopAdvertising();
        }
    }

    /**
     * Create beacon
     * @param id2
     * @param id3
     * @return
     */
    public static Beacon createBeacon(String id2, String id3) {
        Beacon.Builder beacon = new Beacon.Builder()
                .setId1(UUID_IBEACON)
                .setId2(id2)
                .setId3(id3)
                .setTxPower(TX_POWER)
                .setManufacturer(MANUFACTURNER);
        return beacon.build();
    }
}
