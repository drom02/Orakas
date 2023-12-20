package graphics.sorter.controllers;

import graphics.sorter.*;
import graphics.sorter.Structs.AvailableAssistants;
import graphics.sorter.Structs.ShiftTextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;

public class ShiftPickerController {
    @FXML
    private ScrollPane TestScrollPane;
    @FXML
    private Label welcomeText;
    @FXML
    private ListView assistantList;
    private ArrayList listOfAssist;
    private Month editedMonth;
    private Assistant selectedAssistant;
    private ArrayList<ArrayList<String>> dayArrayList = new ArrayList();
    private ArrayList<ArrayList<String>> nightArrayList = new ArrayList();
    private ArrayList<TextArea> areaList = new ArrayList<TextArea>();
    private ArrayList<ArrayList> listOfAssistantLists = new ArrayList<>();
   private  ArrayList<ShiftTextArea> assistantsDayList = new ArrayList<ShiftTextArea>();
   private  ArrayList<ShiftTextArea> assistantsNightList = new ArrayList<ShiftTextArea>();


    public void test(){
        editedMonth = Month.DECEMBER;
        listOfAssistantLists.add(assistantsDayList);
        listOfAssistantLists.add(assistantsNightList);
        ArrayList<TextArea> rowTitles = new ArrayList<TextArea>();
        int i = 0;
        int clienMothIter = 1;
        /*
        Vytvoří nadpisy pro jednotlivé dny
         */
        GridPane grid = new GridPane();
        while (i <= editedMonth.length(false)){
            String inputText;
            ShiftTextArea rowTitleAr = new ShiftTextArea();
            String[] titles = {"Date", "Day shift", "Night shift"};
            if(i==0){
                int row = 0;
                for (String st : titles){
                    rowTitleAr = new ShiftTextArea();
                    inputText = titles[row];
                    rowTitleAr.setEditable(false);
                    rowTitleAr.setText(inputText);
                    rowTitles.add(rowTitleAr);
                    rowTitleAr.setPrefSize(250,100);
                    grid.setConstraints(rowTitleAr,0,row,1,1);
                    row++;
                }
                i++;
            }else{
                int row = 0;
                inputText = i + "."+ "12"+ "."+"2023";
                rowTitleAr.setText(inputText);
                areaList.add(rowTitleAr);
                rowTitleAr.setPrefSize(250,100);
                grid.setConstraints(rowTitleAr,i,row,1,1);
                int shift = 0;
                for (ArrayList arList : listOfAssistantLists) {
                    rowTitleAr = new ShiftTextArea();
                    inputText= "";
                    rowTitleAr.setText(inputText);
                    rowTitleAr.setType(shift);
                    rowTitleAr.setID(i);
                    rowTitleAr.setOnMouseClicked(this :: onTextAreaClicked);
                    arList.add(rowTitleAr);
                    rowTitleAr.setPrefSize(250,300);
                    grid.setConstraints(rowTitleAr,i,row+1,1,1);
                    row++;
                    shift++;
                }
                i++;
            }
        }

        grid.getChildren().addAll(areaList);
        grid.getChildren().addAll(assistantsNightList);
        grid.getChildren().addAll(assistantsDayList);
        grid.getChildren().addAll(rowTitles);
        TestScrollPane.setContent(grid);
        System.out.println("Test");

    }
    public void initialize() throws IOException {
        JsonManip jsoMap= new JsonManip();
        ListOfAssistants listOfA = jsoMap.loadAssistantInfo();
        listOfAssist = listOfA .getAssistantList();
        ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.assistantList);
        assistantList.setItems(observAssistantList);
        test();
        loadAvailableAssistants();
        System.out.println("hi");
    }

    public void switchPageToMain(ActionEvent actionEvent) throws IOException {
       Scene scen = TestScrollPane.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("test-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);

    }
    public void selectAssistant(MouseEvent mouseEvent) {
        selectedAssistant = (Assistant) assistantList.getSelectionModel().getSelectedItem();
    }
    public void onTextAreaClicked(MouseEvent mouseEvent){
       ShiftTextArea loadedArea = (ShiftTextArea) mouseEvent.getSource();
        MouseButton button = mouseEvent.getButton();
        String oldText = loadedArea.getText();
        if(button== MouseButton.PRIMARY){
            if(selectedAssistant != null && !oldText.contains(selectedAssistant.getName() +" "+ selectedAssistant.getSurname())){
                loadedArea.getAvailableAssistants().add(selectedAssistant);
                String output = new String();
                for(Assistant a : loadedArea.getAvailableAssistants()){
                    output = output + "," +a.getName() +" "+ a.getSurname();
                }
               // output = output + "," +selectedAssistant.getName() +" "+ selectedAssistant.getSurname();
                loadedArea.setText(output);
                //loadedArea.setText(oldText + "," +selectedAssistant.getName() +" "+ selectedAssistant.getSurname());
            }
        }else{
            if(selectedAssistant != null && oldText.contains(selectedAssistant.getName() +" "+ selectedAssistant.getSurname())){
               // loadedArea.getAvailableAssisants().remove(selectedAssistant);
                loadedArea.getAvailableAssistants().remove(selectedAssistant);
                String output = new String();
                for(Assistant a : loadedArea.getAvailableAssistants()){
                    output = output + "," +a.getName() +" "+ a.getSurname();
                }
                loadedArea.setText(output);
               // loadedArea.setText(oldText.replace("," +selectedAssistant.getName() +" "+ selectedAssistant.getSurname(),""));
            }
        }


    }
    public void loadAvailableAssistants() throws IOException {
        JsonManip jsom = new JsonManip();
        int i =0;
        ArrayList<ShiftTextArea> tempListDay = listOfAssistantLists.get(0);
        AvailableAssistants avAsD = jsom.loadAvailableAssistantInfo();
        ArrayList<ArrayList<Assistant>> listDays = avAsD.getAvailableAssistantsAtDays();
        for(ShiftTextArea arList: tempListDay ){
                arList.setAvailableAssistants(listDays.get(i));
                String output;
                for(Assistant as: listDays.get(i)){
                     output = arList.getText();
                    arList.setText(output + "," + as.getName() +" "+ as.getSurname());
                }
                i++;

        }
        i = 0;
        ArrayList<ShiftTextArea> tempListNight = listOfAssistantLists.get(1);
        AvailableAssistants avAsN = jsom.loadAvailableAssistantInfo();
        ArrayList<ArrayList<Assistant>> listNights = avAsD.getAvailableAssistantsAtNights();
        for(ShiftTextArea sText: tempListNight ){
                sText.setAvailableAssistants(listNights.get(i));
            String output;
            for(Assistant as: listDays.get(i)){
                output = sText.getText();
                sText.setText(output + "," + as.getName() +" "+ as.getSurname());
            }
                i++;
        }

    }
    public void saveCurrentState() throws IOException {
        JsonManip jsom = new JsonManip();
        AvailableAssistants availableAssistants = new AvailableAssistants();
        ArrayList<ArrayList<Assistant>> dayList = new ArrayList<>();
        ArrayList<ArrayList<Assistant>> nightList = new ArrayList<>();
        int shift = 0;
            for(ArrayList<ShiftTextArea> arlist: listOfAssistantLists){
                for(ShiftTextArea arl: arlist){
                    if(arl.getType() ==0){
                        dayList.add(arl.getAvailableAssistants());
                    }else{
                        nightList.add(arl.getAvailableAssistants());
                    }
                }
            }

        availableAssistants.setAvailableAssistantsAtDays(dayList);
            availableAssistants.setAvailableAssistantsAtNights(nightList);
          jsom.saveAvailableAssistantInfo(availableAssistants);
    }
}