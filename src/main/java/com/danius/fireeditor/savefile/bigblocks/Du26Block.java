package com.danius.fireeditor.savefile.bigblocks;

import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

public class Du26Block {

    private final byte[] rawStreet; //StreetPass Team and Einherjar Map Encounters (Ignored)
    private byte[] rawSpot; //Downloadable Content
    private final byte[] rawEnd; //Dlc Turns and EVST data

    public Du26Block(byte[] bytes) {
        this.rawEnd = Arrays.copyOfRange(bytes, bytes.length - 0x98, bytes.length);
        this.rawSpot = Arrays.copyOfRange(bytes,
                bytes.length - rawEnd.length - 0x20F, bytes.length - rawEnd.length);
        this.rawStreet = Arrays.copyOfRange(bytes,
                0x0, bytes.length - rawSpot.length - rawEnd.length);
    }

    public int dlcTurn(int slot) {
        return rawEnd[slot] & 0xFF;
    }

    public void setDlcTurn(int slot, int value) {
        rawEnd[slot] = (byte) (value & 0xFF);
    }

    public void addSpotpass() {
        String relativePath = "com/danius/fireeditor/templates/blocks/rawSpotpass";
        File file = new File("src/main/resources/" + relativePath);
        rawSpot = Hex.getFileBytes(String.valueOf(file));
    }

    public void setDuelRenown(int value) {
        int point = 0xE;
        Hex.setByte4(rawStreet, point, value);
    }

    public byte[] bytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(rawStreet);
            outputStream.write(rawSpot);
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
