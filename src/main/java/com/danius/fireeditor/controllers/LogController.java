package com.danius.fireeditor.controllers;

import com.danius.fireeditor.savefile.units.SkillLogic;
import com.danius.fireeditor.savefile.units.Stats;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.extrablock.LogBlock;
import com.danius.fireeditor.util.Hex;
import com.danius.fireeditor.util.Names;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.io.IOException;

public class LogController {
    private boolean listenersAdded = false;
    private Unit unit;
    private boolean isWest;
    @FXML
    private ComboBox<String> comboCard, comboAsset, comboFlaw;
    @FXML
    private Button btnSetLogId;
    @FXML
    private Spinner<Integer> spinBuild, spinFace, spinHair, spinVoice;
    @FXML
    private TextField txtLogId, txtName;
    @FXML
    private CheckBox checkGender, checkCard;
    @FXML
    private ColorPicker colorPickerHair;
    @FXML
    private ImageView imgHair, imgHairColor, imgBuild;

    public void initialize() {
        UI.setSpinnerNumeric(spinBuild, 2); //0-2
        UI.setSpinnerNumeric(spinFace, 4 + 16); //0-4 + DLC
        UI.setSpinnerNumeric(spinHair, 4); //0-4
        UI.setSpinnerNumeric(spinVoice, 2); //0-2
        txtLogId.setText("0");
        UI.setHexTextField(txtLogId, 26);
        //Profile Card
        UI.setSpinnerNumeric(spinDifficulty, 2);
        UI.setSpinnerNumeric(spinPenalty, 255);
        UI.setSpinnerNumeric(spinExpression, 255);
        UI.setSpinnerNumeric(spinTrait, 255);
        UI.setSpinnerNumeric(spinHome, 255);
        UI.setSpinnerNumeric(spinValues, 255);
        UI.setSpinnerNumeric(spinIdentity, 255);
        UI.setSpinnerNumeric(spinDay, 31);
        UI.setSpinnerNumeric(spinMonth, 12);
        //Comboboxes
        //Einherjar names
        ObservableList<String> cardUnits = FXCollections.observableArrayList();
        cardUnits.addAll(Names.spotPassNames);
        cardUnits.addAll(Names.dlcNames);
        comboCard.setItems(cardUnits);
        comboCard.getSelectionModel().select(0);
        //Asset and flaw
        ObservableList<String> modifiers = FXCollections.observableArrayList();
        modifiers.addAll(Names.modifNames);
        comboAsset.setItems(modifiers);
        comboFlaw.setItems(modifiers);
        //Class
        ObservableList<String> classes = FXCollections.observableArrayList();
        classes.addAll(Names.classNames);
        comboClass.setItems(classes);
        //S-Pairings
        ObservableList<String> units = FXCollections.observableArrayList();
        units.addAll(Names.unitNames);
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
        if (unit.hasLogBlock) {
            //unit.rawLog.changeRegion(isWest);
            disableFields(false);
            loadFields();
            loadProfileFields();
            addListeners();
            displayImage();
        }
    }

