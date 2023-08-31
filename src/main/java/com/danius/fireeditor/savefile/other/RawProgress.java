package com.danius.fireeditor.savefile.other;

import com.danius.fireeditor.util.Hex;

public class RawProgress {

    public byte[] bytes;

    public RawProgress(byte[] bytes) {
        this.bytes = bytes;
    }

    public RawProgress() {
        this.bytes = new byte[UserBlock.SIZE_MAP];
        bytes[0] = 0x1;
    }

    public int chapterId() {
        return bytes[0x1] & 0xFF;
    }

    public void setChapterId(int id) {
        bytes[0x1] = (byte) (id & 0xFF);
    }

    public int turns() {
        return Hex.getByte2(bytes, 0x2);
    }

    public void setTurns(int value) {
        Hex.setByte2(bytes, 0x2, value);
    }

    public int playTime() {
        return Hex.getByte4(bytes, 0x4);
    }

    public void setTime(int value) {
        Hex.setByte4(bytes, 0x4, value);
    }

    public int unitFirst() {
        return Hex.getByte2(bytes, 0x8);
    }

    public void setUnitFirst(int value) {
        Hex.setByte2(bytes, 0x8, value);
    }

    public int classFirst() {
        return Hex.getByte2(bytes, 0xA);
    }

    public void setClassFirst(int value) {
        Hex.setByte2(bytes, 0xA, value);
    }

    public int unitSecond() {
        return Hex.getByte2(bytes, 0xC);
    }

    public void setUnitSecond(int value) {
        Hex.setByte2(bytes, 0xC, value);
    }

    public int classSecond() {
        return Hex.getByte2(bytes, 0xE);
    }

    public void setClassSecond(int value) {
        Hex.setByte2(bytes, 0xE, value);
    }
}
