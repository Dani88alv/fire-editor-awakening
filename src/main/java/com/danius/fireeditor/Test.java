package com.danius.fireeditor;


import com.danius.fireeditor.model.ItemDb;
import com.danius.fireeditor.savefile.Chapter13;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.mainblock.RawInventory;
import com.danius.fireeditor.savefile.units.mainblock.RawItem;
import com.danius.fireeditor.util.Hex;
import javafx.scene.paint.Color;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) throws IOException {

    }

    private static void printHexArray(byte[] array) {
        for (byte b : array) {
            System.out.printf("%02X ", b);
        }
        System.out.println();
    }

    public static byte[] readTestFile(String saveFile) throws IOException {
        String filePath = "templates/path.txt";
        try {
            // Create a File object for the file path containing the actual file path
            File filePathFile = new File(filePath);
            // Check if the file path file exists
            if (!filePathFile.exists()) {
                System.out.println("File path does not exist: " + filePathFile.getAbsolutePath());
                return null;  // Exit the method if the file path doesn't exist
            }
            // Read the content of the file path file
            Path path = Paths.get(filePathFile.getAbsolutePath());
            List<String> lines = Files.readAllLines(path);
            if (!lines.isEmpty()) {
                String content = lines.get(0);  // Assuming the first line contains the actual file path
                // Create a File object for the content (actual file path)
                File file = new File(content + saveFile);
                // Check if the file exists
                if (file.exists()) {
                    System.out.println("File exists: " + file.getAbsolutePath());
                    return Hex.getFileBytes(content + saveFile);
                } else {
                    System.out.println("File does not exist: " + file.getAbsolutePath());
                }
            } else {
                System.out.println("File path content is empty.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
