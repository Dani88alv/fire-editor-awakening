package com.danius.fireeditor;

import com.danius.fireeditor.controllers.CheatController;
import com.danius.fireeditor.controllers.MainController;
import com.danius.fireeditor.controllers.convoy.ConvoyController;
import com.danius.fireeditor.controllers.unit.UnitController;
import com.danius.fireeditor.controllers.user.UserController;
import com.danius.fireeditor.savefile.Chapter13;
import com.danius.fireeditor.savefile.SaveFile;
import com.danius.fireeditor.savefile.global.Global;
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
    public static Global global;
    public static UnitController unitController;
    public static ConvoyController convoyController;
    public static UserController userController;
    public static CheatController cheatController;
    public static MainController mainController;

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

                    byte[] fileBytes = Hex.getFileBytes(content + saveFile);
                    boolean isChapter = SaveFile.isChapter(fileBytes);
                    if (isChapter) {
                        chapterFile = new Chapter13(fileBytes);
                    } else {
                        global = new Global(fileBytes);
                    }
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

    public static File readResource(String fileName) {
        String filePath = "templates/" + fileName;
        // Create a File object for the file path containing the actual file path
        File filePathFile = new File(filePath);

        // Check if the file path file exists and is not null
        if (filePathFile.exists()) {
            return filePathFile;  // Return the file if it exists
        } else {
            //System.out.println("File path does not exist: " + filePathFile.getAbsolutePath());
            return null;  // Return null if the file path doesn't exist or is null
        }
    }


}