package com.danius.fireeditor.savefile;

import com.danius.fireeditor.savefile.global.GlobalFile;
import com.danius.fireeditor.util.Hex;


public class Main {
    public static void main(String[] args) {
        String citraUs = "C:\\Users\\user1\\AppData\\Roaming\\Citra\\sdmc\\Nintendo 3DS\\00000000000000000000000000000000\\00000000000000000000000000000000\\title\\00040000\\000a0500\\data\\00000001\\";
        String download = "C:\\Users\\user1\\Downloads\\Chapter0 (1)";
        byte[] fileBytes = Hex.getFileBytes(download);
        Hex.writeFile(SaveFile.decompressBytes(fileBytes, 0xC0), download);

    }

}
