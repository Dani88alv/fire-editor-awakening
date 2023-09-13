package com.danius.fireeditor.savefile.other;

import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class HeaderBlock {
    /*
    Useless block, only to edit the fake difficulty settings on the main menu
     */
    private byte[] rawBlock1;
    public RawDifficulty rawDifficulty;
    private byte[] rawTitle;

    public HeaderBlock(byte[] bytes) {
        initialize(bytes);
    }

    private void initialize(byte[] bytes) {
        rawBlock1 = Arrays.copyOfRange(bytes, 0x0, 0x8);
        rawDifficulty = new RawDifficulty(Arrays.copyOfRange(
                bytes, rawBlock1.length, rawBlock1.length + 0x6
        ));
        rawTitle = Arrays.copyOfRange(bytes, rawBlock1.length + rawDifficulty.length(), bytes.length);
    }

    public void setPlaytime(int frames) {
        Hex.setByte4(rawBlock1, 0x1, frames);
    }

    public byte[] bytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(rawBlock1);
            outputStream.write(rawDifficulty.bytes);
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
        return rawBlock1.length + rawDifficulty.length() + rawTitle.length;
    }
}
