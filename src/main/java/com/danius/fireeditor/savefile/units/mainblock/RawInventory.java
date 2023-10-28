package com.danius.fireeditor.savefile.units.mainblock;

import com.danius.fireeditor.model.ItemDb;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.inventory.Refinement;
import com.danius.fireeditor.savefile.inventory.TranBlock;
import com.danius.fireeditor.model.MiscDb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.danius.fireeditor.model.ItemDb.*;

public class RawInventory {
    public List<RawItem> items;

    public RawInventory() {
        String path = Constants.RES_BLOCK + "rawUnitInventory";
        try {
            byte[] bytes = Objects.requireNonNull(RawInventory.class.getResourceAsStream(path)).readAllBytes();
            initialize(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RawInventory(byte[] blockBytes) {
        initialize(blockBytes);
    }

    public void initialize(byte[] blockBytes) {
        items = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
            byte[] itemBytes = Arrays.copyOfRange(blockBytes, i * 5, i * 5 + 0x5);
            items.add(new RawItem(itemBytes));
        }
    }

    public void maxAmount() {
        for (RawItem item : items) {
            int itemId = item.itemId();
            //Only regular items are modified
            if (itemId < getItemCountVanilla()) {
                item.setUses(getItemUses(itemId));
            }
        }
    }

    //Sets the max amount of an item, using the refinement list
    public void maxAmount(List<Refinement> refiList) {
        int maxVanillaId = getMaxItemId();
        int maxModId = MOD_MAX_ID;
        for (RawItem item : items) {
            int itemId = item.itemId();
            //Modded Items
            if (itemId > maxVanillaId && itemId <= maxModId) {
            }
            //Forged Weapons
            else if (itemId > maxModId && itemId <= maxModId + Constants.MAX_FORGE_COUNT) {
                int position = itemId - maxModId - 1;
                boolean found = false;
                for (Refinement refinement : refiList) {
                    if (refinement.position() == position) {
                        int weaponId = refinement.weaponId();
                        item.setUses(getItemUses(weaponId));
                        found = true;
                    }
                }
                //If no valid weapon was found, it's removed to avoid problems
                if (!found) {
                    item.removeItem();
                }
            }
            //Regular items
            else {
                item.setUses(getItemUses(itemId));
            }
        }
    }

    /*
    Combines all the items
     */
    public byte[] bytes() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (RawItem item : items) outputStream.write(item.bytes());
        return outputStream.toByteArray();
    }

    public String report() {
        String text = "";
        text += "\n" + "Items: ";
        for (RawItem item : items) {
            text += item.report() + ", ";
        }
        text = text.substring(0, text.length() - 2);
        return text;
    }

    public int length() {
        return 0x5 * 5;
    }
}
