package com.danius.fireeditor.controllers;

import com.danius.fireeditor.savefile.barrack.EvstBlock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

import static com.danius.fireeditor.data.UnitDb.*;

public class BarrackController {

    private EvstBlock evstBlock;
    @FXML
    private ComboBox<String> comboSlot, comboUnit1, comboUnit2, comboEvent;
    @FXML
    private Spinner<Integer> spinIcon;

    public void initialize() {
        setupElements();
    }

    public void setupElements() {
        UI.setSpinnerNumeric(spinIcon, 255);
        //Units
        ObservableList<String> units = FXCollections.observableArrayList();
        units.add("None");
        units.addAll(getUnitNames());
        comboUnit1.setItems(units);
        comboUnit2.setItems(units);
        ObservableList<String> events = FXCollections.observableArrayList(
                "Undefined", "Stat Boost", "Exp Gain", "Weapon Exp Gain", "Random Item", "Conversation", "Birthday");
        comboEvent.setItems(events);
        ObservableList<String> slots = FXCollections.observableArrayList(
                "Event #1", "Event #2", "Event #3", "Event #4", "Event #5");
        comboSlot.setItems(slots);
    }

    public void addListeners() {
        comboSlot.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int slot = comboSlot.getSelectionModel().getSelectedIndex();
                    //Unit slots
                    int unit1 = evstBlock.eventList.get(slot).unit1();
                    int unit2 = evstBlock.eventList.get(slot).unit2();
                    if (unit1 == 65535) unit1 = -1;
                    if (unit2 == 65535) unit2 = -1;
                    //Selected
                    comboUnit1.getSelectionModel().select(unit1 + 1);
                    comboUnit2.getSelectionModel().select(unit2 + 1);
                    comboEvent.getSelectionModel().select(evstBlock.eventList.get(slot).eventType());
                    spinIcon.getValueFactory().setValue(evstBlock.eventList.get(slot).eventIcon());
                }
        );
        comboUnit1.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int slot = comboSlot.getSelectionModel().getSelectedIndex();
                    int unit1 = comboUnit1.getSelectionModel().getSelectedIndex() - 1;
                    if (unit1 == -1) unit1 = 65535;
                    evstBlock.eventList.get(slot).setUnit1(unit1);
                }
        );
        comboUnit2.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int slot = comboSlot.getSelectionModel().getSelectedIndex();
                    int unit2 = comboUnit2.getSelectionModel().getSelectedIndex() - 1;
                    if (unit2 == -1) unit2 = 65535;
                    evstBlock.eventList.get(slot).setUnit2(unit2);
                }
        );
        comboEvent.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int slot = comboSlot.getSelectionModel().getSelectedIndex();
                    evstBlock.eventList.get(slot).setEventType(comboEvent.getSelectionModel().getSelectedIndex());
                }
        );
        spinIcon.valueProperty().addListener((observable, oldValue, newValue) -> {
            int slot = comboSlot.getSelectionModel().getSelectedIndex();
            evstBlock.eventList.get(slot).setEventIcon(spinIcon.getValue());
        });
    }

    public void setEvstBlock(EvstBlock evstBlock) {
        this.evstBlock = evstBlock;
        addListeners();
        comboSlot.getSelectionModel().select(0);
    }
}
