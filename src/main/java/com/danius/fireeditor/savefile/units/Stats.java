package com.danius.fireeditor.savefile.units;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.units.extrablock.ChildBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Stats {

    /*
    Los stats máximos es max de la clase + modificador personaje
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
        unit.maxGrowth();
        int[] maxStats = calcMaxStats(unit, hasLimitBreaker(unit));
        //if (unit.rawSkill.skillString.charAt())
        unit.rawBlock1.setCurrentHp(maxStats[0]); //Updates the current HP to match out
        unit.rawBlock1.setLevelMax(); //Updates the level and experience
        unit.rawBlock2.setMaxWeaponExp(); //Sets the max weapon exp
    }

    public static int[] calcMaxGrowth(Unit unit) {
        //The higher max values classes can reach in vanilla
        int[] maxClass = new int[]{99, 60, 66, 60, 56, 55, 60, 60};
        //The modifiers are added to the higher values
        int[] modif = calcModif(unit);
        for (int i = 0; i < maxClass.length; i++) {
            maxClass[i] += modif[i];
        }
        int[] growths = new int[8];
        int[] unitAddition = FireEditor.unitDb.getAddition(unit.rawBlock1.unitId()); //Hardcoded
        int[] classAddition = FireEditor.classDb.getBase(unit.rawBlock1.unitClass()); //Hardcoded
        int[] currentStats = new int[8];
        //If it has logbook data, +2 on their asset
        if (unit.rawLog != null) {
            int asset = unit.rawLog.getAssetFlaw()[0];
            if (asset != 0) {
                unitAddition[asset - 1] += 2; //-1 bc asset 0 is none, asset 1 is HP, but addition 0 is HP
            }
        }
        //The current stats are calculated
        for (int i = 0; i < currentStats.length; i++) {
            currentStats[i] += unitAddition[i] + classAddition[i] + growths[i];
            if (currentStats[i] > 255) currentStats[i] -= 256; //If higher than the actual limit size
            else if (currentStats[i] > maxClass[i]) currentStats[i] = maxClass[i]; //If it is higher than the limit
        }
        //The growth is updated
        for (int i = 0; i < currentStats.length; i++) {
            if (maxClass[i] > currentStats[i]) {
                int difference = maxClass[i] - currentStats[i];
                growths[i] += difference;
            }
        }
        return growths;
    }

    /*
    Adds all the stats together to calculate the current stats
     */
    public static int[] calcCurrentStats(Unit unit, boolean limitBreak) {
        int[] maxStats = calcMaxStats(unit, limitBreak);
        int[] growths = unit.rawBlock1.growth();
        int[] unitAddition = FireEditor.unitDb.getAddition(unit.rawBlock1.unitId()); //Hardcoded
        int[] classAddition = FireEditor.classDb.getBase(unit.rawBlock1.unitClass()); //Hardcoded
        //If it has logbook data, +2 on their asset
        if (unit.rawLog != null) {
            int asset = unit.rawLog.getAssetFlaw()[0];
            if (asset != 0) {
                unitAddition[asset - 1] += 2; //-1 bc asset 0 is none, asset 1 is HP, but addition 0 is HP
            }
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
        int[] maxClass = FireEditor.classDb.getMax(unit.rawBlock1.unitClass());
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
        //Avatar AND Child Units
        if (unit.rawLog != null && unit.rawChild != null) {
            int[] assets = calcAssetFlaw(unit.rawLog.getAssetFlaw()[0], unit.rawLog.getAssetFlaw()[1]);
            int[] modif = calcModifChild(unit);
            //The child modifiers and the assets and flaws are added (including base modifiers)
            for (int i = 0; i < assets.length; i++) {
                modif[i] += assets[i];
            }
            return modif;
        }
        //Legal units
        else if (unit.rawLog != null) return calcModifLog(unit); //MU & Logbook
        else if (unit.rawChild != null) return calcModifChild(unit); //Child units
        else return FireEditor.unitDb.getModifiers(unit.rawBlock1.unitId());  //Regular units
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
        //Adds ALL the possible extra 18 modifiers
        for (int i = 0; i < mods.length; i++) {
            mods[i] = FireEditor.unitDb.getModifiers(unit.rawBlock1.unitId())[i]; //Default modifiers
            //If both parents are valid, check their base modifiers and assets/flaws
            //Then, check the grandparents IDs before getting their stats
            if (father >= 0 && father <= 0x38 && mother >= 0 && mother <= 0x38) {
                mods[i] += FireEditor.unitDb.getModifiers(block.parentId(0))[i] //Father modifiers
                        + FireEditor.unitDb.getModifiers(block.parentId(1))[i] //Mother modifiers
                        + calcAssetFlaw(block.asset(0), block.flaw(0))[i] //Father assets
                        + calcAssetFlaw(block.asset(1), block.flaw(1))[i]; //Mother assets
                //Check the unit IDs of the grandparents
                int fatherGrandpa = block.parentId(2);
                int fatherGrandma = block.parentId(3);
                int motherGrandpa = block.parentId(4);
                int motherGrandma = block.parentId(5);
                if (fatherGrandpa >= 0 && fatherGrandpa <= 0x38 && fatherGrandma >= 0 && fatherGrandma <= 0x38) {
                    mods[i] += FireEditor.unitDb.getModifiers(fatherGrandpa)[i] //Father's father modifiers
                            + FireEditor.unitDb.getModifiers(fatherGrandma)[i] //Father's mother modifiers
                            + calcAssetFlaw(block.asset(2), block.flaw(2))[i] //Father's father assets
                            + calcAssetFlaw(block.asset(3), block.flaw(4))[i]; //Father's mother assets
                }
                if (motherGrandpa >= 0 && motherGrandpa <= 0x38 && motherGrandma >= 0 && motherGrandma <= 0x38) {
                    mods[i] += FireEditor.unitDb.getModifiers(motherGrandpa)[i] //Mother's father modifiers
                            + FireEditor.unitDb.getModifiers(motherGrandpa)[i] //Mother's mother modifiers
                            + calcAssetFlaw(block.asset(4), block.flaw(3))[i] //Mother's father assets
                            + calcAssetFlaw(block.asset(5), block.flaw(5))[i]; //Mother's mother assets
                }
                //Extra +1 if none of the parents are child units, hardcoded by the game
                mods[i] += 1;
            }
        }
        mods[0] = 0; //Fix HP modifier to 0
        return mods;
    }

    private static int[] calcModifLog(Unit unit) {
        int[] mods = calcAssetFlaw(unit.rawLog.getAssetFlaw()[0], unit.rawLog.getAssetFlaw()[1]);
        int[] baseModif = FireEditor.unitDb.getModifiers(unit.rawBlock1.unitId());
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

    public static int[] getAssets(int id) {
        if (id >= assets().size() || id < 0) return assets().get(0x0);
        else return assets().get(id);
    }

    public static int[] getFlaws(int id) {
        if (id >= flaws().size() || id < 0) return flaws().get(0x0);
        else return flaws().get(id);
    }

    public static int getMoveTotal(Unit unit) {
        int move = FireEditor.classDb.getMove(unit.rawBlock1.unitClass()) + getMoveBuff(unit) + unit.rawBlock1.movement();
        if (move > 255) move -= 256;
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
