package graphics.sorter.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphics.sorter.*;
import graphics.sorter.Structs.HumanCellFactory;
import graphics.sorter.Structs.ShiftTextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.converter.ColorConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class AssistantViewController {

    @FXML
    private ColumnConstraints parentGrid;
    @FXML
    private GridPane daysInWeekGrid;
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
    private ChoiceBox contractField;
    @FXML
    private TextField workField;
    @FXML
    private Text workText;
    @FXML
    private TextArea comments;
    @FXML
    private ArrayList listOfAssist;
    private Assistant selectedAssistant;
    private int[] stateOfDays;
    private JsonManip jsoMap;
    private ListOfAssistants listOfA;
    private ArrayList<Control> assistantNodes;
    Settings set;
    public void deleteAssistant(MouseEvent mouseEvent) throws IOException {
        if(!(selectedAssistant == null)){
            listOfA.getAssistantList().remove(selectedAssistant);
            ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.assistantList);
            listViewofA.setItems(observAssistantList);
            for(Control n : assistantNodes){
                if (n instanceof TextArea) {
                    ((TextArea) n).clear();
                } else if (n instanceof TextField) {
                    ((TextField) n).clear();
                } else if (n instanceof CheckBox) {
                    ((CheckBox) n).setSelected(false);
                }
            }
            jsoMap.saveAssistantInfo(listOfA);
        }else{
            System.out.println("Vyberte asistenta k odstranění.");
        }

    }
   /*
   @TODO Add how to deal with somebody changing name and surname, check that it is still unique.
    */
public void saveAssistant(MouseEvent mouseEvent) throws IOException {
        selectedAssistant.setName(nameField.getText());
        selectedAssistant.setSurname(surnameField.getText());
        selectedAssistant.setContractType((String) contractField.getValue());
        selectedAssistant.setLikesOvertime(overtimeCheck.isSelected());
        selectedAssistant.setWorksOnlyDay(dayCheck.isSelected());
        selectedAssistant.setWorksOnlyNight(nightCheck.isSelected());
        selectedAssistant.setWorkTime(Double.parseDouble(workField.getText()));
        selectedAssistant.setComments(comments.getText());
        selectedAssistant.setWorkDays(stateOfDays);
        jsoMap.saveAssistantInfo(listOfA);
        ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.assistantList);
        listViewofA.setItems(observAssistantList);

    }
    public void initialize() throws IOException {

        assistantNodes = new ArrayList<>(Arrays.asList(nameField, surnameField,overtimeCheck,dayCheck,nightCheck,workField,comments));
        contractField.getItems().addAll("HPP","DPP","DPČ");
        contractField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
                    loadContract();
                });
        listViewofA.setCellFactory(new HumanCellFactory());
        jsoMap= new JsonManip();
        set = jsoMap.loadSettings("E:\\JsonWriteTest\\");
         listOfA = jsoMap.loadAssistantInfo();
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
        selectedAssistant = (Assistant) listViewofA.getSelectionModel().getSelectedItem();
    if(!(selectedAssistant== null)){
        nameField.setText(selectedAssistant.getName());
        surnameField.setText(selectedAssistant.getSurname());
        contractField.setValue(selectedAssistant.getContractType());
        overtimeCheck.setSelected(selectedAssistant.getLikesOvertime());
        dayCheck.setSelected(selectedAssistant.getWorksOnlyDay());
        nightCheck.setSelected(selectedAssistant.getWorksOnlyNight());
        loadContract();
        workField.setText(String.valueOf(selectedAssistant.getWorkTime()));
        comments.setText(selectedAssistant.getComments());
        stateOfDays = selectedAssistant.getWorkDays();
        populateDaysInWeekTable();
    }

    }

