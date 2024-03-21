package graphics.sorter.controllers;

import graphics.sorter.*;
import graphics.sorter.AssistantAvailability.AssistantAvailability;
import graphics.sorter.Filters.Sorter;
import graphics.sorter.Structs.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.io.Console;
import java.io.IOException;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
public class MainPageController implements ControllerInterface{
    //region Graphical components
    @FXML
    private Text selectedYear;
    @FXML
    private Text selectedMonth;
    @FXML
    private  GridPane sideGrid;
    @FXML
    private Pane basePane;
    @FXML
    private ChoiceBox<Location> locationChoiceBox;
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
    private HashMap<TextFlow,ClientDay> textClientIndex = new HashMap<>();
    private HashMap<AnchorPane,ServiceInterval> paneServiceIndex = new HashMap<>();
    private HashMap<ServiceInterval,AnchorPane> servicePaneIndex= new HashMap<>();
    private HashMap<UUID,ClientProfile> clientIndex = new HashMap<>();
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
        isMenuVisible = false;
        settings = Database.loadSettings();
        selectedYearValueVisual.setText(String.valueOf(settings.getCurrentYear()));
        selectedMonthValueVisual.setText(String.valueOf(settings.getCurrentMonth()));
        selectedYearValue = settings.getCurrentYear();
        selectedMonthValue = settings.getCurrentMonth();
        locationChoiceBox.setOnAction(this :: selectLocation);
        ArrayList<Location> lic = Objects.requireNonNull(Database.loadLocations()).getListOfLocations();
        lic.add(null);
        locationChoiceBox.getItems().setAll(lic);
        populateView(getClientsOfMonth(settings));
        mainGrid.setConstraints(calendarScrollPane,mainGrid.getColumnIndex(calendarScrollPane),mainGrid.getRowIndex(calendarScrollPane),mainGrid.getColumnSpan(calendarScrollPane),mainGrid.getRowSpan(calendarScrollPane)+1);
        attachObservers();
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
        for(Client cl : LiCcl.getClientList()){
            listOfClm.getListOfClientMonths().add(Database.loadClientMonth(settings.getCurrentYear(),settings.getCurrentMonth(),cl.getID()));
        }
        //vytahnout y klientu m2s9c2
        editedMonth = Month.of(settings.getCurrentMonth());
         areaList = new ArrayList<TextFlow>();
         titleList.clear();
        /*
        Vytvoří nadpisy pro jednotlivé dny
         */
        int i = 0;
        while (i <= editedMonth.length(false)){
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
                    inputText = LiCcl.getClientList().get(clientIter).getName() + " " + LiCcl.getClientList().get(clientIter).getSurname();
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
            for(Client cl: out.getClientList()){
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
                listOfClients.getClientList().add(out);
            }
            Database.saveAllClientMonths(listOfClm);
            System.out.println("LoadError");
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
    private void graphicSetup(){
       // grid.setStyle("-fx-background-color: " + GS.getColors().get("Night")+";");
    for(TextFlow tex: dayList){
        tex.setStyle("-fx-control-inner-background: " + GS.getColors().get("Day")+";");
        tex.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume(); // This blocks the event from being processed further.
            }
        });
    }
    for(TextFlow tex: nightList){
        tex.setStyle("-fx-control-inner-background: " + GS.getColors().get("Night")+";");
        tex.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume(); // This blocks the event from being processed further.
            }
        });
        }
        for(TextFlow tex: titleList){
            Text t = (Text) tex.getChildren().getFirst();
            tex.setStyle("-fx-border-color: " + GS.getColors().get("Night")+";");
            t.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            tex.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    event.consume(); // This blocks the event from being processed further.
                }
            });
        }
    }
    public void switchPage(ActionEvent actionEvent) throws IOException {
       Scene scen = calendarScrollPane.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("assistant-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);

    }
    public void switchPageToShift(ActionEvent actionEvent) throws IOException {
        Scene scen = calendarScrollPane.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("shiftPicker-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);
    }
    public List<ArrayList<ArrayList<UUID>>>prepareMergedData(ListOfClients listOfClients,ListOfClientMonths listOfClm, int length) {
        ArrayList<ArrayList<ClientDay>> mergedListDay = new ArrayList<>();
        ArrayList<ArrayList<ClientDay>> mergedListNight = new ArrayList<>();
        ArrayList<ArrayList<UUID>> outputDay = new ArrayList<>();
        ArrayList<ArrayList<UUID>> outputNight = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<UUID>>> finalOutput = new ArrayList<>(Arrays.asList( outputDay,outputNight));
        for (int dayIter = 0; dayIter < length; dayIter++) {
            ArrayList<ClientDay> arD = new ArrayList<ClientDay>();
            ArrayList<ClientDay> arN = new ArrayList<ClientDay>();
            for(ClientMonth cl :listOfClm.getListOfClientMonths()) {
                arD.add(cl.getClientDaysInMonth().get(dayIter));
                arN.add(cl.getClientDaysInMonth().get(dayIter));

            }
            mergedListDay.add(arD);
            mergedListNight.add(arN);
        }

        for(ArrayList<ClientDay> cl : mergedListDay){
            List<ClientDay> mergedClientDays = cl.stream()
                    .filter(ClientDay::isMerged)
                    .collect(Collectors.toList());
            if(!mergedClientDays.isEmpty()) {
                Map<Location, List<ClientDay>> entitiesByLocation = mergedClientDays.stream()
                        .collect(Collectors.groupingBy(ClientDay::getLocation));
                ArrayList<ArrayList<ClientDay>> listOfLists = new ArrayList<>(
                        entitiesByLocation.values().stream()
                                .map(ArrayList::new) // Convert List<ClientDay> to ArrayList<ClientDay>
                                .collect(Collectors.toList()));
                for (ArrayList<ClientDay> ar : listOfLists) {
                    Comparator<ClientDay> dayLengthComparator = Comparator.comparingLong(ClientDay::shiftLength);
                    ar.sort(dayLengthComparator);
                  //  ar.remove(ar.getFirst());
                    if (!ar.isEmpty()) {
                        ArrayList<UUID> out = ar.stream().map(ClientDay::getClient).collect(Collectors.toCollection(ArrayList::new));
                        outputDay.add(out);
                    } else {
                        outputDay.add(new ArrayList<UUID>());
                    }
                }
            }else{
                outputDay.add(new ArrayList<UUID>());
            }
        }
        for(ArrayList<ClientDay> cl : mergedListNight){
            List<ClientDay> mergedClientDays = cl.stream()
                    .filter(ClientDay::isMerged)
                    .collect(Collectors.toList());
            if(!mergedClientDays.isEmpty()) {
                Map<Location, List<ClientDay>> entitiesByLocation = mergedClientDays.stream()
                        .collect(Collectors.groupingBy(ClientDay::getLocation));
                ArrayList<ArrayList<ClientDay>> listOfLists = new ArrayList<>(
                        entitiesByLocation.values().stream()
                                .map(ArrayList::new) // Convert List<ClientDay> to ArrayList<ClientDay>
                                .collect(Collectors.toList()));
                for (ArrayList<ClientDay> ar : listOfLists) {
                    Comparator<ClientDay> dayLengthComparator = Comparator.comparingLong(ClientDay::shiftLength);
                    ar.sort(dayLengthComparator);
                    ar.remove(ar.getFirst());
                    if (!ar.isEmpty()) {
                        ArrayList<UUID> out = ar.stream().map(ClientDay::getClient).collect(Collectors.toCollection(ArrayList::new));
                        outputNight.add(out);
                    } else {
                        outputNight.add(new ArrayList<UUID>());
                    }
                }
            }else{
                outputNight.add(new ArrayList<UUID>());
            }
        }

        /*
          for (int dayIter = 0; dayIter < length; dayIter++) {
        for(ClientMonth cl :listOfClm.getListOfClientMonths()) {
            ArrayList<Client> clL = new ArrayList<Client>();
            if (cl.getClientDaysInMonth().get(dayIter).isMerged() == true) {
                for (ClientMonth cl2 : listOfClm.getListOfClientMonths()) {

                }

            }
        }

        }
         */

    return  finalOutput;
    }
    private Assistant getAssistantFromID(ArrayList<Assistant> asList, UUID id){
        for(Assistant as : asList){
            if(as.getID().equals(id)){
                return as;
            }
        }
        return null;
    }
    public void findSolutionV2(ActionEvent actionEvent) throws IOException {
        ListOfAssistants asL = Database.loadAssistants();
        Sorter sorter = new Sorter(asL);
        int monthLength;
        AvailableAssistants avAs = null;
        avAs = Database.loadAssistantAvailability(selectedYearValue,selectedMonthValue);

        ListOfClients listOfClients = Database.loadFullClients(selectedYearValue,selectedMonthValue);

        listOfClm = new ListOfClientMonths();
        for(Client cl : listOfClients.getClientList()){
            listOfClm .getListOfClientMonths().add(cl.getClientsMonth());
        }


        monthLength = listOfClm.getListOfClientMonths().get(0).getClientDaysInMonth().size();
        List<ArrayList<ArrayList<UUID>>> iterList = prepareMergedData(listOfClients,listOfClm,monthLength);
        for (int dayIter = 0; dayIter < monthLength; dayIter++) {

            for( Client cl : listOfClients.getClientList()) {
                if(iterList.getFirst().get(dayIter).contains(cl.getID())){
                    if(iterList.getFirst().get(dayIter).getFirst().equals(cl.getID())){
                        ClientDay clDay = cl.getClientsMonth().getClientDaysInMonth().get(dayIter);
                        ArrayList<AssistantAvailability> listOfAvailableAtDay = getAvailableAssistantForDay(avAs,dayIter,true);
                        UUID dayPicked = sorter.sort(listOfAvailableAtDay,dayIter,0,clDay,asL);
                        for(int i = 1;i<iterList.getFirst().get(dayIter).size();i++ ){
                            ClientDay cld = listOfClm.getMonthOfSpecificClient(iterList.getFirst().get(dayIter).get(i)).getClientDaysInMonth().get(dayIter);
                            for(ServiceInterval serv : cld.getDayIntervalListUsefull()){
                                serv.setOverseeingAssistant(getAssistantFromID(asL.getAssistantList(),dayPicked));
                            }
                        }
                    }else{
                        System.out.println("hi");
                        continue;
                    }

                }else{
                    ClientDay clDay = cl.getClientsMonth().getClientDaysInMonth().get(dayIter);
                    ArrayList<AssistantAvailability> listOfAvailableAtDay = getAvailableAssistantForDay(avAs,dayIter,true);
                    UUID dayPicked = sorter.sort(listOfAvailableAtDay,dayIter,0,clDay,asL);
                }

                //System.out.println(sorter.getIdFromList(listOfAvailableAtDay));
            }
            for( Client cl : listOfClients.getClientList()) {
                if(iterList.get(1).get(dayIter).contains(cl)){
                    System.out.println("hi");
                    continue;
                }
                ClientDay clNight = cl.getClientsMonth().getClientNightsInMonth().get(dayIter);
                ArrayList<AssistantAvailability> listOfAvailableAtNight = getAvailableAssistantForDay(avAs,dayIter,false);
                UUID nightPicked = sorter.sort(listOfAvailableAtNight,dayIter,1,clNight,asL);
            }

        }
        Database.saveAllClientMonths(listOfClm);
        //jsom.saveClientInfo();
        populateView(getClientsOfMonth(settings));
        CompletableFuture<Void> future = CompletableFuture.runAsync(()-> { for(ClientProfile clip :listOfClients.convertToListOfClientProfiles().getFullClientList()){
            Database.saveClientProfile(clip);
        }});
        sorter.report();

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
        System.out.println("Selected year" + selectedYearValue);
        System.out.println("Selected month" + selectedMonthValue);
       // settings = Database.loadSettings();
        Database.saveSettings(settings);
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
        isMergedCheckBox.setSelected(day.isMerged());
        locationChoiceBox.setValue(day.getLocation());
        prepareMergeable();
    }
    private String prepareDayInfo(ClientDay day){
        String outputString = new String();
        outputString ="Datum : " + day.getDay() +"."+ day.getMonth().getValue()+"."+day.getYear() + "\n";
        String dayName =LocalDate.of(day.getYear(),day.getMonth().getValue(),day.getDay()).getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("cs", "CZ"));
        dayName =  dayName.substring(0, 1).toUpperCase() + dayName.substring(1);
        outputString = outputString + "Den v týdnu : " + dayName+ "\n";
        outputString = outputString + "Odpovídající asistent : "+printAssistantsOfDay(day)+ "\n";
        return outputString;
    }
    public void selectLocation(ActionEvent actionEvent){
        textClientIndex.get(selectedTextArea).setLocation(locationChoiceBox.getValue());
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
    private void displayIntervalInfo(MouseEvent mouseEvent) {
       ServiceInterval serv = paneServiceIndex.get(mouseEvent.getSource());
       selectedAnchorPane.getStyleClass().remove("selected-interval");
       selectedAnchorPane = servicePaneIndex.get(serv);
       selectedInterval = serv;
       intervalCommentArea.setText(serv.getComment());
       isRequiredCheckBox.setSelected(serv.getIsNotRequired());
        isServiceMergedCheckBox.setSelected(serv.isMerged());
        selectedAnchorPane.getStyleClass().add("selected-interval");
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
            selectedAnchorPane = servicePaneIndex.get(serv);
            selectedAnchorPane.getStyleClass().add("selected-interval");
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
                        , setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()), as, null, false, false));
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
                                , temp, s.getOverseeingAssistant(), null, true,false));
                        System.out.println("Type 3");
                        break;
                    }

                } else {
                    System.out.println("nothing");
                    break;// paneServiceIndex.get(selectedTextArea).getDayIntervalList().add(new ServiceInterval());
                }
            }
            day.getDayIntervalList().add(new ServiceInterval(setLocalDateTime(startHoursChoice.getValue(), startMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay())
                    , setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()), day.getDayIntervalList().get(0).getOverseeingAssistant(), null, false, false));
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
        boolean alreadyExists = false;
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
                        , setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()), as, null, false, false));
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
                        , temp, s.getOverseeingAssistant(), null, false, false));
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
                , setLocalDateTime(endHoursChoice.getValue(), endMinutesChoice.getValue(), textClientIndex.get(selectedTextArea).getDay()), day.getDayIntervalList().getFirst().getOverseeingAssistant(), null, false, false));
        if(day.getDayIntervalList().getFirst().getStart().isEqual(startNew) || day.getDayIntervalList().getLast().getEnd().isEqual(endNew)){
            intervalOverreach(startNew, endNew);
        }
        Database.saveAllClientMonths(listOfClm);
        day.getDayIntervalListUsefull().get(0).setComment("Testing save logic");
        setIntervalBars(day);
    }
    public void saveIntervalAlg() throws IOException {
        ClientDay day = textClientIndex.get(selectedTextArea);
        day.getDayIntervalList().remove(selectedInterval);
        LocalDateTime startNew = setLocalDateTime(startHoursChoice.getValue(),startMinutesChoice.getValue(),textClientIndex.get(selectedTextArea).getDay());
        LocalDateTime endNew = setLocalDateTime(endHoursChoice.getValue(), + endMinutesChoice.getValue(),textClientIndex.get(selectedTextArea).getDay());
        selectedInterval.setStart(startNew);
        selectedInterval.setEnd(endNew);
        selectedInterval.setNotRequired(isRequiredCheckBox.isSelected());
        selectedInterval.setComment(intervalCommentArea.getText());
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
        Database.saveAllClientMonths(listOfClm);
        saveChangedDays();
        //day.getDayIntervalListUsefull().get(0).setComment("Testing save logic");
        setIntervalBars(day);
    }
    private void saveChangedDays(){
        if(futureDay !=null){
            Database.saveIndividualClientDay(futureDay,DatabaseUtils.prepMID(futureMonth));
        }
        if(pastDay !=null){
            Database.saveIndividualClientDay(pastDay,DatabaseUtils.prepMID(pastMonth));
        }

    }
    public void createNewInterval(ActionEvent actionEvent) throws IOException {
       createNewIntervalAlg();

    }
    public void intervalOverreach( LocalDateTime newStart, LocalDateTime newEnd){
       // LocalDateTime defStart = LocalDateTime.of(settings.getDeftStart()[0],settings.getDeftStart()[1]);
       // LocalDateTime defEnd = settings.getDefEnd();
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
//} else if (newStart.getDayOfMonth() == newStart.getMonth().length(Year.isLeap(newStart.getYear()))+1) {
        } else if (newStart.getDayOfMonth() == newStart.getMonth().length(Year.isLeap(newStart.getYear()))) {
            specialFuture(textClientIndex.get(selectedTextArea) ,switchCode,newEnd,getFutureMonths(textClientIndex.get(selectedTextArea).getClient()));
            universalPast( textClientIndex.get(selectedTextArea) ,switchCode,newEnd);
        }else{
           // intervalOverreachInternals(newStart,newEnd);
            universalFuture( textClientIndex.get(selectedTextArea) ,switchCode,newEnd);
            universalPast( textClientIndex.get(selectedTextArea) ,switchCode,newStart);
        }
    }
    private void specialPast(ClientDay currentDay,int[] switchCode, LocalDateTime newStart,ClientMonth specialMonth){
        ClientDay past;
        if(currentDay.getDayStatus()){
           // past = textClientIndex.get(nightList.get(dayList.indexOf(selectedTextArea)+switchCode[0]));
            past = specialMonth.getClientNightsInMonth().getLast();
            pastDay = past;
        }else{
          //  past = textClientIndex.get(dayList.get(nightList.indexOf(selectedTextArea) + switchCode[0]));
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
            //future = textClientIndex.get(nightList.get(dayList.indexOf(selectedTextArea)+switchCode[1]));
            future = specialMonth.getClientNightsInMonth().getFirst();
            futureDay= future;
        }else{
            //future = textClientIndex.get(dayList.get(nightList.indexOf(selectedTextArea) + switchCode[1]));
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
    private void universalFuture(ClientDay currentDay,int[] switchCode, LocalDateTime newEnd){
        ClientDay future;
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
        System.out.println("Saved");

    }
    private void correctInterval(ServiceIntervalArrayList serviceIntervals){
        if(serviceIntervals.size()>1){
            for(int i =0;i<serviceIntervals.size()-1;i++){
                serviceIntervals.get(i).setEnd(serviceIntervals.get(i+1).getStart());
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
        findSolutionV2(actionEvent);
    }
    public void clearTable() throws IOException {
       ListOfClients cliList = Database.loadFullClients(selectedYearValue,selectedMonthValue);
       ListOfClientMonths cliMoth = new ListOfClientMonths();
       for(Client cl : cliList.getClientList()){
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

    }
}
