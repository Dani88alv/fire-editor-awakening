package com.danius.fireeditor.controllers;

import com.danius.fireeditor.savefile.inventory.Refinement;
import com.danius.fireeditor.savefile.units.Unit;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

import java.util.List;

import static com.danius.fireeditor.data.ItemDb.*;

public class ItemController {

    private Unit unit;
    private List<Refinement> refiList;
    private int maxCount;

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

    public void setUnit(Unit unit, List<Refinement> refiList) {
        this.unit = unit;
        this.refiList = refiList;
        //Item comboboxes
        ObservableList<String> items = FXCollections.observableArrayList();
        items.addAll(getItemNamesAll(refiList));
        comboItem1.setItems(items);
        comboItem2.setItems(items);
        comboItem3.setItems(items);
        comboItem4.setItems(items);
        comboItem5.setItems(items);
        setValues();
        setListeners();
    }

    public void setMaxAmount() {
        unit.rawInventory.maxAmount(refiList);
        setValues();
    }

    public void setValues() {
        List<ComboBox<String>> itemId = List.of(comboItem1, comboItem2, comboItem3, comboItem4, comboItem5);
        List<Spinner<Integer>> itemUse = List.of(spinItem1, spinItem2, spinItem3, spinItem4, spinItem5);
        List<CheckBox> equipped = List.of(checkItem1, checkItem2, checkItem3, checkItem4, checkItem5);
        List<CheckBox> dropped = List.of(dropItem1, dropItem2, dropItem3, dropItem4, dropItem5);
        for (int i = 0; i < 5; i++) {
            itemId.get(i).getSelectionModel().select(unit.rawInventory.items.get(i).itemId());
            itemUse.get(i).getValueFactory().setValue(unit.rawInventory.items.get(i).uses());
            equipped.get(i).setSelected(unit.rawInventory.items.get(i).equipped());
            dropped.get(i).setSelected(unit.rawInventory.items.get(i).dropped());
        }
    }

    public void setListeners() {
        List<ComboBox<String>> itemId = List.of(comboItem1, comboItem2, comboItem3, comboItem4, comboItem5);
        List<Spinner<Integer>> itemUse = List.of(spinItem1, spinItem2, spinItem3, spinItem4, spinItem5);
        List<CheckBox> equipped = List.of(checkItem1, checkItem2, checkItem3, checkItem4, checkItem5);
        List<CheckBox> dropped = List.of(dropItem1, dropItem2, dropItem3, dropItem4, dropItem5);
        for (int i = 0; i < 5; i++) {
            setupCombobox(itemId.get(i), i);
            setupSpinner(itemUse.get(i), i);
            setupEquip(equipped.get(i), i);
            setupDrop(dropped.get(i), i);
        }
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
