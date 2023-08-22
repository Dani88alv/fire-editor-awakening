package com.danius.fireeditor.savefile.inventory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RefiBlock {
    public boolean isWest;
    private final byte[] blockHeader;
    public List<Refinement> refiList;

    public RefiBlock(byte[] blockBytes, boolean isWest) {
        this.blockHeader = Arrays.copyOfRange(blockBytes, 0, 0x7); //Header (5) + count + unknown
        this.isWest = isWest;
        refiList = new ArrayList<>();
        readRefinements(blockBytes);
    }

    public String reportCount() {
        return "Forged Weapons: " + refiList.size();
    }

    public void addRefi(Refinement refinement) {
        if (refiList.size() < 150) {
            refiList.add(refinement);
        } else {
            System.out.println("Max Forged Weapon Amount Reached!");
        }
    }

    public List<String> refiNames() {
        List<String> list = new ArrayList<>();
        for (Refinement refi : refiList) {
            list.add(refi.getName());
        }
        for (int i = list.size(); i < 150; i++) {
            list.add("Forged Slot #" + (i + 1));
        }
        return list;
    }

    public List<Integer> refiIds() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < refiList.size(); i++) {
            list.add(refiList.get(i).weaponId());
        }
        for (int i = list.size(); i < 150; i++) {
            list.add(0);
        }
        return list;
    }

    public byte[] getBlockBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        this.blockHeader[0x5] = (byte) (refiList.size() & 0xFF); //The weapon count is updated

        try {
            outputStream.write(blockHeader);
            //All the weapons are looped
            for (int i = 0; i < refiList.size(); i++) {
                refiList.get(i).setBlockPosition(i); //The position is updated
                outputStream.write(refiList.get(i).bytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public void readRefinements(byte[] blockBytes) {
        int amount = blockBytes[0x5] & 0xFF;
        if (amount == 0) return;
        List<Refinement> refiList = new ArrayList<>();
        int refiSize = (isWest) ? Refinement.SIZE_US : Refinement.SIZE_JP;
        int start = blockHeader.length;
        System.out.println("REFINEMENTS:");
        for (int i = 0; i < amount; i++) {
            byte[] refiBytes = Arrays.copyOfRange(blockBytes,
                    (refiSize * i) + start, (refiSize * i) + start + refiSize);
            Refinement refi = new Refinement(refiBytes);
            refiList.add(refi);
            System.out.println(i + " - " + refi.getName());
        }
        this.refiList = refiList;
    }

}
