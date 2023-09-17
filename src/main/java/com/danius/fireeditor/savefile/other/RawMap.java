package com.danius.fireeditor.savefile.other;

import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RawMap {
    private final byte[] header;
    private final List<byte[]> spawnList;

    //First 3 bytes are header, then two 0xD spawns
    public RawMap(byte[] bytes) {
        this.spawnList = new ArrayList<>();
        this.header = Arrays.copyOfRange(bytes, 0x0, 0x3);
        int offset = header.length;
        byte[] spawn1 = Arrays.copyOfRange(bytes, offset, offset + 0xD);
        offset += spawn1.length;
        byte[] spawn2 = Arrays.copyOfRange(bytes, offset, offset + 0xD);
        spawnList.add(spawn1);
        spawnList.add(spawn2);
    }

    public int lockState() {
        return header[0x1] & 0xFF;
    }

    //00: Locked, 01: Beaten, 02: Not Beaten
    public void setLockState(int value) {
        header[0x1] = (byte) (value & 0xFF);
    }

    /*
    Empty: 00 00
    Risen: 01 01
    Merchant 03 02
    Wireless 02 02
     */
    public boolean isEmpty(int slot) {
        return (spawnList.get(slot)[0x0] & 0xFF) == 0x0 && (spawnList.get(slot)[0x1] & 0xFF) == 0x0;
    }

    public boolean isRisen(int slot) {
        return (spawnList.get(slot)[0x0] & 0xFF) == 0x1 && (spawnList.get(slot)[0x1] & 0xFF) == 0x1;
    }

    public boolean isMerchant(int slot) {
        return (spawnList.get(slot)[0x0] & 0xFF) == 0x3 && (spawnList.get(slot)[0x1] & 0xFF) == 0x2;
    }

    public boolean isWireless(int slot) {
        return (spawnList.get(slot)[0x0] & 0xFF) == 0x2 && (spawnList.get(slot)[0x1] & 0xFF) == 0x2;
    }

    //0: Empty, 1: Risen, 2: Merchant, 3: Wireless
    public void setEncounter(int slot, int encounter) {
        switch (encounter) {
            case 0 -> {
                spawnList.get(slot)[0x0] = 0x0;
                spawnList.get(slot)[0x1] = 0x0;
            }
            case 1 -> {
                spawnList.get(slot)[0x0] = 0x1;
                spawnList.get(slot)[0x1] = 0x1;
            }
            case 2 -> {
                spawnList.get(slot)[0x0] = 0x3;
                spawnList.get(slot)[0x1] = 0x2;
            }
            case 3 -> {
                spawnList.get(slot)[0x0] = 0x2;
                spawnList.get(slot)[0x1] = 0x2;
            }
        }
    }

    /*
    0x0: 0x0: Nothing 0x1: Reeking Box 0x2: Wireless 0x3: Merchant
    0x1: 0x0: Nothing 0x1: Reeking Box 0x2: Merchant/Wireless
    0x2-0x3 OverWorld Class
    0x4-0x5 Spawn Pool Type?
    0x6 Time Out (if it reaches 0, the encounter vanishes after a map reload)
    0x7 ? Wireless Team ID (Always FF when not)
    0x8-0xB RNG Seed?
    0xC ? 0x2 first spawn, 0x0 second spawn
     */

    public int getUnitClass(int slot) {
        int point = 0x2;
        return Hex.getByte2(spawnList.get(slot), point);
    }

    public void setUnitClass(int slot, int value) {
        int point = 0x2;
        Hex.setByte2(spawnList.get(slot), point, value);
    }

    public int getPool(int slot) {
        int point = 0x4;
        return Hex.getByte2(spawnList.get(slot), point);
    }

    public void setPool(int slot, int value) {
        int point = 0x4;
        Hex.setByte2(spawnList.get(slot), point, value);
    }

    public int getTimeOut(int slot) {
        int point = 0x6;
        return spawnList.get(slot)[point] & 0xFF;
    }

    public void setTimeOut(int slot, int value) {
        int point = 0x6;
        spawnList.get(slot)[point] = (byte) (value & 0xFF);
    }

    public int getWirelessId(int slot) {
        int point = 0x7;
        return spawnList.get(slot)[point] & 0xFF;
    }

    public void setWirelessId(int slot, int value) {
        int point = 0x7;
        spawnList.get(slot)[point] = (byte) (value & 0xFF);
    }

    public String getSeed(int slot) {
        int point = 0x8;
        byte[] seed = Hex.getByte4Array(spawnList.get(slot), point);
        return Hex.byteArrayToHexString(seed);
    }

    public void setSeed(int slot, String value) {
        int point = 0x8;
        //Add remaining ceros to the left
        StringBuilder sb = new StringBuilder(value);
        while (sb.length() < (4 * 2)) {
            sb.insert(0, '0');
        }
        //Convert it to a byte array
        byte[] logId = Hex.hexStringToByteArray(String.valueOf(sb));
        System.arraycopy(logId, 0, spawnList.get(slot), point, 4);
    }

    public byte[] bytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(header);
            for (byte[] spawn : spawnList) byteArrayOutputStream.write(spawn);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
