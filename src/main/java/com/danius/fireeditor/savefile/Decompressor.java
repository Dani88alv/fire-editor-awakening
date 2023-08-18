package com.danius.fireeditor.savefile;

import com.danius.fireeditor.compression.DataView;
import com.danius.fireeditor.compression.Huffman;
import com.danius.fireeditor.compression.Uint8Array;
import com.danius.fireeditor.util.HexConverts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Decompressor {
    private final static String PMOC = "504D4F43";

    /*
    Checks if the save file is decompressed
     */
    private boolean isDecompressed(byte[] fileBytes) {
        if (fileBytes.length < 1048576) {
            String text = new String(fileBytes);
            return text.contains("TINU") && text.contains("IFER") && text.contains("EDNI") &&
                    text.contains("RESU");
        }
        return false;
    }

    public static byte[] decompressBytes(byte[] all) {
        int decompressStart = 0xC0; //TODO: JAP = 0x80

        ByteArrayOutputStream out = null;
        byte[] data = null;
        try {
            byte[] header = Arrays.copyOf(all, decompressStart);
            Uint8Array decompressedArray = Huffman.decompressArray(Arrays.copyOfRange(all, decompressStart + 0x10, all.length));

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


    private byte[] compressBytes(byte[] all) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] compressedBytes = null;
        try {
            byte[] start = Arrays.copyOf(all, 0xC0);
            byte[] header = getHeader(all, (int) (all.length - 0xC0));
            byte[] compressed = new Huffman().compressArray(Arrays.copyOfRange(all, 0xC0, all.length));
            outputStream.write(start);
            outputStream.write(header);
            outputStream.write(compressed);
            compressedBytes = outputStream.toByteArray();

        } catch (IOException ex) {
            Logger.getLogger(ChapterFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(ChapterFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return compressedBytes;
    }

    /*
    Used to compress the save file
     */
    private byte[] getHeader(byte[] decmp, int length) {
        byte[] Header = new byte[0x10];

        System.arraycopy(HexConverts.hexStringToByteArray(PMOC), 0, Header, 0, 0x4);

        System.arraycopy(DataView.getBytes(2), 0, Header, 0x4, 0x4);
        System.arraycopy(DataView.getBytes(length), 0, Header, 0x8, 0x4);
        byte[] checkSumBytes = DataView.getBytes(getChecksumJava(decmp));
        System.arraycopy(checkSumBytes, 0, Header, 0xC, 0x4);
        //for(int i=0;i<checkSumBytes.length;i++){
        //    System.out.println("checksum "+checkSumBytes[i]);
        //}
        // CRC32 of Decompressed Data.
        return Header;
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
