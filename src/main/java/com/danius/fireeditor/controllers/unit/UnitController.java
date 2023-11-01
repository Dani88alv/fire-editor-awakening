package com.danius.fireeditor.controllers.unit;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.controllers.MainController;
import com.danius.fireeditor.controllers.UI;
import com.danius.fireeditor.data.ClassDb;
import com.danius.fireeditor.data.ItemDb;
import com.danius.fireeditor.data.UnitDb;
import com.danius.fireeditor.data.model.ClassModel;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.inventory.Refinement;
import com.danius.fireeditor.savefile.units.Stats;
import com.danius.fireeditor.savefile.units.Unit;
import com.danius.fireeditor.savefile.units.UnitBlock;
import com.danius.fireeditor.savefile.wireless.DuTeam;
import com.danius.fireeditor.savefile.wireless.UnitDu;
import com.danius.fireeditor.util.Portrait;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.danius.fireeditor.data.ClassDb.*;

public class UnitController {
    @FXML
    public static OtherController otherController;
    @FXML
    public static SupportController supportController;
    public UnitBlock unitBlock;
    @FXML
    public ListView<Unit> listViewUnit;
    @FXML
    private Label labelUnitName, lblUnitCount;
    @FXML
    private Button btnMaxStats, btnMoveUnit, btnDuplicate, btnRemove, btnUp, btnDown,
            btnOpenBattle, btnOpenSupport, btnOpenChild, btnOpenAvatar,
            btnOpenItem, btnOpenSkills, btnOpenFlags;
    @FXML
    private TextField txtModif, txtTotalStat, txtGrowthMove,
            txtStatHp, txtStatStr, txtStatMag, txtStatSkl, txtStatSpd, txtStatLck, txtStatDef, txtStatRes, txtStatMove,
            txtGrowthHp, txtGrowthStr, txtGrowthMag, txtGrowthSkl, txtGrowthSpd, txtGrowthLck, txtGrowthDef, txtGrowthRes,
            txtBuffHp, txtBuffStr, txtBuffMag, txtBuffSkl, txtBuffSpd, txtBuffLck, txtBuffDef, txtBuffRes, txtBuffMove;
    @FXML
    private Spinner<Integer> spinUnitId, spinLevel, spinExp;
    @FXML
    private ComboBox<String> comboClass, comboGroupMove;
    @FXML
    public ComboBox<String> comboUnitGroup;
    @FXML
    private ColorPicker colorHair;
    @FXML
    private ImageView imgBuild, imgHairColor, imgHair;
    @FXML
    private CheckBox checkLimit;

    public void initialize() {
        FireEditor.unitController = this;
        setupElements(); //The UI elements are configured
        loadUnitBlock(); //The listView is loaded
        setupListeners(); //Additional listeners
        disableElements(false);
    }

