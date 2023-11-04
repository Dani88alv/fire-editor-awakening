package com.danius.fireeditor.savefile.global;

import com.danius.fireeditor.savefile.units.extrablock.LogBlock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class UnitLog {
    public byte[] rawBlock1;
    public LogBlock logBlock;

    public UnitLog(byte[] unitBytes) {
        this.rawBlock1 = Arrays.copyOfRange(unitBytes, 0x0, GlUnitBlock.MAIN_SIZE);
        byte[] logBytes = Arrays.copyOfRange(unitBytes, rawBlock1.length, unitBytes.length);

        // Create a new byte array with an extra byte (0) at the end
        byte[] newLogBytes = Arrays.copyOf(logBytes, logBytes.length + 1);
        newLogBytes[logBytes.length] = 0; // Set the last byte to 0
        logBytes = newLogBytes;

        this.logBlock = new LogBlock(logBytes);
        //The new 0 is removed lol
        logBlock.removeFooter();
    }

    public void changeRegion(boolean isWest) {
        logBlock.changeRegion(isWest);
    }

    public byte[] getBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(rawBlock1);
            byteArrayOutputStream.write(logBlock.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public String toString() {
        return logBlock.getName();
    }
}
