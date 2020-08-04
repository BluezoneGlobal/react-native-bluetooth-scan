package com.scan.gatt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;

import com.scan.AppConstants;
import com.scan.AppUtils;

/**
 * Class connect iOS and read Characteristic
 * @author khanhxu
 */
public class GattConnectSession {
    private ScanResult mScanResult;
    private BluetoothGatt mBluetoothGatt;
    private long mTimeStartConnect;
    private ConnectDataCallback mConnectDataCallback;
    private Context mContext;

    // RSSI connect
    // private int mRssiConnect;

    /**
     * Callback
     */
    public interface ConnectDataCallback {

        void onReadBluezoneId(byte[] bluezoneId, String macId, int rssi);
    }

    /**
     * Init
     * @param scanResult
     */
    public GattConnectSession(Context context, ScanResult scanResult, ConnectDataCallback callback) {
        mScanResult = scanResult;
        mContext = context;
        mConnectDataCallback = callback;
    }

    /**
     * Init start connect
     */
    public void startConnect() {
        final BluetoothDevice bluetoothDevice = mScanResult.getDevice();
        if (bluetoothDevice != null) {
            final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    // Check status
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        gatt.requestMtu(512);
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED || newState == BluetoothProfile.STATE_DISCONNECTING) {
                        closeAndDisconnectBluetoothGatt();
                    }
                }

                @Override
                public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                    gatt.discoverServices();
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    // Check status
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        BluetoothGattService bluetoothGattService = gatt.getService(AppUtils.BLE_UUID_IOS.getUuid());
                        if (bluetoothGattService != null) {
                            // Read characteristic
                            BluetoothGattCharacteristic gattCharacteristic = bluetoothGattService.getCharacteristic(AppUtils.BLE_UUID_CHARECTIC);
                            if (gattCharacteristic != null) {
//                                if (!gatt.readRemoteRssi()) {
//                                    mRssiConnect = mScanResult.getRssi();
//                                }

                                if (!gatt.readCharacteristic(gattCharacteristic)) {
                                    closeAndDisconnectBluetoothGatt();
                                }
                            } else {
                                if (mConnectDataCallback != null) {
                                    mConnectDataCallback.onReadBluezoneId(AppConstants.BLUEZONE_BYTE_NONE, gatt.getDevice().getAddress(), mScanResult.getRssi());
                                }
                                closeAndDisconnectBluetoothGatt();
                            }
                        } else {
                            if (mConnectDataCallback != null) {
                                mConnectDataCallback.onReadBluezoneId(AppConstants.BLUEZONE_BYTE_NONE, gatt.getDevice().getAddress(), mScanResult.getRssi());
                            }
                            closeAndDisconnectBluetoothGatt();
                        }
                    } else {
                        closeAndDisconnectBluetoothGatt();
                    }
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);
                    if (characteristic != null && gatt != null && gatt.getDevice() != null) {
                        // check
                        if (mConnectDataCallback != null) {
                            mConnectDataCallback.onReadBluezoneId(characteristic.getValue(), gatt.getDevice().getAddress(), mScanResult.getRssi());
                        }
                    }

                    closeAndDisconnectBluetoothGatt();
                }

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                    super.onReadRemoteRssi(gatt, rssi, status);
                    // lay rssi
                    // mRssiConnect = rssi;
                }
            };

            mTimeStartConnect = System.currentTimeMillis();

            // Start
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBluetoothGatt = bluetoothDevice.connectGatt(mContext, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
            } else {
                mBluetoothGatt = bluetoothDevice.connectGatt(mContext, false, gattCallback);
            }
        } else {
            closeAndDisconnectBluetoothGatt();
        }
    }

    /**
     * Get session ScanResult
     * @return
     */
    public ScanResult getScanResult() {
        return mScanResult;
    }

    /**
     * Check finish
     * @return
     */
    public boolean isFinished() {
        return mBluetoothGatt == null;
    }

    /**
     * Check timeout disconnect
     */
    public void checkTimeout() {
        if (System.currentTimeMillis() - mTimeStartConnect > AppConstants.Config.TIMEOUT_CONNECT) {
            closeAndDisconnectBluetoothGatt();
        }
    }

    /**
     * Close and disconnect
     */
    public synchronized void closeAndDisconnectBluetoothGatt() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }
}
