package com.danius.fireeditor.controllers.user;

import com.danius.fireeditor.controllers.UI;
import com.danius.fireeditor.data.MiscDb;
import com.danius.fireeditor.savefile.user.UserBlock;
import com.danius.fireeditor.savefile.wireless.Du26Block;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

public class UserMiscController {

    private UserBlock userBlock;
    private Du26Block du26Block;

    @FXML
    private ComboBox<String> comboChapterDlc;
    @FXML
    private Spinner<Integer> spinDlcTurns;

    public void initialize() {
        setupElements();
        addDlcListeners();
    }

    public void setBlocks(UserBlock userBlock, Du26Block du26Block) {
        this.userBlock = userBlock;
        this.du26Block = du26Block;

        comboChapterDlc.getSelectionModel().select(0);
    }

    public void setupElements() {
        //DLC Chapters
        UI.setSpinnerNumeric(spinDlcTurns, 255);
        ObservableList<String> dlcChapters = FXCollections.observableArrayList();
        dlcChapters.addAll(MiscDb.chapterDlcNames);
        comboChapterDlc.setItems(dlcChapters);
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
}
