package com.danius.fireeditor.controllers;

import com.danius.fireeditor.FireEditor;
import com.danius.fireeditor.savefile.Constants;
import com.danius.fireeditor.savefile.other.UserBlock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

public class CreditController {

    private UserBlock userBlock;
    @FXML
    private Spinner<Integer> spinCreditTime, spinCreditTurns;
    @FXML
    private ComboBox<String> comboCreditChapter,
            comboUnit1, comboUnit2, comboClass1, comboClass2, comboCreditSlot;
    @FXML
    private Button btnRemoveRecord;

    public void initialize() {
        setupElements();
        //The modded classes are checked
        int modCount = FireEditor.maxClasses;
        int vanillaCount = Constants.MAX_CLASSES;
        if (modCount > vanillaCount) {
            for (int i = vanillaCount; i < modCount; i++) {
                String className = "Mod Class#" + (i + 1 - vanillaCount);
                comboClass1.getItems().add(className);
            }
        }
    }

    public void removeRecord() {
        int position = comboCreditSlot.getSelectionModel().getSelectedIndex();

        if (position >= 0) {
            userBlock.progress.remove(position);
            comboCreditSlot.getItems().remove(position);

            int size = comboCreditSlot.getItems().size();

            if (size > 0) {
                // If there are items left, select the first item
                comboCreditSlot.getSelectionModel().select(0);
            } else {
                // If no items are left, handle the case accordingly
                disableCredits(true);
            }
        }
    }


    public void setBlock(UserBlock userBlock) {
        this.userBlock = userBlock;
        addListeners();
        setFields();
    }

    public void setFields() {
        //Credits Data
        ObservableList<String> progress = FXCollections.observableArrayList();
        for (int i = 0; i < userBlock.progress.size(); i++) {
            progress.add("Chapter #" + (i + 1));
        }
        comboCreditSlot.setItems(progress);
        //Disable the fields if the chapter count is 0 (Premonition Map Save File)
        disableCredits(userBlock.progress.size() == 0);
        //The modded map entries from the credits are checked
        int totalChapters = comboCreditChapter.getItems().size();
        //Modded Chapters are checked
        for (int i = 0; i < userBlock.progress.size(); i++) {
            if (userBlock.progress.get(i).chapterId() > totalChapters) {
                totalChapters = userBlock.progress.get(i).chapterId();
            }
        }
        int currentChapters = comboCreditChapter.getItems().size();
        for (int i = currentChapters; i <= totalChapters; i++) {
            comboCreditChapter.getItems().add("Mod Chapter #" + (i - currentChapters + 1));
        }
        if (userBlock.progress.size() > 0) {
            comboCreditSlot.getSelectionModel().select(0);
        }
    }

    public void setupElements() {
        UI.setSpinnerNumeric(spinCreditTurns, 255);
        UI.setSpinnerTimer(spinCreditTime, 216000000);
        //Units
        ObservableList<String> units = FXCollections.observableArrayList();
        units.add("None");
        units.addAll(FireEditor.unitDb.getNames());
        comboUnit1.setItems(units);
        comboUnit2.setItems(units);
        //Classes
        ObservableList<String> classes = FXCollections.observableArrayList();
        classes.add("None");
        classes.addAll(FireEditor.classDb.getNames());
        comboClass1.setItems(classes);
        comboClass2.setItems(classes);
        //Chapter Names
        ObservableList<String> chapters = FXCollections.observableArrayList();
        //The names are stored
        for (int i = 0; i <= Constants.MAX_CHAPTERS; i++) {
            chapters.add(chapterName(i));
        }
        comboCreditChapter.setItems(chapters);
    }

    public void addListeners() {
        comboCreditSlot.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    //The old selection is updated
                    //updateCredits((Integer) oldValue);
                    int slot = validateSlot();
                    if (slot == -1) return;
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
        );
        comboUnit1.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int slot = validateSlot();
                    if (slot == -1) return;
                    int unit1 = comboUnit1.getSelectionModel().getSelectedIndex() - 1;
                    if (unit1 == -1) unit1 = 0xFFFF;
                    userBlock.progress.get(slot).setUnitFirst(unit1);
                }
        );
        comboUnit2.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int slot = validateSlot();
                    if (slot == -1) return;
                    int unit2 = comboUnit2.getSelectionModel().getSelectedIndex() - 1;
                    if (unit2 == -1) unit2 = 0xFFFF;
                    userBlock.progress.get(slot).setUnitSecond(unit2);
                }
        );
        comboClass1.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int slot = validateSlot();
                    if (slot == -1) return;
                    int class1 = comboClass1.getSelectionModel().getSelectedIndex() - 1;
                    if (class1 == -1) class1 = 0xFFFF;
                    userBlock.progress.get(slot).setClassFirst(class1);
                }
        );
        comboClass2.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int slot = validateSlot();
                    if (slot == -1) return;
                    int class2 = comboClass2.getSelectionModel().getSelectedIndex() - 1;
                    if (class2 == -1) class2 = 0xFFFF;
                    userBlock.progress.get(slot).setClassSecond(class2);
                }
        );
        comboCreditChapter.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int slot = validateSlot();
                    if (slot == -1) return;
                    userBlock.progress.get(slot).setChapterId(comboCreditChapter.getSelectionModel().getSelectedIndex());
                }
        );
        spinCreditTime.valueProperty().addListener((observable, oldValue, newValue) -> {
            int slot = validateSlot();
            if (slot == -1) return;
            userBlock.progress.get(slot).setTime(spinCreditTime.getValue() * 60);
        });
        spinCreditTurns.valueProperty().addListener((observable, oldValue, newValue) -> {
            int slot = validateSlot();
            if (slot == -1) return;
            userBlock.progress.get(slot).setTurns(spinCreditTurns.getValue());
        });
    }

    public int validateSlot() {
        int slot = comboCreditSlot.getSelectionModel().getSelectedIndex();
        int size = comboCreditSlot.getItems().size();
        if (size == 0) return -1;
        else if (slot == -1) return 0;
        return slot;
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
        btnRemoveRecord.setDisable(disable);
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
        } else return "Mod Chapter #" + (Constants.MAX_CHAPTERS - id + 1);
    }
}
