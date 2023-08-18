package com.danius.fireeditor.controllers;

import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.mainblock.RawBlock2;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Objects;

public class WeaponExpController {
    @FXML
    private Spinner<Integer> spinSword, spinLance, spinAxe, spinBow, spinTome, spinStave;

    private Unit unit;

    public void initialize() {
        setupElements();
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
        setFields();
    }

    public void maxFields() {
        unit.rawBlock2.setMaxWeaponExp();
        setFields();
    }

    public void setFields() {
        RawBlock2 rawBlock2 = unit.rawBlock2;
        int[] weaponExp = rawBlock2.getWeaponExp();
        spinSword.getValueFactory().setValue(weaponExp[0]);
        spinBow.getValueFactory().setValue(weaponExp[1]);
        spinLance.getValueFactory().setValue(weaponExp[2]);
        spinAxe.getValueFactory().setValue(weaponExp[3]);
        spinTome.getValueFactory().setValue(weaponExp[4]);
        spinStave.getValueFactory().setValue(weaponExp[5]);
        setListeners(spinSword);
        setListeners(spinBow);
        setListeners(spinLance);
        setListeners(spinAxe);
        setListeners(spinTome);
        setListeners(spinStave);
    }

    public void setupElements() {
        int max = 90;
        UI.setSpinnerNumeric(spinSword, max);
        UI.setSpinnerNumeric(spinBow, max);
        UI.setSpinnerNumeric(spinLance, max);
        UI.setSpinnerNumeric(spinAxe, max);
        UI.setSpinnerNumeric(spinTome, max);
        UI.setSpinnerNumeric(spinStave, max);
    }

    private void updateFromField() {
        unit.rawBlock2.setWeaponExp(spinSword.getValue(), 0);
        unit.rawBlock2.setWeaponExp(spinBow.getValue(), 1);
        unit.rawBlock2.setWeaponExp(spinLance.getValue(), 2);
        unit.rawBlock2.setWeaponExp(spinAxe.getValue(), 3);
        unit.rawBlock2.setWeaponExp(spinTome.getValue(), 4);
        unit.rawBlock2.setWeaponExp(spinStave.getValue(), 5);
    }

    private void setListeners(Spinner<Integer> spinner){
        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                updateFromField();
            }
        });
    }

}
