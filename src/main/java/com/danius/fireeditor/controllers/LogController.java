package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.units.SkillLogic;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.extrablock.LogBlock;
import com.danius.fireeditor.data.MiscDb;
import com.danius.fireeditor.util.Portrait;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;

import static com.danius.fireeditor.data.ClassDb.*;
import static com.danius.fireeditor.data.UnitDb.*;

public class LogController {
    private boolean listenersAdded = false;
    private Unit unit;
    private boolean isWest;
    @FXML
    private ComboBox<String> comboCard, comboAsset, comboFlaw, comboDifficulty;
    @FXML
    private Button btnSetLogId, btnRandomId;
    @FXML
    private Spinner<Integer> spinBuild, spinFace, spinHair, spinVoice;
    @FXML
    private TextField txtLogId, txtName;
    @FXML
    private CheckBox checkGender, checkCard,
            checkGameCasual, checkGameLunaplus, checkGameBeaten, checkGameHidden;
    @FXML
    private ColorPicker colorPickerHair;
    @FXML
    private ImageView imgHair, imgHairColor, imgBuild;

    public void initialize() {
        UI.setSpinnerNumeric(spinBuild, 2); //0-2
        UI.setSpinnerNumeric(spinFace, 4 + 16); //0-4 + DLC
        UI.setSpinnerNumeric(spinHair, 4); //0-4
        UI.setSpinnerNumeric(spinVoice, 4); //0-4
        txtLogId.setText("0");
        UI.setHexTextField(txtLogId, 26);
        //Profile Card
        UI.setSpinnerNumeric(spinExpression, 255);
        UI.setSpinnerNumeric(spinTrait, 255);
        UI.setSpinnerNumeric(spinHome, 255);
        UI.setSpinnerNumeric(spinValues, 255);
        UI.setSpinnerNumeric(spinIdentity, 255);
        UI.setSpinnerNumeric(spinDay, 31);
        UI.setSpinnerNumeric(spinMonth, 12);
        ObservableList<String> difficulty = FXCollections.observableArrayList("Normal", "Hard", "Lunatic");
        comboDifficulty.setItems(difficulty);
        //Einherjar names
        ObservableList<String> cardUnits = FXCollections.observableArrayList();
        cardUnits.addAll(getEinherjarNames());
        comboCard.setItems(cardUnits);
        comboCard.getSelectionModel().select(0);
        //Asset and flaw
        ObservableList<String> modifiers = FXCollections.observableArrayList();
        modifiers.addAll(MiscDb.modifNames);
        comboAsset.setItems(modifiers);
        comboFlaw.setItems(modifiers);
        //Class
        ObservableList<String> classes = FXCollections.observableArrayList();
        classes.addAll(getClassNames(FireEditor.chapterFile.MAX_ID_CLASS));
        comboClass.setItems(classes);
        //S-Pairings
        ObservableList<String> units = FXCollections.observableArrayList();
        units.addAll(getUnitNames());
        units.set(0, "None/Avatar (M)");
        comboWife.setItems(units);
        comboHusband.setItems(units);
        ObservableList<String> slots = FXCollections.observableArrayList();
        for (int i = 1; i <= 30; i++) {
            slots.add("Slot " + i);
        }
        comboPairingSlot.setItems(slots);
        //Everything is disabled
        disableFields(true);
    }

    public void setUnit(Unit unit, boolean isWest) {
        this.unit = unit;
        this.isWest = isWest;
        if (unit.rawLog != null) {
            //unit.rawLog.changeRegion(isWest);
            disableFields(false);
            loadFields();
            loadProfileFields();
            addListeners();
            displayImage();
        }
    }

    public void addLogData() {
        if (unit.rawLog == null) {
            String unitName = unit.unitName();
            this.unit.addBlockLog();
            unit.rawLog.changeRegion(isWest);
            unit.rawLog.setName(unitName);
            unit.rawLog.setAsset(0);
            unit.rawLog.setFlaw(0);
            unit.rawLog.setHairColorFx(unit.rawBlockEnd.getHairColorFx());
            unit.rawLog.setGender(SkillLogic.isFemaleUnit(this.unit));
            unit.rawLog.setLogIdRandom();

            disableFields(false);
            loadFields();
            loadProfileFields();
            if (!listenersAdded) addListeners();
            displayImage();
        }
    }

