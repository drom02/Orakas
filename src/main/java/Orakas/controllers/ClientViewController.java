package Orakas.controllers;

import Orakas.Humans.ClientProfile;
import Orakas.Database.Database;
import Orakas.GraphicalFunctions;
import Orakas.Location;
import Orakas.Mediator.InternalController;
import Orakas.Settings;
import Orakas.Structs.ClientProfileCellFactory;
import Orakas.Structs.ListOfClientsProfiles;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
/*
JavaFX controller for client view
 */
public class ClientViewController extends SaveableControllerInterface implements ControllerInterface{
    //region graphical components
    @FXML
    private  Pane mainPane;
    @FXML
    private  GridPane mainGrid;
    @FXML
    private ChoiceBox<Location> homeLocationBox;
    @FXML
    private TextField nameField;
    @FXML
    private CheckBox statusChoiceBox;
    @FXML
    private ListView<ClientProfile> listViewofC;
    @FXML
    private TextArea comments;
    @FXML
    private TextField surnameField;
    //endregion
    //region variables
    private Settings settings;
    private ListOfClientsProfiles listOfc;
    private ClientProfile selectedClient;
    private InternalController internalController = new InternalController(this);
    private ArrayList<Node> requiredNodes = new ArrayList<>(Arrays.asList(nameField,surnameField,homeLocationBox));
    //endregion
    public void saveClient(MouseEvent mouseEvent) throws IOException {
        save();
    }
    public void loadClient(MouseEvent mouseEvent) throws IOException {
        selectedClient = (ClientProfile) listViewofC.getSelectionModel().getSelectedItem();
        if(!(selectedClient==null)){
            nameField.setText(selectedClient.getName());
            surnameField.setText(selectedClient.getSurname());
            comments.setText(selectedClient.getComment());
            statusChoiceBox.setSelected(selectedClient.getActivityStatus());
            homeLocationBox.getItems().setAll(Database.loadLocations().getListOfLocations());
            homeLocationBox.setValue(selectedClient.getHomeLocation());
        }
    }
    public void initialize() throws IOException {
        listViewofC.setCellFactory(new ClientProfileCellFactory());
        settings = Settings.getSettings();
        listOfc = Database.loadClientProfiles();
        homeLocationBox.getItems().setAll(Database.loadLocations().getListOfLocations());
        ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getFullClientList());
        listViewofC.setItems(observClientList);
        statusChoiceBox.setSelected(true);
        Platform.runLater(() -> {
            GraphicalFunctions.screenResizing(mainPane,mainGrid);
        });
        if(listViewofC.getItems().isEmpty()){
            selectedClient = null;
        }else{
            listViewofC.getSelectionModel().select(0);
            loadClient(null);
        }
    }
    public void deleteClient(MouseEvent mouseEvent) throws IOException {
        listOfc.getFullClientList().remove(selectedClient);
        Database.softDeleteClient(selectedClient);
        if(listOfc.getFullClientList().isEmpty()){
            selectedClient = null;
        }else{
            selectedClient = listOfc.getFullClientList().getFirst();
        }
        nameField.clear();
        homeLocationBox.getItems().clear();
        surnameField.clear();
        comments.clear();
        ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getFullClientList());
        listViewofC.setItems(observClientList);
        internalController.send("Assistant");
    }
    public void saveNewClient(ActionEvent actionEvent) {
        saveNew();
    }

    @Override
    public void updateScreen() {
        homeLocationBox.getItems().setAll(Database.loadLocations().getListOfLocations());
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
    @Override
    void save() {
        if(verifyRequired()){
            if(selectedClient != null){
                selectedClient.setName(nameField.getText());
                selectedClient.setSurname(surnameField.getText());
                selectedClient.setComment(comments.getText());
                selectedClient.setActivityStatus(statusChoiceBox.isSelected());
                selectedClient.setHomeLocation((Location) homeLocationBox.getValue());
                Database.saveClientProfile(selectedClient);
                ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getFullClientList());
                listViewofC.setItems(observClientList);
                internalController.send("Assistant");
            }
        }
    }
    @Override
    void saveNew() {
        if(verifyRequired()){
            ClientProfile clip = new ClientProfile(UUID.randomUUID(),statusChoiceBox.isSelected(), nameField.getText(),surnameField.getText(), homeLocationBox.getValue() ,comments.getText());
            listOfc.getFullClientList().add(clip);
            Database.saveClientProfile(clip);
            ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getFullClientList());
            listViewofC.setItems(observClientList);
            internalController.send("Assistant");
        }

    }
}