    public void loadUnitBlock() {
        if (FireEditor.chapterFile != null && FireEditor.unitController != null) {
            //The unit block is set
            this.unitBlock = FireEditor.chapterFile.blockUnit;
            //The modded classes are retrieved
            int maxClasses = FireEditor.chapterFile.MAX_ID_CLASS;
            ObservableList<String> classes = FXCollections.observableArrayList(getClassNames(maxClasses));
            comboClass.setItems(classes);

            updateComboGroups();
            comboGroupMove.getSelectionModel().select(0x3);
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
        spinUnitId.increment(0);
        spinUnitId.getValueFactory().setValue(unit.rawBlock1.unitId());
        colorHair.setValue(unit.rawBlockEnd.getHairColorFx());
        spinLevel.getValueFactory().setValue(unit.rawBlock1.level());
        spinExp.getValueFactory().setValue(unit.rawBlock1.exp());
        txtGrowthMove.setText(String.valueOf(unit.rawBlock1.movement()));
        //Unit class
        comboClass.getSelectionModel().select(unit.rawBlock1.unitClass());
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
        checkLimit.setSelected(Stats.hasLimitBreaker(unit));
        setFieldsStats(unit);
        setImage();
    }

    public void setImage() {
        Image[] images = Portrait.setImage(listViewUnit.getSelectionModel().getSelectedItem());
        imgBuild.setImage(images[0]);
        if (images[1] != null) imgHairColor.setImage(images[1]);
        else imgHairColor.setImage(null);
        if (images[2] != null) imgHair.setImage(images[2]);
        else imgHair.setImage(null);
    }

    /*
    Updates a unit using all the input parameters
     */
    public void updateUnitFromFields(Unit unit) {
        if (unit != null) {
            //The spinners lose focus
            spinUnitId.increment(0);
            spinLevel.increment(0);
            spinExp.increment(0);
            //General Stats
            unit.rawBlock1.setUnitId(spinUnitId.getValue());
            unit.rawSupport.unitId = spinUnitId.getValue();
            unit.rawBlock1.setUnitClass(comboClass.getSelectionModel().getSelectedIndex());
            unit.rawBlock1.setLevel(spinLevel.getValue());
            unit.rawBlock1.setExp(spinExp.getValue());
            unit.rawBlockEnd.setHairColorFx(colorHair.getValue());
            //Updates the current list
            int unitGroup = comboUnitGroup.getSelectionModel().getSelectedIndex();
            unitBlock.unitList.set(unitGroup, listViewUnit.getItems());
            setImage();
        }
    }

    public void unitDuplicate() {
        ObservableList<Unit> unitList = listViewUnit.getItems();
        Unit selectedUnit = listViewUnit.getSelectionModel().getSelectedItem();
        if (selectedUnit != null && unitList.size() < Constants.UNIT_LIMIT) {
            //Create a new instance of the selected unit
            updateUnitFromFields(selectedUnit);
            Unit duplicatedUnit = new Unit(selectedUnit.getUnitBytes());
            int selectedIndex = unitList.indexOf(selectedUnit);
            unitList.add(selectedIndex + 1, duplicatedUnit);
            listViewUnit.setItems(FXCollections.observableArrayList(unitList));
            listViewUnit.getSelectionModel().select(selectedIndex + 1);
            displayUnitCount();
        }
    }

    public void unitDelete() {
        ObservableList<Unit> unitList = listViewUnit.getItems();
        Unit selectedUnit = listViewUnit.getSelectionModel().getSelectedItem();
        int index = listViewUnit.getSelectionModel().getSelectedIndex();
        if (selectedUnit != null) {
            unitList.remove(index);

            listViewUnit.setItems(unitList); // You can directly set the updated list
            unitBlock.unitList.set(comboUnitGroup.getSelectionModel().getSelectedIndex(), listViewUnit.getItems());
            displayUnitCount();

            // If the list is empty after deletion, clear the selection
            if (unitList.isEmpty()) {
                listViewUnit.getSelectionModel().clearSelection();
            }
        }
    }

    @FXML
    private void classReport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setPrefWidth(500);
        int itemsPerRow = 5;
        Unit unit = listViewUnit.getSelectionModel().getSelectedItem();
        alert.setTitle("Class Sets");
        alert.setHeaderText("Available classes for " + unit.unitName() + ":");

        List<ClassModel> baseClasses = ClassDb.getUnitBaseClasses(unit.getUnitId(), unit.isFemale());

        // Create a formatted content text with rows of three elements and commas
        StringBuilder contentText = new StringBuilder("Base Classes:\n");
        for (int i = 0; i < baseClasses.size(); i++) {
            contentText.append(baseClasses.get(i).getName());
            if (i % itemsPerRow == (itemsPerRow - 1) || i == baseClasses.size() - 1) contentText.append("\n");
            else contentText.append(", ");
        }

        if (unit.rawChild != null) {
            for (int i = 0; i < 6; i++) {
                int parent = unit.rawChild.parentId(i);
                if (parent == 0xFFFF) continue;

                contentText.append("\n");
                contentText.append("Inherited from ").append(UnitDb.getUnitName(parent)).append(":\n");
                List<ClassModel> parentClasses = ClassDb.getInheritClasses(unit, i);
                boolean isTactician = false;

                //Checks if it is a Tactician Tree (the message is too big
                for (ClassModel classModel : parentClasses) {
                    if (classModel.isTactician()) {
                        isTactician = true;
                        break;
                    }
                }

                if (isTactician) contentText.append("(Tactician Class Tree)\n");
                for (int k = 0; k < parentClasses.size(); k++) {
                    if (!isTactician || !parentClasses.get(k).isTacticianTree()) {
                        contentText.append(parentClasses.get(k).getName());
                        if (k % itemsPerRow == (itemsPerRow - 1) || k == parentClasses.size() - 1)
                            contentText.append("\n");
                        else contentText.append(", ");
                    }
                }

            }
        }

        alert.setContentText(contentText.toString());
        alert.showAndWait();
    }


