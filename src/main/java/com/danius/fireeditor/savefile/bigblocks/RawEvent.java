package com.danius.fireeditor.savefile.bigblocks;

import com.danius.fireeditor.util.Hex;

public class RawEvent {

    private byte[] bytes;

    public RawEvent(byte[] bytes) {
        this.bytes = bytes;
    }

    /*
    0: Invalid
    1: Stat Boost
    2: Exp Gain
    3: Weapon Exp Gain
    4: Random Item
    5: Conversation
    6: Birthday
     */
    public int eventType() {
        int point = 0x2;
        return Hex.getByte2(bytes, point);
    }

    public void setEventType(int id) {
        int point = 0x2;
        Hex.setByte2(bytes, point, id);
    }

    public int unit1() {
        int point = 0x3;
        return Hex.getByte2(bytes, point);
    }

    public int unit2() {
        int point = 0x5;
        return Hex.getByte2(bytes, point);
    }

    public void setUnit1(int value) {
        int point = 0x3;
        Hex.setByte2(bytes, point, value);
    }

    public void setUnit2(int value) {
        int point = 0x5;
        Hex.setByte2(bytes, point, value);
    }

    //Only used on special conversations or birthdays
    public int eventIcon(){
        int point = 0x7;
        return bytes[0x7] & 0xFF;
    }

    public void setEventIcon(int value){
        int point = 0x7;
        Hex.setByte2(bytes, point, value);
    }

    public byte[] bytes() {
        //Shifts the character slot if the first unit is invalid
        if (unit1() == 65535 && unit2() != 65535) {
            setUnit1(unit2());
            setUnit2(65535);
        }
        int unitAmount = 0;
        if (unit1() != 65535) unitAmount += 1;
        if (unit2() != 65535) unitAmount += 1;
        //The unit count is updated
        bytes[0x1] = (byte) (unitAmount);
        return bytes;
    }

}
