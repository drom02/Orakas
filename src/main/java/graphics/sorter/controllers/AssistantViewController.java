package graphics.sorter.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphics.sorter.Assistant;
import graphics.sorter.HelloApplication;
import graphics.sorter.JsonManip;
import graphics.sorter.ListOfAssistants;
import graphics.sorter.Structs.HumanCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AssistantViewController {
    @FXML
    private ListView listViewofA;
    @FXML
    private CheckBox overtimeCheck;
    @FXML
    private CheckBox dayCheck;
    @FXML
    private CheckBox nightCheck;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField contractField;
    @FXML
    private TextField workField;
    @FXML
    private TextArea comments;
    @FXML
    private ArrayList listOfAssist;

    public void deleteAssistant(MouseEvent mouseEvent) {
    }

    public void saveAssistant(MouseEvent mouseEvent) {
    }
    public void initialize() throws IOException {
        listViewofA.setCellFactory(new HumanCellFactory());
        JsonManip jsoMap= new JsonManip();
        ListOfAssistants listOfA = jsoMap.loadAssistantInfo();
        listOfAssist = listOfA .getAssistantList();
        ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.assistantList);
        listViewofA.setItems(observAssistantList);

    }
    private ListOfAssistants loadAssistantsJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[]  jsonData = Files.readAllBytes(Paths.get("E:\\JsonWriteTest\\test.json"));
        ListOfAssistants listOfA = objectMapper.readValue(jsonData, ListOfAssistants.class );
        listOfAssist = listOfA.assistantList;
        return listOfA;
    }

    public void switchPage(ActionEvent actionEvent) throws IOException {
        Scene scen = listViewofA.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Main-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);
    }

    public void loadAssistant(MouseEvent mouseEvent) {
       Assistant selectedAssistant = (Assistant) listViewofA.getSelectionModel().getSelectedItem();
       nameField.setText(selectedAssistant.getName());
       surnameField.setText(selectedAssistant.getSurname());
       contractField.setText(selectedAssistant.getContractType());
       overtimeCheck.setSelected(selectedAssistant.getLikesOvertime());
       dayCheck.setSelected(selectedAssistant.getWorksOnlyDay());
       nightCheck.setSelected(selectedAssistant.getWorksOnlyNight());
       workField.setText(String.valueOf(selectedAssistant.getWorkTime()));
       comments.setText(selectedAssistant.getComments());

    }
}
