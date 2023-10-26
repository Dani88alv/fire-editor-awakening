package com.danius.fireeditor.savefile.wireless;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.inventory.Refinement;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.extrablock.LogBlock;
import com.danius.fireeditor.savefile.units.mainblock.RawSkill;
import com.danius.fireeditor.util.Bitflag;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UnitDu {

    public boolean isWest;
    private byte[] rawBlock1;
    public List<DuItem> itemList;
    private byte[] rawChild;
    public RawSkill rawSkill;
    private byte[] rawUnknown;

    public LogBlock rawLog;

    public UnitDu(boolean isWest) {
        String path = Constants.RES_BLOCK + "rawUnitDu";
        try {
            byte[] bytes = Objects.requireNonNull(UnitDu.class.getResourceAsStream(path)).readAllBytes();
            initialize(bytes, null);
            changeRegion(isWest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public UnitDu(byte[] bytes, byte[] extraData) {
        initialize(bytes, extraData);
    }

    public void initialize(byte[] bytes, byte[] extraData) {
        isWest = bytes.length == 0x12F;
        itemList = new ArrayList<>();
        //Main Information
        this.rawBlock1 = Arrays.copyOfRange(bytes, 0x0, 0x21);
        int offset = rawBlock1.length;
        //Inventory
        int itemSize = (isWest) ? DuTeam.US_WEAPON + 0x4 : DuTeam.JP_WEAPON + 0x4;
        for (int i = 0; i < 5; i++) {
            DuItem item = new DuItem(Arrays.copyOfRange(bytes, offset, offset + itemSize));
            itemList.add(item);
            offset += item.length();
        }
        //Child Data
        this.rawChild = Arrays.copyOfRange(bytes, offset, offset + 0x8);
        offset += rawChild.length;
        //Learned Skills
        this.rawSkill = new RawSkill(Arrays.copyOfRange(bytes, offset, offset + 0xD));
        offset += rawSkill.length();
        //Unknown
        this.rawUnknown = Arrays.copyOfRange(bytes, offset, offset + 0x3);
        offset += rawUnknown.length;
        //Avatar Name
        int nameSize = (isWest) ? DuTeam.US_NAME_LOG : DuTeam.JP_NAME_LOG;
        byte[] rawName = Arrays.copyOfRange(bytes, offset, offset + nameSize);
        offset += rawName.length;
        //Profile Card
        byte[] rawAvatar = Arrays.copyOfRange(bytes, offset, bytes.length);
        //All the logbook data is combined
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(rawName);
            byteArrayOutputStream.write(rawAvatar);
            byteArrayOutputStream.write(Objects.requireNonNullElseGet(extraData, () -> new byte[336]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.rawLog = new LogBlock(byteArrayOutputStream.toByteArray());
    }

    public String getName() {
        if (hasFlag(1)) return "Outrealm";
        String avatarName = rawLog.getName();
        if (!avatarName.equals("")) return avatarName;
        return FireEditor.unitDb.getName(getUnitId());
    }

    public int getUnitId() {
        return Hex.getByte2(rawBlock1, 0x1);
    }

    public void setUnitId(int value) {
        Hex.setByte2(rawBlock1, 0x1, value);
    }

    public Unit toUnit() {
        Unit unit = new Unit();
        unit.addBlockChild();
        unit.addBlockLog();
        //General Data
        unit.rawBlock1.setUnitId(getUnitId());
        unit.rawBlock1.setUnitClass(getUnitClass());
        unit.rawBlock1.setLevel(getLevel());
        unit.rawBlock1.setExp(0);
        unit.rawFlags.setHiddenLevel(getHiddenLevel());
        unit.rawBlockEnd.setHairColor(getOffspringColor());
        //Flags
        unit.rawFlags.setBattleFlag(27, hasFlag(1)); //Outrealm
        unit.rawFlags.setTraitFlag(4, hasFlag(4)); //Leader
        if (hasFlag(2) || hasFlag(3)) {
            unit.rawFlags.setBattleFlag(8, true); //Foreign / Enemy
            unit.rawFlags.setBattleFlag(29, true); //Wireless
        }
        //Skills
        unit.rawSkill = this.rawSkill;
        //Stats
        int[] growths = getGrowth();
        int[] weaponExp = getWeaponExp();
        int[] activeSkills = getActiveSkills();
        for (int i = 0; i < growths.length; i++) unit.rawBlock1.setGrowth(growths[i], i);
        for (int i = 0; i < weaponExp.length; i++) unit.rawBlock2.setWeaponExp(weaponExp[i], i);
        for (int i = 0; i < activeSkills.length; i++) unit.rawBlock2.setCurrentSkill(activeSkills[i], i);
        //Extra data
        for (int i = 0; i < 6; i++) unit.rawChild.setParentId(i, getParent(i));
        unit.rawLog = this.rawLog;
        this.rawLog.footer = new byte[]{1}; //Child Terminator
        //Remove extra data if empty
        if (unit.rawLog.isBlank()) {
            unit.removeBlockExtra(true);
        }
        if (unit.rawChild.isBlank()) {
            unit.removeBlockExtra(false);
        }
        return unit;
    }

    public String report() {
        String text = "";
        //General
        text += getName();
        return text;
    }

    public int getUnitClass() {
        return rawBlock1[0x3] & 0xFF;
    }

    public void setUnitClass(int value) {
        rawBlock1[0x3] = (byte) (value & 0xFF);
    }

    /*
    0: 0x1 Teammate
    1: 0x2 Outrealm
    2: 0x4 SpotPass Recruit
    3: 0x8 StreetPass Recruit
    4: 0x10 Team Leader
    0x20
    0x40
    0x80
     */
    public boolean hasFlag(int slot) {
        return Bitflag.byte1ToReversedBinaryString(rawBlock1[0x4]).charAt(slot) == '1';
    }

    public void setFlag(int slot, boolean set) {
        Bitflag.setByte1Flag(rawBlock1, 0x4, slot, set);
    }

    public int getLevel() {
        return rawBlock1[0x5] & 0xFF;
    }

    public void setLevel(int value) {
        rawBlock1[0x5] = (byte) (value & 0xFF);
    }

    //0x6 Unused Exp value?

    public int getHiddenLevel() {
        return rawBlock1[0x7] & 0xFF;
    }

    public void setHiddenLevel(int value) {
        rawBlock1[0x7] = (byte) (value & 0xFF);
    }

    public int getSprite() {
        return Hex.getByte2(rawBlock1, 0x8);
    }

    public void setSprite(int value) {
        Hex.setByte2(rawBlock1, 0x8, value);
    }

    public String getOffspringColor() {
        int pointer = 0xA;
        byte[] hairColorBytes = new byte[3];
        hairColorBytes[0] = rawBlock1[pointer];
        hairColorBytes[1] = rawBlock1[pointer + 1];
        hairColorBytes[2] = rawBlock1[pointer + 2];
        // Combine the three bytes into a single integer value using bitwise operators
        int hairColor = ((hairColorBytes[0] & 0xFF) << 16) | ((hairColorBytes[1] & 0xFF) << 8) | (hairColorBytes[2] & 0xFF);
        // Convert the integer to a hexadecimal string with leading zeros
        return String.format("%06X", hairColor);
    }

    public void setOffspringColor(String hexString) {
        Hex.setColorToByteArray(rawBlock1, 0xA, hexString);
    }

    public int[] getGrowth() {
        int[] growth = new int[8];
        int point = 0xE;
        for (int i = 0; i < growth.length; i++) {
            growth[i] = rawBlock1[point + i] & 0xFF;
        }
        return growth;
    }

    public void setGrowth(int value, int slot) {
        int point = 0xE;
        rawBlock1[point + slot] = (byte) (value & 0xFF);
    }

    public int[] getActiveSkills() {
        int[] skills = new int[5];
        int point = 0x16;
        for (int i = 0; i < skills.length; i++) {
            skills[i] = rawBlock1[point + i] & 0xFF;
        }
        return skills;
    }

    public void setActiveSkills(int value, int slot) {
        int point = 0x16;
        rawBlock1[point + slot] = (byte) (value & 0xFF);
    }

    public int[] getWeaponExp() {
        int[] exp = new int[5];
        int point = 0x1B;
        for (int i = 0; i < exp.length; i++) {
            exp[i] = rawBlock1[point + i] & 0xFF;
        }
        return exp;
    }

    public void setWeaponExp(int value, int slot) {
        int point = 0x1B;
        rawBlock1[point + slot] = (byte) (value & 0xFF);
    }

    //Parents (6 slots, no avatar modifiers)
    public int getParent(int slot) {
        return rawChild[slot] & 0xFF;
    }

    public void setParent(int value, int slot) {
        if (value >= 255) value = 255;
        rawChild[slot] = (byte) (value & 0xFF);
    }

    public int length() {
        return bytes().length;
    }

    public void changeRegion(boolean isWest) {
        this.isWest = isWest;
        //Items
        for (DuItem item : itemList) item.changeRegion(isWest);
        //Logbook Data
        rawLog.changeRegion(isWest);
    }

    public byte[] bytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(rawBlock1);
            for (DuItem item : itemList) byteArrayOutputStream.write(item.bytes());
            byteArrayOutputStream.write(rawChild);
            byteArrayOutputStream.write(rawSkill.bytes());
            byteArrayOutputStream.write(rawUnknown);
            //The profile card and messages are excluded
            int nameSize = (isWest) ? DuTeam.US_NAME_LOG : DuTeam.JP_NAME_LOG;
            byteArrayOutputStream.write(Arrays.copyOfRange(rawLog.getBytes(), 0x0, nameSize + 0x1E));
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
