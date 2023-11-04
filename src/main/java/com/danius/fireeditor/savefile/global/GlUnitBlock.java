package com.danius.fireeditor.savefile.global;

import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.extrablock.LogBlock;
import com.danius.fireeditor.savefile.wireless.UnitDu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlUnitBlock {
    public static int MAIN_SIZE_US = 0xFC;
    public static int MAIN_SIZE_JP = 0xAC;
    public static int US_SIZE = MAIN_SIZE_US + Unit.LBLOCK_SIZE_US - 1;
    public static int JP_SIZE = MAIN_SIZE_JP + Unit.LBLOCK_SIZE_JP - 1;
    private final byte[] header;
    public List<UnitDu> unitList;

    public GlUnitBlock(byte[] blockBytes) {
        unitList = new ArrayList<>();
        this.header = Arrays.copyOfRange(blockBytes, 0x0, 0xC);
        int unitCount = header[0xA];
        System.out.println("Logbook Avatars: " + unitCount);
        byte[] allUnitBytes = Arrays.copyOfRange(blockBytes, header.length, blockBytes.length);
        if (allUnitBytes.length == 0) return;
        int unitSize = allUnitBytes.length / unitCount;

        for (int i = 0; i < unitCount; i++) {
            int startIndex = i * unitSize;
            int endIndex = (i == unitCount - 1) ? allUnitBytes.length : (i + 1) * unitSize;
            byte[] part = Arrays.copyOfRange(allUnitBytes, startIndex, endIndex);

            byte[] header = Arrays.copyOfRange(part, 0, 5);
            byte[] main = Arrays.copyOfRange(part, 5, part.length);

            UnitDu unit = new UnitDu(main, null);
            unit.setHeader(header);
            System.out.println(i + " - " + unit.rawLog.getName());
            unitList.add(unit);
        }
    }

    public void changeRegion(boolean isWest) {
        for (UnitDu unitDu : unitList) {
            unitDu.changeRegion(isWest);
        }
    }

    public byte[] getBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        header[0xA] = (byte) (unitList.size() & 0xFF);
        try {
            byteArrayOutputStream.write(header);
            for (UnitDu unit : unitList) {
                byteArrayOutputStream.write(unit.bytesFull());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
