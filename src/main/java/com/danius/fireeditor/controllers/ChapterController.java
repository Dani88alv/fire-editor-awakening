package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.Constants13;
import com.danius.fireeditor.savefile.other.Du26Block;
import com.danius.fireeditor.savefile.other.GmapBlock;
import com.danius.fireeditor.savefile.other.HeaderBlock;
import com.danius.fireeditor.savefile.other.UserBlock;
import com.danius.fireeditor.util.Names13;
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

    @FXML
    private Spinner<Integer> spinTime, spinMoney, spinDlcTurns, spinRenown;
    @FXML
    private ComboBox<String> comboChapterDlc, comboDifficulty;
    @FXML
    ComboBox<String> comboChapter, comboChapterData;
    @FXML
    private CheckBox checkLunatic, checkCasual;

    public void initialize() {
        FireEditor.chapterController = this;
        setupElements();
        addDlcListeners();
        addGmapListeners();
        loadBlocks();
    }

    public void addSpotpass() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("SpotPass Data");
        alert.setHeaderText("This will reset all the SpotPass content. Proceed? \n" +
                "WARNING: This feature has not been fully tested!");
        alert.setContentText("If you already had SpotPass content downloaded, this will reset all the \"new\" flags " +
                "(the recruited units will not be removed).");
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

    public void compileValues() {
        //Updates the general settings in the user block and header block
        userBlock.rawDifficulty.setDifficulty(comboDifficulty.getSelectionModel().getSelectedIndex());
        headerBlock.rawDifficulty.setDifficulty(comboDifficulty.getSelectionModel().getSelectedIndex());
        //Difficulty
        userBlock.rawDifficulty.setLunaticPlus(checkLunatic.isSelected());
        userBlock.rawDifficulty.setGameModeFlag(2, checkCasual.isSelected());
        headerBlock.rawDifficulty.setLunaticPlus(checkLunatic.isSelected());
        headerBlock.rawDifficulty.setGameModeFlag(2, checkCasual.isSelected());
        //Other
        userBlock.setPlaytime(spinTime.getValue() * 60);
        headerBlock.setPlaytime(spinTime.getValue() * 60);
        userBlock.setMoney(spinMoney.getValue());
        userBlock.setRenown(spinRenown.getValue());
        du26Block.setWirelessRenown(spinRenown.getValue());
        //Credits and DLC
        du26Block.setDlcTurn(comboChapterDlc.getSelectionModel().getSelectedIndex(), spinDlcTurns.getValue());
        //Gmap
        gmapBlock.maps.get(comboChapter.getSelectionModel().getSelectedIndex()).setLockState(
                comboChapterData.getSelectionModel().getSelectedIndex());
    }

    public void loadBlocks() {
        if (FireEditor.chapterFile != null) {
            this.userBlock = FireEditor.chapterFile.blockUser;
            this.headerBlock = FireEditor.chapterFile.blockHeader;
            this.du26Block = FireEditor.chapterFile.blockDu26;
            this.gmapBlock = FireEditor.chapterFile.blockGmap;
            //General Data
            comboDifficulty.getSelectionModel().select(userBlock.rawDifficulty.difficulty());
            checkLunatic.setSelected(userBlock.rawDifficulty.isLunaticPlus());
            checkCasual.setSelected(userBlock.rawDifficulty.gameModeFlag(2));
            spinMoney.getValueFactory().setValue(userBlock.money());
            spinTime.getValueFactory().setValue(userBlock.playtime() / 60);
            spinRenown.getValueFactory().setValue(userBlock.renown());
            //DLC
            comboChapterDlc.getSelectionModel().select(0);
            //Gmap Chapters
            ObservableList<String> chaptersMap = FXCollections.observableArrayList();
            for (int i = 0; i < gmapBlock.maps.size(); i++) {
                chaptersMap.add(gChapterName(i));
            }
            comboChapter.setItems(chaptersMap);
            comboChapter.getSelectionModel().select(0);
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
        dlcChapters.addAll(Names13.chapterDlcNames);
        comboChapterDlc.setItems(dlcChapters);
        //Gmap
        ObservableList<String> chapterState = FXCollections.observableArrayList("Locked", "Beaten", "Unlocked");
        comboChapterData.setItems(chapterState);
    }

    public void resetRenownFlags() {
        userBlock.resetRenownFlags();
    }

    public void addDlcListeners() {
        comboChapterDlc.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    if (userBlock != null && newValue != null && oldValue != null) {
                        //The old selection is updated
                        if ((Integer) oldValue >= 0) du26Block.setDlcTurn((Integer) oldValue, spinDlcTurns.getValue());
                        spinDlcTurns.getValueFactory().setValue(du26Block.dlcTurn((Integer) newValue));
                    }
                }
        );
    }

    public void addGmapListeners() {
        comboChapter.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    if (gmapBlock != null && newValue != null && oldValue != null) {
                        //The old selection is updated
                        if ((Integer) oldValue >= 0) {
                            gmapBlock.maps.get((Integer) oldValue).setLockState(
                                    comboChapterData.getSelectionModel().getSelectedIndex());
                        }
                        if ((Integer) newValue != -1) {
                            comboChapterData.getSelectionModel().select(gmapBlock.maps.get((Integer) newValue).lockState());
                        }
                    }
                }
        );
    }

    public void openCredits() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FireEditor.class.getResource("viewCredits.fxml"));
            Parent root = fxmlLoader.load();
            // Pass the selected value to the second view's controller
            CreditController creditController = fxmlLoader.getController();
            creditController.setBlock(userBlock);
            // Create a new stage for the secondary view
            Stage secondaryStage = new Stage();
            secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
            secondaryStage.setTitle("Credits Records");
            secondaryStage.setScene(new Scene(root));
            secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String gChapterName(int id) {
        if (id == 0) return "Prologue";
        if (id > 0 && id <= 26) {
            return "Chapter " + id;
        } else if (id > 26 && id <= 49) {
            int chapter = id - 26;
            return "Paralogue " + chapter;
        } else if (id == 50) return "Outrealm Gate";
        else {
            return "Mod Chapter #" + (Constants13.MAX_CHAPTERS - id + 1);
        }
    }
}
