package com.danius.fireeditor.util;

import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.units.Supports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Names {

    //TODO: usar este método a partir de ahora y hacer la lista private
    public static String unitName(int id) {
        if (id == 0xFFFF) return "None";
        if (id >= 4096) {
            id = id - 4096;
            return "Map NPC " + (id + 1);
        }
        if (id >= unitNames.size()) return "Outrealm Unit #" + (unitNames.size() - id + 1);
        return unitNames.get(id);
    }

    public static String className(int id) {
        if (id > Constants.MAX_CLASSES) return "Outrealm Class " + (Constants.MAX_CLASSES - id + 2);
        return classNames.get(id);
    }

    public static String itemName(int id) {
        if (id <= itemNames.size()) return itemNames.get(id);
        return "Forged Weapon " + (Integer.toHexString(id));
    }

    public static String itemName2(int id, int maxCount) {
        int vanillaCount = Names.itemNames.size();
        if (id >= vanillaCount && id < maxCount) {
            int itemId = id - vanillaCount + 1;
            return "Modded Item #" + itemId;
        } else if (id >= maxCount && id <= maxCount + 150) {
            int itemId = id - maxCount + 1;
            return "Forged Weapon #" + itemId;
        } else return itemNames.get(id);
    }

    public static String supportLevel(int unitId, int value, int slot) {
        //Checks if it is a modded unit
        int[] validSupports = Supports.getSupportUnits(unitId);
        if (slot >= validSupports.length) return "Unknown";
        //Valid supports
        int type = Supports.getSupportTypes(unitId)[slot];
        int[] maxValues = Supports.supportValues().get(type);
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

    public static final List<String> unitNames = Arrays.asList(
            "Avatar (M)", "Avatar (F)", "Logbook Unit", "Chrom",
            "Lissa", "Frederick", "Virion", "Sully", "Vaike", "Stahl", "Miriel",
            "Kellam", "Sumia", "Lon'qu", "Ricken", "Maribelle", "Panne", "Gaius",
            "Cordelia", "Gregor", "Nowi", "Libra", "Tharja", "Olivia", "Cherche",
            "Henry", "Lucina", "Say'ri", "Basilio", "Flavia", "Donnel", "Anna",
            "Owain", "Inigo", "Brady", "Kjelle", "Cynthia", "Severa", "Gerome",
            "Morgan (M)", "Morgan (F)", "Yarne", "Laurent", "Noire", "Nah",
            "Tiki", "Gangrel", "Walhart", "Emmeryn", "Yen'fay", "Aversa", "Priam",
            "Marth", "Dummy/Maiden", "Monster", "Monster (+)", "Merchant"
    );


    public static final List<String> dlcNames = Arrays.asList(
            "Pr. Marth (DLC)", "Roy (DLC)", "Micaiah (DLC)",
            "Leif (DLC)", "Alm (DLC)", "Seliph (DLC)",
            "Elincia (DLC)", "Eirika (DLC)", "Lyn (DLC)",
            "Ephraim (DLC)", "Celica (DLC)", "Ike (DLC)",
            "Palla (DLC)", "Catria (DLC)", "Est (DLC)",
            "Katarina (DLC)"
    );

    public static final List<String> spotPassNames = Arrays.asList(
            "Nyna", "Caeda", "Linde", "Merric", "Tiki",
            "Minerva", "Ogma", "Navarre", "Gharnef", "Pr. Marth",
            "Clair", "Boey", "Mycen", "Valbar", "Luthier",
            "Clive", "Nomah", "Deen", "Celica", "Alm",
            "Norne", "Catria", "Malice", "Athena", "Horace",
            "Etzel", "Legion", "Katarina", "Hardin", "King Marth",
            "Deirdre", "Arden", "Jamke", "Raquesis", "Ethlyn",
            "Quan", "Ayra", "Lewyn", "Arvis", "Sigurd",
            "Fee", "Arthur", "Ulster", "Larcei", "Altena",
            "Ced", "Julia", "Ares", "Julius", "Seliph",
            "Nanna", "Dagdar", "Salem", "Olwen", "Eyvel",
            "Finn", "Saias", "Mareeta", "Raydrik", "Leif",
            "Wolt", "Shanna", "Lugh", "Raigh", "Sophia",
            "Cecilia", "Perceval", "Lilina", "Zephiel", "Roy",
            "Florina", "Nino", "Serra", "Matthew", "Karel",
            "Jaffar", "Lyn", "Hector", "Nergal", "Eliwood",
            "Eirika", "Amelia", "Moulder", "Lute", "Marisa",
            "Innes", "L'Arachel", "Seth", "Lyon", "Ephraim",
            "Mist", "Soren", "Mia", "Zihark", "Titania",
            "Elincia", "Geoffrey", "Lucia", "Ashnard", "Ike",
            "Edward", "Leonardo", "Brom", "Nephenee", "Sanaki",
            "Sigrun", "Sothe", "Black Knight", "Sephiran", "Micaiah",
            "Camus", "Travant", "Ishtar", "Narcian", "Lloyd",
            "Linus", "Ursula", "Selena", "Petrine", "Oliver",
            "Eldigan"
    );

    public static final List<String> classNames = Arrays.asList(
            "Lord M", "Lord F", "Great Lord M", "Great Lord F", "Tactician M",
            "Tactician F", "Grandmaster M", "Grandmaster F", "Cavalier M",
            "Cavalier F", "Knight M", "Knight F", "Paladin M", "Paladin F",
            "Great Knight M", "Great Knight F", "General M", "General F",
            "Barbarian", "Fighter", "Mercenary M", "Mercenary F", "Archer M",
            "Archer F", "Berserker", "Warrior", "Hero M", "Hero F", "Bow Knight M",
            "Bow Knight F", "Sniper M", "Sniper F", "Myrmidon M", "Myrmidon F", "Thief M",
            "Thief F", "Swordmaster M", "Swordmaster F", "Assassin M", "Assassin F",
            "Trickster M", "Trickster F", "Pegasus Knight", "Falcon Knight",
            "Dark Flier", "Wyvern Rider M", "Wyvern Rider F", "Wyvern Lord M",
            "Wyvern Lord F", "Griffon Rider M", "Griffon Rider F", "Troubadour", "Priest",
            "Cleric", "Mage M", "Mage F", "Dark Mage M", "Dark Mage F", "Valkyrie", "War Monk",
            "War Cleric", "Sage M", "Sage F", "Dark Knight M", "Dark Knight F", "Sorcerer M",
            "Sorcerer F", "Dancer", "Manakete", "Taguel M", "Taguel F", "Soldier", "Villager",
            "Merchant", "Reverant", "Entombed", "Conqueror", "Lodestar", "Grima", "Mirage",
            "Dread Fighter", "Bride", "Dummy"
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
            /*100*/ "Aggressor", "Rally Heart", "Bond", "Dummy"
    );

    public static final List<String> armies = Arrays.asList(
            "None", "Other", "Halidom of Ylisse", "Regna Ferox",
            "Plegia", "Empire of Valm", "Grimleal", "Rebels",
            "Unknown Foe", "Risen", "Ruffians", "Villagers"
    );

    public static final List<String> traitFlags = Arrays.asList(
            "Female", "Hero", "Player", "Advanced Class",
            "Boss/Leader", "Defeat Condition", "Movement Ban", "Force Battle Animations",
            "Battle Animation Ban", "Defeated Experience +20", "Defeated Experience -10", "Unknown_1_3",
            "Marth/Lucina", "Walhart", "Aversa", "Owain",
            "Manakete", "Taguel", "Destroy Villages", "Crit Ban",
            "Avoid Ban", "Enemy Only", "Special Performances", "Guest",
            "Original Marth", "Entombed Experience", "Delivery Face", "White Dragon",
            "Unknown_3_4", "Unknown_3_5", "Unknown_3_6", "Unknown_3_7"
    );

    public static final List<String> battleFlags = Arrays.asList(
            "Moved in this turn", "Paired Up (Front)", "Paired Up (Back)", "Unknown_0_3",
            "Unknown_0_4", "Unknown_0_5", "Unknown_0_6", "Unknown_0_7",
            "Unknown_1_0", "Unknown_1_1", "Unknown_1_2", "Unknown_1_3",
            "Unknown_1_4", "Unknown_1_5", "Unknown_1_6", "Unknown_1_7",
            "Unknown_2_0", "Unknown_2_1", "Unknown_2_2", "Galeforce Used?",
            "Unknown_2_4", "Unknown_2_5", "Married to Maiden", "Unknown_2_7",
            "Unknown_3_0", "Tiki Meditating", "Unknown_3_2", "Unknown_3_3",
            "Unknown_3_4", "Unknown_3_5", "Unknown_3_6", "Unknown_3_7"
    );

    public static List<String> getItemNames(int totalCount) {
        List<String> names = new ArrayList<>();
        int vanillaCount = itemNames.size();
        for (int i = 0; i < totalCount; i++) {
            if (i >= vanillaCount) names.add("Modded Item #" + (i - vanillaCount + 1));
            else names.add(itemNames.get(i));
        }
        return names;
    }

    /*
    None: 0x0
    Swords: 0x1-0x22
    Spears: 0x23-0x39
    Axes: 0x3A-0x51
    Bows: 0x52-0x65
    Spells: 0x66-0x84
    Staves: 0x85-0x90
    Other Weapons: 0x91-0x97
    Consumables: 0x98-0xC9
     */
    public static final List<String> itemNames = Arrays.asList(
            "None", "Bronze Sword", "Iron Sword", "Steel Sword",
            "Silver Sword", "Brave Sword", "Armorslayer", "Wyrmslayer",
            "Killing Edge", "Levin Sword", "Rapier", "Noble Rapier",
            "Missletainn", "Sol", "Amatsu", "Falchion",
            "Exalted Falchion", "Parallel Falchion", "Mercurius", "Tyrfing",
            "Mystletainn", "Balmung", "Sol Katti", "Ragnell",
            "Ragnell (Inf. Uses)", "Tree Branch", "Soothing Sword", "Glass Sword",
            "Superior Edge", "Eliwood's Blade", "Roy's Blade", "Alm's Blade",
            "Leif's Blade", "Eirika's Blade", "Seliph's Blade", "Bronze Lance",
            "Iron Lance", "Steel Lance", "Silver Lance", "Brave Lance",
            "Javelin", "Short Spear", "Spear", "Beast Killer",
            "Blessed Lance", "Killer Lance", "Luna", "Gradivus",
            "Gungnir", "Gae Bolg", "Log", "Miniature Lance",
            "Shockstick", "Glass Lance", "Superior Lance", "Sigurd's Lance",
            "Ephraim's Lance", "Finn's Lance", "Bronze Axe", "Iron Axe",
            "Steel Axe", "Silver Sxe", "Brave Axe", "Hand Axe",
            "Short Axe", "Tomahawk", "Hammer", "Bolt Axe",
            "Killer Axe", "Vengeance", "Wolf Berg", "Hauteclere",
            "Helswath", "Armads", "Ladle", "Imposing Axe",
            "Volant Axe", "Glass Axe", "Superior Axe", "Titania's Axe",
            "Orsin's Hatchet", "Hector's Axe", "Bronze Bow", "Iron Bow",
            "Steel Bow", "Silver Bow", "Brave Bow", "Blessed Bow",
            "Killer Bow", "Long Bow", "Astra", "Parthia",
            "Yewfelle", "Nidhogg", "Double Bow", "Slack Bow",
            "Towering Bow", "Underdog Bow", "Glass Bow", "Superior Bow",
            "Wolt's Bow", "Innes' Bow", "Fire", "Elfire",
            "Arcfire", "Bolganone", "Valflame", "Thunder",
            "Elthunder", "Arcthunder", "Thoron", "Mjollnir",
            "Wind", "Elwind", "Arcwind", "Rexcalibur",
            "Forseti", "Excalibur", "Book of Naga", "Flux",
            "Nosferatu", "Ruin", "Waste", "Goetia",
            "Grima's Truth", "Mire", "Dying Blaze", "Micaiah's Pyre",
            "Superior Jolt", "Katarina's Bolt", "Wilderwind", "Celica's Gale",
            "Aversa's Night", "Heal", "Mend", "Physic",
            "Recover", "Fortify", "Goddess Staff", "Rescue",
            "Ward", "Hammerne", "Kneader", "Balmwood Staff",
            "Catharsis", "Dragonstone", "Dragonstone +", "Beaststone",
            "Beaststone +", "Blighted Claws", "Blighted Talons", "Expiration",
            "Vulnerary", "Concoction", "Elixer", "Pure Water",
            "HP Tonic", "Strength Tonic", "Magic Tonic", "Skill Tonic",
            "Speed Tonic", "Luck Tonic", "Defense Tonic", "Resistance Tonic",
            "Door Key", "Chest Key", "Master Key", "Seraph Robe",
            "Energy Drop", "Spirit Dust", "Secret Book", "Speedwing",
            "Goddess Icon", "Dracoshield", "Talisman", "Naga's Tear",
            "Boots", "Arms Scroll", "Master Seal", "Second Seal",
            "Bullion (S)", "Bullion (M)", "Bullion (L)", "Sweet Tincture",
            "Gaius' Confect", "Kris' Confect", "Tiki's Tear", "Seed of Trust",
            "Reeking Box", "Rift Door", "Supreme Emblem", "All Stats +2",
            "Paragon", "Iote's Shield", "Limit Breaker", "Silver Card",
            "Dread Scroll", "Wedding Bouquet", "1,000 Gold", "3,000 Gold",
            "5,000 Gold", "7,000 Gold"
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
