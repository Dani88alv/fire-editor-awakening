package com.danius.fireeditor.savefile.bigblocks;

import com.danius.fireeditor.savefile.RawDifficulty;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class HeaderBlock {
    /*
    Useless block, only to edit the fake difficulty settings on the main menu
     */
    private final byte[] rawBlock1;
    public RawDifficulty rawDifficulty;
    private final byte[] rawTitle;

    public HeaderBlock(byte[] bytes) {
        rawBlock1 = Arrays.copyOfRange(bytes, 0x0, 0x8);
        rawDifficulty = new RawDifficulty(Arrays.copyOfRange(
                bytes, rawBlock1.length, rawBlock1.length + 0x6
        ));
        rawTitle = Arrays.copyOfRange(bytes, rawBlock1.length + rawDifficulty.length(), bytes.length);
    }

    public void setPlaytime(int frames) {
        Hex.setByte4(rawBlock1, 0x1, frames);
    }

    public byte[] bytes() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(rawBlock1);
        outputStream.write(rawDifficulty.bytes);
        outputStream.write(rawTitle);
        return outputStream.toByteArray();
    }

    public int length() {
        return rawBlock1.length + rawDifficulty.length() + rawTitle.length;
    }
}
