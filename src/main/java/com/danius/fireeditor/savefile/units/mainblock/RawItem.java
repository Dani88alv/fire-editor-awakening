package com.danius.fireeditor.savefile.units.mainblock;

import com.danius.fireeditor.util.Hex;

import java.io.IOException;

import static com.danius.fireeditor.data.ItemDb.*;
import static com.danius.fireeditor.util.Hex.*;

public class RawItem {

    private final byte[] bytes;

    public RawItem(byte[] bytes) {
        this.bytes = bytes;
    }

    public RawItem() {
        this.bytes = Hex.toByte("04 00 00 00 00");
    }

    public int itemId() {
        return Hex.getByte2(bytes, 0x1);
    }

    public void setItemId(int id) {
        Hex.setByte2(bytes, 0x1, id);
    }

    public int uses() {
        return bytes[0x3] & 0xFF;
    }

    public void setUses(int value) {
        bytes[0x3] = (byte) (value & 0xFF);
    }

    public boolean equipped() {
        return hasItemFlag(4);
    }

    public boolean dropped() {
        return hasItemFlag(5);
    }

    //Flag 0x10 (fourth) is equipped
    public void setEquipped(boolean set) {
        setItemFlag(4, set);
    }

    //Flag 0x20 (fifth) is equipped
    public void setDropped(boolean set) {
        setItemFlag(5, set);
    }

    public boolean hasItemFlag(int slot) {
        return hasBitFlag(bytes[0x4], slot);
    }

    public void setItemFlag(int slot, boolean set) {
        setBitFlag(bytes, 0x4, slot, set);
    }

    public void removeItem() {
        setItemId(0);
        setUses(0);
        setEquipped(false);
        setDropped(false);
    }

    public byte[] bytes() throws IOException {
        return bytes;
    }

    public String report() {
        return getItemName(itemId()) + " (" + uses() + ")";
    }

    @Override
    public String toString() {
        return getItemName(itemId());
    }

    public int length() {
        return bytes.length;
    }

    private String byteToBinaryString(byte value) {
        return "";
    }
}
