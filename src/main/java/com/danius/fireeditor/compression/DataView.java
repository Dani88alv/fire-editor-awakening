/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danius.fireeditor.compression;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Edgar
 */
public class DataView {
    private int offset = 0;
    private byte[] arreglo;

    public DataView(byte[] array, int offset) {
        this.offset = offset;
        arreglo = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            int part = array[i];
            if (part < 0) {
                part = part & 0xff;
            }
            arreglo[i] = (byte) part;
        }


    }

    public static long byteAsULong(byte b) {
        return ((long) b) & 0x00000000000000FFL;
    }

    public static byte[] getBytes(int value) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteStream);
        byte[] c = null;
        try {
            stream.writeInt(value);
            c = byteStream.toByteArray();

            for (int i = 0; i < c.length; i++) {
                if (c[i] < 0) {
                    c[i] = (byte) (c[i] & 0xff);
                }
            }
            c = flipArray(c);
        } catch (IOException e) {
            return new byte[4];
        } finally {
            try {
                stream.close();
                byteStream.close();
            } catch (IOException ex) {
                Logger.getLogger(DataView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return c;
    }

    public static long getUint32(byte[] arreglo, int offset) {

        long value = byteAsULong(arreglo[0 + offset]) | (byteAsULong(arreglo[1 + offset]) << 8) | (byteAsULong(arreglo[2 + offset]) << 16) | (byteAsULong(arreglo[3 + offset]) << 24);
        //System.out.println("arreglo 0 "+ byteAsULong(arreglo[0+offset]));
        //System.out.println("arreglo 1 "+ byteAsULong(arreglo[1+offset]));
        //System.out.println("arreglo 2 "+ byteAsULong(arreglo[2+offset]));
        //System.out.println("arreglo 3 "+ byteAsULong(arreglo[3+offset]));

        return value;
    }

    public static long getUint16(byte[] arreglo, int offset) {

        long value = byteAsULong(arreglo[0 + offset]) | (byteAsULong(arreglo[1 + offset]) << 8);
        //System.out.println("arreglo 0 "+ byteAsULong(arreglo[0+offset]));
        //System.out.println("arreglo 1 "+ byteAsULong(arreglo[1+offset]));
        //System.out.println("arreglo 2 "+ byteAsULong(arreglo[2+offset]));
        //System.out.println("arreglo 3 "+ byteAsULong(arreglo[3+offset]));

        return value;
    }

    private static byte[] flipArray(byte[] arreglo) {
        for (int i = 0; i < arreglo.length / 2; i++) {
            byte temp = arreglo[i];
            arreglo[i] = arreglo[arreglo.length - i - 1];
            arreglo[arreglo.length - i - 1] = temp;
        }
        return arreglo;
    }

}
 