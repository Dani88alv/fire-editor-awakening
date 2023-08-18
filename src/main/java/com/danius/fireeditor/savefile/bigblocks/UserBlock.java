package com.danius.fireeditor.savefile.bigblocks;

import com.danius.fireeditor.savefile.RawDifficulty;
import com.danius.fireeditor.util.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserBlock {

    public static final int SIZE_BLOCK = 0xEE; //+1 on JP
    public static final int SIZE_MAP = 0x10;

    private final byte[] header;
    public byte[] rawPlaytime; //Playtime in frames
    private final byte[] rawBlock1; //??
    public List<RawProgress> progress; //Chapter List
    private final byte[] rawBlock2; //??
    public RawDifficulty rawDifficulty; //Difficulty and Penalty
    public byte[] rawMoney; //Money
    private final byte[] rawBlockEnd; //??

    /*

     */
    public UserBlock(byte[] blockBytes) {
        //The total chapters are calculated using the block total size
        int chapters = (blockBytes.length - SIZE_BLOCK) / SIZE_MAP;
        //The chapter byte is checked, JP is larger by 1 byte
        int extraByte = 0;
        if ((blockBytes[0x15] & 0xFF) != chapters && (blockBytes[0x16] & 0xFF) == chapters) {
            extraByte = 1;
        }
        //The blocks are split
        header = Arrays.copyOfRange(blockBytes, 0x0, 0x5);
        int length = header.length;
        //Playtime
        rawPlaytime = Arrays.copyOfRange(blockBytes, length, length + 0x4);
        length += rawPlaytime.length;
        //??
        rawBlock1 = Arrays.copyOfRange(blockBytes, length, length + 0xC + extraByte);
        length += rawBlock1.length;
        //Story Progress
        parseCreditChapters((Arrays.copyOfRange(
                blockBytes, length, length + (chapters * SIZE_MAP) + 1) //+1 because of the chapter count
        ));
        length += chaptersLength();
        //??
        rawBlock2 = Arrays.copyOfRange(blockBytes, length, length + 0x63);
        length += rawBlock2.length;
        //Difficulty & Money
        rawDifficulty = new RawDifficulty(Arrays.copyOfRange(blockBytes, length, length + 0x6));
        length += rawDifficulty.length();
        rawMoney = Arrays.copyOfRange(blockBytes, length, length + 0x4);
        length += rawMoney.length;
        //??
        rawBlockEnd = Arrays.copyOfRange(blockBytes, length, blockBytes.length);
    }

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
        outputStream.write(rawPlaytime);
        outputStream.write(rawBlock1);
        //Amount of chapters
        outputStream.write(progress.size() & 0xFF);
        //Each chapter is saved
        for (RawProgress rawProgress : progress) {
            outputStream.write(rawProgress.bytes);
        }
        outputStream.write(rawBlock2);
        outputStream.write(rawDifficulty.bytes);
        outputStream.write(rawMoney);
        outputStream.write(rawBlockEnd);
        return outputStream.toByteArray();
    }

    /*
    Returns the total playtime in frames
     */
    public int playtime() {
        return Hex.getByte4(rawPlaytime, 0x0);
    }

    public void setPlaytime(int frames) {
        Hex.setByte4(rawPlaytime, 0x0, frames);
    }

    public int money() {
        return Hex.getByte4(rawMoney, 0x0);
    }

    public void setMoney(int value) {
        Hex.setByte4(rawMoney, 0x0, value);
    }

    public String report() {
        String report = "";
        //Difficulty Settings
        report += rawDifficulty.report();
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
        //Money
        report += " " + "Money: " + money();
        //Story Progress
        report += "\n" + "Chapters Beaten: " + progress.size();
        return report;
    }

    public int chaptersLength() {
        return 0x10 * progress.size() + 1;
    }

    public int length() {
        return header.length + rawPlaytime.length + chaptersLength() +
                rawDifficulty.length() + rawMoney.length + rawBlockEnd.length
                + rawBlock1.length + rawBlock2.length;
    }
}
