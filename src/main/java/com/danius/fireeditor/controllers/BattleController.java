package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.util.Names;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class BattleController {
    private Unit unit;
    @FXML
    private Spinner<Integer> spinX1, spinY1, spinX2, spinY2,
            spinCurrentHp, spinDeploy, spinBattle, spinVictory;
    @FXML
    private ComboBox<String> comboTrait, comboBattle, comboArmy;
    @FXML
    private CheckBox checkTrait, checkBattle;
    @FXML
    private Label lblTrait, lblBattle;

    public void initialize() {
        setupElements();
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
        setFields();
    }

    public void enableAllUnusedStats() {
        unit.rawFlags.setAllTonicFlags();
    }

    public void enableAllTonics() {
        unit.rawFlags.setAllUnusedStats();
    }

    public void setFields() {
        comboArmy.getSelectionModel().select(unit.rawFlags.army());
        comboTrait.getSelectionModel().select(0);
        comboBattle.getSelectionModel().select(0);
        spinX1.getValueFactory().setValue(unit.rawBlock1.coordinates1()[0]);
        spinY1.getValueFactory().setValue(unit.rawBlock1.coordinates1()[1]);
        spinX2.getValueFactory().setValue(unit.rawBlock1.coordinates2()[0]);
        spinY2.getValueFactory().setValue(unit.rawBlock1.coordinates2()[1]);
        spinCurrentHp.getValueFactory().setValue(unit.rawBlock1.currentHp());
        spinDeploy.getValueFactory().setValue(unit.rawFlags.slotParty());
        spinBattle.getValueFactory().setValue(unit.rawBlockEnd.battleCount());
        spinVictory.getValueFactory().setValue(unit.rawBlockEnd.victoryCount());
        updateFlagCount();
        //Listeners
        setupSpinners(spinCurrentHp);
        setupSpinners(spinDeploy);
        setupSpinners(spinX1);
        setupSpinners(spinY1);
        setupSpinners(spinX2);
        setupSpinners(spinY2);
        setupSpinners(spinBattle);
        setupSpinners(spinVictory);
    }

    public void setupElements() {
        //Spinners
        UI.setSpinnerNumeric(spinX1, 255);
        UI.setSpinnerNumeric(spinY1, 255);
        UI.setSpinnerNumeric(spinX2, 255);
        UI.setSpinnerNumeric(spinY2, 255);
        UI.setSpinnerNumeric(spinCurrentHp, 255);
        UI.setSpinnerNumeric(spinDeploy, 255);
        UI.setSpinnerNumeric(spinBattle, 65335);
        UI.setSpinnerNumeric(spinVictory, 65335);
        //Combobox
        ObservableList<String> traitFlags = FXCollections.observableArrayList(Names.traitFlags);
        comboTrait.setItems(traitFlags);
        ObservableList<String> battleFlags = FXCollections.observableArrayList(Names.battleFlags);
        comboBattle.setItems(battleFlags);
        ObservableList<String> armies = FXCollections.observableArrayList(Names.armies);
        for (int i = Constants.MAX_ARMY; i < FireEditor.maxArmies; i++) {
            armies.add("Extra #" + (FireEditor.maxArmies() - Constants.MAX_ARMY + 1));
        }
        comboArmy.setItems(armies);
        comboArmy.getSelectionModel().select(0);
        //Listeners
        setupComboTrait();
        setupComboBattle();
        setupCheckTrait();
        setupCheckBattle();
        setupComboArmy();
    }

    private void updateFromFields() {
        if (unit != null) {
            unit.rawFlags.setArmy(comboArmy.getSelectionModel().getSelectedIndex());
            unit.rawBlock1.setCurrentHp(spinCurrentHp.getValue());
            unit.rawFlags.setSlotParty(spinDeploy.getValue());
            unit.rawBlock1.setCoordinates1(spinX1.getValue(), spinY1.getValue());
            unit.rawBlock1.setCoordinates2(spinX2.getValue(), spinY2.getValue());
            unit.rawBlockEnd.setBattles(spinBattle.getValue());
            unit.rawBlockEnd.setVictories(spinVictory.getValue());
        }
    }

    private void setupComboArmy() {
        comboArmy.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null) {
                int selectedArmy = comboArmy.getSelectionModel().getSelectedIndex();
                unit.rawFlags.setArmy(selectedArmy);
            }
        });
    }

    private void setupComboTrait() {
        comboTrait.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null) {
                int selectedFlag = comboTrait.getSelectionModel().getSelectedIndex();
                List<Integer> flags = unit.rawFlags.traitFlagList();
                checkTrait.setSelected(flags.contains(selectedFlag));
            }
        });
    }

    private void setupComboBattle() {
        comboBattle.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null) {
                int selectedFlag = comboBattle.getSelectionModel().getSelectedIndex();
                List<Integer> flags = unit.rawFlags.battleFlagList();
                checkBattle.setSelected(flags.contains(selectedFlag));
            }
        });
    }

    private void setupCheckTrait() {
        checkTrait.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null) {
                int flag = comboTrait.getSelectionModel().getSelectedIndex();
                unit.rawFlags.setTraitFlag(flag, newValue);
                updateFlagCount();
            }
        });
    }

    private void setupCheckBattle() {
        checkBattle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null) {
                int flag = comboBattle.getSelectionModel().getSelectedIndex();
                unit.rawFlags.setBattleFlag(flag, newValue);
                updateFlagCount();
            }
        });
    }

    private void setupSpinners(Spinner<Integer> spinner) {
        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                updateFromFields();
            }
        });
    }

    private void updateFlagCount() {
        List<Integer> traitFlags = unit.rawFlags.traitFlagList();
        List<Integer> battleFlags = unit.rawFlags.battleFlagList();
        lblTrait.setText("Trait Flags: " + traitFlags.size());
        lblBattle.setText("Battle Flags: " + battleFlags.size());
    }
}
