package com.danius.fireeditor;


import com.danius.fireeditor.controllers.MainController;
import com.danius.fireeditor.model.ClassModel;
import com.danius.fireeditor.savefile.Chapter13;
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
        Chapter13 chapter13 = readTestFile("Chapter0");
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
