package com.danius.fireeditor;


import com.danius.fireeditor.savefile.Chapter13;
import com.danius.fireeditor.savefile.SaveFile;
import com.danius.fireeditor.util.Hex;

public class Test {

    public static void main(String[] args) {

    }

    public static void printHexArray(byte[] array) {
        for (byte b : array) {
            System.out.printf("%02X ", b);
        }
        System.out.println();
    }
}
