package com.danius.fireeditor.savefile.units.mainblock;

import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.wireless.UnitDu;
import com.danius.fireeditor.util.Hex;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class RawBlockEnd {

    private byte[] blockBytes;

    public RawBlockEnd(){
        String path = Constants.RES_BLOCK + "rawUnitEnd";
        try {
            this.blockBytes = Objects.requireNonNull(RawBlockEnd.class.getResourceAsStream(path)).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RawBlockEnd(byte[] blockBytes) {
        this.blockBytes = blockBytes;
    }
    /*
    0x4 End Section 1 (byte 1)
    0x5 End Section 1 (byte 2)
    0x6 End Section 2 (byte 2)
    0x7
    0x8 End Section 2 (byte 3)
    0x9 End Section 2 (byte 1)
    0xA-0xB
    0xC Action AI
    0xD Mission AI
    0xE Attack AI
    0xF Move AI
    0x10-0x17 Action AI Param (4)
    0x18-0x1F Mission AI Param (4)
    0x20-0x27 Attack AI Param (4)
    0x28-0x2F Move AI Param (4)
     */

    /*
    PARAM Notes
    "0" is stored as 00 00
    "pos(2,1)" is 02 01
     */

    /*
    0xC ACTION
    0x00 AI_AC_Null
    0x01 AI_AC_Everytime
    0x02 AI_AC_AttackRange || numeric
    0x03 AI_AC_AttackRangeExcludePerson || V_Default,PID_サイリ
    0x04 AI_AC_BandRange
    0x05
    0x06
    0x07
    0x08
    0x09
    0x0A AI_AC_Turn
    0x0B AI_AC_FlagTrue || FLAG_敵突撃 || FLAG_セレナ敵対 (Severa) || FLAG_変化壁１開 || FLAG_変化壁開 || FLAG_変化壁挟撃開 || FLAG_変化壁３
    0x0C
    0x0D AI_AC_TurnAttackRange
    0x0E AI_AC_TurnBandRange
    0x0F AI_AC_TurnAttackRangeHealRange
    0x10 AI_AC_FlagTrueAttackRange || FLAG_敵突撃
    0x11
    0x12
    0x13
    0x14 AI_AC_FlagTrueAttackRangeExcludePerson || FLAG_敵突撃,PID_サイリ
     */

    /*
    0xD MISSION
    0x00 AI_MI_Null
    0x01 AI_MI_Talk
    0x02 AI_MI_Treasure || Param pos(5,17)
    0x03 AI_MI_Village || pos(18,3)
    0x04
    0x05 AI_MI_EscapeSlow || Param pos(2,1)
    0x06
    0x07 AI_MI_X009Boss || pos(20,1),50
    0x08 AI_MI_X010Serena || PID_X010_ホラント
     */

    /*
    0xE ATTACK
    0x00 AI_AT_Null
    0x01 AI_AT_Attack
    0x02 AI_AT_MustAttack
    0x03 AI_AT_Heal
    0x04 AI_AT_AttackToHeal
    0x05 AI_AT_AttackToMustHeal
    0x06 AI_AT_MustAttackToMustHeal
    0x07
    0x08
    0x09 AI_AT_Person || Param Unit ID
    0x0A AI_AT_ExcludePerson (CID_9) || Param Unit ID
    0x0B
    0x0C
    0x0D AI_AT_X002Anna || JID_蛮族男,pos(18,3)
    0x0E AI_AT_X017Enemy (Tiki Paralogue) || Param Unit ID
     */

    /*
    0xF MOVE
    0x00 AI_MV_Null || nothing / PID_サイリ
    0x01 AI_MV_NearestEnemy
    0x02
    0x03 AI_MV_NearestEnemyExcludePerson || Param Unit ID
    0x04
    0x05
    0x06
    0x07
    0x08
    0x09
    0x0A AI_MV_Person || Param Unit ID
    0x0B
    0x0C AI_MV_Position || pos(1,16)
    0x0D
    0x0E AI_MV_EscapeSlow || Param pos(2,1)
    0x0F AI_MV_TrasureToEscape || Param pos(5,17),0,pos(20,21)
    0x10 AI_MV_VillageToAttack || pos(18,5),V_Max
    0x11 AI_MV_VillageNoThroughToAttack || pos(18,3)
    0x12
    0x13
    0x14 AI_MV_Irregular (Mirage)
    0x15 AI_MV_X009Boss || pos(20,1)
    0x16 AI_MV_X010Serena || PID_X010_ホラント
    0x17 AI_MV_X017Enemy (Tiki Paralogue) || Param Unit ID
     */

    public String endSectionString(){
        int point = 0x4;
        int pontEnd = 0xB;
        byte[] array = Arrays.copyOfRange(blockBytes, point, pontEnd);
        StringBuilder hexString = new StringBuilder();
        for (byte b : array) {
            String hex = String.format("%02X", b); // Convert the byte to a two-digit hexadecimal representation
            hexString.append(hex);
        }
        return hexString.toString();
    }

    //Action, Mission, Attack & Move
    public int aiType(int slot) {
        int point = 0xC;
        return blockBytes[point + slot] & 0xFF;
    }

    public void setAiType(int slot, int value) {
        int point = 0xC;
        blockBytes[point + slot] = (byte) (value & 0xFF);
    }

    public int aiParam(int aiSlot, int paramSlot) {
        int point = 0x10;
        return Hex.getByte2(blockBytes, point + (paramSlot * 0x2) + (aiSlot * 0x8));
    }

    public void setAiParam(int aiSlot, int paramSlot, int value) {
        int point = 0x10;
        Hex.setByte2(blockBytes, point + (paramSlot * 0x2) + (aiSlot * 0x8), value);
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

    public int retireChapter() {
        int point = 0x37;
        return blockBytes[point] & 0xFF;
    }

    public void setRetireChapter(int value) {
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

    public byte[] bytes() {
        return blockBytes;
    }

    /*
    Adds or remove the terminator of the additional block
    00 01 child
    01 06 log
     */
    public void setTerminator(int byte1, int byte2) {
        blockBytes[length() - 2] = (byte) (byte1 & 0xFF);
        blockBytes[length() - 1] = (byte) (byte2 & 0xFF);
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
