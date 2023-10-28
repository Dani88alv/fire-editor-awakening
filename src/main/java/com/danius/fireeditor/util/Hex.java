package com.danius.fireeditor.util;

import javafx.scene.paint.Color;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Hex {

    public static int getByte2(byte[] byteArray, int offset) {
        // Retrieve the two bytes starting at the offset position
        byte b1 = byteArray[offset];
        byte b2 = byteArray[offset + 1];
        // Combine the two bytes into an int value in little-endian order
        return ((b2 & 0xFF) << 8) | (b1 & 0xFF);
    }

    public static void setByte2(byte[] byteArray, int offset, int value) {
        // Convert the int value to two bytes in little-endian order
        byte b1 = (byte) (value & 0xFF);
        byte b2 = (byte) ((value >> 8) & 0xFF);
        // Set the two bytes starting at the offset position
        byteArray[offset] = b1;
        byteArray[offset + 1] = b2;
    }

    public static int getByte4(byte[] byteArray, int offset) {
        int value = 0;
        int length = Math.min(byteArray.length - offset, 4);

        for (int i = 0; i < length; i++) {
            value |= (byteArray[offset + i] & 0xFF) << (8 * i);
        }

        return value;
    }

    public static void setByte4(byte[] fileBytes, int offset, int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(value);
        byte[] valueBytes = buffer.array();
        System.arraycopy(valueBytes, 0, fileBytes, offset, valueBytes.length);
    }

    public static byte[] getByte4Array(byte[] source, int offset) {
        byte[] result = new byte[4];
        System.arraycopy(source, offset, result, 0, 4);
        return result;
    }

    public static Color getColor(byte[] byteArray, int offset) {
        double red = (byteArray[offset] & 0xFF) / 255.0;
        double green = (byteArray[offset + 1] & 0xFF) / 255.0;
        double blue = (byteArray[offset + 2] & 0xFF) / 255.0;
        double opacity = (byteArray[offset + 3] & 0xFF) / 255.0;
        return new Color(red, green, blue, opacity);
    }

    public static void setColor(byte[] byteArray, int offset, Color color) {
        if (byteArray == null || offset < 0 || offset + 4 > byteArray.length) {
            throw new IllegalArgumentException("Invalid arguments for setColor method.");
        }

        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        int opacity = (int) (color.getOpacity() * 255);

        byteArray[offset] = (byte) red;
        byteArray[offset + 1] = (byte) green;
        byteArray[offset + 2] = (byte) blue;
        byteArray[offset + 3] = (byte) opacity;
    }


    //Checks the bitflag of a single byte
    public static boolean hasBitFlag(byte value, int slot) {
        return ((value >> slot) & 1) != 0;
    }

    //Checks the bitflags of a string of bytes
    public static boolean hasBitFlag(byte[] byteArray, int offset, int bit) {
        if (bit < 0 || bit >= byteArray.length * 8) {
            return false; // Bit index is out of range.
        }

        int byteIndex = (bit / 8) + offset;
        int bitIndex = bit % 8;
        byte targetByte = byteArray[byteIndex];

        return ((targetByte >> bitIndex) & 1) == 1;
    }

    //Sets the bitflag in a byte array
    public static void setBitFlag(byte[] byteArray, int offset, int bit, boolean set) {
        if (bit < 0 || bit >= byteArray.length * 8) {
            return; // Bit index is out of range.
        }

        int byteIndex = (bit / 8) + offset;
        int bitIndex = bit % 8;

        if (set) byteArray[byteIndex] |= (1 << bitIndex);
        else byteArray[byteIndex] &= ~(1 << bitIndex);
    }


    public static String byteArrayToString(byte[] bytes) {
        String name = new String(bytes, StandardCharsets.UTF_16LE);
        name = name.replace("\u0000", ""); //The blank characters are removed
        return name;
    }

    public static byte[] stringToByteArray(String str, int byteLimit) {
        byte[] byteArray = new byte[byteLimit];
        byte[] stringBytes = str.getBytes(StandardCharsets.UTF_16LE);

        // Copy the string bytes to the byte array
        int length = Math.min(stringBytes.length, byteArray.length);
        System.arraycopy(stringBytes, 0, byteArray, 0, length);

        // Fill the remaining bytes with null bytes
        for (int i = length; i < byteArray.length; i += 2) {
            byteArray[i] = 0;
            byteArray[i + 1] = 0;
        }
        return byteArray;
    }

    //Converts an int number to 2 bytes in Little Endian
    public static byte[] int2ToByteArray(int number) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) number;
        bytes[1] = (byte) (number >> 8);
        return bytes;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static Color hexToColor(String hexString) {
        int r = Integer.parseInt(hexString.substring(0, 2), 16);
        int g = Integer.parseInt(hexString.substring(2, 4), 16);
        int b = Integer.parseInt(hexString.substring(4, 6), 16);
        return Color.rgb(r, g, b);
    }

    public static String colorToHex(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("%02X%02X%02X", r, g, b);
    }

    private static byte[] hexToByteArrayColor(String hexString) {
        byte[] byteArray = new byte[3];
        for (int i = 0; i < 3; i++) {
            String hex = hexString.substring(i * 2, i * 2 + 2);
            byteArray[i] = (byte) Integer.parseInt(hex, 16);
        }
        return byteArray;
    }

    public static byte[] hexStringToByteArray(String hexString) {
        hexString = hexString.replaceAll("\\s", ""); // Remove any whitespaces
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string: " + hexString);
        }
        int len = hexString.length();
        byte[] byteArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return byteArray;
    }


    public static void setColorToByteArray(byte[] byteArray, int offset, String hexString) {
        int length = hexString.length();
        if (length != 6) {
            throw new IllegalArgumentException("Hex string must have 6 characters");
        }
        byte[] replacementBytes = hexToByteArrayColor(hexString);
        byteArray[offset] = replacementBytes[0];
        byteArray[offset + 1] = replacementBytes[1];
        byteArray[offset + 2] = replacementBytes[2];
    }

    //Returns a byte array of a file using an absolute path
    public static byte[] getFileBytes(String testPath) {
        byte[] fileBytes = new byte[0];
        try {
            fileBytes = Files.readAllBytes(Paths.get(testPath));
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
        return fileBytes;
    }

    //Used for testing only
    public static void writeFile(byte[] fileBytes, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(fileBytes);
            fos.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing the file: " + e.getMessage());
        }
    }

    /*
    Changes a byte array to a new length
     */
    public static byte[] changeSizeArray(byte[] bytes, int newSize) {
        byte[] newBlock = new byte[newSize];
        for (int i = 0; i < newBlock.length; i++) {
            if (i >= bytes.length) newBlock[i] = 0x0; //If JP to US
            else newBlock[i] = bytes[i]; //US to JP
        }
        return newBlock;
    }


    public static int indexOf(byte[] source, byte[] target, int initialOffset, int sequenceIndex) {
        int count = 0;
        for (int i = initialOffset; i < source.length - target.length + 1; i++) {
            boolean found = true;
            for (int j = 0; j < target.length; j++) {
                if (source[i + j] != target[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                if (count == sequenceIndex) {
                    return i + target.length;
                }
                count++;
            }
        }
        return -1;
    }

    public static byte[] toByte(String s) {
        String[] parts = s.split(" ");
        byte[] byteArray = new byte[parts.length];
        for (int i = 0; i < parts.length; i++) {
            byteArray[i] = (byte) Integer.parseInt(parts[i], 16);
        }
        return byteArray;
    }
}
