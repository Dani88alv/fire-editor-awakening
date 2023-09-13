package com.danius.fireeditor.savefile.units.mainblock;

import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.util.Bitflag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RawFlags {

    /*
    Battle related data, a lot of flags
     */

    public final byte[] bytes;

    public RawFlags(){
        String path = Constants.RES_BLOCK + "rawUnitFlags";
        try {
            this.bytes = Objects.requireNonNull(RawFlags.class.getResourceAsStream(path)).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RawFlags(byte[] bytes) {
        this.bytes = bytes;
    }

    //0x0: Internal level?

    public int hiddenLevel(){
        int point = 0x0;
        return bytes[point] & 0xFF;
    }

    public void setHiddenLevel(int value){
        int point = 0x0;
        bytes[point] = (byte) (value & 0xFF);
    }

    /*
    Ylisse, Plegia, Risen, Ruffian, etc
     */
    public int army() {
        int point = 0x1;
        return bytes[point] & 0xFF;
    }

    public void setArmy(int army) {
        int point = 0x1;
        bytes[point] = (byte) (army & 0xFF);
    }

    /*
    Deployment slot, maybe it is used after a battle to order the units
     */
    public int slotParty() {
        int point = 0x2;
        return bytes[point] & 0xFF;
    }

    public void setSlotParty(int slot) {
        int point = 0x2;
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
        int point = 0x7;
        byte[] array = Arrays.copyOfRange(bytes, point, point + 4);
        return Bitflag.bytesToReversedBinaryString(array);
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
        int point = 0xF;
        byte[] array = Arrays.copyOfRange(bytes, point, point + 4);
        return Bitflag.bytesToReversedBinaryString(array);
    }

    public void setTraitFlag(int flag, boolean set) {
        int point = 0x7;
        //The flag is set
        char[] flagsChar = traitFlagString().toCharArray();
        if (set) flagsChar[flag] = '1';
        else flagsChar[flag] = '0';
        //The string is converted back to byte array
        String flagString = new String(flagsChar);
        byte[] flagsArray = Bitflag.binaryStringToByteArray(flagString);
        for (int i = 0; i < flagsArray.length; i++) {
            bytes[point + i] = flagsArray[i];
        }
    }

    public void setBattleFlag(int flag, boolean set) {
        int point = 0xF;
        //The flag is set
        char[] flagsChar = battleFlagString().toCharArray();
        if (set) flagsChar[flag] = '1';
        else flagsChar[flag] = '0';
        //The string is converted back to byte array
        String flagString = new String(flagsChar);
        byte[] flagsArray = Bitflag.binaryStringToByteArray(flagString);
        for (int i = 0; i < flagsArray.length; i++) {
            bytes[point + i] = flagsArray[i];
        }
    }

    /*
    0x13-0x18: ??
    0x19: 0x2, do not mess with it
    0x1A: RES Boost (Pure Water)
    0x1B: ??
    0x1C: STR+2, MAG+2, DEF+2, RES+2
    0x1D: STR+4
    0x1E: MAG+4
    0x1F: SKL+4
    0x20: SPD+4
    0x21: LCK+8
    0x22: DEF+4
    0x23: RES+4
    0x24: All Stats +4 (No HP)
    0x25: Movement+1
    0x26: All Stats +2, Movement+1 (No HP)
    0x27: Tonic Bitflags (+5 HP, +2 Other)
    0x28: Barrack boost bitflags (+4 every stat, HP does not work)
    0x29: ??
     */

    //Pure Water Buff
    public int resBuff() {
        return bytes[0x1A] & 0xFF;
    }

    public void setResBuff(int value) {
        bytes[0x1A] = (byte) (value);
    }

    //Stat boolean flags, excluding the tonics (slots 0-10)
    public boolean skillBuffFlag(int slot) {
        int point = 0x1C;
        int value = bytes[point + slot] & 0xFF;
        return value > 0;
    }

    public void setSkillBuffFlag(int slot, boolean isTrue) {
        int point = 0x1C;
        if (isTrue) bytes[point + slot] = 0x1;
        else bytes[point + slot] = 0x0;
    }

    //Tonic Bitflags
    public String tonicFlagString() {
        int point = 0x27;
        return Bitflag.byte1ToReversedBinaryString(bytes[point]);
    }

    public void setTonicFlag(int flag, boolean set) {
        int point = 0x27;
        Bitflag.setByte1Flag(bytes, point, flag, set);
    }

    //Barrack Buffs Bitflags
    public String barrackFlagString() {
        int point = 0x28;
        return Bitflag.byte1ToReversedBinaryString(bytes[point]);
    }

    public void setBarrackFlag(int flag, boolean set) {
        int point = 0x28;
        Bitflag.setByte1Flag(bytes, point, flag, set);
    }

    //Easier methods
    public void setAllTonicFlags() {
        int point = 0x27;
        bytes[point] = (byte) (0xFF);
    }

    public void setAllOtherBuffs() {
        //Barrack Bitflags
        bytes[0x28] = (byte) (0xFF);
        //Skill Flags
        for (int i = 0x1C; i <= 0x26; i++) {
            bytes[i] = (byte) (0x1);
        }
        //Pure Water
        setResBuff(5);
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

}
