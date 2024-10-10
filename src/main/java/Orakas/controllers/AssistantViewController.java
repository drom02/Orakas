package Orakas.controllers;

import Orakas.*;
import Orakas.AssistantAvailability.Availability;
import Orakas.AssistantAvailability.ShiftAvailability;
import Orakas.Database.Database;
import Orakas.Humans.Assistant;
import Orakas.Humans.ClientProfile;
import Orakas.JavaFXCustomComponents.AssistantViewConSetup;
import Orakas.Structs.ListOfClientsProfiles;
import Orakas.Structs.VacationCellFactory;
import Orakas.Vacations.Vacation;
import Orakas.Vacations.VacationTemp;
import Orakas.Mediator.InternalController;
import Orakas.Structs.HumanCellFactory;
import Orakas.Structs.ListOfAssistants;
import Orakas.Structs.Utility.CircularList;
import Orakas.workHoursAllocation.WorkHoursCalcul;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
/*
JavaFX controller for assistant view
 */
public class AssistantViewController extends SaveableControllerInterface implements ControllerInterface{


    //region graphical components
    @FXML
    private CheckBox vacationCountsWorkCheck;
    @FXML
    private ListView<Vacation> vacationList;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextFlow startFlow;
    @FXML
    private TextFlow endFlow;
    @FXML
    private  Pane mainPane;
    @FXML
    private  GridPane mainGrid;
    @FXML
    private  GridPane clientOpinionGrid;
    @FXML
    private ColumnConstraints parentGrid;
    @FXML
    private GridPane daysInWeekGrid;
    @FXML
    private ListView listViewofA;
    @FXML
    private CheckBox statusCheckBox;
    @FXML
    private CheckBox isDriverCheck;
    @FXML
    private CheckBox overtimeCheck;
    @FXML
    private CheckBox dayCheck;
    @FXML
    private CheckBox nightCheck;
    @FXML
    private CheckBox emergencyAssistantCheck;
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
    private ArrayList<Assistant> listOfAssist;
    //endregion
    //region variables
    private Assistant selectedAssistant;
    private  ArrayList<ShiftAvailability> stateOfDays= ShiftAvailability.generateWeek();
    private ListOfAssistants listOfA;
    private ArrayList<Control> assistantNodes;
    private HashMap<UUID, ArrayList<Object>> itemIndex;
    private Vacation selectedVacation;
    private VacationTemp listOfVacations;
    private ArrayList<AssistantViewConSetup> listDataEntry= new ArrayList<>();
    private CircularList<AssistantViewConSetup> linkedViewConList = new CircularList<>(Collections.nCopies(14, null));
    private ArrayList<TextField> numericFields = new ArrayList<>(Arrays.asList(workField));
    private InternalController internalController = new InternalController(this);
    private ArrayList<Node> requiredNodes ;
    //endregion
    
