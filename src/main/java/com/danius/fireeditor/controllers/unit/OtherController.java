package com.danius.fireeditor.controllers.unit;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.controllers.UI;
import com.danius.fireeditor.data.ChapterDb;
import com.danius.fireeditor.data.UnitDb;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.mainblock.RawBlock2;
import com.danius.fireeditor.data.MiscDb;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;

public class OtherController {
    private Unit unit;
    @FXML
    private Spinner<Integer> spinX1, spinY1, spinX2, spinY2,
            spinCurrentHp, spinDeploy, spinBattle, spinVictory,
            spinSword, spinLance, spinAxe, spinBow, spinTome, spinStave,
            spinResBuff, spinHiddenLevel;
    @FXML
    private ComboBox<String> comboArmy, comboRetire;
    @FXML
    private CheckBox checkRetire, checkDead1, checkDead2;
    @FXML
    private Label lblSword, lblBow, lblLance, lblAxe, lblTome, lblStave, lblRetired;
    private Label[] weaponLevels = new Label[]{};

    public void initialize() {
        setupElements();
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
        setFields();
        setAiFields();
    }


    public void setFields() {
        boolean canRetire = UnitDb.canUnitRetire(unit.getUnitId());
        String state = (canRetire) ? "Retired On:" : "Died On:";
        lblRetired.setText(state);
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
        spinHiddenLevel.getValueFactory().setValue(unit.rawFlags.hiddenLevel());
        checkRetire.setSelected(unit.rawFlags.hasBattleFlag(3));
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
        lblLance.setText(weaponLevel(unit.rawBlock2.getWeaponExp()[1]));
        lblAxe.setText(weaponLevel(unit.rawBlock2.getWeaponExp()[2]));
        lblBow.setText(weaponLevel(unit.rawBlock2.getWeaponExp()[3]));
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
        setupSpinners(spinHiddenLevel);
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
        UI.setSpinnerNumeric(spinHiddenLevel, 255);
        //Combobox
        int maxArmy = FireEditor.chapterFile.MAX_ID_ARMY;
        ObservableList<String> armies = FXCollections.observableArrayList(MiscDb.getArmyNames(maxArmy));
        comboArmy.setItems(armies);
        comboArmy.getSelectionModel().select(0);

        ObservableList<String> retireChapters = FXCollections.observableArrayList(ChapterDb.getDescriptions());
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

        //AI
        Spinner<Integer>[] aiType = new Spinner[]{spinAction, spinMission, spinAttack, spinMove};
        Label[] aiLabel = new Label[]{lblAction, lblMission, lblAttack, lblMove};
        action = new Spinner[]{spinAction1, spinAction2, spinAction3, spinAction4};
        mission = new Spinner[]{spinMission1, spinMission2, spinMission3, spinMission4};
        attack = new Spinner[]{spinAttack1, spinAttack2, spinAttack3, spinAttack4};
        move = new Spinner[]{spinMove1, spinMove2, spinMove3, spinMove4};
        for (int i = 0; i < 4; i++) {
            //AI Type
            UI.setSpinnerNumeric(aiType[i], 255);
            setupAiListener(aiType[i], i, aiLabel[i]);
            //Parameters
            UI.setSpinnerNumeric(action[i], 0xFFFF);
            UI.setSpinnerNumeric(mission[i], 0xFFFF);
            UI.setSpinnerNumeric(attack[i], 0xFFFF);
            UI.setSpinnerNumeric(move[i], 0xFFFF);
            setupAiParamListener(action[i], 0, i);
            setupAiParamListener(mission[i], 1, i);
            setupAiParamListener(attack[i], 2, i);
            setupAiParamListener(move[i], 3, i);
        }

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
            unit.rawFlags.setHiddenLevel(spinHiddenLevel.getValue());
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


    /*
    AI Tab
     */

    @FXML
    private Spinner<Integer> spinAction, spinMission, spinAttack, spinMove,
            spinAction1, spinAction2, spinAction3, spinAction4,
            spinMission1, spinMission2, spinMission3, spinMission4,
            spinAttack1, spinAttack2, spinAttack3, spinAttack4,
            spinMove1, spinMove2, spinMove3, spinMove4;
    @FXML
    private Label lblAction, lblMission, lblAttack, lblMove;
    @FXML
    private TextField txtEndSection;
    private Spinner<Integer>[] action, mission, attack, move;

    public void setAiFields() {
        spinAction.getValueFactory().setValue(unit.rawBlockEnd.aiType(0));
        spinMission.getValueFactory().setValue(unit.rawBlockEnd.aiType(1));
        spinAttack.getValueFactory().setValue(unit.rawBlockEnd.aiType(2));
        spinMove.getValueFactory().setValue(unit.rawBlockEnd.aiType(3));
        txtEndSection.setText(unit.rawBlockEnd.endSectionString());
        Label[] aiLabel = new Label[]{lblAction, lblMission, lblAttack, lblMove};
        for (int i = 0; i < 4; i++) {
            action[i].getValueFactory().setValue(unit.rawBlockEnd.aiParam(0, i));
            mission[i].getValueFactory().setValue(unit.rawBlockEnd.aiParam(1, i));
            attack[i].getValueFactory().setValue(unit.rawBlockEnd.aiParam(2, i));
            move[i].getValueFactory().setValue(unit.rawBlockEnd.aiParam(3, i));

            switch (i) {
                case 0 -> aiLabel[i].setText(MiscDb.getAiAction(unit.rawBlockEnd.aiType(i)));
                case 1 -> aiLabel[i].setText(MiscDb.getAiMission(unit.rawBlockEnd.aiType(i)));
                case 2 -> aiLabel[i].setText(MiscDb.getAiAttack(unit.rawBlockEnd.aiType(i)));
                case 3 -> aiLabel[i].setText(MiscDb.getAiMove(unit.rawBlockEnd.aiType(i)));
            }
        }
    }

    public void setupAiListener(Spinner<Integer> spinner, int aiSlot, Label label) {
        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                unit.rawBlockEnd.setAiType(aiSlot, newValue);
                switch (aiSlot) {
                    case 0 -> label.setText(MiscDb.getAiAction(newValue));
                    case 1 -> label.setText(MiscDb.getAiMission(newValue));
                    case 2 -> label.setText(MiscDb.getAiAttack(newValue));
                    case 3 -> label.setText(MiscDb.getAiMove(newValue));
                }
            }
        });
    }

    public void setupAiParamListener(Spinner<Integer> spinner, int aiSlot, int paramSlot) {
        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                unit.rawBlockEnd.setAiParam(aiSlot, paramSlot, newValue);
            }
        });
    }

}
