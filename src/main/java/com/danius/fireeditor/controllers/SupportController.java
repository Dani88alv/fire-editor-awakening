package com.danius.fireeditor.controllers;

import com.danius.fireeditor.savefile.units.Supports;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.util.Names;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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

    public void initialize() {
        UI.setSpinnerNumeric(spinValue, 0x16);
        ObservableList<String> levelOptions = FXCollections.observableArrayList(
                "C-Pending", "B-Pending", "A-Pending", "S-Pending");
        comboLevel.setItems(levelOptions);
        comboLevel.getSelectionModel().select(0);
        setupComboUnit();
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
        setFields();
        setupSpinners(spinValue);
        setLabelLevel();
    }

    public void setFields() {
        if (unit != null) {
            //Unit Selection
            ObservableList<String> unitNames = FXCollections.observableArrayList();
            int[] characters = Supports.getSupportUnits(unit.rawBlock1.unitId());
            for (int i = 0; i < characters.length; i++) {
                unitNames.add(Names.unitName(characters[i] - 1));
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
    private void setUnitsToLevel(){
        int level = comboLevel.getSelectionModel().getSelectedIndex();
        unit.rawSupport.setAllSupportsTo(level);
        int selectedUnit = comboUnit.getSelectionModel().getSelectedIndex();
        spinValue.getValueFactory().setValue(unit.rawSupport.supportValue(selectedUnit));
        setLabelLevel();
    }

    private void setLabelLevel(){
        int value = spinValue.getValue();
        int slot = comboUnit.getSelectionModel().getSelectedIndex();
        lblLevel.setText(Names.supportLevel(unit.rawBlock1.unitId(), value, slot));
    }

}
