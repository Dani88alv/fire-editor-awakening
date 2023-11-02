package com.danius.fireeditor.data;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.data.model.ItemModel;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.inventory.Refinement;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.mainblock.RawItem;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemDb {
    private static final int MAX_FORGED = Constants.MAX_FORGE_COUNT;
    private static final ItemDb database = new ItemDb();
    private List<ItemModel> itemList;

    public static int MOD_MAX_ID = getMaxItemId();

    public ItemDb() {
        readItems();
    }

    public static ItemModel getItem(int id) {
        for (ItemModel item : database.itemList) {
            if (item.getId() == id) return item;
        }
        return new ItemModel();
    }

    public static String getItemName(int id) {
        if (!isInvalid(id)) return getItem(id).getName();
        //If it is a modded item
        if (id <= MOD_MAX_ID) {
            int vanillaCount = getItemCountVanilla();
            return "Modded Item #" + (id - vanillaCount + 1);
        }
        //Refinement
        else return "Forged Weapon #" + (id - MOD_MAX_ID);
    }

    public static List<String> getItemNamesRegular() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i <= MOD_MAX_ID; i++) {
            list.add(getItemName(i));
        }
        return list;
    }

    public static List<String> getItemNamesAll() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < MOD_MAX_ID + MAX_FORGED; i++) {
            list.add(getItemName(i));
        }
        return list;
    }

    public static List<String> getItemNamesAll(List<Refinement> refiList) {
        List<String> names = getItemNamesRegular();
        //All the individual refinement IDs are stored
        List<Integer> positions = new ArrayList<>();
        for (Refinement refinement : refiList) {
            positions.add(refinement.position());
        }
        //The forged names are added
        for (int i = 0; i < MAX_FORGED; i++) {
            if (positions.contains(i)) {
                names.add(getRefinement(i, refiList).getName());
            } else {
                names.add(getItemName(i + MOD_MAX_ID + 1));
            }
        }
        return names;
    }

    private static Refinement getRefinement(int position, List<Refinement> refiList) {
        for (Refinement refinement : refiList) {
            if (refinement.position() == position) return refinement;
        }
        return refiList.get(0);
    }

    public static int getItemUses(int id) {
        if (isInvalid(id)) return 0;
        else return getItem(id).getUses();
    }

    public static int getItemUses(Refinement refinement) {
        int id = refinement.weaponId();
        return getItemUses(id);
    }

    public static int getItemType(int id) {
        if (isInvalid(id)) return 10;
        return getItem(id).getType1();
    }

    public static int[] getItemBuffs(int id) {
        if (isInvalid(id)) return new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        else return getItem(id).getBuffs();
    }

    public static List<Integer> faireTypes(Unit unit) {
        List<Integer> types = new ArrayList<>();
        int[] skills = unit.rawBlock2.getCurrentSkills();
        for (int skill : skills) {
            if (skill == 48) types.add(0); //Sword
            else if (skill == 49) types.add(1); //Lance
            else if (skill == 50) types.add(2); //Axe
            else if (skill == 51) types.add(3); //Bow
            else if (skill == 52) types.add(4); //Tome
        }
        //The duplicated types are removed
        Set<Integer> uniqueTypes = new HashSet<>(types);
        return new ArrayList<>(uniqueTypes);
    }

    public static int[] getFaireBuffs(Unit unit) {
        int[] totalBuffs = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        List<Integer> faireTypes = faireTypes(unit);
        //Loops through all the equipped faire skills
        for (int i = 0; i < faireTypes.size(); i++) {
            RawItem item = unit.rawInventory.items.get(i);
            if (item.equipped()) {
                int type = faireTypes.get(i);
                int itemId = item.itemId();
                int[] buffs = getFaireBuff(itemId, type);
                for (int k = 0; k < totalBuffs.length; k++) {
                    totalBuffs[k] += buffs[k];
                }
            }
        }
        return totalBuffs;
    }

    public static int[] getFaireBuff(int id, int faireType) {
        int[] totalBuffs = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        int type = getItemType(id);
        ItemModel item = getItem(id);
        //Sword
        if (type == 0 && type == faireType) {
            if (item.isMagic()) totalBuffs[2] += 5;
            else totalBuffs[1] += 5;
        }
        //Lance
        else if (type == 1 && type == faireType) {
            if (item.isMagic()) totalBuffs[2] += 5;
            else totalBuffs[1] += 5;
        }
        //Axe
        else if (type == 2 && type == faireType) {
            if (item.isMagic()) totalBuffs[2] += 5;
            else totalBuffs[1] += 5;
        }
        //Bows
        else if (type == 3 && type == faireType) {
            if (item.isMagic()) totalBuffs[2] += 5;
            else totalBuffs[1] += 5;
        }
        //Tomes
        else if (type == 4 && type == faireType) {
            totalBuffs[2] += 5;
        }

        return totalBuffs;
    }

    public static int getItemMight(int id) {
        if (isInvalid(id)) return 0;
        return getItem(id).getMight();
    }

    public static int getItemHit(int id) {
        if (isInvalid(id)) return 0;
        return getItem(id).getHit();
    }

    public static int getItemCrit(int id) {
        if (isInvalid(id)) return 0;
        return getItem(id).getCrit();
    }

    public static String getAmountString(int id, int amount) {
        if (amount == 0) return "None";
        int maxValue = 0;
        //If it is a valid item, get from the database
        if (id < getItemCountVanilla()) maxValue = getItemUses(id);
        //If the item has infinite uses, consider it as 1 use for the sake of the string
        if (maxValue == 0) maxValue = 1;
        //The string is created
        int quotient = amount / maxValue;
        int remainder = amount % maxValue;
        String text = "";
        if (quotient == 0 && remainder == 0) return "None";
        if (quotient > 0) text += quotient + " full ";
        if (remainder > 0) text += remainder + "/" + maxValue + " uses";
        return text;
    }

    private static boolean isInvalid(int id) {
        return id < 0 || id >= getItemCountVanilla();
    }

    public static int getMaxItemId() {
        return database.itemList.size() - 1;
    }

    public static int getItemCountVanilla() {
        return database.itemList.size();
    }

    public void readItems() {
        File file = FireEditor.readResource(Constants.ADDON_XML + "items.xml");
        String xmlFilePath = Constants.RES_XML + "items.xml";
        itemList = new ArrayList<>();

        try {
            InputStream is;
            // Check if the file exists
            if (file != null && file.exists()) is = new FileInputStream(file);
            else {
                is = UnitDb.class.getResourceAsStream(xmlFilePath);
                if (is == null) throw new FileNotFoundException("Resource not found: " + xmlFilePath);
            }
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(is);
            Element rootElement = document.getRootElement();
            String[] buffs = new String[]{"hp", "str", "mag", "skl", "spd", "lck", "def", "res"};
            // Iterate through character elements in the XML
            for (Element element : rootElement.getChildren("item")) {
                ItemModel itemModel = new ItemModel();
                // Parse attributes from the XML
                itemModel.setId(Integer.parseInt(element.getAttributeValue("id")));
                itemModel.setName(element.getAttributeValue("name"));
                itemModel.setType1(Integer.parseInt(element.getAttributeValue("type1")));

                //Buffs
                Element elemBuff = element.getChild("buffs");
                int[] base = new int[buffs.length];
                for (int i = 0; i < base.length; i++) {
                    String value = elemBuff.getAttributeValue(buffs[i]);
                    base[i] = Integer.parseInt(value);
                }
                itemModel.setBuffs(base);

                boolean isMagicFaire = "true".equals(element.getAttributeValue("isMagic"));
                itemModel.setMagic(isMagicFaire);

                //Refinement Stats
                Element elemStats = element.getChild("stats");
                int uses = Integer.parseInt(elemStats.getAttributeValue("uses"));
                int might = Integer.parseInt(elemStats.getAttributeValue("might"));
                int hit = Integer.parseInt(elemStats.getAttributeValue("hit"));
                int crit = Integer.parseInt(elemStats.getAttributeValue("crit"));
                itemModel.setUses(uses);
                itemModel.setMight(might);
                itemModel.setHit(hit);
                itemModel.setCrit(crit);

                itemList.add(itemModel);
            }
        } catch (IOException | JDOMException ex) {
            throw new RuntimeException(ex);
        }
    }
}
