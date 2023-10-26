package com.danius.fireeditor.util;

public class Bitflag {

    public static boolean hasFlag1(byte value, int slot) {
        return byte1ToReversedBinaryString(value).charAt(slot) == '1';
    }

    public static void setByte1Flag(byte[] mainBytes, int offset, int bit, boolean set) {
        if (bit < 0 || bit >= mainBytes.length * 8) return;
        int byteIndex = bit / 8;
        int bitIndex = bit % 8;
        byte targetByte = mainBytes[offset + byteIndex];

        if (set) targetByte |= (1 << bitIndex);
        else targetByte &= ~(1 << bitIndex);
        mainBytes[offset + byteIndex] = targetByte;
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
