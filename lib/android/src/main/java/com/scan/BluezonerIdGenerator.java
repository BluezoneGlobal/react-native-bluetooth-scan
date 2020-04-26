package com.scan;

import java.util.Random;

/**
 * Class Create BluezonerId random
 * @author khanhxu
 */
public class BluezonerIdGenerator {
    /**
     * Create BluezonerId
     * @param numberChar
     * @return
     */
    public static String createBluezonerId(int numberChar) {

        // Char random
        final String charRandom = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final int charRandomLength = charRandom.length();

        // String
        StringBuilder bluezonerIdBuilder = new StringBuilder();
        
        // Random
        Random rand = new Random(System.currentTimeMillis());

        // for tao numberChar ki tu
        for (int i = 0;  i < numberChar; i++) {
            // Check
            long randomValue = Math.abs(rand.nextLong());

            // Index
            int index = (int) (randomValue % charRandomLength);

            // Create
            bluezonerIdBuilder.append(charRandom.substring(index, index + 1));
        }

        // Ret
        return bluezonerIdBuilder.toString();
    }
}
