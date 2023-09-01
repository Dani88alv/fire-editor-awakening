package com.danius.fireeditor;

import com.danius.fireeditor.controllers.*;
import com.danius.fireeditor.savefile.Chapter13;
import com.danius.fireeditor.savefile.Constants13;
import com.danius.fireeditor.util.Hex;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FireEditor extends Application {
    public static Chapter13 chapterFile;
    public static UnitController unitController;
    public static ConvoyController convoyController;
    public static ChapterController chapterController;
    public static CheatController cheatController;
    public static MainController mainController;

    public static int maxClasses = Constants13.MAX_CLASSES;
    public static int maxArmies = Constants13.MAX_ARMY;

    @Override
    public void start(Stage stage) throws IOException {
        readTestFile("Chapter0");
        FXMLLoader fxmlLoader = new FXMLLoader(FireEditor.class.getResource("viewMain.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 445);
        stage.setTitle("Fire Editor: Awakening");
        stage.setScene(scene);
        stage.show();
    }

    public void readTestFile(String saveFile) {
        String filePath = "templates/path.txt";
        try {
            // Create a File object for the file path containing the actual file path
            File filePathFile = new File(filePath);
            // Check if the file path file exists
            if (!filePathFile.exists()) {
                System.out.println("File path does not exist: " + filePathFile.getAbsolutePath());
                return;  // Exit the method if the file path doesn't exist
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
                    MainController.backupFile = file;
                    MainController.path = content;
                    chapterFile = new Chapter13(Hex.getFileBytes(content + saveFile));
                } else {
                    System.out.println("File does not exist: " + file.getAbsolutePath());
                }
            } else {
                System.out.println("File path content is empty.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }

    //Scans the whole save file to find additional modded classes
    public static int maxClasses() {
        int maxClasses = Constants13.MAX_CLASSES;
        if (chapterFile != null) {
            //The modded classes are checked viewing all the stored units
            for (int i = 0; i < chapterFile.blockUnit.unitList.size(); i++) {
                for (int j = 0; j < chapterFile.blockUnit.unitList.get(i).size(); j++) {
                    int unitClass = chapterFile.blockUnit.unitList.get(i).get(j).rawBlock1.unitClass();
                    if (unitClass > Constants13.MAX_CLASSES) {
                        if (unitClass > maxClasses) maxClasses = unitClass;
                    }
                    //The logbook class is checked
                    if (chapterFile.blockUnit.unitList.get(i).get(j).rawLog != null) {
                        int logClass = chapterFile.blockUnit.unitList.get(i).get(j).rawLog.getProfileCard()[0];
                        if (logClass > maxClasses) maxClasses = logClass;
                    }
                }
            }
            //The credit records are checked
            for (int i = 0; i < chapterFile.blockUser.progress.size(); i++) {
                int classFirst = chapterFile.blockUser.progress.get(i).classFirst();
                int classSecond = chapterFile.blockUser.progress.get(i).classSecond();
                if (classFirst != 65535 && classFirst > maxClasses) {
                    maxClasses = classFirst;
                }
                if (classSecond != 65535 && classSecond > maxClasses) {
                    maxClasses = classSecond;
                }
            }
        }
        return maxClasses;
    }

    public static int maxArmies() {
        int maxArmies = Constants13.MAX_ARMY;
        for (int i = 0; i < chapterFile.blockUnit.unitList.size(); i++) {
            for (int j = 0; j < chapterFile.blockUnit.unitList.get(i).size(); j++) {
                int army = chapterFile.blockUnit.unitList.get(i).get(j).rawFlags.army();
                if (army > Constants13.MAX_ARMY) {
                    if (army > maxArmies) maxArmies = army;
                }
            }
        }
        return maxArmies;
    }
}