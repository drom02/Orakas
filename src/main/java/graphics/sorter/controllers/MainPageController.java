package graphics.sorter.controllers;

import graphics.sorter.*;
import graphics.sorter.Structs.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;

public class MainPageController {
    @FXML
    private ScrollPane TestScrollPane;
    @FXML
    private Label welcomeText;
    @FXML
    private DatePicker datePick;
    @FXML
    private Text selectedYear;
    @FXML
    private Text selectedMonth;
    @FXML
    private Text selectedYearValue;
    @FXML
    private Text selectedMonthValue;
    @FXML
    private GridPane mainGrid;
    @FXML
    private GridPane dayInfoGrid;
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    private Month editedMonth;
    private ArrayList<TextArea> nightList = new ArrayList<TextArea>();
    private ArrayList<TextArea> dayList = new ArrayList<TextArea>();
    private ArrayList<TextArea> areaList;
    private ArrayList<TextArea> titleList;
    private JsonManip jsom = new JsonManip();
    private Settings settings;
    private GridPane grid = new GridPane();
    private Boolean isMenuVisible;
    private HashMap<TextArea,ClientDay> textClientIndex = new HashMap<>();




    public void populateView(ListOfClients LiCcl){
        grid.getChildren().clear();
        ListOfClientMonths LiClMo = new ListOfClientMonths();
        for(Client cl : LiCcl.getClientList()){
            LiClMo.getListOfClientMonths().add(cl.getClientsMonth());
        }
        //vytahnout y klientu m2s9c2
        editedMonth = Month.of(settings.getCurrentMonth());
         areaList = new ArrayList<TextArea>();
         titleList = new ArrayList<TextArea>();

        int i = 0;
        /*
        Vytvoří nadpisy pro jednotlivé dny
         */
        while (i <= editedMonth.length(false)){
            String inputText;
            if(i == 0){
                inputText = "Jméno Klienta";
            }else{
                 inputText = i + "."+ settings.getCurrentMonth()+ "."+settings.getCurrentYear();
            }
            TextArea newTextArea = new TextArea(inputText);
            titleList.add(newTextArea);
            i++;
        }
        TextArea newTextArea = new TextArea("Další měsíc");
        titleList.add(newTextArea);
        i = 0;

        for (TextArea ar : titleList){
            ar.setPrefSize(250,50);
            grid.setConstraints(ar,i,0,2,1);
            grid.getChildren().addAll(ar);
                i = i+2;
        }
        /* Vytvoří text area pro jednotlivé měsíce klienta
        */
        int clienMothIter = 1;
        int clientIter = 0;
        for(ClientMonth clm : LiClMo.getListOfClientMonths()){
            LiClMo.getListOfClientMonths().get(0);
            i = 0;
            for (int dayIter = -1; dayIter < clm.getClientDaysInMonth().size(); dayIter++){
                String inputText;
                TextArea dayTextAr = new TextArea();
                if(i==0){
                    inputText = LiCcl.getClientList().get(clientIter).getName() + " " + LiCcl.getClientList().get(clientIter).getSurname();
                    setTextArea(dayTextAr,inputText,true,titleList);
                    grid.setConstraints(dayTextAr,i,clienMothIter,2,1);
                    i = i+2;
                }else{
                    if(getAssistantOfDay(clm.getClientDaysInMonth().get(dayIter)) == null){
                        //inputText= "Day" + i/2 +"\n" + getAssistantOfDay(clm.getClientDaysInMonth().get(dayIter));
                        inputText= "Day" + i/2 +"\n" + "none" ;
                    }else{
                        inputText= "Day" + i/2 + "\n" + getAssistantOfDay(clm.getClientDaysInMonth().get(dayIter)).getName() +" "+getAssistantOfDay(clm.getClientDaysInMonth().get(dayIter)).getSurname();
                        System.out.println(inputText);
                    }
                    setTextArea(dayTextAr,inputText,false,dayList,clm.getClientDaysInMonth().get(dayIter));
                    grid.setConstraints(dayTextAr,i,clienMothIter,1,1);
                    i = i+2;
                }


            }
            i=0;
            for (int dayIter = 0; dayIter < clm.getClientNightsInMonth().size(); dayIter++){
                String inputText;
                TextArea nightTextAr = new TextArea();
                if(i==0){
                    inputText = LiCcl.getClientList().get(clientIter).getName() + " " + LiCcl.getClientList().get(clientIter).getSurname();
                    setTextArea(nightTextAr,inputText,true,titleList);
                    grid.setConstraints(nightTextAr,i,clienMothIter+1,2,1);
                    TextArea ar2 = new TextArea();
                    inputText= "Night" + 1;
                    setTextArea(ar2,inputText,false,nightList,clm.getClientDaysInMonth().get(dayIter));
                    grid.setMargin(ar2,new Insets(0, 0, 0, 125));
                    grid.setConstraints(ar2,i+2,clienMothIter+1,3,1);

                    i = i+4;
                }else{
                    inputText= "Night" + i/2;
                    setTextArea(nightTextAr,inputText,false,nightList,clm.getClientDaysInMonth().get(dayIter));
                    grid.setMargin(nightTextAr,new Insets(0, 0, 0, 125));
                    grid.setConstraints(nightTextAr,i,clienMothIter+1,3,1);
                    i = i+2;
                }


            }
            clienMothIter = clienMothIter+2;
            clientIter++;
        }

        grid.getChildren().addAll(areaList);
        System.out.println("end");
        TestScrollPane.setContent(grid);
    }



