package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.Batch;
import com.danius.fireeditor.savefile.Chapter13;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

public class CheatController {

    @FXML
    private ComboBox<String> comboUnitGroup, comboSupport, comboMap;
    @FXML
    private Spinner<Integer> spinItemUses, spinItemAmount;

    public void initialize() {
        FireEditor.cheatController = this;
        setupElements();
    }

    public void setupElements() {
        UI.setSpinnerNumeric(spinItemAmount, 999);
        UI.setSpinnerNumeric(spinItemUses, 65335);

        ObservableList<String> groups = FXCollections.observableArrayList(
                "Blue Units", "Red Units", "Green Units", "Main Units", "Dead Units", "Other Units");
        comboUnitGroup.setItems(groups);
        comboUnitGroup.getSelectionModel().select(3);

        ObservableList<String> levelOptions = FXCollections.observableArrayList(
                "D-Rank", "C-Pending", "C-Rank", "B-Pending", "B-Rank",
                "A-Pending", "A-Rank", "S-Pending", "S-Rank");
        comboSupport.setItems(levelOptions);
        comboSupport.getSelectionModel().select(7);

        ObservableList<String> encounters = FXCollections.observableArrayList("None", "Risen", "Merchant", "Random");
        comboMap.setItems(encounters);
        comboMap.getSelectionModel().select(3);
    }

    public void reload() {
        FireEditor.mainController.reloadTabs(FireEditor.chapterFile.getBytes());
    }

    public void setSupports() {
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        int level = comboSupport.getSelectionModel().getSelectedIndex();
        Batch.setSupports(FireEditor.chapterFile, unitSlot, level);
        reload();
    }

    public void setSkillsAll() {
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        Batch.setSkillsAll(FireEditor.chapterFile, unitSlot, true);
        reload();
    }

    public void setSkillsLegal() {
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        Batch.setSkillsLegal(FireEditor.chapterFile, unitSlot);
        reload();
    }

    public void setSkillsReset() {
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        Batch.setSkillsAll(FireEditor.chapterFile, unitSlot, false);
        reload();
    }

    public void setMoveTwo() {
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        Batch.setMovement(FireEditor.chapterFile, unitSlot, 2);
        reload();
    }

    public void setMaxStats() {
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        Batch.setMaxStats(FireEditor.chapterFile, unitSlot);
        reload();
    }

    public void setBuffs() {
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        Batch.setTemporalBuffs(FireEditor.chapterFile, unitSlot, true);
        reload();
    }

    public void pickGodAndPray() {
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        int level = comboSupport.getSelectionModel().getSelectedIndex();
        Batch.setSupports(FireEditor.chapterFile, unitSlot, level);
        Batch.setSkillsLegal(FireEditor.chapterFile, unitSlot);
        Batch.setMaxStats(FireEditor.chapterFile, unitSlot);
        Batch.setMovement(FireEditor.chapterFile, unitSlot, 2);
        Batch.setTemporalBuffs(FireEditor.chapterFile, unitSlot, true);
        reload();
    }

    public void setConvoyUses() {
        int value = spinItemUses.getValue();
        Batch.setConvoyUsesTo(FireEditor.chapterFile, value);
        reload();
    }

    public void setConvoyAmount() {
        int value = spinItemAmount.getValue();
        Batch.setConvoyAmountTo(FireEditor.chapterFile, value);
        reload();
    }

    public void setEncounters() {
        int type = comboMap.getSelectionModel().getSelectedIndex();
        FireEditor.chapterFile.blockGmap.randomizeMaps(type);
        reload();
    }


    public void addSpotPass() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("SpotPass Data");
        alert.setHeaderText("This will reset all the SpotPass content. Proceed? \n" +
                "WARNING: This feature has not been fully tested!");
        alert.setContentText("If you already have SpotPass content downloaded, nothing will be modified.");
        // Add Confirm and Cancel buttons
        ButtonType confirmButton = new ButtonType("Confirm");
        ButtonType cancelButton = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        // Show the dialog and wait for a response
        alert.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                FireEditor.chapterFile.blockDu26.addSpotpass();
                reload();
            } else if (response == cancelButton) {
                return;
            }
        });
    }

    public void changeRegion() {
        Chapter13 chapter13 = FireEditor.chapterFile;
        boolean isWest = chapter13.isWest;
        String originalRegion = (isWest) ? "US/Europe" : "Japan";
        String targetRegion = (isWest) ? "Japan" : "US/Europe";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Change Region");
        alert.setHeaderText("Current region: " + originalRegion + "\n" +
                "The save file will be changed to " + targetRegion);
        alert.setContentText("Remember to save in-game after the conversion to completely fix the save file!");
        // Add Confirm and Cancel buttons
        ButtonType confirmButton = new ButtonType("Confirm");
        ButtonType cancelButton = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        // Show the dialog and wait for a response
        alert.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                FireEditor.chapterFile.changeRegion(!isWest);
                reload();
            } else if (response == cancelButton) {
                return;
            }
        });

    }


}
