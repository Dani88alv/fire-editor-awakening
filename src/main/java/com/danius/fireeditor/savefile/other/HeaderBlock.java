package com.danius.fireeditor.savefile.other;

import com.danius.fireeditor.util.Bitflag;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class HeaderBlock {
    /*
    Useless block, only to edit the fake difficulty settings on the main menu
     */
    private byte[] rawBlock1;
    private byte[] rawTitle;
    private int remainingFrames = 0;

    public HeaderBlock(byte[] bytes) {
        initialize(bytes);
    }

    private void initialize(byte[] bytes) {
        rawBlock1 = Arrays.copyOfRange(bytes, 0x0, 0x8 + 0x6);
        rawTitle = Arrays.copyOfRange(bytes, rawBlock1.length, bytes.length);
        remainingFrames = playtime() % 60;
    }

    public int playtime() {
        return Hex.getByte4(rawBlock1, 0x1);
    }

    public void setPlaytime(int frames) {
        Hex.setByte4(rawBlock1, 0x1, frames + remainingFrames);
    }

    public boolean gameModeFlag(int slot) {
        int point = 0x8;
        return Bitflag.hasFlag1(rawBlock1[point], slot);
    }

    public void setGameModeFlag(int slot, boolean set) {
        int point = 0x8;
        Bitflag.setByte1Flag(rawBlock1, point, slot, set);
    }

    public boolean isLunaticPlus() {
        int point = 0x9;
        return Bitflag.hasFlag1(rawBlock1[point], 0);
    }

    public void setLunaticPlus(boolean set) {
        int point = 0x9;
        Bitflag.setByte1Flag(rawBlock1, point, 0, set);
    }

    public int difficulty() {
        return rawBlock1[0xD] & 0xFF;
    }

    public void setDifficulty(int id) {
        rawBlock1[0xD] = (byte) (id & 0xFF);
    }

    public byte[] bytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(rawBlock1);
            outputStream.write(rawTitle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();
    }

    public void changeRegion(boolean isWest) {
        byte[] byteArray = bytes();
        boolean isCurrentWest = byteArray.length == 0xC0;
        if (isCurrentWest == isWest) return;

        byte[] newArray;
        // Add 64 bytes
        if (isWest) {
            newArray = new byte[byteArray.length + 64];
            System.arraycopy(byteArray, 0, newArray, 0, byteArray.length);
        }
        // Remove 64 bytes
        else {
            newArray = new byte[byteArray.length - 64];
            System.arraycopy(byteArray, 0, newArray, 0, newArray.length);
        }
        initialize(newArray);
    }

    public int length() {
        return rawBlock1.length + rawTitle.length;
    }
}
