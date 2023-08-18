package com.danius.fireeditor.controllers;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;

public class UI {
    public static void setSpinnerNumeric(Spinner<Integer> spinner, int maxValue) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxValue);
        spinner.setValueFactory(valueFactory);

        // Add a listener to the spinner editor's text property to filter non-numeric input
        spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                spinner.getEditor().setText(oldValue);
            }
            if (newValue.length() > 0) {
                try {
                    int value = Integer.parseInt(newValue);
                    if (value > maxValue) {
                        spinner.getEditor().setText(String.valueOf(maxValue));
                    }
                } catch (NumberFormatException e) {
                    spinner.getEditor().setText(oldValue);
                }
            }
        });

        // Add a focus change listener to the spinner editor
        spinner.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // When the editor loses focus, check if it's empty and set to 0
                if (spinner.getEditor().getText().isEmpty()) {
                    spinner.getValueFactory().setValue(0);
                }
            }
        });

        // Add a listener to the spinner value to check for an empty editor and set to 0
        spinner.getValueFactory().valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (spinner.getEditor().getText().isEmpty()) {
                    spinner.getValueFactory().setValue(0);
                }
            } catch (NullPointerException e) {
                // Do nothing, as the NPE has been caught
            }
        });

        // Add a key event listener to the spinner editor to handle backspace
        spinner.getEditor().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE && spinner.getEditor().getText().length() == 1) {
                spinner.getValueFactory().setValue(0);
            }
        });
    }

    public static void setNumericTextField(TextField textField, int maxValue) {
        // Add a listener to the text field to filter non-numeric input
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(oldValue);
            }
            if (newValue.length() > 0) {
                try {
                    int value = Integer.parseInt(newValue);
                    if (value > maxValue) {
                        textField.setText(String.valueOf(maxValue));
                    }
                } catch (NumberFormatException e) {
                    textField.setText(oldValue);
                }
            }
        });

        // Add a focus change listener to the text field
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // When the field loses focus, check if it's empty and set to 0
                if (textField.getText().isEmpty()) {
                    textField.setText("0");
                }
            }
        });
    }


    public static void setHexTextField(TextField textField, int maxLength) {
        // Add a listener to the text field to filter non-numeric input
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9A-Fa-f]*")) {
                textField.setText(oldValue);
            }
            if (newValue.length() > maxLength) {
                textField.setText(newValue.substring(0, maxLength));
            }
        });

        // Add a focus change listener to the text field
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // When the field loses focus, check if it's empty and set to 0
                if (textField.getText().isEmpty()) {
                    textField.setText("0");
                }
            }
        });
    }

    public static void setTextField(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setText(newValue.substring(0, maxLength));
            }
        });

        // Add a focus change listener to the text field
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // When the field loses focus, check if it's empty and set to 0
                if (textField.getText().isEmpty()) {
                    textField.setText("");
                }
            }
        });
    }

    public static void setSpinnerTimer(Spinner<Integer> spinner, int maxSeconds) {
        int step = 1;
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxSeconds, 0, step);
        valueFactory.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer value) {
                int hours = value / 3600;
                int minutes = (value - (hours * 3600)) / 60;
                int seconds = value % 60;
                return String.format("%03d:%02d:%02d", hours, minutes, seconds);
            }

            @Override
            public Integer fromString(String string) {
                String[] parts = string.split(":");
                if (parts.length != 3) {
                    return null;
                }
                try {
                    int hours = Integer.parseInt(parts[0]) * 3600;
                    int minutes = Integer.parseInt(parts[1]) * 60;
                    int seconds = Integer.parseInt(parts[2]);
                    return hours + minutes + seconds;
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        });

        spinner.setValueFactory(valueFactory);
        spinner.setEditable(true);
        TextField editor = spinner.getEditor();
        editor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d{0,3}:\\d{0,2}:\\d{0,2}")) {
                editor.setText(newValue);
            } else {
                editor.setText(oldValue);
            }
        });
        spinner.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                String[] parts = editor.getText().split(":");
                int hours = 0;
                int minutes = 0;
                int seconds = 0;
                if (parts.length >= 1 && !parts[0].isEmpty()) {
                    hours = Math.min(Integer.parseInt(parts[0]), 999);
                }
                if (parts.length >= 2 && !parts[1].isEmpty()) {
                    minutes = Math.min(Integer.parseInt(parts[1]), 59);
                }
                if (parts.length >= 3 && !parts[2].isEmpty()) {
                    seconds = Math.min(Integer.parseInt(parts[2]), 59);
                }
                editor.setText(String.format("%03d:%02d:%02d", hours, minutes, seconds));
            }
        });
    }








}
