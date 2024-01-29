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

    opens graphics.sorter to javafx.fxml, com.fasterxml.jackson.databind;
    exports graphics.sorter;
    exports graphics.sorter.Structs;
    opens graphics.sorter.Structs to com.fasterxml.jackson.databind, javafx.fxml;
    exports graphics.sorter.controllers;
    opens graphics.sorter.controllers to com.fasterxml.jackson.databind, javafx.fxml;
}