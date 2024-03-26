module graphics.sorter {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.opencsv;
    requires org.apache.poi.ooxml;
    requires java.sql;
    requires de.focus_shift.jollyday.core;

    opens graphics.sorter to javafx.fxml, com.fasterxml.jackson.databind;
    exports graphics.sorter;
    exports graphics.sorter.Structs;
    opens graphics.sorter.Structs to com.fasterxml.jackson.databind, javafx.fxml;
    exports graphics.sorter.controllers;
    exports graphics.sorter.AssistantAvailability;
    opens graphics.sorter.AssistantAvailability to com.fasterxml.jackson.databind;
    opens graphics.sorter.controllers to com.fasterxml.jackson.databind, javafx.fxml;
    opens graphics.sorter.Vacations to com.fasterxml.jackson.databind;
    exports graphics.sorter.JavaFXCustomComponents;
    opens graphics.sorter.JavaFXCustomComponents to com.fasterxml.jackson.databind, javafx.fxml;
}