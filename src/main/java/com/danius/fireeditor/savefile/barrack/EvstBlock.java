package com.danius.fireeditor.savefile.barrack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EvstBlock {

    private byte[] header; //Next events RNG encoded?
    public List<RawEvent> eventList; //Barrack Events

    public EvstBlock(byte[] bytes) {
        this.header = Arrays.copyOfRange(bytes, 0x0, 0x50);
        eventList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int offset = header.length + (0x8 * i);
            eventList.add(new RawEvent(Arrays.copyOfRange(bytes, offset, offset + 0x8)));
        }
    }

    public byte[] bytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(header);
            //All the maps are looped
            for (RawEvent event : eventList) {
                outputStream.write(event.bytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }
}
