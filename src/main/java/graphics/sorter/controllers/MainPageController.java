package graphics.sorter.controllers;

import graphics.sorter.*;
import graphics.sorter.Filters.Sorter;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

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
    private TextArea selectedTextArea = null;




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
                    if(getAssistantOfDay(clm.getClientNightsInMonth().get(dayIter)) == null){
                        inputText= "Night" + 1 +"\n" + "none" ;
                    }else{
                        inputText= "Night" + 1 + "\n" + getAssistantOfDay(clm.getClientNightsInMonth().get(dayIter)).getName() +" "+getAssistantOfDay(clm.getClientNightsInMonth().get(dayIter)).getSurname();
                    }
                    setTextArea(ar2,inputText,false,nightList,clm.getClientNightsInMonth().get(dayIter));
                    grid.setMargin(ar2,new Insets(0, 0, 0, 125));
                    grid.setConstraints(ar2,i+2,clienMothIter+1,3,1);

                    i = i+4;
                }else{
                    if(getAssistantOfDay(clm.getClientNightsInMonth().get(dayIter)) == null){
                        inputText= "Night" + i/2 +"\n" + "none" ;
                    }else{
                        inputText= "Night" + i/2 + "\n" + getAssistantOfDay(clm.getClientNightsInMonth().get(dayIter)).getName() +" "+getAssistantOfDay(clm.getClientNightsInMonth().get(dayIter)).getSurname();
                    }
                    //inputText= "Night" + i/2;
                    setTextArea(nightTextAr,inputText,false,nightList,clm.getClientNightsInMonth().get(dayIter));
                    grid.setMargin(nightTextAr,new Insets(0, 126, 0, 125));
                    grid.setConstraints(nightTextAr,i,clienMothIter+1,3,1);
                    i = i+2;
                }


            }
            clienMothIter = clienMothIter+2;
            clientIter++;
        }

        grid.getChildren().addAll(areaList);
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
        for (int dayIter = 0; dayIter < monthLength; dayIter++) {
            for( Client cl : listOfClients.getClientList()) {
                ClientDay clDay = cl.getClientsMonth().getClientDaysInMonth().get(dayIter);
                ClientDay clNight = cl.getClientsMonth().getClientNightsInMonth().get(dayIter);
                ArrayList<Assistant> listOfAvailableAtDay = getAvailableAssistantForDay(avAs,dayIter,true);
                ArrayList<Assistant> listOfAvailableAtNight = getAvailableAssistantForDay(avAs,dayIter,false);

                if (!listOfAvailableAtDay.isEmpty()) {
                    for (ServiceInterval sevInt : clDay.getDayIntervalList()) {
                        sevInt.setOverseeingAssistant(listOfAvailableAtDay.get(0));
                    }
                    listOfAvailableAtDay.remove(0);
                }else{
                    for (ServiceInterval sevInt : clDay.getDayIntervalList()) {
                        sevInt.setOverseeingAssistant(null);
                    }
                }

                if (!listOfAvailableAtNight.isEmpty()) {
                    for (ServiceInterval sevInt : clNight.getDayIntervalList()) {
                        sevInt.setOverseeingAssistant(listOfAvailableAtNight.get(0));
                    }
                    listOfAvailableAtNight.remove(0);
                }else {
                    for (ServiceInterval sevInt : clNight.getDayIntervalList()) {
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
    }
    public void findSolutionV2(ActionEvent actionEvent) throws IOException {
        Sorter sorter = new Sorter(jsom.loadAssistantInfo());

        int monthLength;
        AvailableAssistants avAs = null;
        avAs = jsom.loadAvailableAssistantInfo(settings);

        ListOfClients listOfClients = jsom.loadClientInfo(settings);
        ListOfClientMonths cliM = new ListOfClientMonths();
        for(Client cl : listOfClients.getClientList()){
            cliM .getListOfClientMonths().add(cl.getClientsMonth());
        }
        monthLength = cliM.getListOfClientMonths().get(0).getClientDaysInMonth().size();
        for (int dayIter = 0; dayIter < monthLength; dayIter++) {
            for( Client cl : listOfClients.getClientList()) {
                ClientDay clDay = cl.getClientsMonth().getClientDaysInMonth().get(dayIter);
                ClientDay clNight = cl.getClientsMonth().getClientNightsInMonth().get(dayIter);
                ArrayList<Assistant> listOfAvailableAtDay = getAvailableAssistantForDay(avAs,dayIter,true);
                ArrayList<Assistant> listOfAvailableAtNight = getAvailableAssistantForDay(avAs,dayIter,false);

                UUID dayPicked = sorter.sort(listOfAvailableAtDay,dayIter,0,clDay);
                //System.out.println(sorter.getIdFromList(listOfAvailableAtDay));
                UUID nightPicked = sorter.sort(listOfAvailableAtNight,dayIter,1,clNight);
            }

        }
        jsom.saveClientRequirementsForMonth(cliM,settings);
        jsom.saveClientInfo(listOfClients.convertToListOfClientProfiles());
        populateView(getClientsOfMonth(settings));
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
        if(selectedTextArea==null){
            mainGrid.setConstraints(TestScrollPane,mainGrid.getColumnIndex(TestScrollPane),mainGrid.getRowIndex(TestScrollPane),mainGrid.getColumnSpan(TestScrollPane),mainGrid.getRowSpan(TestScrollPane)-1);
            dayInfoGrid.setVisible(true);
            isMenuVisible=true;
            selectedTextArea = (TextArea) mouseEvent.getSource();
            loadDayData(selectedTextArea);
            selectedTextArea.setStyle("-fx-border-color: green");
        } else if (selectedTextArea==mouseEvent.getSource()) {
                mainGrid.setConstraints(TestScrollPane,mainGrid.getColumnIndex(TestScrollPane),mainGrid.getRowIndex(TestScrollPane),mainGrid.getColumnSpan(TestScrollPane),mainGrid.getRowSpan(TestScrollPane)+1);
                dayInfoGrid.setVisible(false);
                isMenuVisible=false;
                selectedTextArea.setStyle("-fx-border-color: null");
                selectedTextArea =null;

            }else{

            selectedTextArea.setStyle("-fx-border-color: null");
            selectedTextArea =(TextArea) mouseEvent.getSource();
            selectedTextArea.setStyle("-fx-border-color: green");
            loadDayData(selectedTextArea);
        }
        }
    private void loadDayData(TextArea sourceTex){
        TextArea outputText = new TextArea();
        ClientDay day = textClientIndex.get(sourceTex);
        //.getDisplayName(TextStyle.FULL, new Locale("cs", "CZ")
        outputText.setText("Datum : " + day.getDay() +"."+ day.getMonth().getValue()+"."+day.getYear() + "\n");
        String dayName =LocalDate.of(day.getYear(),day.getMonth().getValue(),day.getDay()).getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("cs", "CZ"));
        dayName =  dayName.substring(0, 1).toUpperCase() + dayName.substring(1);
        outputText.setText(outputText.getText() + dayName+ "\n");

        outputText.setText(outputText.getText() + printAssistantsOfDay(day));
        dayInfoGrid.setConstraints(outputText,0,0,dayInfoGrid.getColumnCount(),dayInfoGrid.getRowCount());
        dayInfoGrid.getChildren().add(outputText);
    }
    public String printAssistantsOfDay(ClientDay day){
        Set<Assistant> assistants = new HashSet<>();
        for (ServiceInterval sein : day.getDayIntervalList()) {
            if (sein.getOverseeingAssistant() != null) {
                assistants.add(sein.getOverseeingAssistant());
            }
        }

        return assistants.stream()
                .map(as -> as.getName() + " " + as.getSurname())
                .collect(Collectors.joining(", "));
    }
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
