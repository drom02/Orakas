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
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ClientViewController {
    //region graphical components
    @FXML
    private  Pane mainPane;
    @FXML
    private  GridPane mainGrid;
    @FXML
    private TextField homeLocationField;
    @FXML
    private TextField nameField;
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
    public void saveClient(MouseEvent mouseEvent) {
    }
    public void loadClient(MouseEvent mouseEvent) {
        selectedClient = (ClientProfile) listViewofC.getSelectionModel().getSelectedItem();
        if(!(selectedClient==null)){
            nameField.setText(selectedClient.getName());
            surnameField.setText(selectedClient.getSurname());
            comments.setText("Temporarily unavailable");
            homeLocationField.setText(selectedClient.getSurname());
        }


    }
    public void initialize() throws IOException {
        listViewofC.setCellFactory(new HumanCellFactory());
        jsoMap= new JsonManip();
        settings = jsoMap.loadSettings();
        listOfc = jsoMap.loadClientProfileInfo();
        ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getClientList());
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
        listOfc.getClientList().remove(selectedClient);
        jsoMap.saveClientInfo(listOfc);
        nameField.clear();
        homeLocationField.clear();
        surnameField.clear();
        comments.clear();
        ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getClientList());
        listViewofC.setItems(observClientList);

    }
}
