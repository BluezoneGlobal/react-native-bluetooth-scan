package com.scan.bluezoneid;

import android.content.Context;
import android.util.Base64;

import com.google.gson.Gson;
import com.scan.AppUtils;
import com.scan.BluezonerIdGenerator;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author khanhxu
 */
public class BluezoneIdUtils {

    /**
     * Get hex bluezone id
     * @param context
     * @return
     */
    public static String getHexBluezoneId(Context context) {
        byte[] bluezoneId = BluezoneIdGenerator.getInstance(context).getBluezoneId();
        if (isBluezoneIdValidate(bluezoneId)) {
            return AppUtils.convertBytesToHex(bluezoneId);
        }

        return "";
    }

    /**
     * Hash SHA256
     * @param text
     * @return
     */
    static byte[] sha256(String text) {
        byte[] ret = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(text.getBytes());
            ret = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Hash SHA256
     * @param data
     * @return
     */
    static byte[] sha256(byte[] data) {
        byte[] ret = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data);
            ret = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * SHA256 encode
     * @param text
     * @return
     */
    static String sha256Encode(String text) {
        String ret = "";

        try {
            byte[] digest = sha256(text);
            ret = Base64.encodeToString(digest, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Convert bytes to long
     * @param bytes
     * @return
     */
    static long bytesToLong(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
        byteBuffer.put(bytes);
        byteBuffer.rewind();
        return byteBuffer.getLong();
    }

    /**
     * Add two bytes
     * @param bytesA
     * @param bytesB
     * @return
     */
    static byte[] addByteArrays(byte[] bytesA, byte[] bytesB) {
        byte[] result = new byte[bytesA.length + bytesB.length];
        System.arraycopy(bytesA, 0, result, 0, bytesA.length);
        System.arraycopy(bytesB, 0, result, bytesA.length, bytesB.length);
        return result;
    }

    /**
     * Convert object to json
     * @param object
     * @return
     */
    static String objectToJson(Object object) {
        return new Gson().toJson(object);
    }

    /**
     * Convert json to Object
     * @return
     */
    static <T> T jsonToObject(String json, Class<T> classOfT) {
        try {
            return new Gson().fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Validate bluezone id
     * @return
     */
    public static boolean isBluezoneIdValidate(byte[] dataBytes) {
        if (dataBytes != null && dataBytes.length == BluezoneIdConstants.Config.LENGTH_BYTE) {
            return true;
        }

        return false;
    }
}
