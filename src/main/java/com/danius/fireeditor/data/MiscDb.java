package com.danius.fireeditor.data;

import com.danius.fireeditor.savefile.units.mainblock.RawSupport;

import java.util.ArrayList;
import java.util.Arrays;
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
}
