package com.danius.fireeditor.savefile.wireless;

import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class DuItem {

    private byte[] rawStats;
    private byte[] rawName;

    public DuItem(byte[] bytes) {
        this.rawStats = Arrays.copyOfRange(bytes, 0x0, 0x4);
        this.rawName = Arrays.copyOfRange(bytes, 0x4, bytes.length);
    }

    public int getItemId() {
        return rawStats[0x0] & 0xFF;
    }

    public String getName() {
        byte[] nameArray = Arrays.copyOfRange(rawName, 0x0, (rawName.length));
        return Hex.byteArrayToString(nameArray);
    }

    public void changeRegion(boolean isWest) {
        int nameSize = (isWest) ? DuTeam.US_WEAPON : DuTeam.JP_WEAPON;
        rawName = Hex.changeSizeArray(rawName, nameSize);
    }

    public int length() {
        return rawName.length + rawStats.length;
    }

    public byte[] bytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(rawStats);
            byteArrayOutputStream.write(rawName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
