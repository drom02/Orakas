package graphics.sorter.controllers;

import graphics.sorter.*;
import graphics.sorter.Structs.HumanCellFactory;
import graphics.sorter.Structs.ListOfClients;
import graphics.sorter.Structs.ListOfClientsProfiles;
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

import java.io.IOException;

public class ClientViewController {
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
    private Settings settings;

    public void saveClient(MouseEvent mouseEvent) {
    }
    public void loadClient(MouseEvent mouseEvent) {
        ClientProfile selectedClient = (ClientProfile) listViewofC.getSelectionModel().getSelectedItem();
        nameField.setText(selectedClient.getName());
        surnameField.setText(selectedClient.getSurname());
        comments.setText("Temporarily unavailable");
        homeLocationField.setText(selectedClient.getSurname());

    }
    public void initialize() throws IOException {
        listViewofC.setCellFactory(new HumanCellFactory());
        JsonManip jsoMap= new JsonManip();
        settings = jsoMap.loadSettings("E:\\JsonWriteTest\\");

        ListOfClientsProfiles listOfc = jsoMap.loadClientProfileInfo();
        ObservableList<ClientProfile> observClientList = FXCollections.observableList(listOfc.getClientList());
        listViewofC.setItems(observClientList);
    }


    public void switchPage(ActionEvent actionEvent) throws IOException {
        Scene scen = listViewofC.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Main-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);
    }

    public void deleteClient(MouseEvent mouseEvent) {


    }
}
