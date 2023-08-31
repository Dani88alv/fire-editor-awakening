package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.controllers.UI;
import com.danius.fireeditor.savefile.Constants13;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.mainblock.RawBlock2;
import com.danius.fireeditor.util.Names13;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class OtherController {
    private Unit unit;
    @FXML
    private Spinner<Integer> spinX1, spinY1, spinX2, spinY2,
            spinCurrentHp, spinDeploy, spinBattle, spinVictory,
            spinSword, spinLance, spinAxe, spinBow, spinTome, spinStave,
            spinResBuff;
    @FXML
    private ComboBox<String> comboArmy, comboRetire;
    @FXML
    private CheckBox checkRetire, checkDead1, checkDead2;
    @FXML
    private Label lblSword, lblBow, lblLance, lblAxe, lblTome, lblStave;
    private Label[] weaponLevels = new Label[]{};

    public void initialize() {
        setupElements();
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
        setFields();
    }


    public void setFields() {
        comboArmy.getSelectionModel().select(unit.rawFlags.army());
        comboRetire.getSelectionModel().select(unit.rawBlockEnd.retireChapter());
        spinX1.getValueFactory().setValue(unit.rawBlock1.coordinates1()[0]);
        spinY1.getValueFactory().setValue(unit.rawBlock1.coordinates1()[1]);
        spinX2.getValueFactory().setValue(unit.rawBlock1.coordinates2()[0]);
        spinY2.getValueFactory().setValue(unit.rawBlock1.coordinates2()[1]);
        spinCurrentHp.getValueFactory().setValue(unit.rawBlock1.currentHp());
        spinDeploy.getValueFactory().setValue(unit.rawFlags.slotParty());
        spinBattle.getValueFactory().setValue(unit.rawBlockEnd.battleCount());
        spinVictory.getValueFactory().setValue(unit.rawBlockEnd.victoryCount());
        spinResBuff.getValueFactory().setValue(unit.rawFlags.resBuff());
        checkRetire.setSelected(unit.rawFlags.battleFlagString().charAt(3) == '1');
        checkDead1.setSelected(unit.rawBlockEnd.deadFlag1());
        checkDead2.setSelected(unit.rawBlockEnd.deadFlag2());
        RawBlock2 rawBlock2 = unit.rawBlock2;
        int[] weaponExp = rawBlock2.getWeaponExp();
        spinSword.getValueFactory().setValue(weaponExp[0]);
        spinLance.getValueFactory().setValue(weaponExp[1]);
        spinAxe.getValueFactory().setValue(weaponExp[2]);
        spinBow.getValueFactory().setValue(weaponExp[3]);
        spinTome.getValueFactory().setValue(weaponExp[4]);
        spinStave.getValueFactory().setValue(weaponExp[5]);
        lblSword.setText(weaponLevel(unit.rawBlock2.getWeaponExp()[0]));
        lblBow.setText(weaponLevel(unit.rawBlock2.getWeaponExp()[1]));
        lblLance.setText(weaponLevel(unit.rawBlock2.getWeaponExp()[2]));
        lblAxe.setText(weaponLevel(unit.rawBlock2.getWeaponExp()[3]));
        lblTome.setText(weaponLevel(unit.rawBlock2.getWeaponExp()[4]));
        lblStave.setText(weaponLevel(unit.rawBlock2.getWeaponExp()[5]));
        //Listeners
        setupOtherListeners();
        setupSpinners(spinCurrentHp);
        setupSpinners(spinDeploy);
        setupSpinners(spinX1);
        setupSpinners(spinY1);
        setupSpinners(spinX2);
        setupSpinners(spinY2);
        setupSpinners(spinBattle);
        setupSpinners(spinVictory);
        setupSpinners(spinResBuff);
    }

    public void setupElements() {
        //Spinners
        int maxExp = 90;
        UI.setSpinnerNumeric(spinResBuff, 255);
        UI.setSpinnerNumeric(spinX1, 255);
        UI.setSpinnerNumeric(spinY1, 255);
        UI.setSpinnerNumeric(spinX2, 255);
        UI.setSpinnerNumeric(spinY2, 255);
        UI.setSpinnerNumeric(spinCurrentHp, 255);
        UI.setSpinnerNumeric(spinDeploy, 255);
        UI.setSpinnerNumeric(spinBattle, 65335);
        UI.setSpinnerNumeric(spinVictory, 65335);
        UI.setSpinnerNumeric(spinSword, maxExp);
        UI.setSpinnerNumeric(spinBow, maxExp);
        UI.setSpinnerNumeric(spinLance, maxExp);
        UI.setSpinnerNumeric(spinAxe, maxExp);
        UI.setSpinnerNumeric(spinTome, maxExp);
        UI.setSpinnerNumeric(spinStave, maxExp);
        //Combobox
        ObservableList<String> armies = FXCollections.observableArrayList(Names13.armies);
        for (int i = Constants13.MAX_ARMY; i < FireEditor.maxArmies; i++) {
            armies.add("Extra #" + (FireEditor.maxArmies() - Constants13.MAX_ARMY + 1));
        }
        comboArmy.setItems(armies);
        comboArmy.getSelectionModel().select(0);

        ObservableList<String> retireChapters = FXCollections.observableArrayList(Names13.retireChapters);
        comboRetire.setItems(retireChapters);
        comboRetire.getSelectionModel().select(0);
        //Listeners
        weaponLevels = new Label[]{lblSword, lblLance, lblAxe, lblBow, lblTome, lblStave};
        setWeaponSpinListener(spinSword, 0);
        setWeaponSpinListener(spinLance, 1);
        setWeaponSpinListener(spinAxe, 2);
        setWeaponSpinListener(spinBow, 3);
        setWeaponSpinListener(spinTome, 4);
        setWeaponSpinListener(spinStave, 5);
    }

    public void setMaxExp() {
        int max = 90;
        spinSword.getValueFactory().setValue(max);
        spinBow.getValueFactory().setValue(max);
        spinLance.getValueFactory().setValue(max);
        spinAxe.getValueFactory().setValue(max);
        spinTome.getValueFactory().setValue(max);
        spinStave.getValueFactory().setValue(max);
    }

    private void setWeaponSpinListener(Spinner<Integer> spinner, int slot) {
        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (unit != null) {
                    unit.rawBlock2.setWeaponExp(spinner.getValue(), slot);
                    String level = weaponLevel(unit.rawBlock2.getWeaponExp()[slot]);
                    weaponLevels[slot].setText(level);
                }
            }
        });
    }

    private void setupOtherListeners() {
        comboArmy.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null) {
                int selectedArmy = comboArmy.getSelectionModel().getSelectedIndex();
                unit.rawFlags.setArmy(selectedArmy);
                FireEditor.unitController.setImage();
            }
        });
        comboRetire.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null) {
                int retireChapter = comboRetire.getSelectionModel().getSelectedIndex();
                unit.rawBlockEnd.setRetireChapter(retireChapter);
            }
        });
        checkDead1.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (unit != null) unit.rawBlockEnd.setDeadFlag1(checkDead1.isSelected());
        });
        checkDead2.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (unit != null) unit.rawBlockEnd.setDeadFlag2(checkDead2.isSelected());
        });
        checkRetire.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (unit != null) unit.rawFlags.setBattleFlag(3, checkRetire.isSelected());
        });
    }

    private void updateFromFields() {
        if (unit != null) {
            unit.rawBlock1.setCurrentHp(spinCurrentHp.getValue());
            unit.rawFlags.setSlotParty(spinDeploy.getValue());
            unit.rawBlock1.setCoordinates1(spinX1.getValue(), spinY1.getValue());
            unit.rawBlock1.setCoordinates2(spinX2.getValue(), spinY2.getValue());
            unit.rawBlockEnd.setBattles(spinBattle.getValue());
            unit.rawBlockEnd.setVictories(spinVictory.getValue());
            unit.rawFlags.setResBuff(spinResBuff.getValue());
            FireEditor.unitController.setFieldsStats(unit);
        }
    }

    private void setupSpinners(Spinner<Integer> spinner) {
        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                updateFromFields();
            }
        });
    }

    private String weaponLevel(int value) {
        if (value >= 0 && value <= 14) return "E-Rank";
        else if (value >= 15 && value <= 34) return "D-Rank";
        else if (value >= 35 && value <= 59) return "C-Rank";
        else if (value >= 60 && value <= 89) return "B-Rank";
        else return "A-Rank";
    }
}
