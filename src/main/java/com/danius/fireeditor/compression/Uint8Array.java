/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danius.fireeditor.compression;

import java.util.HashMap;

/**
 * @author Edgar
 */
public class Uint8Array {
    private HashMap arregloMap;
    //private byte[] arreglo;
    private long size;

    public Uint8Array(long length) {
        arregloMap = new HashMap();
        for (long i = 0; i < length; i++) {
            arregloMap.put(i, 0);
        }
        size = length;
    }

    public Uint8Array(byte[] s) {
        arregloMap = new HashMap();

        //arreglo=new byte[s.length];
        for (long i = 0; i < s.length; i++) {
            int part = s[(int) i];
            if (part < 0) {
                part = part & 0xff;
            }
            arregloMap.put(i, part);

            //arreglo[i]=(byte)part;
        }
        size = s.length;

    }

    public void set(long pos, long val) {
        if (val < 0) {
            val = val & 0xff;
        }
        arregloMap.put(pos, (byte) val);
    }

    public long get(long i) {
        return Long.parseLong(arregloMap.get(i).toString());
    }

    public long length() {
        return arregloMap.size();
    }
}