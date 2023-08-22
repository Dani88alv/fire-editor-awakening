package com.danius.fireeditor.savefile.units.extrablock;

import com.danius.fireeditor.savefile.units.Supports;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ChildBlock {
    /*
    This handles the additional block that child units have
     */

    private RawParent father;
    private RawParent mother;
    private byte[] footer; //Sibling support

    public ChildBlock(byte[] blockBytes) {
        splitBlocks(blockBytes);
    }

    public ChildBlock() {
        //Maiden!Lucina Child Block
        byte[] block = Hex.toByte("01 01 00 FF FF 00 00 00 FF FF 00 00 00 FF FF " +
                "00 00 00 01 00 FF FF 00 00 00 FF FF 00 00 00 FF FF 00 00 00 00 00");
        splitBlocks(block);
    }

    private void splitBlocks(byte[] bytes) {
        father = new RawParent(Arrays.copyOfRange(bytes, 0x1, 0x12));
        mother = new RawParent(Arrays.copyOfRange(bytes, 0x12, 0x23));
        footer = Arrays.copyOfRange(bytes, 0x23, bytes.length);
    }

    /*
    Retrieves unid id, modifiers and extra byte of parents
    0: Father
    1: Mother
    2: Father's father
    3: Father's mother
    4: Mother's father
    5: Mother's mother
     */
    public int parentId(int slot) {
        return switch (slot) {
            case 0 -> father.parentId(0);
            case 1 -> mother.parentId(0);
            case 2 -> father.parentId(1);
            case 3 -> father.parentId(2);
            case 4 -> mother.parentId(1);
            case 5 -> mother.parentId(2);
            default -> 65335;
        };
    }

    public void setParentId(int slot, int value) {
        switch (slot) {
            case 0 -> father.setParentId(value, 0);
            case 1 -> mother.setParentId(value, 0);
            case 2 -> father.setParentId(value, 1);
            case 3 -> father.setParentId(value, 2);
            case 4 -> mother.setParentId(value, 1);
            case 5 -> mother.setParentId(value, 2);
        }
    }

    public int asset(int slot) {
        return switch (slot) {
            case 0 -> father.asset(0);
            case 1 -> mother.asset(0);
            case 2 -> father.asset(1);
            case 3 -> father.asset(2);
            case 4 -> mother.asset(1);
            case 5 -> mother.asset(2);
            default -> 0;
        };
    }

    public void setAsset(int slot, int value) {
        switch (slot) {
            case 0 -> father.setAsset(value, 0);
            case 1 -> mother.setAsset(value, 0);
            case 2 -> father.setAsset(value, 1);
            case 3 -> father.setAsset(value, 2);
            case 4 -> mother.setAsset(value, 1);
            case 5 -> mother.setAsset(value, 2);
        }
    }

    public int flaw(int slot) {
        return switch (slot) {
            case 0 -> father.flaw(0);
            case 1 -> mother.flaw(0);
            case 2 -> father.flaw(1);
            case 3 -> father.flaw(2);
            case 4 -> mother.flaw(1);
            case 5 -> mother.flaw(2);
            default -> 0;
        };
    }

    public void setFlaw(int slot, int value) {
        switch (slot) {
            case 0 -> father.setFlaw(value, 0);
            case 1 -> mother.setFlaw(value, 0);
            case 2 -> father.setFlaw(value, 1);
            case 3 -> father.setFlaw(value, 2);
            case 4 -> mother.setFlaw(value, 1);
            case 5 -> mother.setFlaw(value, 2);
        }
    }

    public int supportParentValue(boolean isFather) {
        if (isFather) return father.extraByte(2);
        else return mother.extraByte(2);
    }

    public void setSupportParent(boolean isFather, int points) {
        if (isFather) father.setExtraByte(points, 2);
        else mother.setExtraByte(points, 2);
    }

    public int supportSiblingValue() {
        return footer[0x1] & 0xFF;
    }

    public void setSupportSibling(int points) {
        footer[0x1] = (byte) (points & 0xFF);
    }

    public void setAllSupportsToLevel(int level) {
        //Gets the type of support of the parameter character
        int type = 4;
        //Gets the max values of the type gotten
        int[] maxValues = Supports.supportValues().get(type);
        if (level == 0) {
            setSupportParent(true, maxValues[0]);
            setSupportParent(false, maxValues[0]);
            setSupportSibling(0);
        } else {
            setSupportParent(true, maxValues[level - 1]);
            setSupportParent(false, maxValues[level - 1]);
            setSupportSibling(maxValues[level - 1]);
        }
    }

    /*
    Support state of both of the parent conversations
     */
    /*
    public int[] getSupportsParents() {
        int[] supports = new int[2];
        int point = 0x11;
        supports[0] = blockBytes[point] & 0xFF;
        supports[1] = blockBytes[point + 0x11] & 0xFF;
        return supports;
    }

     */

    public String report() {
        String text = "";
        text += father.report() + "\n";
        text += mother.report() + "\n";

        return text;
    }

    public byte[] bytes() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(0x1); //Header
        outputStream.write(father.bytes);
        outputStream.write(mother.bytes);
        outputStream.write(footer);
        return outputStream.toByteArray();
    }
}
