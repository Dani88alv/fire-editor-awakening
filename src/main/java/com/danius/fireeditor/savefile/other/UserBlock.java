package com.danius.fireeditor.savefile.other;

import com.danius.fireeditor.util.Bitflag;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserBlock {

    public static final int SIZE_BLOCK = 0xEE; //+1 on JP
    public static final int SIZE_MAP = 0x10;
    private boolean isWest = true;
    private final byte[] header;
    private final byte[] rawBlock1; //??
    public List<RawProgress> progress; //Chapter List
    private final byte[] rawBlock2;
    private int remainingFrames = 0;

    public UserBlock(byte[] blockBytes) {
        //The total chapters are calculated using the block total size
        int chapters = (blockBytes.length - SIZE_BLOCK) / SIZE_MAP;
        //The chapter byte is checked, JP is larger by 1 byte
        int extraByte = 0;
        if ((blockBytes[0x15] & 0xFF) != chapters && (blockBytes[0x16] & 0xFF) == chapters) {
            extraByte = 1;
            this.isWest = false;
        }
        //The blocks are split
        header = Arrays.copyOfRange(blockBytes, 0x0, 0x5);
        int length = header.length;
        rawBlock1 = Arrays.copyOfRange(blockBytes, length, length + 0x10 + extraByte);
        length += rawBlock1.length;
        //Story Progress
        parseCreditChapters((Arrays.copyOfRange(
                blockBytes, length, length + (chapters * SIZE_MAP) + 1) //+1 because of the chapter count
        ));
        length += chaptersLength();
        //Settings
        rawBlock2 = Arrays.copyOfRange(blockBytes, length, blockBytes.length);

        this.remainingFrames = playtime() % 60;
    }

   /*
   0x4-0x7 ?
   0x8 Story chapters beaten
   0x9 Last chapter beaten?
   0xA Current chapter
    */

    //0x65 barracas y cosas

    public int playtime() {
        return Hex.getByte4(rawBlock1, 0x0);
    }

    public void setPlaytime(int frames) {
        Hex.setByte4(rawBlock1, 0x0, frames + remainingFrames);
    }

    //0x12-013 Global flags
    //0x5F - 0x62 Guide Entries Flags

    /*
    General flags (4 bytes)
    0: 0x1
    1: 0x2
    2: 0x4 Casual
    3: 0x8
    4: 0x10
    5: 0x20 Force Map Reload?
    6: 0x40
    7: 0x80
     */
    public boolean gameModeFlag(int slot) {
        int point = 0x63;
        return Bitflag.hasFlag1(rawBlock2[point], slot);
    }

    public void setGameModeFlag(int slot, boolean set) {
        int point = 0x63;
        Bitflag.setByte1Flag(rawBlock2, point, slot, set);
    }

    public boolean isLunaticPlus() {
        int point = 0x64;
        return Bitflag.hasFlag1(rawBlock2[point], 0);
    }

    public void setLunaticPlus(boolean set) {
        int point = 0x64;
        Bitflag.setByte1Flag(rawBlock2, point, 0, set);
    }

    public int difficulty() {
        int point = 0x68;
        return rawBlock2[point] & 0xFF;
    }

    public void setDifficulty(int id) {
        int point = 0x68;
        rawBlock2[point] = (byte) (id & 0xFF);
    }

    public int money() {
        return Hex.getByte4(rawBlock2, 0x69);
    }

    public void setMoney(int value) {
        Hex.setByte4(rawBlock2, 0x69, value);
    }

    public int renown() {
        return Hex.getByte4(rawBlock2, 0x6D);
    }

    public void setRenown(int value) {
        Hex.setByte4(rawBlock2, 0x6D, value);
    }

    //0x71-0x79 ?
    //0x7A Advanced Auto Settings

    public void resetRenownFlags() {
        int point = 0x7B;
        for (int i = point; i < point + 0x5; i++) {
            rawBlock2[i] = (byte) 0x0;
        }
    }

    //0x80-0x83 ?

    //0x84-0x97 Map settings
    /*
    0x84 Bitflag
    0 0x1 Circle Pad Analog
    1 0x2 Combat Animations On
    2 0x4 Attack Flow Evolved
    3 0x8 Cursor Memory Off
    4 0x10 Smart End On
    5 0x20 Auto Battle Unlocked
    6 0x40 Slide Guides On
    7 0x80 Interface Simplified

    0x85 Bitflag
    0 0x1
    1 0x2 Map Tilt On
    2 0x4 Story Voices On
    3 0x8 Speaker Mode On
    4 0x10 Confirm Auto
    5 0x20
    6 0x40 Advanced Auto Off
    7 0x80 Confirm End

    0x86 Bitflag
    0 0x1 Movie Subtitles On

    0x87 Unused Bitflag Byte?
     */

    /*
    0x88 Combat Animations
    0x89 Camera 1
    0x8A Camera Positions
    0x8B Camera 2
    0x8C Game Speed
    0x8D
    0x8E Skip Actions
    0x8F Danger Area
    0x90 Grid
    0x91 Music Volume
    0x92 SFX Volume
    0x93 System Volume
    0x94 Voice Volume
    0x95 Music ID
    0x96
    0x97 HP Gauges
     */

    //Populates the chapter list used on the credits
    public void parseCreditChapters(byte[] bytes) {
        progress = new ArrayList<>();
        int size = 0x10;
        if (bytes.length > 0) {
            int amount = bytes[0] & 0xFF;  // Mask the sign extension
            for (int i = 0; i < amount; i++) {
                int startIndex = i * size + 1;
                int endIndex = startIndex + size;
                byte[] progressBytes = Arrays.copyOfRange(bytes, startIndex, endIndex);
                progress.add(new RawProgress(progressBytes));
            }
        }
    }


    public byte[] bytes() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(header);
        outputStream.write(rawBlock1);
        //Amount of chapters
        outputStream.write(progress.size() & 0xFF);
        //Each chapter is saved
        for (RawProgress rawProgress : progress) {
            outputStream.write(rawProgress.bytes);
        }
        outputStream.write(rawBlock2);
        return outputStream.toByteArray();
    }


    public String report() {
        String report = "";
        //Playtime calculation
        int frames = playtime();
        int frameRate = 60;
        int hours = frames / (frameRate * 60 * 60);
        frames %= (frameRate * 60 * 60);
        int minutes = frames / (frameRate * 60);
        frames %= (frameRate * 60);
        int seconds = frames / frameRate;
        int remainingFrames = frames % frameRate;
        report += "\n" + "Playtime: " + hours + ":" + minutes + ":" + seconds;
        //Story Progress
        report += "\n" + "Chapters Beaten: " + progress.size();
        return report;
    }

    public int chaptersLength() {
        return 0x10 * progress.size() + 1;
    }

    public int length() {
        return header.length + rawBlock1.length + chaptersLength() + rawBlock2.length;
    }
}
