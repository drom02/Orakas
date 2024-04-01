package Orakas.controllers;

import Orakas.*;
import Orakas.AssistantAvailability.AssistantAvailability;
import Orakas.Database.Database;
import Orakas.Database.DatabaseUtils;
import Orakas.Filters.AssistantMonthWorks;
import Orakas.Humans.Client;
import Orakas.Humans.Assistant;
import Orakas.MergedIntervals.MergedRegistry;
import Orakas.Structs.*;
import Orakas.Structs.TimeStructs.ClientDay;
import Orakas.Structs.TimeStructs.ServiceInterval;
import Orakas.AssistantAvailability.AvailableAssistantsLocalDateTime;
import Orakas.AssistantAvailability.DateTimeAssistantAvailability;
import Orakas.AssistantAvailability.Reporter;
import Orakas.Filters.Sorter;
import Orakas.Filters.WorkOfMonth;
import Orakas.Mediator.InternalController;
import Orakas.Structs.Availability.AvailableAssistants;
import Orakas.Structs.TimeStructs.ClientMonth;
import Orakas.Structs.TimeStructs.ServiceIntervalArrayList;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
public class MainPageController implements ControllerInterface{
    @FXML
    private Text tempAssistantText;
    @FXML
    private ChoiceBox<Assistant> assignedAssistantBox;
    @FXML
    private ChoiceBox<Location> intervalLocationBox;
    //region Graphical components
    @FXML
    private Text selectedYear;
    @FXML
    private Text selectedMonth;
    @FXML
    private  GridPane sideGrid;
    @FXML
    private Pane basePane;
 //   @FXML
  //  private ChoiceBox<Location> locationChoiceBox;
    @FXML
    private CheckBox isMergedCheckBox;
    @FXML
    private CheckBox isServiceMergedCheckBox;
    @FXML
    private  GridPane intervalDetailsGrid;
    @FXML
    private Text isRequiredDescription;
    @FXML
    private CheckBox isRequiredCheckBox;
    @FXML
    private TextArea intervalCommentArea;
    @FXML
    private ChoiceBox<Integer> startHoursChoice;
    @FXML
    private ChoiceBox<LocationRepresentative> mergedWithChoiceBox;
    @FXML
    private ChoiceBox<Integer>  startMinutesChoice;
    @FXML
    private ChoiceBox<Integer>  endHoursChoice;
    @FXML
    private ChoiceBox<Integer>  endMinutesChoice;
    @FXML
    private ScrollPane calendarScrollPane;
    @FXML
    private Label welcomeText;
    @FXML
    private DatePicker datePick;
    @FXML
    private Text selectedYearValueVisual;
    @FXML
    private Text selectedMonthValueVisual;
    @FXML
    private GridPane mainGrid;
    @FXML
    private GridPane dayInfoGrid;
    @FXML
    private GridPane barGrid;
    //endregion
    //region variables
    private Month editedMonth;
    private ArrayList<TextFlow> nightList = new ArrayList<TextFlow>();
    private ArrayList<TextFlow> dayList = new ArrayList<TextFlow>();
    private ArrayList<ArrayList<TextFlow>>  dayNightSwitch = new ArrayList<ArrayList<TextFlow>>(Arrays.asList(dayList,nightList));
    private ArrayList<TextFlow> areaList ;
    private ArrayList<TextFlow> titleList = new ArrayList<TextFlow>();
    private ArrayList<StackPane> clientCardList= new ArrayList<StackPane>();
    private Settings settings;
    private GridPane dayGrid = new GridPane();
    private Boolean isMenuVisible = false;
    private HashMap<TextFlow, ClientDay> textClientIndex = new HashMap<>();
    private HashMap<AnchorPane, ServiceInterval> paneServiceIndex = new HashMap<>();
    private HashMap<ServiceInterval,AnchorPane> servicePaneIndex= new HashMap<>();
    private HashMap<UUID, ClientProfile> clientIndex = new HashMap<>();
    private HashMap<UUID, Assistant> assistantIndex = new HashMap<>();
    private HashMap<UUID,Location> locationIndex = new HashMap<>();
    private TextFlow selectedTextArea = null;
    private ListOfClientMonths listOfClm = new ListOfClientMonths();
    private ArrayList<Integer> hoursList = new ArrayList<>();
    private ArrayList<Integer> minuteList = new ArrayList<>();
    private ArrayList<ChangeListener<Integer>> listOfObserv = new ArrayList<>();
    private ServiceInterval selectedInterval;
    private ClientMonth futureMonth = null;
    private ClientMonth  pastMonth = null;
    private GraphicalSettings GS= new GraphicalSettings(null,null);
    private int selectedYearValue;
    private int selectedMonthValue;
    private ClientDay pastDay = null;
    private ClientDay futureDay = null;
    private AnchorPane selectedAnchorPane;
    private AvailableAssistants availableAssistants;
    private DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy ");
    private InternalController internalController = new InternalController(this);
    //endregion
    public void initialize() throws IOException {
        prepareHoursAndMinutes();
        dayInfoGrid.setVisible(false);
        calendarScrollPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume(); // This blocks the event from being processed further.
            }
        });
        populateClientIndex();
        populateAssistantIndex();
        populateLocationIndex();
        isMenuVisible = false;
        settings = Settings.getSettings();
        selectedYearValueVisual.setText(String.valueOf(settings.getCurrentYear()));
        selectedMonthValueVisual.setText(String.valueOf(settings.getCurrentMonth()));
        selectedYearValue = settings.getCurrentYear();
        selectedMonthValue = settings.getCurrentMonth();
        //locationChoiceBox.setOnAction(this :: selectLocation);
        ArrayList<Location> lic = Objects.requireNonNull(Database.loadLocations()).getListOfLocations();
        lic.add(null);
       // locationChoiceBox.getItems().setAll(lic);
        populateView(getClientsOfMonth(settings));
        mainGrid.setConstraints(calendarScrollPane,mainGrid.getColumnIndex(calendarScrollPane),mainGrid.getRowIndex(calendarScrollPane),mainGrid.getColumnSpan(calendarScrollPane),mainGrid.getRowSpan(calendarScrollPane)+1);
        attachObservers();
        availableAssistants = Database.loadAssistantAvailability(settings.getCurrentYear(), settings.getCurrentMonth());
        Platform.runLater(() -> {
        GraphicalFunctions.screenResizing(basePane,mainGrid);
        });
        // barGrid.maxWidthProperty().bind(dayInfoGrid.widthProperty());
    }
    private void populateClientIndex() throws IOException {
        ListOfClientsProfiles l = Database.loadClientProfiles();
        for(ClientProfile clip : Objects.requireNonNull(l).getFullClientList()){
            clientIndex.put(clip.getID(),clip);
        }

    }
    private void populateAssistantIndex() throws IOException {
        ListOfAssistants l = Database.loadAssistants();
        for(Assistant clip : Objects.requireNonNull(l).getFullAssistantList()){
            assistantIndex.put(clip.getID(),clip);
        }
    }
    private void populateLocationIndex() throws IOException {
        ListOfLocations l = Database.loadLocations();
        for(Location clip : Objects.requireNonNull(l).getListOfLocations()){
            locationIndex.put(clip.getID(),clip);
        }
    }

    public void graphical(){
        for(TextFlow tex: titleList){
           // Text t = (Text) tex.getChildren().getFirst();
          //  tex.setStyle("-fx-border-color: " + GS.getColors().get("Night")+";");
          //  t.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            tex.getStyleClass().add("title-area-flow");
            tex.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    event.consume(); // This blocks the event from being processed further.
                }
            });
        }
        for(TextFlow tex: nightList){
            tex.getStyleClass().add("night-area-pane");
            tex.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    event.consume(); // This blocks the event from being processed further.
                }
            });
        }
        for(TextFlow tex: dayList){
            tex.getStyleClass().add("day-area-pane");
            tex.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    event.consume(); // This blocks the event from being processed further.
                }
            });
        }
        selectedYear.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-font-style: italic;");
        selectedMonth.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-font-style: italic;");
        selectedYearValueVisual.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-font-style: italic;");
        selectedMonthValueVisual.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-font-style: italic;-fx-fill: #FF0000;");
        //sideGrid.setStyle("-fx-control-inner-background:" +GS.getColors().get("SecondaryColor") +";");

    }
    public void populateView(ListOfClients LiCcl){
        dayGrid.getChildren().clear();
        listOfClm = new ListOfClientMonths();
        for(Client cl : LiCcl.getActiveClients()){
            listOfClm.getListOfClientMonths().add(Database.loadClientMonth(settings.getCurrentYear(),settings.getCurrentMonth(),cl.getID()));
        }
        //vytahnout y klientu m2s9c2
        editedMonth = Month.of(settings.getCurrentMonth());
        areaList = new ArrayList<TextFlow>();
        titleList.clear();
        dayList.clear();
        nightList.clear();
        clientCardList.clear();

        /*
        Vytvoří nadpisy pro jednotlivé dny
         */
        int i = 0;
        while (i <= editedMonth.length(Year.isLeap(settings.getCurrentYear()))){
            String inputText;
            if(i == 0){
                inputText = "Jméno Klienta";
            }else{
                LocalDate date = LocalDate.of(settings.getCurrentYear(), settings.getCurrentMonth(), i);
                 inputText = i + "."+ settings.getCurrentMonth()+ "."+settings.getCurrentYear() +
                         "\n" + getNameOfDay(date);
            }
            TextFlow newTextArea = new TextFlow();
            setTextArea(newTextArea,inputText,titleList);
            i++;
        }
        TextFlow newTextArea = new TextFlow();
        setTextArea(newTextArea,"Další měsíc",titleList);
        //newTextArea.getChildren().add(new Text("Další měsíc"));
       // TextArea newTextArea = new TextArea("Další měsíc");
       // titleList.add(newTextArea);
        i = 0;

        for (TextFlow ar : titleList){
            dayGrid.setConstraints(ar,i,0,2,1);
                i = i+2;
        }
        dayGrid.getChildren().addAll(titleList);
        /* Vytvoří text area pro jednotlivé měsíce klienta
        */
        int clienMothIter = 1;
        int clientIter = 0;
        for(ClientMonth clm : listOfClm.getListOfClientMonths()){
            listOfClm.getListOfClientMonths().get(0);
            i = 0;
            for (int dayIter = -1; dayIter < clm.getClientDaysInMonth().size(); dayIter++){
                String inputText;
                TextFlow dayTextAr = new TextFlow();
                if(i==0){
                    TextFlow dayTextArT = new TextFlow();
                    inputText = LiCcl.getActiveClients().get(clientIter).getName() + " " + LiCcl.getActiveClients().get(clientIter).getSurname();
                    StackPane stack = setTextAreaCard(dayTextArT,inputText,clientCardList);
                    dayTextArT.getStyleClass().add("day-title-card");
                    stack.getStyleClass().add("day-title-card-stack");
                    dayGrid.setConstraints(stack,i,clienMothIter,2,1);
                    i = i+2;
                }else{
                    if(getAssistantOfDay(clm.getClientDaysInMonth().get(dayIter)) == null){
                        //inputText= "Day" + i/2 +"\n" + getAssistantOfDay(clm.getClientDaysInMonth().get(dayIter));
                        inputText= "Den " + i/2 +"\n" + "none" ;
                    }else{
                        inputText= "Den " + i/2 + "\n" + getAssistantOfDay(clm.getClientDaysInMonth().get(dayIter)).getName() +" "+getAssistantOfDay(clm.getClientDaysInMonth().get(dayIter)).getSurname();
                    }
                    setTextArea(dayTextAr,inputText,false,dayList,clm.getClientDaysInMonth().get(dayIter));
                    dayGrid.setConstraints(dayTextAr,i,clienMothIter,1,1);
                    i = i+2;
                }
            }
            i=0;
            for (int dayIter = 0; dayIter < clm.getClientNightsInMonth().size(); dayIter++){
                String inputText;
                TextFlow nightTextAr = new TextFlow();
                if(i==0){
                    TextFlow nightTextArT = new TextFlow();
                    inputText = LiCcl.getClientList().get(clientIter).getName() + " " + LiCcl.getClientList().get(clientIter).getSurname();
                    StackPane stack = setTextAreaCard(nightTextArT,inputText,clientCardList);
                    nightTextArT.getStyleClass().add("night-title-card");
                    stack.getStyleClass().add("night-title-card-stack");
                    dayGrid.setConstraints(stack,i,clienMothIter+1,2,1);
                    TextFlow ar2 = new TextFlow();
                    if(getAssistantOfDay(clm.getClientNightsInMonth().get(dayIter)) == null){
                        inputText= "Night" + 1 +"\n" + "none" ;
                    }else{
                        inputText= "Night" + 1 + "\n" + getAssistantOfDay(clm.getClientNightsInMonth().get(dayIter)).getName() +" "+getAssistantOfDay(clm.getClientNightsInMonth().get(dayIter)).getSurname();
                    }
                    setTextArea(ar2,inputText,false,nightList,clm.getClientNightsInMonth().get(dayIter));
                    ar2.setPrefSize(375,100);
                    //grid.setMargin(ar2,new Insets(0, 0, 0, 125));
                    dayGrid.setConstraints(ar2,i+2,clienMothIter+1,3,1);

                    i = i+4;
                }else{
                    if(getAssistantOfDay(clm.getClientNightsInMonth().get(dayIter)) == null){
                        inputText= "Night" + i/2 +"\n" + "none" ;
                    }else{
                        inputText= "Night" + i/2 + "\n" + getAssistantOfDay(clm.getClientNightsInMonth().get(dayIter)).getName() +" "+getAssistantOfDay(clm.getClientNightsInMonth().get(dayIter)).getSurname();
                    }
                    //inputText= "Night" + i/2;
                    setTextArea(nightTextAr,inputText,false,nightList,clm.getClientNightsInMonth().get(dayIter));
                    if(i != clm.getClientNightsInMonth().size()){
                        dayGrid.setMargin(nightTextAr,new Insets(0, 125, 0, 125));
                    }else{
                        dayGrid.setMargin(nightTextAr,new Insets(0, 125, 0, 125));
                    }

                    dayGrid.setConstraints(nightTextAr,i,clienMothIter+1,3,1);
                    i = i+2;
                }
            }
            clienMothIter = clienMothIter+2;
            clientIter++;
        }
        dayGrid.getChildren().addAll(clientCardList);
        dayGrid.getChildren().addAll(areaList);
        calendarScrollPane.setContent(dayGrid);
       // graphicSetup();
        graphical();
    }
    private String getNameOfDay(LocalDate date){
        DayOfWeek dayValue = date.getDayOfWeek();
        String nameOfDay = dayValue.getDisplayName(TextStyle.FULL, new Locale("cs", "CZ"));
        if(nameOfDay !=null){
            return nameOfDay.substring(0,1).toUpperCase() +nameOfDay.substring(1);
        }
        return null;
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
            ListOfClients out =   Database.loadFullClients(selectedYearValue,selectedMonthValue);
            for(Client cl: out.getActiveClients()){
                listOfClm.getListOfClientMonths().add(Database.loadClientMonth(settings.getCurrentYear(),settings.getCurrentMonth(),cl.getID()));
            }
            return  out;
        }catch(Exception e){
            ListOfClientsProfiles lOCP = Database.loadClientProfiles();
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
                listOfClients.getActiveClients().add(out);
            }
            Database.saveAllClientMonths(listOfClm);
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
    private Assistant getAssistantFromID(ArrayList<Assistant> asList, UUID id){
        for(Assistant as : asList){
            if(as.getID().equals(id)){
                return as;
            }
        }
        return null;
    }
    private void forListOfCLmTrim(ListOfClientMonths cl,AvailableAssistantsLocalDateTime avs){
        for(ClientMonth clm : cl.getListOfClientMonths()){
            for(ClientDay cld : clm.getClientDaysInMonth()){
                for(ServiceInterval serv : cld.getDayIntervalListUsefull()){
                    trimAvailableAssistants(cld,serv,avs);
                }

            }
            for(ClientDay cld : clm.getClientNightsInMonth()){
                for(ServiceInterval serv : cld.getDayIntervalListUsefull()){
                    trimAvailableAssistants(cld,serv,avs);
                }
            }
        }
    }
    public MergedRegistry prepareMergeable(ListOfClientMonths listOfClm){
        MergedRegistry mergedRegistry = new MergedRegistry();
        for(ClientMonth clm : listOfClm.getListOfClientMonths()){
            for(ClientDay cl : clm.getClientDaysInMonth()){
                for(ServiceInterval serv: cl.getDayIntervalListUsefull()){
                    if(serv.isMerged()){
                        mergedRegistry.addIntervalToMergedRecord(cl.getDay(),serv,serv.getLocation(),cl.getDayStatus());
                    }
                }
            }
            for(ClientDay cl : clm.getClientNightsInMonth()){
                for(ServiceInterval serv: cl.getDayIntervalListUsefull()){
                    if(serv.isMerged()){
                        mergedRegistry.addIntervalToMergedRecord(cl.getDay(),serv,serv.getLocation(),cl.getDayStatus());
                    }
                }
            }
        }
        return mergedRegistry;
    }
    public void findSolutionV3(ActionEvent actionEvent) throws IOException {
        clearTable();
        ListOfAssistants asL = Database.loadAssistants();

        int monthLength;
        AvailableAssistantsLocalDateTime avAs = null;
        AvailableAssistants test = Database.loadAssistantAvailability(selectedYearValue,selectedMonthValue);
        //Edit avAS to reflect presets
        avAs = Database.loadAssistantAvailability(selectedYearValue,selectedMonthValue).convertToDateTimeAvailability(settings.getCurrentYear(), settings.getCurrentMonth());

        ListOfClients listOfClients = Database.loadFullClients(selectedYearValue,selectedMonthValue);

        listOfClm = new ListOfClientMonths();
        for(Client cl : listOfClients.getActiveClients()){
            listOfClm .getListOfClientMonths().add(cl.getClientsMonth());
        }
        forListOfCLmTrim(listOfClm,avAs);
        monthLength = listOfClm.getListOfClientMonths().get(0).getClientDaysInMonth().size();
        MergedRegistry mergedRegistry = prepareMergeable(listOfClm);
        Sorter sorter = new Sorter(asL,settings.getCurrentYear(), settings.getCurrentMonth(),mergedRegistry);
        for (int dayIter = 0; dayIter < monthLength; dayIter++) {
            for( Client cl : listOfClients.getActiveClients()) {

                    ClientDay clDay = cl.getClientsMonth().getClientDaysInMonth().get(dayIter);
                    ArrayList<DateTimeAssistantAvailability> listOfAvailableAtDay = getDateTimeAvailableAssistantForDay(avAs,dayIter,true);
                    UUID dayPicked = sorter.sortDate(listOfAvailableAtDay,dayIter,0,clDay,asL);
                }

                //System.out.println(sorter.getIdFromList(listOfAvailableAtDay));

            for( Client cl : listOfClients.getActiveClients()) {
                ClientDay clNight = cl.getClientsMonth().getClientNightsInMonth().get(dayIter);
                ArrayList<DateTimeAssistantAvailability> listOfAvailableAtNight = getDateTimeAvailableAssistantForDay(avAs,dayIter,false);
                UUID nightPicked = sorter.sortDate(listOfAvailableAtNight,dayIter,1,clNight,asL);
            }

        }
        AssistantMonthWorks w =sorter.getWorkMonth();
        Reporter rep = new Reporter(w);
        Database.saveAllClientMonths(listOfClm);
        populateView(getClientsOfMonth(settings));
        CompletableFuture<Void> future = CompletableFuture.runAsync(()-> { for(ClientProfile clip :listOfClients.convertToListOfClientProfiles().getFullClientList()){
            Database.saveClientProfile(clip);
        }});
        sorter.report();
        Database.saveMonthWorkResult(settings.getCurrentYear(),settings.getCurrentMonth(),remainingWork(Reporter.totalWorked(w),sorter.getWorkHoursOfMonth()));
    }
    private WorkOfMonth remainingWork(HashMap<UUID, Double> doneWork, HashMap<UUID, Double> workSupposedToBeDone){
        WorkOfMonth output = new WorkOfMonth(new HashMap<UUID,Double>());
        for(UUID id : doneWork.keySet()){
          Double o =   workSupposedToBeDone.get(id)-doneWork.get(id);
          output.registerResult(id,o);
        }
        return output;
    }

    private ArrayList<DateTimeAssistantAvailability> getDateTimeAvailableAssistantForDay(AvailableAssistantsLocalDateTime lisA, int date, boolean day ){
        ArrayList<DateTimeAssistantAvailability> output;
        if(day==true){
            output = lisA.getLocalDateTimeDay().get(date);
        }else{
            output = lisA.getLocalDateTimeNight().get(date);
        }
        return output;

    }
    private ArrayList<AssistantAvailability> getAvailableAssistantForDay(AvailableAssistants lisA, int date, boolean day ){
        ArrayList<AssistantAvailability> output;
        if(day==true){
            output = lisA.getAvailableAssistantsAtDays().get(date);
        }else{
            output = lisA.getAvailableAssistantsAtNights().get(date);
        }
        return output;

    }
    public void saveChangedDate() throws IOException {
        String[] parts = datePick.getValue().toString().split("-");
        selectedYearValueVisual.setText(parts[0]);
        selectedMonthValueVisual.setText(parts[1]);
        selectedYearValue = Integer.parseInt(parts[0]);
        selectedMonthValue= Integer.parseInt(parts[1]);
        settings.setCurrentYear(selectedYearValue);
        settings.setCurrentMonth(selectedMonthValue);
        Database.saveSettings(settings);
        populateView(getClientsOfMonth(settings));
        internalController.send("Assistant");
        }
    private void setTextArea(TextFlow textArea, String inputText, Boolean isDescrip, ArrayList<TextFlow> arList){
        textArea.getChildren().add(new Text(inputText));
        textArea.setPrefSize(250,100);
        arList.add(textArea);
        areaList.add(textArea);
        if(!isDescrip){
            textArea.setOnMouseClicked(this :: displayDayInfoFull);
        }
    }
    private void setTextArea(TextFlow textArea, String inputText, ArrayList<TextFlow> arList){
        textArea.setTextAlignment(TextAlignment.CENTER);
        textArea.getChildren().add(new Text(inputText));
        textArea.setPrefSize(250,50);
        arList.add(textArea);
    }
    private void setTextArea(TextFlow textArea, String inputText, Boolean isDescrip, ArrayList arList, ClientDay day){
        setTextArea(textArea,inputText,isDescrip,arList);
        textClientIndex.put(textArea,day);
    }
    private StackPane setTextAreaCard(TextFlow textArea, String inputText, ArrayList arList){
        textArea.setTextAlignment(TextAlignment.CENTER);
        textArea.getChildren().add(new Text(inputText));
        textArea.setPrefSize(250,25);
        textArea.setMaxSize(250,25);
        StackPane stackPane = new StackPane();
        arList.add(stackPane);
        stackPane.setPrefSize(250,100);
        stackPane.getChildren().add(textArea);
        StackPane.setAlignment(textArea, Pos.CENTER_RIGHT);
        //areaList.add(textArea);
    return stackPane;
    }
    public void displayDayInfoFull(MouseEvent mouseEvent) {
        if(selectedTextArea==null){
            mainGrid.setConstraints(calendarScrollPane,mainGrid.getColumnIndex(calendarScrollPane),mainGrid.getRowIndex(calendarScrollPane),mainGrid.getColumnSpan(calendarScrollPane),mainGrid.getRowSpan(calendarScrollPane)-1);
            dayInfoGrid.setVisible(true);
            isMenuVisible=true;
            selectedTextArea = (TextFlow) mouseEvent.getSource();
            pastDay = null;
            futureDay = null;
            loadDayData(selectedTextArea);
            selectedTextArea.getStyleClass().add("selected-pane");
        } else if (selectedTextArea==mouseEvent.getSource()) {
                mainGrid.setConstraints(calendarScrollPane,mainGrid.getColumnIndex(calendarScrollPane),mainGrid.getRowIndex(calendarScrollPane),mainGrid.getColumnSpan(calendarScrollPane),mainGrid.getRowSpan(calendarScrollPane)+1);
                dayInfoGrid.setVisible(false);
                isMenuVisible=false;
                selectedTextArea.getStyleClass().remove("selected-pane");
                selectedTextArea =null;
                pastDay = null;
                futureDay = null;
            }else{
            //selectedTextArea.setStyle("-fx-border-color: null");
            selectedTextArea.getStyleClass().remove("selected-pane");
            selectedTextArea =(TextFlow) mouseEvent.getSource();
            selectedTextArea.getStyleClass().add("selected-pane");
            pastDay = null;
            futureDay = null;
            loadDayData(selectedTextArea);
        }
        }
    public void isMergedSwitch(ActionEvent actionEvent) {
        CheckBox check = (CheckBox) actionEvent.getSource();
        check.isSelected();
        if(!(mergedWithChoiceBox.getValue() == null)){
            textClientIndex.get(selectedTextArea).setMerged(check.isSelected());
                ClientMonth clim = listOfClm.getMonthOfSpecificClient(mergedWithChoiceBox.getValue().getClp().getID());
                ArrayList<ClientDay> modified = new ArrayList<>();
                if(dayList.contains(selectedTextArea)){
                    modified = clim.getClientDaysInMonth();
                }else{
                    modified = clim.getClientNightsInMonth();
                }
            modified.get(mergedWithChoiceBox.getValue().getClDay().getDay()-1).setMerged(check.isSelected());
        }
    }
    private void loadDayData(TextFlow sourceTex)  {
        TextArea outputText = new TextArea();
        outputText.getStyleClass().add("day-info-text");
        ClientDay day = textClientIndex.get(sourceTex);
        //.getDisplayName(TextStyle.FULL, new Locale("cs", "CZ")
        outputText.setText(prepareDayInfo(day));
        setIntervalBars(day);
        dayInfoGrid.setConstraints(outputText,0,0,dayInfoGrid.getColumnCount()-3,dayInfoGrid.getRowCount()-2);
        dayInfoGrid.getChildren().add(outputText);
        displayIntervalInfoDef(day);
        //locationChoiceBox.setValue(day.getLocation());
        prepareMergeable();
    }
    private String prepareDayInfo(ClientDay day){
        StringBuilder output = new StringBuilder();
        output.append("Datum : " + day.getDay() +"."+ day.getMonth().getValue()+"."+day.getYear() + "\n");
        String dayName =LocalDate.of(day.getYear(),day.getMonth().getValue(),day.getDay()).getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("cs", "CZ"));
        dayName =  dayName.substring(0, 1).toUpperCase() + dayName.substring(1);
        output.append( "Den v týdnu : " + dayName+ "\n");
        int i = 1;
        for(ServiceInterval serv :day.getDayIntervalList()){
            output.append("Směna " + i + " začíná v : " + serv.getStart().format(customFormatter) + " a končí v : " + serv.getEnd().format(customFormatter)
                    + ", zodpovědný asistent : ");
            output.append(((serv.getOverseeingAssistant() == null) ? (serv.getIsNotRequired() == false) ?  "Nikdo vhodný nebyl nalezen": "Asistent není potřebný" : serv.getOverseeingAssistant().getName() + " " + serv.getOverseeingAssistant().getSurname())+ "\n");
            if(serv.getComment().length()>1){
                output.append("Komentář k směně : " + "\n");
                output.append(serv.getComment()+ "\n");
            }
            if(clientIndex.get(day.getClient()).getComment().length()>1){
                output.append("Poznámky ke klientovi : " + "\n");
                output.append(clientIndex.get(day.getClient()).getComment()+ "\n");
            }
            output.append("--------------------------------------------------------------------------------------------"+"\n");
            i++;
        }

        return  output.toString();
    }
    public void selectLocation(ActionEvent actionEvent){
       // textClientIndex.get(selectedTextArea).setLocation(locationChoiceBox.getValue());
        prepareMergeable();
    }
    private ArrayList<Double> calculateBarWidth(ClientDay day){
      LocalDateTime  start = day.getDayIntervalList().getFirst().getStart();
        LocalDateTime  end = day.getDayIntervalList().getLast().getEnd();
        double multiplier = 100 - ((day.getDayIntervalList().size()-1)*0.5);
        double minutes = ChronoUnit.MINUTES.between(start,end);
        ArrayList<Double> sizes = new ArrayList<>();
        for(ServiceInterval serv : day.getDayIntervalList()){
            double minutesT = ChronoUnit.MINUTES.between(serv.getStart(),serv.getEnd());
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
        barGrid.getChildren().clear();
        barGrid.getRowConstraints().add(new RowConstraints() {{ setPercentHeight(100); }});
        //barGrid.getRowConstraints().add(new RowConstraints() {{ setPercentHeight(30); }});
        int i = day.getDayIntervalList().size();
        int curInt = 1;
        int sizesIndex = 0;
        for(ServiceInterval serv : day.getDayIntervalList()){
            AnchorPane bar = new AnchorPane();
            paneServiceIndex.put(bar,serv);
            servicePaneIndex.put(serv,bar);
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
               // bar2.setMinSize(10, 10);
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
        dayInfoGrid.getColumnConstraints();
        barGrid.getChildren().addAll(listToAdd);
       // System.out.println("done");


    }
    private void trimAvailableAssistants(ClientDay cl, ServiceInterval serv, AvailableAssistantsLocalDateTime avAs){
        if(serv.getAssignedAssistant() != null){
            UUID assigned = serv.getAssignedAssistant();
            if(cl.getDayStatus()){
               ArrayList<DateTimeAssistantAvailability>  tday = avAs.getLocalDateTimeDay().get(cl.getDay());
                ArrayList<DateTimeAssistantAvailability>  tnight = avAs.getLocalDateTimeNight().get(cl.getDay());
               ArrayList<DateTimeAssistantAvailability> toBeRemoved = new ArrayList<>();
               for(DateTimeAssistantAvailability tem : tday){
                   if(tem.getAssistantAvailability().getAssistant().equals(assigned)){
                       toBeRemoved.add(tem);
                   }
               }
               //Modify to not completely remove the intervals but instead resize them to fit the 8 hours limit between shifts
              // tday.removeAll(toBeRemoved);
                toBeRemoved = new ArrayList<>();
                for(DateTimeAssistantAvailability tem : tnight){
                    if(tem.getAssistantAvailability().getAssistant().equals(assigned)){
                        toBeRemoved.add(tem);
                    }
                }
                tnight.removeAll(toBeRemoved);
                toBeRemoved = new ArrayList<>();
              if(avAs.getLocalDateTimeNight().get(cl.getDay()-2) != null){
                  ArrayList<DateTimeAssistantAvailability>  tnight2 = avAs.getLocalDateTimeNight().get(cl.getDay()-2);
                  for(DateTimeAssistantAvailability tem : tnight2){
                      if(tem.getAssistantAvailability().getAssistant().equals(assigned)){
                          toBeRemoved.add(tem);
                      }
                  }
                  tnight2.removeAll(toBeRemoved);
              }
            }else{
                ArrayList<DateTimeAssistantAvailability>  tday = avAs.getLocalDateTimeDay().get(cl.getDay());
                ArrayList<DateTimeAssistantAvailability>  tnight = avAs.getLocalDateTimeNight().get(cl.getDay());
                ArrayList<DateTimeAssistantAvailability> toBeRemoved = new ArrayList<>();
                for(DateTimeAssistantAvailability tem : tday){
                    if(tem.getAssistantAvailability().getAssistant().equals(assigned)){
                        toBeRemoved.add(tem);
                    }
                }
                //Modify to not completely remove the intervals but instead resize them to fit the 8 hours limit between shifts
                // tday.removeAll(toBeRemoved);
                toBeRemoved = new ArrayList<>();
                for(DateTimeAssistantAvailability tem : tnight){
                    if(tem.getAssistantAvailability().getAssistant().equals(assigned)){
                        toBeRemoved.add(tem);
                    }
                }
                tnight.removeAll(toBeRemoved);
                toBeRemoved = new ArrayList<>();
                if(avAs.getLocalDateTimeDay().get(cl.getDay()+1) != null){
                    ArrayList<DateTimeAssistantAvailability>  tnight2 = avAs.getLocalDateTimeNight().get(cl.getDay()+1);
                    for(DateTimeAssistantAvailability tem : tnight2){
                        if(tem.getAssistantAvailability().getAssistant().equals(assigned)){
                            toBeRemoved.add(tem);
                        }
                    }
                    tnight2.removeAll(toBeRemoved);
                }
            }
        }
    }
    private void displayIntervalInfo(MouseEvent mouseEvent) {
       ServiceInterval serv = paneServiceIndex.get(mouseEvent.getSource());
       displayIntervalInfoAlg(serv);

    }
    private void setupAssignedAssistant(ServiceInterval serv) throws IOException {
        AvailableAssistantsLocalDateTime av = availableAssistants.convertToDateTimeAvailability(settings.getCurrentYear(), settings.getCurrentMonth());
        ClientDay cl = textClientIndex.get(selectedTextArea);
        ArrayList<ArrayList<DateTimeAssistantAvailability>> editedList;
        ArrayList<Assistant> output = new ArrayList<>();
        if(cl.getDayStatus()){
            editedList = av.getLocalDateTimeDay();
        }else{
            editedList = av.getLocalDateTimeNight();
        }
        ArrayList<DateTimeAssistantAvailability> day = editedList.get(cl.getDay()-1);
        for(DateTimeAssistantAvailability date : day){
            if(serv.getStart().isAfter(date.getStart())||serv.getStart().isEqual(date.getStart()) && serv.getEnd().isBefore(date.getEnd())||serv.getEnd().isEqual(date.getEnd()) ){
                output.add(assistantIndex.get(date.getAssistantAvailability().getAssistant()));
            }

        }
        if(!output.contains(assistantIndex.get(serv.getAssignedAssistant()))){
            serv.setAssignedAssistant(null);
           // saveIntervalAlg();
        }
        output.add(null);
        ObservableList<Assistant> observ = FXCollections.observableList(output);
        assignedAssistantBox.setItems(observ);
        assignedAssistantBox.setValue(assistantIndex.get(serv.getAssignedAssistant()));

    }
    private void displayIntervalInfoAlg(ServiceInterval serv){
        selectedAnchorPane.getStyleClass().remove("selected-interval");
        selectedAnchorPane = servicePaneIndex.get(serv);
        selectedInterval = serv;
        isMergedCheckBox.setSelected(serv.isMerged());
        intervalCommentArea.setText(serv.getComment());
        isRequiredCheckBox.setSelected(serv.getIsNotRequired());
        assignedAssistantBox.setValue(assistantIndex.get(serv.getAssignedAssistant()));
        isServiceMergedCheckBox.setSelected(serv.isMerged());
        selectedAnchorPane.getStyleClass().add("selected-interval");
        intervalLocationBox.setValue(locationIndex.get(serv.getLocation()));
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
        try {
            setupAssignedAssistant(serv);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(selectedInterval.getOverseeingAssistant() != null){
            tempAssistantText.setText( selectedInterval.getOverseeingAssistant().getName()+" " + selectedInterval.getOverseeingAssistant().getSurname());
        }else{
            tempAssistantText.setText("none");
        }
    }
    private void displayIntervalInfoDef(ClientDay day) {
        if(!day.getDayIntervalList().isEmpty()) {
            ServiceInterval serv = day.getDayIntervalList().get(0);
            selectedInterval = serv;
            intervalCommentArea.setText(serv.getComment());
            selectedAnchorPane = servicePaneIndex.get(serv);
            selectedAnchorPane.getStyleClass().add("selected-interval");
            displayIntervalInfoAlg(serv);
        }

    }
    private void setIntervalDescription(AnchorPane anch, ServiceInterval serv){
      //  Text start = new Text(serv.getStart().getHour() +":" + serv.getStart().getMinute());
       // Text end = new Text(serv.getEnd().getHour() +":" + serv.getEnd().getMinute());
        Text start = new Text(serv.getStart().toString());
        Text end = new Text(serv.getEnd().toString());
        anch.setTopAnchor(start,1.0);
        anch.setLeftAnchor(start,1.0);
        anch.setTopAnchor(end,1.0);
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
       List<Assistant> out = assistants.stream().distinct().toList();
        return out.stream()
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
        ClientDay editedDay = textClientIndex.get(selectedTextArea);
        if(editedDay!=null){
            if(editedDay.getDayIntervalList().size()>1){
                if(selectedInterval.equals(editedDay.getDayIntervalList().getFirst())){
                    editedDay.getDayIntervalList().remove(selectedInterval);
                    editedDay.getDayIntervalList().getFirst().setStart(selectedInterval.getStart());
                    selectedInterval = editedDay.getDayIntervalList().getFirst();
                    selectedAnchorPane = servicePaneIndex.get(selectedInterval);
                    selectedAnchorPane.getStyleClass().add("selected-interval");
                } else if (selectedInterval.equals(editedDay.getDayIntervalList().getLast())) {
                    editedDay.getDayIntervalList().remove(selectedInterval);
                    editedDay.getDayIntervalList().getLast().setEnd(selectedInterval.getEnd());
                    selectedInterval = editedDay.getDayIntervalList().getLast();
                    selectedAnchorPane = servicePaneIndex.get(selectedInterval);
                    selectedAnchorPane.getStyleClass().add("selected-interval");
                }else{
                    editedDay.getDayIntervalList().remove(selectedInterval);
                    correctInterval(editedDay.getDayIntervalList());
                }
            }else{
                selectedInterval.setNotRequired(true);
                //selectedInterval.setOverseeingAssistant(null);
                selectedInterval.setLocation(null);
                selectedInterval.setComment("Klient v tomto období nevyžaduje asistenta.");
                intervalCommentArea.setText("Klient v tomto období nevyžaduje asistenta.");
            }
            setIntervalBars(editedDay);
            Database.saveAllClientMonths(listOfClm);
            displayIntervalInfoDef(editedDay);
        }

    }
    public void prepareHoursAndMinutes(){
        hoursList = (ArrayList<Integer>) IntStream.rangeClosed(0, 24).boxed().collect(Collectors.toList());
        minuteList = (ArrayList<Integer>) IntStream.rangeClosed(0, 60).boxed().collect(Collectors.toList());
    }
    public void createNewInterval3(ActionEvent actionEvent) throws IOException {
       // System.out.println("toBeReady");
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
                    (endNew.isAfter(start)||endNew.isEqual(start)) && (endNew.isBefore(end)||endNew.isEqual(end)) ){
                toBeResized.add(s);
              //  System.out.println("toBeResized");
            }
            if(start.isAfter(startNew)&& end.isBefore(endNew) ){
             //   System.out.println("toBeRemoved");
                toBeRemoved.add(s);
            }
            if(start.isEqual(startNew)&&end.isEqual(endNew)){
                alreadyExists=true;
             //   System.out.println("Already exists");
            }
        }
        for(ServiceInterval serv : toBeRemoved){
            Assistant as = serv.getOverseeingAssistant();
            day.getDayIntervalList().remove(serv);

            if(day.getDayIntervalList().isEmpty()){
                day.getDayIntervalList().add(new ServiceInterval(setLocalDateTime(startHoursChoice.getValue(), startMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay())
                        , setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()), as, null,null, false, false,day.getLocation().getID(),false));
                if(day.getDayIntervalList().getFirst().getStart().isEqual(startNew) || day.getDayIntervalList().getLast().getEnd().isEqual(endNew)){
                    intervalOverreach(startNew, endNew);
                    System.out.println("Is empty");
                }
                Database.saveAllClientMonths(listOfClm);
                setIntervalBars(day);
                return;
            }
        }
        if(!alreadyExists) {
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
                                , temp, s.getOverseeingAssistant(), null,null, true,false,day.getLocation().getID(),false));
                        System.out.println("Type 3");
                        break;
                    }

                } else {
                    System.out.println("nothing");
                    break;// paneServiceIndex.get(selectedTextArea).getDayIntervalList().add(new ServiceInterval());
                }
            }
            day.getDayIntervalList().add(new ServiceInterval(setLocalDateTime(startHoursChoice.getValue(), startMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay())
                    , setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()), day.getDayIntervalList().get(0).getOverseeingAssistant(), null,null, false, false,day.getLocation().getID(),false));
            if(day.getDayIntervalList().getFirst().getStart().isEqual(startNew) || day.getDayIntervalList().getLast().getEnd().isEqual(endNew)){
                intervalOverreach(startNew, endNew);
            }
            Database.saveAllClientMonths(listOfClm);
            day.getDayIntervalListUsefull().get(0).setComment("Testing save logic");
            setIntervalBars(day);
            System.out.println("done");
        }
    }
    public void createNewIntervalAlg() throws IOException {
        ClientDay day = textClientIndex.get(selectedTextArea);
        LocalDateTime startNew = setLocalDateTime(startHoursChoice.getValue(),startMinutesChoice.getValue(),textClientIndex.get(selectedTextArea).getDay());
        LocalDateTime endNew = setLocalDateTime(endHoursChoice.getValue(), + endMinutesChoice.getValue(),textClientIndex.get(selectedTextArea).getDay());
        ArrayList<ServiceInterval> toBeRemoved = new ArrayList<>();
        ArrayList<ServiceInterval> toBeResized = new ArrayList<>();
        for(ServiceInterval s : day.getDayIntervalList()){
            LocalDateTime start = s.getStart();
            LocalDateTime end = s.getEnd();
            if(start.isEqual(startNew)&&end.isEqual(endNew)){
                return;
            }
            if((startNew.isAfter(start) ||startNew.isEqual(start)) && (startNew.isBefore(end) ||startNew.isEqual(end)) ||
                    (endNew.isAfter(start)||endNew.isEqual(start)) && (endNew.isBefore(end)||endNew.isEqual(end)) ){
                toBeResized.add(s);
            }
            if((start.isAfter(startNew)&& end.isBefore(endNew)) || start.isEqual(startNew) && end.isBefore(endNew) || start.isAfter(startNew ) && end.isEqual(endNew) ){
                //   System.out.println("toBeRemoved");
                toBeRemoved.add(s);
            }
        }
        for(ServiceInterval serv : toBeRemoved){
            Assistant as = serv.getOverseeingAssistant();
            day.getDayIntervalList().remove(serv);
            if(day.getDayIntervalList().isEmpty()){
                day.getDayIntervalList().add(new ServiceInterval(setLocalDateTime(startHoursChoice.getValue(), startMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay())
                        , setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()), as, null,null, false, false,day.getLocation().getID(),false));
                if(day.getDayIntervalList().getFirst().getStart().isEqual(startNew) || day.getDayIntervalList().getLast().getEnd().isEqual(endNew)){
                    intervalOverreach(startNew, endNew);
                    System.out.println("Is empty");
                }
                Database.saveAllClientMonths(listOfClm);
                setIntervalBars(day);

                return;
            }
        }
        for (ServiceInterval s : toBeResized) {
            LocalDateTime start = s.getStart();
            LocalDateTime end = s.getEnd();
            if ((startNew.isAfter(start) && startNew.isBefore(end)) & (endNew.isAfter(end) || endNew.isEqual(end))) {
                s.setEnd(setLocalDateTime(startHoursChoice.getValue(), startMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()));
                System.out.println("Type 1");
            } else if ((startNew.isBefore(start) || startNew.isEqual(start)) & (endNew.isAfter(start) || endNew.isEqual(start) && endNew.isBefore(end) || endNew.isEqual(end))) {
                s.setStart(setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()));
                System.out.println("Type 2");
            } else if(start.isBefore(startNew) && end.isAfter(endNew)) {
                LocalDateTime temp = s.getEnd();
                s.setEnd(setLocalDateTime(startHoursChoice.getValue(), startMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()));
                day.getDayIntervalList().add(new ServiceInterval(setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay())
                        , temp, s.getOverseeingAssistant(), null,null, false,false,day.getLocation().getID(),false));
                System.out.println("Type 3");
                break;
            }else{
                System.out.println("Type Error");
                System.out.println("start " + start);
                System.out.println("end " + end);
                System.out.println("newStart " + startNew);
                System.out.println("newEnd " + endNew);
            }
        }
        day.getDayIntervalList().add(new ServiceInterval(setLocalDateTime(startHoursChoice.getValue(), startMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay())
                , setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()), day.getDayIntervalList().getFirst().getOverseeingAssistant(), null,null, false, false,day.getLocation().getID(),false));
        if(day.getDayIntervalList().getFirst().getStart().isEqual(startNew) || day.getDayIntervalList().getLast().getEnd().isEqual(endNew)){
            intervalOverreach(startNew, endNew);
        }
        Database.saveAllClientMonths(listOfClm);
        setIntervalBars(day);
        displayIntervalInfoDef(day);
    }
    public void saveIntervalAlg() throws IOException {
        ClientDay day = textClientIndex.get(selectedTextArea);
        day.getDayIntervalList().remove(selectedInterval);
        LocalDateTime startNew = setLocalDateTime(startHoursChoice.getValue(),startMinutesChoice.getValue(),textClientIndex.get(selectedTextArea).getDay());
        LocalDateTime endNew = setLocalDateTime(endHoursChoice.getValue(), + endMinutesChoice.getValue(),textClientIndex.get(selectedTextArea).getDay());
        selectedInterval.setAssignedAssistant((assignedAssistantBox.getValue()!= null)? assignedAssistantBox.getValue().getID(): null);
        selectedInterval.setStart(startNew);
        selectedInterval.setEnd(endNew);
        selectedInterval.setNotRequired(isRequiredCheckBox.isSelected());
        selectedInterval.setComment(intervalCommentArea.getText());
        selectedInterval.setOverseeingAssistant(null);
        ArrayList<ServiceInterval> toBeRemoved = new ArrayList<>();
        ArrayList<ServiceInterval> toBeResized = new ArrayList<>();
        for(ServiceInterval s : day.getDayIntervalList()){
            LocalDateTime start = s.getStart();
            LocalDateTime end = s.getEnd();
            if(start.isEqual(startNew)&&end.isEqual(endNew)){
                return;
            }
            //if((startNew.isAfter(start) ||startNew.isEqual(start)) && (startNew.isBefore(end) ||startNew.isEqual(end)) ||
            if((startNew.isAfter(start) ||startNew.isEqual(start)) && (startNew.isBefore(end)) ||
                    (endNew.isAfter(start)||endNew.isEqual(start)) && (endNew.isBefore(end)||endNew.isEqual(end)) ){
                toBeResized.add(s);
            }
            if((start.isAfter(startNew)&& end.isBefore(endNew)) || start.isEqual(startNew) && end.isBefore(endNew) || start.isAfter(startNew ) && end.isEqual(endNew) ){
                //   System.out.println("toBeRemoved");
                toBeRemoved.add(s);
            }
        }
        for(ServiceInterval serv : toBeRemoved){
            Assistant as = serv.getOverseeingAssistant();
            day.getDayIntervalList().remove(serv);
            if(day.getDayIntervalList().isEmpty()){
                day.getDayIntervalList().add(selectedInterval);
                if(day.getDayIntervalList().getFirst().getStart().isEqual(startNew) || day.getDayIntervalList().getLast().getEnd().isEqual(endNew)){
                    intervalOverreach(startNew, endNew);
                }
                Database.saveAllClientMonths(listOfClm);
                saveChangedDays();
                setIntervalBars(day);
                return;
            }
        }
        for (ServiceInterval s : toBeResized) {
            LocalDateTime start = s.getStart();
            LocalDateTime end = s.getEnd();
            if ((startNew.isAfter(start) && startNew.isBefore(end)) & (endNew.isAfter(end) || endNew.isEqual(end))) {
                s.setEnd(setLocalDateTime(startHoursChoice.getValue(), startMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()));
                System.out.println("Type 1");
            } else if ((startNew.isBefore(start) || startNew.isEqual(start)) & (endNew.isAfter(start) || endNew.isEqual(start) && endNew.isBefore(end) || endNew.isEqual(end))) {
                s.setStart(setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()));
                System.out.println("Type 2");
            }else{
                System.out.println("Type Error");
                System.out.println("start " + start);
                System.out.println("end " + end);
                System.out.println("newStart " + startNew);
                System.out.println("newEnd " + endNew);
            }
        }
        day.getDayIntervalList().add(selectedInterval);
        if(day.getDayIntervalList().getFirst().getStart().isEqual(startNew) || day.getDayIntervalList().getLast().getEnd().isEqual(endNew)){
            intervalOverreach(startNew, endNew);
        }
        correctIntervalFuture(day.getDayIntervalList());
        Database.saveAllClientMonths(listOfClm);
        saveChangedDays();
        //day.getDayIntervalListUsefull().get(0).setComment("Testing save logic");
        setIntervalBars(day);
    }
    private void saveChangedDays(){
        if(futureDay !=null){
            Database.saveIndividualClientDay(futureDay, DatabaseUtils.prepMID(futureMonth));
        }
        if(pastDay !=null){
            Database.saveIndividualClientDay(pastDay,DatabaseUtils.prepMID(pastMonth));
        }

    }
    public void createNewInterval(ActionEvent actionEvent) throws IOException {
       createNewIntervalAlg();
    }
    public void intervalOverreach( LocalDateTime newStart, LocalDateTime newEnd){
        boolean isDay;
        int[] switchCode;
        if(dayList.contains(selectedTextArea)){
            isDay = true;
            switchCode = new int[]{-1,0};
        }else{
            isDay = false;
            switchCode = new int[]{0,1};
        }
        if(newStart.getDayOfMonth() == 1){
            universalFuture( textClientIndex.get(selectedTextArea) ,switchCode,newEnd);
            specialPast(textClientIndex.get(selectedTextArea) ,switchCode,newStart,getPastMonths(textClientIndex.get(selectedTextArea).getClient()));
        } else if (newStart.getDayOfMonth() == newStart.getMonth().length(Year.isLeap(newStart.getYear()))) {
            specialFuture(textClientIndex.get(selectedTextArea) ,switchCode,newEnd,getFutureMonths(textClientIndex.get(selectedTextArea).getClient()));
            universalPast( textClientIndex.get(selectedTextArea) ,switchCode,newEnd);
        }else{
            universalFuture( textClientIndex.get(selectedTextArea) ,switchCode,newEnd);
            universalPast( textClientIndex.get(selectedTextArea) ,switchCode,newStart);
        }
    }
    private void specialPast(ClientDay currentDay,int[] switchCode, LocalDateTime newStart,ClientMonth specialMonth){
        ClientDay past;
        if(currentDay.getDayStatus()){
            past = specialMonth.getClientNightsInMonth().getLast();
            pastDay = past;
        }else{

            past = specialMonth.getClientDaysInMonth().getLast();
            pastDay = past;
        }
        if((currentDay.getDayIntervalList().getFirst().getStart().isEqual(newStart) )) {
            ServiceIntervalArrayList servListPast = past.getDayIntervalList();
            //  int i = servListPast.size()-1;
            ArrayList<ServiceInterval> toBeRemoved = new ArrayList<>();
            for (ServiceInterval serv : servListPast.reversed()) {
                LocalDateTime startTemp = serv.getStart();
                if (startTemp.isAfter(newStart)) {
                    toBeRemoved.add(serv);
                }
            }
            servListPast.removeAll(toBeRemoved);
            servListPast.getLast().setEnd(newStart);
        }
    }
    private void specialFuture(ClientDay currentDay,int[] switchCode, LocalDateTime newEnd,ClientMonth specialMonth){
        ClientDay future;
        if(currentDay.getDayStatus()){
            future = specialMonth.getClientNightsInMonth().getFirst();
            futureDay= future;
        }else{
            future = specialMonth.getClientDaysInMonth().getFirst();
            futureDay= future;
        }
        if((currentDay.getDayIntervalList().getLast().getEnd().isEqual(newEnd))){
            ServiceIntervalArrayList servListFuture= future.getDayIntervalList();
            //int iFuture = servListFuture.size()-1;
            ArrayList<ServiceInterval> toBeRemovedFuture = new ArrayList<>();
            for(ServiceInterval serv: servListFuture){
                LocalDateTime endTemp= serv.getEnd();
                if(endTemp.isBefore(newEnd)){
                    toBeRemovedFuture.add(serv);
                }
            }
            servListFuture.removeAll(toBeRemovedFuture);
            servListFuture.getFirst().setStart(newEnd);

        }
    }
    private void universalPast(ClientDay currentDay,int[] switchCode, LocalDateTime newStart){
        ClientDay past;
        if(currentDay.getDayStatus()){
             past = textClientIndex.get(nightList.get(dayList.indexOf(selectedTextArea)+switchCode[0]));
        }else{
             past = textClientIndex.get(dayList.get(nightList.indexOf(selectedTextArea) + switchCode[0]));
        }

        if((currentDay.getDayIntervalList().getFirst().getStart().isEqual(newStart) )) {
            ServiceIntervalArrayList servListPast = past.getDayIntervalList();
            ArrayList<ServiceInterval> toBeRemoved = new ArrayList<>();
            for (ServiceInterval serv : servListPast.reversed()) {
                LocalDateTime startTemp = serv.getStart();
                if (startTemp.isAfter(newStart)) {
                    toBeRemoved.add(serv);
                }
            }
            servListPast.removeAll(toBeRemoved);
            servListPast.getLast().setEnd(newStart);
        }
    }
    private void universalFuture(ClientDay currentDay,int[] switchCode, LocalDateTime newEnd){
        ClientDay future;
        if(nightList.size()>31 || dayList.size()>31){
            System.out.println(" ");
        }
        if(currentDay.getDayStatus()){
             future = textClientIndex.get(nightList.get(dayList.indexOf(selectedTextArea)+switchCode[1]));
        }else{
             future = textClientIndex.get(dayList.get(nightList.indexOf(selectedTextArea) + switchCode[1]));
        }
        //TODO problem is here
        if((currentDay.getDayIntervalList().getLast().getEnd().isEqual(newEnd))){
            ServiceIntervalArrayList servListFuture= future.getDayIntervalList();
            //int iFuture = servListFuture.size()-1;
            ArrayList<ServiceInterval> toBeRemovedFuture = new ArrayList<>();
            for(ServiceInterval serv: servListFuture){
                LocalDateTime endTemp= serv.getEnd();
                if(endTemp.isBefore(newEnd)){
                    toBeRemovedFuture.add(serv);
                }
            }
            servListFuture.removeAll(toBeRemovedFuture);
            servListFuture.getFirst().setStart(newEnd);

        }
    }
    private void intervalOverreachInternals(LocalDateTime newStart, LocalDateTime newEnd){
        boolean isDay;
        int[] switchCode;
        if(dayList.contains(selectedTextArea)){
            isDay = true;
            switchCode = new int[]{-1,0};
        }else{
            isDay = false;
            switchCode = new int[]{0,1};
        }
        if(isDay){
            ClientDay past = textClientIndex.get(nightList.get(dayList.indexOf(selectedTextArea)+switchCode[0]));
            if((textClientIndex.get(selectedTextArea).getDayIntervalList().getFirst().getStart().isEqual(newStart) )){
                ServiceIntervalArrayList servListPast= past.getDayIntervalList();
                //  int i = servListPast.size()-1;
                ArrayList<ServiceInterval> toBeRemoved = new ArrayList<>();
                for(ServiceInterval serv: servListPast.reversed()){
                    LocalDateTime startTemp = serv.getStart();
                    if(startTemp.isAfter(newStart)){
                        toBeRemoved.add(serv);
                    }
                }
                servListPast.removeAll(toBeRemoved);
                servListPast.getLast().setEnd(newStart);
            }
            ClientDay future = textClientIndex.get(nightList.get(dayList.indexOf(selectedTextArea)+switchCode[1]));
            if((textClientIndex.get(selectedTextArea).getDayIntervalList().getLast().getEnd().isEqual(newEnd))){
                ServiceIntervalArrayList servListFuture= future.getDayIntervalList();
                //int iFuture = servListFuture.size()-1;
                ArrayList<ServiceInterval> toBeRemovedFuture = new ArrayList<>();
                for(ServiceInterval serv: servListFuture){
                    LocalDateTime endTemp= serv.getEnd();
                    if(endTemp.isBefore(newEnd)){
                        toBeRemovedFuture.add(serv);
                    }
                }
                servListFuture.removeAll(toBeRemovedFuture);
                servListFuture.getFirst().setStart(newEnd);

            }

        }else {
            ClientDay past = textClientIndex.get(dayList.get(nightList.indexOf(selectedTextArea) + switchCode[0]));
            if ((textClientIndex.get(selectedTextArea).getDayIntervalList().getFirst().getStart().isEqual(newStart)))
            {
                ServiceIntervalArrayList servListPast = past.getDayIntervalList();
                ArrayList<ServiceInterval> toBeRemoved = new ArrayList<>();
                for (ServiceInterval serv : servListPast.reversed()) {
                    LocalDateTime startTemp = serv.getStart();
                    if (startTemp.isAfter(newStart)) {
                        toBeRemoved.add(serv);
                    }
                }
                servListPast.removeAll(toBeRemoved);
                servListPast.getLast().setEnd(newStart);
            }
            ClientDay future = textClientIndex.get(dayList.get(nightList.indexOf(selectedTextArea) + switchCode[1]));
            if ((textClientIndex.get(selectedTextArea).getDayIntervalList().getLast().getEnd().isEqual(newEnd))) {
                ServiceIntervalArrayList servListFuture = future.getDayIntervalList();
                ArrayList<ServiceInterval> toBeRemovedFuture = new ArrayList<>();
                for (ServiceInterval serv : servListFuture) {
                    LocalDateTime endTemp = serv.getEnd();
                    if (endTemp.isBefore(newEnd)) {
                        toBeRemovedFuture.add(serv);
                    }
                }
                servListFuture.removeAll(toBeRemovedFuture);
                servListFuture.getFirst().setStart(newEnd);
            }
        }

    }
    /*
    private void intervalOverreachInternals(LocalDateTime newStart, LocalDateTime newEnd){
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
            if((textClientIndex.get(selectedTextArea).getDayIntervalList().getFirst().getStart().isEqual(newStart) )){
                ServiceIntervalArrayList servListPast= past.getDayIntervalList();
                //  int i = servListPast.size()-1;
                ArrayList<ServiceInterval> toBeRemoved = new ArrayList<>();
                for(ServiceInterval serv: servListPast.reversed()){
                    LocalDateTime startTemp = serv.getStart();
                    if(startTemp.isAfter(newStart)){
                        toBeRemoved.add(serv);
                    }
                }
                servListPast.removeAll(toBeRemoved);
                servListPast.getLast().setEnd(newStart);
            }
            ClientDay future = textClientIndex.get(nightList.get(dayList.indexOf(selectedTextArea)+switchCode[1]));
            if((textClientIndex.get(selectedTextArea).getDayIntervalList().getLast().getEnd().isEqual(newEnd))){
                ServiceIntervalArrayList servListFuture= future.getDayIntervalList();
                //int iFuture = servListFuture.size()-1;
                ArrayList<ServiceInterval> toBeRemovedFuture = new ArrayList<>();
                for(ServiceInterval serv: servListFuture){
                    LocalDateTime endTemp= serv.getEnd();
                    if(endTemp.isBefore(newEnd)){
                        toBeRemovedFuture.add(serv);
                    }
                }
                servListFuture.removeAll(toBeRemovedFuture);
                servListFuture.getFirst().setStart(newEnd);

            }

        }else {
            ClientDay past = textClientIndex.get(dayList.get(nightList.indexOf(selectedTextArea) + switchCode[0]));
            if ((textClientIndex.get(selectedTextArea).getDayIntervalList().getFirst().getStart().isEqual(newStart)))
            {
                ServiceIntervalArrayList servListPast = past.getDayIntervalList();
                ArrayList<ServiceInterval> toBeRemoved = new ArrayList<>();
                for (ServiceInterval serv : servListPast.reversed()) {
                    LocalDateTime startTemp = serv.getStart();
                    if (startTemp.isAfter(newStart)) {
                        toBeRemoved.add(serv);
                    }
                }
                servListPast.removeAll(toBeRemoved);
                servListPast.getLast().setEnd(newStart);
            }
            ClientDay future = textClientIndex.get(dayList.get(nightList.indexOf(selectedTextArea) + switchCode[1]));
            if ((textClientIndex.get(selectedTextArea).getDayIntervalList().getLast().getEnd().isEqual(newEnd))) {
                ServiceIntervalArrayList servListFuture = future.getDayIntervalList();
                ArrayList<ServiceInterval> toBeRemovedFuture = new ArrayList<>();
                for (ServiceInterval serv : servListFuture) {
                    LocalDateTime endTemp = serv.getEnd();
                    if (endTemp.isBefore(newEnd)) {
                        toBeRemovedFuture.add(serv);
                    }
                }
                servListFuture.removeAll(toBeRemovedFuture);
                servListFuture.getFirst().setStart(newEnd);
            }
        }

    }
     */
    public void saveIntervalChanges(ActionEvent actionEvent) throws IOException {
       saveIntervalAlg();
    }
    private void correctInterval(ServiceIntervalArrayList serviceIntervals){
        if(serviceIntervals.size()>1){
            for(int i =0;i<serviceIntervals.size()-1;i++){
                serviceIntervals.get(i).setEnd(serviceIntervals.get(i+1).getStart());
            }
        }
    }
    private void correctIntervalFuture(ServiceIntervalArrayList serviceIntervals){
        if(serviceIntervals.size()>1){
            for(int i =1;i<serviceIntervals.size();i++){
                serviceIntervals.get(i).setStart(serviceIntervals.get(i-1).getEnd());
            }
        }
    }
    public LocalDateTime setLocalDateTime(int hours, int minutes, int day){
        if(dayList.contains(selectedTextArea)){
            return LocalDateTime.of(Integer.parseInt(selectedYearValueVisual.getText()),
                    Month.of(Integer.parseInt(selectedMonthValueVisual.getText())), day, hours, minutes);
        }else{
            int year = Integer.parseInt(selectedYearValueVisual.getText());
            int month = Integer.parseInt(selectedMonthValueVisual.getText());
            int newYear = year;
            int newMonth = month;
            int newDay = day;

            if(day == Month.of(month).length(Year.isLeap(year))) {
                if(month == 12){
                    newYear = year+1;
                    newMonth = 1;
                }else{
                    newMonth = month +1;
                    newDay = 1;
                }
                System.out.println("triggered");
            }else{
                newDay++;
            }
            if(hours> 12){
                return LocalDateTime.of(Integer.valueOf(selectedYearValueVisual.getText()),
                        Month.of(Integer.valueOf(selectedMonthValueVisual.getText())), day, hours, minutes);
            }else{
                return LocalDateTime.of(newYear,
                        Month.of(newMonth), newDay, hours, minutes);
            }
        }
    }
    public LocalDateTime setLocalDateTimeEnd(int hours, int minutes, int day){
        if(dayList.contains(selectedTextArea)){
            return LocalDateTime.of(Integer.parseInt(selectedYearValueVisual.getText()),
                    Month.of(Integer.parseInt(selectedMonthValueVisual.getText())), day, hours, minutes);
        }else{
            int year = Integer.parseInt(selectedYearValueVisual.getText());
            int month = Integer.parseInt(selectedMonthValueVisual.getText());
            int newYear = year;
            int newMonth = month;
            int newDay = day;

            if(day == Month.of(month).length(Year.isLeap(year))) {
                if(month == 12){
                    newYear = year+1;
                    newMonth = 1;
                }else{
                    newMonth = month +1;
                    day = 1;
                }
                System.out.println("triggered");
            }else{
                newDay++;
            }
            if(hours> 12){
                return LocalDateTime.of(Integer.parseInt(selectedYearValueVisual.getText()),
                        Month.of(Integer.parseInt(selectedMonthValueVisual.getText())), day, hours, minutes);
            }else{
                return LocalDateTime.of(newYear,
                        Month.of(newMonth), newDay, hours, minutes);
            }
        }
    }
    private ArrayList<LocationRepresentative> prepareMergeable()  {
        ClientDay cl = textClientIndex.get(selectedTextArea);
        ArrayList<LocationRepresentative> output = new ArrayList<>();
            //ListOfClientMonths lic = jsom.loadClientRequirementsForMonth(settings);
            for(ClientMonth c : listOfClm.getListOfClientMonths()){
                ArrayList<ClientDay> visitedMonth;
                if(dayList.contains(selectedTextArea)) {
                    visitedMonth = c.getClientDaysInMonth();
                }else{
                    visitedMonth = c.getClientNightsInMonth();
                }
                ClientDay cld = visitedMonth.get(cl.getDay()-1);
                    if(!(cld.getLocation() ==null) &&cld.getLocation().equals(cl.getLocation() )){
                        LocationRepresentative locp = new LocationRepresentative();
                        locp.setClp(clientIndex.get(cld.getClient()));
                        locp.setClDay(cld);
                        output.add(locp);
                }
            }
        List disp = output.stream().filter(c->(!c.getClp().getID().equals(textClientIndex.get(selectedTextArea).getClient()))).toList();
        mergedWithChoiceBox.getItems().clear();
        mergedWithChoiceBox.getItems().addAll(disp);
            return output;
    }
    public void findNewSolution(ActionEvent actionEvent) throws IOException {
        JsonManip.getJsonManip().generateNewMonthsAssistants(settings.getCurrentYear(), settings.getCurrentMonth());
        findSolutionV3(actionEvent);
    }
    public void hideDayInfo() {
         if (selectedTextArea!=null) {
            mainGrid.setConstraints(calendarScrollPane,mainGrid.getColumnIndex(calendarScrollPane),mainGrid.getRowIndex(calendarScrollPane),mainGrid.getColumnSpan(calendarScrollPane),mainGrid.getRowSpan(calendarScrollPane)+1);
            dayInfoGrid.setVisible(false);
            isMenuVisible=false;
            selectedTextArea.getStyleClass().remove("selected-pane");
            selectedTextArea =null;
            pastDay = null;
            futureDay = null;
        }
    }
    public void clearTable() throws IOException {
       ListOfClients cliList = Database.loadFullClients(selectedYearValue,selectedMonthValue);
        hideDayInfo();
        nightList.clear();
        dayList.clear();
       textClientIndex.clear();
       ListOfClientMonths cliMoth = new ListOfClientMonths();
       for(Client cl : cliList.getActiveClients()){
           cliMoth.getListOfClientMonths().add(cl.getClientsMonth());
           ArrayList<ClientDay> dayList = cl.getClientsMonth().getClientDaysInMonth();
           for(ClientDay day: dayList) {
            for(ServiceInterval serv : day.getDayIntervalList()){
                serv.setOverseeingAssistant(null);
            }
           }
           ArrayList<ClientDay> nightList = cl.getClientsMonth().getClientNightsInMonth();
           for(ClientDay day: nightList) {
               for(ServiceInterval serv : day.getDayIntervalList()){
                   serv.setOverseeingAssistant(null);
               }
           }

       }
       Database.saveAllClientMonths(cliMoth);
       populateView(Database.loadFullClients(selectedYearValue,selectedMonthValue));
    }
    private ClientMonth getPastMonths(UUID id){
        int yearIter = 0;
        int month = settings.getCurrentMonth();
        if(month==1){
            yearIter = -1;
        }
        if(pastMonth == null){
            pastMonth = Database.loadClientMonth(settings.getCurrentYear()+yearIter, changeMonth(settings.getCurrentMonth(),-1),id);
        }
        return pastMonth;
    }
    private ClientMonth getFutureMonths(UUID id){
        int yearIter = 0;
        int month = settings.getCurrentMonth();
        if(month==12){
            yearIter = 1;
        }
        if(futureMonth == null){
            futureMonth = Database.loadClientMonth(settings.getCurrentYear()+yearIter, changeMonth(settings.getCurrentMonth(),+1),id);
        }
        return futureMonth;
    }
    private int changeMonth(int inputMonth, int change){
        if(inputMonth+change>12){
            return ((inputMonth+change)-12);
        } else if (inputMonth+change<=0) {
            return ((inputMonth+change)+12);
        }else {
        return inputMonth+change;
        }
    }
    @Override
    public void updateScreen() {
           // populateView(getClientsOfMonth(settings));
        availableAssistants = Database.loadAssistantAvailability(settings.getCurrentYear(), settings.getCurrentMonth());
    }

    @Override
    public void loadAndUpdateScreen() {
        System.out.println("Main");
        ListOfClients loc = Database.loadFullClients(selectedYearValue,selectedMonthValue);
        Platform.runLater(() -> {
            populateView(loc);
        });
        try {
            populateLocationIndex();
            populateClientIndex();
            populateAssistantIndex();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
}
