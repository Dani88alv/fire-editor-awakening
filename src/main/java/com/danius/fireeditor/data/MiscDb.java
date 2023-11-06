package com.danius.fireeditor.data;

import com.danius.fireeditor.savefile.units.mainblock.RawSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.danius.fireeditor.data.UnitDb.*;

public class MiscDb {

    public static final List<String> modifNames = Arrays.asList(
            "None", "HP", "Str", "Mag", "Skill", "Spd", "Lck", "Def", "Res"
    );

    public static String getArmyName(int id) {
        int maxId = armies.size() - 1;
        if (id > maxId) return "Mod #" + (id - maxId);
        else return armies.get(id);
    }

    public static List<String> getArmyNames() {
        return armies;
    }

    public static List<String> getArmyNames(int max) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            names.add(getArmyName(i));
        }
        return names;
    }

    private static final List<String> armies = Arrays.asList(
            "None", "Other", "Halidom of Ylisse", "Regna Ferox",
            "Plegia", "Empire of Valm", "Grimleal", "Rebels",
            "Unknown Foe", "Risen", "Ruffians", "Villagers"
    );

    public static final List<String> traitFlags = Arrays.asList(
            "Female", "Hero", "Player", "Advanced Class",
            "Boss/Leader", "Defeat Condition", "Movement Ban", "Force Battle Anim.",
            "Battle Anim. Ban", "Defeated Exp +20", "Defeated Exp -10", "Unknown 1-3",
            "Marth/Lucina", "Walhart", "Aversa", "Owain",
            "Manakete", "Taguel", "Destroy Villages", "Crit Ban",
            "Avoid Ban", "Enemy Only", "Special Performance", "Guest",
            "Original Marth", "Entombed Exp", "Delivery Face", "White Dragon",
            "Unknown 3-4", "Unknown 3-5", "Unknown 3-6", "Unknow 3-7"
    );

    /*
    Foreign Unit: Enemies/Units just recruited in a map/Foreign Avatars (Logbook Limit)
    Tiki Meditating: Paralogue 17 Tiki Animation
    Outrealm Unit: Changes portrait
    Wireless Unit: Logbook Limit
     */
    public static final List<String> battleFlags = Arrays.asList(
            "Moved in this turn", "Paired Up (Front)", "Paired Up (Back)", "Retired/Dead (Classic)",
            "Unknown 0-4", "Unknown 0-5", "Unknown 0-6", "Retired on Current Map",
            "Foreign Unit", "Unknown 1-1", "Selected for Battle", "Unknown 1-3",
            "Unknown 1-4", "Unknown 1-5", "Unknown 1-6", "Unknown 1-7",
            "Unknown 2-0", "Unknown 2-1", "Unknown 2-2", "Unknown 2-3",
            "Galeforce Used", "Unknown 2-5", "Married to Maiden", "Unknown 2-7",
            "Unknown 3-0", "Tiki Meditating", "Unknown 3-2", "Outrealm Unit",
            "Unknown 3-4", "Wireless Unit", "Unknown 3-6", "Unknown 3-7"
    );

    public static final List<String> buffSkills = Arrays.asList(
            "STR,MAG,DEF,RES +2 (Special Dance)", "STR +4 (Rally Strength)",
            "MAG +4 (Rally Magic)", "SKL +4 (Rally Skill)", "SPD +4 (Rally Speed)",
            "LCK +8 (Rally Luck)", "DEF +4 (Rally Defense)", "RES +4 (Rally Resistance)",
            "All Stats +4 (Rally Spectrum)", "Movement +1 (Rally Movement)",
            "All Stats +2, Mov +1 (Rally Heart)"
    );

    public static final List<String> buffTonics = Arrays.asList(
            "HP +5", "STR +2", "MAG +2", "SKL +2",
            "SPD +2", "LCK +2", "DEF +2", "RES +2"
    );

    public static final List<String> buffBarracks = Arrays.asList(
            "Unknown", "STR +4", "MAG +4", "SKL +4",
            "SPD +4", "LCK +4", "DEF +4", "RES +4"
    );

    public static final List<String> chapterDlcNames = Arrays.asList(
            "Champions of Yore 1", "Champions of Yore 2", "Champions of Yore 3",
            "The Golden Gaffe", "EXPonentional Growth", "Infinite Regalia",
            "Lost Bloodlines 1", "Lost Bloodlines 2", "Lost Bloodlines 3",
            "Smash Brethren 1", "Smash Brethren 2", "Smash Brethren 3",
            "Rogues & Redeemers 1", "Rogues & Redeemers 2", "Rogues & Redeemers 3",
            "Death's Embrace", "Five-Anna Firefight", "Roster Rescue",
            "Harvest Scramble", "Summer Scramble", "Hot-Spring Scramble",
            "The Future Past 1", "The Future Past 2", "The Future Past 3",
            "Apotheosis"
    );

    public static final List<String> doubleDuelNames = Arrays.asList(
            "Kellam's Kettles", "Cherche's Fliers", "Maribelle's Mercy", "Frederick's Loyal",
            "Sumia's Fliers", "Cordelia's Pride", "Sully's Riders", "Panne's Patrol",
            "Nowi's Wyverns", "Basilio's Brashest", "Virion's Archest", "Lon'qu's Blades",
            "Stahl's Horsemen", "Vaike's Victory", "Ricken's Chosen", "Henry's Spellslingers",
            "Gaius's Sneak Attack", "Tharja's Curse", "Gregor's Swell Swords", "Say'ri's Ascension",
            "Tiki's Wyverns", "Army of Shadow"
    );

    public static String getAiAction(int id) {
        String result = aiAction.get(id);
        return result != null ? result : "Unknown";
    }

    public static String getAiMission(int id) {
        String result = aiMission.get(id);
        return result != null ? result : "Unknown";
    }

    public static String getAiAttack(int id) {
        String result = aiAttack.get(id);
        return result != null ? result : "Unknown";
    }

    public static String getAiMove(int id) {
        String result = aiMove.get(id);
        return result != null ? result : "Unknown";
    }


    private static final HashMap<Integer, String> aiAction = new HashMap<Integer, String>() {{
        put(0x00, "AI_AC_Null");
        put(0x01, "AI_AC_Everytime");
        put(0x02, "AI_AC_AttackRange");
        put(0x03, "AI_AC_AttackRangeExcludePerson");
        put(0x04, "AI_AC_BandRange");
        put(0x0A, "AI_AC_Turn");
        put(0x0B, "AI_AC_FlagTrue");
        put(0x0D, "AI_AC_TurnAttackRange");
        put(0x0E, "AI_AC_TurnBandRange");
        put(0x0F, "AI_AC_TurnAttackRangeHealRange");
        put(0x10, "AI_AC_FlagTrueAttackRange");
        put(0x14, "AI_AC_FlagTrueAttackRangeExcludePerson");
    }};

    private static final HashMap<Integer, String> aiMission = new HashMap<Integer, String>() {{
        put(0x00, "AI_MI_Null");
        put(0x01, "AI_MI_Talk");
        put(0x02, "AI_MI_Treasure");
        put(0x03, "AI_MI_Village");
        put(0x05, "AI_MI_EscapeSlow");
        put(0x07, "AI_MI_X009Boss");
        put(0x08, "AI_MI_X010Serena");
    }};

    private static final HashMap<Integer, String> aiAttack = new HashMap<Integer, String>() {{
        put(0x00, "AI_AT_Null");
        put(0x01, "AI_AT_Attack");
        put(0x02, "AI_AT_MustAttack");
        put(0x03, "AI_AT_Heal");
        put(0x04, "AI_AT_AttackToHeal");
        put(0x05, "AI_AT_AttackToMustHeal");
        put(0x06, "AI_AT_MustAttackToMustHeal");
        put(0x09, "AI_AT_Person");
        put(0x0A, "AI_AT_ExcludePerson");
        put(0x0D, "AI_AT_X002Anna");
        put(0x0E, "AI_AT_X017Enemy");
    }};

    private static final HashMap<Integer, String> aiMove = new HashMap<Integer, String>() {{
        put(0x00, "AI_MV_Null");
        put(0x01, "AI_MV_NearestEnemy");
        put(0x03, "AI_MV_NearestEnemyExcludePerson");
        put(0x0A, "AI_MV_Person");
        put(0x0C, "AI_MV_Position");
        put(0x0E, "AI_MV_EscapeSlow");
        put(0x0F, "AI_MV_TrasureToEscape");
        put(0x10, "AI_MV_VillageToAttack");
        put(0x11, "AI_MV_VillageNoThroughToAttack");
        put(0x14, "AI_MV_Irregular");
        put(0x15, "AI_MV_X009Boss");
        put(0x16, "AI_MV_X010Serena");
        put(0x17, "AI_MV_X017Enemy");
    }};


}
