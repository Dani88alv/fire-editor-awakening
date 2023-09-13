package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.other.*;
import com.danius.fireeditor.savefile.wireless.Du26Block;
import com.danius.fireeditor.util.Names;
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
    ComboBox<String> comboChapter, comboChapterData;
    @FXML
    private CheckBox checkLunatic, checkCasual;

    public void initialize() {
        FireEditor.chapterController = this;
        setupElements();
        addUserListeners();
        //addDlcListeners();
        addGmapListeners();
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

    public void compileValues() {
        //Updates the general settings in the user block and header block
        userBlock.rawDifficulty.setDifficulty(comboDifficulty.getSelectionModel().getSelectedIndex());
        headerBlock.rawDifficulty.setDifficulty(comboDifficulty.getSelectionModel().getSelectedIndex());
        //Difficulty
        userBlock.rawDifficulty.setLunaticPlus(checkLunatic.isSelected());
        userBlock.rawDifficulty.setGameModeFlag(2, checkCasual.isSelected());
        headerBlock.rawDifficulty.setLunaticPlus(checkLunatic.isSelected());
        headerBlock.rawDifficulty.setGameModeFlag(2, checkCasual.isSelected());
    }

    public void loadBlocks() {
        if (FireEditor.chapterFile != null) {
            this.userBlock = FireEditor.chapterFile.blockUser;
            this.headerBlock = FireEditor.chapterFile.blockHeader;
            this.du26Block = FireEditor.chapterFile.blockDu26;
            this.gmapBlock = FireEditor.chapterFile.blockGmap;
            this.evstBlock = FireEditor.chapterFile.blockEvst;
            //General Data
            comboDifficulty.getSelectionModel().select(userBlock.rawDifficulty.difficulty());
            checkLunatic.setSelected(userBlock.rawDifficulty.isLunaticPlus());
            checkCasual.setSelected(userBlock.rawDifficulty.gameModeFlag(2));
            spinMoney.getValueFactory().setValue(userBlock.money());
            spinTime.getValueFactory().setValue(userBlock.playtime() / 60);
            spinRenown.getValueFactory().setValue(userBlock.renown());
            //DLC
            //comboChapterDlc.getSelectionModel().select(0);
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
        dlcChapters.addAll(Names.chapterDlcNames);
        //comboChapterDlc.setItems(dlcChapters);
        //Gmap
        ObservableList<String> chapterState = FXCollections.observableArrayList("Locked", "Beaten", "Unlocked");
        comboChapterData.setItems(chapterState);
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
            if (newValue != null && userBlock != null) userBlock.setRenown(Integer.parseInt(newValue));
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
        comboChapterData.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    if (gmapBlock != null && newValue != null && oldValue != null &&
                            comboChapter.getSelectionModel().getSelectedItem() != null) {
                        gmapBlock.maps.get(comboChapter.getSelectionModel().getSelectedIndex()).setLockState(
                                comboChapterData.getSelectionModel().getSelectedIndex());
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
            secondaryStage.setTitle("Story Progress");
            secondaryStage.setScene(new Scene(root));
            secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openBarrack(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FireEditor.class.getResource("viewBarrack.fxml"));
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

    public String gChapterName(int id) {
        if (id == 0) return "Prologue";
        if (id > 0 && id <= 26) {
            return "Chapter " + id;
        } else if (id > 26 && id <= 49) {
            int chapter = id - 26;
            return "Paralogue " + chapter;
        } else if (id == 50) return "Outrealm Gate";
        else {
            return "Mod Chapter #" + (Constants.MAX_CHAPTERS - id + 1);
        }
    }
}
