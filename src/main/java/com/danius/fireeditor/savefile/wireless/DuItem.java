package com.danius.fireeditor.savefile.wireless;

import com.danius.fireeditor.savefile.inventory.Refinement;
import com.danius.fireeditor.util.Hex;
import javafx.scene.layout.Region;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class DuItem {

    private byte[] rawStats;
    private byte[] rawName;

    public DuItem(byte[] bytes) {
        this.rawStats = Arrays.copyOfRange(bytes, 0x0, 0x4);
        this.rawName = Arrays.copyOfRange(bytes, 0x4, bytes.length);
    }

    public Refinement toRefinement(){
        int byteSize = (isWest()) ? Refinement.SIZE_US : Refinement.SIZE_JP;
        byte[] refiBytes = new byte[byteSize];
        Refinement refinement = new Refinement(refiBytes);
        refinement.setName(getName());
        refinement.setWeaponId(getItemId());
        refinement.setMight(might());
        refinement.setHit(hit());
        refinement.setCrit(crit());
        return refinement;
    }

    public boolean isRefinement() {
        return !(getName().equals("") && might() == 0 && crit() == 0 && hit() == 0);
    }

    public int getItemId() {
        return rawStats[0x0] & 0xFF;
    }

    public void setItemId(int value) {
        int point = 0x0;
        rawStats[point] = (byte) (value & 0xFF);
    }

    public int might() {
        int point = 0x1;
        return rawStats[point] & 0xFF;
    }

    public void setMight(int value) {
        int point = 0x1;
        rawStats[point] = (byte) (value & 0xFF);
    }

    public int hit() {
        int point = 0x2;
        return rawStats[point] & 0xFF;
    }

    public void setHit(int value) {
        int point = 0x2;
        rawStats[point] = (byte) (value & 0xFF);
    }

    public int crit() {
        int point = 0x3;
        return rawStats[point] & 0xFF;
    }

    public void setCrit(int value) {
        int point = 0x3;
        rawStats[point] = (byte) (value & 0xFF);
    }

    public String getName() {
        byte[] nameArray = Arrays.copyOfRange(rawName, 0x0, (rawName.length));
        return Hex.byteArrayToString(nameArray);
    }

    public void setName(String name) {
        if (name.length() > rawName.length) name = name.substring(0, rawName.length);
        byte[] nameBytes = Hex.stringToByteArray(name, rawName.length);
        System.arraycopy(nameBytes, 0, rawName, 0, nameBytes.length);
    }

    public boolean isWest() {
        return rawName.length == DuTeam.US_WEAPON;
    }

    public void changeRegion(boolean isWest) {
        int nameSize = (isWest) ? DuTeam.US_WEAPON : DuTeam.JP_WEAPON;
        rawName = Hex.changeSizeArray(rawName, nameSize);
    }

    public int length() {
        return rawName.length + rawStats.length;
    }

    public byte[] bytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(rawStats);
            byteArrayOutputStream.write(rawName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public String toString() {
        return getName();
    }
}
