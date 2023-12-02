package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.global.Global;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.wireless.DuTeam;
import com.danius.fireeditor.savefile.wireless.UnitDu;
import com.danius.fireeditor.util.Portrait;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class GlobalController {
    private Global global;
    @FXML
    private ListView<UnitDu> listViewUnit;
    @FXML
    private Label lblUnitCount, labelUnitName;
    @FXML
    private ImageView imgBuild, imgHairColor, imgHair;
    @FXML
    private Spinner<Integer> spinRenown;
    @FXML
    private CheckBox check1, check2, check3;

    public void initialize() {
        FireEditor.globalController = this;
        UI.setSpinnerNumeric(spinRenown, 99999);
        imgBuild.setImage(null);
        imgHair.setImage(null);
        imgHairColor.setImage(null);
        labelUnitName.setText("");
        //Load the save file
        loadFile();
        //Unit List Listeners
        listViewUnit.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(UnitDu unit, boolean empty) {
                super.updateItem(unit, empty);
                if (empty || unit == null) {
                    setText(null);
                } else {
                    setText(unit.toString());
                }
            }
        });
        listViewUnit.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateUnits();
                setPortrait();
            }
        });
        spinRenown.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && FireEditor.global != null) {
                spinRenown.increment(0);
                global.glUserBlock.setRenown(Integer.parseInt(newValue));
            }
        });
        setCheckboxFlag(check1, 1);
        setCheckboxFlag(check2, 2);
        setCheckboxFlag(check3, 3);
    }

    public void loadFile() {
        if (FireEditor.global != null && FireEditor.globalController != null) {
            this.global = FireEditor.global;
            listViewUnit.setItems(FXCollections.observableArrayList(global.glUnitBlock.unitList));
            listViewUnit.getSelectionModel().selectLast();
            listViewUnit.getSelectionModel().selectFirst();
            lblUnitCount.setText("Logbook Avatars: " + global.glUnitBlock.unitList.size() + "/99");
            check1.setSelected(global.glUserBlock.hasGlobalFlag(1));
            check2.setSelected(global.glUserBlock.hasGlobalFlag(2));
            check3.setSelected(global.glUserBlock.hasGlobalFlag(3));
            spinRenown.getValueFactory().setValue(global.glUserBlock.getRenown());
            setPortrait();
        }
    }

    public void setCheckboxFlag(CheckBox checkBox, int bit) {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (FireEditor.global != null) {
                global.glUserBlock.setGlobalFlag(bit, checkBox.isSelected());
            }
        });
    }

    public void updateUnits() {
        global.glUnitBlock.unitList = listViewUnit.getItems();
    }

    public void orderUp() {
        int selectedIndex = listViewUnit.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0) {
            UnitDu selectedItem = listViewUnit.getSelectionModel().getSelectedItem();
            listViewUnit.getItems().remove(selectedIndex);
            listViewUnit.getItems().add(selectedIndex - 1, selectedItem);
            listViewUnit.getSelectionModel().select(selectedIndex - 1);
        }
    }

    public void orderDown() {
        int selectedIndex = listViewUnit.getSelectionModel().getSelectedIndex();
        int itemCount = listViewUnit.getItems().size();

        if (selectedIndex >= 0 && selectedIndex < itemCount - 1) {
            UnitDu selectedItem = listViewUnit.getSelectionModel().getSelectedItem();
            listViewUnit.getItems().remove(selectedIndex);
            listViewUnit.getItems().add(selectedIndex + 1, selectedItem);
            listViewUnit.getSelectionModel().select(selectedIndex + 1);
        }
    }

    public void setPortrait() {
        UnitDu unitDu = listViewUnit.getSelectionModel().getSelectedItem();
        if (unitDu != null) {
            Unit unit = unitDu.toUnit();
            Image[] portrait = Portrait.setImage(unit);
            imgBuild.setImage(portrait[0]);
            imgHairColor.setImage(portrait[1]);
            imgHair.setImage(portrait[2]);
            labelUnitName.setText(unit.unitName());
        } else {
            imgBuild.setImage(null);
            imgHairColor.setImage(null);
            imgHair.setImage(null);
            labelUnitName.setText("");
        }
    }

    public void changeRegion() {
        boolean isCurrentWest = global.glUserBlock.avatarMale.isWest;
        String originalRegion = (isCurrentWest) ? "US/Europe" : "Japan";
        String targetRegion = (isCurrentWest) ? "Japan" : "US/Europe";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Change Region");
        alert.setHeaderText("Current region: " + originalRegion + "\n" +
                "The save file will be changed to " + targetRegion);
        alert.setContentText("Note: The name of the Einherjar Units from the Avatar Logbook will be modified.");
        // Add Confirm and Cancel buttons
        ButtonType confirmButton = new ButtonType("Confirm");
        ButtonType cancelButton = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        // Show the dialog and wait for a response
        alert.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                FireEditor.global.changeRegion(!isCurrentWest);
                FireEditor.mainController.reloadGlobal(FireEditor.global.getBytes());
            } else if (response == cancelButton) {
                return;
            }
        });
    }

    public void unlockSupports() {
        global.glUserBlock.fullSupportLog();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("All the Support Log and Unit Gallery entries have been unlocked!");
        alert.showAndWait();
    }

}
