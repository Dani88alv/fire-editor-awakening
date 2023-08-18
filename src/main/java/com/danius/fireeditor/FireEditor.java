package com.danius.fireeditor;

import com.danius.fireeditor.controllers.ChapterController;
import com.danius.fireeditor.controllers.ConvoyController;
import com.danius.fireeditor.savefile.ChapterFile;
import com.danius.fireeditor.controllers.UnitController;
import com.danius.fireeditor.util.Hex;
import com.danius.fireeditor.util.Names;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FireEditor extends Application {
    public static ChapterFile chapterFile;
    public static UnitController unitController;
    public static ConvoyController convoyController;
    public static ChapterController chapterController;

    public static String citraUS = "C:\\Users\\user1\\AppData\\Roaming\\Citra\\sdmc\\Nintendo 3DS\\00000000000000000000000000000000\\00000000000000000000000000000000\\title\\00040000\\000a0500\\data\\00000001\\";
    String citraJP = "C:\\Users\\user1\\AppData\\Roaming\\Citra\\sdmc\\Nintendo 3DS\\00000000000000000000000000000000\\00000000000000000000000000000000\\title\\00040000\\00072000\\data\\00000001\\";
    String citraEU = "C:\\Users\\user1\\AppData\\Roaming\\Citra\\sdmc\\Nintendo 3DS\\00000000000000000000000000000000\\00000000000000000000000000000000\\title\\00040000\\0009f100\\data\\00000001\\";
    String path = citraUS + "Chapter2";

    @Override
    public void start(Stage stage) throws IOException {

        //byte[] fileBytes = Hex.getFileBytes(path);
        //chapterFile = new ChapterFile(fileBytes);
        FXMLLoader fxmlLoader = new FXMLLoader(FireEditor.class.getResource("viewMain.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 445);
        stage.setTitle("Fire Editor: Awakening");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}