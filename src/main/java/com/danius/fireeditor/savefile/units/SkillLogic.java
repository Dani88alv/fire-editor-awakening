package com.danius.fireeditor.savefile.units;

import com.danius.fireeditor.FireEditor;

import java.util.*;

public class SkillLogic {

    //Sets all the legal skills a unit can have according to their current data (not only hardcoded skills)
    public static void setLegalSkills(Unit unit) {
        int unitId = unit.rawBlock1.unitId();
        unit.rawSkill.setAll(false);
        boolean isFemale = isFemaleUnit(unit);
        //Main skills
        List<Integer> availableClasses = hardcodedClasses(unitId, isFemale);
        List<Integer> allSkills = getSkillsFromClasses(availableClasses);
        //Recruited Skills
        List<Integer> recruitSkills = FireEditor.unitDb.getSkills(unitId);
        allSkills.addAll(recruitSkills);
        //Related Current Class Skills
        List<Integer> relatedClasses = FireEditor.classDb.getFamilyClass(unit.rawBlock1.unitClass());
        List<Integer> relatedSkills = getSkillsFromClasses(relatedClasses);
        allSkills.addAll(relatedSkills);
        //Additional skills
        List<Integer> extraSkills = getExtraSkills(unit);
        allSkills.addAll(extraSkills);
        //Remove Aether or Rightful King
        if (unitId != 0x3 && unitId != 0x1A) allSkills.removeIf(number -> number.equals(20));
        if (unitId != 0x3 && unitId != 0x1A) allSkills.removeIf(number -> number.equals(72));
        //Clean up
        cleanList(allSkills);

        //Parent Skills (last skill slot inheritance)
        //The skills of each parent slot are stored
        if (unit.rawChild != null) {
            for (int i = 0; i < 6; i++) {
                int parent = unit.rawChild.parentId(i);
                //The parent class skills are retrieved
                List<Integer> skills = getSkillsFromClasses(hardcodedClasses(parent, isFemale));
                removeSpecialSkills(skills, unit);
                //The recruited skills from the parent are added
                skills.addAll(FireEditor.unitDb.getSkills(parent));
                allSkills.addAll(skills);
                cleanList(allSkills);
            }
        }
        //The skills are set
        for (int skill : allSkills) unit.rawSkill.setLearnedSkill(true, skill);
    }

    public static void removeSpecialSkills(List<Integer> skills, Unit unit) {
        if (unit.rawChild == null) return;
        int unitId = unit.rawBlock1.unitId();
        boolean isFemale = isFemaleUnit(unit);
        //Remove Special Dance is the character is not Olivia
        if (unitId != 0x17) skills.removeIf(number -> number.equals(0x36));
        //Remove Exclusive Lord Skills
        skills.removeIf(number -> number.equals(36));
        skills.removeIf(number -> number.equals(18));
        skills.removeIf(number -> number.equals(72));
        skills.removeIf(number -> number.equals(20));

        //Add Aether of Rightful King according to the gender of the unit (Chrom's child)
        for (int i = 0; i < 6; i++) {
            int parent = unit.rawChild.parentId(i);
            //If the parent is a Lord, add gender-exclusive Lord skills
            if (parent == 0x3 || parent == 0x1A) {
                if (isFemale) skills.add(72);
                else skills.add(20);
            }
        }
        //Add Lord skills if they have Lord as base class
        if (unitId == 0x3 || unitId == 0x1A) {
            skills.add(36);
            skills.add(18);
            skills.add(72);
            skills.add(20);
        }
    }

    //Retrieves additional skills with specific requirements
    private static List<Integer> getExtraSkills(Unit unit) {
        List<Integer> extraSkills = new ArrayList<>();
        int id = unit.rawBlock1.unitId();
        boolean isFemale = isFemaleUnit(unit);
        //Manakete Flag
        if (isManakete(unit)) {
            extraSkills.add(getSkillsFromClass(0x44).get(0));
            extraSkills.add(getSkillsFromClass(0x44).get(1));
        }
        //Taguel Flag
        if (isTaguel(unit)) {
            extraSkills.add(getSkillsFromClass(0x45).get(0));
            extraSkills.add(getSkillsFromClass(0x45).get(1));
        }
        //Bride and Dread Fighter Skills
        extraSkills.add(isFemale ? 0x65 : 0x63);
        extraSkills.add(isFemale ? 0x66 : 0x64);
        //DLC Skills
        extraSkills.add(0x58); //All Stats +2
        extraSkills.add(0x59); //Paragon
        extraSkills.add(0x5A); //Iote's Shield
        extraSkills.add(0x5B); //Limit Breaker
        int unitClass = unit.rawBlock1.unitClass();
        //Enemy Skills
        if (id >= 0x35) {
            extraSkills.add(0x8); //Hit Rate +10
            extraSkills.add(0x60); //Vantage+
            extraSkills.add(0x5F); //Luna+
            extraSkills.add(0x5E); //Hawkeye
            extraSkills.add(0x61); //Pavise+
            extraSkills.add(0x62); //Aegis+
            extraSkills.add(0x5D); //Rightful God
            extraSkills.add(0x5C); //Dragonskin
        }
        //Grima Skills cuz why not
        if (unitClass == 0x4E) {
            extraSkills.add(0x5D); //Rightful God
            extraSkills.add(0x5C); //Dragonskin
        }
        //Einherjar Exclusive Skills
        if (unit.rawLog != null) {
            if (unit.rawLog.hasEinherjarIdDlc()) {
                int logId = unit.rawLog.getLogIdLastByte();
                List<Integer> einherjarSkills = FireEditor.unitDb.getEinSkills(logId);
                extraSkills.addAll(einherjarSkills);
            }
        }
        return extraSkills;
    }

