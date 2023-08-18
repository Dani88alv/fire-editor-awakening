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
public class StringArray {
    private HashMap arregloMap;
    //private byte[] arreglo;
    private long size;

    public StringArray(long length) {
        arregloMap = new HashMap();
        for (long i = 0; i < length; i++) {
            arregloMap.put(i, "");
        }
        size = length;
    }

    public void set(long pos, String val) {

        arregloMap.put(pos, val);
    }

    public String get(long i) {
        return arregloMap.get(i).toString();
    }

    public long length() {
        return arregloMap.size();
    }
}