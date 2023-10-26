module com.example.fireeditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires org.jdom2;


    opens com.danius.fireeditor to javafx.fxml;
    exports com.danius.fireeditor;
    exports com.danius.fireeditor.util;
    opens com.danius.fireeditor.util to javafx.fxml;
    exports com.danius.fireeditor.savefile;
    opens com.danius.fireeditor.savefile to javafx.fxml;
    exports com.danius.fireeditor.controllers;
    opens com.danius.fireeditor.controllers to javafx.fxml;
    exports com.danius.fireeditor.savefile.units;
    opens com.danius.fireeditor.savefile.units to javafx.fxml;
    exports com.danius.fireeditor.savefile.inventory;
    opens com.danius.fireeditor.savefile.inventory to javafx.fxml;
    exports com.danius.fireeditor.savefile.units.mainblock;
    opens com.danius.fireeditor.savefile.units.mainblock to javafx.fxml;
    exports com.danius.fireeditor.savefile.units.extrablock;
    opens com.danius.fireeditor.savefile.units.extrablock to javafx.fxml;
    exports com.danius.fireeditor.savefile.user;
    opens com.danius.fireeditor.savefile.user to javafx.fxml;
    exports com.danius.fireeditor.savefile.global;
    opens com.danius.fireeditor.savefile.global to javafx.fxml;
    exports com.danius.fireeditor.model;
    exports com.danius.fireeditor.savefile.wireless;
    opens com.danius.fireeditor.savefile.wireless to javafx.fxml;
    exports com.danius.fireeditor.savefile.barrack;
    opens com.danius.fireeditor.savefile.barrack to javafx.fxml;
    exports com.danius.fireeditor.savefile.map;
    opens com.danius.fireeditor.savefile.map to javafx.fxml;
    opens com.danius.fireeditor.model to javafx.fxml;

}