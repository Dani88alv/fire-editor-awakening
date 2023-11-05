package com.danius.fireeditor.controllers.user;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.controllers.UI;
import com.danius.fireeditor.data.MiscDb;
import com.danius.fireeditor.savefile.user.UserBlock;
import com.danius.fireeditor.savefile.wireless.Du26Block;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

public class UserMiscController {

    private UserBlock userBlock;
    private Du26Block du26Block;
    @FXML
    private ComboBox<String> comboChapterDlc, comboDuelNames;
    @FXML
    private Spinner<Integer> spinDlcTurns, spinDuelTurns;
    @FXML
    private CheckBox checkDuel;

    public void initialize() {
        setupElements();
        addDlcListeners();
        addDuelListeners();
    }

    public void setBlocks(UserBlock userBlock, Du26Block du26Block) {
        this.userBlock = userBlock;
        this.du26Block = du26Block;

        comboChapterDlc.getSelectionModel().select(0);
        comboDuelNames.getSelectionModel().select(0);
    }

    public void setupElements() {
        //DLC Chapters
        UI.setSpinnerNumeric(spinDlcTurns, 255);
        ObservableList<String> dlcChapters = FXCollections.observableArrayList();
        dlcChapters.addAll(MiscDb.chapterDlcNames);
        comboChapterDlc.setItems(dlcChapters);
        //Double Duel
        UI.setSpinnerNumeric(spinDuelTurns, 255);
        ObservableList<String> duelNames = FXCollections.observableArrayList();
        duelNames.addAll(MiscDb.doubleDuelNames);
        comboDuelNames.setItems(duelNames);
    }

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

    public void addDuelListeners() {
        comboDuelNames.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    if (du26Block != null && newValue != null && oldValue != null) {
                        //The old selection is updated
                        if ((Integer) oldValue >= 0) du26Block.setDuelScore((Integer) oldValue, spinDuelTurns.getValue());
                        spinDuelTurns.getValueFactory().setValue(du26Block.getDuelScore((Integer) newValue));
                        checkDuel.setSelected(du26Block.isDuelBeaten(comboDuelNames.getSelectionModel().getSelectedIndex()));
                    }
                }
        );
        spinDuelTurns.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && du26Block != null) {
                spinDuelTurns.increment(0);
                du26Block.setDuelScore(comboDuelNames.getSelectionModel().getSelectedIndex(), spinDuelTurns.getValue());
            }
        });
        checkDuel.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && du26Block != null) {
                int slot = comboDuelNames.getSelectionModel().getSelectedIndex();
                du26Block.setDuelBeaten(slot, checkDuel.isSelected());
            }
        });
    }
}
