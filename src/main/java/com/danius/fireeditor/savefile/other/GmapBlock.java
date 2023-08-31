package com.danius.fireeditor.savefile.other;

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
    }

    public byte[] bytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(header);
            //All the maps are looped
            for (RawMap map : maps) {
                outputStream.write(map.bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public int length() {
        return bytes().length;
    }
}
