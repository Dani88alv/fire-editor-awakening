package com.danius.fireeditor.savefile.global;

import com.danius.fireeditor.data.UnitDb;
import com.danius.fireeditor.savefile.units.extrablock.LogBlock;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlUserBlock {

    private final byte[] header; //Title
    private final byte[] rawBlock1; //Unlocks and settings
    private final byte[] unitGallery; //Unit Gallery Flags
    private final byte[] supportLog; //Support Log Flags
    private final byte[] hairColorFlags; //Better not mess with this
    private final byte[] unknownFlags; //???
    public final LogBlock avatarMale; //Support Log portrait
    public final LogBlock avatarFemale; //Support Log portrait
    private final byte[] hairColors; //Hex values (same number as hair color flags)

    public GlUserBlock(byte[] blockBytes) {
        this.header = Arrays.copyOfRange(blockBytes, 0x0, 0x4);
        int offset = header.length;

        //Main Flags
        this.rawBlock1 = Arrays.copyOfRange(blockBytes, offset, offset + 0xD);
        offset += rawBlock1.length;

        //Unit Gallery Flags
        int unitCount1 = Hex.getByte4(blockBytes, offset);
        offset += 4; //Count value
        this.unitGallery = Arrays.copyOfRange(blockBytes, offset, offset + (unitCount1 / 8) + 1);
        offset += unitGallery.length;

        //Support Log Flags
        int supportCount = Hex.getByte4(blockBytes, offset);
        offset += 4; //Count value
        this.supportLog = Arrays.copyOfRange(blockBytes, offset, offset + (supportCount / 8) + 1);
        offset += supportLog.length;

        //Hair Color Flags
        int colorCount = Hex.getByte4(blockBytes, offset);
        offset += 4; //Count value
        this.hairColorFlags = Arrays.copyOfRange(blockBytes, offset, offset + (colorCount / 8) + 1);
        offset += hairColorFlags.length;

        //Unknown Flags
        int unknownCount = Hex.getByte4(blockBytes, offset);
        offset += 4; //Count value
        this.unknownFlags = Arrays.copyOfRange(blockBytes, offset, offset + (unknownCount / 8));
        offset += unknownFlags.length;

        //Hair color values (at the end)
        int hairColorCount = colorCount * 4;
        this.hairColors = Arrays.copyOfRange(blockBytes, blockBytes.length - hairColorCount - 1, blockBytes.length);

        //Avatar data
        int finalOffset = blockBytes.length - hairColors.length;
        byte[] avatarData = Arrays.copyOfRange(blockBytes, offset, finalOffset);
        int avatarSize = avatarData.length / 2;

        //Weird stuff because I messed up storing the base unit footer
        byte[] firstHalf = new byte[avatarSize];
        System.arraycopy(avatarData, 1, firstHalf, 0, avatarSize - 1);
        firstHalf[avatarSize - 1] = 0;
        byte[] secondHalf = new byte[avatarSize];
        System.arraycopy(avatarData, avatarSize + 1, secondHalf, 0, avatarSize - 1);
        secondHalf[avatarSize - 1] = 0;

        //The logbook blocks are initialized
        this.avatarMale = new LogBlock(firstHalf);
        avatarMale.removeFooter();
        this.avatarFemale = new LogBlock(secondHalf);
        avatarFemale.removeFooter();

        System.out.println("Unit Gallery: " + unitCount1);
        System.out.println("Support Log: " + supportCount);
        System.out.println("Global Flags: " + globalFlags());
    }

    /*
    0 0x1 ???
    1 0x2 Game Clear (Theater, Support Log, Unit Gallery)
    2 0x4 Lunatic+ Classic/Casual
    3 0x8 Lunatic+ Classic Only
    4 0x10 ???
    5 0x20 ???
    6 0x40 ???
    7 0x80 ???

    8  0x1 ???
    9  0x2 (Is enabled)
    10 0x3 ???
    11 0x4 Voice set to JP
     */
    public boolean hasGlobalFlag(int bit) {
        int point = 0x1;
        return Hex.hasBitFlag(rawBlock1, point, bit);
    }

    public void setGlobalFlag(int bit, boolean set) {
        int point = 0x1;
        Hex.setBitFlag(rawBlock1, point, bit, set);
    }

    private List<Integer> globalFlags() {
        List<Integer> flags = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (hasGlobalFlag(i)) flags.add(i);
        }
        return flags;
    }

    //0x5 Last Save Slot Used

    public int getRenown() {
        int point = 0x7;
        return Hex.getByte4(rawBlock1, point);
    }

    public void setRenown(int value) {
        int point = 0x7;
        Hex.setByte4(rawBlock1, point, value);
    }

    public void changeRegion(boolean isWest) {
        avatarMale.changeRegion(isWest);
        avatarFemale.changeRegion(isWest);
    }

    public void setUnitGalleryFlag(int bit, boolean set) {
        Hex.setBitFlag(unitGallery, 0x0, bit, set);
    }

    public void setSupportFlag(int bit, boolean set) {
        Hex.setBitFlag(supportLog, 0x0, bit, set);
    }

    public void fullSupportLog() {
        int unitGallery = UnitDb.entriesUnitGallery();
        int supportCount = UnitDb.entriesSupportLog();

        //The unit gallery is maxed (if not, units will be greyed out on the support log)
        for (int i = 0; i < unitGallery; i++) {
            setUnitGalleryFlag(i, true);
        }
        //The support log is maxed
        for (int i = 0; i < supportCount; i++) {
            setSupportFlag(i, true);
        }
    }

    public byte[] getBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            //Header and main flags
            outputStream.write(header);
            outputStream.write(rawBlock1);

            //Unit Gallery Flags
            int unitCount = (unitGallery.length - 1) * 8;
            outputStream.write(Hex.intToByteArray4(unitCount));
            outputStream.write(unitGallery);

            //Support Log Flags
            int supportCount = (supportLog.length - 1) * 8;
            outputStream.write(Hex.intToByteArray4(supportCount));
            outputStream.write(supportLog);

            //Hair Color Flags
            int colorCount = (hairColorFlags.length - 1) * 8;
            outputStream.write(Hex.intToByteArray4(colorCount));
            outputStream.write(hairColorFlags);

            //Unknown Flags
            int unknownCount = unknownFlags.length * 8;
            outputStream.write(Hex.intToByteArray4(unknownCount));
            outputStream.write(unknownFlags);

            //Avatar Data
            outputStream.write(0x6); //Logbook Block Header
            outputStream.write(avatarMale.getBytes());
            outputStream.write(0x6); //Logbook Block Header
            outputStream.write(avatarFemale.getBytes());

            //Hair Color Hex Values
            outputStream.write(hairColors);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public int length() {
        return getBytes().length;
    }

}
