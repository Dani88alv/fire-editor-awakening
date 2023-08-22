package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.units.Stats;
import com.danius.fireeditor.savefile.units.Supports;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.UnitBlock;
import com.danius.fireeditor.util.Hex;
import com.danius.fireeditor.util.Names;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class UnitController {
    @FXML
    public static WeaponExpController weaponExpController;
    @FXML
    public static BattleController battleController;
    @FXML
    public static SupportController supportController;
    public UnitBlock unitBlock;
    @FXML
    public ListView<Unit> listViewUnit;
    @FXML
    private Label labelUnitName, lblUnitCount, lblSkillCount;
    @FXML
    private Button btnMaxStats, btnMaxSkills, btnMoveUnit, btnDuplicate, btnRemove,
            btnOpenWeapon, btnOpenBattle, btnOpenSupport, btnOpenChild, btnOpenAvatar,
            btnOpenItem, btnLegalSkills;
    @FXML
    private TextField txtLevel, txtExp, txtBoots,
            txtStatHp, txtStatStr, txtStatMag, txtStatSkl, txtStatSpd, txtStatLck, txtStatDef, txtStatRes,
            txtGrowthHp, txtGrowthStr, txtGrowthMag, txtGrowthSkl, txtGrowthSpd, txtGrowthLck, txtGrowthDef, txtGrowthRes;
    @FXML
    private Spinner<Integer> spinUnitId;
    @FXML
    private ComboBox<String> comboClass,
            comboSkillL, comboCSkill1, comboCSkill2, comboCSkill3, comboCSkill4, comboCSkill5,
            comboGroupMove;
    @FXML
    public ComboBox<String> comboUnitGroup;
    @FXML
    private CheckBox checkSkillL;
    @FXML
    private ColorPicker colorHair;

    public void initialize() {
        FireEditor.unitController = this;
        setupElements(); //The UI elements are configured
        loadUnitBlock(); //The listView is loaded
        setupStatsListeners(); //Additional listeners
        disableElements(false);
    }



    public void loadUnitBlock() {
        if (FireEditor.chapterFile != null && FireEditor.unitController != null) {
            //The unit block is set
            this.unitBlock = FireEditor.chapterFile.blockUnit;
            //The modded classes are retrieved
            int maxClasses = FireEditor.maxClasses;
            int currentClasses = comboClass.getItems().size() - 1;
            if (maxClasses > Constants.MAX_CLASSES && maxClasses > currentClasses) {
                for (int i = 0; i < maxClasses - currentClasses; i++) {
                    comboClass.getItems().add("Mod Class #" + (i + 1));
                }
            }

            //The unit group is selected, chosen between blue units or main units
            if (unitBlock.unitList.get(0x0).size() > 0) {
                comboUnitGroup.getSelectionModel().select(0x0);
            } else {
                comboUnitGroup.getSelectionModel().select(0x3);
            }
            //The listview is loaded
            listViewUnit.setItems(FXCollections.observableArrayList( //Loads the unit group
                    unitBlock.unitList.get(comboUnitGroup.getSelectionModel().getSelectedIndex())));
            listViewUnit.getSelectionModel().selectLast();
            listViewUnit.getSelectionModel().selectFirst();
            displayUnitCount();
        }
    }

    /*
    Sets all the UI elements to the values of the current units
     */
    public void setFields(Unit unit) {
        //General
        labelUnitName.setText(unit.unitName());
        spinUnitId.getValueFactory().setValue(unit.rawBlock1.unitId());
        colorHair.setValue(Hex.hexToColor(unit.rawBlockEnd.getHairColor()));
        //Unit class
        comboClass.getSelectionModel().select(unit.rawBlock1.unitClass());
        //Battle Stats
        txtLevel.setText(String.valueOf(unit.rawBlock1.level()));
        txtExp.setText(String.valueOf(unit.rawBlock1.exp()));
        txtBoots.setText(String.valueOf(unit.rawBlock1.movement()));
        //txtBattles.setText(String.valueOf(unit.rawBlockEnd.battleCount()));
        //txtVictories.setText(String.valueOf(unit.rawBlockEnd.victoryCount()));
        //Stats
        int[] growth = unit.rawBlock1.growth();
        txtGrowthHp.setText(String.valueOf(growth[0]));
        txtGrowthStr.setText(String.valueOf(growth[1]));
        txtGrowthMag.setText(String.valueOf(growth[2]));
        txtGrowthSkl.setText(String.valueOf(growth[3]));
        txtGrowthSpd.setText(String.valueOf(growth[4]));
        txtGrowthLck.setText(String.valueOf(growth[5]));
        txtGrowthDef.setText(String.valueOf(growth[6]));
        txtGrowthRes.setText(String.valueOf(growth[7]));
        //Current Skills
        int[] currentSkills = unit.rawBlock2.getCurrentSkills();
        comboCSkill1.getSelectionModel().select(currentSkills[0]);
        comboCSkill2.getSelectionModel().select(currentSkills[1]);
        comboCSkill3.getSelectionModel().select(currentSkills[2]);
        comboCSkill4.getSelectionModel().select(currentSkills[3]);
        comboCSkill5.getSelectionModel().select(currentSkills[4]);
        //Learned Skills
        List<Integer> skills = unit.rawSkill.getLearnedSkills();
        //Sets the default skill position
        if (skills.size() > 0) {
            comboSkillL.getSelectionModel().select(skills.get(skills.size() - 1));
            checkSkillL.setSelected(true);
        } else {
            comboSkillL.getSelectionModel().select(0x1);
            checkSkillL.setSelected(false);
        }
        displaySkillCount(unit.rawSkill.skillCount());
        setFieldsStats();
    }

    /*
    Updates a unit using all the input parameters
     */
    public void updateUnitFromFields(Unit unit) {
        if (unit != null) {
            //General Stats
            unit.rawBlock1.setUnitId(spinUnitId.getValue());
            unit.rawSupport.unitId = spinUnitId.getValue();
            unit.rawBlock1.setUnitClass(comboClass.getSelectionModel().getSelectedIndex());
            unit.rawBlock1.setLevel(Integer.parseInt(txtLevel.getText()));
            unit.rawBlock1.setExp(Integer.parseInt(txtExp.getText()));
            unit.rawBlock1.setMovement(Integer.parseInt(txtBoots.getText()));
            unit.rawBlockEnd.setHairColor(Hex.colorToHex(colorHair.getValue()));
            //Stats
            unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthHp.getText()), 0);
            unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthStr.getText()), 1);
            unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthMag.getText()), 2);
            unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthSkl.getText()), 3);
            unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthSpd.getText()), 4);
            unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthLck.getText()), 5);
            unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthDef.getText()), 6);
            unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthRes.getText()), 7);
            //Equipped Skills
            unit.rawBlock2.setCurrentSkill(comboCSkill1.getSelectionModel().getSelectedIndex(), 0);
            unit.rawBlock2.setCurrentSkill(comboCSkill2.getSelectionModel().getSelectedIndex(), 1);
            unit.rawBlock2.setCurrentSkill(comboCSkill3.getSelectionModel().getSelectedIndex(), 2);
            unit.rawBlock2.setCurrentSkill(comboCSkill4.getSelectionModel().getSelectedIndex(), 3);
            unit.rawBlock2.setCurrentSkill(comboCSkill5.getSelectionModel().getSelectedIndex(), 4);
            //Updates the current list
            int unitGroup = comboUnitGroup.getSelectionModel().getSelectedIndex();
            unitBlock.unitList.set(unitGroup, listViewUnit.getItems());
        }
    }

    public void unitDuplicate() {
        ObservableList<Unit> unitList = listViewUnit.getItems();
        Unit selectedUnit = listViewUnit.getSelectionModel().getSelectedItem();
        if (selectedUnit != null && unitList.size() < 255) {
            //Create a new instance of the selected unit
            updateUnitFromFields(selectedUnit);
            Unit duplicatedUnit = new Unit(selectedUnit.getUnitBytes());
            int selectedIndex = unitList.indexOf(selectedUnit);
            unitList.add(selectedIndex + 1, duplicatedUnit);
            listViewUnit.setItems(FXCollections.observableArrayList(unitList));
            displayUnitCount();
        }
    }


    public void unitDelete() {
        ObservableList<Unit> unitList = listViewUnit.getItems();
        Unit selectedUnit = listViewUnit.getSelectionModel().getSelectedItem();
        if (selectedUnit != null) {
            unitList.remove(selectedUnit);
            listViewUnit.setItems(FXCollections.observableArrayList(unitList));
            displayUnitCount();
        }
    }

    @FXML
    public void maxStats() {
        updateUnitFromFields(listViewUnit.getSelectionModel().getSelectedItem());
        Stats.setMaxStatsHigh(listViewUnit.getSelectionModel().getSelectedItem());
        setFields(listViewUnit.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void maxSkills() {
        updateUnitFromFields(listViewUnit.getSelectionModel().getSelectedItem());
        listViewUnit.getSelectionModel().getSelectedItem().rawSkill.setAll(true);
        setFields(listViewUnit.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void maxSkillsLegal() {
        updateUnitFromFields(listViewUnit.getSelectionModel().getSelectedItem());
        listViewUnit.getSelectionModel().getSelectedItem().setLegalSkills();
        setFields(listViewUnit.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void moveUnitToGroup() {
        int id = listViewUnit.getSelectionModel().getSelectedIndex();
        int group2 = comboGroupMove.getSelectionModel().getSelectedIndex();
        //The unit is added to the target group
        if (unitBlock.unitList.get(group2).size() < 255) {
            unitBlock.unitList.get(group2).add(listViewUnit.getItems().get(id));
            //The unit is removed from the current group
            listViewUnit.getItems().remove(id);
            displayUnitCount();
            return;
        }
        System.out.println("TARGET GROUP FULL!");
    }

    /*
    LISTENERS AND UI SETUPS
     */

    private void setupUnitList(ListView<Unit> listViewUnit) {
        listViewUnit.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Unit unit, boolean empty) {
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
                disableElements(false);
                // When another Unit is selected, the last unit is updated and the new one is loaded
                if (oldValue != null) {
                    updateUnitFromFields(oldValue);
                }
                setFields(newValue);
            } else disableElements(true);
        });
    }

    //Configures the JavaFX elementsS
    private void setupElements() {
        //Unit Group
        ObservableList<String> groups = FXCollections.observableArrayList(
                "Blue Units", "Red Units", "Green Units", "Main Units", "Dead Units", "Other Units");
        comboUnitGroup.setItems(groups);
        comboGroupMove.setItems(groups);
        comboGroupMove.getSelectionModel().select(0x3);
        //General
        UI.setSpinnerNumeric(spinUnitId, 65335);
        //Battle Stats
        UI.setNumericTextField(txtLevel, 30);
        UI.setNumericTextField(txtExp, 99);
        UI.setNumericTextField(txtBoots, 255);
        //Growth Stats
        UI.setNumericTextField(txtGrowthHp, 255);
        UI.setNumericTextField(txtGrowthStr, 255);
        UI.setNumericTextField(txtGrowthMag, 255);
        UI.setNumericTextField(txtGrowthSkl, 255);
        UI.setNumericTextField(txtGrowthSpd, 255);
        UI.setNumericTextField(txtGrowthLck, 255);
        UI.setNumericTextField(txtGrowthDef, 255);
        UI.setNumericTextField(txtGrowthRes, 255);
        //Classes
        ObservableList<String> classes = FXCollections.observableArrayList(Names.classNames);
        comboClass.setItems(classes);
        //Skills
        ObservableList<String> skills = FXCollections.observableArrayList(Names.skillNames);
        comboCSkill1.setItems(skills);
        comboCSkill2.setItems(skills);
        comboCSkill3.setItems(skills);
        comboCSkill4.setItems(skills);
        comboCSkill5.setItems(skills);
        comboSkillL.setItems(skills);
        //Learned Skills
        setupComboSkill();
        setupCheckSkill();
        //IMPORTANT ORDER
        setupUnitList(listViewUnit);
        setupComboGroup();
    }

    public void disableElements(boolean disable) {
        comboClass.setDisable(disable);
        spinUnitId.setDisable(disable);
        txtLevel.setDisable(disable);
        txtExp.setDisable(disable);
        txtBoots.setDisable(disable);
        colorHair.setDisable(disable);
        //Stats
        txtGrowthHp.setDisable(disable);
        txtGrowthStr.setDisable(disable);
        txtGrowthMag.setDisable(disable);
        txtGrowthSkl.setDisable(disable);
        txtGrowthSpd.setDisable(disable);
        txtGrowthLck.setDisable(disable);
        txtGrowthDef.setDisable(disable);
        txtGrowthRes.setDisable(disable);
        btnMaxStats.setDisable(disable);
        //Skills
        comboSkillL.setDisable(disable);
        checkSkillL.setDisable(disable);
        comboCSkill1.setDisable(disable);
        comboCSkill2.setDisable(disable);
        comboCSkill3.setDisable(disable);
        comboCSkill4.setDisable(disable);
        comboCSkill5.setDisable(disable);
        btnMaxSkills.setDisable(disable);
        btnLegalSkills.setDisable(disable);
        //Other
        btnMoveUnit.setDisable(disable);
        btnDuplicate.setDisable(disable);
        btnRemove.setDisable(disable);
        comboGroupMove.setDisable(disable);
        btnOpenWeapon.setDisable(disable);
        btnOpenBattle.setDisable(disable);
        btnOpenSupport.setDisable(disable);
        btnOpenChild.setDisable(disable);
        btnOpenAvatar.setDisable(disable);
        btnOpenItem.setDisable(disable);
    }

    /*
    Updates the checkbox of learned skills
     */
    private void setupComboSkill() {
        comboSkillL.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && listViewUnit.getSelectionModel().getSelectedItem() != null) {
                int selectedSkill = comboSkillL.getSelectionModel().getSelectedIndex();
                Unit unit = listViewUnit.getSelectionModel().getSelectedItem();
                List<Integer> skills = unit.rawSkill.getLearnedSkills();
                checkSkillL.setSelected(skills.contains(selectedSkill));
            }
        });
    }

    /*
    Updates the learned skills each time the checkbox is updated
     */
    private void setupCheckSkill() {
        checkSkillL.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && listViewUnit.getSelectionModel().getSelectedItem() != null) {
                int skill = comboSkillL.getSelectionModel().getSelectedIndex();
                listViewUnit.getSelectionModel().getSelectedItem().rawSkill.setLearnedSkill(newValue, skill);
                displaySkillCount(listViewUnit.getSelectionModel().getSelectedItem().rawSkill.skillCount());
            }
        });
    }

    private void setupComboGroup() {
        comboUnitGroup.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Get the selected index
                int selectedIndex = comboUnitGroup.getSelectionModel().getSelectedIndex();
                // Update the listViewUnit with the corresponding data
                listViewUnit.setItems(FXCollections.observableArrayList(unitBlock.unitList.get(selectedIndex)));
                //If the new group is not empty, display the data
                if (unitBlock.unitList.get(selectedIndex).size() != 0) {
                    listViewUnit.getSelectionModel().select(0);
                }
                displayUnitCount();
            }
        });
    }

    private void setupStatsListeners() {
        //Unit ID
        spinUnitId.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && listViewUnit.getSelectionModel().getSelectedItem() != null) {
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setUnitId(spinUnitId.getValue());
                listViewUnit.getSelectionModel().getSelectedItem().rawSupport.unitId = spinUnitId.getValue();
                labelUnitName.setText(listViewUnit.getSelectionModel().getSelectedItem().unitName());
                setFieldsStats();
            }
        });
        //Class
        comboClass.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && listViewUnit.getSelectionModel().getSelectedItem() != null) {
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setUnitClass(
                        comboClass.getSelectionModel().getSelectedIndex());
                setFieldsStats();
            }
        });
        //Growth
        textStatsListeners(txtGrowthHp);
        textStatsListeners(txtGrowthStr);
        textStatsListeners(txtGrowthMag);
        textStatsListeners(txtGrowthSkl);
        textStatsListeners(txtGrowthSpd);
        textStatsListeners(txtGrowthLck);
        textStatsListeners(txtGrowthDef);
        textStatsListeners(txtGrowthRes);
    }

    private void textStatsListeners(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(newValue, "") && newValue != null
                    && listViewUnit.getSelectionModel().getSelectedItem() != null) {
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setGrowth(
                        Integer.parseInt(txtGrowthHp.getText()), 0);
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setGrowth(
                        Integer.parseInt(txtGrowthStr.getText()), 1);
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setGrowth(
                        Integer.parseInt(txtGrowthMag.getText()), 2);
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setGrowth(
                        Integer.parseInt(txtGrowthSkl.getText()), 3);
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setGrowth(
                        Integer.parseInt(txtGrowthSpd.getText()), 4);
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setGrowth(
                        Integer.parseInt(txtGrowthLck.getText()), 5);
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setGrowth(
                        Integer.parseInt(txtGrowthDef.getText()), 6);
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setGrowth(
                        Integer.parseInt(txtGrowthRes.getText()), 7);
                setFieldsStats();
            }
        });
    }

    private void setFieldsStats() {
        int[] currentStats = listViewUnit.getSelectionModel().getSelectedItem().currentStats();
        txtStatHp.setText(String.valueOf(currentStats[0]));
        txtStatStr.setText(String.valueOf(currentStats[1]));
        txtStatMag.setText(String.valueOf(currentStats[2]));
        txtStatSkl.setText(String.valueOf(currentStats[3]));
        txtStatSpd.setText(String.valueOf(currentStats[4]));
        txtStatLck.setText(String.valueOf(currentStats[5]));
        txtStatDef.setText(String.valueOf(currentStats[6]));
        txtStatRes.setText(String.valueOf(currentStats[7]));
    }


    private void displaySkillCount(int count) {
        lblSkillCount.setText("Learned Skills (" + count + ")");
    }

    public void displayUnitCount() {
        lblUnitCount.setText("Units: " + listViewUnit.getItems().size() + "/255");
    }

    /*
    Loads the Weapon Exp editor window
    */
    public void openWeaponExp(ActionEvent event) {
        try {
            if (listViewUnit.getSelectionModel().getSelectedItem() != null) {
                //The current fields are updated
                updateUnitFromFields(listViewUnit.getSelectionModel().getSelectedItem());
                FXMLLoader fxmlLoader = new FXMLLoader(FireEditor.class.getResource("viewWeaponExp.fxml"));
                Parent root = fxmlLoader.load();
                // Get the selected value from the main view's controller
                Unit selectedValue = listViewUnit.getSelectionModel().getSelectedItem();
                // Pass the selected value to the second view's controller
                WeaponExpController weaponExpController = fxmlLoader.getController();
                weaponExpController.setUnit(selectedValue);
                // Create a new stage for the secondary view
                Stage secondaryStage = new Stage();
                secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
                secondaryStage.setTitle("Weapon EXP");
                secondaryStage.setScene(new Scene(root));
                secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openBattle(ActionEvent event) {
        try {
            if (listViewUnit.getSelectionModel().getSelectedItem() != null) {
                //The current fields are updated
                updateUnitFromFields(listViewUnit.getSelectionModel().getSelectedItem());
                FXMLLoader fxmlLoader = new FXMLLoader(FireEditor.class.getResource("viewBattle.fxml"));
                Parent root = fxmlLoader.load();
                // Get the selected value from the main view's controller
                Unit selectedValue = listViewUnit.getSelectionModel().getSelectedItem();
                // Pass the selected value to the second view's controller
                BattleController battleController = fxmlLoader.getController();
                battleController.setUnit(selectedValue);
                // Create a new stage for the secondary view
                Stage secondaryStage = new Stage();
                secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
                secondaryStage.setTitle("Battle Data");
                secondaryStage.setScene(new Scene(root));
                secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openSupports(ActionEvent event) {
        try {
            if (listViewUnit.getSelectionModel().getSelectedItem() != null) {
                //Stop invalid units from editing supports
                //if (listViewUnit.getSelectionModel().getSelectedItem().rawSupport.supportCount() == 0) return;
                //The unit ID is updated
                listViewUnit.getSelectionModel().getSelectedItem().rawSupport.setUnitId(spinUnitId.getValue());
                int supportId = listViewUnit.getSelectionModel().getSelectedItem().rawSupport.unitId;
                int[] supportList = Supports.getSupportUnits(supportId);
                //If it is an invalid unit, do NOT open the window
                //if (supportList.length == 0) return;
                //Check the support block size
                listViewUnit.getSelectionModel().getSelectedItem().rawSupport.expandBlock();
                updateUnitFromFields(listViewUnit.getSelectionModel().getSelectedItem());
                FXMLLoader fxmlLoader = new FXMLLoader(FireEditor.class.getResource("viewSupports.fxml"));
                Parent root = fxmlLoader.load();
                // Get the selected value from the main view's controller
                Unit selectedValue = listViewUnit.getSelectionModel().getSelectedItem();
                // Pass the selected value to the second view's controller
                SupportController supportController = fxmlLoader.getController();
                supportController.setUnit(selectedValue);
                // Create a new stage for the secondary view
                Stage secondaryStage = new Stage();
                secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
                secondaryStage.setTitle("Supports");
                secondaryStage.setScene(new Scene(root));
                secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openChild(ActionEvent event) {
        try {
            if (listViewUnit.getSelectionModel().getSelectedItem() != null) {
                //The current fields are updated
                updateUnitFromFields(listViewUnit.getSelectionModel().getSelectedItem());
                FXMLLoader fxmlLoader = new FXMLLoader(FireEditor.class.getResource("viewChild.fxml"));
                Parent root = fxmlLoader.load();
                // Get the selected value from the main view's controller
                Unit selectedValue = listViewUnit.getSelectionModel().getSelectedItem();
                // Pass the selected value to the second view's controller
                ChildController childController = fxmlLoader.getController();
                childController.setUnit(selectedValue);
                // Create a new stage for the secondary view
                Stage secondaryStage = new Stage();
                secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
                secondaryStage.setTitle("Child Data");
                secondaryStage.setScene(new Scene(root));
                secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openLog(ActionEvent event) {
        try {
            if (listViewUnit.getSelectionModel().getSelectedItem() != null) {
                //The current fields are updated
                updateUnitFromFields(listViewUnit.getSelectionModel().getSelectedItem());
                FXMLLoader fxmlLoader = new FXMLLoader(FireEditor.class.getResource("viewLogbook.fxml"));
                Parent root = fxmlLoader.load();
                // Get the selected value from the main view's controller
                Unit selectedValue = listViewUnit.getSelectionModel().getSelectedItem();
                //The Logbook block size is updated
                if (selectedValue.hasLogBlock) selectedValue.rawLog.changeRegion(unitBlock.isWest);
                // Pass the selected value to the second view's controller
                LogController logController = fxmlLoader.getController();
                logController.setUnit(selectedValue, unitBlock.isWest);
                // Create a new stage for the secondary view
                Stage secondaryStage = new Stage();
                secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
                secondaryStage.setTitle("Avatar Data");
                secondaryStage.setScene(new Scene(root));
                secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openItem(ActionEvent event) {
        try {
            if (listViewUnit.getSelectionModel().getSelectedItem() != null) {
                //The current fields are updated
                updateUnitFromFields(listViewUnit.getSelectionModel().getSelectedItem());
                FXMLLoader fxmlLoader = new FXMLLoader(FireEditor.class.getResource("viewInventory.fxml"));
                Parent root = fxmlLoader.load();
                // Get the selected value from the main view's controller
                Unit selectedValue = listViewUnit.getSelectionModel().getSelectedItem();
                ItemController itemController = fxmlLoader.getController();
                int itemCount = FireEditor.chapterFile.blockTran.regularItemCount();
                itemController.setUnit(selectedValue, itemCount);
                // Create a new stage for the secondary view
                Stage secondaryStage = new Stage();
                secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
                secondaryStage.setTitle("Inventory");
                secondaryStage.setScene(new Scene(root));
                secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}