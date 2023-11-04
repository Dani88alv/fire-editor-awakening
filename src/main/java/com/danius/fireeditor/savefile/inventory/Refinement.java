package com.danius.fireeditor.savefile.inventory;

import com.danius.fireeditor.data.ItemDb;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class Refinement {
    public static final int CHAR_JP = 10;
    public static final int CHAR_US = 18;
    public static final int SIZE_US = 0x2C;
    public static final int SIZE_JP = 0x1C;

    private byte[] rawHeader;
    private byte[] rawName;
    private byte[] rawStats;

    public Refinement(byte[] refiBytes) {
        initialize(refiBytes);
    }

    public Refinement(boolean isWest) {
        byte[] byteArray = new byte[0x2C];
        initialize(byteArray);
        changeRegion(isWest);
    }

    private void initialize(byte[] refiBytes) {
        boolean isWest = (refiBytes.length == SIZE_US);
        int nameSize = (isWest) ? CHAR_US : CHAR_JP;
        this.rawHeader = Arrays.copyOfRange(refiBytes, 0, 0x2);
        this.rawName = Arrays.copyOfRange(refiBytes, 0x2, 0x2 + (nameSize * 2));
        this.rawStats = Arrays.copyOfRange(refiBytes, 0x2 + rawName.length, refiBytes.length);
    }


    /*
    Not always starts at 0, must be synced in the UI with the forged weapon inventory
     */
    public int position() {
        return rawHeader[0] & 0xFF;
    }

    public void setPosition(int id) {
        rawHeader[0] = (byte) (id & 0xFF);
    }

    public String getName() {
        byte[] nameArray = Arrays.copyOfRange(rawName, 0x0, (rawName.length));
        return Hex.byteArrayToString(nameArray);
    }

    public void setName(String name) {
        if (name.length() > rawName.length) name = name.substring(0, rawName.length);
        byte[] nameBytes = Hex.stringToByteArray(name, rawName.length);
        System.arraycopy(nameBytes, 0, rawName, 0, nameBytes.length);
    }

    public int weaponId() {
        return Hex.getByte2(rawStats, 0x0);
    }

    public void setWeaponId(int value) {
        Hex.setByte2(rawStats, 0x0, value);
    }

    /*
    Might, hit and critical are not absolute, their values are added to the base weapon stats
     */
    public int might() {
        int point = 0x2;
        return rawStats[point] & 0xFF;
    }

    public void setMight(int value) {
        int point = 0x2;
        rawStats[point] = (byte) (value & 0xFF);
    }

    public int hit() {
        int point = 0x3;
        return rawStats[point] & 0xFF;
    }

    public void setHit(int value) {
        int point = 0x3;
        rawStats[point] = (byte) (value & 0xFF);
    }

    public int crit() {
        int point = 0x4;
        return rawStats[point] & 0xFF;
    }

    public void setCrit(int value) {
        int point = 0x4;
        rawStats[point] = (byte) (value & 0xFF);
    }

    public boolean isEnemy() {
        int point = 0x5;
        int value = rawStats[point];
        return value == 1;
    }

    public void setFlagEnemy(boolean set) {
        int point = 0x5;
        rawStats[point] = (byte) ((set) ? 1 : 0);
    }

    public byte[] bytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(rawHeader);
            outputStream.write(rawName);
            outputStream.write(rawStats);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return outputStream.toByteArray();
    }

    public int totalMight() {
        int value = ItemDb.getItemMight(weaponId()) + might();
        while (value > 0xFF) value -= 0x100;
        return value;
    }

    public int totalHit() {
        int value = ItemDb.getItemHit(weaponId()) + hit();
        while (value > 0xFF) value -= 0x100;
        return value;
    }

    public int totalCrit() {
        int value = ItemDb.getItemCrit(weaponId()) + crit();
        while (value > 0xFF) value -= 0x100;
        return value;
    }

    public void changeRegion(boolean isWest) {
        int nameSize = (isWest) ? CHAR_US : CHAR_JP;
        rawName = Hex.changeSizeArray(rawName, nameSize * 2);
        Hex.setByte2(rawName, rawName.length - 2, 0);
    }

    @Override
    public String toString() {
        return getName();
    }
}
