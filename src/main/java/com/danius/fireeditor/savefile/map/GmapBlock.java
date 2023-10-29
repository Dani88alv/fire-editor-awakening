package com.danius.fireeditor.savefile.map;

import com.danius.fireeditor.data.ChapterDb;
import com.danius.fireeditor.savefile.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public List<String> chapterNames() {
        List<String> mapList = new ArrayList<>();
        for (int i = 0; i < maps.size(); i++) mapList.add(gChapterName(i));
        return mapList;
    }

    private String gChapterName(int id) {
        if (id == 0) return "";
        if (id == 1) return "Premonition";
        if (id == 2) return "Prologue";
        if (id > 2 && id <= 26) {
            return "Chapter " + (id - 2);
        } else if (id > 26 && id <= 49) {
            int chapter = id - 26;
            return "Paralogue " + chapter;
        } else if (id == 50) return "Outrealm Gate";
        else {
            return "Modded #" + (Constants.MAX_CHAPTERS - id + 1);
        }
    }
}
