package com.danius.fireeditor.savefile.inventory;

import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Refinement {
    public static final int CHAR_JP = 10;
    public static final int CHAR_US = 18;
    public static final int SIZE_US = 0x2C;
    public static final int SIZE_JP = 0x1C;

    private final byte[] rawHeader;
    private final byte[] rawName;
    private final byte[] rawStats;

    public Refinement(byte[] refiBytes) {
        boolean isWest = (refiBytes.length == SIZE_US);
        int nameSize = (isWest) ? CHAR_US : CHAR_JP;
        this.rawHeader = Arrays.copyOfRange(refiBytes, 0, 0x2);
        this.rawName = Arrays.copyOfRange(refiBytes, 0x2, 0x2 + (nameSize * 2));
        this.rawStats = Arrays.copyOfRange(refiBytes, 0x2 + rawName.length, refiBytes.length);
    }

    //Only used when the block is combined again
    public void setBlockPosition(int id) {
        rawHeader[0] = (byte) (id & 0xFF);
    }

    public String getName() {
        byte[] nameArray = Arrays.copyOfRange(rawName, 0x0, (rawName.length));
        return Hex.byteArrayToString(nameArray);
    }

    public int weaponId() {
        return rawStats[0] & 0xFF;
    }

    public byte[] bytes() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(rawHeader);
        outputStream.write(rawName);
        outputStream.write(rawStats);
        return outputStream.toByteArray();
    }

    @Override
    public String toString() {
        return getName();
    }
}
