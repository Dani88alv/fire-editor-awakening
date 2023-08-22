package com.danius.fireeditor.controllers;

import com.danius.fireeditor.savefile.units.Supports;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.util.Names;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;

import java.util.List;

public class SupportController {
    private Unit unit;
    @FXML
    ComboBox<String> comboUnit, comboLevel;
    @FXML
    Spinner<Integer> spinValue;
    @FXML
    Label lblLevel;
    @FXML
    Button btnRemoveSupport, btnSetAll;

    public void initialize() {
        UI.setSpinnerNumeric(spinValue, 0x16);
        ObservableList<String> levelOptions = FXCollections.observableArrayList(
                "D-Rank", "C-Pending", "C-Rank", "B-Pending", "B-Rank",
                "A-Pending", "A-Rank", "S-Pending", "S-Rank");
        comboLevel.setItems(levelOptions);
        comboLevel.getSelectionModel().select(0);
        setupComboUnit();
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
        if (unit.rawSupport.supportCount() > 0) {
            setFields();
            setupSpinners(spinValue);
            setLabelLevel();
        } else disableElements();
    }

    public void setFields() {
        if (unit != null) {
            //Unit Selection
            ObservableList<String> unitNames = FXCollections.observableArrayList();
            int totalCount = unit.rawSupport.supportCount();
            int[] characters = Supports.getSupportUnits(unit.rawBlock1.unitId());
            for (int character : characters) {
                unitNames.add(Names.unitName(character - 1));
            }
            //If there are modded supports, they are added
            for (int i = characters.length; i < totalCount; i++) {
                unitNames.add("Extra Support #" + (i - characters.length + 1));
            }
            comboUnit.setItems(unitNames);
            comboUnit.getSelectionModel().select(0);
        }
    }

    private void setupComboUnit() {
        comboUnit.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null) {
                int selectedUnit = comboUnit.getSelectionModel().getSelectedIndex();
                spinValue.getValueFactory().setValue(unit.rawSupport.supportValue(selectedUnit));
                setLabelLevel();
            }
        });
    }

    private void setupSpinners(Spinner<Integer> spinner) {
        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                int value = spinValue.getValue();
                int slot = comboUnit.getSelectionModel().getSelectedIndex();
                unit.rawSupport.setSupportValue(slot, value);
                setLabelLevel();
            }
        });
    }

    @FXML
    private void setUnitsToLevel() {
        int level = comboLevel.getSelectionModel().getSelectedIndex();
        unit.rawSupport.setAllSupportsTo(level);
        int selectedUnit = comboUnit.getSelectionModel().getSelectedIndex();
        spinValue.getValueFactory().setValue(unit.rawSupport.supportValue(selectedUnit));
        setLabelLevel();
    }

    @FXML
    private void removeExtraSupports() {
        unit.rawSupport.removeExtraSupports();
        int currentSupports = comboUnit.getItems().size();
        int slotsToRemove = currentSupports - unit.rawSupport.supportCount();
        for (int i = 0; i < slotsToRemove; i++) {
            if (comboUnit.getItems().size() != 0) {
                int aa = comboUnit.getItems().size();
                comboUnit.getSelectionModel().selectFirst();
            }
            comboUnit.getItems().remove(comboUnit.getItems().size() - 1);
        }
        if (comboUnit.getItems().size() == 0) {
            disableElements();
        } else comboUnit.getSelectionModel().select(0);
    }

    private void disableElements() {
        comboUnit.setDisable(true);
        spinValue.setDisable(true);
        btnRemoveSupport.setDisable(true);
        btnSetAll.setDisable(true);
        comboLevel.setDisable(true);
    }

    private void setLabelLevel() {
        int value = spinValue.getValue();
        int slot = comboUnit.getSelectionModel().getSelectedIndex();
        lblLevel.setText(Names.supportLevel(unit.rawBlock1.unitId(), value, slot));
    }

}
