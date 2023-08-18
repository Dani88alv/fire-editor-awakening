package com.danius.fireeditor.savefile.units.mainblock;

import com.danius.fireeditor.util.Hex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RawFlags {

    /*
    Battle related data, a lot of flags
     */

    private final byte[] bytes;

    //0x1-0x2: ?

    public RawFlags(byte[] bytes) {
        this.bytes = bytes;
    }

    /*
    Ylisse, Plegia, Risen, Ruffian, etc
     */
    public int army() {
        int point = 0x2;
        return bytes[point] & 0xFF;
    }

    public void setArmy(int army) {
        int point = 0x2;
        bytes[point] = (byte) (army & 0xFF);
    }

    /*
    Deployment slot, maybe it is used after a battle to order the units
     */
    public int slotParty() {
        int point = 0x3;
        return bytes[point] & 0xFF;
    }

    public void setSlotParty(int slot) {
        int point = 0x3;
        bytes[point] = (byte) (slot & 0xFF);
    }

    //0x4-0x7: ?

    /*
    Group 1 (0x8)
    0x1: Female
    0x2: Hero
    0x4: Player
    0x8: Advanced Class
    0x10: Leader/Boss
    0x20: Defeat Condition
    0x40: Movement Ban
    0x80: Force Battle Animations

    Group 2 (0x9)
    0x1: Battle Animation Ban
    0x2: Experience Gain When Defeated +20
    0x4: Experience Gain When Defeated -10
    0x8: Unknown (Experience Gain +?)
    0x10: Marth/Lucina
    0x20: Walhart
    0x40: Aversa
    0x80: Owain

    Group 3 (0xA)
    0x1: Manakete
    0x2: Taguel
    0x4: Destroy Villages
    0x8: Crit Ban
    0x10: Avoid Ban
    0x20: Enemy Only
    0x40: Special Performances
    0x80: Guest

    Group 4 (0xB)
    0x1: Original Marth
    0x2: Entombed Experience (?)s
    0x4: Delivery Face (?)
    0x8: White Dragon
    0x10: ?
    0x20: ?
    0x40: ?
    0x80: ?
     */
    public String traitFlagString() {
        int point = 0x8;
        byte[] array = Arrays.copyOfRange(bytes, point, point + 4);
        return bytesToReversedBinaryString(array);
    }

    //0xC-0xF: ?

    /*
    Battle Flags
    Group 1 (0x10) Battle-Related
    0x1: Moved in this turn
    0x2: Paired Up (Front)
    0x4: Paired Up (Back)

    Group 2 (0x11) Battle-Related
    0x40: Invisible on battle
    0x80: Red sprite on battle

    Group 3 (0x12)
    0x8: Used Galeforce in this turn
    0x40: Married to Maiden

    Group 4 (0x13)
    0x2: Tiki meditating
    0x8: Portrait Change (?)
     */
    public String battleFlagString() {
        int point = 0x10;
        byte[] array = Arrays.copyOfRange(bytes, point, point + 4);
        return bytesToReversedBinaryString(array);
    }

    public void setTraitFlag(int flag, boolean set) {
        int point = 0x8;
        //The flag is set
        char[] flagsChar = traitFlagString().toCharArray();
        if (set) flagsChar[flag] = '1';
        else flagsChar[flag] = '0';
        //The string is converted back to byte array
        String flagString = new String(flagsChar);
        byte[] flagsArray = binaryStringToByteArray(flagString);
        for (int i = 0; i < flagsArray.length; i++) {
            bytes[point + i] = flagsArray[i];
        }
    }

    public void setBattleFlag(int flag, boolean set) {
        int point = 0x10;
        //The flag is set
        char[] flagsChar = battleFlagString().toCharArray();
        if (set) flagsChar[flag] = '1';
        else flagsChar[flag] = '0';
        //The string is converted back to byte array
        String flagString = new String(flagsChar);
        byte[] flagsArray = binaryStringToByteArray(flagString);
        for (int i = 0; i < flagsArray.length; i++) {
            bytes[point + i] = flagsArray[i];
        }
    }

    /*
    TODO Testear Supports en combate con seeds of trust
    0x14-0x19: ??
    0x1A: Byte identifier?
    0x1B: RES Addition (Any Value)
    0x1C: ??
    0x1D: STR+2, MAG+2, DEF+2, RES+2
    0x1E: STR+4
    0x1F: MAG+4
    0x20: SKL+4
    0x21: SPD+4
    0x22: LCK+8
    0x23: DEF+4
    0x24: RES+4
    0x25: All Stats +4 (No HP)
    0x26: Movement+1
    0x27: All Stats +4, Movement+1 (No HP)
    0x28: Tonic Flags (+5 HP, +2 Other)
    0x29: More flags (+4 every stat, HP does not work)
    0x2A: ??
     */

    //Stat boolean flags, excluding the tonics (slots 0-10)
    public boolean statAdditionFlag(int slot) {
        int point = 0x1D;
        int value = bytes[point + slot] & 0xFF;
        return value > 0;
    }

    public void setStatAddition(int slot, boolean isTrue) {
        int point = 0x1D;
        if (isTrue) bytes[point + slot] = 0x1;
        else bytes[point + slot] = 0x0;
    }

    //Tonic Bitflags
    public String tonicFlagString() {
        int point = 0x28;
        return byteToReversedBinaryString(bytes[point]);
    }

    public void setTonicFlag(int flag, boolean set){
        int point = 0x28;
        //The flag is set
        char[] flagsChar = tonicFlagString().toCharArray();
        if (set) flagsChar[flag] = '1';
        else flagsChar[flag] = '0';
        //The string is converted back to byte array
        String flagString = new String(flagsChar);
        byte[] flagsArray = binaryStringToByteArray(flagString);
        System.arraycopy(flagsArray, 0, bytes, point, flagsArray.length);
    }

    //Weird Stat Addition Bitflags
    public String extraStatsFlagString() {
        int point = 0x29;
        return byteToReversedBinaryString(bytes[point]);
    }

    public void setExtraStatFlag(int flag, boolean set){
        int point = 0x29;
        //The flag is set
        char[] flagsChar = tonicFlagString().toCharArray();
        if (set) flagsChar[flag] = '1';
        else flagsChar[flag] = '0';
        //The string is converted back to byte array
        String flagString = new String(flagsChar);
        byte[] flagsArray = binaryStringToByteArray(flagString);
        System.arraycopy(flagsArray, 0, bytes, point, flagsArray.length);
    }

    //Easier methods
    public void setAllTonicFlags(){
        int point = 0x28;
        bytes[point] = (byte) (0xFF);
    }

    public void setAllUnusedStats(){
        //Extra Stats Bitflags
        bytes[0x29] = (byte) (0xFF);
        //Regular Flags
        for (int i = 0x1D; i <= 0x27; i++){
            bytes[i] = (byte) (0x1);
        }
    }

    public List<Integer> traitFlagList() {
        List<Integer> flags = new ArrayList<>();
        String flagString = traitFlagString();
        for (int i = 0; i < flagString.length(); i++) {
            if (flagString.charAt(i) == '1') flags.add(i);
        }
        return flags;
    }

    public List<Integer> battleFlagList() {
        List<Integer> flags = new ArrayList<>();
        String flagString = battleFlagString();
        for (int i = 0; i < flagString.length(); i++) {
            if (flagString.charAt(i) == '1') flags.add(i);
        }
        return flags;
    }


    public String report() {
        String report = "";
        report += "Flags1: " + traitFlagString() + "\n";
        report += "Flags2: " + battleFlagString();
        //report += "Army: " + army();
        return report;
    }

    public byte[] bytes() {
        return bytes;
    }

    private String byteToReversedBinaryString(byte b) {
        String binaryString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
        StringBuilder reversedString = new StringBuilder(binaryString).reverse();

        return reversedString.toString();
    }

    private String bytesToReversedBinaryString(byte[] byteArray) {
        StringBuilder combinedString = new StringBuilder();

        for (byte b : byteArray) {
            String binaryString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            StringBuilder reversedString = new StringBuilder(binaryString).reverse();
            combinedString.append(reversedString);
        }

        return combinedString.toString();
    }

    /*
   Un-reverses the order of byteArrayToBinaryString to properly write the block to the unit
    */
    private static byte[] binaryStringToByteArray(String binaryString) {
        int length = binaryString.length();
        byte[] byteArray = new byte[length / 8];
        for (int i = 0; i < length; i += 8) {
            String byteString = binaryString.substring(i, i + 8);
            StringBuilder reversedByteString = new StringBuilder(byteString).reverse();
            try {
                byte b = (byte) Integer.parseInt(reversedByteString.toString(), 2);
                byteArray[i / 8] = b;
            } catch (NumberFormatException e) {
                System.err.println("Invalid binary digit found: " + reversedByteString);
                // Handle the error case as needed (e.g., assign a default value, skip the byte, etc.)
            }
        }
        return byteArray;
    }


}
