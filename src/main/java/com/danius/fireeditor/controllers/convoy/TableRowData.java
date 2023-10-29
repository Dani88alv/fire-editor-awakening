package com.danius.fireeditor.controllers.convoy;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableRowData {
    private final StringProperty value;

    public TableRowData(String value) {
        this.value = new SimpleStringProperty(value);
    }

    public StringProperty valueProperty() {
        return value;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }
}
