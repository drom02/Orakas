package graphics.sorter.controllers;

import graphics.sorter.*;
import graphics.sorter.Structs.HumanCellFactory;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
Controller for shift picker window. It is used to select when are assistants available. Select name from the list and
left click to field to add the assistant for specific shift. Right click to remove.
 */
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
   private Settings settings;


   /*
   Method will generate empty table with only column and row titles.
    */
    public void populateTable(Month moth, int year){
        editedMonth = moth;
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
                LocalDate date = LocalDate.of(year, editedMonth.getValue(), i);
                DayOfWeek dayValue = date.getDayOfWeek();
                inputText = i + "."+ moth.getValue()+ "."+year +"\n" + dayValue.getDisplayName(TextStyle.FULL, new Locale("cs", "CZ"));
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
    }
    /*
    Method initialize
    */
    public void initialize() throws IOException {
        JsonManip jsoMap= new JsonManip();
        settings = jsoMap.loadSettings("E:\\JsonWriteTest\\");
        assistantList.setCellFactory(new HumanCellFactory());

        ListOfAssistants listOfA = jsoMap.loadAssistantInfo();
        listOfAssist = listOfA .getAssistantList();
        ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.assistantList);
        assistantList.setItems(observAssistantList);
        populateTable(Month.of(settings.getCurrentMonth()),settings.getCurrentYear());
        loadAvailableAssistants();
    }

    /*
    Will switch window to main window.
     */
    public void switchPageToMain(ActionEvent actionEvent) throws IOException {
       Scene scen = TestScrollPane.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Main-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);

    }
    /*
    Will select assistant based on the clicked field in list.
     */
    public void selectAssistant(MouseEvent mouseEvent) {
        selectedAssistant = (Assistant) assistantList.getSelectionModel().getSelectedItem();
    }
    /*
    Will add or remove selected assistant to clicked shift field.
     */
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
    /*
    Will populate the table with latest saved state.
    //Todo option to select save to load.
     */
    public void loadAvailableAssistants() throws IOException {
        JsonManip jsom = new JsonManip();

        int i =0;
        ArrayList<ShiftTextArea> tempListDay = listOfAssistantLists.get(0);
        AvailableAssistants avAsD = null;
        avAsD = jsom.loadAvailableAssistantInfo(settings);
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
        AvailableAssistants avAsN = jsom.loadAvailableAssistantInfo(settings);
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
    public void generateNewMonthsAssistants() throws IOException {
        JsonManip jsom = new JsonManip();
        ListOfAssistants listOfA = jsom.loadAssistantInfo();
        AvailableAssistants availableAssistants = new AvailableAssistants();
        ArrayList<ArrayList<Assistant>> dayList = new ArrayList<>();
        ArrayList<ArrayList<Assistant>> nightList = new ArrayList<>();
        for(int i =0; i < Month.of(settings.getCurrentMonth()).length(Year.isLeap(settings.getCurrentYear())); i++){
            ArrayList<Assistant> inputDayList = new ArrayList<>();
            ArrayList<Assistant> inputNightList = new ArrayList<>();
            dayList.add(inputDayList);
            nightList.add(inputNightList);
            for(Assistant asis : listOfA.getAssistantList()){
                if(asis.getWorkDays()[(LocalDate.of(settings.getCurrentYear(), settings.getCurrentMonth(), i).getDayOfWeek().getValue()-1)] == 1 ){
                    inputDayList.add(asis);
                }
                if(asis.getWorkDays()[(LocalDate.of(settings.getCurrentYear(), settings.getCurrentMonth(), i).getDayOfWeek().getValue()+7)] == 1 ){
                    inputNightList.add(asis);
                }

            }


        }

        availableAssistants.setAvailableAssistantsAtDays(dayList);
        availableAssistants.setAvailableAssistantsAtNights(nightList);
        jsom.saveAvailableAssistantInfo(availableAssistants,settings);
    }
    /*
    Will save current state to json file.
    //TODO better save system
     */
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
          jsom.saveAvailableAssistantInfo(availableAssistants,settings);
    }
    public void generateEmptyState() throws IOException {
        JsonManip jsom = new JsonManip();
        AvailableAssistants availableAssistants = new AvailableAssistants();
        ArrayList<ArrayList<Assistant>> dayList = new ArrayList<>();
        ArrayList<ArrayList<Assistant>> nightList = new ArrayList<>();
        int shift = 0;

            for(int i =0; i < Month.of(settings.getCurrentMonth()).length(Year.isLeap(settings.getCurrentYear())); i++){
                    dayList.add(new ArrayList<>());
                    nightList.add(new ArrayList<>());

        }

        availableAssistants.setAvailableAssistantsAtDays(dayList);
        availableAssistants.setAvailableAssistantsAtNights(nightList);
        jsom.saveAvailableAssistantInfo(availableAssistants,settings);
    }
}