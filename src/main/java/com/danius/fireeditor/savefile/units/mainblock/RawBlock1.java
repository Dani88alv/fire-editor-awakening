package com.danius.fireeditor.savefile.units.mainblock;

import com.danius.fireeditor.util.Hex;

import java.util.Arrays;

public class RawBlock1 {

    private byte[] blockBytes;

    public RawBlock1(byte[] blockBytes) {
        this.blockBytes = blockBytes;
    }

    public byte[] bytes() {
        return blockBytes;
    }

    /*
    Character ID of the unit
    IDs larger or equal than 4096 represents hardcoded enemy units from story maps
    0x1-0x2
    */
    public int unitId() {
        return Hex.getByte2(blockBytes, 0x1);
    }

    public void setUnitId(int id) {
        Hex.setByte2(blockBytes, 0x1, id);
    }

    public int unitClass() {
        return blockBytes[0x3] & 0xFF;
    }

    public void setUnitClass(int idClass) {
        blockBytes[0x3] = (byte) (idClass & 0xFF);
    }

    //TODO: 0x6-0x9: Pointer to something

    /*
    Retrieves a list of all the level-up growths
    Order: HP, STR, MAG, SKL, SPD, LCK, DEF, RES
    0xA-0x11
     */
    public int[] growth() {
        int point = 0xA;
        int[] ev = new int[8];
        for (int i = 0; i < ev.length; i++) {
            ev[i] = blockBytes[point + i] & 0xFF;
        }
        return ev;
    }

    public void setGrowth(int value, int slot) {
        int point = 0xA;
        if (slot == 0x8) setMovement(value);
        else blockBytes[point + slot] = (byte) (value & 0xFF);
    }

    public void setMaxGrowth() {
        for (int i = 0; i < 8; i++) setGrowth(99, i);
    }

    /*
    Current level of the unit
     */
    public int level() {
        return blockBytes[0x12] & 0xFF;
    }

    public void setLevel(int level) {
        blockBytes[0x12] = (byte) level;
    }

    public void setLevelMax() {
        int[] promoted = new int[]{0x43, 0x44, 0x45, 0x46, 0x48, 0x4E, 0x4D, 0x50, 0x51};
        int max = 20;
        for (int j : promoted) {
            if (unitClass() == j) max = 30;
        }
        setLevel(max);
        setExp(0);
    }

    /*
    Current experience of the unit
     */
    public int exp() {
        return blockBytes[0x13] & 0xFF;
    }

    public void setExp(int exp) {
        blockBytes[0x13] = (byte) (exp & 0xFF);
    }

    /*
    Used on battle and when using a tonic
     */
    public int currentHp() {
        return blockBytes[0x14] & 0xFF;
    }

    public void setCurrentHp(int hp) {
        blockBytes[0x14] = (byte) (hp & 0xFF);
    }

    /*
    Retrieves the extra movement given by Boots
    Legally the max number is 2, though it can be increased further
    0x15
     */
    public int movement() {
        return blockBytes[0x15] & 0xFF;
    }

    public void setMovement(int movement) {
        blockBytes[0x15] = (byte) (movement & 0xFF);
    }

    /*
    Used on battle, Up and Left
    0x16-0x17
     */
    public int[] coordinates1() {
        return new int[]{blockBytes[0x16] & 0xFF, blockBytes[0x17] & 0xFF};
    }

    public void setCoordinates1(int x, int y) {
        blockBytes[0x16] = (byte) (x & 0xFF);
        blockBytes[0x17] = (byte) (y & 0xFF);
    }

    public int[] coordinates2() {
        return new int[]{blockBytes[0x18] & 0xFF, blockBytes[0x19] & 0xFF};
    }

    public void setCoordinates2(int x, int y) {
        blockBytes[0x18] = (byte) (x & 0xFF);
        blockBytes[0x19] = (byte) (y & 0xFF);
    }

    public String report() {
        String text = "";
        //General
        text += "Level: " + level() + " EXP: " + exp() +
                " Boots: " + movement();
        //Battle Stats
        text += "\n" + "Current HP: " + currentHp() + " Position: " + Arrays.toString(coordinates1());
        //Stats
        text += "\n" + "EV: " + Arrays.toString(growth());
        return text;
    }

    public int length() {
        return blockBytes.length;
    }
}
