package com.danius.fireeditor.controllers.user;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.controllers.MainController;
import com.danius.fireeditor.controllers.UI;
import com.danius.fireeditor.data.MiscDb;
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

import java.util.HashMap;
import java.util.Map;

public class UserController {
    public UserBlock userBlock;
    public HeaderBlock headerBlock;
    public Du26Block du26Block;
    public GmapBlock gmapBlock;
    public EvstBlock evstBlock;
    @FXML
    private Spinner<Integer> spinTime, spinMoney, spinDlcTurns, spinRenown;
    @FXML
    private ComboBox<String> comboDifficulty;
    @FXML
    private CheckBox checkLunatic, checkCasual,
            checkGlobal0, checkGlobal1, checkGlobal3, checkGlobal4;

    public void initialize() {
        FireEditor.userController = this;
        setupElements();
        addUserListeners();
        loadBlocks();
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
            //Global flags
            checkGlobal0.setSelected(userBlock.hasGlobalFlag(0));
            checkGlobal1.setSelected(userBlock.hasGlobalFlag(1));
            checkGlobal3.setSelected(userBlock.hasGlobalFlag(3));
            checkGlobal4.setSelected(userBlock.hasGlobalFlag(4));
        }
    }

    public void setupElements() {
        //General data
        UI.setSpinnerTimer(spinTime, 216000000);
        UI.setSpinnerNumeric(spinMoney, 999999);
        UI.setSpinnerNumeric(spinRenown, 99999);
        ObservableList<String> difficulty = FXCollections.observableArrayList
                ("Normal", "Hard", "Lunatic");
        comboDifficulty.setItems(difficulty);
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
        //Global Flags
        Map<CheckBox, Integer> checkboxToNumber = new HashMap<>();
        checkboxToNumber.put(checkGlobal0, 0);
        checkboxToNumber.put(checkGlobal1, 1);
        checkboxToNumber.put(checkGlobal3, 3);
        checkboxToNumber.put(checkGlobal4, 4);
        for (CheckBox checkBox : checkboxToNumber.keySet()) {
            int number = checkboxToNumber.get(checkBox);
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                userBlock.setGlobalFlag(number, newValue);
            });
        }


    }

    public void openCredits() {
        try {
            FXMLLoader fxmlLoader = MainController.getWindowUser("viewCredits.fxml");
            Parent root = fxmlLoader.load();
            // Pass the selected value to the second view's controller
            StoryController storyController = fxmlLoader.getController();
            storyController.setBlock(userBlock);
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
            FXMLLoader fxmlLoader = MainController.getWindowUser("viewBarrack.fxml");
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
            FXMLLoader fxmlLoader = MainController.getWindowUser("viewOverworld.fxml");
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
            FXMLLoader fxmlLoader = MainController.getWindowUser("viewTeams.fxml");
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

    public void openMisc() {
        try {
            FXMLLoader fxmlLoader = MainController.getWindowUser("viewUserMisc.fxml");
            Parent root = fxmlLoader.load();
            // Pass the selected value to the second view's controller
            UserMiscController controller = fxmlLoader.getController();
            controller.setBlocks(userBlock, du26Block);
            // Create a new stage for the secondary view
            Stage secondaryStage = new Stage();
            secondaryStage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
            secondaryStage.setTitle("Miscellaneous Data");
            secondaryStage.setScene(new Scene(root));
            secondaryStage.showAndWait(); // Show the secondary view and wait until it's closed

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
