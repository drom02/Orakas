package graphics.sorter.controllers;

import graphics.sorter.*;
import graphics.sorter.Structs.ListOfLocations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;

public class LocationController {
    @FXML
    private TextField adressField;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea comments;
    @FXML
    private ListView listViewofL;
    @FXML
    private ArrayList listOfLoc;
    public void initialize() throws IOException {
        JsonManip jsoMap= new JsonManip();
        Settings set = jsoMap.loadSettings("E:\\JsonWriteTest\\");
        ListOfLocations listOfL = jsoMap.loadLocations(set.getFilePath());
        listOfLoc = listOfL .getListOfLocations();
        ObservableList<Location> observLocationList = FXCollections.observableList(listOfL.getListOfLocations());
        listViewofL.setItems(observLocationList);

    }
    public void switchPage(ActionEvent actionEvent) throws IOException {
        Scene scen = listViewofL.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Main-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);
    }

    public void deleteLocation(MouseEvent mouseEvent) {
    }

    public void saveLocation(MouseEvent mouseEvent) {
    }

    public void loadLocation(MouseEvent mouseEvent) {
        Location selectedLocation = (Location) listViewofL.getSelectionModel().getSelectedItem();
        adressField.setText(selectedLocation.getAddress());
        nameField.setText(selectedLocation.getCasualName());
        comments.setText(selectedLocation.getComments());

    }
}
