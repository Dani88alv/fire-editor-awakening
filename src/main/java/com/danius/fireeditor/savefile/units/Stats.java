package com.danius.fireeditor.savefile.units;

import com.danius.fireeditor.data.ClassDb;
import com.danius.fireeditor.data.ItemDb;
import com.danius.fireeditor.data.model.ClassModel;
import com.danius.fireeditor.data.model.ItemModel;
import com.danius.fireeditor.savefile.units.extrablock.ChildBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.danius.fireeditor.data.ClassDb.*;
import static com.danius.fireeditor.data.UnitDb.getUnitAddition;
import static com.danius.fireeditor.data.UnitDb.getUnitModifiers;

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

        //Regular stat modifier skills
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

    //Refinements are ignored
    public static int[] itemBuff(Unit unit) {
        //Check if the unit has a faire skill equipped
        List<Integer> faireSkills = List.of(48, 49, 50, 51, 52);

        //Check if it has a faire skill
        List<Integer> faireTypes = new ArrayList<>();
        boolean hasFaire = false;
        if (unit.rawBlock2.hasSkillsEquipped(faireSkills)) {
            hasFaire = true;
            faireTypes = ItemDb.faireTypes(unit);
        }

        int[] totalBuffs = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < unit.rawInventory.items.size(); i++) {
            if (unit.rawInventory.items.get(i).equipped()) {

                int id = unit.rawInventory.items.get(i).itemId();
                ItemModel itemModel = ItemDb.getItem(id);
                int weaponType = ItemDb.getItemType(id);

                //If the faire type and weapon type match, retrieve faire buffs
                if (hasFaire && (weaponType == itemModel.getType1() && faireTypes.contains(weaponType))) {
                    totalBuffs = ItemDb.getFaireBuff(id, weaponType);
                }

                //If not, retrieve regular equipped buffs
                else totalBuffs = ItemDb.getItemBuffs(id);
                //Break after finding the first equipped item
                break;
            }
        }

        return totalBuffs;
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
        for (int i = 0; i < 8; i++) {
            if (unit.rawFlags.hasTonicFlag(i)) {
                if (i == 0) buffs[i] += 5;
                else buffs[i] += 2;
            }
        }
        //Barrack flags
        for (int i = 1; i < 8; i++) {
            if (unit.rawFlags.hasBarrackFlag(i)) buffs[i] += 4;
        }

        return buffs;
    }

    public static void setMaxStatsHigh(Unit unit) {
        unit.maxGrowth();
        int[] maxStats = calcMaxStats(unit, hasLimitBreaker(unit));
        //if (unit.rawSkill.skillString.charAt())
        unit.rawBlock1.setCurrentHp(maxStats[0]); //Updates the current HP to match out
        unit.rawBlock1.setLevelMax(); //Updates the level and experience
        unit.rawBlock2.setMaxWeaponExp(); //Sets the max weapon exp
        unit.rawFlags.setHiddenLevel(200); //Hidden Level Cap
    }

    //Calculates the highest growth from a unit based on their class set
    public static int[] calcMaxGrowth(Unit unit) {
        //Main variables
        List<ClassModel> availableClasses = getClassesFromUnit(unit);
        int[] modifiers = calcModif(unit);
        int[] unitAddition = getUnitAddition(unit.rawBlock1.unitId()); //Hardcoded
        //If it has logbook data, +2 on their asset
        if (unit.rawLog != null) {
            int asset = unit.rawLog.getAssetFlaw()[0];
            int flaw = unit.rawLog.getAssetFlaw()[1];
            //Asset/Flaw 0 is None, HP is 1
            if (asset != 0) unitAddition[asset - 1] += 2;
            if (flaw != 0) {
                if (flaw == 6) unitAddition[flaw - 1] -= 2;
                else unitAddition[flaw - 1] -= 1;
            }
        }

        //The growth is calculated
        int[] growths = new int[8];
        for (ClassModel classModel : availableClasses) {
            int[] classBase = classModel.getStatsBase().clone();
            int[] classMax = classModel.getStatsMax().clone();
            for (int i = 0; i < growths.length; i++) if (i != 0) classMax[i] += 10; //Limit breaker
            //The stats are checked
            for (int i = 0; i < growths.length; i++) {
                int statMax = classMax[i] + modifiers[i];
                int statMin = unitAddition[i] + classBase[i];
                int requiredGrowth = statMax - statMin;
                if (requiredGrowth > growths[i]) {
                    growths[i] = requiredGrowth;
                }
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
        int[] unitAddition = getUnitAddition(unit.rawBlock1.unitId()); //Hardcoded
        int[] classAddition = getClassBaseStats(unit.rawBlock1.unitClass()); //Hardcoded
        //If it has logbook data, +2 on their asset, -2 flaw
        if (unit.rawLog != null) {
            int asset = unit.rawLog.getAssetFlaw()[0];
            int flaw = unit.rawLog.getAssetFlaw()[1];
            //Asset/Flaw 0 is None, HP is 1
            if (asset != 0) unitAddition[asset - 1] += 2;
            if (flaw != 0) {
                if (flaw == 6) unitAddition[flaw - 1] -= 2;
                else unitAddition[flaw - 1] -= 1;
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
        int[] maxClass = getClassMaxStats(unit.rawBlock1.unitClass());
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
        else return getUnitModifiers(unit.rawBlock1.unitId());  //Regular units
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
            mods[i] = getUnitModifiers(unit.rawBlock1.unitId())[i]; //Default modifiers
            //If both parents are valid, check their base modifiers and assets/flaws
            //Then, check the grandparents IDs before getting their stats
            if (father >= 0 && father <= 0x38 && mother >= 0 && mother <= 0x38) {
                mods[i] += getUnitModifiers(block.parentId(0))[i] //Father modifiers
                        + getUnitModifiers(block.parentId(1))[i] //Mother modifiers
                        + calcAssetFlaw(block.asset(0), block.flaw(0))[i] //Father assets
                        + calcAssetFlaw(block.asset(1), block.flaw(1))[i]; //Mother assets
                //Check the unit IDs of the grandparents
                int fatherGrandpa = block.parentId(2);
                int fatherGrandma = block.parentId(3);
                int motherGrandpa = block.parentId(4);
                int motherGrandma = block.parentId(5);
                if (fatherGrandpa >= 0 && fatherGrandpa <= 0x38 && fatherGrandma >= 0 && fatherGrandma <= 0x38) {
                    mods[i] += getUnitModifiers(fatherGrandpa)[i] //Father's father modifiers
                            + getUnitModifiers(fatherGrandma)[i] //Father's mother modifiers
                            + calcAssetFlaw(block.asset(2), block.flaw(2))[i] //Father's father assets
                            + calcAssetFlaw(block.asset(3), block.flaw(4))[i]; //Father's mother assets
                }
                if (motherGrandpa >= 0 && motherGrandpa <= 0x38 && motherGrandma >= 0 && motherGrandma <= 0x38) {
                    mods[i] += getUnitModifiers(motherGrandpa)[i] //Mother's father modifiers
                            + getUnitModifiers(motherGrandpa)[i] //Mother's mother modifiers
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
        int[] baseModif = getUnitModifiers(unit.rawBlock1.unitId());
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
        int move = getClassMove(unit.rawBlock1.unitClass()) + getMoveBuff(unit) + unit.rawBlock1.movement();
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
}
