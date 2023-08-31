package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.controllers.UI;
import com.danius.fireeditor.savefile.inventory.RefiBlock;
import com.danius.fireeditor.savefile.inventory.TranBlock;
import com.danius.fireeditor.util.Names13;
import javafx.beans.property.SimpleStringProperty;
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
    private TableView<DataItem> tableConvoy, tableRefi;
    @FXML
    private TableColumn<DataItem, String> nameColumn, colRefiName;
    @FXML
    private TableColumn<DataItem, Integer> amountColumn, colRefiAmount;
    @FXML
    private TableColumn<DataItem, String> totalUsesColumn, colRefiUses;


    public void initialize() {
        FireEditor.convoyController = this;
        loadBlocks();
    }

    public void loadBlocks() {
        if (FireEditor.chapterFile != null) {
            this.tranBlock = FireEditor.chapterFile.blockTran;
            this.refiBlock = FireEditor.chapterFile.blockRefi;
            loadItemTable(tranBlock.inventoryMain, tranBlock.inventoryRefi);
        }
    }

    //Loads the table with a regular item amount list
    public void loadItemTable(List<Integer> inventoryMain, List<Integer> inventoryRefi) {
        try {
            //The tables are cleared up
            tableConvoy.getItems().clear();
            tableRefi.getItems().clear();
            List<String> itemNames = Names13.getItemNames(tranBlock.regularItemCount());
            List<String> refiNames = refiBlock.refiNames();
            List<Integer> refiId = refiBlock.refiIds();
            //The values are loaded
            for (int i = 0; i < itemNames.size(); i++) {
                tableConvoy.getItems().add(new DataItem(itemNames.get(i), inventoryMain.get(i), i));
            }
            for (int i = 0; i < inventoryRefi.size(); i++) {
                tableRefi.getItems().add(new DataItem(refiNames.get(i), inventoryRefi.get(i), refiId.get(i)));
            }
            //The table columns are set up
            if (!addedListeners) {
                colRefiName.setCellValueFactory(new PropertyValueFactory<>("stringData"));
                colRefiAmount.setCellValueFactory(new PropertyValueFactory<>("numberData"));
                colRefiAmount.setCellFactory(spinnerCellFactory(true));
                colRefiUses.setCellValueFactory(cellData -> cellData.getValue().totalUsesProperty());

                nameColumn.setCellValueFactory(new PropertyValueFactory<>("stringData"));
                amountColumn.setCellValueFactory(new PropertyValueFactory<>("numberData"));
                amountColumn.setCellFactory(spinnerCellFactory(false));
                totalUsesColumn.setCellValueFactory(cellData -> cellData.getValue().totalUsesProperty());
            }
            addedListeners = true;
        }
        catch (Exception e){
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

    public List<Integer> getRefiUses() {
        List<Integer> numberValues = new ArrayList<>();
        for (DataItem dataItem : tableRefi.getItems()) {
            numberValues.add(dataItem.getNumberData());
        }
        return numberValues;
    }


    //Setups somehow the table spinners
    private Callback<TableColumn<DataItem, Integer>, TableCell<DataItem, Integer>> spinnerCellFactory(boolean refi) {
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
                        if (!refi) {
                            int rowIndex = getTableRow().getIndex();
                            dataItem.setNumberData(newValue);
                            dataItem.setTotalUses(amountString(rowIndex, newValue));
                        } else {
                            dataItem.setNumberData(newValue);
                            dataItem.setTotalUses(amountString(dataItem.id, newValue));
                        }
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


}
