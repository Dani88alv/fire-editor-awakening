package com.danius.fireeditor.savefile.units;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnitBlock {
    /*
    This handles the "TINU" block from the save file
    It stores different unit groups, each one with a different purpose
     */
    private final byte[] blockHeader;
    private byte[] blockFooter;
    public List<List<Unit>> unitList; //Different unit groups
    public final boolean isWest;

    public UnitBlock(byte[] blockBytes, boolean isWest) {
        this.unitList = new ArrayList<>();
        this.isWest = isWest; //Used to determine the logbook blocks size
        //Splits the block header and the units
        this.blockHeader = Arrays.copyOfRange(blockBytes, 0x0, 0x5);
        //The unit groups are parsed
        parseUnitBlock(blockBytes);
        //All the support blocks are expanded for easier editing
        //expandSupportBlocks();
    }

    public String reportCount() {
        //Total count
        String report = "";
        report += "Units: " + unitList.get(3).size();
        report += "\n" + "Dead Units: " + unitList.get(4).size();
        report += "\n" + "Blue Units: " + unitList.get(0).size();
        report += "\n" + "Red Units: " + unitList.get(1).size();
        report += "\n" + "Green Units: " + unitList.get(2).size();
        report += "\n" + "Other Units: " + unitList.get(5).size();
        return report;
    }

    /*
    TESTING ONLY
     */
    public void moveUnitToGroup(int id, int idGroup1, int idGroup2) {
        if (unitList.get(idGroup2).size() < 255) {
            Unit unit = unitList.get(idGroup1).get(id);
            unitList.get(idGroup2).add(unit);
            unitList.get(idGroup1).remove(id);
        }
    }

    public void duplicateUnit(int id, int idGroup) {
        if (unitList.get(idGroup).size() < 255) {
            Unit unit = unitList.get(idGroup).get(id);
            unitList.get(idGroup).add(unit);
        }
    }

    /*
    The header and the unit groups are combined
     */
    public byte[] getBlockBytes() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(blockHeader);
            //Writes all the normal unit groups
            for (int i = 0; i < unitList.size(); i++) {
                List<Unit> units = unitList.get(i);
                if (units.size() > 0) { //If the group exists, write the data
                    outputStream.write(i); //Group ID
                    outputStream.write(units.size()); //Unit Count
                    for (Unit unit : units) { //Unit bytes
                        outputStream.write(unit.getUnitBytes());
                    }
                }
            }
            outputStream.write(blockFooter);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /*
    Parses the all the unit groups
    Each group has a prefix byte, a unit count byte, and then the unit bytes
    00: Blue Units (Map)
    01: Red Units (Map)
    02: Green Units (Map)
    03: Main Units
    04: Dead Units
    05: Other Units (Map)
     */
    private void parseUnitBlock(byte[] blockBytes) {
        int byteCount = blockHeader.length;
        //The documented unit groups are stored
        for (int i = 0; i <= 0x5; i++) {
            List<Unit> group = parseGroup(blockBytes, byteCount, i);
            byteCount += unitGroupByteCount(group);
            unitList.add(group);
        }
        //Additional missable data
        blockFooter = Arrays.copyOfRange(blockBytes, byteCount, blockBytes.length);
    }

    /*
    Retrieves the byte count of a unit list
     */
    private int unitGroupByteCount(List<Unit> unitList) {
        int size = 0;
        if (unitList.size() > 0) {
            size = 2; //Unit group ID + unit count bytes
            for (Unit unit : unitList) {
                size += unit.getUnitBytes().length;
            }
        }
        return size;
    }

    //Not used because the exported save file will be larger and more difficult to test things out
    public void expandSupportBlocks() {
        for (List<Unit> unitGroup : unitList) {
            for (Unit unit : unitGroup) {
                unit.rawSupport.expandBlock();
            }
        }
    }

    /*
    Loops using the stored unit count starting from the offset and saves a list with all the units found
     */
    public List<Unit> parseGroup(byte[] blockBytes, int offset, int prefix) {
        try {
            //If it finds the looked unit group, parse the units
            if ((blockBytes[offset] & 0xFF) == prefix) {
                System.out.println("GROUP " + prefix + ":");
                offset++; //Prefix byte
                int lBlockSize = (isWest) ? Unit.LBLOCK_SIZE_US : Unit.LBLOCK_SIZE_JP; //The Logbook block size is set
                int unitCount = blockBytes[offset] & 0xFF;
                List<Unit> listUnit = new ArrayList<>();
                offset++; //Unit count byte
                for (int i = 0; i < unitCount; i++) {
                    byte[] gBlock, cBlock = null, lBlock = null;
                    //Checks the size of the support block and adds the value to the static general block size
                    int supportSize = blockBytes[offset + 0x43] & 0xFF;
                    int unknownSize = blockBytes[offset + 0x43 + supportSize + 1] & 0xFF;
                    //The general block is copied
                    gBlock = Arrays.copyOfRange(blockBytes, offset, Unit.GBLOCK_SIZE + supportSize + unknownSize + offset);
                    //Checks if there is a child block next (00 01)
                    if (gBlock[gBlock.length - 2] == 0 && gBlock[gBlock.length - 1] == 1) {
                        cBlock = Arrays.copyOfRange(blockBytes, offset + gBlock.length,
                                offset + gBlock.length + Unit.CBLOCK_SIZE);
                    }
                    //Checks if there is a Logbook block next (01 06)
                    else if (gBlock[gBlock.length - 2] == 1 && gBlock[gBlock.length - 1] == 6) {
                        lBlock = Arrays.copyOfRange(blockBytes, offset + gBlock.length,
                                offset + gBlock.length + lBlockSize);
                    }
                    //All the blocks are combined
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    try {
                        outputStream.write(gBlock);
                        if (cBlock != null) outputStream.write(cBlock);
                        if (lBlock != null) outputStream.write(lBlock);
                    } catch (Exception ignored) {
                        throw new RuntimeException();
                    }
                    offset += outputStream.toByteArray().length; //The total unit size is updated
                    Unit unit = new Unit(outputStream.toByteArray()); //The unit is initialized
                    //System.out.println(i + " - " + unit.reportBasic());
                    System.out.println(i + " - " + unit.reportBasic());
                    listUnit.add(unit);
                }
                System.out.println();
                return listUnit;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            System.out.println("UNABLE TO PARSE UNIT GROUP " + prefix);
            throw new RuntimeException();
        }

    }
}