public void loadContract(){
    if(contractField.getValue().equals("HPP") ){
        workText.setText("Úvazek");
    }else{
        workText.setText("Počet smluvních hodin v měsíci");
    }
}
    public void saveNewAssistant(ActionEvent actionEvent) throws IOException {
        if(!(nameField.getText().isEmpty()) & !(surnameField.getText().isEmpty())){
            if(listOfA.getAssistantList().isEmpty()){
                listOfAssist.add(new Assistant(UUID.randomUUID(),nameField.getText(), surnameField.getText(), (String) contractField.getValue(),Double.parseDouble(workField.getText()),overtimeCheck.isSelected(), dayCheck.isSelected(), nightCheck.isSelected(), comments.getText(),stateOfDays));
                jsoMap.saveAssistantInfo(listOfA);
                ObservableList<Assistant> observLocationList = FXCollections.observableList(listOfA.getAssistantList());
                listViewofA.setItems(observLocationList);
                System.out.println("No other assistants exist");
                return;

            }
            ArrayList<String> nameAndSurname = new ArrayList<>();
            for(Assistant loc: listOfA.getAssistantList()){
                nameAndSurname.add(loc.getName() +" "+ loc.getSurname());
            }
                if(!( nameAndSurname.contains(nameField.getText() +" "+ surnameField.getText()))){
                    listOfAssist.add(new Assistant(UUID.randomUUID(),nameField.getText(), surnameField.getText(), (String) contractField.getValue(),Double.parseDouble(workField.getText()),overtimeCheck.isSelected(), dayCheck.isSelected(), nightCheck.isSelected(), comments.getText(),stateOfDays));
                    jsoMap.saveAssistantInfo(listOfA);
                    ObservableList<Assistant> observLocationList = FXCollections.observableList(listOfA.getAssistantList());
                    listViewofA.setItems(observLocationList);
                    System.out.println("FFS");

                }else{
                    System.out.println("Asistent už existuje");

                }


        }else{
            System.out.println("Vyplntě povinné údaje");
        }
    }
    public void populateDaysInWeekTable(){
        String[] days = {"Pondělí","Úterý","Středa","Čtvrtek","Pátek","Sobota","Neděle"};
        ArrayList<TextArea> allAreas = new ArrayList<>();
        for(int iRow=0; iRow<7;iRow++){
            for(int iCol=0; iCol<2;iCol++){
                TextArea dayArea = new TextArea();
                dayArea.setEditable(false);
                allAreas.add(dayArea);
                daysInWeekGrid.setConstraints(dayArea,iCol,iRow,1,1);
                String displayText;
                dayArea.setOnMouseClicked(this :: switchState);

                if(iCol==0){
                    displayText = days[iRow];
                }else{
                    displayText = "";
                }
                dayArea.setText(displayText);
                setDayState(dayArea,stateOfDays[iRow]);
            }
        }
        daysInWeekGrid.getChildren().addAll(allAreas);
        setGridSize();

    }
    public void setGridSize(){
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        daysInWeekGrid.getColumnConstraints().setAll(column1, column2);
        ArrayList<RowConstraints> rowConList = new ArrayList<>();
        for(int i=0;i<7;i++){
            RowConstraints rowC = new RowConstraints();
            rowConList.add(rowC);
            rowC.setPercentHeight(100/7);
        }
        daysInWeekGrid.getRowConstraints().setAll(rowConList);
    }
    private void setDayState(TextArea inputText, int day){

        if(day == 0){
            inputText.setStyle("-fx-control-inner-background:" +toHexString(Color.RED));
        }else{
            inputText.setStyle("-fx-control-inner-background:" +toHexString(Color.GREEN));
        }

    }
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }


    public void switchState(MouseEvent mouseEvent) {
        TextArea loadedArea = (TextArea) mouseEvent.getSource();
        int rowIndex = daysInWeekGrid.getRowIndex(loadedArea);
        int colIndex = daysInWeekGrid.getColumnIndex(loadedArea);
        if(colIndex == 0){
            if(selectedAssistant.getWorkDays()[rowIndex] ==0){
                selectedAssistant.getWorkDays()[rowIndex] = 1;
                setDayState(loadedArea,1);
            }else{
                selectedAssistant.getWorkDays()[rowIndex] = 0;
                setDayState(loadedArea,0);
            }
        }else{
            if(selectedAssistant.getWorkDays()[rowIndex+7] ==0){
                selectedAssistant.getWorkDays()[rowIndex+7] = 1;
                setDayState(loadedArea,1);
            }else{
                selectedAssistant.getWorkDays()[rowIndex+7] = 0;
                setDayState(loadedArea,0);
            }
        }

    System.out.println(rowIndex);

    }
}
