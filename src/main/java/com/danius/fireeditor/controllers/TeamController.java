package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.other.GmapBlock;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.wireless.Du26Block;
import com.danius.fireeditor.savefile.wireless.DuTeam;
import com.danius.fireeditor.savefile.wireless.UnitDu;
import com.danius.fireeditor.util.Hex;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TeamController {

    private Du26Block du26Block;
    private GmapBlock gmapBlock;
    @FXML
    private TextField txtTeamName;
    @FXML
    private Label labelUnitName;
    @FXML
    private ListView<DuTeam> teamList;
    @FXML
    private ListView<UnitDu> unitList;

    public void initialize() {

    }

    public void setBlocks(Du26Block du26Block, GmapBlock gmapBlock) {
        this.du26Block = du26Block;
        this.gmapBlock = gmapBlock;
        ObservableList<DuTeam> observableTeamList = FXCollections.observableArrayList();
        observableTeamList.add(du26Block.playerTeam);
        observableTeamList.addAll(du26Block.teamList);
        teamList.setItems(observableTeamList);

        teamList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateTeamBlock();
                loadTeamValues();
            }
        });

        unitList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                //The units are updated
                teamList.getSelectionModel().getSelectedItem().unitList = unitList.getItems();
                updateTeamBlock();
                loadUnitValues();
            }
        });

        teamList.getSelectionModel().select(0);
    }

    private void loadTeamValues() {
        txtTeamName.setText(teamList.getSelectionModel().getSelectedItem().getTeamName());
        ObservableList<UnitDu> units = FXCollections.observableArrayList();
        units.addAll(teamList.getSelectionModel().getSelectedItem().unitList);
        unitList.setItems(units);
        unitList.getSelectionModel().select(0);
    }

    public void loadUnitValues() {
        UnitDu unitDu = unitList.getSelectionModel().getSelectedItem();
        labelUnitName.setText(unitDu.getName());
    }

    public void test() {
        Hex.writeFile(unitList.getSelectionModel().getSelectedItem().bytes(), "units");
    }

    public void updateTeamBlock() {
        //The teams are updated
        List<DuTeam> wireless = new ArrayList<>();
        for (int i = 1; i < teamList.getItems().size(); i++) {
            wireless.add(teamList.getItems().get(i));
        }
        du26Block.teamList = wireless;
        du26Block.playerTeam = teamList.getItems().get(0);
    }

    public void moveUnit() {
        if (FireEditor.unitController.listViewUnit.getItems().size() >= Constants.UNIT_LIMIT) return;

        ObservableList<UnitDu> list = unitList.getItems();
        UnitDu selectedUnit = unitList.getSelectionModel().getSelectedItem();
        int index = unitList.getSelectionModel().getSelectedIndex();

        if (selectedUnit != null) {
            selectedUnit.updateUnit();
            Unit unit = new Unit(selectedUnit.unit.getUnitBytes());
            unit.unitDu = selectedUnit;
            FireEditor.unitController.addUnit(unit);
            list.remove(index);
            unitList.setItems(list); // You can directly set the updated list

            // If the list is empty after deletion, clear the selection
            if (list.isEmpty()) {
                unitList.getSelectionModel().clearSelection();
            }
        }
    }
}
