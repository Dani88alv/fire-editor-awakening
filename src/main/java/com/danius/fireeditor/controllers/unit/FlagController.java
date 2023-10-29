package com.danius.fireeditor.controllers.unit;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.data.MiscDb;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;

public class FlagController {

    @FXML
    private AnchorPane paneTrait, paneBattle, paneBuff, paneTonic, paneTonicUnused;
    private final CheckBox[] traitChecks = new CheckBox[32];
    private final CheckBox[] battleChecks = new CheckBox[32];
    private final CheckBox[] skillChecks = new CheckBox[11];
    private final CheckBox[] tonicChecks = new CheckBox[8];
    private final CheckBox[] barrackChecks = new CheckBox[8];
    private Unit unit;

    public void initialize() {
        generateTraitChecks();
        generateBattleChecks();
        generateSkillChecks();
        generateBarrackChecks();
        generateTonicChecks();
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
        for (int i = 0; i < traitChecks.length; i++) {
            traitChecks[i].setSelected(unit.rawFlags.hasTraitFlag(i));
        }
        for (int i = 0; i < battleChecks.length; i++) {
            battleChecks[i].setSelected(unit.rawFlags.hasBattleFlag(i));
        }
        for (int i = 0; i < tonicChecks.length; i++) {
            tonicChecks[i].setSelected(unit.rawFlags.hasTonicFlag(i));
        }
        for (int i = 0; i < barrackChecks.length; i++) {
            barrackChecks[i].setSelected(unit.rawFlags.hasBarrackFlag(i));
        }
        for (int i = 0; i < 11; i++) {
            skillChecks[i].setSelected(unit.rawFlags.hasSkillFlag(i));
        }
    }

    public void setAllBuffs() {
        for (CheckBox tonicCheck : tonicChecks) tonicCheck.setSelected(true);
        for (CheckBox buffCheck : skillChecks) buffCheck.setSelected(true);
        for (CheckBox tonicUnusedCheck : barrackChecks) tonicUnusedCheck.setSelected(true);
        FireEditor.unitController.setFieldsStats(unit);
    }

    public void setAllTonics() {
        for (CheckBox tonicCheck : tonicChecks) tonicCheck.setSelected(true);
        FireEditor.unitController.setFieldsStats(unit);
    }

    public void unsetAllBuffs() {
        for (CheckBox tonicCheck : tonicChecks) tonicCheck.setSelected(false);
        for (CheckBox buffCheck : skillChecks) buffCheck.setSelected(false);
        for (CheckBox tonicUnusedCheck : barrackChecks) tonicUnusedCheck.setSelected(false);
        FireEditor.unitController.setFieldsStats(unit);
    }

    private void generateTonicChecks() {
        int NUM_COLUMNS = 4;
        int NUM_FULL_ROWS = 2;
        // Calculate the width and height of each checkbox based on the AnchorPane size
        // Calculate the width and height of each checkbox based on the AnchorPane size
        double checkboxWidth = paneTonic.getPrefWidth() / NUM_COLUMNS;
        double checkboxHeight = paneTonic.getPrefHeight() / NUM_FULL_ROWS;

        int checkboxCount = 0;

        for (int row = 0; row < NUM_FULL_ROWS; row++) {
            for (int col = 0; col < NUM_COLUMNS; col++) {
                CheckBox checkBox = createTonicCheck(checkboxCount, col * checkboxWidth, row * checkboxHeight);
                tonicChecks[checkboxCount] = checkBox;
                paneTonic.getChildren().add(checkBox);
                checkboxCount++;
            }
        }
    }