    public void deleteAssistant(MouseEvent mouseEvent) throws IOException {
        if(!(selectedAssistant == null)){
            Database.softDeleteAssistant(selectedAssistant);
            listOfA.getFullAssistantList().remove(selectedAssistant);
            ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.getFullAssistantList());
            listViewofA.setItems(observAssistantList);
            internalController.send("Assistant");
            for(Control n : assistantNodes){
                if (n instanceof TextArea) {
                    ((TextArea) n).clear();
                } else if (n instanceof TextField) {
                    ((TextField) n).clear();
                } else if (n instanceof CheckBox) {
                    ((CheckBox) n).setSelected(false);
                }
            }
            if(listOfA.getAssistantList().isEmpty()){
                selectedAssistant = null;
            }else{
                selectedAssistant = listOfA.getAssistantList().getFirst();
            }
        }else{
            System.out.println("Vyberte asistenta k odstranění.");
        }
    }
   /*
   @TODO Add how to deal with somebody changing name and surname, check that it is still unique.
    */
    public void saveAssistant(MouseEvent mouseEvent) throws IOException {
        save();
    }
    private void setNumericCheck(){
    TextField tex = workField;

        tex.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) { // Regular expression for digits with optional decimal dot
                if (newValue.isEmpty()) {
                    tex.setText(newValue);
                } else if (newValue.matches("\\d*\\.")) {
                    // Allow input value if it's a number followed by a dot (beginning of decimal)
                    tex.setText(newValue);
                } else {
                    // Remove all non-digits and extra dots
                    String valueWithoutNonDigits = newValue.replaceAll("[^\\d.]", "");
                    // Handling multiple dots: Keep the first dot, remove the rest
                    int firstDotIndex = valueWithoutNonDigits.indexOf(".");
                    if (firstDotIndex != -1) {
                        String beforeDot = valueWithoutNonDigits.substring(0, firstDotIndex + 1);
                        String afterDot = valueWithoutNonDigits.substring(firstDotIndex + 1).replaceAll("\\.", "");
                        tex.setText(beforeDot + afterDot);
                    } else {
                        tex.setText(valueWithoutNonDigits);
                    }
                }
            }
        });

    }
    private void setupDatePickers(){
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            // This code block will be executed whenever the user selects a date
            AssistantVacationManager.onMouseClickedObs(newValue,startFlow);
        });
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            // This code block will be executed whenever the user selects a date
            AssistantVacationManager.onMouseClickedObs(newValue,endFlow);
        });
    }
    public void initialize() throws IOException {
        assistantNodes = new ArrayList<>(Arrays.asList(nameField, statusCheckBox, surnameField,overtimeCheck,dayCheck,nightCheck,workField,comments));
        contractField.getItems().addAll("HPP","DPP","DPČ","HPP-Vlastní");
        contractField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
                    loadContract();
                });
        listViewofA.setCellFactory(new HumanCellFactory());
        vacationList.setCellFactory(new VacationCellFactory());
        listOfA = Database.loadAssistants();
        listOfAssist = Database.loadAssistants().getAssistantList();

        ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.getFullAssistantList());
        listViewofA.setItems(observAssistantList);
        Platform.runLater(() -> {
            statusCheckBox.setSelected(true);
            GraphicalFunctions.screenResizing(mainPane,mainGrid);
            populateDaysInWeekTable();
            try {
                populateClientOpinion();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            setNumericCheck();
            //setupDatePickers();
            if(listViewofA.getItems().isEmpty()){
                selectedAssistant = null;
            }else{
                listViewofA.getSelectionModel().select(0);
                loadAssistant(null);
            }
            requiredNodes = new ArrayList<>(Arrays.asList(nameField,surnameField,contractField,workField));
        });

    }
    public void loadAssistant(MouseEvent mouseEvent) {
        selectedAssistant = (Assistant) listViewofA.getSelectionModel().getSelectedItem();
    if(!(selectedAssistant== null)){
        nameField.setText(selectedAssistant.getName());
        surnameField.setText(selectedAssistant.getSurname());
        statusCheckBox.setSelected(selectedAssistant.getActivityStatus());
        contractField.setValue(selectedAssistant.getContractType());
        overtimeCheck.setSelected(selectedAssistant.getLikesOvertime());
        isDriverCheck.setSelected(selectedAssistant.isDriver());
        loadContract();
        workField.setText(String.valueOf(selectedAssistant.getContractTime()));
        comments.setText(selectedAssistant.getComment());
        stateOfDays = selectedAssistant.getWorkDays();
        emergencyAssistantCheck.setSelected(selectedAssistant.isEmergencyAssistant());
        loadClientOpinion(selectedAssistant);
        listOfVacations = Database.loadVacation(selectedAssistant.getID());
        AssistantVacationManager.loadVacationList(listOfVacations, vacationList);
        loadTimes();
        refreshDays();
    }
    }
    private void loadTimes(){
        ArrayList<ShiftAvailability> availabilities =selectedAssistant.getWorkDays();
        for(int i = 0; i<14;i++){
            setupLoadTimes(i,availabilities);
        }
    }
    private void setupLoadTimes(int i,ArrayList<ShiftAvailability> availabilities){
        AssistantViewConSetup view = listDataEntry.get(i);
        Availability ava = availabilities.get((i % 2 != 0) ? 7+i/2 : i / 2 ).getAvailability();
        view.getStartHours().getValueFactory().setValue(ava.getStart()[0]);
        view.getStartMinutes().getValueFactory().setValue(ava.getStart()[1]);
        view.getEndHours().getValueFactory().setValue(ava.getEnd()[0]);
        view.getEndMinutes().getValueFactory().setValue(ava.getEnd()[1]);
    }
    private void saveLoadTimes(){
    ArrayList<ShiftAvailability> avail = selectedAssistant.getWorkDays();
    int i = 0;
    for(ShiftAvailability s : avail){
        AssistantViewConSetup con = listDataEntry.get((i < 7) ? i*2 : i +(i-13));
        s.getAvailability().setStartHours(con.getStartHours().getValue());
        s.getAvailability().setStartMinutes(con.getStartMinutes().getValue());
        s.getAvailability().setEndHours(con.getEndHours().getValue());
        s.getAvailability().setEndMinutes(con.getEndMinutes().getValue());
        i++;
    }

    }
    public void loadContract(){
    if(contractField.getValue().equals("HPP") || contractField.getValue().equals("HPP-Vlastní") ){
        workText.setText("Úvazek");
    }else{
        workText.setText("Počet smluvních hodin v měsíci");
    }
}
    private ArrayList<ArrayList<UUID>> savePreferred() throws IOException {
     ArrayList<ArrayList<UUID>> output = new ArrayList<ArrayList<UUID>>(Arrays.asList(new ArrayList<UUID>(),new ArrayList<UUID>(),new ArrayList<UUID>(),new ArrayList<UUID>()));
     for(ClientProfile cl : Database.loadClientProfiles().getFullClientList()){
        // ArrayList<RadioButton>
        List<Object> buttons = itemIndex.get(cl.getID()).subList(2,6);
        int i =0;
        for(Object rad : buttons) {
                if(((RadioButton) rad).isSelected() == true){
                    output.get(i).add(cl.getID());
                }
            i++;
        }
     }
     return output;
 }
    public void saveNewAssistant(ActionEvent actionEvent) throws IOException {
        saveNew();
    }
    private void genericAssistant() throws IOException {
        Assistant temp = new Assistant(UUID.randomUUID(), statusCheckBox.isSelected(),nameField.getText(), surnameField.getText(), (String) contractField.getValue(),Double.parseDouble(workField.getText()),overtimeCheck.isSelected(), comments.getText(),null, savePreferred(),emergencyAssistantCheck.isSelected(),isDriverCheck.isSelected());
        listOfA.getFullAssistantList().add(temp);
        Database.saveAssistant(temp);
        selectedAssistant = temp;
        ObservableList<Assistant> observLocationList = FXCollections.observableList(listOfA.getFullAssistantList());
        listViewofA.setItems(observLocationList);
        internalController.send("Assistant");
    }
    public void populateDaysInWeekTable(){
        ContextMenu emptyContextMenu = new ContextMenu();
        String[] days = {"Pondělí","Úterý","Středa","Čtvrtek","Pátek","Sobota","Neděle"};
        ArrayList<TextArea> allAreas = new ArrayList<>();
        ArrayList<GridPane> startsAndEnds = new ArrayList<GridPane>();
        daysInWeekGrid.getChildren().clear();
        for(int iRow=0; iRow<14;iRow = iRow+2){
            for(int iCol=0; iCol<2;iCol++){
                positionGrid(startsAndEnds,iRow,iCol,listDataEntry);
                TextArea dayArea = new TextArea();
                dayArea.setContextMenu(emptyContextMenu);
                dayArea.setEditable(false);
                allAreas.add(dayArea);
                GridPane.setConstraints(dayArea,iCol,iRow,1,1);
                String displayText;
                dayArea.setOnMouseClicked(this :: switchState);
                if(iCol==0){
                    displayText = days[iRow/2];
                }else{
                    displayText = "";
                }
                dayArea.setText(displayText);
                setDayState(dayArea,stateOfDays.get(iRow).getState());
            }
        }

        daysInWeekGrid.getChildren().addAll(allAreas);
        daysInWeekGrid.getChildren().addAll(startsAndEnds);
        setGridSize();

    }
    private void positionGrid(ArrayList<GridPane> ar, int iRow, int iCol, ArrayList<AssistantViewConSetup> completeList){
    AssistantViewConSetup temp = new AssistantViewConSetup();
        linkedViewConList.set(iRow+iCol,temp);
        completeList.add(temp);
        GridPane.setConstraints(temp.getLocalGrid(),iCol,iRow+1,1,1);
        ar.add(temp.getLocalGrid());
        for(Node n : temp.getItemList()){
            setupSpinnerListener((Spinner<Integer>) n,temp);
        }

    }
    private void setupSpinnerListener(Spinner<Integer> spiner, AssistantViewConSetup parent){
        spiner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                validateIntervalOverlaps(parent);
            }

        });
    }
    private void validateIntervalOverlaps(AssistantViewConSetup parent){
        AssistantViewConSetup past =linkedViewConList.previous(parent);
        AssistantViewConSetup next = linkedViewConList.next(parent);
        if(parent.getEndLocalTime().isAfter(next.getStartLocalTime())){
            next.getStartHours().getValueFactory().setValue(parent.getEndHours().getValue());
            next.getStartMinutes().getValueFactory().setValue(parent.getEndMinutes().getValue());
        }
        if(parent.getStartLocalTime().isBefore(past.getEndLocalTime())){
            past.getEndHours().getValueFactory().setValue(parent.getStartHours().getValue());
            past.getEndMinutes().getValueFactory().setValue(parent.getStartMinutes().getValue());
        }
    }

    public void setGridSize(){
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        daysInWeekGrid.getColumnConstraints().setAll(column1, column2);
        ArrayList<RowConstraints> rowConList = new ArrayList<>();
        for(int i=0;i<14;i++){
            RowConstraints rowC = new RowConstraints();
            rowConList.add(rowC);
            rowC.setPercentHeight(100.0/14);
        }
        daysInWeekGrid.getRowConstraints().setAll(rowConList);
    }
    private void setDayState(TextArea inputText, boolean day){

        if(!day){
            inputText.setStyle("-fx-control-inner-background:" +toHexString(Color.RED));
        }else{
            inputText.setStyle("-fx-control-inner-background:" +toHexString(Color.GREEN));
        }
    }
    private void refreshDays(){
    for(int i = 0; i < 14; i = i+2){
        TextArea text = (TextArea) daysInWeekGrid.getChildren().get(i);
        setDayState(text,selectedAssistant.getWorkDays().get(i/2).getState());
    }

    }
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    public void populateClietnOpinionInner(){
        clientOpinionGrid.getRowConstraints().clear();
        clientOpinionGrid.getChildren().clear();
        ListOfClientsProfiles lip = Database.loadClientProfiles();
        itemIndex = new HashMap<>();
        int citer = 0;
        for(ClientProfile clp : lip.getFullClientList()) {
            ArrayList<Object> templist = new ArrayList<Object>();
            GridPane grp = new GridPane();
            grp.getRowConstraints().clear();
            grp.setStyle("-fx-background-color:" +toHexString(Color.GRAY));
            grp.setStyle("-fx-border-color:" +toHexString(Color.BLACK));
            templist.add(grp);
            templist.add(new Text());
            ((Text) templist.get(1)).setText(clp.getName() +" "+ clp.getSurname());
            ToggleGroup group = new ToggleGroup();
            ArrayList<String> titles = new ArrayList<>(Arrays.asList("Neutrální", "Preferovaný", "Nežádoucí", "Nekompatibilní"));
            for(int i = 0; i<4;i++){
                RadioButton temp = new RadioButton(titles.get(i));
                if( i  == 0){
                    temp.setSelected(true);
                }
                temp.setToggleGroup(group);
                templist.add(temp);
            }
            int[] columnWidth = new int[]{20,20,20,20,20};
            int it = 1;
            for(int i : columnWidth){
                ColumnConstraints row = new ColumnConstraints();
                row.setPercentWidth(i);
                grp.getColumnConstraints().add(row);
                grp.setConstraints((Node) templist.get(it),it-1,0,1,1);
                grp.getChildren().add((Node) templist.get(it));
                grp.setValignment((Node) templist.get(it),VPos.CENTER);
                it++;
            }
            grp.getRowConstraints().add(new RowConstraints(){{setValignment(VPos.CENTER);}});
            clientOpinionGrid.getChildren().add(grp);
            clientOpinionGrid.setConstraints(grp,0,citer++);
            clientOpinionGrid.setMargin(grp, new Insets(0,10,0,10));
            itemIndex.put(clp.getID(),templist);
        }
    }
    public void populateClientOpinion() throws IOException {
        populateClietnOpinionInner();
        }
    public void refreshClientOpinion() throws IOException {
        Platform.runLater(() -> {
        populateClietnOpinionInner();
        });
    }
        private void loadClientOpinion(Assistant as){
            for(UUID rowID : itemIndex.keySet()){
                int i = 0;
                for(ArrayList<UUID> id : as.getClientPreference()){
                    if(id.contains(rowID)) {
                       RadioButton rad = (RadioButton) itemIndex.get(rowID).get(i+2);
                       rad.setSelected(true);
                }
                i++;
            }
        }

        }
        private void switchOthers(MouseEvent mouseEvent, ArrayList<Object> ar){
            ArrayList<CheckBox> checkBoxes = (ArrayList<CheckBox>) ar.get(5);
            ArrayList<CheckBox> copiedList = (ArrayList<CheckBox>) checkBoxes.stream()
                    .filter(element -> !element.equals(mouseEvent.getSource()))
                    .collect(Collectors.toList());
        }
    public void switchState(MouseEvent mouseEvent) {
        TextArea loadedArea = (TextArea) mouseEvent.getSource();
        int rowIndex = daysInWeekGrid.getRowIndex(loadedArea);
        int colIndex = daysInWeekGrid.getColumnIndex(loadedArea);
        if(colIndex == 0){
            if(selectedAssistant.getWorkDays().get(rowIndex/2).getState() == false){
                selectedAssistant.getWorkDays().get(rowIndex/2).setState(true);
                setDayState(loadedArea,true);
            }else{
                selectedAssistant.getWorkDays().get(rowIndex/2).setState(false);
                setDayState(loadedArea,false);
            }
        }else{
            if(selectedAssistant.getWorkDays().get(rowIndex/2+7).getState() == false){
                selectedAssistant.getWorkDays().get(rowIndex/2+7).setState(true);
                setDayState(loadedArea,true);
            }else{
                selectedAssistant.getWorkDays().get(rowIndex/2+7).setState(false);
                setDayState(loadedArea,false);
            }
        }
    }

    @Override
    public void updateScreen() {
    }

    @Override
    public void loadAndUpdateScreen() {
        try {
            refreshClientOpinion();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createVacation(ActionEvent actionEvent) {
    if(startDatePicker.getValue() != null && endDatePicker.getValue() != null){
        Vacation vac = new Vacation(startDatePicker.getValue(),endDatePicker.getValue(),vacationCountsWorkCheck.isSelected());
        listOfVacations.getTempVacation().add(vac);
        selectedVacation = vac;
        AssistantVacationManager.loadVacationList(listOfVacations, vacationList);
        Database.saveVacation(listOfVacations);
    }

    }

    public void removeVacation(ActionEvent actionEvent) {
    listOfVacations.getTempVacation().remove(selectedVacation);
    if(!listOfVacations.getTempVacation().isEmpty()){
        selectedVacation = listOfVacations.getTempVacation().getFirst();
        loadVacationAlg(selectedVacation);

    }
    AssistantVacationManager.loadVacationList(listOfVacations, vacationList);
    Database.saveVacation(listOfVacations);
    }

    public void loadVacation(MouseEvent mouseEvent) {
        selectedVacation = (Vacation) vacationList.getSelectionModel().getSelectedItem();
        if(selectedVacation != null){
            loadVacationAlg(selectedVacation);
        }

    }
    private void loadVacationAlg(Vacation vac){
        startDatePicker.setValue(selectedVacation.getStart());
        endDatePicker.setValue(selectedVacation.getEnd());
        vacationCountsWorkCheck.setSelected(selectedVacation.isRemovesWorkDays());
    }

    public void testWorkedHours(ActionEvent actionEvent) {
        System.out.println(WorkHoursCalcul.workDaysCalcul(Settings.getSettings().getCurrentYear(), Settings.getSettings().getCurrentMonth(), 7.5,selectedAssistant.getID(),selectedAssistant.getContractTime()));
    }

    @Override
    public void addToRequiredFields(Node item) {
        requiredNodes.add(item);
    }

    @Override
    public Object getRequiredFields(int index) {
       return requiredNodes.get(index);
    }

    @Override
    public ArrayList<Node> getRequiredFields() {
        return requiredNodes;
    }
    @Override
    boolean verifyRequired() {
    for(Node n : getRequiredFields()){
        if(n instanceof TextField){
            if(((TextField) n).getText().isEmpty()){
                return false;
            }
        } else if (n instanceof ChoiceBox<?>) {
          if(((ChoiceBox<?>) n).getValue() ==null){
              return false;
          }
        }
    }
    return true;
    }

    @Override
    void save() {
        if(verifyRequired()){
            selectedAssistant.setName(nameField.getText());
            selectedAssistant.setSurname(surnameField.getText());
            selectedAssistant.setActivityStatus(statusCheckBox.isSelected());
            selectedAssistant.setContractType((String) contractField.getValue());
            selectedAssistant.setLikesOvertime(overtimeCheck.isSelected());
            WorkedHoursCheck.check(Double.parseDouble(workField.getText()),selectedAssistant,workField);
            selectedAssistant.setComment(comments.getText());
            selectedAssistant.setWorkDays(stateOfDays);
            selectedAssistant.setDriver(isDriverCheck.isSelected());
            try {
                selectedAssistant.setClientPreference(savePreferred());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            saveLoadTimes();
            Database.saveAssistant(selectedAssistant);
            ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.getFullAssistantList());
            listViewofA.setItems(observAssistantList);
            internalController.send("Assistant");
        }
    }

    @Override
    void saveNew() {
        if(verifyRequired()){
                if(listOfA.getFullAssistantList().isEmpty()){
                    try {
                        genericAssistant();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                ArrayList<String> nameAndSurname = new ArrayList<>();
                for(Assistant loc: listOfA.getFullAssistantList()){
                    nameAndSurname.add(loc.getName() +" "+ loc.getSurname());}
                if(!( nameAndSurname.contains(nameField.getText() +" "+ surnameField.getText()))){
                    try {
                        genericAssistant();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    System.out.println("Asistent už existuje");
                }
        }
    }
}
