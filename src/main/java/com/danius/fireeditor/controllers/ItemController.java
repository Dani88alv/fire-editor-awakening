package com.danius.fireeditor.controllers;

import com.danius.fireeditor.controllers.UI;
import com.danius.fireeditor.savefile.Constants13;
import com.danius.fireeditor.savefile.inventory.TranBlock;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.util.Names13;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ItemController {

    private Unit unit;

    @FXML
    private ComboBox<String> comboItem1, comboItem2, comboItem3, comboItem4, comboItem5;
    @FXML
    private Spinner<Integer> spinItem1, spinItem2, spinItem3, spinItem4, spinItem5;
    @FXML
    private CheckBox checkItem1, checkItem2, checkItem3, checkItem4, checkItem5,
            dropItem1, dropItem2, dropItem3, dropItem4, dropItem5;

    public void initialize() {
        UI.setSpinnerNumeric(spinItem1, 99);
        UI.setSpinnerNumeric(spinItem2, 99);
        UI.setSpinnerNumeric(spinItem3, 99);
        UI.setSpinnerNumeric(spinItem4, 99);
        UI.setSpinnerNumeric(spinItem5, 99);
    }

    public void setUnit(Unit unit, int regularItemCount) {
        this.unit = unit;
        int count = regularItemCount + 150;
        //Item comboboxes
        ObservableList<String> items = FXCollections.observableArrayList();
        for (int i = 0; i < count; i++) {
            items.add(Names13.itemName2(i, regularItemCount));
        }
        comboItem1.setItems(items);
        comboItem2.setItems(items);
        comboItem3.setItems(items);
        comboItem4.setItems(items);
        comboItem5.setItems(items);
        setValues();
        setListeners();
    }

    public void setMaxAmount() {
        List<Integer> maxValues = new ArrayList<>();
        for (int i = 0; i < unit.rawInventory.items.size(); i++) {
            int itemId = unit.rawInventory.items.get(i).itemId();
            //Only vanilla and non-forged items are modified
            if (itemId <= Constants13.MAX_ITEM_COUNT) maxValues.add(TranBlock.itemAmounts.get(itemId));
            else maxValues.add(unit.rawInventory.items.get(i).uses());
        }
        spinItem1.getValueFactory().setValue(maxValues.get(0));
        spinItem2.getValueFactory().setValue(maxValues.get(1));
        spinItem3.getValueFactory().setValue(maxValues.get(2));
        spinItem4.getValueFactory().setValue(maxValues.get(3));
        spinItem5.getValueFactory().setValue(maxValues.get(4));
    }

    public void setValues() {
        comboItem1.getSelectionModel().select(unit.rawInventory.items.get(0).itemId());
        comboItem2.getSelectionModel().select(unit.rawInventory.items.get(1).itemId());
        comboItem3.getSelectionModel().select(unit.rawInventory.items.get(2).itemId());
        comboItem4.getSelectionModel().select(unit.rawInventory.items.get(3).itemId());
        comboItem5.getSelectionModel().select(unit.rawInventory.items.get(4).itemId());
        spinItem1.getValueFactory().setValue(unit.rawInventory.items.get(0).uses());
        spinItem2.getValueFactory().setValue(unit.rawInventory.items.get(1).uses());
        spinItem3.getValueFactory().setValue(unit.rawInventory.items.get(2).uses());
        spinItem4.getValueFactory().setValue(unit.rawInventory.items.get(3).uses());
        spinItem5.getValueFactory().setValue(unit.rawInventory.items.get(4).uses());
        checkItem1.setSelected(unit.rawInventory.items.get(0).equipped());
        checkItem2.setSelected(unit.rawInventory.items.get(1).equipped());
        checkItem3.setSelected(unit.rawInventory.items.get(2).equipped());
        checkItem4.setSelected(unit.rawInventory.items.get(3).equipped());
        checkItem5.setSelected(unit.rawInventory.items.get(4).equipped());
        dropItem1.setSelected(unit.rawInventory.items.get(0).dropped());
        dropItem2.setSelected(unit.rawInventory.items.get(1).dropped());
        dropItem3.setSelected(unit.rawInventory.items.get(2).dropped());
        dropItem4.setSelected(unit.rawInventory.items.get(3).dropped());
        dropItem5.setSelected(unit.rawInventory.items.get(4).dropped());
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
        setupEquip(checkItem1, 0);
        setupEquip(checkItem2, 1);
        setupEquip(checkItem3, 2);
        setupEquip(checkItem4, 3);
        setupEquip(checkItem5, 4);
        setupDrop(dropItem1, 0);
        setupDrop(dropItem2, 1);
        setupDrop(dropItem3, 2);
        setupDrop(dropItem4, 3);
        setupDrop(dropItem5, 4);
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
                unit.rawInventory.items.get(slot).setUses(newValue);
            }
        });
    }

    private void setupEquip(CheckBox checkBox, int slot) {
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                unit.rawInventory.items.get(slot).setEquipped(newValue);
            }
        });
    }

    private void setupDrop(CheckBox checkBox, int slot) {
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                unit.rawInventory.items.get(slot).setDropped(newValue);
            }
        });
    }

}
