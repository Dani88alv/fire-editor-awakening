package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.model.MiscDb;
import com.danius.fireeditor.savefile.barrack.EvstBlock;
import com.danius.fireeditor.savefile.map.GmapBlock;
import com.danius.fireeditor.savefile.user.HeaderBlock;
import com.danius.fireeditor.savefile.user.UserBlock;
import com.danius.fireeditor.savefile.wireless.Du26Block;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChapterController {
    public UserBlock userBlock;
    public HeaderBlock headerBlock;
    public Du26Block du26Block;
    public GmapBlock gmapBlock;
    public EvstBlock evstBlock;
    @FXML
    private Spinner<Integer> spinTime, spinMoney, spinDlcTurns, spinRenown;
    @FXML
    private ComboBox<String> comboChapterDlc, comboDifficulty;
    @FXML
    private CheckBox checkLunatic, checkCasual;

    public void initialize() {
        FireEditor.chapterController = this;
        setupElements();
        addUserListeners();
        //addDlcListeners();
        loadBlocks();
    }

    public void addSpotpass() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("SpotPass Data");
        alert.setHeaderText("This will reset all the SpotPass content. Proceed? \n" +
                "WARNING: This feature has not been fully tested!");
        alert.setContentText("If you already have SpotPass content downloaded, nothing will be modified.");
        // Add Confirm and Cancel buttons
        ButtonType confirmButton = new ButtonType("Confirm");
        ButtonType cancelButton = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        // Show the dialog and wait for a response
        alert.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                du26Block.addSpotpass();
            } else if (response == cancelButton) {
                return;
            }
        });
    }

    public void loadBlocks() {
        if (FireEditor.chapterFile != null) {
            this.userBlock = FireEditor.chapterFile.blockUser;
            this.headerBlock = FireEditor.chapterFile.blockHeader;
            this.du26Block = FireEditor.chapterFile.blockDu26;
            this.gmapBlock = FireEditor.chapterFile.blockGmap;
            this.evstBlock = FireEditor.chapterFile.blockEvst;
            //General Data
            comboDifficulty.getSelectionModel().select(userBlock.difficulty());
            checkLunatic.setSelected(userBlock.isLunaticPlus());
            checkCasual.setSelected(userBlock.gameModeFlag(2));
            spinMoney.getValueFactory().setValue(userBlock.money());
            spinTime.getValueFactory().setValue(userBlock.playtime() / 60);
            spinRenown.getValueFactory().setValue(userBlock.renown());
            //DLC
            //comboChapterDlc.getSelectionModel().select(0);
        }
    }

    public void setupElements() {
        UI.setSpinnerNumeric(spinDlcTurns, 255);
        //General data
        UI.setSpinnerTimer(spinTime, 216000000);
        UI.setSpinnerNumeric(spinMoney, 999999);
        UI.setSpinnerNumeric(spinRenown, 99999);
        ObservableList<String> difficulty = FXCollections.observableArrayList("Normal", "Hard", "Lunatic");
        comboDifficulty.setItems(difficulty);
        //DLC Chapters
        ObservableList<String> dlcChapters = FXCollections.observableArrayList();
        dlcChapters.addAll(MiscDb.chapterDlcNames);
        //comboChapterDlc.setItems(dlcChapters);
    }

    public void resetRenownFlags() {
        userBlock.resetRenownFlags();
    }

    public void addUserListeners() {
        spinTime.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && userBlock != null) {
                int value = spinTime.getValue();
                userBlock.setPlaytime(value * 60);
                headerBlock.setPlaytime(value * 60);
            }
        });
        spinMoney.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && userBlock != null) userBlock.setMoney(Integer.parseInt(newValue));
        });
        spinRenown.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && userBlock != null) {
                userBlock.setRenown(Integer.parseInt(newValue));
                du26Block.playerTeam.setRenown(Integer.parseInt(newValue));
            }
        });
        checkCasual.selectedProperty().addListener((observable, oldValue, newValue) -> {
            userBlock.setGameModeFlag(2, newValue);
            headerBlock.setGameModeFlag(2, newValue);
        });
        checkLunatic.selectedProperty().addListener((observable, oldValue, newValue) -> {
            userBlock.setLunaticPlus(newValue);
            headerBlock.setLunaticPlus(newValue);
        });
        comboDifficulty.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            userBlock.setDifficulty((Integer) newValue);
            headerBlock.setDifficulty((Integer) newValue);
        });
        comboDifficulty.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                userBlock.setDifficulty(comboDifficulty.getSelectionModel().getSelectedIndex());
                headerBlock.setDifficulty(comboDifficulty.getSelectionModel().getSelectedIndex());
            }
        });
    }

    /*
    public void addDlcListeners() {
        comboChapterDlc.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    if (du26Block != null && newValue != null && oldValue != null) {
                        //The old selection is updated
                        if ((Integer) oldValue >= 0) du26Block.setDlcTurn((Integer) oldValue, spinDlcTurns.getValue());
                        spinDlcTurns.getValueFactory().setValue(du26Block.dlcTurn((Integer) newValue));
                    }
                }
        );
        spinDlcTurns.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && du26Block != null) {
                spinDlcTurns.increment(0);
                du26Block.setDlcTurn(comboChapterDlc.getSelectionModel().getSelectedIndex(), spinDlcTurns.getValue());
            }
        });
    }

     */

    public void openCredits() {
        try {
            FXMLLoader fxmlLoader = MainController.getWindow("viewCredits.fxml");
            Parent root = fxmlLoader.load();
            // Pass the selected value to the second view's controller
            CreditController creditController = fxmlLoader.getController();
            creditController.setBlock(userBlock);
            // Create a new stage for the secondary view
            Stage secondaryStage = new Stage();
            secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
            secondaryStage.setTitle("Story Progress");
            secondaryStage.setScene(new Scene(root));
            secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openBarrack() {
        try {
            FXMLLoader fxmlLoader = MainController.getWindow("viewBarrack.fxml");
            Parent root = fxmlLoader.load();
            // Pass the selected value to the second view's controller
            BarrackController barrackController = fxmlLoader.getController();
            barrackController.setEvstBlock(evstBlock);
            // Create a new stage for the secondary view
            Stage secondaryStage = new Stage();
            secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
            secondaryStage.setTitle("Barrack Events");
            secondaryStage.setScene(new Scene(root));
            secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openMap() {
        try {
            FXMLLoader fxmlLoader = MainController.getWindow("viewOverworld.fxml");
            Parent root = fxmlLoader.load();
            // Pass the selected value to the second view's controller
            MapController mapController = fxmlLoader.getController();
            mapController.setBlock(gmapBlock);
            // Create a new stage for the secondary view
            Stage secondaryStage = new Stage();
            secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
            secondaryStage.setTitle("Overworld Map");
            secondaryStage.setScene(new Scene(root));
            secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openWireless() {
        try {
            FXMLLoader fxmlLoader = MainController.getWindow("viewTeams.fxml");
            Parent root = fxmlLoader.load();
            // Pass the selected value to the second view's controller
            TeamController teamController = fxmlLoader.getController();
            teamController.setBlocks(du26Block, gmapBlock);
            // Create a new stage for the secondary view
            Stage secondaryStage = new Stage();
            secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
            secondaryStage.setTitle("Wireless Teams");
            secondaryStage.setScene(new Scene(root));
            secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