    public static List<Integer> getSkillsFromClasses(List<Integer> classes) {
        List<Integer> skillList = new ArrayList<>();
        for (Integer aClass : classes) {
            int[] skills = FireEditor.classDb.skills(aClass);
            for (int skill : skills) skillList.add(skill);
        }
        return skillList;
    }

    public static List<Integer> getSkillsFromClass(int unitClass) {
        List<Integer> skillList = new ArrayList<>();
        int[] skills = FireEditor.classDb.skills(unitClass);
        for (int skill : skills) skillList.add(skill);
        return skillList;
    }

    //Retrieves the hardcoded classes of a unit
    private static List<Integer> hardcodedClasses(int unitId, boolean isFemale) {
        List<Integer> classes = new ArrayList<>();
        //Retrieves the male or female re-classes
        int[] reclasses;
        if (isFemale) reclasses = FireEditor.unitDb.getReclassF(unitId);
        else reclasses = FireEditor.unitDb.getReclassM(unitId);
        for (int reclass : reclasses) classes.add(reclass);
        //If it has Hero/Guest flags hardcoded (not working with save editing)
        if (FireEditor.unitDb.hasFlag(unitId, 2) || FireEditor.unitDb.hasFlag(unitId, 23)) {
            classes.addAll(FireEditor.classDb.getGenderClasses(isFemale));
            //Remove special classes
            classes.removeIf(aClass -> aClass >= 0 && aClass <= 3); //Lord
            classes.removeIf(aClass -> aClass >= 67); //Dancer +
        }
        //The promoted classes are added to the list
        for (int reclass : reclasses) {
            int[] unpromoted = FireEditor.classDb.getPromoted(reclass);
            for (int k : unpromoted) classes.add(k);
        }
        List<Integer> classCopy = new ArrayList<>(classes);
        for (int i = 0; i < classCopy.size(); i++) {
            List<Integer> relatedClasses = FireEditor.classDb.getFamilyClass(classes.get(i));
            classes.addAll(relatedClasses);
        }
        return classes;
    }

    //Checks if a unit is male or female according to the re-class options
    public static boolean isFemaleUnit(Unit unit) {
        int id = unit.rawBlock1.unitId();
        //Game hardcoded logic to determine gender re-classes
        //Class gender flag has priority over character gender
        boolean femaleClass = FireEditor.classDb.isFemale(unit.rawBlock1.unitClass());
        if (femaleClass) return true;
        //If class is male, check own unit gender
        boolean femaleUnit; //Male by default
        //Check the hardcoded regular units
        if (unit.rawLog == null) femaleUnit = FireEditor.unitDb.isFemale(id);
            //Check the avatar gender
        else femaleUnit = unit.rawLog.getFullBuild()[4] == 1;
        return femaleUnit;
    }

    private static boolean isManakete(Unit unit) {
        int unitId = unit.rawBlock1.unitId();
        int flag = 16;
        //Hardcoded Flag
        if (FireEditor.unitDb.hasFlag(unitId, flag)) return true;
        //Save editing flag
        if (unit.rawFlags.traitFlagList().contains(flag)) return true;
        //Class
        if (unit.rawBlock1.unitClass() == 68) return true;
        //Logbook Data
        if (unit.rawLog != null) {
            return unit.rawLog.getProfileCard()[0] == 68;
            //if (unit.rawLog.hasEinherjarId() && unit.rawLog.getLogIdLastByte() == 4) return true;
        }
        return false;
    }

    private static boolean isTaguel(Unit unit) {
        int unitId = unit.rawBlock1.unitId();
        int flag = 17;
        //Hardcoded Flag
        if (FireEditor.unitDb.hasFlag(unitId, flag)) return true;
        //Save editing flag
        if (unit.rawFlags.traitFlagList().contains(flag)) return true;
        //Class
        if (unit.rawBlock1.unitClass() == 69 || unit.rawBlock1.unitClass() == 70) return true;
        else return unit.rawLog != null &&
                (unit.rawLog.getProfileCard()[0] == 69 || unit.rawLog.getProfileCard()[0] == 70);
    }

    private static void cleanList(List<Integer> list) {
        HashSet<Integer> uniqueValues = new HashSet<>();
        int index = 0;
        while (index < list.size()) {
            Integer current = list.get(index);

            if (!uniqueValues.contains(current)) {
                uniqueValues.add(current);
                index++;
            } else list.remove(index);
        }
    }


}