    public void removeLogData() {
        if (unit.rawLog != null) {
            this.unit.removeBlockExtra(true);
            disableFields(true);
            FireEditor.unitController.setImage();
        }
    }

    public void loadFields() {
        //Other
        comboAsset.getSelectionModel().select(unit.rawLog.getAssetFlaw()[0]);
        comboFlaw.getSelectionModel().select(unit.rawLog.getAssetFlaw()[1]);
        colorPickerHair.setValue(unit.rawLog.getHairColorFx());
        txtLogId.setText(unit.rawLog.getLogId());
        txtName.setText(unit.rawLog.getName());
        checkCard.setSelected(unit.rawLog.isEinherjar());
        //Build
        spinVoice.getValueFactory().setValue(unit.rawLog.getFullBuild()[3]);
        spinBuild.getValueFactory().setValue(unit.rawLog.getFullBuild()[0]);
        spinHair.getValueFactory().setValue(unit.rawLog.getFullBuild()[2]);
        int female = (unit.rawLog.getFullBuild()[4]);
        checkGender.setSelected(female > 0);
        //DLC Face
        int face = unit.rawLog.getFullBuild()[1];
        if (face > 4) {
            int[] dlcFaces = LogBlock.DLC_FACE_ID;
            int id = 0;
            for (int i = 0; i < dlcFaces.length; i++) {
                if (face == dlcFaces[i]) id = 5 + i;
            }
            face = id;
        }
        spinFace.getValueFactory().setValue(face);
    }

    @FXML
    private void setRandomId() {
        unit.rawLog.setLogIdRandom();
        txtLogId.setText(unit.rawLog.getLogId());
    }

    private void updateBuild() {
        unit.rawLog.setHairColorFx(colorPickerHair.getValue());
        unit.rawLog.setEinherjar(checkCard.isSelected());
        unit.rawLog.setLogId(txtLogId.getText());
        unit.rawLog.setVoice(spinVoice.getValue());
        unit.rawLog.setBuild(0, spinBuild.getValue());
        unit.rawLog.setBuild(2, spinHair.getValue());
        unit.rawLog.setGender(checkGender.isSelected());
        //Face
        int face = spinFace.getValue();
        if (face > 4) face = LogBlock.DLC_FACE_ID[face - 5]; //DLC Faces
        unit.rawLog.setBuild(1, face);
    }

    private void disableFields(boolean disable) {
        btnRandomId.setDisable(disable);
        //General
        comboFlaw.setDisable(disable);
        comboAsset.setDisable(disable);
        comboCard.setDisable(disable);
        txtName.setDisable(disable);
        btnSetLogId.setDisable(disable);
        //Build
        checkCard.setDisable(disable);
        checkGender.setDisable(disable);
        txtLogId.setDisable(disable);
        colorPickerHair.setDisable(disable);
        spinBuild.setDisable(disable);
        spinFace.setDisable(disable);
        spinHair.setDisable(disable);
        spinVoice.setDisable(disable);
        if (disable) {
            imgHair.setImage(null);
            imgBuild.setImage(null);
            imgHairColor.setImage(null);
        }
        //Profile data
        disableProfileFields(disable);
    }

    /*
    Sets an Einherjar ID from the combobox
     */
    public void setLogIdToCard() {
        int selected = comboCard.getSelectionModel().getSelectedIndex();
        if (selected > 120) {
            selected -= 121;
            selected = LogBlock.DLC_LOG_ID[selected];
        }
        txtLogId.setText(Integer.toHexString(selected));
    }

    /*
    Gets the sprites from the resources to display the avatar portrait
     */
    public void displayImage() {
        Image[] images = Portrait.setImage(unit);
        if (images[0] != null) imgBuild.setImage(images[0]);
        else imgBuild.setImage(null);
        if (images[1] != null) imgHairColor.setImage(images[1]);
        else imgHairColor.setImage(null);
        if (images[2] != null) imgHair.setImage(images[2]);
        else imgHair.setImage(null);
        FireEditor.unitController.setImage();
    }

