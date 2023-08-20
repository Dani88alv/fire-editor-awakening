package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.bigblocks.Du26Block;
import com.danius.fireeditor.savefile.bigblocks.GmapBlock;
import com.danius.fireeditor.savefile.bigblocks.HeaderBlock;
import com.danius.fireeditor.savefile.bigblocks.UserBlock;
import com.danius.fireeditor.util.Names;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ChapterController {
    public UserBlock userBlock;
    public HeaderBlock headerBlock;
    public Du26Block du26Block;
    public GmapBlock gmapBlock;
    private boolean listenersAdded = false;
    @FXML
    private Spinner<Integer> spinCreditTime, spinCreditTurns,
            spinTime, spinMoney, spinPenalty, spinDlcTurns,
            spinRenown;
    @FXML
    private ComboBox<String> comboChapterDlc, comboCreditChapter,
            comboUnit1, comboUnit2, comboClass1, comboClass2, comboCreditSlot,
            comboDifficulty, comboChapter, comboChapterData;
    @FXML
    private CheckBox checkLunatic;

    public void initialize() {
        FireEditor.chapterController = this;
        setupElements();
        addCreditListeners();
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
        userBlock.rawDifficulty.setPenalty(spinPenalty.getValue());
        headerBlock.rawDifficulty.setPenalty(spinPenalty.getValue());
        userBlock.rawDifficulty.setLunaticPlus(checkLunatic.isSelected());
        headerBlock.rawDifficulty.setLunaticPlus(checkLunatic.isSelected());
        userBlock.setPlaytime(spinTime.getValue() * 60);
        headerBlock.setPlaytime(spinTime.getValue() * 60);
        userBlock.setMoney(spinMoney.getValue());
        userBlock.setRenown(spinRenown.getValue());
        du26Block.setDuelRenown(spinRenown.getValue());
        //Credits and DLC
        du26Block.setDlcTurn(comboChapterDlc.getSelectionModel().getSelectedIndex(), spinDlcTurns.getValue());
        updateCredits(comboCreditSlot.getSelectionModel().getSelectedIndex());
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
            spinPenalty.getValueFactory().setValue(userBlock.rawDifficulty.penaltyId());
            checkLunatic.setSelected(userBlock.rawDifficulty.isLunaticPlus());
            spinMoney.getValueFactory().setValue(userBlock.money());
            spinTime.getValueFactory().setValue(userBlock.playtime() / 60);
            spinRenown.getValueFactory().setValue(userBlock.renown());
            //Credits Data
            ObservableList<String> progress = FXCollections.observableArrayList();
            for (int i = 0; i < userBlock.progress.size(); i++) {
                progress.add("Chapter #" + (i + 1));
            }
            comboCreditSlot.setItems(progress);
            //Disable the fields if the chapter count is 0 (Premonition Map Save File)
            disableCredits(userBlock.progress.size() == 0);
            if (userBlock.progress.size() > 0) {
                comboCreditSlot.getSelectionModel().select(0);
            }
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
        UI.setSpinnerNumeric(spinCreditTurns, 255);
        UI.setSpinnerTimer(spinCreditTime, 216000000);
        UI.setSpinnerNumeric(spinDlcTurns, 255);
        //General data
        UI.setSpinnerTimer(spinTime, 216000000);
        UI.setSpinnerNumeric(spinMoney, 999999);
        UI.setSpinnerNumeric(spinRenown, 99999);
        UI.setSpinnerNumeric(spinPenalty, 255);
        ObservableList<String> difficulty = FXCollections.observableArrayList("Normal", "Hard", "Lunatic");
        comboDifficulty.setItems(difficulty);
        //Units
        ObservableList<String> units = FXCollections.observableArrayList();
        units.add("None");
        units.addAll(Names.unitNames);
        comboUnit1.setItems(units);
        comboUnit2.setItems(units);
        //Classes
        ObservableList<String> classes = FXCollections.observableArrayList();
        classes.add("None");
        classes.addAll(Names.classNames);
        comboClass1.setItems(classes);
        comboClass2.setItems(classes);
        //Chapter Names
        ObservableList<String> chapters = FXCollections.observableArrayList();
        for (int i = 0; i < 52; i++) {
            chapters.add(chapterName(i));
        }
        comboCreditChapter.setItems(chapters);
        //DLC Chapters
        ObservableList<String> dlcChapters = FXCollections.observableArrayList();
        dlcChapters.addAll(Names.chapterDlcNames);
        comboChapterDlc.setItems(dlcChapters);
        //Gmap
        ObservableList<String> chapterState = FXCollections.observableArrayList("Locked", "Beaten", "Not Beaten");
        comboChapterData.setItems(chapterState);
    }

    public void resetRenownFlags(){
        userBlock.resetRenownFlags();
    }

    public void addCreditListeners() {
        comboCreditSlot.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    if (userBlock != null && newValue != null && userBlock.progress.size() > 0 && !newValue.equals(-1)) {
                        //The old selection is updated
                        updateCredits((Integer) oldValue);
                        int slot = comboCreditSlot.getSelectionModel().getSelectedIndex();
                        //Unit slots
                        int unit1 = userBlock.progress.get(slot).unitFirst();
                        int unit2 = userBlock.progress.get(slot).unitSecond();
                        if (unit1 == 65535) unit1 = -1;
                        if (unit2 == 65535) unit2 = -1;
                        //Class slots
                        int class1 = userBlock.progress.get(slot).classFirst();
                        int class2 = userBlock.progress.get(slot).classSecond();
                        if (class1 == 65535) class1 = -1;
                        if (class2 == 65535) class2 = -1;
                        //Selected
                        comboUnit1.getSelectionModel().select(unit1 + 1);
                        comboUnit2.getSelectionModel().select(unit2 + 1);
                        comboClass1.getSelectionModel().select(class1 + 1);
                        comboClass2.getSelectionModel().select(class2 + 1);
                        comboCreditChapter.getSelectionModel().select(userBlock.progress.get(slot).chapterId());
                        spinCreditTurns.getValueFactory().setValue(userBlock.progress.get(slot).turns());
                        int playtime = userBlock.progress.get(slot).playTime() / 60;
                        spinCreditTime.getValueFactory().setValue(playtime);
                    }
                }

        );
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

    public void updateCredits(int slot) {
        if (slot == -1 || comboCreditSlot.isDisable()) return;
        //The units and classes are fixed
        int unit1 = comboUnit1.getSelectionModel().getSelectedIndex() - 1;
        if (unit1 == -1) unit1 = 0xFFFF;
        int unit2 = comboUnit2.getSelectionModel().getSelectedIndex() - 1;
        if (unit2 == -1) unit2 = 0xFFFF;
        int class1 = comboClass1.getSelectionModel().getSelectedIndex() - 1;
        if (class1 == -1) class1 = 0xFFFF;
        int class2 = comboClass2.getSelectionModel().getSelectedIndex() - 1;
        if (class2 == -1) class2 = 0xFFFF;
        //The values are updated
        userBlock.progress.get(slot).setChapterId(comboCreditChapter.getSelectionModel().getSelectedIndex());
        userBlock.progress.get(slot).setTurns(spinCreditTurns.getValue());
        userBlock.progress.get(slot).setTime(spinCreditTime.getValue() * 60);
        userBlock.progress.get(slot).setUnitFirst(unit1);
        userBlock.progress.get(slot).setUnitSecond(unit2);
        userBlock.progress.get(slot).setClassFirst(class1);
        userBlock.progress.get(slot).setClassSecond(class2);
    }

    public void disableCredits(boolean disable) {
        comboCreditSlot.setDisable(disable);
        comboCreditChapter.setDisable(disable);
        comboUnit1.setDisable(disable);
        comboUnit2.setDisable(disable);
        comboClass1.setDisable(disable);
        comboClass2.setDisable(disable);
        spinCreditTime.setDisable(disable);
        spinCreditTurns.setDisable(disable);
    }

    public String chapterName(int id) {
        if (id == 0) return "Xenologue";
        if (id == 1) return "Premonition";
        if (id == 2) return "Prologue";
        if (id > 2 && id <= 28) {
            int chapter = id - 2;
            return "Chapter " + chapter;
        } else if (id > 28 && id <= 51) {
            int chapter = id - 28;
            return "Paralogue " + chapter;
        } else return "Unknown";
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
            return "Modded Map";
        }
    }
}
