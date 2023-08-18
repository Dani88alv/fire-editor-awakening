package com.danius.fireeditor.controllers;

import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.util.Names;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

public class ItemController {

    private Unit unit;
    private int regularItemCount;

    @FXML
    private ComboBox<String> comboItem1, comboItem2, comboItem3, comboItem4, comboItem5;
    @FXML
    private Spinner<Integer> spinItem1, spinItem2, spinItem3, spinItem4, spinItem5;
    @FXML
    private CheckBox checkItem1, checkItem2, checkItem3, checkItem4, checkItem5;

    public void initialize() {
        UI.setSpinnerNumeric(spinItem1, 99);
        UI.setSpinnerNumeric(spinItem2, 99);
        UI.setSpinnerNumeric(spinItem3, 99);
        UI.setSpinnerNumeric(spinItem4, 99);
        UI.setSpinnerNumeric(spinItem5, 99);
    }

    public void setUnit(Unit unit, int regularItemCount) {
        this.unit = unit;
        int count = regularItemCount + 150; //150 seems to be very large for Java FX?
        //Item comboboxes
        ObservableList<String> items = FXCollections.observableArrayList();
        for (int i = 0; i < count; i++) {
            items.add(Names.itemName2(i, regularItemCount));
        }
        comboItem1.setItems(items);
        comboItem2.setItems(items);
        comboItem3.setItems(items);
        comboItem4.setItems(items);
        comboItem5.setItems(items);
        setValues();
        setListeners();
    }

    public void setValues() {
        comboItem1.getSelectionModel().select(unit.rawInventory.items.get(0).itemId());
        comboItem2.getSelectionModel().select(unit.rawInventory.items.get(1).itemId());
        comboItem3.getSelectionModel().select(unit.rawInventory.items.get(2).itemId());
        comboItem4.getSelectionModel().select(unit.rawInventory.items.get(3).itemId());
        comboItem5.getSelectionModel().select(unit.rawInventory.items.get(4).itemId());
        spinItem1.getValueFactory().setValue(unit.rawInventory.items.get(0).amount());
        spinItem2.getValueFactory().setValue(unit.rawInventory.items.get(1).amount());
        spinItem3.getValueFactory().setValue(unit.rawInventory.items.get(2).amount());
        spinItem4.getValueFactory().setValue(unit.rawInventory.items.get(3).amount());
        spinItem5.getValueFactory().setValue(unit.rawInventory.items.get(4).amount());
        checkItem1.setSelected(unit.rawInventory.items.get(0).equipped());
        checkItem2.setSelected(unit.rawInventory.items.get(1).equipped());
        checkItem3.setSelected(unit.rawInventory.items.get(2).equipped());
        checkItem4.setSelected(unit.rawInventory.items.get(3).equipped());
        checkItem5.setSelected(unit.rawInventory.items.get(4).equipped());
    }

    public void setListeners() {
        setupCombobox(comboItem1, 0);
        setupCombobox(comboItem2, 1);
        setupCombobox(comboItem3, 2);
        setupCombobox(comboItem4, 3);
        setupCombobox(comboItem5, 4);
        setupSpinner(spinItem1, 0);
        setupSpinner(spinItem2, 1);
        setupSpinner(spinItem3, 2);
        setupSpinner(spinItem4, 3);
        setupSpinner(spinItem5, 4);
        setupCheckbox(checkItem1, 0);
        setupCheckbox(checkItem2, 1);
        setupCheckbox(checkItem3, 2);
        setupCheckbox(checkItem4, 3);
        setupCheckbox(checkItem5, 4);
    }

    public void setupCombobox(ComboBox<String> combobox, int slot) {
        combobox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        unit.rawInventory.items.get(slot).setItemId((Integer) newValue);
                    }
                }
        );
    }

    private void setupSpinner(Spinner<Integer> spinner, int slot) {
        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                unit.rawInventory.items.get(slot).setAmount(newValue);
            }
        });
    }

    private void setupCheckbox(CheckBox checkBox, int slot) {
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                unit.rawInventory.items.get(slot).setEquipped(newValue);
            }
        });
    }

}