    private void addListeners() {
        setupSpinners(spinBuild);
        setupSpinners(spinHair);
        setupSpinners(spinFace);
        setupSpinners(spinVoice);
        setupCheckboxBuild(checkCard);
        setupCheckboxBuild(checkGender);
        setupCombobox(comboAsset);
        setupCombobox(comboFlaw);
        colorPickerHair.valueProperty().addListener((observable, oldColor, newColor) -> {
            updateBuild();
            displayImage();
        });
        txtLogId.textProperty().addListener((observable, oldValue, newValue) -> {
            updateBuild();
            displayImage();
        });
        //Names and profile messages
        setupTextfields(txtName);
        UI.setTextField(txtName, unit.rawLog.NAME_CHARACTERS);
        setupTextfields(txtProfile);
        setupTextfields(txtChallenge);
        setupTextfields(txtGreeting);
        setupTextfields(txtRecruit);
        UI.setTextField(txtProfile, unit.rawLog.MESSAGE_CHARACTERS);
        UI.setTextField(txtChallenge, unit.rawLog.MESSAGE_CHARACTERS);
        UI.setTextField(txtGreeting, unit.rawLog.MESSAGE_CHARACTERS);
        UI.setTextField(txtRecruit, unit.rawLog.MESSAGE_CHARACTERS);
        //Profile options
        setupCombobox(comboDifficulty);
        setupCheckboxFlag(checkGameCasual, 0);
        setupCheckboxFlag(checkGameLunaplus, 1);
        setupCheckboxFlag(checkGameBeaten, 2);
        setupCheckboxFlag(checkGameHidden, 3);
        setupCombobox(comboClass);
        setupProfileSpinners(spinExpression);
        setupProfileSpinners(spinHome);
        setupProfileSpinners(spinTrait);
        setupProfileSpinners(spinValues);
        setupProfileSpinners(spinIdentity);
        setupProfileSpinners(spinDay);
        setupProfileSpinners(spinMonth);
        //S-Pairings
        setupComboPairingSlot();
        setupComboHusband();
        setupComboWife();
        comboPairingSlot.getSelectionModel().select(0);
        listenersAdded = true;
    }