    @FXML
    private void moveUnitToGroup() {
        Unit unit = listViewUnit.getSelectionModel().getSelectedItem();
        int selectedUnitIndex = listViewUnit.getSelectionModel().getSelectedIndex();
        int groupTarget = comboGroupMove.getSelectionModel().getSelectedIndex();
        int currentGroup = comboUnitGroup.getSelectionModel().getSelectedIndex();
        //If there is enough room
        if (groupTarget <= 5) {
            if (unitBlock.unitList.get(groupTarget).size() >= Constants.UNIT_LIMIT) return;
        }
        //If it is being moved to dead units, set the dead flag
        if (groupTarget == 4 && currentGroup != 4) {
            unit.kill();
        }
        //If it is being moved from dead units, unset the dead flag
        else if (groupTarget != 4 && currentGroup == 4) {
            unit.revive();
        }
        //Regular Unit Group
        if (groupTarget <= 5) {
            //The unit is added
            unitBlock.unitList.get(groupTarget).add(unit);
        }
        //Wireless Teams
        else {
            boolean isWest = FireEditor.chapterFile.isWest;
            UnitDu unitDu = unit.toUnitDu(isWest);
            //The items are updated
            int maxCount = ItemDb.MOD_MAX_ID;
            for (int i = 0; i < unitDu.itemList.size(); i++) {
                int itemId = unit.rawInventory.items.get(i).itemId();
                //Forged item
                if (itemId > maxCount) {
                    itemId -= maxCount + 1; //Position on the refinement block
                    Refinement refinement = FireEditor.chapterFile.blockRefi.getRefinement(itemId);
                    itemId = refinement.weaponId();
                    unitDu.itemList.get(i).setName(refinement.getName());
                    unitDu.itemList.get(i).setMight(refinement.might());
                    unitDu.itemList.get(i).setHit(refinement.hit());
                    unitDu.itemList.get(i).setCrit(refinement.crit());
                }
                unitDu.itemList.get(i).setItemId(itemId);
            }

            //Player's StreetPass Team
            if (groupTarget == 6) {
                if (FireEditor.userController.du26Block.playerTeam.unitList.size() >= 10) return;
                FireEditor.userController.du26Block.playerTeam.unitList.add(unitDu);
            }
            //External Team
            else {
                int slot = groupTarget - 6 - 1;
                if (FireEditor.userController.du26Block.teamList.get(slot).unitList.size() >= 10) return;
                FireEditor.userController.du26Block.teamList.get(slot).unitList.add(unitDu);
            }
        }
        //The unit is removed from the current group
        listViewUnit.getItems().remove(selectedUnitIndex);
        unitBlock.unitList.set(currentGroup, listViewUnit.getItems());
        displayUnitCount();
    }

