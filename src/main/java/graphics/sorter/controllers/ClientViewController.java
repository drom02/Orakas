package graphics.sorter.controllers;

import graphics.sorter.*;
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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.UUID;

public class ClientViewController {
    //region graphical components
    @FXML
    private  Pane mainPane;
    @FXML
    private  GridPane mainGrid;
    @FXML
    private ChoiceBox homeLocationBox;
    @FXML
    private TextField nameField;
    @FXML
    private CheckBox statusChoiceBox;
    @FXML
    private ListView listViewofC;
    @FXML
    private TextArea comments;
    @FXML
    private TextField surnameField;
    //endregion
    //region variables
    private Settings settings;
    private ListOfClientsProfiles listOfc;
    private ClientProfile selectedClient;
    private JsonManip jsoMap;
    //endregion
    public void saveClient(MouseEvent mouseEvent) throws IOException {
        if(selectedClient != null){
            selectedClient.setName(nameField.getText());
            selectedClient.setSurname(surnameField.getText());
            selectedClient.setComment(comments.getText());
            selectedClient.setActivityStatus(statusChoiceBox.isSelected());
            selectedClient.setHomeLocation((Location) homeLocationBox.getValue());
            jsoMap.saveClientInfo(listOfc);
            ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getFullClientList());
            listViewofC.setItems(observClientList);
        }
    }
    public void loadClient(MouseEvent mouseEvent) throws IOException {
        selectedClient = (ClientProfile) listViewofC.getSelectionModel().getSelectedItem();
        if(!(selectedClient==null)){
            nameField.setText(selectedClient.getName());
            surnameField.setText(selectedClient.getSurname());
            comments.setText(selectedClient.getComment());
            statusChoiceBox.setSelected(selectedClient.getActivityStatus());
            homeLocationBox.getItems().setAll(jsoMap.loadLocations(settings).getListOfLocations());
            homeLocationBox.setValue(selectedClient.getHomeLocation());
        }


    }
    public void newClient() throws IOException {
        ClientProfile clip = new ClientProfile(UUID.randomUUID(),selectedClient.getActivityStatus(),selectedClient.getName(),selectedClient.getSurname(), selectedClient.getHomeLocation(),selectedClient.getComment());
        listOfc.getFullClientList().add(clip);
        jsoMap.saveClientInfo(listOfc);
    }
    public void initialize() throws IOException {
        listViewofC.setCellFactory(new HumanCellFactory());
        jsoMap= JsonManip.getJsonManip();
        settings = jsoMap.loadSettings();
        listOfc = jsoMap.loadClientProfileInfo();
        ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getFullClientList());
        listViewofC.setItems(observClientList);
        Platform.runLater(() -> {
            GraphicalFunctions.screenResizing(mainPane,mainGrid);

        });
    }


    public void switchPage(ActionEvent actionEvent) throws IOException {
        Scene scen = listViewofC.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("Main-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);
    }

    public void deleteClient(MouseEvent mouseEvent) throws IOException {
        listOfc.getFullClientList().remove(selectedClient);
        jsoMap.saveClientInfo(listOfc);
        nameField.clear();
        homeLocationBox.getItems().clear();
        surnameField.clear();
        comments.clear();
        ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getFullClientList());
        listViewofC.setItems(observClientList);

    }

    public void saveNewClient(ActionEvent actionEvent) throws IOException {
        ClientProfile clip = new ClientProfile(UUID.randomUUID(),statusChoiceBox.isSelected(), nameField.getText(),surnameField.getText(), null ,comments.getText());
        listOfc.getFullClientList().add(clip);
        try {
            jsoMap.saveClientInfo(listOfc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getFullClientList());
       // ListOfClientsProfiles asdasd = jsoMap.loadClientProfileInfo();
        listViewofC.setItems(observClientList);
    }
}
