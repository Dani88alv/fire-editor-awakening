package com.danius.fireeditor.controllers.user;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.controllers.MainController;
import com.danius.fireeditor.controllers.UI;
import com.danius.fireeditor.data.ChapterDb;
import com.danius.fireeditor.data.ItemDb;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.inventory.Refinement;
import com.danius.fireeditor.savefile.inventory.TranBlock;
import com.danius.fireeditor.savefile.map.GmapBlock;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.wireless.Du26Block;
import com.danius.fireeditor.savefile.wireless.DuItem;
import com.danius.fireeditor.savefile.wireless.DuTeam;
import com.danius.fireeditor.savefile.wireless.UnitDu;
import com.danius.fireeditor.util.Portrait;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TeamController {

    private Du26Block du26Block;
    private GmapBlock gmapBlock;
    @FXML
    private ImageView imgBuild, imgHairColor, imgHair;
    @FXML
    private TextField txtTeamName, txtSlot;
    @FXML
    private Label labelUnitName, lblTeamCount;
    @FXML
    private ListView<DuTeam> teamList;
    @FXML
    private ListView<UnitDu> unitListview;
    @FXML
    private Spinner<Integer> spinRenown, spinSprite;
    @FXML
    private CheckBox check0, check1, check2, check3, check4;
    @FXML
    private Button btnUp, btnDown, btnMove, btnDeleteTeam, btnSetLocation;
    @FXML
    private ComboBox<String> comboMap;

    public void initialize() {
        UI.setSpinnerNumeric(spinRenown, 99999);
        UI.setSpinnerNumeric(spinSprite, 65535);
    }

    public void setBlocks(Du26Block du26Block, GmapBlock gmapBlock) {
        this.du26Block = du26Block;
        this.gmapBlock = gmapBlock;
        ObservableList<DuTeam> observableTeamList = FXCollections.observableArrayList();
        observableTeamList.add(du26Block.playerTeam);
        observableTeamList.addAll(du26Block.teamList);
        teamList.setItems(observableTeamList);

        ObservableList<String> chaptersMap = FXCollections.observableArrayList();
        chaptersMap.setAll(ChapterDb.getOverWorldNames());
        comboMap.setItems(FXCollections.observableArrayList(chaptersMap));
        comboMap.getSelectionModel().select(0);

        setListeners();

        teamList.getSelectionModel().select(0);
    }

    public void setListeners() {
        //Team Renown
        spinRenown.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && teamList.getSelectionModel().getSelectedItem() != null) {
                spinRenown.increment(0);
                teamList.getSelectionModel().getSelectedItem().setRenown(spinRenown.getValue());
            }
        });
        //Team Name
        txtTeamName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && teamList.getSelectionModel().getSelectedItem() != null) {
                teamList.getSelectionModel().getSelectedItem().setName(txtTeamName.getText());
            }
        });
        //Unit flags
        CheckBox[] checkList = new CheckBox[]{check0, check1, check2, check3, check4};
        for (int i = 0; i < checkList.length; i++) {
            int finalI = i;
            checkList[i].selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (unitListview.getSelectionModel().getSelectedItem() != null) {
                    unitListview.getSelectionModel().getSelectedItem().setDuFlag(finalI, checkList[finalI].isSelected());
                    //Outrealm Flag Portrait Change
                    if (finalI == 1) {
                        setPortrait();
                    }
                }
            });
        }
        check0.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (unitListview.getSelectionModel().getSelectedItem() != null) {
                setPortrait();
            }
        });

        //Unit Sprite
        spinSprite.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && unitListview.getSelectionModel().getSelectedItem() != null) {
                spinSprite.increment(0);
                unitListview.getSelectionModel().getSelectedItem().setSprite(spinSprite.getValue());
            }
        });
        //Team List
        teamList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DuTeam team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setText(null);
                } else {
                    setText(team.toString());
                }
            }
        });
        teamList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                disableUnitFields(false);
                updateTeamBlock();
                loadTeamValues();
                disableUnitFields(unitListview.getItems().size() == 0);
                setPortrait();
            }
        });
        //Unit List
        unitListview.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                //The units are updated
                teamList.getSelectionModel().getSelectedItem().unitList = unitListview.getItems();
                updateTeamBlock();
                loadUnitValues();
                setPortrait();
            }
        });
    }

    private void disableUnitFields(boolean disable) {
        if (disable) labelUnitName.setText("");
        check0.setDisable(disable);
        check1.setDisable(disable);
        check2.setDisable(disable);
        check4.setDisable(disable);
        check3.setDisable(disable);
        btnDown.setDisable(disable);
        btnUp.setDisable(disable);
        btnMove.setDisable(disable);
        spinSprite.setDisable(disable);
        imgHair.setImage(null);
        imgBuild.setImage(null);
        imgHairColor.setImage(null);
    }

    private void loadTeamValues() {
        byte[] teamSlot = teamList.getSelectionModel().getSelectedItem().getSlot();
        String slot = (teamSlot.length > 0) ? String.valueOf(teamSlot[0]) : "";
        txtSlot.setText(slot);
        spinRenown.getValueFactory().setValue(teamList.getSelectionModel().getSelectedItem().getRenown());
        txtTeamName.setText(teamList.getSelectionModel().getSelectedItem().getTeamName());
        //Units
        ObservableList<UnitDu> units = FXCollections.observableArrayList();
        units.addAll(teamList.getSelectionModel().getSelectedItem().unitList);
        unitListview.setItems(units);
        unitListview.getSelectionModel().select(0);
        //Team Limit
        lblTeamCount.setText("Wireless Teams: " + (teamList.getItems().size() - 1) + "/" + Constants.TEAM_LIMIT);
        //Disable features not available to the player's team
        boolean isPlayer = teamList.getSelectionModel().getSelectedIndex() == 0;
        btnDeleteTeam.setDisable(isPlayer);
        btnSetLocation.setDisable(isPlayer);
        comboMap.setDisable(isPlayer);
        //Other
        if (!isPlayer) {
            int locatedMap = gmapBlock.teamLocation(teamSlot[0]);
            comboMap.getSelectionModel().select(locatedMap);
        }
    }

    @FXML
    private void setLocation() {
        int mapToWrite = comboMap.getSelectionModel().getSelectedIndex();
        if (mapToWrite == -1) return;
        int slot = teamList.getSelectionModel().getSelectedItem().getSlot()[0] & 0xFF;
        int unitClass = 4;
        if (teamList.getSelectionModel().getSelectedItem().unitList.size() > 0) {
            unitClass = teamList.getSelectionModel().getSelectedItem().unitList.get(0).getUnitClass();
        }
        gmapBlock.maps.get(mapToWrite).setWirelessEncounter(slot, unitClass);
    }

    public void loadUnitValues() {
        UnitDu unitDu = unitListview.getSelectionModel().getSelectedItem();
        check0.setSelected(unitDu.hasDuFlag(0));
        check1.setSelected(unitDu.hasDuFlag(1));
        check2.setSelected(unitDu.hasDuFlag(2));
        check3.setSelected(unitDu.hasDuFlag(3));
        check4.setSelected(unitDu.hasDuFlag(4));
        spinSprite.getValueFactory().setValue(unitDu.getSprite());
    }

    private void setPortrait() {
        UnitDu unitDu = unitListview.getSelectionModel().getSelectedItem();
        if (unitDu != null) {
            labelUnitName.setText(unitDu.getName());
            Unit unit = unitDu.toUnit();
            Image[] portrait = Portrait.setImage(unit);
            imgBuild.setImage(portrait[0]);
            imgHairColor.setImage(portrait[1]);
            imgHair.setImage(portrait[2]);
        } else {
            labelUnitName.setText("");
            imgBuild.setImage(null);
            imgHairColor.setImage(null);
            imgHair.setImage(null);
        }

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

        ObservableList<UnitDu> list = unitListview.getItems();
        UnitDu selectedUnit = unitListview.getSelectionModel().getSelectedItem();
        int index = unitListview.getSelectionModel().getSelectedIndex();

        if (selectedUnit != null) {
            Unit unit = selectedUnit.toUnit();

            //The items are checked
            for (int i = 0; i < selectedUnit.itemList.size(); i++) {
                DuItem duItem = selectedUnit.itemList.get(i);
                int itemId = duItem.getItemId();
                //If the current item is a refinement, store it in the inventory
                if (duItem.isRefinement()) {
                    Refinement refinement = duItem.toRefinement();
                    int position = FireEditor.chapterFile.blockRefi.getPosition(refinement);
                    //If it is in the expected range, store it
                    if (position < TranBlock.MAX_FORGED) {
                        refinement.setPosition(position);
                        refinement.setFlagEnemy(true);
                        //It is not in the inventory, add it
                        if (!FireEditor.chapterFile.blockRefi.isDuplicated(refinement)) {
                            FireEditor.chapterFile.blockRefi.refiList.add(refinement);
                            //Added to the listview
                            ObservableList<Refinement> refiList =
                                    FXCollections.observableArrayList(FireEditor.convoyController.listViewRefi.getItems());
                            refiList.add(refinement);
                            FireEditor.convoyController.listViewRefi.setItems(refiList);
                            FireEditor.convoyController.updateRefiCount();
                            //The uses are reset
                            FireEditor.chapterFile.blockTran.setForgedUses(refinement.position(), 0);
                        }
                        //If the position is valid, replace the item id with the inventory forged id
                        itemId = ItemDb.MOD_MAX_ID + position + 1;
                    }
                }
                //The item is replaced
                unit.rawInventory.items.get(i).setItemId(itemId);
            }

            FireEditor.unitController.addUnit(unit);
            list.remove(index);
            unitListview.setItems(list); // You can directly set the updated list

            // If the list is empty after deletion, clear the selection
            if (list.isEmpty()) {
                unitListview.getSelectionModel().clearSelection();
            }
            disableUnitFields(unitListview.getItems().size() == 0);
            setPortrait();
        }
    }

    @FXML
    public void orderUp() {
        int selectedIndex = unitListview.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0) {
            UnitDu selectedItem = unitListview.getSelectionModel().getSelectedItem();
            unitListview.getItems().remove(selectedIndex);
            unitListview.getItems().add(selectedIndex - 1, selectedItem);
            unitListview.getSelectionModel().select(selectedIndex - 1);
        }
    }

    @FXML
    public void orderDown() {
        int selectedIndex = unitListview.getSelectionModel().getSelectedIndex();
        int itemCount = unitListview.getItems().size();

        if (selectedIndex >= 0 && selectedIndex < itemCount - 1) {
            UnitDu selectedItem = unitListview.getSelectionModel().getSelectedItem();
            unitListview.getItems().remove(selectedIndex);
            unitListview.getItems().add(selectedIndex + 1, selectedItem);
            unitListview.getSelectionModel().select(selectedIndex + 1);
        }
    }

    public void exportTeam() {
        DuTeam team = teamList.getSelectionModel().getSelectedItem();
        if (team == null) return;
        //File chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(MainController.path));
        fileChooser.setInitialFileName(team.getTeamName());
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "Fire Emblem Awakening Team", "*" + ".du13");
        fileChooser.getExtensionFilters().add(extensionFilter);
        // Show save dialog
        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;
        MainController.path = file.getParent();
        // Save byte array to the selected file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(team.bytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void importTeam() {
        int maxTeams = Constants.TEAM_LIMIT;
        int currentCount = du26Block.teamList.size();
        if (currentCount >= maxTeams) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(MainController.path));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles == null || selectedFiles.isEmpty()) return;
        MainController.path = selectedFiles.get(0).getParent();

        List<Integer> usedIds = new ArrayList<>();
        for (DuTeam team : du26Block.teamList) {
            byte[] slot = team.getSlot();
            if (slot.length == 1) {
                usedIds.add((int) slot[0] & 0xFF);
            }
        }
        //The teams are set
        for (File file : selectedFiles) {
            try {
                //Same amounts as OverWorld maps
                if (currentCount < maxTeams) {
                    byte[] data = Files.readAllBytes(file.toPath());
                    DuTeam team = new DuTeam(data);
                    //The team slot is overwritten
                    for (int i = 0; i < 255; i++) {
                        if (!usedIds.contains(i)) {
                            team.setSlot(i);
                            break;
                        }
                    }
                    //The team is added
                    ObservableList<DuTeam> observableTeamList = FXCollections.observableArrayList();
                    observableTeamList.add(du26Block.playerTeam);
                    observableTeamList.addAll(du26Block.teamList);
                    observableTeamList.add(team);
                    teamList.setItems(observableTeamList);
                }
                currentCount++;
            } catch (Exception e) {
                throw new RuntimeException("INVALID TEAM FILE!");
            }
        }
    }

    public void deleteTeam() {
        ObservableList<DuTeam> teams = teamList.getItems();
        DuTeam selected = teamList.getSelectionModel().getSelectedItem();
        int index = teamList.getSelectionModel().getSelectedIndex();
        if (selected != null) {
            teams.remove(index);

            teamList.setItems(teams); // You can directly set the updated list

            // If the list is empty after deletion, clear the selection
            if (teams.isEmpty()) {
                teamList.getSelectionModel().clearSelection();
            }
        }
    }
}
