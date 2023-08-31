package com.danius.fireeditor.savefile.other;

public class RawMap {

    public byte[] bytes;

    public RawMap(byte[] bytes) {
        this.bytes = bytes;
    }

    public int lockState() {
        return bytes[0x1] & 0xFF;
    }

    //00: Locked, 01: Beaten, 02: Not Beaten
    public void setLockState(int value){
        bytes[0x1] = (byte) (value & 0xFF);
    }

    //0x5 boss class, 0x3-0x4 need to be 0x1 in order to activate it
}
