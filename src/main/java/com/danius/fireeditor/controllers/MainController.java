package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.Chapter13;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.units.Unit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainController {

    public static String path = System.getProperty("user.dir");
    public static File backupFile;

    @FXML
    private Tab tabConvoy, tabChapter, tabUnit, tabCheats;

    public void initialize() {
        FireEditor.mainController = this;
        if (FireEditor.chapterFile == null) {
            FireEditor.unitController.disableElements(true);
            FireEditor.unitController.comboUnitGroup.setDisable(true);
            tabConvoy.setDisable(true);
            tabChapter.setDisable(true);
            tabCheats.setDisable(true);
        }
    }

    public void openFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(path));

        File file = fileChooser.showOpenDialog(null);
        if (file == null) return;
        path = file.getParent();
        backupFile = file;
        //The blocks are initialized
        try {
            reloadTabs(Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void reloadTabs(byte[] fileBytes) {
        try {
            FireEditor.chapterFile = new Chapter13(fileBytes);
            FireEditor.unitController.loadUnitBlock();
            FireEditor.unitController.comboUnitGroup.setDisable(false);
            tabConvoy.setDisable(false);
            tabChapter.setDisable(false);
            tabCheats.setDisable(false);
            FireEditor.convoyController.loadBlocks();
            FireEditor.chapterController.loadBlocks();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void createAndExportBackup() throws IOException {
        // Create the "bak" folder if it doesn't exist
        String userDir = System.getProperty("user.dir");
        File bakFolder = new File(userDir, "bak");
        if (!bakFolder.exists()) {
            boolean created = bakFolder.mkdir();
            if (!created) {
                System.err.println("Failed to create 'bak' directory.");
                return;
            }
        }
        // Generate a timestamp for the filename
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        // Get the original filename
        String originalFilename = backupFile.getName();
        // Create the backup file path with the timestamp and original filename
        String backupFilePath = bakFolder.getAbsolutePath() + File.separator + timestamp + "_" + originalFilename;
        // Read content from the backupFile and write to the backup file
        try (FileInputStream fis = new FileInputStream(backupFile);
             FileOutputStream fos = new FileOutputStream(backupFilePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    public void exportDecomp() throws IOException {
        if (FireEditor.chapterFile == null) return;
        //The data is compiled
        byte[] data = compileBlocks(true);
        //File chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(path));
        // Show save dialog
        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;
        createAndExportBackup();
        // Save byte array to the selected file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
    }

    public void exportComp() throws IOException {
        if (FireEditor.chapterFile == null) return;
        //The data is compiled
        byte[] data = compileBlocks(false);
        //File chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(path));
        // Show save dialog
        File file = fileChooser.showSaveDialog(null);
        createAndExportBackup();
        if (file == null) return;
        // Save byte array to the selected file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
    }

    public void importUnit() {
        if (FireEditor.chapterFile == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(path));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles == null || selectedFiles.isEmpty()) return;

        path = selectedFiles.get(0).getParent();
        int totalSize = FireEditor.unitController.listViewUnit.getItems().size();
        boolean isWest = FireEditor.unitController.unitBlock.isWest;
        for (File file : selectedFiles) {
            try {
                byte[] data = Files.readAllBytes(file.toPath());
                Unit unit = new Unit(data);
                if (unit.rawLog != null) unit.rawLog.changeRegion(isWest); //Converts the unit to the current region
                if (totalSize < Constants.UNIT_LIMIT) {
                    FireEditor.unitController.addUnit(unit);
                }
                totalSize++;
            } catch (Exception e) {
                throw new RuntimeException("INVALID UNIT FILE!");
            }
        }
    }


    public void exportUnit() {
        //The data is compiled
        //FireEditor.unitController.updateUnitFromFields(
        //        FireEditor.unitController.listViewUnit.getSelectionModel().getSelectedItem());
        Unit unit = FireEditor.unitController.listViewUnit.getSelectionModel().getSelectedItem();
        if (unit == null) return;
        //File chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(path));
        fileChooser.setInitialFileName(unit.unitName());
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "Fire Emblem Awakening Unit", "*" + ".fe13");
        fileChooser.getExtensionFilters().add(extensionFilter);
        // Show save dialog
        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;
        path = file.getParent();
        // Save byte array to the selected file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(unit.getUnitBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] compileBlocks(boolean decomp) {
        //The refinement uses are updated
        //FireEditor.chapterFile.blockTran.setItemAmountFromList(FireEditor.convoyController.getItemUses());
        FireEditor.convoyController.loseSpinnerFocus();
        //The difficulty settings are updated
        FireEditor.chapterController.setDifficulty();
        //The data is compiled
        byte[] data;
        if (decomp) data = FireEditor.chapterFile.getBytes();
        else data = FireEditor.chapterFile.getBytesComp();
        return data;
    }

    public static FXMLLoader getWindow(String name) {
        return new FXMLLoader(FireEditor.class.getResource(Constants.RES_FXML + name));
    }
}
