package com.danius.fireeditor.savefile.units.extrablock;

import com.danius.fireeditor.util.Hex;
import com.danius.fireeditor.util.Names;

import java.util.Arrays;

public class RawParent {

    public byte[] bytes;

    public RawParent(byte[] bytes) {
        this.bytes = bytes;
    }

    /*
    Returns character ID, assets, flaws and extra byte of a parent
    0: Parent
    1: Parent's father
    2: Parent's mother
     */
    public int parentId(int slot) {
        int point = 0x2;
        return Hex.getByte2(bytes, (point + (slot * 0x5)));
    }

    public void setParentId(int id, int slot) {
        int point = 0x2;
        Hex.setByte2(bytes, (point + (slot * 0x5)), id);
    }

    public int asset(int slot) {
        int point = 0x4;
        return bytes[point + (slot * 0x5)] & 0xFF;
    }

    public void setAsset(int asset, int slot) {
        int point = 0x4;
        bytes[point + (slot * 0x5)] = (byte) (asset & 0xFF);
    }

    public int flaw(int slot) {
        int point = 0x5;
        return bytes[point + (slot * 0x5)] & 0xFF;
    }

    public void setFlaw(int flaw, int slot) {
        int point = 0x5;
        bytes[point + (slot * 0x5)] = (byte) (flaw & 0xFF);
    }

    public int extraByte(int slot) {
        int point = 0x6;
        return bytes[point + (slot * 0x5)] & 0xFF;
    }

    public void setExtraByte(int value, int slot) {
        int point = 0x6;
        bytes[point + (slot * 0x5)] = (byte) (value & 0xFF);
    }

    public String report() {
        String report = "";
        report += "Parent: " + Names.unitName(parentId(0)) +
                " (" + Names.modifNames.get(asset(0)) + "-" +
                Names.modifNames.get(flaw(0)) + ")";
        report += "\n";
        report += "Grandpa: " + Names.unitName(parentId(1)) +
                " (" + Names.modifNames.get(asset(1)) + "-" +
                Names.modifNames.get(flaw(1)) + ")";
        report += "\n";
        report += "Grandma: " + Names.unitName(parentId(2)) +
                " (" + Names.modifNames.get(asset(2)) + "-" +
                Names.modifNames.get(flaw(2)) + ")";
        return report;
    }
}
