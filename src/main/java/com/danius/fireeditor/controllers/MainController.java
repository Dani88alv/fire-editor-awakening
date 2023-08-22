package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.ChapterFile;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.units.Unit;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class MainController {

    private String path = System.getProperty("user.dir");

    @FXML
    private Tab tabConvoy, tabChapter, tabUnit;

    public void initialize() {
        if (FireEditor.chapterFile == null) {
            FireEditor.unitController.disableElements(true);
            FireEditor.unitController.comboUnitGroup.setDisable(true);
            tabConvoy.setDisable(true);
            tabChapter.setDisable(true);
        }
    }

    public void openFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(path));

        File file = fileChooser.showOpenDialog(null);
        if (file == null) return;
        path = file.getParent();
        //The blocks are initialized
        try {
            FireEditor.chapterFile = new ChapterFile(Files.readAllBytes(file.toPath()));
            FireEditor.unitController.loadUnitBlock();
            FireEditor.unitController.comboUnitGroup.setDisable(false);
            tabConvoy.setDisable(false);
            tabChapter.setDisable(false);
            FireEditor.convoyController.loadBlocks();
            FireEditor.chapterController.loadBlocks();
            FireEditor.maxClasses = FireEditor.maxClasses();
            FireEditor.maxArmies = FireEditor.maxArmies();
        } catch (Exception e) {
            throw new RuntimeException();
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

        File file = fileChooser.showOpenDialog(null);
        if (file == null) return;
        path = file.getParent();
        //The unit is initialized
        try {
            byte[] data = Files.readAllBytes(file.toPath());
            boolean isWest = FireEditor.unitController.unitBlock.isWest;
            Unit unit = new Unit(data);
            //The unit structure is slightly modified
            //unit.rawSupport.expandBlock(); //VERY IMPORTANT
            if (unit.hasLogBlock) unit.rawLog.changeRegion(isWest); //Converts the unit to the current region
            //The unit is imported to the current list
            ObservableList<Unit> unitList = FireEditor.unitController.listViewUnit.getItems();
            if (unitList.size() < 255) {
                unitList.add(unit);
                FireEditor.unitController.listViewUnit.setItems(unitList);
                FireEditor.unitController.listViewUnit.getSelectionModel().select(unitList.size() - 1);
                FireEditor.unitController.updateUnitFromFields(
                        FireEditor.unitController.listViewUnit.getSelectionModel().getSelectedItem());
                FireEditor.unitController.displayUnitCount();
            }
        } catch (Exception e) {
            throw new RuntimeException("INVALID UNIT FILE!");
        }
    }

    public void exportUnit() {
        //The data is compiled
        FireEditor.unitController.updateUnitFromFields(
                FireEditor.unitController.listViewUnit.getSelectionModel().getSelectedItem());
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

    private byte[] compileBlocks(boolean decomp) {
        //The current unit is updated
        Unit currentUnit = FireEditor.unitController.listViewUnit.getSelectionModel().getSelectedItem();
        FireEditor.unitController.updateUnitFromFields(currentUnit);
        //The unit block is updated
        FireEditor.chapterFile.blockUnit = FireEditor.unitController.unitBlock;
        //The inventory is updated
        FireEditor.chapterFile.blockTran.setItemAmountFromList(FireEditor.convoyController.getItemUses());
        FireEditor.chapterFile.blockTran.inventoryRefi = FireEditor.convoyController.getRefiUses();
        //The user block is updated
        FireEditor.chapterController.compileValues();
        FireEditor.chapterFile.blockUser = FireEditor.chapterController.userBlock;
        FireEditor.chapterFile.blockHeader = FireEditor.chapterController.headerBlock;
        FireEditor.chapterFile.blockGmap = FireEditor.chapterController.gmapBlock;
        FireEditor.chapterFile.blockDu26 = FireEditor.chapterController.du26Block;
        //The data is compiled
        byte[] data;
        if (decomp) data = FireEditor.chapterFile.getBytes();
        else data = FireEditor.chapterFile.getBytesComp();
        return data;
    }
}
