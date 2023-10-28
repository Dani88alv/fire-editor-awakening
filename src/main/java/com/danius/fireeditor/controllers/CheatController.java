package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.Batch;
import com.danius.fireeditor.savefile.Chapter13;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.List;

public class CheatController {

    @FXML
    private ComboBox<String> comboUnitGroup, comboSupport;

    public void initialize() {
        FireEditor.cheatController = this;
        setupElements();
    }

    public void setupElements() {
        ObservableList<String> groups = FXCollections.observableArrayList(
                "Blue Units", "Red Units", "Green Units", "Main Units", "Dead Units", "Other Units");
        comboUnitGroup.setItems(groups);
        ObservableList<String> levelOptions = FXCollections.observableArrayList(
                "D-Rank", "C-Pending", "C-Rank", "B-Pending", "B-Rank",
                "A-Pending", "A-Rank", "S-Pending", "S-Rank");
        comboSupport.setItems(levelOptions);
        comboUnitGroup.getSelectionModel().select(3);
        comboSupport.getSelectionModel().select(7);
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

    public void setMaxBattles() {
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        Batch.setBattlesVictories(FireEditor.chapterFile, unitSlot, 9999);
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

    //Not even Grima could predict this
    public void pickAGodAndPray() {

    }

    public void maxConvoy() {

    }

    public void unlockMaps() {
        Chapter13 chapterFile = new Chapter13(FireEditor.mainController.chapterBytes(true));
        for (int i = 0; i < chapterFile.blockGmap.maps.size(); i++) {
            chapterFile.blockGmap.maps.get(i).setLockState(2);
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }


}
