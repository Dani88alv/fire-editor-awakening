package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.inventory.RefiBlock;
import com.danius.fireeditor.savefile.inventory.Refinement;
import com.danius.fireeditor.savefile.inventory.TranBlock;
import com.danius.fireeditor.util.Names;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

import static com.danius.fireeditor.savefile.inventory.TranBlock.amountString;

public class ConvoyController {
    private boolean addedListeners = false;
    public TranBlock tranBlock;
    public RefiBlock refiBlock;
    @FXML
    public ListView<Refinement> listViewRefi;
    @FXML
    private ComboBox<String> comboWeaponId;
    @FXML
    private Spinner<Integer> spinRefiUse, spinMight, spinHit, spinCrit, spinPos;
    @FXML
    private CheckBox checkRefiFlag;
    @FXML
    private TextField txtRefiName;
    @FXML
    private Label lblRefiAmount, lblRefiCount;
    @FXML
    private TableView<DataItem> tableConvoy;
    @FXML
    private TableColumn<DataItem, String> nameColumn;
    @FXML
    private TableColumn<DataItem, Integer> amountColumn;
    @FXML
    private TableColumn<DataItem, String> totalUsesColumn;


    public void initialize() {
        FireEditor.convoyController = this;
        disableRefi(true);
        ObservableList<String> items = FXCollections.observableArrayList(Names.itemNames);
        comboWeaponId.setItems(items);
        UI.setSpinnerNumeric(spinRefiUse, 65535);
        UI.setSpinnerNumeric(spinMight, 255);
        UI.setSpinnerNumeric(spinHit, 255);
        UI.setSpinnerNumeric(spinCrit, 255);
        UI.setSpinnerNumeric(spinPos, 255);
        UI.setTextField(txtRefiName, 18);
        spinPos.setDisable(true);
        setupRefiList();
        loadBlocks();
        setupListeners();
    }

    public void cleanInventory() {
        List<Integer> usedRefiPositions = new ArrayList<>();
        //The used positions are stored
        for (int i = 0; i < refiBlock.refiList.size(); i++) {
            usedRefiPositions.add(refiBlock.refiList.get(i).position());
        }
        //The unused positions are cleared out
        for (int i = 0; i < tranBlock.inventoryRefi.size(); i++) {
            if (!usedRefiPositions.contains(i)) {
                tranBlock.inventoryRefi.set(i, 0);
            }
        }
    }

    public void loadBlocks() {
        if (FireEditor.chapterFile != null) {
            this.tranBlock = FireEditor.chapterFile.blockTran;
            this.refiBlock = FireEditor.chapterFile.blockRefi;
            //The regular item table is loaded
            cleanInventory();
            loadItemTable(tranBlock.inventoryMain);
            int regularItemCount = tranBlock.regularItemCount();
            for (int i = comboWeaponId.getItems().size(); i < regularItemCount; i++) {
                comboWeaponId.getItems().add("Modded Item #" + (i - Constants.MAX_ITEM_COUNT));
            }
            //The refinement listview is loaded
            loadRefiTable(refiBlock);
            updateRefiCount();
        }
    }