    public void addLogData() throws IOException {
        if (!unit.hasLogBlock) {
            String unitName = unit.unitName();
            this.unit.addBlockLog();
            unit.rawLog.changeRegion(isWest);
            unit.rawLog.setName(unitName);
            unit.rawLog.setAsset(0);
            unit.rawLog.setFlaw(0);
            unit.rawLog.setHairColor(unit.rawBlockEnd.getHairColor());
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
        if (unit.hasLogBlock) {
            this.unit.removeBlockExtra();
            disableFields(true);
        }
    }

    public void loadFields() {
        //Other
        comboAsset.getSelectionModel().select(unit.rawLog.getAssetFlaw()[0]);
        comboFlaw.getSelectionModel().select(unit.rawLog.getAssetFlaw()[1]);
        colorPickerHair.setValue(Hex.hexToColor(unit.rawLog.getLogHairColor()));
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

    private void updateBuild() {
        unit.rawLog.setHairColor(Hex.colorToHex(colorPickerHair.getValue()));
        unit.rawLog.setToCard(checkCard.isSelected());
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
        //The values are gotten
        int build = unit.rawLog.getFullBuild()[0];
        int face = unit.rawLog.getFullBuild()[1];
        int hair = unit.rawLog.getFullBuild()[2];
        boolean female = (unit.rawLog.getFullBuild()[4] > 0);
        //PRIORITY ORDER
        //DLC Units (Eldigan will be considered SpotPass)
        if (unit.rawLog.hasFaceDlc()) {
            String path = "/com/danius/fireeditor/dlc/dlc_" + face + ".png";
            Image buildSprite = new Image(getClass().getResourceAsStream(path));
            imgHair.setImage(null);
            imgHairColor.setImage(null);
            imgBuild.setImage(buildSprite);
        }
        //Regular Avatar
        else if (!unit.rawLog.isEinherjar() && face <= 0x4) {
            //Path
            String path = "/com/danius/fireeditor/avatar_";
            if (female) path += "f/";
            else path += "m/";
            //Build sprite
            String buildPath = path + "build_0" + build + "_0" + face + ".png";
            Image buildSprite = new Image(getClass().getResourceAsStream(buildPath));
            imgBuild.setImage(buildSprite);
            //Hair sprite
            String hairPath = path + "hair_0" + build + "_0" + hair + ".png";
            Image hairSprite = new Image(getClass().getResourceAsStream(hairPath));
            imgHair.setImage(hairSprite);
            //Hair color
            String backPath = path + "back_0" + build + "_0" + hair + ".png";
            Image backSprite = new Image(getClass().getResourceAsStream(backPath));
            String hexColor = "#" + unit.rawLog.getLogHairColor();
            imgHairColor.setImage(fillImageWithColor(backSprite, hexColor));
        }
        //SpotPass Units
        else if (unit.rawLog.isEinherjar() && unit.rawLog.hasEinherjarId()) {
            int logId = unit.rawLog.getLogIdLastByte();
            String path = "/com/danius/fireeditor/spotpass/" + logId + ".png";
            imgHair.setImage(null);
            imgHairColor.setImage(null);
            Image sprite = new Image(getClass().getResourceAsStream(path));
            imgBuild.setImage(sprite);
        }
        //Invalid
        else {
            String path = "/com/danius/fireeditor/spotpass/placeholder.png";
            imgHair.setImage(null);
            imgHairColor.setImage(null);
            Image sprite = new Image(getClass().getResourceAsStream(path));
            imgBuild.setImage(sprite);
        }
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
        setupCombobox(comboClass);
        setupProfileSpinners(spinExpression);
        setupProfileSpinners(spinHome);
        setupProfileSpinners(spinTrait);
        setupProfileSpinners(spinValues);
        setupProfileSpinners(spinIdentity);
        setupProfileSpinners(spinDay);
        setupProfileSpinners(spinMonth);
        setupProfileSpinners(spinDifficulty);
        setupProfileSpinners(spinPenalty);
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

    private void setupCombobox(ComboBox<String> comboBox) {
        comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                unit.rawLog.setAsset(comboAsset.getSelectionModel().getSelectedIndex());
                unit.rawLog.setFlaw(comboFlaw.getSelectionModel().getSelectedIndex());
                unit.rawLog.setProfileCard(comboClass.getSelectionModel().getSelectedIndex(), 0);
            }
        });
    }

    /*
    Changes the color of the hair color sprite
     */
    public static Image fillImageWithColor(Image image, String hexColor) {
        // Convert hex color to JavaFX Color
        Color fillColor = Color.web(hexColor);
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        // Create a writable image with the same dimensions as the original image
        WritableImage filledImage = new WritableImage(width, height);
        // Get the pixel reader for the original image
        PixelReader pixelReader = image.getPixelReader();
        // Get the pixel writer for the filled image
        PixelWriter pixelWriter = filledImage.getPixelWriter();
        // Fill the image with the specified color
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = pixelReader.getColor(x, y);
                if (pixelColor.isOpaque()) {
                    // Use the fill color for opaque pixels
                    pixelWriter.setColor(x, y, fillColor);
                } else {
                    // Preserve transparency for transparent pixels
                    pixelWriter.setColor(x, y, pixelColor);
                }
            }
        }
        return filledImage;
    }


    /*
    PROFILE CARD CODE
     */

    @FXML
    private Spinner<Integer> spinExpression, spinTrait, spinHome, spinIdentity, spinValues,
            spinDifficulty, spinPenalty, spinDay, spinMonth;
    @FXML
    private ComboBox<String> comboClass, comboPairingSlot, comboWife, comboHusband;
    @FXML
    private TextField txtProfile, txtGreeting, txtChallenge, txtRecruit;

    private void loadProfileFields() {
        spinDifficulty.getValueFactory().setValue(unit.rawLog.difficulty());
        spinPenalty.getValueFactory().setValue(unit.rawLog.penalty());
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
            if (newValue != null && unit != null && unit.hasLogBlock) {
                //The previous slot is updated
                int slot = (int) newValue;
                comboWife.getSelectionModel().select(unit.rawLog.getPairing(slot)[0]);
                comboHusband.getSelectionModel().select(unit.rawLog.getPairing(slot)[1]);
            }
        });
    }

    private void setupComboHusband() {
        comboWife.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null && unit.hasLogBlock) {
                int slot = comboPairingSlot.getSelectionModel().getSelectedIndex();
                unit.rawLog.setPairingMale(slot, comboWife.getSelectionModel().getSelectedIndex());
            }
        });
    }

    private void setupComboWife() {
        comboHusband.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unit != null && unit.hasLogBlock) {
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
                unit.rawLog.setPenalty(spinPenalty.getValue());
                unit.rawLog.setDifficulty(spinDifficulty.getValue());
            }
        });
    }

    private void disableProfileFields(boolean disable) {
        spinDifficulty.setDisable(disable);
        spinPenalty.setDisable(disable);
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
