package com.danius.fireeditor.savefile.units;

import java.util.*;

public class SkillLogic {

    //Sets all the legal skills a unit can have according to their current data (not only hardcoded skills)
    public static void setLegalSkills(Unit unit) {
        int unitId = unit.rawBlock1.unitId();
        //Remove skills if the unit is a valid character
        if (unitId < 0x35) unit.rawSkill.setAll(false);
        boolean isFemale = isFemaleUnit(unit);
        //Main skills
        List<Integer> regularSkills = getSkillsFromClass(hardcodedClasses(unitId, isFemale));
        regularSkills.addAll(recruitedSkills(unitId));
        //Parent Skills (last skill slot inheritance)
        //The parent slots list is initialized
        List<List<Integer>> parentSkills = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            parentSkills.add(new ArrayList<>());
        }
        //The skills of each parent slot are stored
        if (unit.rawChild != null) {
            for (int i = 0; i < 6; i++) {
                //The skills of each parent slot are stored
                List<Integer> skills = getParentSkills(unit, i);
                //The duplicated skills from the unit are removed
                skills.removeIf(regularSkills::contains);
                //The duplicated skills from other parents are removed (the order could change, but irrelevant)
                for (List<Integer> previousSkills : parentSkills) {
                    skills.removeIf(previousSkills::contains);
                }
                parentSkills.set(i, skills);
            }
        }
        //Additional skills
        List<Integer> extraSkills = getExtraSkills(unit);
        /*
        The skills are set
         */
        for (int skill : regularSkills) unit.rawSkill.setLearnedSkill(true, skill);
        for (int skill : parentSkills.get(0)) unit.rawSkill.setLearnedSkill(true, skill);
        for (int skill : parentSkills.get(1)) unit.rawSkill.setLearnedSkill(true, skill);
        for (int skill : parentSkills.get(2)) unit.rawSkill.setLearnedSkill(true, skill);
        for (int skill : parentSkills.get(3)) unit.rawSkill.setLearnedSkill(true, skill);
        for (int skill : parentSkills.get(4)) unit.rawSkill.setLearnedSkill(true, skill);
        for (int skill : parentSkills.get(5)) unit.rawSkill.setLearnedSkill(true, skill);
        for (int skill : extraSkills) unit.rawSkill.setLearnedSkill(true, skill);
    }

    //Retrieves the skills that can be inherited through the parent's last skill slot
    private static List<Integer> getParentSkills(Unit unit, int parentSlot) {
        List<Integer> skills = new ArrayList<>();
        int unitId = unit.rawBlock1.unitId();
        int parent = unit.rawChild.parentId(parentSlot);
        boolean isFemale = isFemaleUnit(unit);

        //The parent class skills are retrieved
        skills = getSkillsFromClass(hardcodedClasses(parent, getUnitGenderFlag(parent)));
        //Checks Walhart (Conquest)
        if (parent == 0x2F) skills.add(0x56);
        //Checks Aversa (Shadowgift)
        if (parent == 0x32) skills.add(0x57);

        //Remove Special Dance is the character is not Olivia
        if (unitId != 0x17) skills.removeIf(number -> number.equals(0x36));
        //Remove Lord and Great Lord Skills if the unit is not Chrom or Female Class Lucina
        if (unitId != 0x3 && !(unitId == 0x1A && isFemale)) {
            skills.removeIf(number -> number.equals(0x12));
            skills.removeIf(number -> number.equals(0x24));
            skills.removeIf(number -> number.equals(0x48));
            skills.removeIf(number -> number.equals(0x14));
        }
        //Add Aether of Rightful King according to the gender of the unit (Chrom's child)
        if (parent == 0x3) {
            if (isFemale) skills.add(0x48);
            else skills.add(0x14);
        }
        return skills;
    }

    //Retrieves additional skills with specific requirements
    private static List<Integer> getExtraSkills(Unit unit) {
        List<Integer> extraSkills = new ArrayList<>();
        List<Integer> traitFlags = unit.rawFlags.traitFlagList();
        int id = unit.rawBlock1.unitId();
        boolean isFemale = isFemaleUnit(unit);
        //Manakete Skills (Einherjar Tiki) (Manakete flag)
        if (traitFlags.contains(16)) {
            extraSkills.add(getSkillsFromClass(0x44).get(0));
            extraSkills.add(getSkillsFromClass(0x44).get(1));
        }
        //Conquest (Walhart)
        if (id == 0x2F) extraSkills.add(0x56);
        //Conquest (Einherjar Zephiel, DLC Ephraim)
        if (unit.rawLog != null) {
            if (unit.rawLog.hasEinherjarId() && (unit.rawLog.getLogIdLastByte() == 68) || unit.rawLog.getLogIdLastByte() == 0xD5) {
                extraSkills.add(0x56);
            }
        }
        //Shadowgift (Aversa)
        if (id == 0x32) extraSkills.add(0x57);
        //Shadowgift (DLC Micaiah, DLC Katarina)
        if (unit.rawLog != null) {
            if (unit.rawLog.hasEinherjarId() &&
                    (unit.rawLog.getLogIdLastByte() == 0xCB) || unit.rawLog.getLogIdLastByte() == 0xDB) {
                extraSkills.add(0x56);
            }
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
        if (id == 0x36 || id == 0x37) {
            extraSkills.add(0x8); //Hit Rate +10
            extraSkills.add(0x60); //Vantage+
            extraSkills.add(0x5F); //Luna+
            extraSkills.add(0x5E); //Hawkeye
            extraSkills.add(0x61); //Pavise+
            extraSkills.add(0x62); //Aegis+
        }
        //Grima Skills cuz why not
        if (unitClass == 0x4E) {
            extraSkills.add(0x5D); //Rightful God
            extraSkills.add(0x5C); //Dragonskin
        }
        return extraSkills;
    }

    public static List<Integer> getSkillsFromClass(List<Integer> classes) {
        List<Integer> skillList = new ArrayList<>();
        for (Integer aClass : classes) {
            int[] skills = getClassSkills(aClass);
            for (int skill : skills) skillList.add(skill);
        }
        return skillList;
    }

    public static List<Integer> getSkillsFromClass(int unitClass) {
        List<Integer> skillList = new ArrayList<>();
        int[] skills = getClassSkills(unitClass);
        for (int skill : skills) skillList.add(skill);
        return skillList;
    }

    //Retrieves the hardcoded classes of each of a unit
    private static List<Integer> hardcodedClasses(int unitId, boolean isFemale) {
        List<Integer> classes = new ArrayList<>();
        if (unitId < 0 || unitId >= maleReclasses().size()) return new ArrayList<>();
        //Retrieves the male or female re-classes
        int[] reclasses;
        if (isFemale) reclasses = getReclass(unitId, true);
        else reclasses = getReclass(unitId, false);
        for (int reclass : reclasses) classes.add(reclass);
        //If the unit is avatar, adds all the compatible classes
        if (unitId == 0x0 || unitId == 0x1 || unitId == 0x2 || unitId == 0x34) {
            int gender = isFemale ? 1 : 0;
            for (int i = 0x04; i <= 0x42; i++) {
                if (femaleClassFlags.get(i) == gender) classes.add(i);
            }
        }
        //The promoted classes are added to the list
        for (int reclass : reclasses) {
            int[] unpromoted = getPromotedClasses(reclass);
            for (int k : unpromoted) classes.add(k);
        }
        return classes;
    }

    //Checks if a unit is male or female according to the re-class options
    public static boolean isFemaleUnit(Unit unit) {
        int id = unit.rawBlock1.unitId();
        //Game hardcoded logic to determine gender re-classes
        //If class is female, ignore gender flag (is female)
        boolean femaleClass = classGenderFlag(unit.rawBlock1.unitClass());
        if (femaleClass) return true;
        //If class is male, check build gender
        boolean defaultGender = false; //Male by default
        //Check the hardcoded regular units (no avatars and enemies)
        if (id >= 0x3 && id <= 0x38 && id != 0x36 && id != 0x37) defaultGender = getUnitGenderFlag(id);
            //Check the avatar gender
        else if (unit.rawLog != null) defaultGender = unit.rawLog.getFullBuild()[4] == 1;
        return defaultGender;
    }

    private static int[] getReclass(int id, boolean isFemale) {
        if (id >= maleReclasses().size() || id < 0) return new int[0];
        int[] reclasses;
        if (isFemale) reclasses = femaleReclasses().get(id);
        else reclasses = maleReclasses().get(id);
        for (int i = 0; i < reclasses.length; i++) reclasses[i] -= 1;
        return reclasses;
    }

    private static int[] getPromotedClasses(int id) {
        int[] classes = promotedClasses().get(id);
        if (classes == null) return new int[0];
        return classes;
    }

    private static int[] getClassSkills(int id) {
        int[] skills = classSkills().get(id);
        if (skills == null) return new int[0];
        for (int i = 0; i < skills.length; i++) skills[i] -= 1;
        return skills;
    }

    public static List<Integer> recruitedSkills(int unitId) {
        List<Integer> list = new ArrayList<>();
        if (unitId < 46 || unitId > 51) return list;
        //Only spotpass paralogue characters
        int[] skills = new int[6];
        if (unitId == 46) skills = new int[]{12, 11, 25, 46};
        else if (unitId == 47) skills = new int[]{87};
        else if (unitId == 48) skills = new int[]{3, 30, 56};
        else if (unitId == 49) skills = new int[]{10, 78, 73, 48};
        else if (unitId == 50) skills = new int[]{87, 5, 42, 62, 84};
        else skills = new int[]{64, 65, 66, 74, 75};
        for (int skill : skills) list.add(skill);
        return list;
    }

    private static HashMap<Integer, int[]> classSkills() {
        HashMap<Integer, int[]> chars = new HashMap<Integer, int[]>();
        chars.put(0x00, new int[]{19, 37}); //Lord M
        chars.put(0x01, new int[]{19, 37}); //Lord F
        chars.put(0x02, new int[]{73, 21}); //Great Lord M
        chars.put(0x03, new int[]{73, 21}); //Great Lord F
        chars.put(0x04, new int[]{14, 38}); //Tactician M
        chars.put(0x05, new int[]{14, 38}); //Tactician F
        chars.put(0x06, new int[]{77, 64}); //Grandmaster M
        chars.put(0x07, new int[]{77, 64}); //Grandmaster F
        chars.put(0x08, new int[]{16, 28}); //Cavalier M
        chars.put(0x09, new int[]{16, 28}); //Cavalier F
        chars.put(0x0A, new int[]{7, 29}); //Knight M
        chars.put(0x0B, new int[]{7, 29}); //Knight F
        chars.put(0x0C, new int[]{46, 81}); //Paladin M
        chars.put(0x0D, new int[]{46, 81}); //Paladin F
        chars.put(0x0E, new int[]{76, 20}); //Great Knight M
        chars.put(0x0F, new int[]{76, 20}); //Great Knight F
        chars.put(0x10, new int[]{61, 80}); //General M
        chars.put(0x11, new int[]{61, 80}); //General F
        chars.put(0x12, new int[]{84, 27}); //Barbarian
        chars.put(0x13, new int[]{2, 32}); //Fighter
        chars.put(0x14, new int[]{17, 35}); //Mercenary M
        chars.put(0x15, new int[]{17, 35}); //Mercenary F
        chars.put(0x16, new int[]{5, 34}); //Archer M
        chars.put(0x17, new int[]{5, 34}); //Archer F
        chars.put(0x18, new int[]{33, 51}); //Berserker
        chars.put(0x19, new int[]{56, 82}); //Warrior
        chars.put(0x1A, new int[]{75, 67}); //Hero M
        chars.put(0x1B, new int[]{75, 67}); //Hero F
        chars.put(0x1C, new int[]{58, 68}); //Bow Knight M
        chars.put(0x1D, new int[]{58, 68}); //Bow Knight F
        chars.put(0x1E, new int[]{10, 52}); //Sniper M
        chars.put(0x1F, new int[]{10, 52}); //Sniper F
        chars.put(0x20, new int[]{11, 79}); //Myrmidon M
        chars.put(0x21, new int[]{11, 79}); //Myrmidon F
        chars.put(0x22, new int[]{13, 12}); //Thief M
        chars.put(0x23, new int[]{13, 12}); //Thief F
        chars.put(0x24, new int[]{74, 49}); //Swordmaster M
        chars.put(0x25, new int[]{74, 49}); //Swordmaster F
        chars.put(0x26, new int[]{72, 48}); //Assassin M
        chars.put(0x27, new int[]{72, 48}); //Assassin F
        chars.put(0x28, new int[]{26, 47}); //Trickster M
        chars.put(0x29, new int[]{26, 47}); //Trickster F
        chars.put(0x2A, new int[]{6, 43}); //Pegasus Knight
        chars.put(0x2B, new int[]{59, 50}); //Falcon Knight
        chars.put(0x2C, new int[]{63, 85}); //Dark Flier
        chars.put(0x2D, new int[]{3, 30}); //Wyvern Rider M
        chars.put(0x2E, new int[]{3, 30}); //Wyvern Rider F
        chars.put(0x2F, new int[]{24, 65}); //Wyvern Lord M
        chars.put(0x30, new int[]{24, 65}); //Wyvern Lord F
        chars.put(0x31, new int[]{45, 66}); //Griffon Rider M
        chars.put(0x32, new int[]{45, 66}); //Griffon Rider F
        chars.put(0x33, new int[]{8, 39}); //Troubadour
        chars.put(0x34, new int[]{83, 42}); //Priest
        chars.put(0x35, new int[]{83, 42}); //Cleric
        chars.put(0x36, new int[]{4, 31}); //Mage M
        chars.put(0x37, new int[]{4, 31}); //Mage F
        chars.put(0x38, new int[]{40, 41}); //Dark Mage M
        chars.put(0x39, new int[]{40, 41}); //Dark Mage F
        chars.put(0x3A, new int[]{62, 18}); //Valkyrie
        chars.put(0x3B, new int[]{60, 44}); //War Monk
        chars.put(0x3C, new int[]{60, 44}); //War Cleric
        chars.put(0x3D, new int[]{57, 53}); //Sage M
        chars.put(0x3E, new int[]{57, 53}); //Sage F
        chars.put(0x3F, new int[]{25, 86}); //Dark Knight M
        chars.put(0x40, new int[]{25, 86}); //Dark Knight F
        chars.put(0x41, new int[]{78, 69}); //Sorcerer M
        chars.put(0x42, new int[]{78, 69}); //Sorcerer F
        chars.put(0x43, new int[]{54, 55}); //Dancer
        chars.put(0x44, new int[]{22, 70}); //Manakete
        chars.put(0x45, new int[]{23, 71}); //Taguel M
        chars.put(0x46, new int[]{23, 71}); //Taguel F
        chars.put(0x47, new int[]{}); //Soldier
        chars.put(0x48, new int[]{15, 36}); //Villager
        chars.put(0x49, new int[]{}); //Merchant
        chars.put(0x4A, new int[]{}); //Reverant
        chars.put(0x4B, new int[]{}); //Entombed
        chars.put(0x4C, new int[]{}); //Conqueror
        chars.put(0x4D, new int[]{}); //Lodestar
        chars.put(0x4E, new int[]{}); //Grima
        chars.put(0x4F, new int[]{}); //Mirage
        chars.put(0x50, new int[]{100, 101}); //Dread Fighter
        chars.put(0x51, new int[]{102, 103}); //Bride
        chars.put(0x52, new int[]{}); //Dummy
        return chars;
    }

    private static HashMap<Integer, int[]> promotedClasses() {
        HashMap<Integer, int[]> chars = new HashMap<Integer, int[]>();
        chars.put(0, new int[]{2}); //Lord M
        chars.put(1, new int[]{3}); //Lord F
        chars.put(4, new int[]{6}); //Tactician M
        chars.put(5, new int[]{7}); //Tactician M
        chars.put(8, new int[]{12, 14}); //Cavalier M
        chars.put(9, new int[]{13, 15}); //Cavalier F
        chars.put(10, new int[]{16, 14}); //Knight M
        chars.put(11, new int[]{17, 15}); //Knight F
        chars.put(18, new int[]{24, 25}); //Barbarian
        chars.put(19, new int[]{26, 25}); //Fighter
        chars.put(20, new int[]{26, 28}); //Mercenary M
        chars.put(21, new int[]{27, 29}); //Mercenary F
        chars.put(22, new int[]{30, 28}); //Archer M
        chars.put(23, new int[]{31, 29}); //Archer F
        chars.put(32, new int[]{36, 38}); //Myrmidon M
        chars.put(33, new int[]{37, 39}); //Myrmidon F
        chars.put(34, new int[]{38, 40}); //Thief M
        chars.put(35, new int[]{39, 41}); //Thief F
        chars.put(42, new int[]{43, 44}); //Pegasus Knight
        chars.put(45, new int[]{47, 49}); //Wyvern Rider M
        chars.put(46, new int[]{48, 50}); //Wyvern Rider F
        chars.put(51, new int[]{58, 60}); //Troubadour
        chars.put(52, new int[]{61, 59}); //Priest
        chars.put(53, new int[]{63, 61}); //Cleric
        chars.put(54, new int[]{61, 63}); //Mage M
        chars.put(55, new int[]{62, 64}); //Mage F
        chars.put(56, new int[]{65, 63}); //Dark Mage M
        chars.put(57, new int[]{66, 64}); //Dark Mage M
        return chars;
    }

    private static HashMap<Integer, int[]> maleReclasses() {
        HashMap<Integer, int[]> chars = new HashMap<Integer, int[]>();
        chars.put(0x00, new int[]{5}); //Avatar M
        chars.put(0x01, new int[]{5}); //Avatar F
        chars.put(0x02, new int[]{5}); //Logbook Unit
        chars.put(0x03, new int[]{1, 9, 23}); //Chrom
        chars.put(0x04, new int[]{53, 33, 19}); //Lissa
        chars.put(0x05, new int[]{9, 11, 46}); //Frederick
        chars.put(0x06, new int[]{23, 46, 55}); //Virion
        chars.put(0x07, new int[]{}); //Sully
        chars.put(0x08, new int[]{20, 19, 35}); //Vaike
        chars.put(0x09, new int[]{9, 33, 23}); //Stahl
        chars.put(0x0A, new int[]{55, 19, 57}); //Miriel
        chars.put(0x0B, new int[]{11, 35, 53}); //Kellam
        chars.put(0x0C, new int[]{}); //Sumia
        chars.put(0x0D, new int[]{33, 35, 46}); //Lon'qu
        chars.put(0x0E, new int[]{55, 9, 23}); //Ricken
        chars.put(0x0F, new int[]{53, 9, 55}); //Maribelle
        chars.put(0x10, new int[]{70, 19, 35}); //Panne
        chars.put(0x11, new int[]{35, 33, 20}); //Gaius
        chars.put(0x12, new int[]{}); //Cordelia
        chars.put(0x13, new int[]{21, 19, 33}); //Gregor
        chars.put(0x14, new int[]{}); //Nowi
        chars.put(0x15, new int[]{53, 55, 57}); //Libra
        chars.put(0x16, new int[]{}); //Tharja
        chars.put(0x17, new int[]{21, 19, 33}); //Olivia
        chars.put(0x18, new int[]{46, 20, 53}); //Cherche
        chars.put(0x19, new int[]{57, 19, 35}); //Henry
        chars.put(0x1A, new int[]{}); //Lucina
        chars.put(0x1B, new int[]{}); //Say'ri
        chars.put(0x1C, new int[]{20, 11, 19}); //Basilio
        chars.put(0x1D, new int[]{}); //Flavia
        chars.put(0x1E, new int[]{73, 20, 21}); //Donnel
        chars.put(0x1F, new int[]{}); //Anna
        chars.put(0x20, new int[]{33}); //Owain
        chars.put(0x21, new int[]{21}); //Inigo
        chars.put(0x22, new int[]{53}); //Brady
        chars.put(0x23, new int[]{}); //Kjelle
        chars.put(0x24, new int[]{}); //Cynthia
        chars.put(0x25, new int[]{}); //Severa
        chars.put(0x26, new int[]{46}); //Gerome
        chars.put(0x27, new int[]{}); //Morgan M
        chars.put(0x28, new int[]{}); //Morgan F
        chars.put(0x29, new int[]{70}); //Yarne
        chars.put(0x2A, new int[]{55}); //Laurent
        chars.put(0x2B, new int[]{}); //Noire
        chars.put(0x2C, new int[]{}); //Nah
        chars.put(0x2D, new int[]{}); //Tiki
        chars.put(0x2E, new int[]{35, 19, 57}); //Gangrel
        chars.put(0x2F, new int[]{77, 11, 46}); //Walhart
        chars.put(0x30, new int[]{}); //Emmeryn
        chars.put(0x31, new int[]{33, 46, 23}); //Yen'fay
        chars.put(0x32, new int[]{}); //Aversa
        chars.put(0x33, new int[]{21, 20, 33}); //Priam
        chars.put(0x34, new int[]{78, 5}); //Marth
        chars.put(0x35, new int[]{}); //Maiden - Dummy
        chars.put(0x36, new int[]{}); //Monster (Unpromoted)
        chars.put(0x37, new int[]{}); //Monster (Promoted)
        chars.put(0x38, new int[]{}); //Merchant
        return chars;
    }

    private static HashMap<Integer, int[]> femaleReclasses() {
        HashMap<Integer, int[]> chars = new HashMap<Integer, int[]>();
        chars.put(0x00, new int[]{6}); //Avatar M
        chars.put(0x01, new int[]{6}); //Avatar F
        chars.put(0x02, new int[]{6}); //Logbook Unit
        chars.put(0x03, new int[]{2, 10, 24}); //Chrom
        chars.put(0x04, new int[]{54, 43, 52}); //Lissa
        chars.put(0x05, new int[]{10, 12, 47}); //Frederick
        chars.put(0x06, new int[]{24, 47, 56}); //Virion
        chars.put(0x07, new int[]{10, 34, 47}); //Sully
        chars.put(0x08, new int[]{12, 22, 36}); //Vaike
        chars.put(0x09, new int[]{10, 34, 24}); //Stahl
        chars.put(0x0A, new int[]{56, 52, 58}); //Miriel
        chars.put(0x0B, new int[]{12, 36, 54}); //Kellam
        chars.put(0x0C, new int[]{43, 12, 54}); //Sumia
        chars.put(0x0D, new int[]{34, 36, 47}); //Lon'qu
        chars.put(0x0E, new int[]{56, 10, 24}); //Ricken
        chars.put(0x0F, new int[]{52, 43, 56}); //Maribelle
        chars.put(0x10, new int[]{71, 47, 36}); //Panne
        chars.put(0x11, new int[]{36, 34, 43}); //Gaius
        chars.put(0x12, new int[]{43, 22, 58}); //Cordelia
        chars.put(0x13, new int[]{22, 52, 34}); //Gregor
        chars.put(0x14, new int[]{69, 47, 56}); //Nowi
        chars.put(0x15, new int[]{54, 56, 58}); //Libra
        chars.put(0x16, new int[]{58, 24, 12}); //Tharja
        chars.put(0x17, new int[]{68, 43, 34}); //Olivia
        chars.put(0x18, new int[]{47, 52, 54}); //Cherche
        chars.put(0x19, new int[]{58, 52, 36}); //Henry
        chars.put(0x1A, new int[]{2}); //Lucina
        chars.put(0x1B, new int[]{34, 47, 43}); //Say'ri
        chars.put(0x1C, new int[]{}); //Basilio
        chars.put(0x1D, new int[]{22, 36, 12}); //Flavia
        chars.put(0x1E, new int[]{52, 43, 22}); //Donnel
        chars.put(0x1F, new int[]{36, 24, 56}); //Anna
        chars.put(0x20, new int[]{}); //Owain
        chars.put(0x21, new int[]{}); //Inigo
        chars.put(0x22, new int[]{}); //Brady
        chars.put(0x23, new int[]{12}); //Kjelle
        chars.put(0x24, new int[]{43}); //Cynthia
        chars.put(0x25, new int[]{22}); //Severa
        chars.put(0x26, new int[]{}); //Gerome
        chars.put(0x27, new int[]{}); //Morgan M
        chars.put(0x28, new int[]{}); //Morgan F
        chars.put(0x29, new int[]{}); //Yarne
        chars.put(0x2A, new int[]{}); //Laurent
        chars.put(0x2B, new int[]{24}); //Noire
        chars.put(0x2C, new int[]{69}); //Nah
        chars.put(0x2D, new int[]{69, 47, 56}); //Tiki
        chars.put(0x2E, new int[]{}); //Gangrel
        chars.put(0x2F, new int[]{}); //Walhart
        chars.put(0x30, new int[]{54, 52, 43}); //Emmeryn
        chars.put(0x31, new int[]{}); //Yen'fay
        chars.put(0x32, new int[]{43, 47, 58}); //Aversa
        chars.put(0x33, new int[]{}); //Priam
        chars.put(0x34, new int[]{}); //Marth
        chars.put(0x35, new int[]{}); //Maiden - Dummy
        chars.put(0x36, new int[]{}); //Monster (Unpromoted)
        chars.put(0x37, new int[]{}); //Monster (Promoted)
        chars.put(0x38, new int[]{}); //Merchant
        return chars;
    }

    //Retrieves the gender of a unit according to the hardcoded trait flag
    private static boolean getUnitGenderFlag(int id) {
        if (id < 0 || id > 0x38) return false;
        return (femaleUnitFlags.get(id) == 1);
    }

    private static boolean classGenderFlag(int id) {
        if (id < femaleClassFlags.size()) return (femaleClassFlags.get(id) == 1);
        return false;
    }

    //Hardcoded female flags of every unit
    private static final List<Integer> femaleUnitFlags = Arrays.asList(
            0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
            1, 0, 1, 0, 0, 1, 1, 0, 1, 0,
            1, 0, 1, 1, 1, 0, 1, 1, 0, 1,
            0, 1, 0, 0, 0, 1, 1, 1, 0, 0,
            1, 0, 0, 1, 1, 1, 0, 0, 1, 0,
            1, 0, 0, 0, 0, 0, 1
    );

    //Hardcoded female flags of every class
    private static final List<Integer> femaleClassFlags = Arrays.asList(
            0, 1, 0, 1, 0, 1, 0, 1, 0, 1,
            0, 1, 0, 1, 0, 1, 0, 1, 0, 0,
            0, 1, 0, 1, 0, 0, 0, 1, 0, 1,
            0, 1, 0, 1, 0, 1, 0, 1, 0, 1,
            0, 1, 1, 1, 1, 0, 1, 0, 1, 0,
            1, 1, 0, 1, 0, 1, 0, 1, 1, 0,
            1, 0, 1, 0, 1, 0, 1, 1, 1, 0,
            1, 0, 0, 1, 0, 0, 0, 0, 0, 0,
            0, 1, 0
    );


}