    private void setupSpinners(Spinner<Integer> spinner) {
        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                updateBuild();
                displayImage();
            }
        });
    }

    private void setupCheckboxBuild(CheckBox checkbox) {
        ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                updateBuild();
                displayImage();
            }
        };
        checkbox.selectedProperty().addListener(listener);
    }

    private void setupCheckboxFlag(CheckBox checkBox, int slot) {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            unit.rawLog.setGameModeFlag(slot, checkBox.isSelected());
        });
    }

    private void setupCombobox(ComboBox<String> comboBox) {
        comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                unit.rawLog.setAsset(comboAsset.getSelectionModel().getSelectedIndex());
                unit.rawLog.setFlaw(comboFlaw.getSelectionModel().getSelectedIndex());
                unit.rawLog.setProfileCard(comboClass.getSelectionModel().getSelectedIndex(), 0);
                unit.rawLog.setDifficulty(comboDifficulty.getSelectionModel().getSelectedIndex());
                FireEditor.unitController.setFieldsStats(unit);
            }
        });
    }
    /*
    PROFILE CARD CODE
     */

    @FXML
    private Spinner<Integer> spinExpression, spinTrait, spinHome, spinIdentity, spinValues,
            spinDay, spinMonth;
    @FXML
    private ComboBox<String> comboClass, comboPairingSlot, comboWife, comboHusband;
    @FXML
    private TextField txtProfile, txtGreeting, txtChallenge, txtRecruit;

    private void loadProfileFields() {
        comboDifficulty.getSelectionModel().select(unit.rawLog.difficulty());
        checkGameCasual.setSelected(unit.rawLog.gameModeFlag(0));
        checkGameLunaplus.setSelected(unit.rawLog.gameModeFlag(1));
        checkGameBeaten.setSelected(unit.rawLog.gameModeFlag(2));
        checkGameHidden.setSelected(unit.rawLog.gameModeFlag(3));
        spinDay.getValueFactory().setValue(unit.rawLog.getBirthday()[0]);
        spinMonth.getValueFactory().setValue(unit.rawLog.getBirthday()[1]);
        spinExpression.getValueFactory().setValue(unit.rawLog.getProfileCard()[1]);
        spinTrait.getValueFactory().setValue(unit.rawLog.getProfileCard()[2]);
        spinHome.getValueFactory().setValue(unit.rawLog.getProfileCard()[3]);
        spinIdentity.getValueFactory().setValue(unit.rawLog.getProfileCard()[4]);
        spinValues.getValueFactory().setValue(unit.rawLog.getProfileCard()[5]);
        comboClass.getSelectionModel().select(unit.rawLog.getProfileCard()[0]);
        txtProfile.setText(unit.rawLog.getTextStreet());
        txtChallenge.setText(unit.rawLog.getTextChallenge());
        txtRecruit.setText(unit.rawLog.getTextRecruit());
        txtGreeting.setText(unit.rawLog.getTextGreeting());
    }

    private void setupComboPairingSlot() {
        comboPairingSlot.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null && unit.rawLog != null) {
                //The previous slot is updated
                int slot = (int) newValue;
                comboWife.getSelectionModel().select(unit.rawLog.getPairing(slot)[0]);
                comboHusband.getSelectionModel().select(unit.rawLog.getPairing(slot)[1]);
            }
        });
    }

    private void setupComboHusband() {
        comboWife.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null && unit.rawLog != null) {
                int slot = comboPairingSlot.getSelectionModel().getSelectedIndex();
                unit.rawLog.setPairingMale(slot, comboWife.getSelectionModel().getSelectedIndex());
            }
        });
    }

    private void setupComboWife() {
        comboHusband.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null && unit.rawLog != null) {
                int slot = comboPairingSlot.getSelectionModel().getSelectedIndex();
                unit.rawLog.setPairingFemale(slot, comboHusband.getSelectionModel().getSelectedIndex());
            }
        });
    }

    //Updates all the texts
    private void setupTextfields(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            unit.rawLog.setName(txtName.getText());
            unit.rawLog.setTextStreet(txtProfile.getText());
            unit.rawLog.setTextGreeting(txtGreeting.getText());
            unit.rawLog.setTextChallenge(txtChallenge.getText());
            unit.rawLog.setTextRecruit(txtRecruit.getText());
            FireEditor.unitController.refreshName(unit);
        });
    }

    //Updates the profile card options
    private void setupProfileSpinners(Spinner<Integer> spinner) {
        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                //unit.rawLog.setProfileCard(comboClass.getSelectionModel().getSelectedIndex(), 0);
                unit.rawLog.setProfileCard(spinExpression.getValue(), 1);
                unit.rawLog.setProfileCard(spinTrait.getValue(), 2);
                unit.rawLog.setProfileCard(spinHome.getValue(), 3);
                unit.rawLog.setProfileCard(spinIdentity.getValue(), 4);
                unit.rawLog.setProfileCard(spinValues.getValue(), 5);
                unit.rawLog.setBirthday(spinDay.getValue(), spinMonth.getValue());
            }
        });
    }

    private void disableProfileFields(boolean disable) {
        comboDifficulty.setDisable(disable);
        checkGameCasual.setDisable(disable);
        checkGameLunaplus.setDisable(disable);
        checkGameBeaten.setDisable(disable);
        checkGameHidden.setDisable(disable);
        spinDay.setDisable(disable);
        spinMonth.setDisable(disable);
        spinExpression.setDisable(disable);
        spinTrait.setDisable(disable);
        spinHome.setDisable(disable);
        spinIdentity.setDisable(disable);
        spinValues.setDisable(disable);
        comboClass.setDisable(disable);
        txtProfile.setDisable(disable);
        txtChallenge.setDisable(disable);
        txtRecruit.setDisable(disable);
        txtGreeting.setDisable(disable);
        comboPairingSlot.setDisable(disable);
        comboWife.setDisable(disable);
        comboHusband.setDisable(disable);
    }
}
