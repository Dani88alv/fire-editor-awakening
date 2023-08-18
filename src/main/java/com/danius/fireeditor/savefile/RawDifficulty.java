package com.danius.fireeditor.savefile;

public class RawDifficulty {

    public byte[] bytes;

    public RawDifficulty(byte[] bytes) {
        this.bytes = bytes;
        //TODO: en los mapas penalty tiene valores como 06
        //if (bytes[0x0] != 0 && bytes[0x0] != 4) setPenalty(false); //Invalid values
        //if (difficulty() > 2) setDifficulty(2); //Invalid Values
    }

    /*
    Classic: 0
    Casual: 4
     */
    public int penaltyId() {
        return bytes[0x0] & 0xFF;
    }

    public void setPenalty(int id) {
        bytes[0x0] = (byte) (id & 0xFF);
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
        //Penalty
        report += " (Mode: " + penaltyId() + ")";
        //Lunatic+ Flag
        if (isLunaticPlus()) report += " (Lunatic+)";
        return report;
    }

    public int length() {
        return bytes.length;
    }
}
