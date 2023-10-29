package com.danius.fireeditor.savefile.map;

import com.danius.fireeditor.data.ClassDb;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
            //Empty
            case 0 -> {
                spawnList.get(slot)[0x0] = 0x0;
                spawnList.get(slot)[0x1] = 0x0;
                setUnitClass(slot, 0xFFFF);
                setTimeOut(slot, 0);
                setPool(slot, 0);
                setWirelessId(slot, 255);
                setSeed(slot, "00000000");
            }
            //Risen
            case 1 -> {
                spawnList.get(slot)[0x0] = 0x1;
                spawnList.get(slot)[0x1] = 0x1;
                setTimeOut(slot, 16);
                setWirelessId(slot, 255);
            }
            //Merchant
            case 2 -> {
                spawnList.get(slot)[0x0] = 0x3;
                spawnList.get(slot)[0x1] = 0x2;
                setTimeOut(slot, 16);
                setWirelessId(slot, 255);
                setUnitClass(slot, 73);
            }
            //Wireless
            case 3 -> {
                spawnList.get(slot)[0x0] = 0x2;
                spawnList.get(slot)[0x1] = 0x2;
                setTimeOut(slot, 255);
                setPool(slot, 0);
                setUnitClass(slot, 4); //Tactician Default Class
            }
        }
    }


    /*
    Sets a wireless team encounter with the given team id, without removing existing encounters
    A team can be located in multiple maps at the same time
     */
    public void setWirelessEncounter(int teamSlot, int unitClass) {
        int slotToUse = 0;
        // If the first slot is being used
        if (!isEmpty(0)) {
            //If it is being used by another team, replace the second slot
            if (isWireless(0)) {
                slotToUse = 1;
            }
        }
        setEncounter(slotToUse, 3);
        setWirelessId(slotToUse, teamSlot);
        setUnitClass(slotToUse, unitClass);
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

    private String randomSeed() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int randomDigit = random.nextInt(16); // Generate a random digit from 0 to 15 (0x0 to 0xF)
            sb.append(Integer.toHexString(randomDigit));
        }
        return sb.toString();
    }


    // Randomizes a encounter
    public void setRandomEncounter(int slot, int type) {
        Random random = new Random();
        int class1 = ClassDb.getRandomEnemyClass();
        int pool1 = random.nextInt(36);
        String seed1 = randomSeed();

        switch (type) {
            //Empty
            case 0 -> {
                setEncounter(slot, 0);
            }
            //Risen
            case 1 -> {
                setEncounter(slot, 1);
                setUnitClass(slot, class1);
                setPool(slot, pool1);
                setSeed(slot, seed1);
            }
            //Merchant
            case 2 -> {
                int otherSlot = (slot == 0) ? 1 : 0;
                boolean otherSlotIsMerchant = isMerchant(otherSlot);
                //Prevent 2 merchants from spawning together
                if (!otherSlotIsMerchant) {
                    setEncounter(slot, 2);
                    setPool(slot, pool1);
                    setSeed(slot, seed1);
                }
            }
        }
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