    private CheckBox createTonicCheck(int checkboxCount, double x, double y) {
        CheckBox checkBox = new CheckBox();
        checkBox.setLayoutX(x);
        checkBox.setLayoutY(y);
        checkBox.setText(MiscDb.buffTonics.get(checkboxCount)); // Set checkbox text
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            unit.rawFlags.setTonicFlag(checkboxCount, checkBox.isSelected());
            FireEditor.unitController.setFieldsStats(unit);
        });
        return checkBox;
    }

    private void generateSkillChecks() {
        int NUM_COLUMNS = 1;
        int NUM_FULL_ROWS = 11;
        // Calculate the width and height of each checkbox based on the AnchorPane size
        // Calculate the width and height of each checkbox based on the AnchorPane size
        double checkboxWidth = paneBuff.getPrefWidth() / NUM_COLUMNS;
        double checkboxHeight = paneBuff.getPrefHeight() / NUM_FULL_ROWS;

        int checkboxCount = 0;

        for (int row = 0; row < NUM_FULL_ROWS; row++) {
            for (int col = 0; col < NUM_COLUMNS; col++) {
                CheckBox checkBox = createSkillCheck(checkboxCount, col * checkboxWidth, row * checkboxHeight);
                skillChecks[checkboxCount] = checkBox;
                paneBuff.getChildren().add(checkBox);
                checkboxCount++;
            }
        }
    }

    private CheckBox createSkillCheck(int checkboxCount, double x, double y) {
        CheckBox checkBox = new CheckBox();
        checkBox.setLayoutX(x);
        checkBox.setLayoutY(y);
        checkBox.setText(MiscDb.buffSkills.get(checkboxCount)); // Set checkbox text
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            unit.rawFlags.setSkillBuffFlag(checkboxCount, checkBox.isSelected());
            FireEditor.unitController.setFieldsStats(unit);
        });
        return checkBox;
    }

    private void generateBarrackChecks() {
        int NUM_COLUMNS = 1;
        int NUM_FULL_ROWS = 8;
        // Calculate the width and height of each checkbox based on the AnchorPane size
        // Calculate the width and height of each checkbox based on the AnchorPane size
        double checkboxWidth = paneTonicUnused.getPrefWidth() / NUM_COLUMNS;
        double checkboxHeight = paneTonicUnused.getPrefHeight() / NUM_FULL_ROWS;

        int checkboxCount = 0;

        for (int row = 0; row < NUM_FULL_ROWS; row++) {
            for (int col = 0; col < NUM_COLUMNS; col++) {
                CheckBox checkBox = createBarrackCheck(checkboxCount, col * checkboxWidth, row * checkboxHeight);
                barrackChecks[checkboxCount] = checkBox;
                paneTonicUnused.getChildren().add(checkBox);
                checkboxCount++;
            }
        }
    }

    private CheckBox createBarrackCheck(int checkboxCount, double x, double y) {
        CheckBox checkBox = new CheckBox();
        checkBox.setLayoutX(x);
        checkBox.setLayoutY(y);
        checkBox.setText(MiscDb.buffBarracks.get(checkboxCount)); // Set checkbox text
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            unit.rawFlags.setBarrackFlag(checkboxCount, checkBox.isSelected());
            FireEditor.unitController.setFieldsStats(unit);
        });
        return checkBox;
    }

    private void generateBattleChecks() {
        int NUM_COLUMNS = 4;
        int NUM_FULL_ROWS = 8;
        // Calculate the width and height of each checkbox based on the AnchorPane size
        // Calculate the width and height of each checkbox based on the AnchorPane size
        double checkboxWidth = paneBattle.getPrefWidth() / NUM_COLUMNS;
        double checkboxHeight = paneBattle.getPrefHeight() / NUM_FULL_ROWS;

        int checkboxCount = 0;

        for (int row = 0; row < NUM_FULL_ROWS; row++) {
            for (int col = 0; col < NUM_COLUMNS; col++) {
                CheckBox checkBox = createBattleCheck(checkboxCount, col * checkboxWidth, row * checkboxHeight);
                battleChecks[checkboxCount] = checkBox;
                paneBattle.getChildren().add(checkBox);
                checkboxCount++;
            }
        }
    }

    private void generateTraitChecks() {
        int NUM_COLUMNS = 4;
        int NUM_FULL_ROWS = 8;
        // Calculate the width and height of each checkbox based on the AnchorPane size
        // Calculate the width and height of each checkbox based on the AnchorPane size
        double checkboxWidth = paneTrait.getPrefWidth() / NUM_COLUMNS;
        double checkboxHeight = paneTrait.getPrefHeight() / NUM_FULL_ROWS;

        int checkboxCount = 0;

        for (int row = 0; row < NUM_FULL_ROWS; row++) {
            for (int col = 0; col < NUM_COLUMNS; col++) {
                CheckBox checkBox = createTraitCheck(checkboxCount, col * checkboxWidth, row * checkboxHeight);
                traitChecks[checkboxCount] = checkBox;
                paneTrait.getChildren().add(checkBox);
                checkboxCount++;
            }
        }
    }

    private CheckBox createBattleCheck(int checkboxCount, double x, double y) {
        CheckBox checkBox = new CheckBox();
        checkBox.setLayoutX(x);
        checkBox.setLayoutY(y);
        checkBox.setText(MiscDb.battleFlags.get(checkboxCount)); // Set checkbox text
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            unit.rawFlags.setBattleFlag(checkboxCount, checkBox.isSelected());
        });
        if (checkboxCount == 27){
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                FireEditor.unitController.refreshName(unit);
                FireEditor.unitController.setImage();
            });
        }
        return checkBox;
    }

    /*

     */

    private CheckBox createTraitCheck(int checkboxCount, double x, double y) {
        CheckBox checkBox = new CheckBox();
        checkBox.setLayoutX(x);
        checkBox.setLayoutY(y);
        checkBox.setText(MiscDb.traitFlags.get(checkboxCount)); // Set checkbox text
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            unit.rawFlags.setTraitFlag(checkboxCount, checkBox.isSelected());
        });
        return checkBox;
    }
}
