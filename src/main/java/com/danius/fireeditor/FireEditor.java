package com.danius.fireeditor;

import com.danius.fireeditor.controllers.ChapterController;
import com.danius.fireeditor.controllers.ConvoyController;
import com.danius.fireeditor.savefile.ChapterFile;
import com.danius.fireeditor.controllers.UnitController;
import com.danius.fireeditor.savefile.Constants;
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

    //Scans the whole save file to find additional modded classes
    public static int maxClasses() {
        int maxClasses = Constants.MAX_CLASSES;
        if (chapterFile != null){
            //The modded classes are checked viewing all the stored units
            for (int i = 0; i < chapterFile.blockUnit.unitList.size(); i++) {
                for (int j = 0; j < chapterFile.blockUnit.unitList.get(i).size(); j++) {
                    int unitClass =chapterFile.blockUnit.unitList.get(i).get(j).rawBlock1.unitClass();
                    if (unitClass > Constants.MAX_CLASSES) {
                        if (unitClass > maxClasses) maxClasses = unitClass;
                    }
                    //The logbook class is checked
                    if (chapterFile.blockUnit.unitList.get(i).get(j).rawLog != null){
                        int logClass = chapterFile.blockUnit.unitList.get(i).get(j).rawLog.getProfileCard()[0];
                        if (logClass > maxClasses) maxClasses = logClass;
                    }
                }
            }
            //The credit records are checked
            for (int i = 0; i < chapterFile.blockUser.progress.size(); i++){
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
}