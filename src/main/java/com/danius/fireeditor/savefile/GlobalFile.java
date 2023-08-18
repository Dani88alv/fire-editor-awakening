package com.danius.fireeditor.savefile;

import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class GlobalFile extends SaveFile {
    public byte[] fileBytes;
    private byte[] blockIndex;
    private byte[] blockUser; //Support conversations, hair colors, difficulty unlocked
    private byte[] blockDg18; //Logbook units

    public GlobalFile(byte[] fileBytes) {
        //The save file is decompressed
        if (!isDecompressed(fileBytes)) {
            fileBytes = decompressBytes(fileBytes, 0x0);
        }
        this.fileBytes = fileBytes;
        //The blocks are split
        this.blockIndex = Arrays.copyOfRange(fileBytes, 0x0, 0x44);
        this.blockUser = Arrays.copyOfRange(fileBytes,
                Hex.getByte2(blockIndex, 0x4), Hex.getByte2(blockIndex, 0x8));
        this.blockDg18 = Arrays.copyOfRange(fileBytes,
                Hex.getByte2(blockIndex, 0x8), fileBytes.length);
    }

    /*
    The blocks are combined
     */
    public byte[] getBytes() {
        Hex.setByte2(blockIndex, 0x4, blockIndex.length); //User offset
        Hex.setByte2(blockIndex, 0x8, blockIndex.length + blockUser.length); //Dg18 offset
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(blockIndex);
            outputStream.write(blockUser);
            outputStream.write(blockDg18);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    private boolean isDecompressed(byte[] fileBytes) {
        byte[] edni = Hex.toByte("45 44 4E 49"); //EDNI
        byte[] value = Hex.getByte4Array(fileBytes, 0x0);
        return Arrays.equals(value, edni);
    }
}
