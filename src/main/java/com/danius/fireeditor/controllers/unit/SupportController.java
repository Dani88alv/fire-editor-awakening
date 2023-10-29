package com.danius.fireeditor.controllers.unit;

import com.danius.fireeditor.controllers.UI;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.data.MiscDb;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

import static com.danius.fireeditor.data.UnitDb.*;

public class SupportController {
    private Unit unit;
    private List<Integer> unitIdList;
    private List<Unit> unitList;
    @FXML
    ComboBox<String> comboUnit, comboLevel;
    @FXML
    Spinner<Integer> spinValue;
    @FXML
    Label lblLevel;
    @FXML
    Button btnRemoveSupport, btnSetAll;
    @FXML
    CheckBox checkMaiden, checkSync;

    public void initialize() {
        UI.setSpinnerNumeric(spinValue, 0x16);
        ObservableList<String> levelOptions = FXCollections.observableArrayList(
                "D-Rank", "C-Pending", "C-Rank", "B-Pending", "B-Rank",
                "A-Pending", "A-Rank", "S-Pending", "S-Rank");
        comboLevel.setItems(levelOptions);
        comboLevel.getSelectionModel().select(0);
        setupComboUnit();
        checkMaiden.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (unit != null) unit.rawFlags.setBattleFlag(22, checkMaiden.isSelected());
        });
    }

    public void setUnit(Unit unit, List<Unit> unitList) {
        this.unit = unit;
        this.unitList = unitList;
        List<Integer> unitIds = new ArrayList<>();
        for (Unit value : unitList) {
            unitIds.add(value.getUnitId());
        }
        this.unitIdList = unitIds;
        checkMaiden.setSelected(unit.rawFlags.hasBattleFlag(22));
        if (unit.rawSupport.supportCount() > 0) {
            setFields();
            setupSpinners(spinValue);
            setLabelLevel();
        } else disableElements();
        checkSync.setSelected(true);
    }

    public void setFields() {
        if (unit != null) {
            //Unit Selection
            ObservableList<String> unitNames = FXCollections.observableArrayList();
            int totalCount = unit.rawSupport.supportCount();
            int[] characters = getUnitSupportUnits(unit.rawBlock1.unitId());
            for (int character : characters) {
                unitNames.add(getUnitName(character));
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
                setSupportValue(slot, value);
                setLabelLevel();
            }
        });
    }

    private void setSupportValue(int slot, int value) {
        //The main unit is edited
        unit.rawSupport.setSupportValue(slot, value);
        //The other supported units are edited
        if (checkSync.isSelected()) {
            int unitId = unit.getUnitId();
            int currentSlot = comboUnit.getSelectionModel().getSelectedIndex();
            int[] validUnits = getUnitSupportUnits(unitId);
            //Modded units are ignored
            if (currentSlot < validUnits.length) {
                int idToCheck = validUnits[currentSlot];
                for (Unit unitToEdit : unitList) {
                    //If matched, set the same support value
                    if (unitToEdit.getUnitId() == idToCheck) {
                        unitToEdit.rawSupport.setSupportValueByUnit(unitId, value);
                    }
                }
            }
        }

    }

    @FXML
    private void setUnitsToLevel() {
        int level = comboLevel.getSelectionModel().getSelectedIndex();
        unit.rawSupport.setAllSupportsTo(level, unitIdList);
        int selectedUnit = comboUnit.getSelectionModel().getSelectedIndex();
        spinValue.getValueFactory().setValue(unit.rawSupport.supportValue(selectedUnit));

        //The other units are also edited
        if (checkSync.isSelected()) {
            int unitId = unit.getUnitId();
            int[] validSupports = getUnitSupportUnits(unitId);
            for (int idToCheck : validSupports) {
                //All the units are checked
                for (Unit unitToEdit : unitList) {
                    //If matched, set the same support value
                    if (unitToEdit.getUnitId() == idToCheck) {
                        unitToEdit.rawSupport.setSupportLevelByUnit(unitId, level);
                    }
                }
            }
        }

        setLabelLevel();
    }

    @FXML
    private void removeExtraSupports() {
        unit.rawSupport.removeExtraSupports();
        int currentSupports = comboUnit.getItems().size();
        int slotsToRemove = currentSupports - unit.rawSupport.supportCount();
        for (int i = 0; i < slotsToRemove; i++) {
            if (comboUnit.getItems().size() != 0) {
                comboUnit.getSelectionModel().selectFirst();
            }
            comboUnit.getItems().remove(comboUnit.getItems().size() - 1);
        }
        if (comboUnit.getItems().size() == 0) {
            disableElements();
        } else comboUnit.getSelectionModel().select(0);
    }

    private void disableElements() {
        checkSync.setDisable(true);
        comboUnit.setDisable(true);
        spinValue.setDisable(true);
        btnRemoveSupport.setDisable(true);
        btnSetAll.setDisable(true);
        comboLevel.setDisable(true);
    }

    private void setLabelLevel() {
        int value = spinValue.getValue();
        int slot = comboUnit.getSelectionModel().getSelectedIndex();
        lblLevel.setText(getUnitSupportLevelName(unit.getUnitId(), value, slot));
    }

}
