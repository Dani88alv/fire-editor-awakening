package com.danius.fireeditor.savefile.inventory;

import com.danius.fireeditor.data.ItemDb;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.danius.fireeditor.data.ItemDb.*;

public class TranBlock {
    /*
    Stores both vanilla and modded items (size varies with mods)
     */
    public static final int MAX_FORGED = Constants.MAX_FORGE_COUNT;
    /*
    Stores only the amount of items and forged weapons (Always 150 entries)
     */
    private final byte[] blockHeader;
    public List<Integer> inventoryMain; //List of uses of each regular item
    public List<Integer> inventoryRefi; //List of uses of each forged weapon

    public TranBlock(byte[] blockBytes) {
        blockHeader = Arrays.copyOfRange(blockBytes, 0x0, 0x7); //5 header + 2 item count
        int itemCount = Hex.getByte2(blockBytes, 0x5);
        int regularCount = itemCount - MAX_FORGED;

        inventoryMain = getItemList(blockBytes, 0x0, regularCount);
        inventoryRefi = getItemList(blockBytes, regularCount * 2, MAX_FORGED);
        //The modded count is updated
        ItemDb.MOD_MAX_ID = itemCount - MAX_FORGED - 1;
    }

    public void setItemUses(int id, int uses) {
        this.inventoryMain.set(id, uses);
    }

    public void setForgedUses(int id, int uses) {
        this.inventoryRefi.set(id, uses);
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
        if (uses == 0) uses = amount; //Infinite use weapons
        setItemUses(id, uses);
    }

    //Sets the amount (not uses) of a forged weapon
    public void setForgedAmount(int slot, int amount, int weaponId) {
        int uses = getItemUses(weaponId) * amount;
        if (uses == 0) uses = amount; //Infinite use weapons
        setForgedUses(slot, uses);
    }

    //Maxes the full main inventory
    public void maxItemAmount(int amount) {
        for (int i = 1; i < getItemCountVanilla(); i++) {
            setItemAmount(i, amount);
        }
    }

    //Maxes the full refinement inventory
    public void maxForgedAmount(int amount, List<Refinement> refiList) {
        for (Refinement refinement : refiList) {
            int maxUses = getItemUses(refinement.weaponId()) * amount;
            int currentUses = inventoryRefi.get(refinement.position());
            if (currentUses > maxUses) while (maxUses <= currentUses) maxUses *= 2;
            setForgedUses(refinement.position(), maxUses);
        }
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
}
