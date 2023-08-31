package com.danius.fireeditor.savefile.other;

import com.danius.fireeditor.util.Bitflag;

public class RawDifficulty {

    public byte[] bytes;

    public RawDifficulty(byte[] bytes) {
        this.bytes = bytes;
        //TODO: en los mapas penalty tiene valores como 06
    }

    /*
    TODO Map flags
    0: 0x1
    1: 0x2
    2: 0x4 Casual
    3: 0x8
    4: 0x10
    5: 0x20 Cutscene?
    6: 0x40
    7: 0x80
     */
    public boolean gameModeFlag(int slot) {
        int point = 0x0;
        return Bitflag.byte1ToReversedBinaryString(bytes[point]).charAt(slot) == '1';
    }

    public void setGameModeFlag(int slot, boolean set) {
        int point = 0x0;
        Bitflag.setByte1Flag(bytes, point, slot, set);
    }

    /*
    Lunatic+ is a flag applied to the lunatic difficulty
     */
    public boolean isLunaticPlus() {
        return (bytes[0x1] & 0xFF) == 1;
    }

    public void setLunaticPlus(boolean lunaticPlus) {
        if (lunaticPlus) bytes[0x1] = 1;
        else bytes[0x1] = 0;
    }

    /*
    Base Difficulties: Normal, Hard and Lunatic
     */
    public int difficulty() {
        return bytes[0x5] & 0xFF;
    }

    public void setDifficulty(int id) {
        bytes[0x5] = (byte) (id & 0xFF);
    }

    public String report() {
        String report = "";
        report += "Difficulty: ";
        if (difficulty() == 0) report += "Normal";
        if (difficulty() == 1) report += "Hard";
        if (difficulty() == 2) report += "Lunatic";
        //Lunatic+ Flag
        if (isLunaticPlus()) report += " (Lunatic+)";
        return report;
    }

    public int length() {
        return bytes.length;
    }
}
