package com.danius.fireeditor.controllers.user;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.controllers.UI;
import com.danius.fireeditor.data.ChapterDb;
import com.danius.fireeditor.savefile.map.GmapBlock;
import com.danius.fireeditor.savefile.map.RawMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import static com.danius.fireeditor.data.ClassDb.*;

public class MapController {

    @FXML
    private Spinner<Integer> spinTime1, spinTime2, spinPool1, spinPool2, spinWireless1, spinWireless2;
    @FXML
    private ComboBox<String> comboEncounter1, comboEncounter2, comboClass1, comboClass2;
    @FXML
    private TextField txtSeed1, txtSeed2;
    @FXML
    private ListView<String> chapterList;
    @FXML
    private RadioButton radioLock, radioUnlock, radioBeaten;

    private ToggleGroup toggleState;


    private GmapBlock gmapBlock;

    public void initialize() {
        UI.setSpinnerNumeric(spinTime1, 255);
        UI.setSpinnerNumeric(spinTime2, 255);
        UI.setSpinnerNumeric(spinPool1, 255);
        UI.setSpinnerNumeric(spinPool2, 255);
        UI.setSpinnerNumeric(spinWireless1, 255);
        UI.setSpinnerNumeric(spinWireless2, 255);
        UI.setHexTextField(txtSeed1, 8);
        UI.setHexTextField(txtSeed2, 8);

        toggleState = new ToggleGroup();
        radioLock.setToggleGroup(toggleState);
        radioBeaten.setToggleGroup(toggleState);
        radioUnlock.setToggleGroup(toggleState);

        ObservableList<String> classes = FXCollections.observableArrayList();
        classes.add("None");
        classes.addAll(getClassNames(FireEditor.chapterFile.MAX_ID_CLASS));
        comboClass1.setItems(classes);
        comboClass2.setItems(classes);
        ObservableList<String> groups = FXCollections.observableArrayList(
                "Empty", "Risen", "Merchant", "Wireless Team");
        comboEncounter1.setItems(groups);
        comboEncounter2.setItems(groups);

        chapterList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadFields(gmapBlock.maps.get(chapterList.getSelectionModel().getSelectedIndex()));
            }
        });
    }

    public void setBlock(GmapBlock gmapBlock) {
        this.gmapBlock = gmapBlock;
        ObservableList<String> chaptersMap = FXCollections.observableArrayList();
        chaptersMap.setAll(ChapterDb.getOverWorldNames());
        chapterList.setItems(FXCollections.observableArrayList(chaptersMap));
        chapterList.getSelectionModel().select(0);
        setListeners();
    }

    public void loadFields(RawMap map) {
        //Encounter type
        if (map.isRisen(0)) comboEncounter1.getSelectionModel().select(1);
        else if (map.isMerchant(0)) comboEncounter1.getSelectionModel().select(2);
        else if (map.isWireless(0)) comboEncounter1.getSelectionModel().select(3);
        else comboEncounter1.getSelectionModel().select(0);
        if (map.isRisen(1)) comboEncounter2.getSelectionModel().select(1);
        else if (map.isMerchant(1)) comboEncounter2.getSelectionModel().select(2);
        else if (map.isWireless(1)) comboEncounter2.getSelectionModel().select(3);
        else comboEncounter2.getSelectionModel().select(0);
        //Encounter data
        spinTime1.getValueFactory().setValue(map.getTimeOut(0));
        spinTime2.getValueFactory().setValue(map.getTimeOut(1));
        spinPool1.getValueFactory().setValue(map.getPool(0));
        spinPool2.getValueFactory().setValue(map.getPool(1));
        spinWireless1.getValueFactory().setValue(map.getWirelessId(0));
        spinWireless2.getValueFactory().setValue(map.getWirelessId(1));
        txtSeed1.setText(map.getSeed(0));
        txtSeed2.setText(map.getSeed(1));
        //NPC Class
        int class1 = map.getUnitClass(0);
        int class2 = map.getUnitClass(1);
        if (class1 == 65535) class1 = -1;
        if (class2 == 65535) class2 = -1;
        comboClass1.getSelectionModel().select(class1 + 1);
        comboClass2.getSelectionModel().select(class2 + 1);
        //Unlock
        int state = map.lockState();
        if (state == 1) radioBeaten.fire();
        else if (state == 2) radioUnlock.fire();
        else radioLock.fire();
    }

    //What is this
    public void setListeners() {
        spinTime1.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            int slot = chapterList.getSelectionModel().getSelectedIndex();
            spinTime1.increment(0);
            gmapBlock.maps.get(slot).setTimeOut(0, spinTime1.getValue());
        });
        spinTime2.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            int slot = chapterList.getSelectionModel().getSelectedIndex();
            spinTime2.increment(0);
            gmapBlock.maps.get(slot).setTimeOut(1, spinTime2.getValue());
        });
        spinPool1.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            int slot = chapterList.getSelectionModel().getSelectedIndex();
            spinPool1.increment(0);
            gmapBlock.maps.get(slot).setPool(0, spinPool1.getValue());
        });
        spinPool2.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            int slot = chapterList.getSelectionModel().getSelectedIndex();
            spinPool2.increment(0);
            gmapBlock.maps.get(slot).setPool(1, spinPool2.getValue());
        });
        spinWireless1.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            int slot = chapterList.getSelectionModel().getSelectedIndex();
            spinWireless1.increment(0);
            gmapBlock.maps.get(slot).setWirelessId(0, spinWireless1.getValue());
        });
        spinWireless2.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            int slot = chapterList.getSelectionModel().getSelectedIndex();
            spinWireless2.increment(0);
            gmapBlock.maps.get(slot).setWirelessId(1, spinWireless2.getValue());
        });
        txtSeed1.textProperty().addListener((observable, oldValue, newValue) -> {
            int slot = chapterList.getSelectionModel().getSelectedIndex();
            gmapBlock.maps.get(slot).setSeed(0, txtSeed1.getText());
        });
        txtSeed2.textProperty().addListener((observable, oldValue, newValue) -> {
            int slot = chapterList.getSelectionModel().getSelectedIndex();
            gmapBlock.maps.get(slot).setSeed(1, txtSeed2.getText());
        });
        comboClass1.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int slot = chapterList.getSelectionModel().getSelectedIndex();
                    if (slot == -1) return;
                    int class1 = comboClass1.getSelectionModel().getSelectedIndex() - 1;
                    if (class1 == -1) class1 = 0xFFFF;
                    gmapBlock.maps.get(slot).setUnitClass(0, class1);
                }
        );
        comboClass2.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int slot = chapterList.getSelectionModel().getSelectedIndex();
                    if (slot == -1) return;
                    int class2 = comboClass2.getSelectionModel().getSelectedIndex() - 1;
                    if (class2 == -1) class2 = 0xFFFF;
                    gmapBlock.maps.get(slot).setUnitClass(1, class2);
                }
        );
        toggleState.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            int slot = chapterList.getSelectionModel().getSelectedIndex();
            if (newValue == radioLock) {
                gmapBlock.maps.get(slot).setLockState(0);
            } else if (newValue == radioBeaten) {
                gmapBlock.maps.get(slot).setLockState(1);
            } else if (newValue == radioUnlock) {
                gmapBlock.maps.get(slot).setLockState(2);
            }
        });
        comboEncounter1.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int slot = chapterList.getSelectionModel().getSelectedIndex();
                    if (slot == -1) return;
                    int selection = comboEncounter1.getSelectionModel().getSelectedIndex();
                    gmapBlock.maps.get(slot).setEncounter(0, selection);
                    loadFields(gmapBlock.maps.get(slot));
                }
        );
        comboEncounter2.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int slot = chapterList.getSelectionModel().getSelectedIndex();
                    if (slot == -1) return;
                    int selection = comboEncounter2.getSelectionModel().getSelectedIndex();
                    gmapBlock.maps.get(slot).setEncounter(1, selection);
                    loadFields(gmapBlock.maps.get(slot));
                }
        );
    }
}
