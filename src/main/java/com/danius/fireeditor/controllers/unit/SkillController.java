package com.danius.fireeditor.controllers.unit;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.data.ClassDb;
import com.danius.fireeditor.data.SkillDb;
import com.danius.fireeditor.data.UnitDb;
import com.danius.fireeditor.data.model.ClassModel;
import com.danius.fireeditor.data.model.SkillModel;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.data.MiscDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.util.List;

import static com.danius.fireeditor.data.SkillDb.*;

public class SkillController {

    @FXML
    private AnchorPane checkboxAnchorPane;
    @FXML
    private ComboBox<String> comboSkill1, comboSkill2, comboSkill3, comboSkill4, comboSkill5;
    @FXML
    private Label lblCount;
    private Unit unit;
    private static final int NUM_COLUMNS = 5;
    private static final int TOTAL_CHECKBOXES = 104;
    private static final int NUM_FULL_ROWS = TOTAL_CHECKBOXES / NUM_COLUMNS;
    private static final int NUM_LAST_ROW_CHECKBOXES = TOTAL_CHECKBOXES % NUM_COLUMNS;
    private final CheckBox[] checkboxes = new CheckBox[TOTAL_CHECKBOXES];

    public void initialize() {
        generateCheckboxes();
        generateComboboxes();
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
        for (int i = 0; i < checkboxes.length; i++) {
            checkboxes[i].setSelected(unit.rawSkill.isSkillSet(i));
        }
        int[] activeSkills = unit.rawBlock2.getCurrentSkills();
        comboSkill1.getSelectionModel().select(activeSkills[0]);
        comboSkill2.getSelectionModel().select(activeSkills[1]);
        comboSkill3.getSelectionModel().select(activeSkills[2]);
        comboSkill4.getSelectionModel().select(activeSkills[3]);
        comboSkill5.getSelectionModel().select(activeSkills[4]);
    }

    public void setAllLegal() {
        unit.setLegalSkills();
        String rawString = unit.rawSkill.skillString;
        //All the skills are unchecked
        for (CheckBox checkbox : checkboxes) checkbox.setSelected(false);
        //Only the legal skills are checked back
        for (int i = 0; i < checkboxes.length; i++) {
            if (rawString.charAt(i) == '1') checkboxes[i].setSelected(true);
        }
        unselectActive();
    }

    public void setAll() {
        for (int i = 0; i < checkboxes.length; i++) {
            if (i != 0 && i != checkboxes.length - 1) checkboxes[i].setSelected(true);
        }
    }

    public void unsetAll() {
        for (CheckBox checkbox : checkboxes) checkbox.setSelected(false);
        unselectActive();
    }

    public void unselectActive() {
        List<Integer> skills = unit.rawSkill.getLearnedSkills();
        if (!skills.contains(comboSkill1.getSelectionModel().getSelectedIndex()))
            comboSkill1.getSelectionModel().select(0);
        if (!skills.contains(comboSkill2.getSelectionModel().getSelectedIndex()))
            comboSkill2.getSelectionModel().select(0);
        if (!skills.contains(comboSkill3.getSelectionModel().getSelectedIndex()))
            comboSkill3.getSelectionModel().select(0);
        if (!skills.contains(comboSkill4.getSelectionModel().getSelectedIndex()))
            comboSkill4.getSelectionModel().select(0);
        if (!skills.contains(comboSkill5.getSelectionModel().getSelectedIndex()))
            comboSkill5.getSelectionModel().select(0);
    }

    private void generateComboboxes() {
        ObservableList<String> skills = FXCollections.observableArrayList(SkillDb.getSkillNames());
        comboSkill1.setItems(skills);
        comboSkill2.setItems(skills);
        comboSkill3.setItems(skills);
        comboSkill4.setItems(skills);
        comboSkill5.setItems(skills);
        addComboListeners(comboSkill1, 0);
        addComboListeners(comboSkill2, 1);
        addComboListeners(comboSkill3, 2);
        addComboListeners(comboSkill4, 3);
        addComboListeners(comboSkill5, 4);
    }

