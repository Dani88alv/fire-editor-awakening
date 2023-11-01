package com.danius.fireeditor.savefile.units;

import com.danius.fireeditor.data.SkillDb;
import com.danius.fireeditor.data.model.SkillModel;
import com.danius.fireeditor.savefile.units.extrablock.ChildBlock;
import com.danius.fireeditor.savefile.units.extrablock.LogBlock;
import com.danius.fireeditor.savefile.units.mainblock.*;
import com.danius.fireeditor.savefile.wireless.UnitDu;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static com.danius.fireeditor.data.ClassDb.*;
import static com.danius.fireeditor.data.UnitDb.*;


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
    public byte[] rawUnknown; //Sometimes it appears in Map save files, unknown use
    public RawFlags rawFlags; //Battle Flags
    public RawSkill rawSkill; //Learned Skills
    public RawBlockEnd rawBlockEnd; //AI and Misc Stuff
    //Additional blocks
    public ChildBlock rawChild; //Parent Data
    public LogBlock rawLog; //Logbook data

    public Unit() {
        this.rawBlock1 = new RawBlock1();
        this.rawInventory = new RawInventory();
        this.rawBlock2 = new RawBlock2();
        this.rawSupport = new RawSupport();
        this.rawUnknown = new byte[]{0};
        this.rawFlags = new RawFlags();
        this.rawSkill = new RawSkill();
        this.rawBlockEnd = new RawBlockEnd();
    }

    public Unit(int id) {
        this();
        setUnitId(id);
        rawSupport.setUnitId(id);
        rawBlock1.setUnitClass(getStartingClass(id));
        //TODO Stats, weapon exp, level
        //Skills
        List<Integer> skills = getUnitSkills(id);
        for (int i = 0; i < skills.size(); i++) {
            rawBlock2.setCurrentSkill(skills.get(i), i);
            rawSkill.setLearnedSkill(true, skills.get(i));
        }
        //TODO extra block
    }

    public Unit(byte[] unitBytes) {
        splitBlocks(unitBytes);
        checkExtraBlock(unitBytes);
        //System.out.println(rawFlags.report());
    }

    public int getUnitId() {
        return rawBlock1.unitId();
    }

    public void setUnitId(int value) {
        rawBlock1.setUnitId(value);
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
            outputStream.write(rawInventory.bytes());
            outputStream.write(rawBlock2.bytes());
            if (rawSupport != null) outputStream.write(rawSupport.bytes());
            outputStream.write(rawUnknown);
            outputStream.write(rawFlags.bytes());
            outputStream.write(rawSkill.bytes());
            outputStream.write(rawBlockEnd.bytes());
            if (rawLog != null) outputStream.write(rawLog.getBytes());
            if (rawChild != null) outputStream.write(rawChild.bytes());
        } catch (Exception e) {
            throw new RuntimeException("Unable to compile back unit: " + getUnitName(getUnitId()));
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
        if (rawFlags.hasBattleFlag(27)) return "Outrealm";
        if (rawLog != null) return rawLog.getName();
        return getUnitName(getUnitId());
    }

    public int[] modifiers() {
        return Stats.calcModif(this);
    }

    public int[] currentStats(boolean limitBreaker) {
        return Stats.calcCurrentStats(this, limitBreaker);
    }

    public void setLegalSkills() {
        List<SkillModel> legalSkills = SkillDb.getLegalSkills(this);
        rawSkill.setSkillsFromList(legalSkills);
    }

    public boolean isFemale() {
        int id = getUnitId();
        //Game hardcoded logic to determine gender re-classes
        //Class gender flag has priority over character gender
        boolean femaleClass = isClassFemale(rawBlock1.unitClass());
        if (femaleClass) return true;
        //If class is male, check own unit gender
        boolean femaleUnit = isUnitFemale(id);
        if (femaleUnit) return true;
        //If unit is still male, check trait flag
        return rawFlags.hasTraitFlag(0);
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
    public void addBlockLog() {
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
        return unitName() + " | " + getClassName(rawBlock1.unitClass());
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
        rawBlock1.setCurrentHp(Stats.calcMaxStats(this, false)[0] + Stats.temporalBuffs(this)[0]);
    }

    public void kill() {
        rawFlags.setBattleFlag(3, true);
        rawFlags.setBattleFlag(7, true);
        rawBlockEnd.setDeadFlag1(true);
    }

    public void revive() {
        rawFlags.setBattleFlag(3, false);
        rawFlags.setBattleFlag(7, false);
        rawBlockEnd.setDeadFlag1(false);
        rawBlockEnd.setDeadFlag2(false);
        rawBlockEnd.setRetireChapter(0);
    }

    public UnitDu toUnitDu(boolean isWest) {
        UnitDu unitDu = new UnitDu(isWest);
        //General Data
        unitDu.setUnitId(rawBlock1.unitId());
        unitDu.setUnitClass(rawBlock1.unitClass());
        unitDu.setLevel(rawBlock1.level());
        unitDu.setHiddenLevel(rawFlags.hiddenLevel());
        unitDu.setHairColorFx(rawBlockEnd.getHairColorFx());
        for (int i = 0; i < 8; i++) unitDu.setGrowth(rawBlock1.growth()[i], i);
        for (int i = 0; i < 5; i++) unitDu.setActiveSkills(rawBlock2.getCurrentSkills()[i], i);
        for (int i = 0; i < 5; i++) unitDu.setWeaponExp(rawBlock2.getWeaponExp()[i], i);
        //Flags
        unitDu.setDuFlag(4, rawFlags.hasTraitFlag(4)); //Leader Flag
        if (rawFlags.hasBattleFlag(27)) {
            unitDu.setDuFlag(1, true); //Outrealm Flag
            unitDu.setDuFlag(0, true); //Enemy Generic Flag
        }
        if (rawFlags.hasBattleFlag(8) || rawFlags.hasBattleFlag(29)) {
            if (rawLog != null) {
                if (rawLog.hasEinherjarId()) unitDu.setDuFlag(2, true); //SpotPass Flag
                else unitDu.setDuFlag(3, true); //StreetPass Flag
            }
        }
        //Skills
        unitDu.rawSkill = this.rawSkill;
        //Child Data
        if (rawChild != null) for (int i = 0; i < 6; i++) unitDu.setParent(rawChild.parentId(i), i);
        else for (int i = 0; i < 6; i++) unitDu.setParent(255, i);
        //Avatar Data
        if (rawLog != null) unitDu.rawLog = this.rawLog;
        else unitDu.rawLog = new LogBlock();

        return unitDu;
    }

    public String report() {
        String text = "\n";
        //Unit Name and General Data
        text += unitName() + ": " + getClassName(rawBlock1.unitClass());
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
}
