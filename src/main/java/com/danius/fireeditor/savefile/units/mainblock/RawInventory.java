package com.danius.fireeditor.savefile.units.mainblock;

import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.inventory.TranBlock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RawInventory {
    public List<RawItem> items;

    public RawInventory(byte[] blockBytes) {
        items = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
            byte[] itemBytes = Arrays.copyOfRange(blockBytes, i * 5, i * 5 + 0x5);
            items.add(new RawItem(itemBytes));
        }
    }

    public void maxAmount() {
        for (RawItem item : items) {
            int itemId = item.itemId();
            //Only vanilla and non-forged items are modified
            if (itemId <= Constants.MAX_ITEM_COUNT) {
                item.setUses(TranBlock.itemAmounts.get(itemId));
            }
        }
    }

    /*
    Combines all the items
     */
    public byte[] getBlockBytes() throws IOException {
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
