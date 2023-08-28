package com.danius.fireeditor.savefile.bigblocks;

import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

public class Du26Block {
    private final byte[] rawMain; //SpotPass/StreetPass encounters, SpotPass settings, streetpass team settings
    private final byte[] rawEnd; //Dlc Turns

    public Du26Block(byte[] bytes) {
        this.rawMain = Arrays.copyOfRange(bytes, 0x0, bytes.length - 0x98);
        this.rawEnd = Arrays.copyOfRange(bytes, bytes.length - 0x98, bytes.length);
    }

    public int dlcTurn(int slot) {
        return rawEnd[slot] & 0xFF;
    }

    public void setDlcTurn(int slot, int value) {
        rawEnd[slot] = (byte) (value & 0xFF);
    }

    public void addSpotpass() {
        String relativePath = "com/danius/fireeditor/templates/blocks/rawSpotpassShort";
        File file = new File("src/main/resources/" + relativePath);
        byte[] rawSpot = Hex.getFileBytes(String.valueOf(file));
        byte[] header = Hex.toByte("AD 55 0A 19 01");
        int offset = Hex.indexOf(rawMain, header, 0x0, 3);
        if (offset == -1) return;
        System.arraycopy(rawSpot, 0, rawMain, offset, rawSpot.length);
    }

    public void setWirelessRenown(int value) {
        int point = 0xE;
        Hex.setByte4(rawMain, point, value);
    }

    public byte[] bytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(rawMain);
            outputStream.write(rawEnd);

        } catch (Exception e) {
            return null;
        }
        return outputStream.toByteArray();
    }

    public int length() {
        return bytes().length;
    }
}