    private void addComboListeners(ComboBox<String> comboBox, int slot) {
        comboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (unit != null) {
                int selectedIndex = newValue.intValue();
                unit.rawBlock2.setCurrentSkill(selectedIndex, slot);
                FireEditor.unitController.setFieldsStats(unit);
            }
        });
    }

    private void generateCheckboxes() {
        // Calculate the width and height of each checkbox based on the AnchorPane size
        // Calculate the width and height of each checkbox based on the AnchorPane size
        double checkboxWidth = checkboxAnchorPane.getPrefWidth() / NUM_COLUMNS;
        double checkboxHeight = checkboxAnchorPane.getPrefHeight() / NUM_FULL_ROWS;

        int checkboxCount = 0;

        for (int row = 0; row < NUM_FULL_ROWS; row++) {
            for (int col = 0; col < NUM_COLUMNS; col++) {
                CheckBox checkBox = createCheckbox(checkboxCount, col * checkboxWidth, row * checkboxHeight);
                checkboxes[checkboxCount] = checkBox;
                checkboxAnchorPane.getChildren().add(checkBox);
                checkboxCount++;
            }
        }

        // Add the checkboxes for the last row
        double lastRowY = NUM_FULL_ROWS * checkboxHeight;
        for (int col = 0; col < NUM_LAST_ROW_CHECKBOXES; col++) {
            CheckBox checkBox = createCheckbox(checkboxCount, col * checkboxWidth, lastRowY);
            checkboxes[checkboxCount] = checkBox;
            checkboxAnchorPane.getChildren().add(checkBox);
            checkboxCount++;
        }
    }

    private CheckBox createCheckbox(int checkboxCount, double x, double y) {
        CheckBox checkBox = new CheckBox();
        checkBox.setLayoutX(x);
        checkBox.setLayoutY(y);
        checkBox.setText(SkillDb.getSkillNames().get(checkboxCount)); // Set checkbox text
        addCheckboxListener(checkBox, checkboxCount);
        return checkBox;
    }

    // Method to add a listener to a checkbox
    public void addCheckboxListener(CheckBox checkbox, int slot) {
        checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (unit != null) {
                unit.rawSkill.setLearnedSkill(newValue, slot);
                setCount();
            }
        });
    }

    public void setCount() {
        lblCount.setText("Learned Skills: " + unit.rawSkill.skillCount());
    }

    @FXML
    private void skillReport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setPrefWidth(650);
        int itemsPerRow = 8;
        alert.setTitle("Legal Skills");
        alert.setHeaderText("Available skills for " + unit.unitName() + ":");

        List<SkillModel> baseSkills = getSkillsFromBaseClasses(unit);
        StringBuilder contentText = new StringBuilder("Base Skills:\n");
        for (int i = 0; i < baseSkills.size(); i++) {
            contentText.append(baseSkills.get(i).getName());
            if (i % itemsPerRow == (itemsPerRow - 1) || i == baseSkills.size() - 1) contentText.append("\n");
            else contentText.append(", ");
        }
        contentText.append("\n");

        List<SkillModel> personalSkills = getPersonalSkills(unit);
        contentText.append("Personal Skills:\n");
        for (int i = 0; i < personalSkills.size(); i++) {
            contentText.append(personalSkills.get(i).getName());
            if (i % itemsPerRow == (itemsPerRow - 1) || i == personalSkills.size() - 1) contentText.append("\n");
            else contentText.append(", ");
        }
        contentText.append("\n");

        List<SkillModel> itemSkills = getItemSkills();
        contentText.append("Item Skills:\n");
        for (int i = 0; i < itemSkills.size(); i++) {
            contentText.append(itemSkills.get(i).getName());
            if (i % itemsPerRow == (itemsPerRow - 1) || i == itemSkills.size() - 1) contentText.append("\n");
            else contentText.append(", ");
        }
        contentText.append("\n");

        if (unit.rawChild != null) {

            List<SkillModel> inheritClassSkills = getExclusiveInheritClassSkills(unit);
            contentText.append("Exclusive skills from Inherited Classes:\n");
            for (int i = 0; i < inheritClassSkills.size(); i++) {
                contentText.append(inheritClassSkills.get(i).getName());
                if (i % itemsPerRow == (itemsPerRow - 1) || i == inheritClassSkills.size() - 1)
                    contentText.append("\n");
                else contentText.append(", ");
            }
            contentText.append("\n");

            int father = unit.rawChild.parentId(0);
            if (father != 0xFFFF) {
                List<SkillModel> inheritExclusiveFather = getExclusiveInheritSlotSkills(unit, 0);
                contentText.append("Exclusive skills from ")
                        .append(UnitDb.getUnitName(father)).append(":\n");
                for (int i = 0; i < inheritExclusiveFather.size(); i++) {
                    contentText.append(inheritExclusiveFather.get(i).getName());
                    if (i % itemsPerRow == (itemsPerRow - 1) || i == inheritExclusiveFather.size() - 1)
                        contentText.append("\n");
                    else contentText.append(", ");
                }
                contentText.append("\n");
            }

            int mother = unit.rawChild.parentId(1);
            if (mother != 0xFFFF) {
                List<SkillModel> inheritExclusiveMother = getExclusiveInheritSlotSkills(unit, 1);
                contentText.append("Exclusive skills from ")
                        .append(UnitDb.getUnitName(mother)).append(":\n");
                for (int i = 0; i < inheritExclusiveMother.size(); i++) {
                    contentText.append(inheritExclusiveMother.get(i).getName());
                    if (i % itemsPerRow == (itemsPerRow - 1) || i == inheritExclusiveMother.size() - 1)
                        contentText.append("\n");
                    else contentText.append(", ");
                }
            }

        }

        alert.setContentText(contentText.toString());
        alert.showAndWait();
    }
}
