package com.danius.fireeditor.savefile.global;

import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.extrablock.LogBlock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlUnitBlock {
    public static int MAIN_SIZE = 0xFC;
    public static int US_SIZE = MAIN_SIZE + Unit.LBLOCK_SIZE_US;
    public static int JP_SIZE = MAIN_SIZE + Unit.LBLOCK_SIZE_JP;
    private final byte[] header;
    public List<UnitLog> unitList;

    public GlUnitBlock(byte[] blockBytes) {
        unitList = new ArrayList<>();
        this.header = Arrays.copyOfRange(blockBytes, 0x0, 0xC);
        int unitCount = header[0xA];
        System.out.println("Logbook Avatars: " + unitCount);
        byte[] allUnitBytes = Arrays.copyOfRange(blockBytes, header.length, blockBytes.length);
        int unitSize = allUnitBytes.length / unitCount;

        for (int i = 0; i < unitCount; i++) {
            int startIndex = i * unitSize;
            int endIndex = (i == unitCount - 1) ? allUnitBytes.length : (i + 1) * unitSize;
            byte[] part = Arrays.copyOfRange(allUnitBytes, startIndex, endIndex);
            UnitLog unit = new UnitLog(part);
            System.out.println(i + " - " + unit.logBlock.getName());
            unitList.add(unit);
        }
    }

    public byte[] getBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        header[0xA] = (byte) (unitList.size() & 0xFF);
        try {
            byteArrayOutputStream.write(header);
            for (UnitLog unit : unitList) {
                byteArrayOutputStream.write(unit.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
