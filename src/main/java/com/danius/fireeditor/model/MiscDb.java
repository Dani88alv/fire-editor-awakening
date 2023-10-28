package com.danius.fireeditor.model;

import com.danius.fireeditor.savefile.inventory.Refinement;
import com.danius.fireeditor.savefile.units.mainblock.RawSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.danius.fireeditor.model.UnitDb.*;

public class MiscDb {

    //TODO: usar este método a partir de ahora y hacer la lista private
    public static String unitName(int id) {
        if (id == 0xFFFF) return "None";
        if (id >= 4096) {
            id = id - 4096;
            return "Map NPC #" + (id + 1);
        }
        int size = getUnitCount();
        if (id >= size) return "Invalid Unit #" + (id - size + 1);
        return getUnitName(id);
    }

    public static String supportLevel(int unitId, int value, int slot) {
        //Checks if it is a modded unit
        int validSupports = getUnitSupportCount(unitId);
        if (slot >= validSupports) return "Unknown";
        //Valid supports
        int type = getUnitSupportTypes(unitId)[slot];
        int[] maxValues = RawSupport.supportValues().get(type);
        if (value < maxValues[0]) return "D-Rank";
        else if (value == maxValues[0]) return "C-Pending";
        else if (value < maxValues[2]) return "C-Rank";
        else if (value == maxValues[2]) return "B-Pending";
        else if (value < maxValues[4]) return "B-Rank";
        else if (value == maxValues[4]) return "A-Pending";
        else if (value < maxValues[6] || type == 0) return "A-Rank";
        else if (value == maxValues[6]) return "S-Pending";
        else if (value >= maxValues[7]) return "S-Rank";
        return "";
    }

    public static String cardPenalty(int id) {
        if (id >= cardPenaltyNames.size()) return "Unknown";
        return cardPenaltyNames.get(id);
    }

    public static final List<String> modifNames = Arrays.asList(
            "None", "HP", "Str", "Mag", "Skill", "Spd", "Lck", "Def", "Res"
    );

    public static final List<String> cardDifficulty = Arrays.asList(
            "Normal", "Hard", "Lunatic"
    );

    private static final List<String> cardPenaltyNames = Arrays.asList(
            "Classic", "Casual", "Lunatic+ Classic", "Lunatic+ Casual",
            "Classic Beaten", "Casual Beaten", "Lunatic+ Classic Beaten", "Lunatic+ Casual Beaten",
            "Dummy", "Not Displayed"
    );


    public static final List<String> dlcNames = Arrays.asList(
            "Pr. Marth (DLC)", "Roy (DLC)", "Micaiah (DLC)",
            "Leif (DLC)", "Alm (DLC)", "Seliph (DLC)",
            "Elincia (DLC)", "Eirika (DLC)", "Lyn (DLC)",
            "Ephraim (DLC)", "Celica (DLC)", "Ike (DLC)",
            "Palla (DLC)", "Catria (DLC)", "Est (DLC)",
            "Katarina (DLC)"
    );

    /*
    Ordered in groups of 4
     */
    public static final List<String> skillNames = Arrays.asList(
            /*00*/ "No Active Skill", "HP +5", "Strength +2", "Magic +2",
            /*04*/ "Skill +2", "Speed +2", "Defense +2", "Resistance +2",
            /*08*/ "Hit rate +10", "Hit rate +20", "Avoid +10", "Movement +1",
            /*12*/ "Locktouch", "Veteran", "Aptitude", "Discipline",
            /*16*/ "Armsthrift", "Dual Support+", "Dual Strike+", "Dual Guard+",
            /*20*/ "Rightful King", "Odd Rhythm", "Even Rhythm", "Quick Burn",
            /*24*/ "Slow Burn", "Lucky Seven", "Gamble", "Outdoor Fighter",
            /*28*/ "Indoor Fighter", "Tantivy", "Focus", "Zeal",
            /*32*/ "Wrath", "Prescience", "Patience", "Underdog",
            /*36*/ "Charm", "Solidarity", "Demoiselle", "Hex",
            /*40*/ "Anathema", "Healtouch", "Relief", "Renewal",
            /*44*/ "Deliverer", "Defender", "Acrobat", "Pass",
            /*48*/ "Swordfaire", "Lancefaire", "Axefaire", "Bowfaire",
            /*52*/ "Tomefaire", "Luck +4", "Special Dance", "Rally Strength",
            /*56*/ "Rally Magic", "Rally Skill", "Rally Speed", "Rally Luck",
            /*60*/ "Rally Defense", "Rally Resistance", "Rally Movement", "Rally Spectrum",
            /*64*/ "Swordbreaker", "Lancebreaker", "Axebreaker", "Bowbreaker",
            /*68*/ "Tomebreaker", "Wyrmsbane", "Beastbane", "Lethality",
            /*72*/ "Aether", "Astra", "Sol", "Luna",
            /*76*/ "Ignis", "Vengeance", "Vantage", "Pavise",
            /*80*/ "Aegis", "Counter", "Miracle", "Despoil",
            /*84*/ "Galeforce", "Lifetaker", "Conquest", "Shadowgift",
            /*88*/ "All Stats +2", "Paragon", "Iote's Shield", "Limit Breaker",
            /*92*/ "Dragonskin", "Rightful God", "Hawkeye", "Luna+",
            /*96*/ "Vantage+", "Pavise+", "Aegis+", "Resistance +10",
            /*100*/ "Aggressor", "Rally Heart", "Bond", "Unused Skill Slot"
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

    public static final List<String> retireChapters = Arrays.asList(
            "None", "Premonition", "Southtown",

            "West of Ylisstol", "The Northroad", "The Longfort", "Arena Ferox",
            "Border Pass", "Ylisstol", "Breakneck Pass", "Border Sands",
            "Plegia Castle Courtyard", "The Midmire", "Border Wastes", "Port Ferox",
            "Carrion Isle", "The Searoad", "Valm Harbour", "The Mila Tree",
            "Fort Steiger", "The Demon's Ingle", "Valm Castle Approach", "Valm Castle",
            "Plegia Castle", "Table Approach", "The Dragon's Table", "Mount Prism",
            "Origin Peak", "Grima",

            "The Farfort", "The Twin's Turf", "Peaceful Village", "The Twin's Hideout",
            "Sage's Hamlet", "Great Gate", "Mila Shrine Ruins", "Duelling Grounds",
            "Verdant Forest", "Mercenary Fortress", "Wyvern Valley", "The Ruins of Time",
            "Law's End", "Desert Oasis", "Kidnapper's Keep", "Manor of Lost Souls",
            "Divine Dragon Grounds", "Sea-King's Throne", "Conqueror's Whetstone", "Mountain Village",
            "Warrior's Tomb", "Wellspring of Truth", "Garden of Giants",
            "Outrealm Gate", "Unknown", "Plegia Castle"
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
}
