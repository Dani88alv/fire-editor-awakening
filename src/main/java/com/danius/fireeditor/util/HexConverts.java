package com.danius.fireeditor.util;

/**
 * @author Edgar
 */
public class HexConverts {
    public static void writeBytes(byte[] copy, byte[] to, int offset) {
        int copyCounter = 0;
        for (int i = offset; i < copy.length + offset; i++) {
            to[i] = copy[copyCounter];
            copyCounter++;
        }
    }

    public static String getHexFromLetter(String letter) {

        return getHexPair(letter.getBytes()[0]);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String getHexPair(int code) {
        if (code < 0) {
            code = code & 0xff;
        }
        if (code < 0x10) {
            return "0" + Integer.toHexString(code).toUpperCase();
        } else {
            return Integer.toHexString(code).toUpperCase();
        }
    }

    public static String getHexString(byte[] array) {
        String hex = "";
        for (int i = 0; i < array.length; i++) {
            hex = hex + getHexPair(array[i]);
        }
        return hex;
    }

    public static String trimBinaryZeroValueBytes(String binary) {
        String b = binary;
        while (b.startsWith("00")) {
            b = b.substring(2);
        }
        while (b.endsWith("00")) {
            b = b.substring(0, b.length() - 2);
        }
        return b;
    }

    public static int getIntFromHex(String hex) {
        if (!hex.startsWith("0x")) {
            return Integer.decode("0x" + hex);
        } else {
            return Integer.decode(hex);
        }
    }
}
