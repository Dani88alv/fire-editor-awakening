package com.danius.fireeditor.savefile;

import com.danius.fireeditor.compression.DataView;
import com.danius.fireeditor.compression.Huffman;
import com.danius.fireeditor.compression.Uint8Array;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public abstract class SaveFile {
    private static int sizeHeader = 0x0;

    public static byte[] autoDecompress(byte[] fileBytes) {
        byte[] edni = Hex.toByte("45 44 4E 49"); //EDNI
        byte[] pmoc = Hex.toByte("50 4D 4F 43"); //PMOC

        sizeHeader = 0x0;
        boolean isCompressed = false;
        byte[] global = Hex.getByte4Array(fileBytes, 0x0);
        byte[] us = Hex.getByte4Array(fileBytes, 0xC0);
        byte[] jp = Hex.getByte4Array(fileBytes, 0x80);

        //Checks compression
        if (Arrays.equals(global, pmoc) || Arrays.equals(us, pmoc) || Arrays.equals(jp, pmoc)) {
            isCompressed = true;
        }
        //Checks header size
        //US/EU
        if (Arrays.equals(us, edni) || Arrays.equals(us, pmoc)) {
            sizeHeader = 0xC0;
        }
        //JP
        else if (Arrays.equals(jp, edni) || Arrays.equals(jp, pmoc)) {
            sizeHeader = 0x80;
        }
        //If it is compressed, decompress it
        if (isCompressed) {
            return decompressBytes(fileBytes, sizeHeader);
        }
        return fileBytes;
    }

    public static boolean isChapter(byte[] fileBytes) {
        byte[] decrypt = autoDecompress(fileBytes.clone());
        return sizeHeader != 0x0;
    }

    public static byte[] decompressBytes(byte[] all, int start) {
        ByteArrayOutputStream out = null;
        byte[] data = null;
        try {
            byte[] header = Arrays.copyOf(all, start);
            Uint8Array decompressedArray = Huffman.decompressArray(Arrays.copyOfRange(all, start + 0x10, all.length));

            out = new ByteArrayOutputStream();
            out.write(header);
            //ByteArrayOutputStream ddd=new ByteArrayOutputStream();
            for (long i = 0; i < decompressedArray.length(); i++) {
                out.write((int) decompressedArray.get(i));
                //  ddd.write((int)decompressedArray.get(i));
            }
            data = out.toByteArray();
            //System.err.println(HexConverts.getHexString(ddd.toByteArray()));
        } catch (IOException ex) {
            //Logger.getLogger(EmblemFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                assert out != null;
                out.close();
            } catch (IOException ex) {
                //Logger.getLogger(EmblemFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return data;
    }


    public byte[] compressBytes(byte[] all, int initialOffset) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] compressedBytes = null;
        try {
            byte[] start = Arrays.copyOf(all, initialOffset);
            byte[] header = getHeader(all, (int) (all.length - initialOffset));
            byte[] compressed = new Huffman().compressArray(Arrays.copyOfRange(all, initialOffset, all.length));
            outputStream.write(start);
            outputStream.write(header);
            outputStream.write(compressed);
            compressedBytes = outputStream.toByteArray();

        } catch (IOException ex) {
            Logger.getLogger(Chapter13.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Chapter13.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return compressedBytes;
    }

    /*
    Used to compress the save file
     */
    private byte[] getHeader(byte[] decmp, int length) {
        byte[] header = new byte[0x10];
        String PMOC = "504D4F43";
        System.arraycopy(hexStringToByteArray(PMOC), 0, header, 0, 0x4);

        System.arraycopy(DataView.getBytes(2), 0, header, 0x4, 0x4);
        System.arraycopy(DataView.getBytes(length), 0, header, 0x8, 0x4);
        byte[] checkSumBytes = DataView.getBytes(getChecksumJava(decmp));
        System.arraycopy(checkSumBytes, 0, header, 0xC, 0x4);
        //for(int i=0;i<checkSumBytes.length;i++){
        //    System.out.println("checksum "+checkSumBytes[i]);
        //}
        // CRC32 of Decompressed Data.
        return header;
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    private int getChecksumJava(byte[] data) {
        Checksum checksum = new CRC32();
        /*
         * To compute the CRC32 checksum for byte array, use
         *
         * void update(bytes[] b, int start, int length)
         * method of CRC32 class.
         */
        checksum.update(data, 0, data.length);
        /*
         * Get the generated checksum using
         * getValue method of CRC32 class.
         */
        long lngChecksum = checksum.getValue();
        return (int) lngChecksum;
    }

}
