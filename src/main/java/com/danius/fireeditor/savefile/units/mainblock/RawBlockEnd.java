package com.danius.fireeditor.savefile.units.mainblock;

import com.danius.fireeditor.util.Hex;

public class RawBlockEnd {

    private byte[] blockBytes;

    public RawBlockEnd(byte[] blockBytes) {
        this.blockBytes = blockBytes;
    }

    public boolean deadFlag1() {
        int point = 0x35;
        return (blockBytes[point] & 0xFF) == 1;
    }

    public boolean deadFlag2() {
        int point = 0x38;
        return (blockBytes[point] & 0xFF) == 1;
    }

    public void setDeadFlag1(boolean set) {
        int point = 0x35;
        blockBytes[point] = (byte) (set ? 1 : 0);
    }

    public void setDeadFlag2(boolean set) {
        int point = 0x38;
        blockBytes[point] = (byte) (set ? 1 : 0);
    }

    public int retireChapter(){
        int point = 0x37;
        return blockBytes[point] & 0xFF;
    }

    public void setRetireChapter(int value){
        int point = 0x37;
        blockBytes[point] = (byte) (value & 0xFF);
    }

    public String getHairColor() {
        int pointer = 0x39;
        byte[] hairColorBytes = new byte[3];
        hairColorBytes[0] = blockBytes[pointer];
        hairColorBytes[1] = blockBytes[pointer + 1];
        hairColorBytes[2] = blockBytes[pointer + 2];
        // Combine the three bytes into a single integer value using bitwise operators
        int hairColor = ((hairColorBytes[0] & 0xFF) << 16) | ((hairColorBytes[1] & 0xFF) << 8) | (hairColorBytes[2] & 0xFF);
        // Convert the integer to a hexadecimal string with leading zeros
        return String.format("%06X", hairColor);
    }


    public void setHairColor(String hexString) {
        int pointer = 0x39;
        Hex.setColorToByteArray(blockBytes, pointer, hexString);
    }

    /*
   Retrieves the battles and victories of a unit
    */
    public int battleCount() {
        int point = 0x31;
        return Hex.getByte2(blockBytes, point);
    }

    public int victoryCount() {
        int point = 0x33;
        return Hex.getByte2(blockBytes, point);
    }

    public void setBattles(int battleCount) {
        int point = 0x31;
        Hex.setByte2(blockBytes, point, battleCount); //Battles
    }

    public void setVictories(int victoryCount) {
        int point = 0x31;
        Hex.setByte2(blockBytes, point + 2, victoryCount); //Victories
    }

    public byte[] getBlockBytes() {
        return blockBytes;
    }

    /*
    Adds or remove the terminator of the additional block
     */
    public void addOffsetChild() {
        blockBytes[length() - 2] = 0x0;
        blockBytes[length() - 1] = 0x1;
    }

    public void addOffsetLog() {
        blockBytes[length() - 2] = 0x1;
        blockBytes[length() - 1] = 0x6;
    }

    public void removeOffsetBlock() {
        blockBytes[length() - 2] = 0x0;
        blockBytes[length() - 1] = 0x0;
    }

    public String report() {
        String text = "";
        text += "\n" + "Battles: " + battleCount() +
                " Victories: " + victoryCount();

        //Hair color (regular units also store it, though it only changes the color of their children)
        text += "\n" + "Hair: #" + getHairColor();
        return text;
    }

    public int length() {
        return blockBytes.length;
    }
}
