package com.danius.fireeditor.savefile.units;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.units.extrablock.ChildBlock;
import com.danius.fireeditor.savefile.units.extrablock.LogBlock;
import com.danius.fireeditor.savefile.units.mainblock.*;
import com.danius.fireeditor.util.Hex;
import com.danius.fireeditor.util.Names;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Unit {
    public static int GBLOCK_SIZE = 0xBB;
    public static int CBLOCK_SIZE = 0x25;
    public static int LBLOCK_SIZE_US = 0x188;
    public static int LBLOCK_SIZE_JP = 0xFC;

    //The unit is divided into multiple blocks for easier editing
    public RawBlock1 rawBlock1; //General Stuff
    public RawInventory rawInventory; //Inventory
    public RawBlock2 rawBlock2; //Current Skills and Weapon EXP
    public RawSupport rawSupport; //Support conversations, size variable
    //Sometimes it appears in Map save files, unknown use
    public byte[] rawUnknown;
    public RawFlags rawFlags; //Battle Flags
    public RawSkill rawSkill; //Learned Skills
    public RawBlockEnd rawBlockEnd; //AI and Misc Stuff
    //Additional blocks
    public ChildBlock rawChild; //Parent Data
    public LogBlock rawLog; //Logbook data

    public Unit(byte[] unitBytes) {
        splitBlocks(unitBytes);
        checkExtraBlock(unitBytes);
        //System.out.println(rawFlags.report());
    }

    /*
    Initializes all the main blocks
     */
    private void splitBlocks(byte[] unitBytes) {
        try {
            this.rawBlock1 = new RawBlock1(Arrays.copyOfRange(unitBytes, 0x0, 0x1A));
            int length = rawBlock1.length();
            this.rawInventory = new RawInventory(Arrays.copyOfRange(unitBytes, length, length + 0x19));
            length += rawInventory.length();
            this.rawBlock2 = new RawBlock2(Arrays.copyOfRange(unitBytes, length, length + 0x10));
            length += rawBlock2.length();
            this.rawSupport = new RawSupport(
                    Arrays.copyOfRange(unitBytes, length,
                            length + (unitBytes[length] & 0xFF) + 1), rawBlock1.unitId());
            length += rawSupport.length();
            this.rawUnknown = Arrays.copyOfRange(unitBytes, length,
                    length + (unitBytes[length] & 0xFF) + 1);
            length += rawUnknown.length;
            this.rawFlags = new RawFlags(Arrays.copyOfRange(unitBytes, length, length + 0x2A));
            length += rawFlags.bytes().length;
            this.rawSkill = new RawSkill(Arrays.copyOfRange(unitBytes, length, length + 0xD));
            length += rawSkill.length();
            this.rawBlockEnd = new RawBlockEnd(Arrays.copyOfRange(unitBytes, length, length + 0x3F));
        } catch (Exception e) {
            System.out.println("Unable to split unit ID " + Hex.getByte2(unitBytes, 0x1));
            throw new RuntimeException();
        }
    }

    /*
    Combines back all the blocks
     */
    public byte[] getUnitBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(rawBlock1.bytes());
            outputStream.write(rawInventory.getBlockBytes());
            outputStream.write(rawBlock2.bytes());
            if (rawSupport != null) outputStream.write(rawSupport.bytes());
            outputStream.write(rawUnknown);
            outputStream.write(rawFlags.bytes());
            outputStream.write(rawSkill.bytes());
            outputStream.write(rawBlockEnd.getBlockBytes());
            if (rawLog != null) outputStream.write(rawLog.getBytes());
            if (rawChild != null) outputStream.write(rawChild.bytes());
        } catch (Exception e) {
            throw new RuntimeException("Unable to compile back unit: " + Names.unitName(rawBlock1.unitId()));
        }
        return outputStream.toByteArray();
    }

    /*
    Checks if there is a child or logbook block
     */
    private void checkExtraBlock(byte[] unitBytes) {
        try {
            //0x39 is the last current skill pointer, then it comes the support conversations
            //findFBlockStart(unitBytes);
            //Checks the size of the unit to determine their type
            int sizeExtraBlock = unitBytes.length - (GBLOCK_SIZE + rawSupport.length() + rawUnknown.length - 2);
            //The units are split into 1 or 2 blocks
            //Child Units (have a child block)
            if (sizeExtraBlock == CBLOCK_SIZE) {
                this.rawChild = new ChildBlock(Arrays.copyOfRange(unitBytes,
                        unitBytes.length - sizeExtraBlock, unitBytes.length));
            }
            //Avatar and Logbook Units (have a logbook block)
            else if (sizeExtraBlock == LBLOCK_SIZE_US || sizeExtraBlock == LBLOCK_SIZE_JP) {
                this.rawLog = new LogBlock(Arrays.copyOfRange(unitBytes,
                        unitBytes.length - sizeExtraBlock, unitBytes.length));
            }
            //Avatar + Child Unit!
            else if (sizeExtraBlock == (LBLOCK_SIZE_US + CBLOCK_SIZE) ||
                    sizeExtraBlock == (LBLOCK_SIZE_JP + CBLOCK_SIZE)) {
                //The child block is parsed first
                int offsetChild = unitBytes.length - CBLOCK_SIZE;
                this.rawChild = new ChildBlock(
                        Arrays.copyOfRange(unitBytes, offsetChild, unitBytes.length));
                //The logbook block size is calculated
                int logSize = sizeExtraBlock - CBLOCK_SIZE;
                int offsetLog = unitBytes.length - CBLOCK_SIZE - logSize;
                this.rawLog = new LogBlock(Arrays.copyOfRange(unitBytes,
                        offsetLog, unitBytes.length - CBLOCK_SIZE));
            }
            //Invalid extra block, aborting
            else if (sizeExtraBlock != 0) {
                throw new RuntimeException("Invalid size of unit: " + unitName());
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public String unitName() {
        if (rawLog != null) return rawLog.getName();
        return Names.unitName(rawBlock1.unitId());
    }

    public int[] modifiers() {
        return Stats.calcModif(this);
    }

    public int[] currentStats(boolean limitBreaker) {
        return Stats.calcCurrentStats(this, limitBreaker);
    }

    public void setLegalSkills() {
        SkillLogic.setLegalSkills(this);
    }

    //Adds a child block
    public void addBlockChild() {
        //If it does not have any block
        if (rawLog == null && rawChild == null) {
            rawBlockEnd.setTerminator(0, 1);
            this.rawChild = new ChildBlock();
        }
        //If it is a mix unit
        else if (rawLog != null && rawChild == null) {
            rawBlockEnd.setTerminator(1, 6);
            rawLog.setTerminator(true);
            this.rawChild = new ChildBlock();
        }
    }

    //Adds a logbook block
    public void addBlockLog() throws IOException {
        //If it does not have any block
        if (rawLog == null && rawChild == null) {
            rawBlockEnd.setTerminator(1, 6);
            this.rawLog = new LogBlock();
            rawLog.setTerminator(false);
        }
        //If it is a mix unit
        else if (rawLog == null) {
            rawBlockEnd.setTerminator(1, 6);
            this.rawLog = new LogBlock();
            rawLog.setTerminator(true);
        }
    }

    public void removeBlockExtra(boolean deleteLog) {
        //Remove Avatar Block
        if (deleteLog) {
            rawLog = null;
            if (rawChild == null) rawBlockEnd.setTerminator(0, 0);
            else rawBlockEnd.setTerminator(0, 1);
        }
        //Remove Child Block
        else {
            rawChild = null;
            if (rawLog == null) rawBlockEnd.setTerminator(0, 0);
            else rawLog.setTerminator(false);
        }
    }

    public String reportBasic() {
        return unitName() + " (" + FireEditor.classDb.getName(rawBlock1.unitClass()) + ") ";
    }

    //Max out the unit
    public void maxStats() {
        //MAX STATS (Growth, level, weapon exp)
        Stats.setMaxStatsHigh(this);
    }

    public void maxGrowth() {
        int[] growth = Stats.calcMaxGrowth(this);
        for (int i = 0; i < growth.length; i++) {
            rawBlock1.setGrowth(growth[i], i);
        }
    }

    public String report() {
        String text = "\n";
        //Unit Name and General Data
        text += unitName() + ": " + FireEditor.classDb.getName(rawBlock1.unitClass());
        text += "\n" + "Modifiers: " + Arrays.toString(modifiers());
        //Stats
        text += "\n" + rawBlock1.report();
        //Weapon Exp & Current skills
        text += "\n" + rawBlock2.report();
        //Learned skills
        text += "\n" + rawSkill.report();
        //Inventory
        text += rawInventory.report();
        //Other
        text += rawBlockEnd.report();
        //Additional data
        if (rawChild != null) text += "\n" + rawChild.report();
        if (rawLog != null) text += "\n" + rawLog.report();
        //text += "\n";
        return text;
    }

    @Override
    public String toString() {
        return unitName();
    }

    /*
    Hair Colors
    Morgan: 5B 58 55
     */
}
