package com.danius.fireeditor.savefile;

import com.danius.fireeditor.savefile.inventory.Refinement;
import com.danius.fireeditor.savefile.units.Unit;

import java.util.List;

public class Batch {
    /*
    Collection of cheats
     */
    private final int[] unitGroups = new int[]{0, 3, 4}; //Playable unit groups

    /* Sets all supports from a unit group to a specific level */
    public static void setSupports(Chapter13 chapterFile, int groupSlot, int level) {
        List<Integer> unitIds = chapterFile.blockUnit.unitIdsInGroup(groupSlot);
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(groupSlot).size(); i++) {
            Unit unit = chapterFile.blockUnit.unitList.get(groupSlot).get(i);
            unit.rawSupport.expandBlock();
            unit.rawSupport.setAllSupportsTo(level, unitIds);
            if (unit.rawChild != null) {
                int sibling = chapterFile.blockUnit.findSibling(unit);
                unit.rawChild.setValidSupportsToLevel(level, sibling);
            }
            if (level == 0) unit.rawSupport.removeExtraSupports();
        }
    }

    /* Sets all the skills from a unit group */
    public static void setSkillsAll(Chapter13 chapterFile, int groupSlot, boolean set) {
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(groupSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(groupSlot).get(i).rawSkill.setAll(set);
        }
    }

    /* Sets all legal skills from a unit group */
    public static void setSkillsLegal(Chapter13 chapterFile, int groupSlot) {
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(groupSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(groupSlot).get(i).setLegalSkills();
        }
    }

    /* Sets battle and victory count from a unit group */
    public static void setBattlesVictories(Chapter13 chapterFile, int groupSlot, int value) {
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(groupSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(groupSlot).get(i).rawBlockEnd.setBattles(value);
            chapterFile.blockUnit.unitList.get(groupSlot).get(i).rawBlockEnd.setVictories(value);
        }
    }

    /* Sets max growth, level and weapon experience */
    public static void setMaxStats(Chapter13 chapterFile, int groupSlot) {
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(groupSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(groupSlot).get(i).maxStats();
            //chapterFile.blockUnit.unitList.get(groupSlot).get(i).rawFlags.setHiddenLevel(0);
        }
    }

    /* Sets extra movement (boots) */
    public static void setMovement(Chapter13 chapterFile, int groupSlot, int value) {
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(groupSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(groupSlot).get(i).rawBlock1.setMovement(value);
        }
    }

    /* Turn on tonics, skill buffs, barrack buffs and pure water buff */
    public static void setTemporalBuffs(Chapter13 chapterFile, int groupSlot, boolean set) {
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(groupSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(groupSlot).get(i).rawFlags.setAllTonicFlags(set);
            chapterFile.blockUnit.unitList.get(groupSlot).get(i).rawFlags.setAllBarrackFlags(set);
            chapterFile.blockUnit.unitList.get(groupSlot).get(i).rawFlags.setAllOtherBuffs(set);
        }
    }

    /* Sets the same use value to all the convoy items */
    public static void setConvoyUsesTo(Chapter13 chapter13, int uses) {
        //Regular Items
        chapter13.blockTran.setAllItemUsesTo(uses);
        //Forged Weapons
        List<Refinement> refinementList = chapter13.blockRefi.refiList;
        chapter13.blockTran.setAllForgedUsesTo(uses, refinementList);
    }

    /* Sets the same amount (different uses) to all the convoy items */
    public static void setConvoyAmountTo(Chapter13 chapter13, int uses) {
        //Regular Items
        chapter13.blockTran.setAllItemAmountTo(uses);
        //Forged Weapons
        List<Refinement> refinementList = chapter13.blockRefi.refiList;
        chapter13.blockTran.setAllForgedAmountTo(uses, refinementList);
    }
}
