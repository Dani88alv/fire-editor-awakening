package com.danius.fireeditor.savefile.units.mainblock;

import com.danius.fireeditor.util.Hex;
import com.danius.fireeditor.util.Names13;

import java.io.IOException;

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
        String binaryString = String.format("%8s", Integer.toBinaryString(bytes[0x4] & 0xFF)).replace(' ', '0');
        return (binaryString.charAt(3)) == '1';
    }

    public boolean dropped() {
        String binaryString = String.format("%8s", Integer.toBinaryString(bytes[0x4] & 0xFF)).replace(' ', '0');
        return (binaryString.charAt(2)) == '1';
    }

    //Flag 0x10 (fourth) is equipped
    public void setEquipped(boolean equip) {
        char value = (equip) ? '1' : '0';
        String binaryString = String.format("%8s", Integer.toBinaryString(bytes[0x4] & 0xFF)).replace(' ', '0');
        char[] charArray = binaryString.toCharArray();
        charArray[0x3] = value;
        String modifiedString = new String(charArray);
        byte convertedByte = (byte) Integer.parseInt(modifiedString, 2);
        bytes[0x4] = convertedByte;
    }

    //Flag 0x20 (fifth) is equipped
    public void setDropped(boolean dropped) {
        char value = (dropped) ? '1' : '0';
        String binaryString = String.format("%8s", Integer.toBinaryString(bytes[0x4] & 0xFF)).replace(' ', '0');
        char[] charArray = binaryString.toCharArray();
        charArray[0x2] = value;
        String modifiedString = new String(charArray);
        byte convertedByte = (byte) Integer.parseInt(modifiedString, 2);
        bytes[0x4] = convertedByte;
    }

    public byte[] bytes() throws IOException {
        return bytes;
    }

    public String report() {
        return Names13.itemName(itemId()) + " (" + uses() + ")";
    }

    @Override
    public String toString() {
        return Names13.itemName(itemId());
    }

    public int length() {
        return bytes.length;
    }

    private String byteToBinaryString(byte value) {
        return "";
    }
}
