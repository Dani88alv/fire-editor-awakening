package com.danius.fireeditor.util;

public class Bitflag {

    public static void setByte1Flag(byte[] mainBytes, int offsetPoint, int slot, boolean set) {
        //The flag is set
        char[] flagsChar = byte1ToReversedBinaryString(mainBytes[offsetPoint]).toCharArray();
        if (set) flagsChar[slot] = '1';
        else flagsChar[slot] = '0';
        //The string is converted back to byte array
        String flagString = new String(flagsChar);
        byte[] flagsArray = binaryStringToByteArray(flagString);
        System.arraycopy(flagsArray, 0, mainBytes, offsetPoint, flagsArray.length);
    }

    //Converts a single byte to a reversed binary string
    public static String byte1ToReversedBinaryString(byte b) {
        String binaryString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
        StringBuilder reversedString = new StringBuilder(binaryString).reverse();

        return reversedString.toString();
    }

    //Converts a byte array to a reversed binary string
    public static String bytesToReversedBinaryString(byte[] byteArray) {
        StringBuilder combinedString = new StringBuilder();

        for (byte b : byteArray) {
            String binaryString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            StringBuilder reversedString = new StringBuilder(binaryString).reverse();
            combinedString.append(reversedString);
        }

        return combinedString.toString();
    }

    /*
   Un-reverses the order of byteArrayToBinaryString to properly write the block to the unit
    */
    public static byte[] binaryStringToByteArray(String binaryString) {
        int length = binaryString.length();
        byte[] byteArray = new byte[length / 8];
        for (int i = 0; i < length; i += 8) {
            String byteString = binaryString.substring(i, i + 8);
            StringBuilder reversedByteString = new StringBuilder(byteString).reverse();
            try {
                byte b = (byte) Integer.parseInt(reversedByteString.toString(), 2);
                byteArray[i / 8] = b;
            } catch (NumberFormatException e) {
                System.err.println("Invalid binary digit found: " + reversedByteString);
                // Handle the error case as needed (e.g., assign a default value, skip the byte, etc.)
            }
        }
        return byteArray;
    }
}
