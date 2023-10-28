package com.danius.fireeditor.savefile.units.mainblock;

import com.danius.fireeditor.savefile.Constants;

import java.io.IOException;
import java.util.Objects;

import static com.danius.fireeditor.util.Hex.*;

public class RawFlags {

    /*
    Battle related data, a lot of flags
     */

    public final byte[] bytes;

    public RawFlags() {
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

    public int hiddenLevel() {
        int point = 0x0;
        return bytes[point] & 0xFF;
    }

    public void setHiddenLevel(int value) {
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

    //0x4-0x5: ?
    //0x6: Advanced Auto Battle Settings
    //0x7: ?

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

    public boolean hasTraitFlag(int slot) {
        int point = 0x7;
        return hasBitFlag(bytes, point, slot);
    }

    public void setTraitFlag(int bit, boolean set) {
        int point = 0x7;
        setBitFlag(bytes, point, bit, set);
    }

    //0xC-0xF: ?

    //They are like trait flags, but they seem to be used in scripts instead of the ROMfs
    public boolean hasBattleFlag(int slot) {
        int point = 0xF;
        return hasBitFlag(bytes, point, slot);
    }

    public void setBattleFlag(int bit, boolean set) {
        int point = 0xF; // Offset for battle flags
        setBitFlag(bytes, point, bit, set);
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

    //Skills buffs flags
    public boolean hasSkillFlag(int slot) {
        int point = 0x1C;
        int value = bytes[point + slot] & 0xFF;
        return value > 0;
    }

    public void setSkillBuffFlag(int slot, boolean set) {
        int point = 0x1C;
        bytes[point + slot] = (byte) (set ? 0x1 : 0x0);
    }

    //Tonic Bitflags
    public boolean hasTonicFlag(int slot) {
        int point = 0x27;
        return hasBitFlag(bytes[point], slot);
    }

    public void setTonicFlag(int flag, boolean set) {
        int point = 0x27;
        setBitFlag(bytes, point, flag, set);
    }

    //Barrack Buffs Bitflags
    public boolean hasBarrackFlag(int slot) {
        int point = 0x28;
        return hasBitFlag(bytes[point], slot);
    }

    public void setBarrackFlag(int flag, boolean set) {
        int point = 0x28;
        setBitFlag(bytes, point, flag, set);
    }

    //Batch
    public void setAllTonicFlags(boolean set) {
        for (int i = 0; i < 8; i++) setTonicFlag(i, set);
    }

    public void setAllBarrackFlags(boolean set) {
        for (int i = 1; i < 8; i++) setBarrackFlag(i, set);
    }

    public void setAllOtherBuffs(boolean set) {
        //Skill Flags
        for (int i = 0x1C; i <= 0x26; i++) {
            bytes[i] = (byte) (0x1);
        }
        //Pure Water
        setResBuff(5);
    }

    public String report() {
        return "";
    }

    public byte[] bytes() {
        return bytes;
    }

}
