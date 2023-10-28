package com.danius.fireeditor.model;

import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.inventory.Refinement;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ItemDb {
    private static final int MAX_FORGED = Constants.MAX_FORGE_COUNT;
    private static final ItemDb database = new ItemDb();
    private List<ItemModel> itemList;

    public static int MOD_MAX_ID = getMaxItemId();

    public ItemDb() {
        readItems();
    }

    private static ItemModel getItem(int id) {
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
            }
            else {
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
        itemList = new ArrayList<>();
        String path = "/com/danius/fireeditor/database/";
        String xmlFilePath = path + "items.xml";
        try (InputStream is = UnitDb.class.getResourceAsStream(xmlFilePath)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + xmlFilePath);
            }
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(is);
            Element rootElement = document.getRootElement();
            String[] buffs = new String[]{"hp", "str", "mag", "skl", "spd", "lck", "def", "res"};
            // Iterate through character elements in the XML
            for (Element itemElement : rootElement.getChildren("item")) {
                ItemModel itemModel = new ItemModel();
                // Parse attributes from the XML
                itemModel.setId(Integer.parseInt(itemElement.getAttributeValue("id")));
                itemModel.setName(itemElement.getAttributeValue("name"));

                //Buffs
                Element elemBuff = itemElement.getChild("buffs");
                int[] base = new int[buffs.length];
                for (int i = 0; i < base.length; i++) {
                    String value = elemBuff.getAttributeValue(buffs[i]);
                    base[i] = Integer.parseInt(value);
                }
                itemModel.setBuffs(base);

                //Buffs
                Element elemStats = itemElement.getChild("stats");
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
