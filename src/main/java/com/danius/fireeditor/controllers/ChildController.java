package com.danius.fireeditor.controllers;

import com.danius.fireeditor.savefile.units.Supports;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.util.Names;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class ChildController {
    private Unit unit;
    private boolean listenersAdded = false;
    @FXML
    ComboBox<String> comboSlot, comboUnit, comboAsset, comboFlaw;
    @FXML
    Spinner<Integer> spinFather, spinMother, spinSibling;
    @FXML
    TextField txtModif;
    @FXML
    Label lblFather, lblMother, lblSibling;

    public void initialize() {
        UI.setSpinnerNumeric(spinFather, 0x10);
        UI.setSpinnerNumeric(spinMother, 0x10);
        UI.setSpinnerNumeric(spinSibling, 0x10);
        ObservableList<String> parentSlots = FXCollections.observableArrayList(
                "Father", "Mother", "Paternal Grandfather", "Paternal Grandmother",
                "Maternal Grandfather", "Maternal Grandmother");
        comboSlot.setItems(parentSlots);
        //Unit names
        ObservableList<String> unitNames = FXCollections.observableArrayList();
        unitNames.add("None");
        for (int i = 0; i <= 56; i++) {
            unitNames.add(Names.unitName(i));
        }
        comboUnit.setItems(unitNames);
        //Assets and flaws
        ObservableList<String> modifiers = FXCollections.observableArrayList();
        modifiers.addAll(Names.modifNames);
        comboAsset.setItems(modifiers);
        comboFlaw.setItems(modifiers);
        //The fields are disabled
        disableFields(true);
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
        displayModifiers();
        if (this.unit.hasChildBlock) {
            disableFields(false);
            //Supports
            setSupportValues();
            addListeners();
        }
    }

    public void addListeners() {
        setupComboSlot();
        setupSpinners(spinFather);
        setupSpinners(spinMother);
        setupSpinners(spinSibling);
        setupComboUnit();
        setupComboAsset();
        setupComboFlaw();
        comboSlot.getSelectionModel().select(0);
        listenersAdded = true;
    }

    public void addChildData() {
        if (!unit.hasChildBlock) {
            this.unit.addBlockChild();
            disableFields(false);
            if (!listenersAdded) addListeners();
            //Supports
            setSupportValues();
        }
    }

    public void setSupportValues(){
        spinFather.getValueFactory().setValue(unit.rawChild.supportParentValue(true));
        spinMother.getValueFactory().setValue(unit.rawChild.supportParentValue(false));
        spinSibling.getValueFactory().setValue(unit.rawChild.supportSiblingValue());
        setSupportLabels();
    }

    public void removeChildData() {
        if (unit.hasChildBlock) {
            this.unit.removeBlockExtra();
            disableFields(true);
            displayModifiers();
        }
    }

    private void disableFields(boolean enable) {
        comboSlot.setDisable(enable);
        comboUnit.setDisable(enable);
        comboFlaw.setDisable(enable);
        comboAsset.setDisable(enable);
        spinFather.setDisable(enable);
        spinFather.setDisable(enable);
        spinMother.setDisable(enable);
        spinSibling.setDisable(enable);
    }

    /*
    Sets up the modifiers of the combobox
     */
    private void setupComboUnit() {
        comboUnit.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null && unit.hasChildBlock) {
                int slot = comboSlot.getSelectionModel().getSelectedIndex();
                int unitId = comboUnit.getSelectionModel().getSelectedIndex();
                if (unitId == 0) unitId = 65535;
                else unitId--;
                unit.rawChild.setParentId(slot, unitId);
                displayModifiers();
            }
        });
    }

    private void setupComboAsset() {
        comboAsset.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null && unit.hasChildBlock) {
                int slot = comboSlot.getSelectionModel().getSelectedIndex();
                unit.rawChild.setAsset(slot, comboAsset.getSelectionModel().getSelectedIndex());
                displayModifiers();
            }
        });
    }

    private void setupComboFlaw() {
        comboFlaw.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null && unit.hasChildBlock) {
                int slot = comboSlot.getSelectionModel().getSelectedIndex();
                unit.rawChild.setFlaw(slot, comboFlaw.getSelectionModel().getSelectedIndex());
                displayModifiers();
            }
        });
    }


    private void updateModifiers(int slot) {
        //Parent ID
        int unitId = comboUnit.getSelectionModel().getSelectedIndex();
        if (unitId == 0) unitId = 65535;
        else unitId--;
        unit.rawChild.setParentId(slot, unitId);
        //Modifiers
        unit.rawChild.setAsset(slot, comboAsset.getSelectionModel().getSelectedIndex());
        unit.rawChild.setFlaw(slot, comboFlaw.getSelectionModel().getSelectedIndex());
    }

    /*
    Sets the main combobox controller
     */
    private void setupComboSlot() {
        comboSlot.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null && unit.hasChildBlock) {
                //The previous slot is updated
                updateModifiers(oldValue.intValue());
                int slot = (int) newValue;
                //Parent Unit
                int parent = unit.rawChild.parentId(slot);
                parent++;
                if (parent == 65535 + 1) parent = 0;
                comboUnit.getSelectionModel().select(parent);
                //Assets and flaw
                int asset = unit.rawChild.asset(slot);
                comboAsset.getSelectionModel().select(asset);
                comboFlaw.getSelectionModel().select(unit.rawChild.flaw(slot));
                displayModifiers();
            }
        });
    }


    private void setupSpinners(Spinner<Integer> spinner) {
        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                unit.rawChild.setSupportParent(true, spinFather.getValue());
                unit.rawChild.setSupportParent(false, spinMother.getValue());
                unit.rawChild.setSupportSibling(spinSibling.getValue());
                setSupportLabels();
            }
        });
    }

    private void setSupportLabels() {
        lblFather.setText(calcSupportLevel(spinFather.getValue()));
        lblMother.setText(calcSupportLevel(spinMother.getValue()));
        lblSibling.setText(calcSupportLevel(spinSibling.getValue()));
    }

    private String calcSupportLevel(int value) {
        int[] maxValues = Supports.supportValues().get(4);
        if (value < maxValues[0]) return "D-Rank";
        else if (value == maxValues[0]) return "C-Pending";
        else if (value < maxValues[2]) return "C-Rank";
        else if (value == maxValues[2]) return "B-Pending";
        else if (value < maxValues[4]) return "B-Rank";
        else if (value == maxValues[4]) return "A-Pending";
        else if (value == maxValues[5]) return "A-Rank";
        return "?";
    }

    private void displayModifiers() {
        txtModif.setText(Arrays.toString(unit.modifiers()));
    }
}
