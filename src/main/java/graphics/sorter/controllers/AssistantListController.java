package graphics.sorter.controllers;

import graphics.sorter.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;

public class AssistantListController {
    @FXML
    private MenuBar menu;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    TextArea textArea1 = new TextArea("Enter your name");
    TextArea textArea2 = new TextArea("Enter your nameeeeeeeeeeeeeeeeee");

    public void test(){


    }
    public void initialize(){
        test();
    }


    public void switchPage(ActionEvent actionEvent) throws IOException {
       Scene scen = menu.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Main-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);

    }
}