package com.danius.fireeditor.savefile.wireless;

import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.extrablock.LogBlock;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DuTeam {
    public boolean isWest;
    public static final int US_NAME_TEAM = 0x2A;
    public static final int JP_NAME_TEAM = 0x16;
    public static final int US_MESSAGE = LogBlock.MESSAGE_US;
    public static final int JP_MESSAGE = LogBlock.MESSAGE_JP;
    public static final int US_WEAPON = 0x22;
    public static final int JP_WEAPON = 0x12;
    public static final int US_NAME_LOG = 0x1A;
    public static final int JP_NAME_LOG = 0xE;
    public static final int HEADER_SIZE = 0xB;
    //110 + 26
    private byte[] slot;
    private final byte[] header;
    public List<UnitDu> unitList;
    private byte[] teamName;
    private byte[] extraData; //Profile Card & Messages (shared among units)

    private int calcUnitSize() {
        //Adds the unit size
        int weaponSize = (isWest) ? US_WEAPON : JP_WEAPON;
        int unitName = (isWest) ? US_NAME_LOG : JP_NAME_LOG;
        int rawBlock1 = 0x21;
        int weaponTotal = (weaponSize + 0x4) * 5;
        int rawBlock2 = 0x18;
        int rawEnd = unitName + 0x1E;
        return rawBlock1 + weaponTotal + rawBlock2 + rawEnd;
    }

    public DuTeam(byte[] bytes) {
        unitList = new ArrayList<>();
        int offset = 0;
        int totalBlockSizeUs = Du26Block.teamSize(true) + HEADER_SIZE;
        int totalBlockSizeJp = Du26Block.teamSize(false) + HEADER_SIZE;
        int givenSize = bytes.length;

        //Checks if it is a player's team or foreign team (player does not have slot byte)
        if (givenSize - totalBlockSizeUs == 1 || givenSize - totalBlockSizeJp == 1) {
            this.slot = new byte[]{bytes[0x0]};
            offset++;
        }

        //Checks the region
        if (givenSize - totalBlockSizeUs == 0 || givenSize - totalBlockSizeUs == 1) this.isWest = true;
        else if (givenSize - totalBlockSizeJp == 0 || givenSize - totalBlockSizeJp == 1) this.isWest = false;

        //Block Sizes
        int unitSize = calcUnitSize();
        int nameSize = (isWest) ? US_NAME_TEAM : JP_NAME_TEAM;
        int messageSize = (isWest) ? US_MESSAGE : JP_MESSAGE;
        messageSize *= 4;

        this.header = Arrays.copyOfRange(bytes, offset, offset + HEADER_SIZE);
        int unitCount = header[0x9];
        offset += header.length;
        int offsetEnd = offset + (unitSize * 10);

        //The units are read last to add the extra global logbook block
        //Team Name
        this.teamName = Arrays.copyOfRange(bytes, offsetEnd, offsetEnd + nameSize);
        offsetEnd += teamName.length;

        //Team Settings
        byte[] profileCard = Arrays.copyOfRange(bytes, offsetEnd, offsetEnd + 0x47);
        offsetEnd += profileCard.length;
        //Avatar Messages
        byte[] messages = Arrays.copyOfRange(bytes, offsetEnd, offsetEnd + messageSize);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(profileCard);
            byteArrayOutputStream.write(messages);
            byteArrayOutputStream.write(0); //Block terminator (will be removed when exporting)
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.extraData = byteArrayOutputStream.toByteArray();

        //Always 10 unit slots, though they can be empty
        for (int i = 0; i < 10; i++) {
            if (i < unitCount) {
                UnitDu unitDu = new UnitDu(Arrays.copyOfRange(bytes, offset, offset + unitSize), extraData);
                unitList.add(unitDu);
            }
            offset += unitSize;
        }
        System.out.println(report());
    }

    public String getTeamName() {
        byte[] nameArray = Arrays.copyOfRange(teamName, 0x0, (teamName.length));
        return Hex.byteArrayToString(nameArray);
    }

    public UnitDu getUnit(int slot) {
        return unitList.get(slot);
    }

    public void setUnit(UnitDu unit, int slot) {
        unitList.set(slot, unit);
    }

    public void setExtraData(UnitDu unit) {
        int nameSize = (isWest) ? US_NAME_LOG : JP_NAME_LOG;
        int regularData = nameSize + 0x1E;
        byte[] all = unit.rawLog.getBytes();
        this.extraData = Arrays.copyOfRange(all, regularData, all.length - 1); //The terminator is excluded
    }

    public byte[] getSlot() {
        if (slot.length == 0) return null;
        else return slot;
    }

    public void setSlot(int value) {
        this.slot = new byte[]{(byte) (value & 0xFF)};
    }

    public byte[] bytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int unitCount = unitList.size();
        try {
            if (slot != null) {
                byteArrayOutputStream.write(slot);
            }
            header[0x9] = (byte) (unitCount & 0xFF);
            byteArrayOutputStream.write(header);
            //The units are written
            for (UnitDu unitDu : unitList) {
                byteArrayOutputStream.write(unitDu.bytes());
            }
            for (int i = unitCount; i < 10; i++) {
                UnitDu unit = new UnitDu(isWest);
                byteArrayOutputStream.write(unit.bytes());
            }
            //The profile settings are messages are written
            byteArrayOutputStream.write(teamName);
            if (unitCount > 0) setExtraData(unitList.get(0)); //The extra data is updated
            byteArrayOutputStream.write(extraData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public void changeRegion(boolean isWest) {
        for (UnitDu unit : unitList) unit.changeRegion(isWest);
        int teamNameSize = (isWest) ? US_NAME_TEAM : JP_NAME_TEAM;
        teamName = Hex.changeSizeArray(teamName, teamNameSize);
        this.isWest = isWest;
    }

    public int unitCount() {
        return unitList.size();
    }

    public int length() {
        return bytes().length;
    }

    public String report() {
        String text = "";
        int unitCount = unitList.size();
        if (slot != null) text += slot[0] + " - ";
        text += getTeamName() + ": " + unitCount + "\n";
        for (UnitDu unitDu : unitList) {
            text += unitDu.getName() + ", ";
        }
        //text = text.substring(0, text.length() - 1);
        if (unitCount > 0) text = text.substring(0, text.length() - 2);
        return text;
    }

    @Override
    public String toString() {
        return getTeamName();
    }
}
