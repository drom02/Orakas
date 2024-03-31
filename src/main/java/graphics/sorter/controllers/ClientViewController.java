package graphics.sorter.controllers;

import graphics.sorter.*;
import graphics.sorter.Mediator.InternalController;
import graphics.sorter.Structs.ClientProfileCellFactory;
import graphics.sorter.Structs.HumanCellFactory;
import graphics.sorter.Structs.ListOfClientsProfiles;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.UUID;

public class ClientViewController implements ControllerInterface{
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
    //endregion
    public void saveClient(MouseEvent mouseEvent) throws IOException {
        if(selectedClient != null){
            selectedClient.setName(nameField.getText());
            selectedClient.setSurname(surnameField.getText());
            selectedClient.setComment(comments.getText());
            selectedClient.setActivityStatus(statusChoiceBox.isSelected());
            selectedClient.setHomeLocation((Location) homeLocationBox.getValue());
            //jsoMap.saveClientInfo(listOfc);
            Database.saveClientProfile(selectedClient);
            ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getFullClientList());
            listViewofC.setItems(observClientList);
            internalController.send("Assistant");
        }
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
        //listOfc = Database.loadClientProfiles();
        homeLocationBox.getItems().setAll(Database.loadLocations().getListOfLocations());
        ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getFullClientList());
        listViewofC.setItems(observClientList);
        statusChoiceBox.setSelected(true);
        Platform.runLater(() -> {
            GraphicalFunctions.screenResizing(mainPane,mainGrid);
        });
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
        ClientProfile clip = new ClientProfile(UUID.randomUUID(),statusChoiceBox.isSelected(), nameField.getText(),surnameField.getText(), homeLocationBox.getValue() ,comments.getText());
        listOfc.getFullClientList().add(clip);
        Database.saveClientProfile(clip);
        ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getFullClientList());
       // ListOfClientsProfiles asdasd = jsoMap.loadClientProfileInfo();
        listViewofC.setItems(observClientList);
        internalController.send("Assistant");
    }

    @Override
    public void updateScreen() {
        homeLocationBox.getItems().setAll(Database.loadLocations().getListOfLocations());
    }

    @Override
    public void loadAndUpdateScreen() {

    }
}
