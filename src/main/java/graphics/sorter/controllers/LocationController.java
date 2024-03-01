package graphics.sorter.controllers;

import graphics.sorter.*;
import graphics.sorter.Structs.ListOfLocations;
import graphics.sorter.Structs.LocationCellFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class LocationController {
    @FXML
    private GridPane mainGrid;
    @FXML
    private Pane basePane;
    @FXML
    private TextField addressField;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea comments;
    @FXML
    private ListView listViewofL;
    @FXML
    private ArrayList<Location> listOfLoc;
    private ListOfLocations listOfL;
    private UUID selectedID;
    private Location selectedLocationGlobal;
    JsonManip jsoMap;
    Settings set;
    public void initialize() throws IOException {
        jsoMap= JsonManip.getJsonManip();
        set = jsoMap.loadSettings();
        listOfL = jsoMap.loadLocations(set);
        listOfLoc = listOfL .getListOfLocations();
        ObservableList<Location> observLocationList = FXCollections.observableList(listOfL.getListOfLocations());
        listViewofL.setItems(observLocationList);
        listViewofL.setCellFactory(new LocationCellFactory());
        Platform.runLater(() -> {
            GraphicalFunctions.screenResizing(basePane,mainGrid);
        });

    }
    public void switchPage(ActionEvent actionEvent) throws IOException {
        Scene scen = listViewofL.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("Main-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);
    }

    public void deleteLocation(MouseEvent mouseEvent) throws IOException {
        System.out.println();
        if(!(selectedID==null)){
            listOfLoc.remove(selectedLocationGlobal);
            selectedLocationGlobal = null;
            jsoMap.saveLocations(listOfL);
            addressField.clear();
            nameField.clear();
            comments.clear();
            ObservableList<Location> observLocationList = FXCollections.observableList(listOfL.getListOfLocations());
            listViewofL.setItems(observLocationList);
        }else{
            System.out.println("Prosím vyberte lokaci k odstranění");
        }

    }

    public void saveLocation(MouseEvent mouseEvent) {
    }

    public void loadLocation(MouseEvent mouseEvent) {
        Location selectedLocation = (Location) listViewofL.getSelectionModel().getSelectedItem();
        addressField.setText(selectedLocation.getAddress());
        nameField.setText(selectedLocation.getCasualName());
        comments.setText(selectedLocation.getComments());
        selectedID=selectedLocation.getID();
        selectedLocationGlobal = selectedLocation;

    }

    public void saveNewLocation(ActionEvent actionEvent) throws IOException {
        if(!(addressField.getText().isEmpty()) & !(nameField.getText().isEmpty())){
            if(listOfL.getListOfLocations().isEmpty()){
                listOfLoc.add(new Location(UUID.randomUUID(), addressField.getText(),nameField.getText()));
                jsoMap.saveLocations(listOfL);
                ObservableList<Location> observLocationList = FXCollections.observableList(listOfL.getListOfLocations());
                listViewofL.setItems(observLocationList);
                return;
            }
            ArrayList<String> name = new ArrayList<>();
            for(Location loc: listOfL.getListOfLocations()){
                name.add(loc.getCasualName());
            }
                if(!(name.contains(nameField.getText()))){
                    listOfLoc.add(new Location(UUID.randomUUID(), addressField.getText(),nameField.getText()));
                    jsoMap.saveLocations(listOfL);
                    ObservableList<Location> observLocationList = FXCollections.observableList(listOfL.getListOfLocations());
                    listViewofL.setItems(observLocationList);
                }else{
                    System.out.println("Lokace už existuje");
                }
        }else{
            System.out.println("Vyplntě povinné údaje");
        }
    }
}
