package com.danius.fireeditor.savefile;

import com.danius.fireeditor.savefile.other.*;
import com.danius.fireeditor.savefile.inventory.TranBlock;
import com.danius.fireeditor.savefile.inventory.RefiBlock;
import com.danius.fireeditor.savefile.units.UnitBlock;
import com.danius.fireeditor.savefile.wireless.Du26Block;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Chapter13 extends SaveFile {
    public HeaderBlock blockHeader; //From 0x0 to 0xC0 (West) or 0x80 (JP)
    private byte[] blockIndex; //Stores the block addresses
    private byte[] blockPers; //?? - Map Exclusive
    public UserBlock blockUser; //User Data
    public GmapBlock blockGmap; //Over world Data
    public UnitBlock blockUnit; //Unit Data
    public RefiBlock blockRefi; //Forged Weapons Data
    public TranBlock blockTran; //Inventory Data
    private byte[] blockMapBattle; //Map Data? - Map Exclusive
    public Du26Block blockDu26; //StreetPass & SpotPass Data
    public EvstBlock blockEvst; //Barrack
    private int region; //0xC0 = US/EU, 0x80 = JP
    public boolean isChapter = true; //Chapter or Map save file

    /*
    This class handles the Chapter save file blocks
     */
    public Chapter13(byte[] fileBytes) {
        initialize(fileBytes);
        //The general data is printed
        System.out.println("\n" + blockUnit.reportCount());
        //System.out.println(blockRefi.reportCount());
        System.out.println(blockUser.report());
        if (isChapter) System.out.println("Chapter File Loaded");
        else System.out.println("Map File Loaded");
    }

    public void changeRegion(boolean isWest) {
        blockHeader.changeRegion(isWest);
        //blockUser.changeRegion(isWest);
        blockUnit.changeRegion(isWest);
        blockRefi.changeRegion(isWest);
        blockDu26.changeRegion(isWest);
        region = (isWest) ? 0xC0 : 0x80;
    }

    /*
    The save file is decompressed and split into all the different blocks, for easier future editing
     */
    private void initialize(byte[] fileBytes) {
        //The save file is decompressed
        if (!isDecompressed(fileBytes)) {
            fileBytes = decompressBytes(fileBytes, region);
        }
        isChapter = isChapterFile(fileBytes); //Checks the type of save file
        //Splits the blocks
        try {
            splitBlocksIndex(fileBytes);
        } catch (
                Exception e) {
            //If it fails, try ignoring the index block addresses
            System.out.println("INDEX BLOCK MISMATCH, ATTEMPTING TO SEARCH MANUALLY");
            splitBlocksBackup(fileBytes);
        }
    }

    /*
    Splits the blocks using the index data
     */
    private void splitBlocksIndex(byte[] fileBytes) {
        //The header is saved, and the index block is checked
        if (region != 0xC0 && region != 0x80) {
            throw new RuntimeException("Region mismatch!: " + Integer.toHexString(region));
        }
        blockHeader = new HeaderBlock(Arrays.copyOfRange(fileBytes, 0x0, region));
        //Index Block
        blockIndex = Arrays.copyOfRange(fileBytes, region, region + 0x44);
        //The other blocks are mapped out
        int offset = 0x4;
        //Map exclusive block
        if (!isChapter) {
            blockPers = Arrays.copyOfRange(fileBytes,
                    Hex.getByte4(blockIndex, offset), Hex.getByte4(blockIndex, offset + 4));
            offset += 4;
        }
        //User block
        byte[] userBytes = Arrays.copyOfRange(fileBytes,
                Hex.getByte4(blockIndex, offset), Hex.getByte4(blockIndex, offset + 4));
        blockUser = new UserBlock(userBytes);
        offset += 4;
        //OverWorld Map
        blockGmap = new GmapBlock(Arrays.copyOfRange(fileBytes,
                Hex.getByte4(blockIndex, offset), Hex.getByte4(blockIndex, offset + 4)));
        offset += 4;
        //Units (IMPORTANT)
        boolean isWest = (region == 0xC0);
        blockUnit = new UnitBlock(Arrays.copyOfRange(fileBytes, Hex.getByte4(blockIndex, offset),
                Hex.getByte4(blockIndex, offset + 4)), isWest);
        offset += 4;
        //Forged Weapons
        blockRefi = new RefiBlock(Arrays.copyOfRange(fileBytes,
                Hex.getByte4(blockIndex, offset), Hex.getByte4(blockIndex, offset + 4)), isWest);
        offset += 4;
        //Inventory
        blockTran = new TranBlock(Arrays.copyOfRange(fileBytes,
                Hex.getByte4(blockIndex, offset), Hex.getByte4(blockIndex, offset + 4)));
        offset += 4;
        //Map Exclusive Block
        if (!isChapter) {
            blockMapBattle = Arrays.copyOfRange(fileBytes,
                    Hex.getByte4(blockIndex, offset), Hex.getByte4(blockIndex, offset + 4));
            offset += 4;
        }
        //StreetPass & SpotPass
        blockDu26 = new Du26Block(Arrays.copyOfRange(fileBytes,
                Hex.getByte4(blockIndex, offset), Hex.getByte4(blockIndex, offset + 4)), isWest);
        offset += 4;
        //Barrack Data
        blockEvst = new EvstBlock(Arrays.copyOfRange(fileBytes,
                Hex.getByte4(blockIndex, offset), fileBytes.length));
    }

    /*
    Splits the blocks by searching their headers instead of looking to the index block
    Useful for broken save files by users or edited using Old Fire Editor, or when
    testing on a hex editor and don't want to fix the addresses
     */
    public void splitBlocksBackup(byte[] bytes) {
        //The offsets are searched
        int pers = Hex.indexOf(bytes, Hex.toByte("53 52 45 50"), 0x0, 0) - 4;
        int user = Hex.indexOf(bytes, Hex.toByte("52 45 53 55"), 0x0, 0) - 4;
        int gmap = Hex.indexOf(bytes, Hex.toByte("50 41 4D 47"), 0x0, 0) - 4;
        int unit = Hex.indexOf(bytes, Hex.toByte("54 49 4E 55"), 0x0, 0) - 4;
        int refi = Hex.indexOf(bytes, Hex.toByte("49 46 45 52"), 0x0, 0) - 4;
        int tran = Hex.indexOf(bytes, Hex.toByte("4E 41 52 54"), 0x0, 0) - 4;
        int mapb = Hex.indexOf(bytes, Hex.toByte("20 50 41 4D"), 0x0, 0) - 4;
        int du26 = Hex.indexOf(bytes, Hex.toByte("36 32 55 44"), 0x0, 0) - 4;
        int evst = Hex.indexOf(bytes, Hex.toByte("54 53 56 45"), 0x0, 0) - 4;
        //The blocks are initialized
        boolean isWest = (region == 0xC0);
        this.blockHeader = new HeaderBlock(Arrays.copyOfRange(bytes, 0x0, region));
        //Chapter Exclusive Block 1
        if (isChapter) {
            this.blockIndex = Arrays.copyOfRange(bytes, region, user);
        } else {
            this.blockIndex = Arrays.copyOfRange(bytes, region, pers);
            this.blockPers = Arrays.copyOfRange(bytes, pers, user);
        }
        this.blockUser = new UserBlock(Arrays.copyOfRange(bytes, user, gmap));
        this.blockGmap = new GmapBlock(Arrays.copyOfRange(bytes, gmap, unit));
        this.blockUnit = new UnitBlock(Arrays.copyOfRange(bytes, unit, refi), isWest);
        this.blockRefi = new RefiBlock(Arrays.copyOfRange(bytes, refi, tran), isWest);
        //Chapter Exclusive Block 2
        if (isChapter) {
            this.blockTran = new TranBlock(Arrays.copyOfRange(bytes, tran, du26));
        } else {
            this.blockTran = new TranBlock(Arrays.copyOfRange(bytes, tran, mapb));
            this.blockMapBattle = Arrays.copyOfRange(bytes, mapb, du26);
        }
        this.blockDu26 = new Du26Block(Arrays.copyOfRange(bytes, du26, evst), isWest);
        this.blockEvst = new EvstBlock(Arrays.copyOfRange(bytes, evst, bytes.length));
    }


    /*
    Combines all the blocks to export the save file and fixes the index block pointers
     */
    public byte[] getBytes() {
        //The index block addresses are fixed
        fixIndexBlock();
        //All the blocks are combined
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(blockHeader.bytes());
            outputStream.write(blockIndex);
            if (!isChapter) outputStream.write(blockPers);
            outputStream.write(blockUser.bytes());
            outputStream.write(blockGmap.bytes());
            outputStream.write(blockUnit.getBlockBytes());
            outputStream.write(blockRefi.getBlockBytes());
            outputStream.write(blockTran.getBlockBytes());
            if (!isChapter) outputStream.write(blockMapBattle);
            outputStream.write(blockDu26.bytes());
            outputStream.write(blockEvst.bytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public byte[] getBytesComp() {
        return compressBytes(getBytes(), region);
    }

    /*
    The index block store the beginning offsets of each block
    Since each block may change in size while editing, it needs to be fixed
     */
    private void fixIndexBlock() {
        if (isChapter) {
            int[] lengths = {blockHeader.length() + blockIndex.length,
                    blockUser.length(), blockGmap.length(), blockUnit.getBlockBytes().length,
                    blockRefi.getBlockBytes().length, blockTran.getBlockBytes().length,
                    blockDu26.length()};
            int sum = 0;
            //The index block is fixed
            for (int i = 0; i < lengths.length; i++) {
                sum += lengths[i];
                Hex.setByte4(blockIndex, 0x4 + i * 4, sum);
            }
        }
        //TODO: Duplicated code, I don't care
        else {
            int[] lengths = {blockHeader.length() + blockIndex.length, blockPers.length,
                    blockUser.length(), blockGmap.length(), blockUnit.getBlockBytes().length,
                    blockRefi.getBlockBytes().length, blockTran.getBlockBytes().length,
                    blockMapBattle.length, blockDu26.length()};
            int sum = 0;
            //The index block is fixed
            for (int i = 0; i < lengths.length; i++) {
                sum += lengths[i];
                Hex.setByte4(blockIndex, 0x4 + i * 4, sum);
            }
        }
    }

    /*
    Checks if the save file is decompressed and sets the region of the save file
     */
    private boolean isDecompressed(byte[] fileBytes) {
        byte[] edni = Hex.toByte("45 44 4E 49"); //EDNI
        byte[] pmoc = Hex.toByte("50 4D 4F 43"); //PMOC
        byte[] us = Hex.getByte4Array(fileBytes, 0xC0);
        byte[] jp = Hex.getByte4Array(fileBytes, 0x80);
        if (Arrays.equals(us, edni) || Arrays.equals(us, pmoc)) this.region = 0xC0;
        if (Arrays.equals(jp, edni) || Arrays.equals(jp, pmoc)) this.region = 0x80;
        String regionName = (region == 0xC0) ? "US/EU" : "JP";
        System.out.print("REGION LOADED: " + regionName + "\n");
        return Arrays.equals(us, edni) || Arrays.equals(jp, edni);
    }

    /*
    Checks the number of blocks stored on the index block to determine if it is a
    Chapter save file or a Map save file
     */
    private boolean isChapterFile(byte[] bytes) {
        byte[] blockIndex = Arrays.copyOfRange(bytes, region, region + 0x44);
        //Map Save Files have 2 additional blocks
        int lastBlockOffset = Hex.getByte4(blockIndex, 0x24);
        return (lastBlockOffset == 0);
    }


    //Scans the whole save file to find additional modded classes
    public int maxClasses() {
        int maxClasses = Constants.MAX_CLASSES;

        //The modded classes are checked viewing all the stored units
        for (int i = 0; i < blockUnit.unitList.size(); i++) {
            for (int j = 0; j < blockUnit.unitList.get(i).size(); j++) {
                int unitClass = blockUnit.unitList.get(i).get(j).rawBlock1.unitClass();
                if (unitClass > Constants.MAX_CLASSES) {
                    if (unitClass > maxClasses) maxClasses = unitClass;
                }
                //The logbook class is checked
                if (blockUnit.unitList.get(i).get(j).rawLog != null) {
                    int logClass = blockUnit.unitList.get(i).get(j).rawLog.getProfileCard()[0];
                    if (logClass > maxClasses) maxClasses = logClass;
                }
            }
        }
        //The credit records are checked
        for (int i = 0; i < blockUser.progress.size(); i++) {
            int classFirst = blockUser.progress.get(i).classFirst();
            int classSecond = blockUser.progress.get(i).classSecond();
            if (classFirst != 65535 && classFirst > maxClasses) {
                maxClasses = classFirst;
            }
            if (classSecond != 65535 && classSecond > maxClasses) {
                maxClasses = classSecond;
            }
        }

        return maxClasses;
    }

    public int maxArmies() {
        int maxArmies = Constants.MAX_ARMY;
        for (int i = 0; i < blockUnit.unitList.size(); i++) {
            for (int j = 0; j < blockUnit.unitList.get(i).size(); j++) {
                int army = blockUnit.unitList.get(i).get(j).rawFlags.army();
                if (army > Constants.MAX_ARMY) {
                    if (army > maxArmies) maxArmies = army;
                }
            }
        }
        return maxArmies;
    }

}

