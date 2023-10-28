package com.danius.fireeditor.savefile.inventory;

import com.danius.fireeditor.model.ItemDb;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.danius.fireeditor.model.ItemDb.*;

public class TranBlock {
    /*
    Regular item count is different with and without DLC?
     */
    public static final int MAX_FORGED = 150;
    private final int itemCount;
    /*
    Stores only the amount of items and forged weapons
     */
    private final byte[] blockHeader;
    public List<Integer> inventoryMain; //List of uses of each regular item
    public List<Integer> inventoryRefi; //List of uses of each forged weapon

    public TranBlock(byte[] blockBytes) {
        blockHeader = Arrays.copyOfRange(blockBytes, 0x0, 0x7); //5 header + 2 item count
        itemCount = Hex.getByte2(blockBytes, 0x5);
        int regularCount = regularItemCount();

        inventoryMain = getItemList(blockBytes, 0x0, regularCount);
        inventoryRefi = getItemList(blockBytes, regularCount * 2, MAX_FORGED);
        //The modded count is updated
        ItemDb.MOD_MAX_ID = itemCount - MAX_FORGED - 1;
    }

    //TODO REMOVE THIS
    //Retrieves the total count of regular items, since there can be modded items
    public int regularItemCount() {
        return itemCount - MAX_FORGED;
    }

    public void setItemUses(int id, int uses) {
        this.inventoryMain.set(id, uses);
    }

    public void setForgedUses(int id, int uses) {
        this.inventoryRefi.set(id, uses);
    }

    //Combines both inventories together
    public byte[] getBlockBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(blockHeader);
            // Regular Items Amount
            for (int amount : inventoryMain) {
                outputStream.write(Hex.int2ToByteArray(amount));
            }
            // Forged Weapons Amount
            for (int amount : inventoryRefi) {
                outputStream.write(Hex.int2ToByteArray(amount));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    //The size of the main inventory can change with mods
    public void setItemAmountFromList(List<Integer> uses) {
        for (int i = 0; i < inventoryMain.size(); i++) {
            inventoryMain.set(i, uses.get(i));
        }
    }

    public List<Integer> getItemList(byte[] blockBytes, int offset1, int size) {
        List<Integer> regularItems = new ArrayList<>();
        int index = blockHeader.length + offset1;
        for (int i = index; i < (size * 2) + index; i += 2) {
            int amount = Hex.getByte2(blockBytes, i);
            regularItems.add(amount);
        }
        return regularItems;
    }

    //Sets the amount (not uses) of a regular item
    public void setItemAmount(int id, int amount) {
        int uses = getItemUses(id) * amount;
        if (uses == 0) uses = amount; //Special Weapons
        setItemUses(id, uses);
    }

    //Sets the amount (not uses) of a forged weapon
    public void setForgedAmount(int slot, int amount, int weaponId) {
        int uses = getItemUses(inventoryRefi.get(weaponId)) * amount;
        if (uses == 0) uses = amount; //Special Weapons
        setForgedUses(slot, uses);
    }

    //Maxes the full main inventory
    public void maxItemAmount(int weaponAmount, int consumeAmount) {
        for (int i = 1; i < getItemCountVanilla(); i++) {
            if (i < 0x9C) setItemAmount(i, weaponAmount);
            else setItemAmount(i, consumeAmount);
        }
    }

    //Maxes the full refinement inventory
    public void maxForgedAmounts(List<Refinement> refiList) {
        for (Refinement refinement : refiList) {
            int maxUses = getItemUses(refinement.weaponId());
            int currentUses = inventoryRefi.get(refinement.position());
            if (currentUses > maxUses) while (maxUses <= currentUses) maxUses *= 2;
            setForgedUses(refinement.position(), maxUses);
        }
    }
}