    public void addUnit(Unit unit) {
        if (listViewUnit.getItems().size() >= Constants.UNIT_LIMIT) return;
        // Get the selected index
        int selectedIndex = comboUnitGroup.getSelectionModel().getSelectedIndex();
        // Update the listViewUnit with the corresponding data
        unitBlock.unitList.get(selectedIndex).add(unit);
        listViewUnit.setItems(FXCollections.observableArrayList(unitBlock.unitList.get(selectedIndex)));
        listViewUnit.getSelectionModel().select(listViewUnit.getItems().size() - 1);
        displayUnitCount();
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
                Unit unit = new Unit(newValue.getUnitBytes());
                disableElements(false);
                // When another Unit is selected, the last unit is updated and the new one is loaded
                if (oldValue != null) {
                    updateUnitFromFields(oldValue);
                }
                setFields(unit);
            } else disableElements(true);
        });
    }

    //Configures the JavaFX elementsS
    private void setupElements() {
        //Unit Group
        ObservableList<String> groups = FXCollections.observableArrayList(
                "Blue Units", "Red Units", "Green Units", "Main Units", "Dead Units", "Other Units");
        comboUnitGroup.setItems(groups);
        //General
        UI.setSpinnerNumeric(spinUnitId, 65335);
        UI.setSpinnerNumeric(spinLevel, 30);
        UI.setSpinnerNumeric(spinExp, 99);
        //Growth Stats
        UI.setNumericTextField(txtGrowthHp, 255);
        UI.setNumericTextField(txtGrowthStr, 255);
        UI.setNumericTextField(txtGrowthMag, 255);
        UI.setNumericTextField(txtGrowthSkl, 255);
        UI.setNumericTextField(txtGrowthSpd, 255);
        UI.setNumericTextField(txtGrowthLck, 255);
        UI.setNumericTextField(txtGrowthDef, 255);
        UI.setNumericTextField(txtGrowthRes, 255);
        UI.setNumericTextField(txtGrowthMove, 255);
        //Classes
        ObservableList<String> classes = FXCollections.observableArrayList(getClassNames());
        comboClass.setItems(classes);
        //IMPORTANT ORDER
        setupUnitList(listViewUnit);
        setupComboGroup();
    }

    public void updateComboGroups() {
        ObservableList<String> groups = FXCollections.observableArrayList(
                "Blue Units", "Red Units", "Green Units", "Main Units", "Dead Units", "Other Units");
        groups.add("StreetPass Team");
        for (DuTeam team : FireEditor.chapterFile.blockDu26.teamList) {
            groups.add(team.getTeamName());
        }
        comboGroupMove.setItems(groups);
    }

    public void disableElements(boolean disable) {
        comboClass.setDisable(disable);
        spinUnitId.setDisable(disable);
        spinLevel.setDisable(disable);
        spinExp.setDisable(disable);
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
        txtGrowthMove.setDisable(disable);
        btnMaxStats.setDisable(disable);
        //Other
        btnUp.setDisable(disable);
        btnDown.setDisable(disable);
        btnMoveUnit.setDisable(disable);
        btnDuplicate.setDisable(disable);
        btnRemove.setDisable(disable);
        comboGroupMove.setDisable(disable);
        btnOpenBattle.setDisable(disable);
        btnOpenSupport.setDisable(disable);
        btnOpenChild.setDisable(disable);
        btnOpenAvatar.setDisable(disable);
        btnOpenItem.setDisable(disable);
        btnOpenSkills.setDisable(disable);
        btnOpenFlags.setDisable(disable);
        checkLimit.setDisable(disable);
        if (disable) {
            imgBuild.setImage(null);
            imgHair.setImage(null);
            imgHairColor.setImage(null);
            labelUnitName.setText("");
        }
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

    private void setupListeners() {
        //Limit break checkbox
        checkLimit.setOnAction(event -> {
            setFieldsStats(listViewUnit.getSelectionModel().getSelectedItem());
        });
        //If the hair color is updated, the portrait too
        colorHair.valueProperty().addListener((observable, oldColor, newColor) -> {
            listViewUnit.getSelectionModel().getSelectedItem().rawBlockEnd.setHairColorFx(colorHair.getValue());
            setImage();
        });
        //If the Unit ID is changed, the name, portrait and stats are updated too
        spinUnitId.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && listViewUnit.getSelectionModel().getSelectedItem() != null) {
                spinUnitId.increment(0);
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setUnitId(spinUnitId.getValue());
                listViewUnit.getSelectionModel().getSelectedItem().rawSupport.unitId = spinUnitId.getValue();
                labelUnitName.setText(listViewUnit.getSelectionModel().getSelectedItem().unitName());
                refreshData(listViewUnit.getSelectionModel().getSelectedItem());
            }
        });
        spinLevel.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && listViewUnit.getSelectionModel().getSelectedItem() != null) {
                spinLevel.increment(0);
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setLevel(spinLevel.getValue());
            }
        });
        spinExp.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && listViewUnit.getSelectionModel().getSelectedItem() != null) {
                spinExp.increment(0);
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setExp(spinExp.getValue());
            }
        });
        //If the class is changed, the portrait and stats are updated too
        comboClass.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && listViewUnit.getSelectionModel().getSelectedItem() != null) {
                listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.setUnitClass(
                        comboClass.getSelectionModel().getSelectedIndex());
                refreshData(listViewUnit.getSelectionModel().getSelectedItem());
            }
        });
        //If the growth is updated, the displayed stats are updated too
        textStatsListeners(txtGrowthHp);
        textStatsListeners(txtGrowthStr);
        textStatsListeners(txtGrowthMag);
        textStatsListeners(txtGrowthSkl);
        textStatsListeners(txtGrowthSpd);
        textStatsListeners(txtGrowthLck);
        textStatsListeners(txtGrowthDef);
        textStatsListeners(txtGrowthRes);
        textStatsListeners(txtGrowthMove);
    }

    //The 8 growth stats are updated
    private void textStatsListeners(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(newValue, "") && newValue != null
                    && listViewUnit.getSelectionModel().getSelectedItem() != null) {
                Unit unit = listViewUnit.getSelectionModel().getSelectedItem();
                unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthHp.getText()), 0);
                unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthStr.getText()), 1);
                unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthMag.getText()), 2);
                unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthSkl.getText()), 3);
                unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthSpd.getText()), 4);
                unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthLck.getText()), 5);
                unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthDef.getText()), 6);
                unit.rawBlock1.setGrowth(Integer.parseInt(txtGrowthRes.getText()), 7);
                unit.rawBlock1.setMovement(Integer.parseInt(txtGrowthMove.getText()));
                refreshData(listViewUnit.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void refreshData(Unit unit) {
        setFieldsStats(unit); //Stats
        setImage(); //Portrait
        refreshName(unit);
    }

    public void refreshName(Unit unit) {
        labelUnitName.setText(unit.unitName()); //Name
    }

    public void setFieldsStats(Unit unit) {
        int[] currentStats = unit.currentStats(checkLimit.isSelected());
        txtStatHp.setText(String.valueOf(currentStats[0]));
        txtStatStr.setText(String.valueOf(currentStats[1]));
        txtStatMag.setText(String.valueOf(currentStats[2]));
        txtStatSkl.setText(String.valueOf(currentStats[3]));
        txtStatSpd.setText(String.valueOf(currentStats[4]));
        txtStatLck.setText(String.valueOf(currentStats[5]));
        txtStatDef.setText(String.valueOf(currentStats[6]));
        txtStatRes.setText(String.valueOf(currentStats[7]));
        txtStatMove.setText(String.valueOf(Stats.getMoveTotal(unit)));

        int[] buffs = Stats.allBuffs(unit);
        txtBuffHp.setText(String.valueOf(buffs[0]));
        txtBuffStr.setText(String.valueOf(buffs[1]));
        txtBuffMag.setText(String.valueOf(buffs[2]));
        txtBuffSkl.setText(String.valueOf(buffs[3]));
        txtBuffSpd.setText(String.valueOf(buffs[4]));
        txtBuffLck.setText(String.valueOf(buffs[5]));
        txtBuffDef.setText(String.valueOf(buffs[6]));
        txtBuffRes.setText(String.valueOf(buffs[7]));
        txtBuffMove.setText(String.valueOf(Stats.getMoveBuff(unit)));

        txtModif.setText(Arrays.toString(listViewUnit.getSelectionModel().getSelectedItem().modifiers()));
        txtTotalStat.setText(String.valueOf(Stats.rating(unit, checkLimit.isSelected())));
    }

    public void maxGrowth() {
        listViewUnit.getSelectionModel().getSelectedItem().maxGrowth();
        int[] growth = listViewUnit.getSelectionModel().getSelectedItem().rawBlock1.growth();
        txtGrowthHp.setText(String.valueOf(growth[0]));
        txtGrowthStr.setText(String.valueOf(growth[1]));
        txtGrowthMag.setText(String.valueOf(growth[2]));
        txtGrowthSkl.setText(String.valueOf(growth[3]));
        txtGrowthSpd.setText(String.valueOf(growth[4]));
        txtGrowthLck.setText(String.valueOf(growth[5]));
        txtGrowthDef.setText(String.valueOf(growth[6]));
        txtGrowthRes.setText(String.valueOf(growth[7]));
        txtGrowthMove.setText(String.valueOf(2));
    }

    @FXML
    public void orderUp() {
        int selectedIndex = listViewUnit.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0) {
            Unit selectedItem = listViewUnit.getSelectionModel().getSelectedItem();
            listViewUnit.getItems().remove(selectedIndex);
            listViewUnit.getItems().add(selectedIndex - 1, selectedItem);
            listViewUnit.getSelectionModel().select(selectedIndex - 1);
        }
    }

    @FXML
    public void orderDown() {
        int selectedIndex = listViewUnit.getSelectionModel().getSelectedIndex();
        int itemCount = listViewUnit.getItems().size();

        if (selectedIndex >= 0 && selectedIndex < itemCount - 1) {
            Unit selectedItem = listViewUnit.getSelectionModel().getSelectedItem();
            listViewUnit.getItems().remove(selectedIndex);
            listViewUnit.getItems().add(selectedIndex + 1, selectedItem);
            listViewUnit.getSelectionModel().select(selectedIndex + 1);
        }
    }

    public void displayUnitCount() {
        lblUnitCount.setText("Units: " + listViewUnit.getItems().size() +
                "/" + Constants.UNIT_LIMIT);
    }

    public void openFlags(ActionEvent event) {
        try {
            if (listViewUnit.getSelectionModel().getSelectedItem() != null) {
                //The current fields are updated
                //updateUnitFromFields(listViewUnit.getSelectionModel().getSelectedItem());
                FXMLLoader fxmlLoader = MainController.getWindowUnit("viewFlags.fxml");
                Parent root = fxmlLoader.load();
                // Get the selected value from the main view's controller
                Unit selectedValue = listViewUnit.getSelectionModel().getSelectedItem();
                // Pass the selected value to the second view's controller
                FlagController flagController = fxmlLoader.getController();
                flagController.setUnit(selectedValue);
                // Create a new stage for the secondary view
                Stage secondaryStage = new Stage();
                secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
                secondaryStage.setTitle("Flags");
                secondaryStage.setScene(new Scene(root));
                secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openSkills(ActionEvent event) {
        try {
            if (listViewUnit.getSelectionModel().getSelectedItem() != null) {
                //The current fields are updated
                //updateUnitFromFields(listViewUnit.getSelectionModel().getSelectedItem());
                FXMLLoader fxmlLoader = MainController.getWindowUnit("viewSkills.fxml");
                Parent root = fxmlLoader.load();
                // Get the selected value from the main view's controller
                Unit selectedValue = listViewUnit.getSelectionModel().getSelectedItem();
                // Pass the selected value to the second view's controller
                SkillController skillController = fxmlLoader.getController();
                skillController.setUnit(selectedValue);
                // Create a new stage for the secondary view
                Stage secondaryStage = new Stage();
                secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
                secondaryStage.setTitle("Skills");
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
                FXMLLoader fxmlLoader = MainController.getWindowUnit("viewUnitMisc.fxml");
                Parent root = fxmlLoader.load();
                // Get the selected value from the main view's controller
                Unit selectedValue = listViewUnit.getSelectionModel().getSelectedItem();
                // Pass the selected value to the second view's controller
                OtherController otherController = fxmlLoader.getController();
                otherController.setUnit(selectedValue);
                // Create a new stage for the secondary view
                Stage secondaryStage = new Stage();
                secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
                secondaryStage.setTitle("Other Data");
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
                //If it is an invalid unit, do NOT open the window
                //if (supportList.length == 0) return;
                //Check the support block size
                listViewUnit.getSelectionModel().getSelectedItem().rawSupport.expandBlock();
                updateUnitFromFields(listViewUnit.getSelectionModel().getSelectedItem());
                FXMLLoader fxmlLoader = MainController.getWindowUnit("viewSupports.fxml");
                Parent root = fxmlLoader.load();
                // Get the selected value from the main view's controller
                Unit selectedValue = listViewUnit.getSelectionModel().getSelectedItem();
                // Pass the selected value to the second view's controller
                SupportController supportController = fxmlLoader.getController();
                int currentGroup = comboUnitGroup.getSelectionModel().getSelectedIndex();
                supportController.setUnit(selectedValue, unitBlock.unitList.get(currentGroup));
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
                FXMLLoader fxmlLoader = MainController.getWindowUnit("viewChild.fxml");
                Parent root = fxmlLoader.load();
                // Get the selected value from the main view's controller
                Unit selectedValue = listViewUnit.getSelectionModel().getSelectedItem();
                //Calculate the sibling
                int sibling = unitBlock.findSibling(selectedValue);
                // Pass the selected value to the second view's controller
                ChildController childController = fxmlLoader.getController();
                childController.setUnit(selectedValue, sibling);
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
                FXMLLoader fxmlLoader = MainController.getWindowUnit("viewLogbook.fxml");
                Parent root = fxmlLoader.load();
                // Get the selected value from the main view's controller
                Unit selectedValue = listViewUnit.getSelectionModel().getSelectedItem();
                //The Logbook block size is updated
                //if (selectedValue.rawLog != null) selectedValue.rawLog.changeRegion(unitBlock.isWest);
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
                FXMLLoader fxmlLoader = MainController.getWindowUnit("viewInventory.fxml");
                Parent root = fxmlLoader.load();
                // Get the selected value from the main view's controller
                Unit selectedValue = listViewUnit.getSelectionModel().getSelectedItem();
                ItemController itemController = fxmlLoader.getController();
                itemController.setUnit(selectedValue, FireEditor.convoyController.refiBlock.refiList);
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