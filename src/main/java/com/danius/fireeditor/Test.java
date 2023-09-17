package com.danius.fireeditor;


import com.danius.fireeditor.controllers.MainController;
import com.danius.fireeditor.savefile.Chapter13;
import com.danius.fireeditor.savefile.SaveFile;
import com.danius.fireeditor.savefile.inventory.Refinement;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.util.Hex;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) throws IOException {
        FireEditor.loadResources();
        Chapter13 chapter13 = readTestFile("Chapter1");
        //System.out.println(Hex.byteArrayToString(Hex.toByte("31 00 32 00 6F 30 6F 30 C1 30 AD 30 26 20 5E 79 DC 7A CF 65 6E 30 1F 75 4D 30 8B 6B 38 00 26 20 00 00")));
        //Hex.writeFile(SaveFile.autoDecompress(Hex.getFileBytes(aa)), aa + "_dec");
        String fatesEu = "C:\\Users\\user1\\AppData\\Roaming\\Citra\\sdmc\\Nintendo 3DS\\00000000000000000000000000000000\\00000000000000000000000000000000\\title\\00040000\\0017a800\\data\\00000001\\";
        //Hex.writeFile(SaveFile.autoDecompress(Hex.getFileBytes(fatesEu + "Rating")), fatesEu + "Rating_dec");
        //Hex.writeFile(readTestFile("Chapter1").blockDu26.playerTeam.unitList.get(0).toUnit().getUnitBytes(), "TY");
    }

    private static void printHexArray(byte[] array) {
        for (byte b : array) {
            System.out.printf("%02X ", b);
        }
        System.out.println();
    }

    public static Chapter13 readTestFile(String nameFile) throws IOException {
        String filePath = "templates/path.txt";
        // Create a File object for the file path containing the actual file path
        File filePathFile = new File(filePath);
        // Read the content of the file path file
        Path path = Paths.get(filePathFile.getAbsolutePath());
        List<String> lines = Files.readAllLines(path);

        String content = lines.get(0);  // Assuming the first line contains the actual file path
        // Create a File object for the content (actual file path)
        MainController.backupFile = new File(content + nameFile);
        MainController.path = content;
        return new Chapter13(Hex.getFileBytes(content + nameFile));
    }
}
