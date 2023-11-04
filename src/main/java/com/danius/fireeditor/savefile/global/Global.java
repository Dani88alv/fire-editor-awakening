package com.danius.fireeditor.savefile.global;

import com.danius.fireeditor.savefile.SaveFile;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Global extends SaveFile {
    private final byte[] blockIndex;
    public GlUserBlock glUserBlock; //Global Unlocks
    public GlUnitBlock glUnitBlock; //Logbook Units

    public Global(byte[] fileBytes) {
        //The save file is decompressed
        if (!isDecompressed(fileBytes)) {
            fileBytes = decompressBytes(fileBytes, 0x0);
        }
        //Addresses
        this.blockIndex = Arrays.copyOfRange(fileBytes, 0x0, 0x44);
        //User Data
        byte[] userArray = Arrays.copyOfRange(fileBytes,
                Hex.getByte2(blockIndex, 0x4), Hex.getByte2(blockIndex, 0x8));
        this.glUserBlock = new GlUserBlock(userArray);
        //Logbook Units
        byte[] blockDg18 = Arrays.copyOfRange(fileBytes,
                Hex.getByte2(blockIndex, 0x8), fileBytes.length);
        glUnitBlock = new GlUnitBlock(blockDg18);
    }

    public boolean region() {
        return glUserBlock.avatarMale.isWest;
    }

    public void changeRegion(boolean isWest) {
        glUserBlock.changeRegion(isWest);
        glUnitBlock.changeRegion(isWest);
    }

    /*
    The blocks are combined
     */
    public byte[] getBytes() {
        Hex.setByte2(blockIndex, 0x4, blockIndex.length); //User offset
        Hex.setByte2(blockIndex, 0x8, blockIndex.length + glUserBlock.length()); //Dg18 offset
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(blockIndex);
            outputStream.write(glUserBlock.getBytes());
            outputStream.write(glUnitBlock.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public byte[] getBytesComp() {
        return compressBytes(getBytes(), 0x0);
    }

    private boolean isDecompressed(byte[] fileBytes) {
        byte[] edni = Hex.toByte("45 44 4E 49"); //EDNI
        byte[] value = Hex.getByte4Array(fileBytes, 0x0);
        return Arrays.equals(value, edni);
    }
}
