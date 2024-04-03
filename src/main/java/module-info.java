module Orakas {
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

    opens Orakas to javafx.fxml, com.fasterxml.jackson.databind;
    exports Orakas;
    exports Orakas.Structs;
    opens Orakas.Structs to com.fasterxml.jackson.databind, javafx.fxml;
    exports Orakas.controllers;
    exports Orakas.AssistantAvailability;
    exports Orakas.Filters to com.fasterxml.jackson.databind;
    opens Orakas.AssistantAvailability to com.fasterxml.jackson.databind;
    opens Orakas.controllers to com.fasterxml.jackson.databind, javafx.fxml;
    opens Orakas.Vacations to com.fasterxml.jackson.databind;
    exports Orakas.JavaFXCustomComponents;
    opens Orakas.JavaFXCustomComponents to com.fasterxml.jackson.databind, javafx.fxml;
    exports Orakas.Structs.Availability;
    opens Orakas.Structs.Availability to com.fasterxml.jackson.databind, javafx.fxml;
    exports Orakas.Structs.Utility;
    opens Orakas.Structs.Utility to com.fasterxml.jackson.databind, javafx.fxml;
    exports Orakas.Database;
    opens Orakas.Database to com.fasterxml.jackson.databind, javafx.fxml;
    exports Orakas.Structs.TimeStructs;
    opens Orakas.Structs.TimeStructs to com.fasterxml.jackson.databind, javafx.fxml;
    exports Orakas.Humans;
    opens Orakas.Humans to com.fasterxml.jackson.databind, javafx.fxml;
    exports Orakas.Excel;
    opens Orakas.Excel to com.fasterxml.jackson.databind, javafx.fxml;
}