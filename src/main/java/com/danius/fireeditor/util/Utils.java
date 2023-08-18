package com.danius.fireeditor.util;


import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class Utils {
    public static byte[] concatBytes(byte[] one, byte[] two) {
        byte[] combined = new byte[one.length + two.length];
        System.arraycopy(one, 0, combined, 0, one.length);
        System.arraycopy(two, 0, combined, one.length, two.length);
        return combined;
    }

    public static byte[] decompress(byte[] data) {
        try {
            Inflater decompresser = new Inflater();
            decompresser.setInput(data);
            byte[] result = new byte[100];
            int resultLength = decompresser.inflate(result);
            decompresser.end();
            return result;
        } catch (DataFormatException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, ex);
            return null;
        }
    }

    public static int randomInclusive(int max) {
        return randomInclusive(0, max);
    }

    public static int randomInclusive(int min, int max) {
        if (max <= min)
            return -1;
        return (new Random()).nextInt(max - min + 1) + min;
    }

    public static String getStringFromHex(String hex) {
        if (hex.length() % 2 != 0) {
            System.out.println((hex.length() % 2) + " " + hex);
            return null;
        }
        String result = "";
        for (int i = 0; i < hex.length(); i += 2) {
            String hexPair = hex.substring(i, i + 2);
            if (!hexPair.equals("00")) {
                char newChar = (char) getIntFromHex(hexPair);
                result = result + String.valueOf(newChar);
            }
        }
        return result;
    }

    public static String trimBinaryPair(String binary) {
        String b = binary;
        while (b.startsWith("00"))
            b = b.substring(2);
        while (b.endsWith("00"))
            b = b.substring(0, b.length() - 2);
        return b;
    }

    public static double currencyToDouble(String cantidadAux) {
        if (!cantidadAux.equals("")) {
            cantidadAux = cantidadAux.replace(" ", "");
            cantidadAux = cantidadAux.replace("%", "");
            cantidadAux = cantidadAux.replace("kg", "");
            cantidadAux = cantidadAux.replace("$", "");
            cantidadAux = cantidadAux.replace(",", "");
            try {
                double decimal = Double.parseDouble(cantidadAux);
                return decimal;
            } catch (NumberFormatException ex) {
                return 0.0D;
            }
        }
        return 0.0D;
    }

    public static String trim(String texto) {
        StringTokenizer tokens = new StringTokenizer(texto.trim());
        String returned = "";
        while (tokens.hasMoreTokens())
            returned = returned + tokens.nextToken() + " ";
        return returned.trim();
    }

    public static String getHexPair(int code) {
        if (code < 16)
            return "0" + Integer.toHexString(code).toUpperCase();
        return Integer.toHexString(code).toUpperCase();
    }

    public static int getIntFromHex(String hex) {
        if (!hex.startsWith("0x"))
            return Integer.decode("0x" + hex).intValue();
        return Integer.decode(hex).intValue();
    }

    public static String get48HexFromTextSpaced(String newValue) {
        String ends = "";
        for (int i = 0; i < newValue.length(); i++) {
            char ch = newValue.charAt(i);
            String hex = String.format("%02x", new Object[]{Integer.valueOf(ch)});
            ends = ends + hex + "00";
        }
        while (ends.length() < 48)
            ends = ends + "0";
        return ends;
    }

    public static void exportFile(String path, byte[] fileBytes) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(path));
        fileChooser.setTitle("Save File");
        //fileChooser.setInitialFileName("savedata.dat");
        fileChooser.getExtensionFilters().addAll(
                //new FileChooser.ExtensionFilter("dat", "*.dat")
        );
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(selectedFile);
                fileOutputStream.write(fileBytes);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}