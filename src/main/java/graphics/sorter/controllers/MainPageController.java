package graphics.sorter.controllers;

import graphics.sorter.*;
import graphics.sorter.Filters.Sorter;
import graphics.sorter.Structs.*;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainPageController {
    @FXML
    private CheckBox isRequiredCheckBox;
    @FXML
    private TextArea intervalCommentArea;
    @FXML
    private ChoiceBox<Integer> startHoursChoice;
    @FXML
    private ChoiceBox<Integer>  startMinutesChoice;
    @FXML
    private ChoiceBox<Integer>  endHoursChoice;
    @FXML
    private ChoiceBox<Integer>  endMinutesChoice;
    @FXML
    private ScrollPane TestScrollPane;
    @FXML
    private Label welcomeText;
    @FXML
    private DatePicker datePick;
    @FXML
    private Text selectedYearValue;
    @FXML
    private Text selectedMonthValue;
    @FXML
    private GridPane mainGrid;
    @FXML
    private GridPane dayInfoGrid;
    @FXML
    private GridPane barGrid;
    private Month editedMonth;
    private ArrayList<TextArea> nightList = new ArrayList<TextArea>();
    private ArrayList<TextArea> dayList = new ArrayList<TextArea>();
    private ArrayList<ArrayList<TextArea>>  dayNightSwitch = new ArrayList<ArrayList<TextArea>>(Arrays.asList(dayList,nightList));
    private ArrayList<TextArea> areaList;
    private ArrayList<TextArea> titleList;
    private JsonManip jsom = new JsonManip();
    private Settings settings;
    private GridPane grid = new GridPane();
    private Boolean isMenuVisible = false;
    private HashMap<TextArea,ClientDay> textClientIndex = new HashMap<>();
    private HashMap<AnchorPane,ServiceInterval> paneServiceIndex = new HashMap<>();
    private TextArea selectedTextArea = null;
    private ListOfClientMonths listOfClm = new ListOfClientMonths();
    private ArrayList<Integer> hoursList = new ArrayList<>();
    private ArrayList<Integer> minuteList = new ArrayList<>();
    private ArrayList<ChangeListener<Integer>> listOfObserv = new ArrayList<>();
    private ServiceInterval selectedInterval;
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
    public void generateObservers(ArrayList<ChoiceBox<Integer>> arr ){
        for(int i = 0;i<4;i++){
            int finalI = i;
            /*
            TODO add method to deal with hours between start of night shift and midnight. It is impossible to currently set end of night shift to before midnight.
             */
            ChangeListener<Integer> myListener = (observable, oldValue, newValue) -> {
                if (startHoursChoice.getValue() != null & startMinutesChoice.getValue() != null&endHoursChoice.getValue() != null & endMinutesChoice.getValue() != null ){
                    LocalDateTime start = setLocalDateTime(startHoursChoice.getValue(),startMinutesChoice.getValue(),textClientIndex.get(selectedTextArea).getDay());
                    LocalDateTime end = setLocalDateTime(endHoursChoice.getValue(),endMinutesChoice.getValue(),textClientIndex.get(selectedTextArea).getDay());
                  //  System.out.println("t");
                            if(start.isAfter(end) || start.isEqual(end)){
                                arr.get(finalI).setValue(oldValue);
                              //  System.out.println(arr.get(finalI).getValue());


                        }


                   }

            };
            listOfObserv.add(myListener);
        }
    }
    public void attachObservers(){
        ArrayList<ChoiceBox<Integer>> arr = new ArrayList<>(Arrays.asList(startHoursChoice,startMinutesChoice,endHoursChoice,endMinutesChoice));
        generateObservers(arr);
        int i = 0;
        for(ChoiceBox choc : arr){
            choc.getSelectionModel().selectedItemProperty().addListener(listOfObserv.get(i));
            i++;
        }
    }
    public ListOfClients getClientsOfMonth(Settings settings) throws IOException {
        try{
            ListOfClients out =   jsom.loadClientInfo(settings);
            for(Client cl: out.getClientList()){
                listOfClm.getListOfClientMonths().add(cl.getClientsMonth());
            }
            return  out;
        }catch(Exception e){
            ListOfClientsProfiles lOCP = jsom.loadClientProfileInfo();
            listOfClm = new ListOfClientMonths();
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
        if(!clDay.getDayIntervalListUsefull().isEmpty()){
            return clDay.getDayIntervalListUsefull().get(0).getOverseeingAssistant();
        }else{
            return  null;
        }


    }
    public void initialize() throws IOException {

        prepareHoursAndMinutes();
        dayInfoGrid.setVisible(false);
        isMenuVisible = false;
        settings = jsom.loadSettings("E:\\JsonWriteTest\\");
        selectedYearValue.setText(String.valueOf(settings.getCurrentYear()));
        selectedMonthValue.setText(String.valueOf(settings.getCurrentMonth()));
        populateView(getClientsOfMonth(settings));
        mainGrid.setConstraints(TestScrollPane,mainGrid.getColumnIndex(TestScrollPane),mainGrid.getRowIndex(TestScrollPane),mainGrid.getColumnSpan(TestScrollPane),mainGrid.getRowSpan(TestScrollPane)+1);
        attachObservers();


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
    public void findSolutionV2(ActionEvent actionEvent) throws IOException {
        Sorter sorter = new Sorter(jsom.loadAssistantInfo());

        int monthLength;
        AvailableAssistants avAs = null;
        avAs = jsom.loadAvailableAssistantInfo(settings);

        ListOfClients listOfClients = jsom.loadClientInfo(settings);

        listOfClm = new ListOfClientMonths();
        for(Client cl : listOfClients.getClientList()){
            listOfClm .getListOfClientMonths().add(cl.getClientsMonth());
        }


        monthLength = listOfClm.getListOfClientMonths().get(0).getClientDaysInMonth().size();
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
        jsom.saveClientRequirementsForMonth(listOfClm,settings);
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
        setIntervalBars(day);
        dayInfoGrid.setConstraints(outputText,0,0,dayInfoGrid.getColumnCount()-2,dayInfoGrid.getRowCount()-2);
        dayInfoGrid.getChildren().add(outputText);
        displayIntervalInfoDef(day);
    }
    private ArrayList<Double> calculateBarWidth(ClientDay day){
      LocalDateTime  start = day.getDayIntervalList().getFirst().getStart();
        LocalDateTime  end = day.getDayIntervalList().getLast().getEnd();
        double multiplier = 100 - ((day.getDayIntervalList().size()-1)*0.5);
        long minutes = ChronoUnit.MINUTES.between(start,end);
        ArrayList<Double> sizes = new ArrayList<>();
        for(ServiceInterval serv : day.getDayIntervalList()){
            long minutesT = ChronoUnit.MINUTES.between(serv.getStart(),serv.getEnd());
            sizes.add((minutesT/(minutes/100))*multiplier/100);

        }
        return sizes;
    }
    private void setIntervalBars(ClientDay day){
        int c = 0;
       ArrayList<Double> percentSizes = calculateBarWidth(day);
        ArrayList<Pane> listToAdd = new ArrayList<>();
        barGrid.getColumnConstraints().clear();
        barGrid.getRowConstraints().clear();
        barGrid.getRowConstraints().add(new RowConstraints() {{ setPercentHeight(100); }});
        //barGrid.getRowConstraints().add(new RowConstraints() {{ setPercentHeight(30); }});
        int i = day.getDayIntervalList().size();
        int curInt = 1;
        int sizesIndex = 0;
        for(ServiceInterval serv : day.getDayIntervalList()){
            AnchorPane bar = new AnchorPane();
            paneServiceIndex.put(bar,serv);
            bar.setOnMouseClicked(this :: displayIntervalInfo);
            setIntervalDescription(bar,serv);
            if(serv.getIsNotRequired()== true){
                bar.setStyle("-fx-background-color: #FF0000;");
            }else{
                bar.setStyle("-fx-background-color: #008000;");
            }

            listToAdd.add(bar);
            bar.setMinSize(10, 10);
            ColumnConstraints colC = new ColumnConstraints();
            colC .setPercentWidth(percentSizes.get(sizesIndex));
            sizesIndex++;
            barGrid.getColumnConstraints().add(colC );
            barGrid.setConstraints(bar,c,0,1,1);
            c++;
            if(curInt!= i){
                AnchorPane bar2 = new AnchorPane();
                bar2.setMinSize(10, 10);
                listToAdd.add(bar2);
                bar2.setStyle("-fx-background-color: #000000;");
                ColumnConstraints colC2 = new ColumnConstraints();
                colC2 .setPercentWidth(0.5);
                barGrid.getColumnConstraints().add(colC2 );
                barGrid.setConstraints(bar2,c,0,1,1);
                c++;
            }
            curInt++;
        }
        barGrid.getChildren().addAll(listToAdd);

    }
    private void displayIntervalInfo(MouseEvent mouseEvent) {

       ServiceInterval serv = paneServiceIndex.get(mouseEvent.getSource());
       selectedInterval = serv;
       intervalCommentArea.setText(serv.getComment());
        isRequiredCheckBox.setSelected(serv.getIsNotRequired());
        if(dayList.contains(selectedTextArea)){
            //The interval is in day
            startHoursChoice.getItems().setAll(hoursList.subList(0,25));
            endHoursChoice.getItems().setAll(hoursList.subList(0,25));
            startMinutesChoice.getItems().setAll(minuteList.subList(0,60));
            endMinutesChoice.getItems().setAll(minuteList.subList(0,60));
        }else{
            startHoursChoice.getItems().setAll(hoursList.subList(0,25));
            endHoursChoice.getItems().setAll(hoursList.subList(0,25));
            startMinutesChoice.getItems().setAll(minuteList.subList(0,60));
            endMinutesChoice.getItems().setAll(minuteList.subList(0,60));
        }
        startHoursChoice.setValue(serv.getStart().getHour());
        startMinutesChoice.setValue(serv.getStart().getMinute());
        endHoursChoice.setValue(serv.getEnd().getHour());
        endMinutesChoice.setValue(serv.getEnd().getMinute());
        /*
        startHoursChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{

        });
         */

    }
    private void displayIntervalInfoDef(ClientDay day) {
        if(!day.getDayIntervalList().isEmpty()){
            ServiceInterval serv = day.getDayIntervalList().get(0);
            selectedInterval = serv;
            intervalCommentArea.setText(serv.getComment());
            isRequiredCheckBox.setSelected(serv.getIsNotRequired());

            if(dayList.contains(selectedTextArea)){
                //The interval is in day
                startHoursChoice.getItems().setAll(hoursList.subList(0,24));
                endHoursChoice.getItems().setAll(hoursList.subList(0,24));
                startMinutesChoice.getItems().setAll(minuteList.subList(0,60));
                endMinutesChoice.getItems().setAll(minuteList.subList(0,60));
            }else{
                startHoursChoice.getItems().setAll(hoursList.subList(12,24));
                endHoursChoice.getItems().setAll(hoursList.subList(0,12));
                startMinutesChoice.getItems().setAll(minuteList.subList(0,60));
                endMinutesChoice.getItems().setAll(minuteList.subList(0,60));
            }
            startHoursChoice.setValue(serv.getStart().getHour());
            startMinutesChoice.setValue(serv.getStart().getMinute());
            endHoursChoice.setValue(serv.getEnd().getHour());
            endMinutesChoice.setValue(serv.getEnd().getMinute());
        }else{
            return;
        }

    }
    private void setIntervalDescription(AnchorPane anch, ServiceInterval serv){
      //  Text start = new Text(serv.getStart().getHour() +":" + serv.getStart().getMinute());
       // Text end = new Text(serv.getEnd().getHour() +":" + serv.getEnd().getMinute());
        Text start = new Text(serv.getStart().toString());
        Text end = new Text(serv.getEnd().toString());
        anch.setTopAnchor(start,7.0);
        anch.setLeftAnchor(start,1.0);
        anch.setTopAnchor(end,7.0);
        anch.setRightAnchor(end,1.0);
        anch.getChildren().add(start);
        anch.getChildren().add(end);
    }
    public String printAssistantsOfDay(ClientDay day){
        Set<Assistant> assistants = new HashSet<>();
        for (ServiceInterval sein : day.getDayIntervalListUsefull()) {
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
    public void removeInterval(ActionEvent actionEvent) throws IOException {
        ClientDay day = textClientIndex.get(selectedTextArea);
        selectedInterval.setNotRequired(true);
        //selectedInterval.setOverseeingAssistant(null);
        selectedInterval.setLocation(null);
        selectedInterval.setComment("Klient v tomto období nevyžaduje asistenta.");
        intervalCommentArea.setText("Klient v tomto období nevyžaduje asistenta.");
        setIntervalBars(day);
        jsom.saveClientRequirementsForMonth(listOfClm,settings);
    }
    public void prepareHoursAndMinutes(){
        hoursList = (ArrayList<Integer>) IntStream.rangeClosed(0, 24).boxed().collect(Collectors.toList());
        minuteList = (ArrayList<Integer>) IntStream.rangeClosed(0, 60).boxed().collect(Collectors.toList());
    }
    private void newInterOutOfBounds(){

    }
    public void createNewInterval(ActionEvent actionEvent) throws IOException {
        System.out.println("toBeReady");
        ClientDay day = textClientIndex.get(selectedTextArea);
        ArrayList<ServiceInterval> toBeResized = new ArrayList<>();
        LocalDateTime startNew = setLocalDateTime(startHoursChoice.getValue(),startMinutesChoice.getValue(),textClientIndex.get(selectedTextArea).getDay());
        LocalDateTime endNew = setLocalDateTime(endHoursChoice.getValue(), + endMinutesChoice.getValue(),textClientIndex.get(selectedTextArea).getDay());
        ArrayList<ServiceInterval> toBeRemoved = new ArrayList<>();
        boolean alreadyExists = false;
        for(ServiceInterval s : day.getDayIntervalList()){
            LocalDateTime start = s.getStart();
            LocalDateTime end = s.getEnd();
            if((startNew.isAfter(start) ||startNew.isEqual(start)) && (startNew.isBefore(end) ||startNew.isEqual(end)) ||
                    (endNew.isAfter(start)||startNew.isEqual(start)) && (endNew.isBefore(end)||startNew.isEqual(end)) ){
                toBeResized.add(s);
                System.out.println("toBeResized");
            }
            if(start.isAfter(startNew)&& end.isBefore(endNew) ){
                System.out.println("toBeRemoved");
                toBeRemoved.add(s);
            }
            if(start.isEqual(startNew)&&end.isEqual(endNew)){
                alreadyExists=true;
                System.out.println("Already exists");
            }
        }
        if(alreadyExists == false) {

            for (ServiceInterval s : toBeResized) {
                LocalDateTime start = s.getStart();
                LocalDateTime end = s.getEnd();
                if (!startNew.equals(start) || !endNew.equals(end)) {
                    if ((startNew.isAfter(start) && startNew.isBefore(end)) & (endNew.isAfter(end) || endNew.isEqual(end))) {
                        s.setEnd(setLocalDateTime(startHoursChoice.getValue(), startMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()));
                        System.out.println("Type 1");
                    } else if ((startNew.isBefore(start) || startNew.isEqual(start)) & (endNew.isAfter(start) || endNew.isEqual(start) && endNew.isBefore(end) || endNew.isEqual(end))) {
                        s.setStart(setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()));
                        System.out.println("Type 2");
                    } else {
                        LocalDateTime temp = s.getEnd();
                        s.setEnd(setLocalDateTime(startHoursChoice.getValue(), startMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()));
                        day.getDayIntervalList().add(new ServiceInterval(setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay())
                                , temp, s.getOverseeingAssistant(), null, false));
                        System.out.println("Type 3");

                    }
                    day.getDayIntervalList().add(new ServiceInterval(setLocalDateTime(startHoursChoice.getValue(), startMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay())
                            , setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()), day.getDayIntervalList().get(0).getOverseeingAssistant(), null, false));

                } else {
                    System.out.println("nothing");
                    break;// paneServiceIndex.get(selectedTextArea).getDayIntervalList().add(new ServiceInterval());
                }
                intervalOverreach(startNew, endNew);
                jsom.saveClientRequirementsForMonth(listOfClm, settings);
                day.getDayIntervalListUsefull().get(0).setComment("Testing save logic");
                setIntervalBars(day);
            }
        }
    }
    public void intervalOverreach(LocalDateTime newStart, LocalDateTime newEnd){

       // LocalDateTime defStart = LocalDateTime.of(settings.getDeftStart()[0],settings.getDeftStart()[1]);
       // LocalDateTime defEnd = settings.getDefEnd();
        if(newStart.getDayOfMonth() == 1||
                newEnd.getDayOfMonth() == newEnd.getMonth().length(Year.isLeap(newStart.getYear()))){
            return;

        }
        boolean isDay;
        int[] switchCode;
        if(dayList.contains(selectedTextArea)){
            isDay = true;
            switchCode = new int[]{-1,0};
        }else{
            isDay = false;
            switchCode = new int[]{0,1};
        }
            if(isDay==true){
                ClientDay past = textClientIndex.get(nightList.get(dayList.indexOf(selectedTextArea)+switchCode[0]));
                ServiceIntervalArrayList servListPast= past.getDayIntervalList();
              //  int i = servListPast.size()-1;
                ArrayList<ServiceInterval> toBeRemoved = new ArrayList<>();
                for(ServiceInterval serv: servListPast.reversed()){
                    LocalDateTime startTemp = serv.getStart();
                    LocalDateTime defEnd= serv.getEnd();
                    if(startTemp.isAfter(newStart)){
                        toBeRemoved.add(serv);
                    }
                }
                servListPast.removeAll(toBeRemoved);
                servListPast.get(servListPast.size()-1).setEnd(newStart);

                ClientDay future = textClientIndex.get(nightList.get(dayList.indexOf(selectedTextArea)+switchCode[1]));
                ServiceIntervalArrayList servListFuture= future.getDayIntervalList();
                //int iFuture = servListFuture.size()-1;
                ArrayList<ServiceInterval> toBeRemovedFuture = new ArrayList<>();
                for(ServiceInterval serv: servListFuture){
                    LocalDateTime startTemp = serv.getStart();
                    LocalDateTime endTemp= serv.getEnd();
                    if(endTemp.isBefore(newEnd)){
                        toBeRemovedFuture.add(serv);
                    }
                }
                servListFuture.removeAll(toBeRemovedFuture);
                servListFuture.get(0).setStart(newEnd);
            }else{
                ClientDay past = textClientIndex.get(dayList.get(nightList.indexOf(selectedTextArea)+switchCode[0]));
                ServiceIntervalArrayList servListPast= past.getDayIntervalList();
                //  int i = servListPast.size()-1;
                ArrayList<ServiceInterval> toBeRemoved = new ArrayList<>();
                for(ServiceInterval serv: servListPast.reversed()){
                    LocalDateTime startTemp = serv.getStart();
                    LocalDateTime defEnd= serv.getEnd();
                    if(startTemp.isAfter(newStart)){
                        toBeRemoved.add(serv);
                    }
                }
                servListPast.removeAll(toBeRemoved);
                servListPast.get(servListPast.size()-1).setEnd(newStart);

                ClientDay future = textClientIndex.get(dayList.get(nightList.indexOf(selectedTextArea)+switchCode[1]));
                ServiceIntervalArrayList servListFuture= future.getDayIntervalList();
                //int iFuture = servListFuture.size()-1;
                ArrayList<ServiceInterval> toBeRemovedFuture = new ArrayList<>();
                for(ServiceInterval serv: servListFuture){
                    LocalDateTime startTemp = serv.getStart();
                    LocalDateTime endTemp= serv.getEnd();
                    if(endTemp.isBefore(newEnd)){
                        toBeRemovedFuture.add(serv);
                    }
                }
                servListFuture.removeAll(toBeRemovedFuture);
                servListFuture.get(0).setStart(newEnd);
            }







            /*
            if (newEnd> defEnd) {
                ClientDay future = textClientIndex.get(nightList.get(dayList.indexOf(selectedTextArea)+switchCode[1]));
            }
            */

    }
    public void saveIntervalChanges(ActionEvent actionEvent) throws IOException {
        selectedInterval.setNotRequired(isRequiredCheckBox.isSelected());
        selectedInterval.setComment(intervalCommentArea.getText());
        selectedInterval.setStart(setLocalDateTime(startHoursChoice.getValue(),startMinutesChoice.getValue(),textClientIndex.get(selectedTextArea).getDay()));
        selectedInterval.setEnd(setLocalDateTime(endHoursChoice.getValue(),endMinutesChoice.getValue(),textClientIndex.get(selectedTextArea).getDay()));
        jsom.saveClientRequirementsForMonth(listOfClm,settings);
    }
    public LocalDateTime setLocalDateTime(int hours, int minutes, int day){
        if(dayList.contains(selectedTextArea)){
            return LocalDateTime.of(Integer.parseInt(selectedYearValue.getText()),
                    Month.of(Integer.parseInt(selectedMonthValue.getText())), day, hours, minutes);
        }else{
            int year = Integer.parseInt(selectedYearValue.getText());
            int month = Integer.parseInt(selectedMonthValue.getText());
            Integer newYear = year;
            Integer newMonth = month;
            Integer newDay = day;

            if(day == Month.of(month).length(Year.isLeap(year))) {
                if(month == 12){
                    newYear = year+1;
                    newMonth = 1;
                }else{
                    newMonth = month +1;
                }
                System.out.println("triggered");
            }else{
                newDay++;
            }
            if(hours> 12){
                return LocalDateTime.of(Integer.valueOf(selectedYearValue.getText()),
                        Month.of(Integer.valueOf(selectedMonthValue.getText())), day, hours, minutes);
            }else{
                return LocalDateTime.of(newYear,
                        Month.of(newMonth), newDay, hours, minutes);
            }
        }



    }

    public void findNewSolution(ActionEvent actionEvent) throws IOException {
        jsom.generateNewMonthsAssistants(settings);
        Sorter sorter = new Sorter(jsom.loadAssistantInfo());

        int monthLength;
        AvailableAssistants avAs = null;
        avAs = jsom.loadAvailableAssistantInfo(settings);

        ListOfClients listOfClients = jsom.loadClientInfo(settings);

        listOfClm = new ListOfClientMonths();
        for(Client cl : listOfClients.getClientList()){
            listOfClm .getListOfClientMonths().add(cl.getClientsMonth());
        }


        monthLength = listOfClm.getListOfClientMonths().get(0).getClientDaysInMonth().size();
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
        jsom.saveClientRequirementsForMonth(listOfClm,settings);
        jsom.saveClientInfo(listOfClients.convertToListOfClientProfiles());
        populateView(getClientsOfMonth(settings));
    }
}
