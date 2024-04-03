package Orakas.controllers;

import Orakas.*;
import Orakas.Database.Database;
import Orakas.Structs.LocationCellFactory;
import Orakas.Structs.ListOfLocations;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class LocationController extends SaveableControllerInterface implements ControllerInterface{
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
    private ArrayList<Node> requiredNodes = new ArrayList<>(Arrays.asList(nameField,addressField));
    JsonManip jsoMap;
    Settings set;
    public void initialize() throws IOException {
        set = Settings.getSettings();
        listOfL = Database.loadLocations();
        listOfLoc = listOfL.getListOfLocations();
        ObservableList<Location> observLocationList = FXCollections.observableList(listOfL.getListOfLocations());
        listViewofL.setItems(observLocationList);
        listViewofL.setCellFactory(new LocationCellFactory());
        Platform.runLater(() -> {
            GraphicalFunctions.screenResizing(basePane,mainGrid);
        });
        if(!listViewofL.getItems().isEmpty()) {
            listViewofL.getSelectionModel().select(0);
            loadLocation(null);
        }
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
    @Override
    public void save() {
        if(verifyRequired()){
            selectedLocationGlobal.setAddress(addressField.getText());
            selectedLocationGlobal.setCasualName(nameField.getText());
            selectedLocationGlobal.setComments(comments.getText());
            Database.saveLocation(selectedLocationGlobal);
        }else{

        }

    }
    public void saveLocation(MouseEvent mouseEvent) {
        save();
    }

    public void saveNewLocation(MouseEvent mouseEvent) {
        saveNew();
    }

    public void loadLocation(MouseEvent mouseEvent) {
        Location selectedLocation = (Location) listViewofL.getSelectionModel().getSelectedItem();
        addressField.setText(selectedLocation.getAddress());
        nameField.setText(selectedLocation.getCasualName());
        comments.setText(selectedLocation.getComments());
        selectedID=selectedLocation.getID();
        selectedLocationGlobal = selectedLocation;
    }
    @Override
    public void saveNew() {
        if(verifyRequired()){
            if(listOfL.getListOfLocations().isEmpty()){
                selectedLocationGlobal = new Location(UUID.randomUUID(), addressField.getText(),nameField.getText());
                selectedLocationGlobal.setComments(comments.getText());
                listOfLoc.add(selectedLocationGlobal);
                Database.saveLocation(selectedLocationGlobal);
                ObservableList<Location> observLocationList = FXCollections.observableList(listOfL.getListOfLocations());
                listViewofL.setItems(observLocationList);
                return;
            }
            ArrayList<String> name = new ArrayList<>();
            for(Location loc: listOfL.getListOfLocations()){
                name.add(loc.getCasualName());
            }
            if(!(name.contains(nameField.getText()))){
                selectedLocationGlobal = new Location(UUID.randomUUID(), addressField.getText(),nameField.getText());
                selectedLocationGlobal.setComments(comments.getText());
                listOfLoc.add(selectedLocationGlobal);
                Database.saveLocation(selectedLocationGlobal);
                ObservableList<Location> observLocationList = FXCollections.observableList(listOfL.getListOfLocations());
                listViewofL.setItems(observLocationList);
            }else{
                System.out.println("Lokace už existuje");
            }

        }else{

        }


    }

    @Override
    public void updateScreen() {

    }

    @Override
    public void loadAndUpdateScreen() {

    }
    @Override
    public void addToRequiredFields(Node item) {
        requiredNodes.add(item);
    }

    @Override
    public Object getRequiredFields(int index) {
        return requiredNodes.get(index);
    }

    @Override
    public ArrayList<Node> getRequiredFields() {
        return requiredNodes;
    }
    @Override
    boolean verifyRequired() {
        for(Node n : getRequiredFields()){
            if(n instanceof TextField){
                if(((TextField) n).getText().isEmpty()){
                    return false;
                }
            } else if (n instanceof ChoiceBox<?>) {
                if(((ChoiceBox<?>) n).getValue() ==null){
                    return false;
                }

            }
        }
        return true;
    }


}
