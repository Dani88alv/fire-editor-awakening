package com.danius.fireeditor.savefile.units;

import com.danius.fireeditor.savefile.units.extrablock.ChildBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Stats {

    /*
    TODO: Cómo calcular stats
    Los stats máximos es max de la clase + modificadores personaje
    Los stats actuales es stats base clase + base personaje + growth (sin superar el límite)
     */

    public static boolean hasLimitBreaker(Unit unit) {
        boolean hasLimit = false;
        for (int i = 0; i < 5; i++) {
            if (unit.rawBlock2.getCurrentSkills()[i] == 91) hasLimit = true;
        }
        return hasLimit;
    }

    public static int rating(Unit unit, boolean limitBreak) {
        int[] totalStats = calcCurrentStats(unit, limitBreak);
        int[] buffs = allBuffs(unit);
        int total = 0;
        for (int i = 1; i < totalStats.length; i++) total += totalStats[i] + buffs[i];
        return total;
    }

    public static int[] allBuffs(Unit unit) {
        int[] buffTemporal = temporalBuffs(unit);
        int[] buffItem = itemBuff(unit);
        int[] skillBuffs = skillBuffs(unit);
        for (int i = 0; i < buffTemporal.length; i++) {
            buffTemporal[i] += buffItem[i] + skillBuffs[i];
        }
        return buffTemporal;
    }

    public static int[] skillBuffs(Unit unit) {
        //0 HP - 1 STR - 2 MAG - 3 SKL - 4 - SPD - 5 LCK - 6 DEF - 7 RES
        int[] buffs = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        int[] activeSkills = unit.rawBlock2.getCurrentSkills();
        List<Integer> repeatedSkills = new ArrayList<>();
        for (int activeSkill : activeSkills) {
            if (!repeatedSkills.contains(activeSkill)) {
                if (activeSkill == 1) buffs[0] += 5;
                else if (activeSkill == 2) buffs[1] += 2;
                else if (activeSkill == 3) buffs[2] += 2;
                else if (activeSkill == 4) buffs[3] += 2;
                else if (activeSkill == 5) buffs[4] += 2;
                else if (activeSkill == 6) buffs[6] += 2;
                else if (activeSkill == 7) buffs[7] += 2;
                else if (activeSkill == 53) buffs[5] += 4;
                else if (activeSkill == 99) buffs[7] += 10;
                else if (activeSkill == 88) for (int j = 1; j < buffs.length; j++) buffs[j] += 2;
            }
            repeatedSkills.add(activeSkill);
        }
        return buffs;
    }

    public static int[] itemBuff(Unit unit) {
        int[] buffs = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < unit.rawInventory.items.size(); i++) {
            if (unit.rawInventory.items.get(i).equipped()) {
                int id = unit.rawInventory.items.get(i).itemId();
                if (itemBuffList().containsKey(id)) buffs = itemBuffList().get(id);
                break;
            }
        }
        return buffs;
    }

    public static int[] temporalBuffs(Unit unit) {
        //0 HP - 1 STR - 2 MAG - 3 SKL - 4 - SPD - 5 LCK - 6 DEF - 7 RES
        int[] buffs = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        byte[] rawFlags = unit.rawFlags.bytes;
        //RES unused byte
        buffs[7] += rawFlags[0x1A] & 0xFF;
        //Unused Flags
        if ((rawFlags[0x1C] & 0xFF) > 0) { //STR+2, MAG+2, DEF+2, RES+2
            buffs[1] += 2;
            buffs[2] += 2;
            buffs[6] += 2;
            buffs[7] += 2;
        }
        if ((rawFlags[0x1D] & 0xFF) > 0) buffs[1] += 4; //STR+4
        if ((rawFlags[0x1E] & 0xFF) > 0) buffs[2] += 4; //MAG+4
        if ((rawFlags[0x1F] & 0xFF) > 0) buffs[3] += 4; //SKL+4
        if ((rawFlags[0x20] & 0xFF) > 0) buffs[4] += 4; //SPD+4
        if ((rawFlags[0x21] & 0xFF) > 0) buffs[5] += 8; //LCK+8
        if ((rawFlags[0x22] & 0xFF) > 0) buffs[6] += 4; //DEF+4
        if ((rawFlags[0x23] & 0xFF) > 0) buffs[7] += 4; //RES+4
        if ((rawFlags[0x24] & 0xFF) > 0) { //All Stats +4 (No HP)
            for (int i = 1; i < buffs.length; i++) buffs[i] += 4;
        }
        if ((rawFlags[0x26] & 0xFF) > 0) { //All Stats +4, Movement+1 (No HP)
            for (int i = 1; i < buffs.length; i++) buffs[i] += 2;
        }
        //Tonic flags
        String tonics = unit.rawFlags.tonicFlagString();
        for (int i = 0; i < tonics.length(); i++) {
            if (tonics.charAt(i) == '1') {
                if (i == 0) buffs[i] += 5;
                else buffs[i] += 2;
            }
        }
        //Unused Tonic flags
        String extraFlags = unit.rawFlags.barrackFlagString();
        for (int i = 1; i < extraFlags.length(); i++) if (extraFlags.charAt(i) == '1') buffs[i] += 4;

        return buffs;
    }

    public static void setMaxStatsHigh(Unit unit) {
        unit.rawBlock1.setMaxGrowth();
        int[] maxStats = calcMaxStats(unit, hasLimitBreaker(unit));
        //if (unit.rawSkill.skillString.charAt())
        unit.rawBlock1.setCurrentHp(maxStats[0]); //Updates the current HP to match out
        unit.rawBlock1.setLevelMax(); //Updates the level and experience
        unit.rawBlock2.setMaxWeaponExp(); //Sets the max weapon exp
    }

    /*
    Adds all the stats together to calculate the current stats
     */
    public static int[] calcCurrentStats(Unit unit, boolean limitBreak) {
        int[] maxStats = calcMaxStats(unit, limitBreak);
        int[] growths = unit.rawBlock1.growth();
        int[] unitAddition = getUnitAddition(unit.rawBlock1.unitId()); //Hardcoded
        int[] classAddition = getClassAddition(unit.rawBlock1.unitClass()); //Hardcoded
        //If it has logbook data, +2 on their asset
        if (unit.hasLogBlock) {
            int asset = unit.rawLog.getAssetFlaw()[0];
            if (asset != 0) unitAddition[asset - 1] += 2; //-1 bc asset 0 is none, asset 1 is HP, but addition 0 is HP
        }

        for (int i = 0; i < growths.length; i++) {
            growths[i] += unitAddition[i] + classAddition[i];
            if (growths[i] > 255) growths[i] -= 256; //If higher than the actual limit size
            else if (growths[i] > maxStats[i]) growths[i] = maxStats[i]; //If it is higher than the limit
        }
        return growths;
    }

    public static int[] calcMaxStats(Unit unit, boolean limitBreaker) {
        int[] modif = calcModif(unit);
        int[] maxClass = getClassMax(unit.rawBlock1.unitClass());
        for (int i = 0; i < maxClass.length; i++) {
            maxClass[i] += modif[i];
            if (limitBreaker && i != 0) maxClass[i] += 10;
        }
        return maxClass;
    }

    /*
    Calculates the modifiers of a unit
     */
    public static int[] calcModif(Unit unit) {
        if (unit.hasLogBlock) return calcModifLog(unit); //MU & Logbook
        else if (unit.hasChildBlock) return calcModifChild(unit); //Child units
        else return getBaseModif(unit.rawBlock1.unitId());  //Regular units
    }

    /*
    Calculates the FULL modifiers of a child unit
    Ambos padres principales deben ser personajes válidos antes que NADA
    Los assets y flaws de abuelos no se cuentan si dicha pareja, además de los padres, son personajes válidos
     */
    private static int[] calcModifChild(Unit unit) {
        int[] mods = new int[8];
        ChildBlock block = unit.rawChild;
        int father = block.parentId(0);
        int mother = block.parentId(1);
        boolean isFatherChild = father >= 32 && father <= 44 || father == 0x1A;
        boolean isMotherChild = mother >= 32 && mother <= 44 || mother == 0x1A;
        //Adds ALL the possible extra 12 modifiers
        for (int i = 0; i < mods.length; i++) {
            mods[i] = getBaseModif(unit.rawBlock1.unitId())[i]; //Default modifiers
            //If both parents are valid, check their base modifiers and assets/flaws
            //Then, check the grandparents IDs before getting their stats
            if (father >= 0 && father <= 0x38 && mother >= 0 && mother <= 0x38) {
                mods[i] += getBaseModif(block.parentId(0))[i] //Father modifiers
                        + getBaseModif(block.parentId(1))[i] //Mother modifiers
                        + calcAssetFlaw(block.asset(0), block.flaw(0))[i] //Father assets
                        + calcAssetFlaw(block.asset(1), block.flaw(1))[i]; //Mother assets
                //Check the unit IDs of the grandparents
                int fatherGrandpa = block.parentId(2);
                int fatherGrandma = block.parentId(3);
                int motherGrandpa = block.parentId(4);
                int motherGrandma = block.parentId(5);
                if (fatherGrandpa >= 0 && fatherGrandpa <= 0x38 && fatherGrandma >= 0 && fatherGrandma <= 0x38) {
                    mods[i] += getBaseModif(fatherGrandpa)[i] //Father's father modifiers
                            + getBaseModif(fatherGrandma)[i] //Father's mother modifiers
                            + calcAssetFlaw(block.asset(2), block.flaw(2))[i] //Father's father assets
                            + calcAssetFlaw(block.asset(3), block.flaw(4))[i]; //Father's mother assets
                }
                if (motherGrandpa >= 0 && motherGrandpa <= 0x38 && motherGrandma >= 0 && motherGrandma <= 0x38) {
                    mods[i] += getBaseModif(motherGrandpa)[i] //Mother's father modifiers
                            + getBaseModif(motherGrandpa)[i] //Mother's mother modifiers
                            + calcAssetFlaw(block.asset(4), block.flaw(3))[i] //Mother's father assets
                            + calcAssetFlaw(block.asset(5), block.flaw(5))[i]; //Mother's mother assets
                }
                //Extra +1 if none of the parents are child units, hardcoded by the game
                if (!isFatherChild && !isMotherChild) mods[i] += 1;
            }
        }
        mods[0] = 0; //Fix HP modifier to 0
        return mods;
    }

    private static int[] calcModifLog(Unit unit) {
        int[] mods = calcAssetFlaw(unit.rawLog.getAssetFlaw()[0], unit.rawLog.getAssetFlaw()[1]);
        int[] baseModif = getBaseModif(unit.rawBlock1.unitId());
        //The base modifiers and the assets and flaws are added
        for (int i = 0; i < mods.length; i++) {
            mods[i] += baseModif[i];
        }
        return mods;
    }

    /*
    Calculates the modifiers based on an asset and flaw
     */
    private static int[] calcAssetFlaw(int asset, int flaw) {
        int[] mods = new int[8];
        for (int i = 0; i < mods.length; i++) {
            mods[i] = (getAssets(asset)[i]) - (getFlaws(flaw)[i]);
        }
        return mods;
    }

    /*
    Retrieve methods
     */
    public static int[] getBaseModif(int id) {
        //Returns modifiers 0 if the unit is not normal
        if (id >= modifParents().size() || id < 0) return modifParents().get(0);
        else return modifParents().get(id);
    }

    public static int[] getUnitAddition(int id) {
        //Return additions 0 if the unit is not normal
        if (id >= unitAdditions().size() || id < 0) return unitAdditions().get(0x35);
        else return unitAdditions().get(id);
    }

    public static int[] getAssets(int id) {
        if (id >= assets().size() || id < 0) return assets().get(0x0);
        else return assets().get(id);
    }

    public static int[] getFlaws(int id) {
        if (id >= flaws().size() || id < 0) return flaws().get(0x0);
        else return flaws().get(id);
    }

    public static int[] getClassMax(int id) {
        if (id >= classMax().size() || id < 0) return classMax().get(0x52);
        else return classMax().get(id);
    }

    public static int[] getClassAddition(int id) {
        if (id >= classAdditions().size() || id < 0) return classAdditions().get(0x52);
        else return classAdditions().get(id);
    }

    public static int getMoveTotal(Unit unit) {
        int move = getMoveClass(unit.rawBlock1.unitClass()) + getMoveBuff(unit) + unit.rawBlock1.movement();
        if (move > 255) move-= 256;
        return move;
    }

    public static int getMoveBuff(Unit unit) {
        int buff = 0;
        int[] activeSkills = unit.rawBlock2.getCurrentSkills();
        if ((unit.rawFlags.bytes[0x25] & 0xFF) > 0) buff += 1; // Unused Movement+1
        if ((unit.rawFlags.bytes[0x26] & 0xFF) > 0) buff += 1; // Unused All Stats +4, Movement+1 (No HP)
        for (int value : activeSkills) if (value == 11) buff += 1; //Skill Movement +1
        return buff;
    }

    private static int getMoveClass(int unitClass) {
        if (unitClass >= classMove.length) return 5;
        else return classMove[unitClass];
    }

    private static final int[] classMove = new int[]{
            5, 5, 6, 6, 5, 5, 6, 6, 7, 7,
            4, 4, 8, 8, 7, 7, 5, 5, 5, 5,
            5, 5, 5, 5, 6, 6, 6, 6, 8, 8,
            6, 6, 5, 5, 5, 5, 6, 6, 6, 6,
            6, 6, 7, 8, 8, 7, 7, 8, 8, 8,
            8, 7, 5, 5, 5, 5, 5, 5, 8, 6,
            6, 6, 6, 8, 8, 6, 6, 5, 6, 6,
            6, 5, 5, 5, 5, 6, 8, 6, 0, 0,
            6, 6, 5
    };

    /*
    Modifiers of each Non-Avatar & Logbook Unit
     */
    private static HashMap<Integer, int[]> modifParents() {
        HashMap<Integer, int[]> chars = new HashMap<Integer, int[]>();
        chars.put(0x00, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Avatar M
        chars.put(0x01, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Avatar F
        chars.put(0x02, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Logbook Unit
        chars.put(0x03, new int[]{0, 1, 0, 1, 1, 1, -1, -1}); //Chrom
        chars.put(0x04, new int[]{0, -2, 2, -1, 0, 2, -1, 1}); //Lissa
        chars.put(0x05, new int[]{0, 2, -2, 2, -2, 0, 2, 0}); //Frederick
        chars.put(0x06, new int[]{0, 0, 0, 2, 2, -1, -2, 0}); //Virion
        chars.put(0x07, new int[]{0, -1, -1, 2, 2, 0, -1, 0}); //Sully
        chars.put(0x08, new int[]{0, 3, -2, 1, 1, -1, 0, -2}); //Vaike
        chars.put(0x09, new int[]{0, 2, -1, 1, 0, -2, 2, -1}); //Stahl
        chars.put(0x0A, new int[]{0, -2, 3, 1, 1, 0, -2, 0}); //Miriel
        chars.put(0x0B, new int[]{0, 1, 0, 1, -2, -2, 3, 0}); //Kellam
        chars.put(0x0C, new int[]{0, -2, 0, 2, 3, 0, -2, 1}); //Sumia
        chars.put(0x0D, new int[]{0, 0, 0, 3, 3, 0, -2, -2}); //Lon'qu
        chars.put(0x0E, new int[]{0, -1, 2, 0, 0, 1, -1, 0}); //Ricken
        chars.put(0x0F, new int[]{0, -3, 2, 1, 0, 3, -3, 2}); //Maribelle
        chars.put(0x10, new int[]{0, 2, -1, 2, 3, -1, 1, -1}); //Panne
        chars.put(0x11, new int[]{0, 1, -1, 2, 2, -2, -1, 0}); //Gaius
        chars.put(0x12, new int[]{0, 1, -1, 2, 2, -1, 0, -1}); //Cordelia
        chars.put(0x13, new int[]{0, 2, -1, 2, 0, -1, 1, -2}); //Gregor
        chars.put(0x14, new int[]{0, 1, 1, -1, -2, 1, 3, 2}); //Nowi
        chars.put(0x15, new int[]{0, 0, 1, 1, 0, -1, 0, 1}); //Libra
        chars.put(0x16, new int[]{0, 0, 3, -1, 1, -3, 1, 0}); //Tharja
        chars.put(0x17, new int[]{0, 0, 0, 1, 1, 0, -1, -1}); //Olivia
        chars.put(0x18, new int[]{0, 3, 0, -1, -1, 0, 2, -2}); //Cherche
        chars.put(0x19, new int[]{0, 1, 1, 2, 0, -2, 1, -1}); //Henry
        chars.put(0x1A, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Lucina
        chars.put(0x1B, new int[]{0, 1, -1, 1, 1, -1, 0, 1}); //Say'ri
        chars.put(0x1C, new int[]{0, 3, -2, 1, 1, -1, 1, -1}); //Basilio
        chars.put(0x1D, new int[]{0, 1, -1, 2, 1, 0, -1, 0}); //Flavia
        chars.put(0x1E, new int[]{0, 1, -1, -1, -1, 3, 1, -1}); //Donnel
        chars.put(0x1F, new int[]{0, -1, 0, 1, 0, 3, -1, 0}); //Anna
        chars.put(0x20, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Owain
        chars.put(0x21, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Inigo
        chars.put(0x22, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Brady
        chars.put(0x23, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Kjelle
        chars.put(0x24, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Cynthia
        chars.put(0x25, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Severa
        chars.put(0x26, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Gerome
        chars.put(0x27, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Morgan M
        chars.put(0x28, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Morgan F
        chars.put(0x29, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Yarne
        chars.put(0x2A, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Laurent
        chars.put(0x2B, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Noire
        chars.put(0x2C, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Nah
        chars.put(0x2D, new int[]{0, 0, -1, 0, 1, 2, 1, 2}); //Tiki
        chars.put(0x2E, new int[]{0, -2, 0, 3, 3, -1, -1, 0}); //Gangrel
        chars.put(0x2F, new int[]{0, 4, -2, 0, -1, -1, 4, -2}); //Walhart
        chars.put(0x30, new int[]{0, -2, 4, 0, 1, 0, -2, 1}); //Emmeryn
        chars.put(0x31, new int[]{0, 1, -2, 2, 4, 0, -1, -2}); //Yen'fay
        chars.put(0x32, new int[]{0, -1, 3, 1, 1, -2, 0, 0}); //Aversa
        chars.put(0x33, new int[]{0, 3, -2, 1, 0, 0, 2, -2}); //Priam
        chars.put(0x34, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Marth
        chars.put(0x35, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Maiden - Dummy
        chars.put(0x36, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Monster (Unpromoted)
        chars.put(0x37, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Monster (Promoted)
        chars.put(0x38, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Merchant
        return chars;
    }

    private static HashMap<Integer, int[]> unitAdditions() {
        HashMap<Integer, int[]> chars = new HashMap<Integer, int[]>();
        chars.put(0x00, new int[]{3, 2, 2, 0, 1, 4, 1, 1}); //Avatar M
        chars.put(0x01, new int[]{3, 2, 2, 0, 1, 4, 1, 1}); //Avatar F
        chars.put(0x02, new int[]{3, 2, 2, 0, 1, 4, 1, 1}); //Logbook Unit
        chars.put(0x03, new int[]{2, 1, 1, 3, 1, 5, 0, 1}); //Chrom
        chars.put(0x04, new int[]{1, 1, 2, 2, 0, 8, 2, -2}); //Lissa
        chars.put(0x05, new int[]{2, 2, 2, 6, 5, 6, 0, 2}); //Frederick
        chars.put(0x06, new int[]{3, 1, 0, 1, -1, 7, 1, 1}); //Virion
        chars.put(0x07, new int[]{2, 1, 1, 3, 2, 6, 0, 2}); //Sully
        chars.put(0x08, new int[]{4, 1, 0, 3, 1, 4, 1, 0}); //Vaike
        chars.put(0x09, new int[]{4, 2, 0, 2, 0, 5, 1, 1}); //Stahl
        chars.put(0x0A, new int[]{2, 0, 2, 2, 3, 6, 1, 1}); //Miriel
        chars.put(0x0B, new int[]{3, 2, 0, 3, 3, 3, 1, 2}); //Kellam
        chars.put(0x0C, new int[]{2, 2, 1, 4, 3, 8, 1, 1}); //Sumia
        chars.put(0x0D, new int[]{4, 2, 0, 3, 3, 7, 3, 1}); //Lon'qu
        chars.put(0x0E, new int[]{4, 3, 4, 3, 1, 10, 4, 0}); //Ricken
        chars.put(0x0F, new int[]{2, 0, 2, 2, 1, 5, 2, 1}); //Maribelle
        chars.put(0x10, new int[]{10, 6, 1, 5, 5, 8, 4, 1}); //Panne
        chars.put(0x11, new int[]{6, 4, 0, 7, 7, 6, 3, 2}); //Gaius
        chars.put(0x12, new int[]{9, 5, 1, 6, 4, 9, 4, 2}); //Cordelia
        chars.put(0x13, new int[]{12, 7, 0, 5, 4, 8, 5, 2}); //Gregor
        chars.put(0x14, new int[]{0, 2, 0, 1, 2, 8, 0, 0}); //Nowi
        chars.put(0x15, new int[]{14, 9, 10, 8, 7, 10, 5, 10}); //Libra
        chars.put(0x16, new int[]{7, 3, 8, 3, 9, 3, 6, 3}); //Tharja
        chars.put(0x17, new int[]{2, 2, 0, 3, 1, 5, 0, 1}); //Olivia
        chars.put(0x18, new int[]{11, 7, 1, 6, 6, 8, 7, 2}); //Cherche
        chars.put(0x19, new int[]{10, 5, 10, 12, 5, 10, 8, 1}); //Henry
        chars.put(0x1A, new int[]{12, 5, 1, 8, 4, 13, 3, 3}); //Lucina
        chars.put(0x1B, new int[]{19, 10, 5, 12, 13, 20, 6, 6}); //Say'ri
        chars.put(0x1C, new int[]{28, 18, 3, 17, 14, 18, 13, 5}); //Basilio
        chars.put(0x1D, new int[]{26, 17, 4, 17, 16, 21, 15, 8}); //Flavia
        chars.put(0x1E, new int[]{0, 3, 0, 1, 2, 11, 2, 0}); //Donnel
        chars.put(0x1F, new int[]{16, 8, 13, 12, 10, 25, 5, 5}); //Anna
        chars.put(0x20, new int[]{10, 4, 4, 5, 6, 9, 6, 5}); //Owain
        chars.put(0x21, new int[]{11, 5, 2, 4, 9, 12, 4, 4}); //Inigo
        chars.put(0x22, new int[]{9, 6, 5, 3, 2, 10, 7, 4}); //Brady
        chars.put(0x23, new int[]{10, 6, 2, 6, 5, 11, 3, 3}); //Kjelle
        chars.put(0x24, new int[]{7, 5, 2, 4, 10, 17, 6, 6}); //Cynthia
        chars.put(0x25, new int[]{8, 6, 1, 7, 6, 6, 6, 5}); //Severa
        chars.put(0x26, new int[]{13, 8, 0, 4, 8, 5, 5, 1}); //Gerome
        chars.put(0x27, new int[]{9, 6, 8, 7, 6, 7, 3, 7}); //Morgan M
        chars.put(0x28, new int[]{9, 6, 8, 7, 6, 7, 3, 7}); //Morgan F
        chars.put(0x29, new int[]{16, 9, 1, 4, 4, 13, 6, 1}); //Yarne
        chars.put(0x2A, new int[]{10, 3, 7, 7, 4, 11, 4, 5}); //Laurent
        chars.put(0x2B, new int[]{8, 5, 3, 4, 7, 10, 4, 6}); //Noire
        chars.put(0x2C, new int[]{5, 3, 3, 5, 6, 8, 3, 3}); //Nah
        chars.put(0x2D, new int[]{21, 16, 10, 13, 15, 18, 13, 10}); //Tiki
        chars.put(0x2E, new int[]{30, 17, 16, 19, 22, 15, 15, 12}); //Gangrel
        chars.put(0x2F, new int[]{47, 29, 13, 24, 24, 30, 23, 14}); //Walhart
        chars.put(0x30, new int[]{22, 4, 19, 18, 18, 13, 8, 15}); //Emmeryn
        chars.put(0x31, new int[]{40, 23, 3, 28, 27, 28, 15, 14}); //Yen'fay
        chars.put(0x32, new int[]{36, 19, 25, 21, 22, 26, 16, 19}); //Aversa
        chars.put(0x33, new int[]{52, 32, 2, 33, 28, 35, 31, 22}); //Priam
        chars.put(0x34, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Marth
        chars.put(0x35, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //Maiden - Dummy
        chars.put(0x36, new int[]{0, 0, 0, 0, 0, 0, -2, 0}); //Monster (Unpromoted)
        chars.put(0x37, new int[]{0, 0, 0, 0, 0, 0, -1, 0}); //Monster (Promoted)
        chars.put(0x38, new int[]{0, 0, 0, 0, 0, 2, 0, 0}); //Merchant
        return chars;
    }

    /*
    Asset Modifiers
     */
    private static HashMap<Integer, int[]> assets() {
        HashMap<Integer, int[]> chars = new HashMap<Integer, int[]>();
        chars.put(0x0, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //None
        chars.put(0x1, new int[]{0, 1, 1, 0, 0, 2, 2, 2}); //HP
        chars.put(0x2, new int[]{0, 4, 0, 2, 0, 0, 2, 0}); //Str
        chars.put(0x3, new int[]{0, 0, 4, 0, 2, 0, 0, 2}); //Magic
        chars.put(0x4, new int[]{0, 2, 0, 4, 0, 0, 2, 0}); //Skill
        chars.put(0x5, new int[]{0, 0, 0, 2, 4, 2, 0, 0}); //Speed
        chars.put(0x6, new int[]{0, 2, 2, 0, 0, 4, 0, 0}); //Luck
        chars.put(0x7, new int[]{0, 0, 0, 0, 0, 2, 4, 2}); //Def
        chars.put(0x8, new int[]{0, 0, 2, 0, 2, 0, 0, 4}); //Res
        return chars;
    }

    /*
    Flaw Modifiers
     */
    private static HashMap<Integer, int[]> flaws() {
        HashMap<Integer, int[]> chars = new HashMap<Integer, int[]>();
        chars.put(0x0, new int[]{0, 0, 0, 0, 0, 0, 0, 0}); //None
        chars.put(0x1, new int[]{0, 1, 1, 0, 0, 1, 1, 1}); //HP
        chars.put(0x2, new int[]{0, 3, 0, 1, 0, 0, 1, 0}); //Str
        chars.put(0x3, new int[]{0, 0, 3, 0, 1, 0, 0, 1}); //Magic
        chars.put(0x4, new int[]{0, 1, 0, 3, 0, 0, 1, 0}); //Skill
        chars.put(0x5, new int[]{0, 0, 0, 1, 3, 1, 0, 0}); //Speed
        chars.put(0x6, new int[]{0, 1, 1, 0, 0, 3, 0, 0}); //Luck
        chars.put(0x7, new int[]{0, 0, 0, 0, 0, 1, 3, 1}); //Def
        chars.put(0x8, new int[]{0, 0, 1, 0, 1, 0, 0, 3}); //Res
        return chars;
    }

    /*
    Class modifiers
     */
    private static HashMap<Integer, int[]> classAdditions() {
        HashMap<Integer, int[]> chars = new HashMap<Integer, int[]>();
        chars.put(0x00, new int[]{18, 6, 0, 5, 7, 0, 7, 0}); //Lord M
        chars.put(0x01, new int[]{16, 5, 1, 6, 20, 0, 10, 5}); //Lord F
        chars.put(0x02, new int[]{23, 10, 0, 7, 9, 0, 10, 3}); //Great Lord M
        chars.put(0x03, new int[]{20, 8, 1, 9, 11, 0, 8, 4}); //Great Lord F
        chars.put(0x04, new int[]{16, 4, 3, 5, 5, 0, 5, 3}); //Tactician M
        chars.put(0x05, new int[]{16, 4, 3, 5, 5, 0, 5, 3}); //Tactician F
        chars.put(0x06, new int[]{20, 7, 6, 7, 7, 0, 7, 5}); //Grandmaster M
        chars.put(0x07, new int[]{20, 7, 6, 7, 7, 0, 7, 5}); //Grandmaster F
        chars.put(0x08, new int[]{18, 6, 0, 5, 6, 0, 7, 0}); //Cavalier M
        chars.put(0x09, new int[]{18, 6, 0, 5, 6, 0, 7, 0}); //Cavalier F
        chars.put(0x0A, new int[]{18, 8, 0, 4, 2, 0, 11, 0}); //Knight M
        chars.put(0x0B, new int[]{18, 8, 0, 4, 2, 0, 11, 0}); //Knight F
        chars.put(0x0C, new int[]{25, 9, 1, 7, 8, 0, 10, 6}); //Paladin M
        chars.put(0x0D, new int[]{25, 9, 1, 7, 8, 0, 10, 6}); //Paladin F
        chars.put(0x0E, new int[]{26, 11, 0, 6, 5, 0, 14, 1}); //Great Knight M
        chars.put(0x0F, new int[]{26, 11, 0, 6, 5, 0, 14, 1}); //Great Knight F
        chars.put(0x10, new int[]{28, 12, 0, 7, 4, 0, 15, 3}); //General M
        chars.put(0x11, new int[]{28, 12, 0, 7, 4, 0, 15, 3}); //General F
        chars.put(0x12, new int[]{22, 8, 0, 3, 8, 0, 3, 0}); //Barbarian
        chars.put(0x13, new int[]{20, 8, 0, 5, 5, 0, 4, 0}); //Fighter
        chars.put(0x14, new int[]{18, 5, 0, 8, 7, 0, 5, 0}); //Mercenary M
        chars.put(0x15, new int[]{18, 5, 0, 8, 7, 0, 5, 0}); //Mercenary F
        chars.put(0x16, new int[]{16, 5, 0, 8, 6, 0, 5, 0}); //Archer M
        chars.put(0x17, new int[]{16, 5, 0, 8, 6, 0, 5, 0}); //Archer F
        chars.put(0x18, new int[]{30, 13, 0, 5, 11, 0, 5, 1}); //Berserker
        chars.put(0x19, new int[]{28, 12, 0, 8, 7, 0, 7, 3}); //Warrior
        chars.put(0x1A, new int[]{22, 8, 1, 11, 10, 0, 8, 3}); //Hero M
        chars.put(0x1B, new int[]{22, 8, 1, 11, 10, 0, 8, 3}); //Hero F
        chars.put(0x1C, new int[]{24, 8, 0, 10, 10, 0, 6, 2}); //Bow Knight M
        chars.put(0x1D, new int[]{24, 8, 0, 10, 10, 0, 6, 2}); //Bow Knight F
        chars.put(0x1E, new int[]{20, 7, 1, 12, 9, 0, 10, 3}); //Sniper M
        chars.put(0x1F, new int[]{20, 7, 1, 12, 9, 0, 10, 3}); //Sniper F
        chars.put(0x20, new int[]{16, 4, 1, 9, 10, 0, 4, 1}); //Myrmidon M
        chars.put(0x21, new int[]{16, 4, 1, 9, 10, 0, 4, 1}); //Myrmidon F
        chars.put(0x22, new int[]{16, 3, 0, 6, 8, 0, 2, 0}); //Thief M
        chars.put(0x23, new int[]{16, 3, 0, 6, 8, 0, 2, 0}); //Thief F
        chars.put(0x24, new int[]{20, 7, 2, 11, 13, 0, 6, 4}); //Swordmaster M
        chars.put(0x25, new int[]{20, 7, 2, 11, 13, 0, 6, 4}); //Swordmaster F
        chars.put(0x26, new int[]{21, 8, 0, 13, 12, 0, 5, 1}); //Assassin M
        chars.put(0x27, new int[]{21, 8, 0, 13, 12, 0, 5, 1}); //Assassin F
        chars.put(0x28, new int[]{19, 4, 4, 10, 11, 0, 3, 5}); //Trickster M
        chars.put(0x29, new int[]{19, 4, 4, 10, 11, 0, 3, 5}); //Trickster F
        chars.put(0x2A, new int[]{16, 4, 2, 7, 8, 0, 4, 6}); //Pegasus Knight
        chars.put(0x2B, new int[]{20, 6, 3, 10, 11, 0, 6, 9}); //Falcon Knight
        chars.put(0x2C, new int[]{19, 5, 6, 8, 10, 0, 5, 9}); //Dark Flier
        chars.put(0x2D, new int[]{19, 7, 0, 6, 5, 0, 8, 0}); //Wyvern Rider M
        chars.put(0x2E, new int[]{19, 7, 0, 6, 5, 0, 8, 0}); //Wyvern Rider F
        chars.put(0x2F, new int[]{24, 11, 0, 8, 7, 0, 11, 3}); //Wyvern Lord M
        chars.put(0x30, new int[]{24, 11, 0, 8, 7, 0, 11, 3}); //Wyvern Lord F
        chars.put(0x31, new int[]{22, 9, 0, 10, 9, 0, 8, 3}); //Griffon Rider M
        chars.put(0x32, new int[]{22, 9, 0, 10, 9, 0, 8, 3}); //Griffon Rider F
        chars.put(0x33, new int[]{16, 0, 3, 2, 5, 0, 1, 5}); //Troubadour
        chars.put(0x34, new int[]{16, 0, 3, 2, 4, 0, 1, 6}); //Priest
        chars.put(0x35, new int[]{16, 0, 3, 2, 4, 0, 1, 6}); //Cleric
        chars.put(0x36, new int[]{16, 0, 4, 3, 4, 0, 2, 3}); //Mage M
        chars.put(0x37, new int[]{16, 0, 4, 3, 4, 0, 2, 3}); //Mage F
        chars.put(0x38, new int[]{18, 1, 3, 2, 3, 0, 4, 4}); //Dark Mage M
        chars.put(0x39, new int[]{18, 1, 3, 2, 3, 0, 4, 4}); //Dark Mage F
        chars.put(0x3A, new int[]{19, 0, 5, 4, 8, 0, 3, 8}); //Valkyrie
        chars.put(0x3B, new int[]{24, 5, 5, 4, 6, 0, 6, 6}); //War Monk
        chars.put(0x3C, new int[]{24, 5, 5, 4, 6, 0, 6, 6}); //War Cleric
        chars.put(0x3D, new int[]{20, 1, 7, 5, 7, 0, 4, 5}); //Sage M
        chars.put(0x3E, new int[]{20, 1, 7, 5, 7, 0, 4, 5}); //Sage F
        chars.put(0x3F, new int[]{25, 4, 5, 6, 5, 0, 9, 5}); //Dark Knight M
        chars.put(0x40, new int[]{25, 4, 5, 6, 5, 0, 9, 5}); //Dark Knight F
        chars.put(0x41, new int[]{23, 2, 6, 4, 4, 0, 7, 7}); //Sorcerer M
        chars.put(0x42, new int[]{23, 2, 6, 4, 4, 0, 7, 7}); //Sorcerer F
        chars.put(0x43, new int[]{16, 1, 1, 5, 8, 0, 3, 1}); //Dancer
        chars.put(0x44, new int[]{18, 2, 0, 1, 1, 0, 2, 2}); //Manakete
        chars.put(0x45, new int[]{18, 3, 0, 4, 4, 0, 4, 1}); //Taguel M
        chars.put(0x46, new int[]{18, 3, 0, 4, 4, 0, 4, 1}); //Taguel F
        chars.put(0x47, new int[]{16, 3, 0, 3, 3, 0, 3, 0}); //Soldier
        chars.put(0x48, new int[]{16, 1, 0, 1, 1, 0, 1, 0}); //Villager
        chars.put(0x49, new int[]{18, 3, 0, 2, 2, 0, 4, 1}); //Merchant
        chars.put(0x4A, new int[]{18, 3, 0, 0, 0, 0, 0, 0}); //Reverant
        chars.put(0x4B, new int[]{20, 3, 1, 2, 2, 0, 2, 1}); //Entombed
        chars.put(0x4C, new int[]{24, 10, 2, 9, 8, 0, 12, 5}); //Conqueror
        chars.put(0x4D, new int[]{21, 9, 1, 10, 10, 0, 8, 4}); //Lodestar
        chars.put(0x4E, new int[]{30, 15, 15, 15, 15, -2, 15, 10}); //Grima
        chars.put(0x4F, new int[]{1, 0, 0, 0, 0, 0, 0, 0}); //Mirage
        chars.put(0x50, new int[]{22, 8, 4, 8, 9, 0, 7, 10}); //Dread Fighter
        chars.put(0x51, new int[]{21, 7, 6, 11, 10, 0, 7, 6}); //Bride
        chars.put(0x52, new int[]{16, 0, 0, 0, 0, 0, 0, 0}); //Dummy
        return chars;
    }

    /*
    Max values of each class
    Add Unit Modifier to get max stats for a unit
     */
    private static HashMap<Integer, int[]> classMax() {
        HashMap<Integer, int[]> chars = new HashMap<Integer, int[]>();
        chars.put(0x00, new int[]{60, 27, 20, 25, 26, 30, 26, 25}); //Lord M
        chars.put(0x01, new int[]{60, 25, 20, 26, 28, 30, 25, 25}); //Lord F
        chars.put(0x02, new int[]{80, 43, 30, 40, 41, 45, 42, 40}); //Great Lord M
        chars.put(0x03, new int[]{80, 40, 30, 42, 44, 45, 40, 40}); //Great Lord F
        chars.put(0x04, new int[]{60, 25, 25, 25, 25, 30, 25, 25}); //Tactician M
        chars.put(0x05, new int[]{60, 25, 25, 25, 25, 30, 25, 25}); //Tactician F
        chars.put(0x06, new int[]{80, 40, 40, 40, 40, 45, 40, 40}); //Grandmaster M
        chars.put(0x07, new int[]{80, 40, 40, 40, 40, 45, 40, 40}); //Grandmaster F
        chars.put(0x08, new int[]{60, 26, 20, 25, 25, 30, 26, 26}); //Cavalier M
        chars.put(0x09, new int[]{60, 26, 20, 25, 25, 30, 26, 26}); //Cavalier F
        chars.put(0x0A, new int[]{60, 30, 20, 26, 23, 30, 30, 22}); //Knight M
        chars.put(0x0B, new int[]{60, 30, 20, 26, 23, 30, 30, 22}); //Knight F
        chars.put(0x0C, new int[]{80, 42, 30, 40, 40, 45, 42, 42}); //Paladin M
        chars.put(0x0D, new int[]{80, 42, 30, 40, 40, 45, 42, 42}); //Paladin F
        chars.put(0x0E, new int[]{80, 48, 20, 34, 37, 45, 48, 30}); //Great Knight M
        chars.put(0x0F, new int[]{80, 48, 20, 34, 37, 45, 48, 30}); //Great Knight F
        chars.put(0x10, new int[]{80, 50, 30, 41, 35, 45, 50, 35}); //General M
        chars.put(0x11, new int[]{80, 50, 30, 41, 35, 45, 50, 35}); //General F
        chars.put(0x12, new int[]{60, 30, 20, 23, 27, 30, 22, 20}); //Barbarian
        chars.put(0x13, new int[]{60, 29, 20, 26, 25, 30, 25, 23}); //Fighter
        chars.put(0x14, new int[]{60, 26, 20, 28, 26, 30, 25, 23}); //Mercenary M
        chars.put(0x15, new int[]{60, 26, 20, 28, 26, 30, 25, 23}); //Mercenary F
        chars.put(0x16, new int[]{60, 26, 20, 29, 25, 30, 25, 21}); //Archer M
        chars.put(0x17, new int[]{60, 26, 20, 29, 25, 30, 25, 21}); //Archer F
        chars.put(0x18, new int[]{80, 50, 30, 35, 44, 45, 34, 30}); //Berserker
        chars.put(0x19, new int[]{80, 48, 30, 42, 40, 45, 40, 35}); //Warrior
        chars.put(0x1A, new int[]{80, 42, 30, 46, 42, 45, 40, 36}); //Hero M
        chars.put(0x1B, new int[]{80, 42, 30, 46, 42, 45, 40, 36}); //Hero F
        chars.put(0x1C, new int[]{80, 40, 30, 43, 41, 45, 35, 30}); //Bow Knight M
        chars.put(0x1D, new int[]{80, 40, 30, 43, 41, 45, 35, 30}); //Bow Knight F
        chars.put(0x1E, new int[]{80, 41, 30, 48, 40, 45, 40, 31}); //Sniper M
        chars.put(0x1F, new int[]{80, 41, 30, 48, 40, 45, 40, 31}); //Sniper F
        chars.put(0x20, new int[]{60, 24, 22, 27, 28, 30, 22, 24}); //Myrmidon M
        chars.put(0x21, new int[]{60, 24, 22, 27, 28, 30, 22, 24}); //Myrmidon F
        chars.put(0x22, new int[]{60, 22, 20, 30, 28, 30, 21, 20}); //Thief M
        chars.put(0x23, new int[]{60, 22, 20, 30, 28, 30, 21, 20}); //Thief F
        chars.put(0x24, new int[]{80, 38, 34, 44, 46, 45, 33, 38}); //Swordmaster M
        chars.put(0x25, new int[]{80, 38, 34, 44, 46, 45, 33, 38}); //Swordmaster F
        chars.put(0x26, new int[]{80, 40, 30, 48, 46, 45, 31, 30}); //Assassin M
        chars.put(0x27, new int[]{80, 40, 30, 48, 46, 45, 31, 30}); //Assassin F
        chars.put(0x28, new int[]{80, 35, 38, 45, 43, 45, 30, 40}); //Trickster M
        chars.put(0x29, new int[]{80, 35, 38, 45, 43, 45, 30, 40}); //Trickster F
        chars.put(0x2A, new int[]{60, 24, 23, 28, 27, 30, 22, 25}); //Pegasus Knight
        chars.put(0x2B, new int[]{80, 38, 35, 45, 44, 45, 33, 40}); //Falcon Knight
        chars.put(0x2C, new int[]{80, 36, 42, 41, 42, 45, 32, 41}); //Dark Flier
        chars.put(0x2D, new int[]{60, 28, 20, 24, 24, 30, 28, 20}); //Wyvern Rider M
        chars.put(0x2E, new int[]{60, 28, 20, 24, 24, 30, 28, 20}); //Wyvern Rider F
        chars.put(0x2F, new int[]{80, 46, 30, 38, 38, 45, 46, 30}); //Wyvern Lord M
        chars.put(0x30, new int[]{80, 46, 30, 38, 38, 45, 46, 30}); //Wyvern Lord F
        chars.put(0x31, new int[]{80, 40, 30, 43, 41, 45, 40, 30}); //Griffon Rider M
        chars.put(0x32, new int[]{80, 40, 30, 43, 41, 45, 40, 30}); //Griffon Rider F
        chars.put(0x33, new int[]{60, 20, 26, 24, 26, 30, 20, 28}); //Troubadour
        chars.put(0x34, new int[]{60, 22, 25, 24, 25, 30, 22, 27}); //Priest
        chars.put(0x35, new int[]{60, 22, 25, 24, 25, 30, 22, 27}); //Cleric
        chars.put(0x36, new int[]{60, 20, 28, 27, 26, 30, 21, 25}); //Mage M
        chars.put(0x37, new int[]{60, 20, 28, 27, 26, 30, 21, 25}); //Mage F
        chars.put(0x38, new int[]{60, 20, 27, 25, 25, 30, 25, 27}); //Dark Mage M
        chars.put(0x39, new int[]{60, 20, 27, 25, 25, 30, 25, 27}); //Dark Mage F
        chars.put(0x3A, new int[]{80, 30, 42, 38, 43, 45, 30, 45}); //Valkyrie
        chars.put(0x3B, new int[]{80, 40, 40, 38, 41, 45, 38, 43}); //War Monk
        chars.put(0x3C, new int[]{80, 40, 40, 38, 41, 45, 38, 43}); //War Cleric
        chars.put(0x3D, new int[]{80, 30, 46, 43, 42, 45, 31, 40}); //Sage M
        chars.put(0x3E, new int[]{80, 30, 46, 43, 42, 45, 31, 40}); //Sage F
        chars.put(0x3F, new int[]{80, 38, 41, 40, 40, 45, 42, 38}); //Dark Knight M
        chars.put(0x40, new int[]{80, 38, 41, 40, 40, 45, 42, 38}); //Dark Knight F
        chars.put(0x41, new int[]{80, 30, 44, 38, 40, 45, 41, 44}); //Sorcerer M
        chars.put(0x42, new int[]{80, 30, 44, 38, 40, 45, 41, 44}); //Sorcerer F
        chars.put(0x43, new int[]{80, 30, 30, 40, 40, 45, 30, 30}); //Dancer
        chars.put(0x44, new int[]{80, 40, 35, 35, 35, 45, 40, 40}); //Manakete
        chars.put(0x45, new int[]{80, 35, 30, 40, 40, 45, 35, 30}); //Taguel M
        chars.put(0x46, new int[]{80, 35, 30, 40, 40, 45, 35, 30}); //Taguel F
        chars.put(0x47, new int[]{80, 30, 30, 30, 30, 45, 30, 30}); //Soldier
        chars.put(0x48, new int[]{60, 20, 20, 20, 20, 30, 20, 20}); //Villager
        chars.put(0x49, new int[]{60, 20, 20, 20, 20, 30, 20, 20}); //Merchant
        chars.put(0x4A, new int[]{80, 30, 20, 25, 25, 30, 30, 30}); //Reverant
        chars.put(0x4B, new int[]{80, 40, 30, 35, 35, 45, 35, 30}); //Entombed
        chars.put(0x4C, new int[]{80, 45, 25, 40, 40, 45, 45, 35}); //Conqueror
        chars.put(0x4D, new int[]{80, 41, 30, 43, 43, 45, 41, 40}); //Lodestar
        chars.put(0x4E, new int[]{99, 50, 40, 50, 45, 45, 50, 50}); //Grima
        chars.put(0x4F, new int[]{60, 20, 20, 20, 20, 30, 20, 20}); //Mirage
        chars.put(0x50, new int[]{80, 42, 38, 40, 41, 45, 39, 43}); //Dread Fighter
        chars.put(0x51, new int[]{80, 40, 39, 42, 42, 45, 41, 40}); //Bride
        chars.put(0x52, new int[]{60, 20, 20, 20, 20, 30, 20, 20}); //Dummy
        return chars;
    }

    private static HashMap<Integer, int[]> itemBuffList() {
        //0 HP - 1 STR - 2 MAG - 3 SKL - 4 SPD - 5 LCK - 6 DEF - 7 RES
        HashMap<Integer, int[]> chars = new HashMap<Integer, int[]>();
        chars.put(12, new int[]{0, 0, 0, 3, 0, 0, 0, 0}); //Missletainn
        chars.put(19, new int[]{0, 0, 0, 0, 0, 0, 0, 5}); //Tyrfing
        chars.put(20, new int[]{0, 0, 0, 0, 5, 0, 0, 0}); //Mystletainn
        chars.put(21, new int[]{0, 0, 0, 5, 0, 0, 0, 0}); //Balmung
        chars.put(22, new int[]{0, 0, 0, 0, 0, 0, 0, 5}); //Sol Katti
        chars.put(23, new int[]{0, 0, 0, 0, 0, 0, 5, 0}); //Ragnell
        chars.put(24, new int[]{0, 0, 0, 0, 0, 0, 5, 0}); //Ragnell (Priam)
        chars.put(34, new int[]{0, 0, 0, 0, 2, 0, 0, 2}); //Seliph's Blade
        chars.put(49, new int[]{0, 5, 0, 0, 0, 0, 0, 0}); //Gae Bolg
        chars.put(48, new int[]{0, 0, 5, 0, 0, 0, 0, 0}); //Gungnir
        chars.put(56, new int[]{0, 2, 0, 0, 2, 0, 0, 0}); //Ephraim's Lance
        chars.put(57, new int[]{0, 0, 0, 0, 0, 2, 2, 0}); //Finn's Lance
        chars.put(72, new int[]{0, 0, 0, 0, 0, 0, 5, 0}); //Helswath
        chars.put(73, new int[]{0, 0, 0, 0, 0, 0, 5, 0}); //Armads
        chars.put(81, new int[]{0, 2, 0, 0, 0, 0, 2, 0}); //Hector's Axe
        chars.put(92, new int[]{0, 0, 0, 0, 5, 0, 0, 0}); //Yewfelle
        chars.put(93, new int[]{0, 0, 0, 0, 0, 10, 0, 0}); //Nidhogg
        chars.put(94, new int[]{0, 5, 0, 0, 0, 0, 0, 0}); //Double Bow
        chars.put(106, new int[]{0, 0, 5, 0, 0, 0, 0, 0}); //Valflame
        chars.put(111, new int[]{0, 0, 0, 5, 0, 0, 0, 0}); //Mjnolnir
        chars.put(116, new int[]{0, 0, 0, 0, 5, 0, 0, 0}); //Forseti
        chars.put(118, new int[]{0, 0, 0, 0, 0, 0, 5, 5}); //Book of Naga
        chars.put(127, new int[]{0, 0, 0, 0, 0, 0, 2, 2}); //Micaiah's Pyre
        chars.put(145, new int[]{0, 8, 5, 3, 2, 0, 10, 7}); //Dragonstone
        chars.put(146, new int[]{0, 11, 6, 5, 4, 0, 13, 9}); //Dragonstone +
        chars.put(147, new int[]{0, 3, 0, 5, 5, 4, 1, 0}); //Beaststone
        chars.put(148, new int[]{0, 5, 0, 8, 8, 6, 4, 2}); //Beaststone +
        return chars;
    }
}