    public ListOfClients getClientsOfMonth(Settings settings) throws IOException {
        try{
            ListOfClients out =   jsom.loadClientInfo(settings);
            return  out;
        }catch(Exception e){
            ListOfClientsProfiles lOCP = jsom.loadClientProfileInfo();
            ListOfClientMonths listOfClm = new ListOfClientMonths();
            ListOfClients listOfClients = new ListOfClients();
            for(ClientProfile clP: lOCP.getClientList()){
                Client out;
                if(!(listOfClm.getMonthOfSpecificClient(clP.getID()) == null)){
                    out = clP.convertToClient(listOfClm.getMonthOfSpecificClient(clP.getID()));
                }else{
                    ClientMonth temp = new ClientMonth(Month.of(settings.getCurrentMonth()), settings.getCurrentYear(), clP.getID(), clP.getHomeLocation());
                    listOfClm.getListOfClientMonths().add(temp);
                    out = clP.convertToClient(temp);
                }
                listOfClients.getClientList().add(out);
            }
            jsom.saveClientRequirementsForMonth(listOfClm,settings);
            return listOfClients;
        }
    }
    public Assistant getAssistantOfDay(ClientDay clDay){
        if(!clDay.getDayIntervalList().isEmpty()){
            return clDay.getDayIntervalList().get(0).getOverseeingAssistant();
        }else{
            return  null;
        }


    }
    public void initialize() throws IOException {
        dayInfoGrid.setVisible(false);
        isMenuVisible = false;
        settings = jsom.loadSettings("E:\\JsonWriteTest\\");
        selectedYearValue.setText(String.valueOf(settings.getCurrentMonth()));
        selectedMonthValue.setText(String.valueOf(settings.getCurrentYear()));
        populateView(getClientsOfMonth(settings));
        mainGrid.setConstraints(TestScrollPane,mainGrid.getColumnIndex(TestScrollPane),mainGrid.getRowIndex(TestScrollPane),mainGrid.getColumnSpan(TestScrollPane),mainGrid.getRowSpan(TestScrollPane)+1);

    }
    public void initializeClients(){
    }
    public void switchPage(ActionEvent actionEvent) throws IOException {
       Scene scen = TestScrollPane.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("assistant-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);

    }
    public void switchPageToShift(ActionEvent actionEvent) throws IOException {
        Scene scen = TestScrollPane.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("shiftPicker-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);
    }

    public void findSolution(ActionEvent actionEvent) throws IOException {
        int monthLength;
        AvailableAssistants avAs = null;
        avAs = jsom.loadAvailableAssistantInfo(settings);

        ListOfClients listOfClients = jsom.loadClientInfo(settings);
        ListOfClientMonths cliM = new ListOfClientMonths();
        for(Client cl : listOfClients.getClientList()){
            cliM .getListOfClientMonths().add(cl.getClientsMonth());
        }
        monthLength = cliM.getListOfClientMonths().get(0).getClientDaysInMonth().size();
        System.out.println(getAvailableAssistantForDay(avAs,0,true));
        for (int dayIter = 0; dayIter < monthLength; dayIter++) {
            for( Client cl : listOfClients.getClientList()) {
                ClientDay clDay = cl.getClientsMonth().getClientDaysInMonth().get(dayIter);
                ArrayList<Assistant> listOfAvailable = getAvailableAssistantForDay(avAs,dayIter,true);
                if (!listOfAvailable.isEmpty()) {
                    for (ServiceInterval sevInt : clDay.getDayIntervalList()) {
                        sevInt.setOverseeingAssistant(listOfAvailable.get(0));
                    }
                    listOfAvailable.remove(0);
                }else{
                    for (ServiceInterval sevInt : clDay.getDayIntervalList()) {
                        sevInt.setOverseeingAssistant(null);
                    }
                }
            }
        }
        jsom.saveClientRequirementsForMonth(cliM,settings);
        jsom.saveClientInfo(listOfClients.convertToListOfClientProfiles());
        for(TextArea tex : areaList){
            tex.clear();

        }
        populateView(getClientsOfMonth(settings));
        System.out.println("aa");
    }
    private ArrayList<Assistant> getAvailableAssistantForDay(AvailableAssistants lisA, int date, boolean day ){
        ArrayList<Assistant> output;
        if(day==true){
            output = lisA.getAvailableAssistantsAtDays().get(date);
        }else{
            output = lisA.getAvailableAssistantsAtNights().get(date);
        }
        return output;

    }
    public void saveChangedDate() throws IOException {
        String[] parts = datePick.getValue().toString().split("-");
        selectedYearValue.setText(parts[0]);
        selectedMonthValue.setText(parts[1]);
        settings.setCurrentYear(Integer.parseInt(parts[0]));
        settings.setCurrentMonth(Integer.parseInt(parts[1]));
        try {
            jsom.saveSettings(settings, settings.getFilePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        settings = jsom.loadSettings("E:\\JsonWriteTest\\");
        populateView(getClientsOfMonth(settings));
        /*
        for( Node ce : grid.getChildren()){
            if( ce instanceof TextArea){
                TextArea textArea = (TextArea) ce;
                System.out.println("Text " +textArea.getText());
                System.out.println("Column index " +grid.getColumnIndex(textArea));
                System.out.println("Column span " + grid.getColumnSpan(textArea));
                System.out.println(textArea.getWidth());
                System.out.println("------------------------------------------------------------");
            }

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setMaxWidth(125);
            col1.setMinWidth(125);
            col1.setPrefWidth(125);
            grid.getColumnConstraints().addAll(col1);
             */

        }

    public void switchPageToLocation(ActionEvent actionEvent) throws IOException {
        Scene scen = TestScrollPane.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Location-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);
    }

    public void switchPageToClient(ActionEvent actionEvent) throws IOException {
        Scene scen = TestScrollPane.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Client-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);
    }
    private void setTextArea(TextArea textArea, String inputText, Boolean isDescrip, ArrayList arList){
        textArea.setEditable(false);
        textArea.setText(inputText);
        textArea.setPrefSize(250,100);
        arList.add(textArea);
        areaList.add(textArea);
        if(isDescrip==false){
            textArea.setOnMouseClicked(this :: displayDayInfoFull);

        }
    }

    private void setTextArea(TextArea textArea, String inputText, Boolean isDescrip, ArrayList arList, ClientDay day){
        setTextArea(textArea,inputText,isDescrip,arList);
        textClientIndex.put(textArea,day);
    }
    public void displayDayInfoFull(MouseEvent mouseEvent){
    if(isMenuVisible==true){
        mainGrid.setConstraints(TestScrollPane,mainGrid.getColumnIndex(TestScrollPane),mainGrid.getRowIndex(TestScrollPane),mainGrid.getColumnSpan(TestScrollPane),mainGrid.getRowSpan(TestScrollPane)+1);
        dayInfoGrid.setVisible(false);
        isMenuVisible=false;

    }else{mainGrid.setConstraints(TestScrollPane,mainGrid.getColumnIndex(TestScrollPane),mainGrid.getRowIndex(TestScrollPane),mainGrid.getColumnSpan(TestScrollPane),mainGrid.getRowSpan(TestScrollPane)-1);
        dayInfoGrid.setVisible(true);
        isMenuVisible=true;
    }
    }
}
