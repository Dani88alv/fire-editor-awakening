package com.danius.fireeditor.savefile.inventory;

import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    }

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
        int uses = itemAmounts.get(id) * amount;
        if (uses == 0) uses = amount; //Special Weapons
        setItemUses(id, uses);
    }

    //Sets the amount (not uses) of a forged weapon
    public void setForgedAmount(int slot, int amount, int weaponId) {
        int uses = itemAmounts.get(inventoryRefi.get(weaponId)) * amount;
        if (uses == 0) uses = amount; //Special Weapons
        setForgedUses(slot, uses);
    }

    //Maxes the full main inventory
    public void maxItemAmount(int weaponAmount, int consumeAmount) {
        for (int i = 1; i < itemAmounts.size(); i++) {
            if (i < 0x9C) setItemAmount(i, weaponAmount);
            else setItemAmount(i, consumeAmount);
        }
    }

    //Maxes the full refinement inventory
    public void maxForgedAmounts(List<Refinement> refiList) {
        for (int i = 0; i < refiList.size(); i++) {
            int amount = itemAmounts.get(refiList.get(i).weaponId());
            setForgedUses(i, amount);
        }
    }

    public static String amountString(int id, int amount) {
        int maxValue = 0;
        if (id < itemAmounts.size()) maxValue = itemAmounts.get(id);
        if (amount == 0) return "None";
        if (maxValue == 0) maxValue = 1;
        int quotient = amount / maxValue;
        int remainder = amount % maxValue;
        String text = "";
        if (quotient == 0 && remainder == 0) return "None";
        if (quotient > 0) text += quotient + " full ";
        if (remainder > 0) text += remainder + "/" + maxValue + " uses";
        return text;
    }

    //Max values of each item
    public static final List<Integer> itemAmounts = Arrays.asList(
            0, 50, 40, 35, 30, 30, 25, 25,
            30, 25, 35, 25, 35, 30, 30, 0,
            0, 0, 25, 25, 25, 25, 25, 25,
            0, 20, 10, 3, 10, 20, 25, 10,
            20, 20, 15, /*Lances*/ 50, 40, 35, 30, 30,
            25, 25, 25, 25, 35, 30, 30, 25,
            25, 25, 20, 10, 20, 3, 10, 15,
            20, 25, /*Axes*/ 50, 40, 35, 30, 30, 25,
            25, 25, 25, 30, 30, 30, 35, 25,
            25, 25, 20, 10, 10, 3, 10, 20,
            20, 15, /*Bows*/ 50, 40, 35, 30, 30, 35,
            30, 25, 30, 25, 25, 25, 25, 20,
            10, 15, 3, 10, 25, 15, /*Spells*/ 45, 35,
            30, 25, 25, 45, 35, 30, 25, 25,
            45, 35, 30, 25, 25, 25, 25, 45,
            20, 20, 30, 25, 0, 10, 3, 15,
            10, 20, 5, 20, 10, /*Staves*/ 30, 20, 10,
            15, 5, 1, 5, 5, 1, 20, 15,
            5, /*Other*/ 50, 35, 50, 35, 0, 0, 0,
            /*Consumables*/ 3, 3, 3, 3, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 0, 0, 0, 3,
            5, 5, 1, 1, 1, 1, 0, 1,
            1, 1, 1, 0, 1, 1, 0, 0,
            0, 0
    );
}
