package com.danius.fireeditor.savefile.units.mainblock;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import static com.danius.fireeditor.data.UnitDb.*;

public class RawSupport {
    private final List<Integer> supportValues; //Stores the support points/actions of all the valid characters
    public int unitId;

    public RawSupport() {
        this.supportValues = new ArrayList<>();
        this.unitId = 0x2;
    }

    public RawSupport(byte[] blockBytes, int unitId) {
        //Copies only the list, not the amount, the amount is fixed when the bytes are called
        byte[] byteArray = Arrays.copyOfRange(blockBytes, 0x1, blockBytes.length);
        supportValues = new ArrayList<>();
        for (byte b : byteArray) {
            supportValues.add(b & 0xFF);
        }
        this.unitId = unitId;
    }

    public int supportValue(int slot) {
        int value = 0;
        try {
            value = supportValues.get(slot);
        } catch (Exception ignored) {

        }
        return value;
    }

    /*
    Sets a support to a specific level
    PARAMETERS:
    0: C-Pending
    1: B-Pending
    2: A-Pending
    3: S-Pending
    4: S-Rank (NO)
     */
    public void setSupportLevel(int slot, int level) {
        //Gets the type of support of the parameter character
        int type = getUnitSupportTypes(unitId)[slot];
        //Gets the max values of the type gotten
        int[] maxValues = getSupportValues(type);
        if (level == 0) {
            setSupportValue(slot, 0);
        }
        else setSupportValue(slot, maxValues[level - 1]);
    }

    /*
    Sets all the supports to a specific level
     */
    public void setAllSupportsTo(int level) {
        int[] characters = getUnitSupportUnits(unitId); //Ignores the modded supports
        for (int i = 0; i < characters.length; i++) {
            setSupportLevel(i, level);
        }
    }

    public void setAllSupportsTo(int level, List<Integer> units) {
        int[] characters = getUnitSupportUnits(unitId); //Ignores the modded supports
        for (int i = 0; i < characters.length; i++) {
            //Only recruited units are modified
            if (units.contains(characters[i]) || level == 0) {
                setSupportLevel(i, level);
            }
            //The support is reset if not recruited
            else setSupportValue(i, 0);
        }
    }

    public void setSupportValue(int slot, int value) {
        supportValues.set(slot, value);
    }

    //If this unit has the parameter unit as a valid support, edit the raw value
    public void setSupportValueByUnit(int unitId, int value) {
        //The valid supports from this unit are retrieved
        int[] validUnitsEdit = getUnitSupportUnits(this.unitId);
        //The position of the parameter unit is retrieved
        int position = IntStream.range(0, validUnitsEdit.length).filter
                (i -> validUnitsEdit[i] == unitId).findFirst().orElse(-1);
        if (position != -1) {
            setSupportValue(position, value);
        }
    }

    //If this unit has the parameter unit as a valid support, edit the level
    public void setSupportLevelByUnit(int unitId, int value) {
        //The valid supports from this unit are retrieved
        int[] validUnitsEdit = getUnitSupportUnits(this.unitId);
        //The position of the parameter unit is retrieved
        int position = IntStream.range(0, validUnitsEdit.length).filter
                (i -> validUnitsEdit[i] == unitId).findFirst().orElse(-1);
        if (position != -1) {
            setSupportLevel(position, value);
        }
    }

    /*
    The support block varies in size depending on the number and which supports the player made
    This method would increase it to the maximum size, and detect if there are extra modded supports
    This should not be called when reading the units while opening a save file, since it will mess
    up the byte count. The logic of this class relies on this method, so it must be used before editing
     */
    public void expandBlock() {
        try {
            int[] characters = getUnitSupportUnits(unitId);
            //If there are missing bytes, add them
            if (characters.length > supportValues.size()) {
                int extraBytes = characters.length - supportValues.size();
                for (int i = 0; i < extraBytes; i++) {
                    supportValues.add(0x0);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to expand support block of unit: " + unitId);
        }
    }

    /*
    Removes additional bytes from modded supports
     */
    public void removeExtraSupports() {
        try {
            int[] characters = getUnitSupportUnits(unitId);
            //If there are extra bytes, remove them
            if (supportValues.size() > characters.length) {
                int extraBytes = supportValues.size() - characters.length;
                for (int i = 0; i < extraBytes; i++) {
                    supportValues.remove(supportValues.size() - 1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to remove modded supports of unit: " + unitId);
        }
    }

    public byte[] bytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(supportValues.size());
        for (Integer supportValue : supportValues) {
            outputStream.write(supportValue);
        }
        return outputStream.toByteArray();
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public String report() {
        String report = "";
        report += "Supports: " + supportValues;
        return report;
    }

    public int supportCount() {
        return supportValues.size();
    }

    public int length() {
        return supportValues.size() + 1;
    }
}