    public void setupListeners() {
        checkRefiFlag.selectedProperty().addListener((observable, oldValue, newValue) -> {
            listViewRefi.getSelectionModel().getSelectedItem().setFlagEnemy(newValue);
        });
        comboWeaponId.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (listViewRefi.getSelectionModel().getSelectedItem() != null) {
                listViewRefi.getSelectionModel().getSelectedItem().setWeaponId(newValue.intValue());
                lblRefiAmount.setText(amountString(newValue.intValue(),
                        tranBlock.inventoryRefi.get(listViewRefi.getSelectionModel().getSelectedItem().position())));
            }
        });
        txtRefiName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (listViewRefi.getSelectionModel().getSelectedItem() != null) {
                listViewRefi.getSelectionModel().getSelectedItem().setName(newValue);
            }
        });
        spinRefiUse.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (listViewRefi.getSelectionModel().getSelectedItem() != null && newValue != null) {
                spinRefiUse.increment(0);
                tranBlock.setForgedUses(listViewRefi.getSelectionModel().getSelectedItem().position(),
                        spinRefiUse.getValue());
                lblRefiAmount.setText(amountString(listViewRefi.getSelectionModel().getSelectedItem().weaponId(),
                        spinRefiUse.getValue()));
            }
        });
        spinMight.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (listViewRefi.getSelectionModel().getSelectedItem() != null && newValue != null) {
                spinMight.increment(0);
                listViewRefi.getSelectionModel().getSelectedItem().setMight(spinMight.getValue());
            }
        });
        spinHit.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (listViewRefi.getSelectionModel().getSelectedItem() != null && newValue != null) {
                spinHit.increment(0);
                listViewRefi.getSelectionModel().getSelectedItem().setHit(spinHit.getValue());
            }
        });
        spinCrit.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            spinCrit.increment(0);
            if (listViewRefi.getSelectionModel().getSelectedItem() != null && newValue != null) {
                listViewRefi.getSelectionModel().getSelectedItem().setCrit(spinCrit.getValue());
            }
        });
    }

    public void loseSpinnerFocus() {
        spinMight.increment(0);
        spinRefiUse.increment(0);
        spinHit.increment(0);
        spinCrit.increment(0);
    }

    public void setRefiFields(Refinement refi) {
        comboWeaponId.getSelectionModel().select(refi.weaponId());
        spinRefiUse.getValueFactory().setValue(tranBlock.inventoryRefi.get(refi.position()));
        lblRefiAmount.setText(amountString(refi.weaponId(), tranBlock.inventoryRefi.get(refi.position())));
        txtRefiName.setText(refi.getName());
        spinMight.getValueFactory().setValue(refi.might());
        spinCrit.getValueFactory().setValue(refi.crit());
        spinHit.getValueFactory().setValue(refi.hit());
        spinPos.getValueFactory().setValue(refi.position());
        checkRefiFlag.setSelected(refi.isEnemy());
    }

    public void loadRefiTable(RefiBlock refiBlock) {
        if (FireEditor.chapterFile != null) {
            int size = refiBlock.refiList.size();
            if (size == 0) {
                disableRefi(true);
                return;
            }
            disableRefi(false);
            updateRefiCount();
            listViewRefi.setItems(FXCollections.observableArrayList(refiBlock.refiList));
            listViewRefi.getSelectionModel().selectLast();
            listViewRefi.getSelectionModel().selectFirst();
        }
    }

    public void setupRefiList() {
        listViewRefi.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Refinement ref, boolean empty) {
                super.updateItem(ref, empty);
                if (empty || ref == null) {
                    setText(null);
                } else {
                    setText(ref.toString());
                }
            }
        });
        listViewRefi.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Refinement refinement = new Refinement(newValue.bytes());
                // When another Refinement is selected, the last refi is updated and the new one is loaded
                //updateUnitFromFields(oldValue);
                setRefiFields(refinement);
            } //else disableElements(true);
        });
    }

    public void disableRefi(boolean disable) {
        spinRefiUse.setDisable(disable);
        spinMight.setDisable(disable);
        spinHit.setDisable(disable);
        spinCrit.setDisable(disable);
        checkRefiFlag.setDisable(disable);
        txtRefiName.setDisable(disable);
        comboWeaponId.setDisable(disable);
        listViewRefi.setDisable(disable);
    }

    //Loads the table with a regular item amount list
    public void loadItemTable(List<Integer> inventoryMain) {
        try {
            //The tables are cleared up
            tableConvoy.getItems().clear();
            List<String> itemNames = Names.getItemNames(tranBlock.regularItemCount());
            //The values are loaded
            for (int i = 0; i < itemNames.size(); i++) {
                tableConvoy.getItems().add(new DataItem(itemNames.get(i), inventoryMain.get(i), i));
            }
            //The table columns are set up
            if (!addedListeners) {
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("stringData"));
                amountColumn.setCellValueFactory(new PropertyValueFactory<>("numberData"));
                amountColumn.setCellFactory(spinnerCellFactory());
                totalUsesColumn.setCellValueFactory(cellData -> cellData.getValue().totalUsesProperty());
            }
            addedListeners = true;
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    //The list is not fully override if there are modded item slots
    public List<Integer> getItemUses() {
        List<Integer> numberValues = new ArrayList<>();
        for (DataItem dataItem : tableConvoy.getItems()) {
            numberValues.add(dataItem.getNumberData());
        }
        return numberValues;
    }


    //Setups somehow the table spinners
    private Callback<TableColumn<DataItem, Integer>, TableCell<DataItem, Integer>> spinnerCellFactory() {
        return column -> new TableCell<>() {
            private final Spinner<Integer> spinner = new Spinner<>(0, Integer.MAX_VALUE, 0);

            {
                UI.setSpinnerNumeric(spinner, 65535);

                spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (!isEditing()) {
                        commitEdit(newValue);
                    }
                    DataItem dataItem = getTableRow().getItem();
                    if (dataItem != null) {
                        int rowIndex = getTableRow().getIndex();
                        dataItem.setNumberData(newValue);
                        dataItem.setTotalUses(amountString(rowIndex, newValue));
                        tranBlock.inventoryMain.set(rowIndex, newValue);
                    }
                });
                spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!isEditing()) {
                        commitEdit(Integer.valueOf(newValue));
                    }
                    DataItem dataItem = getTableRow().getItem();
                    if (dataItem != null) {
                        int rowIndex = getTableRow().getIndex();
                        dataItem.setNumberData(Integer.parseInt(newValue));
                        dataItem.setTotalUses(amountString(rowIndex, Integer.parseInt(newValue)));
                        tranBlock.inventoryMain.set(rowIndex, Integer.valueOf(newValue));
                    }
                });
                spinner.setEditable(true);
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    spinner.getValueFactory().setValue(item);
                    setGraphic(spinner);
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (!isEmpty()) {
                    spinner.getValueFactory().setValue(getItem());
                }
                setGraphic(spinner);
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setGraphic(spinner);
            }

            @Override
            public void commitEdit(Integer newValue) {
                super.commitEdit(newValue);
                if (getTableRow() != null && getTableRow().getItem() != null) {
                    DataItem dataItem = (DataItem) getTableRow().getItem();
                    dataItem.setNumberData(newValue);
                    tranBlock.inventoryMain.set(dataItem.id, newValue);
                }
            }
        };
    }

    //Required to make the tableview work because Java
    public static class DataItem {
        private final String stringData;
        private int numberData;
        private final SimpleStringProperty totalUses;
        public int id;

        public DataItem(String stringData, int numberData, int id) {
            this.stringData = stringData;
            this.numberData = numberData;
            this.totalUses = new SimpleStringProperty(amountString(id, numberData));
            this.id = id;
        }

        public String getStringData() {
            return stringData;
        }

        public int getNumberData() {
            return numberData;
        }

        public void setNumberData(int data) {
            this.numberData = data;
        }

        public String getTotalUses() {
            return totalUses.get();
        }

        public void setTotalUses(String totalUses) {
            this.totalUses.set(totalUses);
        }

        public SimpleStringProperty totalUsesProperty() {
            return totalUses;
        }
    }

    public void updateRefiCount() {
        lblRefiCount.setText("Forged Weapon Count: " + listViewRefi.getItems().size());
    }


}
