package com.danius.fireeditor.controllers.fe13;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.ChapterFile;
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
        ChapterFile chapterFile = new ChapterFile(FireEditor.mainController.compileBlocks(true));
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
        ChapterFile chapterFile = new ChapterFile(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawSkill.setAll(true);
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setSkillsLegal() {
        ChapterFile chapterFile = new ChapterFile(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).setLegalSkills();
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setSkillsReset() {
        ChapterFile chapterFile = new ChapterFile(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawSkill.setAll(false);
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawBlock2.resetCurrentSkills();
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setMaxBattles() {
        ChapterFile chapterFile = new ChapterFile(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawBlockEnd.setBattles(9999);
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawBlockEnd.setVictories(9999);
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setMoveTwo() {
        ChapterFile chapterFile = new ChapterFile(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawBlock1.setMovement(2);
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setMoveHundred() {
        ChapterFile chapterFile = new ChapterFile(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawBlock1.setMovement(100);
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setMaxStats() {
        ChapterFile chapterFile = new ChapterFile(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).maxStats();
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    public void setBuffs() {
        ChapterFile chapterFile = new ChapterFile(FireEditor.mainController.compileBlocks(true));
        int unitSlot = comboUnitGroup.getSelectionModel().getSelectedIndex();
        for (int i = 0; i < chapterFile.blockUnit.unitList.get(unitSlot).size(); i++) {
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawFlags.setAllTonicFlags();
            chapterFile.blockUnit.unitList.get(unitSlot).get(i).rawFlags.setAllOtherBuffs();
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }

    //Not even Grima could predict this
    public void pickAGodAndPray() {
        ChapterFile chapterFile = new ChapterFile(FireEditor.mainController.compileBlocks(true));
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
        ChapterFile chapterFile = new ChapterFile(FireEditor.mainController.compileBlocks(true));
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
        ChapterFile chapterFile = new ChapterFile(FireEditor.mainController.compileBlocks(true));
        for (int i = 0; i < chapterFile.blockGmap.maps.size(); i++) {
            chapterFile.blockGmap.maps.get(i).setLockState(2);
        }
        FireEditor.mainController.reloadTabs(chapterFile.getBytes());
    }


}
