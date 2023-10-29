package com.danius.fireeditor.savefile.map;

import com.danius.fireeditor.data.ChapterDb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GmapBlock {

    private byte[] header;
    public List<RawMap> maps;

    public GmapBlock(byte[] bytes) {
        this.header = Arrays.copyOfRange(bytes, 0x0, 0x3E);
        int count = bytes[0x3D] & 0xFF;
        maps = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            byte[] mapBytes = Arrays.copyOfRange(bytes,
                    header.length + (0x1D * i), header.length + (0x1D * i) + 0x1D);
            RawMap map = new RawMap(mapBytes);
            maps.add(map);
        }
        ChapterDb.MAX_OVERWORLD_COUNT = maps.size();
    }

    public byte[] bytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(header);
            //All the maps are looped
            for (RawMap map : maps) {
                outputStream.write(map.bytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public int length() {
        return bytes().length;
    }

    public int unlockedChapters() {
        int maxTeams = 1;
        for (RawMap map : maps) {
            if (map.lockState() == 1) maxTeams++;
        }
        return maxTeams;
    }

    public int teamLocation(int teamSlot) {
        for (int i = 0; i < maps.size(); i++) {
            RawMap map = maps.get(i);
            if ((map.isWireless(0) && map.getWirelessId(0) == teamSlot)
                    || (map.isWireless(1) && map.getWirelessId(1) == teamSlot)) {
                return i;
            }
        }
        return -1;
    }

    // 0: Empty | 1: Risen | 2: Merchant | 3: Random!
    public void randomizeMaps(int type) {
        for (RawMap map : maps) {
            Random random = new Random();
            //Only unlocked chapters are modified
            if (map.lockState() == 1) {
                //The map encounters are cleared out
                map.setEncounter(0, 0);
                map.setEncounter(1, 0);

                //The map is randomized
                int firstOption = type;
                if (type == 3) {
                    firstOption = random.nextInt(2) + 1;
                }
                map.setRandomEncounter(0, firstOption);

                //A 1/3 of modifying both slots
                boolean modifyBothEncounters = random.nextDouble() < 1.0 / 3.0;

                //If the option is merchant only, do not modify the second slot
                if (modifyBothEncounters) {
                    //The type is reset to Risen or Merchant only if it is random
                    int secondOption = type;
                    if (type == 3) {
                        secondOption = random.nextInt(2) + 1;
                    }
                    map.setRandomEncounter(1, secondOption);
                }
            }
        }
    }
}
