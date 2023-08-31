package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.Chapter13;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

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

    public void setSupports() {
        Chapter13 chapterFile = new Chapter13(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        int level = comboSupport.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawSupport.expandBlock();
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawSupport.setAllSupportsTo(level);
            if (chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawChild != null) {
                chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawChild.setAllSupportsToLevel(level);
            }
            if (level == 0) chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawSupport.removeExtraSupports();
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setSkillsAll() {
        Chapter13 chapterFile = new Chapter13(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawSkill.setAll(true);
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setSkillsLegal() {
        Chapter13 chapterFile = new Chapter13(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).setLegalSkills();
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setSkillsReset() {
        Chapter13 chapterFile = new Chapter13(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawSkill.setAll(false);
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawBlock2.resetCurrentSkills();
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setMaxBattles() {
        Chapter13 chapterFile = new Chapter13(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawBlockEnd.setBattles(9999);
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawBlockEnd.setVictories(9999);
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setMoveTwo() {
        Chapter13 chapterFile = new Chapter13(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawBlock1.setMovement(2);
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setMoveHundred() {
        Chapter13 chapterFile = new Chapter13(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawBlock1.setMovement(100);
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setMaxStats() {
        Chapter13 chapterFile = new Chapter13(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).maxStats();
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setBuffs() {
        Chapter13 chapterFile = new Chapter13(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawFlags.setAllTonicFlags();
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawFlags.setAllOtherBuffs();
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    //Not even Grima could predict this
    public void pickAGodAndPray() {
        Chapter13 chapterFile = new Chapter13(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        int level = comboSupport.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            //Alright, let's do this
            //SUPPORTS
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawSupport.expandBlock();
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawSupport.setAllSupportsTo(level);
            if (chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawChild != null) {
                chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawChild.setAllSupportsToLevel(level);
            }
            if (level == 0) chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawSupport.removeExtraSupports();
            //ALL SKILLS
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawSkill.setAll(true);
            //Zzzz
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawBlockEnd.setBattles(9999);
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawBlockEnd.setVictories(9999);
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawBlock1.setMovement(2);
            //STATS
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).maxStats();
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawFlags.setAllTonicFlags();
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawFlags.setAllOtherBuffs();
            //ITEMS
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawInventory.maxAmount();
            ;
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void maxConvoy() {
        Chapter13 chapterFile = new Chapter13(FireEditor.mainController.compileBlocks(true));
        //Convoy
        chapterFile.blockTran.maxItemAmount(900, 900);
        chapterFile.blockTran.maxForgedAmounts(FireEditor.chapterFile.blockRefi.refiList);
        //Equipped Items
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawInventory.maxAmount();
            ;
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void unlockMaps() {
        FireEditor.chapterController.comboChapterData.getSelectionModel().select(2);
        Chapter13 chapterFile = new Chapter13(FireEditor.mainController.compileBlocks(true));
        for (int i = 0; i < chapterFile.blockGmap.maps.size(); i++) {
            chapterFile.blockGmap.maps.get(i).setLockState(2);
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }


}
