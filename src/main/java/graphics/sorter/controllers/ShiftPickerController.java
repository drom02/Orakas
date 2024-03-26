package graphics.sorter.controllers;

import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import graphics.sorter.*;
import graphics.sorter.AssistantAvailability.AssistantAvailability;
import graphics.sorter.AssistantAvailability.Availability;
import graphics.sorter.AssistantAvailability.ShiftFlow;
import graphics.sorter.AssistantAvailability.ShiftGrid;
import graphics.sorter.JavaFXCustomComponents.AssistantViewConSetup;
import graphics.sorter.JavaFXCustomComponents.ToggleButton;
import graphics.sorter.Structs.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.opencsv.ICSVWriter.*;

/*
Controller for shift picker window. It is used to select when are assistants available. Select name from the list and
left click to field to add the assistant for specific shift. Right click to remove.
 */
public class ShiftPickerController implements ControllerInterface{

    //region graphical components
    @FXML
    private Pane mainPane;
    @FXML
    private GridPane mainGrid;
    @FXML
    private ScrollPane TestScrollPane;
    @FXML
    private Label welcomeText;
    @FXML
    private ListView assistantList;
    //endregion
    //region variables
    private ArrayList<Assistant> listOfAssist;
    private Month editedMonth;
    private Assistant selectedAssistant;
    private ArrayList<ArrayList<String>> dayArrayList = new ArrayList();
    private ArrayList<ArrayList<String>> nightArrayList = new ArrayList();
    private ArrayList<TextArea> areaList = new ArrayList<TextArea>();
    private ArrayList<TextFlow> titleList = new ArrayList<TextFlow>();
    private ArrayList<ArrayList> listOfAssistantLists = new ArrayList<>();
    private  ArrayList<ShiftGrid > assistantsDayList = new ArrayList<ShiftGrid >();
    private  ArrayList<ShiftGrid > assistantsNightList = new ArrayList<ShiftGrid >();
    private Settings settings;
    private AvailableAssistants AAGlobal;
    private HashMap<String,Assistant> mapOfAssistants= new HashMap<>();
    private HashMap<UUID, AssistantAvailability>  dayAvIndex= new HashMap<UUID, AssistantAvailability>();
    private HashMap<UUID, AssistantAvailability>  nightAvIndex= new HashMap<UUID, AssistantAvailability>();
    private HashMap<UUID, Assistant>  assistantIndex= new HashMap<UUID, Assistant>();
    private AssistantViewConSetup vac;
    private ShiftFlow selectedFlow;
    private ToggleButton tog;
   //endregion


   /*
   Method will generate empty table with only column and row titles.
    */
    public void populateTable(Month moth, int year){
        ContextMenu emptyContextMenu = new ContextMenu();
        editedMonth = moth;
        listOfAssistantLists.add(assistantsDayList);
        listOfAssistantLists.add(assistantsNightList);
        ArrayList<TextFlow> rowTitles = new ArrayList<TextFlow>();
        int i = 0;
        int clienMothIter = 1;
        /*
        Vytvoří nadpisy pro jednotlivé dny
         */
        GridPane grid = new GridPane();
        while (i <= editedMonth.length(false)){
            String inputText;

            String[] titles = {"Date", "Day shift", "Night shift"};
            String[] styles = {"day-title-card", "day-title-card-stack", "night-title-card-stack"};
            String[] dayStyles = {"day-Area", "night-Area"};
            if(i==0){
                int row = 0;
                for (String st : titles){
                    TextFlow rowTitleFlow = new TextFlow();
                    inputText = titles[row];
                    if(row == 0){
                        rowTitleFlow.getStyleClass().add("title-area-flow");
                    }
                    rowTitleFlow.getStyleClass().add(styles[row]);
                    rowTitleFlow.getChildren().add(new Text(inputText));
                    rowTitles.add(rowTitleFlow);
                    rowTitleFlow.setPrefSize(250,50);
                    grid.setConstraints(rowTitleFlow,0,row,1,1);
                    row++;
                }
                i++;
            }else{
                int row = 0;
                TextFlow rowTitleFlow = new TextFlow();
                LocalDate date = LocalDate.of(year, editedMonth.getValue(), i);

                inputText = i + "."+ moth.getValue()+ "."+year +"\n" + getNameOfDay(date);
                rowTitleFlow .getChildren().add(new Text(inputText));
                titleList.add(rowTitleFlow );
                rowTitleFlow.getStyleClass().add("title-area-flow");
                rowTitleFlow.setTextAlignment(TextAlignment.CENTER);
                rowTitleFlow.setPrefSize(250,50);
                grid.setConstraints(rowTitleFlow,i,row,1,1);
                int shift = 0;
                for (ArrayList arList : listOfAssistantLists) {
                    ShiftGrid rowTitleAr = new ShiftGrid (shift == 0);
                    rowTitleAr.getStyleClass().add(dayStyles[shift]);
                   // rowTitleAr.setContextMenu(emptyContextMenu);
                   // rowTitleAr.setEditable(false);
                    //rowTitleAr.getStyleClass().add(dayStyles[shift]);
                    inputText= "";
                   //w rowTitleAr.setText(inputText);
                    //rowTitleAr.displayContent(assistantIndex);
                    //rowTitleAr.setType(shift);
                    //rowTitleAr.setID(i);
                    rowTitleAr.setOnMouseClicked(this :: onTextAreaClicked);
                    arList.add(rowTitleAr);
                    rowTitleAr.setPrefSize(250,410);
                    grid.setConstraints(rowTitleAr,i,row+1,1,1);
                    row++;
                    shift++;
                }
                i++;
            }
        }
        grid.getChildren().addAll(titleList);
        grid.getChildren().addAll(areaList);
        grid.getChildren().addAll(assistantsNightList);
        grid.getChildren().addAll(assistantsDayList);
        grid.getChildren().addAll(rowTitles);
        TestScrollPane.setContent(grid);
    }
    /*
    Method initialize
    */
    @Override
    public void updateScreen()  {
        ListOfAssistants listOfA = null;
        listOfA = Database.loadAssistants();
        listOfAssist = listOfA .getAssistantList();
        ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.getAssistantList());
        assistantList.setItems(observAssistantList);
    }
    private void prepareIndexes(ArrayList<Assistant> aList){
        for(Assistant a : aList){
            dayAvIndex.put(a.getID(),new AssistantAvailability(a.getID(),new Availability(settings.getDeftStart()[0],settings.getDeftStart()[1],settings.getDefEnd()[0],settings.getDefEnd()[1])));
            nightAvIndex.put(a.getID(),new AssistantAvailability(a.getID(),new Availability(settings.getDefEnd()[0],settings.getDefEnd()[1],settings.getDeftStart()[0],settings.getDeftStart()[1])));
            assistantIndex.put(a.getID(),a);
        }
    }
    public void initialize() throws IOException {
        settings = Settings.getSettings();
        assistantList.setCellFactory(new HumanCellFactory());
        ListOfAssistants listOfA = Database.loadAssistants();
        listOfAssist = listOfA .getAssistantList();
        prepareIndexes(listOfAssist);
        ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.getAssistantList());
        assistantList.setItems(observAssistantList);
        populateTable(Month.of(settings.getCurrentMonth()),settings.getCurrentYear());
        loadAvailableAssistants(Database.loadAssistantAvailability(settings.getCurrentYear(),settings.getCurrentMonth()));
        //writeXSLX();
        setupViewCon();
        Platform.runLater(() -> {
            GraphicalFunctions.screenResizing(mainPane,mainGrid);
        });
    }
    private void setupViewCon(){
        vac = new AssistantViewConSetup();
        tog = new ToggleButton("On","Off");
        GridPane.setValignment(tog.getLocalGrid(), VPos.BOTTOM);
        GridPane.setMargin(vac.getLocalGrid(), new Insets(5));
        GridPane.setMargin(tog.getLocalGrid(), new Insets(0,1,5,1));
        GridPane.setConstraints(vac.getLocalGrid(),0,2);
        GridPane.setConstraints(tog.getLocalGrid(),0,2);
        mainGrid.getChildren().add(vac.getLocalGrid());
        mainGrid.getChildren().add(tog.getLocalGrid());
    }
    private String getNameOfDay(LocalDate date){
        DayOfWeek dayValue = date.getDayOfWeek();
        String nameOfDay = dayValue.getDisplayName(TextStyle.FULL, new Locale("cs", "CZ"));
        if(nameOfDay !=null){
            return nameOfDay.substring(0,1).toUpperCase() +nameOfDay.substring(1);
        }
        return null;
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
      // ShiftFlow loadedArea = (ShiftFlow) mouseEvent.getSource();
       ShiftGrid parentGrid = (ShiftGrid) mouseEvent.getSource();
        MouseButton button = mouseEvent.getButton();
        if(!tog.isState()){
            return;
        }
        if(button== MouseButton.PRIMARY){
            if(selectedAssistant != null && !parentGrid.getDisplayedAssistants().contains(selectedAssistant.getID())){
                if(assistantsDayList.contains(parentGrid)){
                    addAssistantToDisplay(new AssistantAvailability(selectedAssistant.getID(),new Availability(8,30,20,30)),parentGrid);
                }else{
                    addAssistantToDisplay(new AssistantAvailability(selectedAssistant.getID(),new Availability(20,30,8,30)),parentGrid);
                }

            }
        }else{
            if(selectedAssistant != null && parentGrid.getDisplayedAssistants().contains(selectedAssistant.getID())){
                removeAssistantToDisplay(selectedAssistant.getID(),parentGrid);
               // loadedArea.setText(oldText.replace("," +selectedAssistant.getName() +" "+ selectedAssistant.getSurname(),""));
            }
        }
    }
    private void saveChangesToShift(){
        boolean isDay = assistantsDayList.contains(selectedFlow.getParent());
        if(vac.validate(isDay)){
            Availability av = selectedFlow.getAssistantAvailability().getAvailability();
            av.setStartHours(vac.getStartHours().getValue());
            av.setStartMinutes(vac.getStartMinutes().getValue());
            av.setEndHours(vac.getEndHours().getValue());
            av.setEndMinutes(vac.getEndMinutes().getValue());
        }
    }
    private void onFlowAreaClicked(MouseEvent mouseEvent) {
        if(!tog.isState()){
            if(selectedFlow != null ){
                saveChangesToShift();
                selectedFlow.displayContent(assistantIndex);
                selectedFlow.getStyleClass().remove("selected-ShiftFlow");
            }
            selectedFlow = (ShiftFlow) mouseEvent.getSource();
            selectedFlow.getStyleClass().add("selected-ShiftFlow");
            setupShiftFlowAvailabilityValues(selectedFlow);
        }

    }
    /*
    Will populate the table with latest saved state.
    //Todo option to select save to load.
     */
    private void removeFromAssistantAVAr(ArrayList<AssistantAvailability> list, UUID id){
        for(AssistantAvailability av : list){
            if(av.getAssistant().equals(id)){
                list.remove(av);
                break;
            }
        }
    }
    private String getNamefFromID(UUID id){
       Assistant a =  assistantIndex.get(id);

       return a.getName() + " " + a.getSurname();
    }
    private void purgeText(){
        for(ArrayList<ShiftGrid> l : listOfAssistantLists){
            for(ShiftGrid s : l){
                s.getChildren().clear();
            }
        }
    }
    public void loadAvailableAssistants(AvailableAssistants av) throws IOException {
        purgeText();
        int i =0;
        ArrayList<ShiftGrid> tempListDay = listOfAssistantLists.get(0);
        AAGlobal = av;
        ArrayList<ArrayList<AssistantAvailability>> listDays = AAGlobal.getAvailableAssistantsAtDays();
        for(ShiftGrid arList: tempListDay ){
            ArrayList<ShiftFlow> flows = new ArrayList<>();
            int iter = 0;
            for(AssistantAvailability a :listDays.get(i)){
                addAssistantToDisplay(flows,a,arList,iter);
                iter++;
            }
            arList.getChildren().addAll(flows);
        i++;
        }
        i = 0;
        ArrayList<ShiftGrid> tempListNight = listOfAssistantLists.get(1);
        ArrayList<ArrayList<AssistantAvailability>> listNights = AAGlobal.getAvailableAssistantsAtNights();
        for(ShiftGrid arList: tempListNight ){
            ArrayList<ShiftFlow> flows = new ArrayList<>();
            int iter = 0;
            for(AssistantAvailability a :listNights.get(i)){
            addAssistantToDisplay(flows,a,arList,iter);
                iter++;
            }
            arList.getChildren().addAll(flows);
            i++;

        }
    }
    private void addAssistantToDisplay(ArrayList<ShiftFlow> flows,AssistantAvailability a, ShiftGrid arList, int iter ){
        ShiftFlow temp = new ShiftFlow(a);
        temp.displayContent(assistantIndex);
        GridPane.setConstraints(temp,0,iter,1,1);
        flows.add(temp);
        arList.addAssistant(a.getAssistant());
        temp.setOnMouseClicked(this :: onFlowAreaClicked);

    }
    private void addAssistantToDisplay(AssistantAvailability a, ShiftGrid arList){
        ShiftFlow temp = new ShiftFlow(a);
        temp.displayContent(assistantIndex);
        GridPane.setConstraints(temp,0,arList.getDisplayedAssistants().size()+1,1,1);
        arList.addAssistant(a.getAssistant());
        arList.getChildren().add(temp);
        temp.setOnMouseClicked(this :: onFlowAreaClicked);
    }


    private void setupShiftFlowAvailabilityValues(ShiftFlow flow){
        Availability av = flow.getAssistantAvailability().getAvailability();
        vac.getStartHours().getValueFactory().setValue(av.getStart()[0]);
        vac.getStartMinutes().getValueFactory().setValue(av.getStart()[1]);
        vac.getEndHours().getValueFactory().setValue(av.getEnd()[0]);
        vac.getEndMinutes().getValueFactory().setValue(av.getEnd()[1]);

    }

    private void removeAssistantToDisplay(UUID a, ShiftGrid arList){
        int i = arList.getDisplayedAssistants().indexOf(a);
        if (i != -1) {
            arList.getChildren().remove(i);
            arList.getDisplayedAssistants().remove(a);
            for (int i2 = 0; i2 < arList.getChildren().size(); i2++) {
                GridPane.setConstraints(arList.getChildren().get(i2), 0, i2);
            }
        }
    }
    public void generateNewMonthsAssistants() throws IOException {
        int year = settings.getCurrentYear();
        int month = settings.getCurrentMonth();
        ListOfAssistants listOfA = Database.loadAssistants();
        AvailableAssistants availableAssistants = new AvailableAssistants();
        ArrayList<ArrayList<AssistantAvailability>> dayList = new ArrayList<>(31);
        ArrayList<ArrayList<AssistantAvailability>> nightList = new ArrayList<>(31);
        for(int i =0; i < Month.of(month).length(Year.isLeap(year)); i++){
            ArrayList<AssistantAvailability> inputDayList = new ArrayList<>();
            ArrayList<AssistantAvailability> inputNightList = new ArrayList<>();
            dayList.add(inputDayList);
            nightList.add(inputNightList);
            for(Assistant asis : listOfA.getAssistantList()){
                if(asis.getWorkDays().get(LocalDate.of(year, month, i + 1).getDayOfWeek().getValue()).getState()){
                    inputDayList.add(new AssistantAvailability(asis.getID(),asis.getWorkDays().get(LocalDate.of(year, month, i + 1).getDayOfWeek().getValue()).getAvailability()));
                }
                if(asis.getWorkDays().get(LocalDate.of(year, month, i + 1).getDayOfWeek().getValue() + 6).getState()){
                    inputNightList.add(new AssistantAvailability(asis.getID(),asis.getWorkDays().get(LocalDate.of(year, month, i + 1).getDayOfWeek().getValue()+ 6).getAvailability()));
                }


            }


        }
        availableAssistants.setAvailableAssistantsAtDays(dayList);
        availableAssistants.setAvailableAssistantsAtNights(nightList);
        Database.saveAssistantAvailability(year, month, availableAssistants);
    }
    /*
    Will save current state to json file.
    //TODO better save system
     */
    public void saveCurrentState() throws IOException {
       // JsonManip jsom = JsonManip.getJsonManip();
        AvailableAssistants availableAssistants = new AvailableAssistants();
        ArrayList<ArrayList<AssistantAvailability>> dayList = new ArrayList<>();
        ArrayList<ArrayList<AssistantAvailability>> nightList = new ArrayList<>();
        int shift = 0;
            for(ArrayList<ShiftGrid> arlist: listOfAssistantLists){
                for(ShiftGrid arl: arlist){
                    if(arl.isDay() ==true){
                        ArrayList<AssistantAvailability> temp = new ArrayList<>();
                        for(Node n : arl.getChildren()){
                            if(n instanceof ShiftFlow){
                                temp.add( ((ShiftFlow) n).getAssistantAvailability());
                            }
                        }
                        dayList.add(temp);
                    }else{
                        ArrayList<AssistantAvailability> temp = new ArrayList<>();
                        for(Node n : arl.getChildren()){
                            if(n instanceof ShiftFlow){
                                temp.add( ((ShiftFlow) n).getAssistantAvailability());
                            }
                        }
                        nightList.add(temp);
                    }
                }
            }
            if(selectedFlow != null){
                saveChangesToShift();
                selectedFlow.displayContent(assistantIndex);
            }


        availableAssistants.setAvailableAssistantsAtDays(dayList);
        availableAssistants.setAvailableAssistantsAtNights(nightList);
        Database.saveAssistantAvailability(settings.getCurrentYear(),settings.getCurrentMonth(),availableAssistants);
    }
    public void generateEmptyState() throws IOException {
        JsonManip jsom = JsonManip.getJsonManip();
        AvailableAssistants availableAssistants = new AvailableAssistants();
        ArrayList<ArrayList<AssistantAvailability>> dayList = new ArrayList<>();
        ArrayList<ArrayList<AssistantAvailability>> nightList = new ArrayList<>();
        int shift = 0;

            for(int i =0; i < Month.of(settings.getCurrentMonth()).length(Year.isLeap(settings.getCurrentYear())); i++){
                    dayList.add(new ArrayList<>());
                    nightList.add(new ArrayList<>());

        }

        availableAssistants.setAvailableAssistantsAtDays(dayList);
        availableAssistants.setAvailableAssistantsAtNights(nightList);
        Database.saveAssistantAvailability(settings.getCurrentYear(),settings.getCurrentMonth(),availableAssistants);
    }
    private AvailableAssistants loadExcel(String path) throws IOException {
       ArrayList<String> acceptableInputs = prepareAcceptableInputs();
        settings = Settings.getSettings();
        AvailableAssistants out = new AvailableAssistants();
        ArrayList<ArrayList<AssistantAvailability>> dayList = new ArrayList<ArrayList<AssistantAvailability>>();
        ArrayList<ArrayList<AssistantAvailability>>  nightList = new ArrayList<ArrayList<AssistantAvailability>>();
        try (FileInputStream fileInputStream = new FileInputStream(new File(path));
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheet("availableAssistants");
            for(int i =1; i < 3;i++){
                Row editedRow = sheet.getRow(i);
                for(int c = 0;c< Month.of(settings.getCurrentMonth()).length(Year.isLeap(settings.getCurrentYear()));c++){
                    Cell editedCell = editedRow.getCell(c);
                    /*
                    TODO redo
                    if(i==1){
                        dayList.add(ExcelOutput.parseCell(acceptableInputs,editedCell, mapOfAssistants));
                    }else{
                        nightList.add(ExcelOutput.parseCell(acceptableInputs,editedCell,mapOfAssistants));
                    }
                     */

                }
            }
            out.setAvailableAssistantsAtDays(dayList);
            out.setAvailableAssistantsAtNights(nightList);
            loadAvailableAssistants(out);
            return out;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void loadCsv(ActionEvent actionEvent) throws IOException, CsvException {
       // writeCSV();
        //writeXSLX();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("csv files (*.csv)","*.csv", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        String st = selectedFile.getAbsolutePath();
        loadExcel(st);
        /**/
        /*
         InputStreamReader filereader = new InputStreamReader(new FileInputStream(st), StandardCharsets.UTF_8);
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(0)
                .build();
        ArrayList<ArrayList<ArrayList<String>>>  input = new ArrayList<ArrayList<ArrayList<String>>>();

        XSSFReader xssfReader = new XSSFReader();

        List<String[]> allData =
        int shift = 0;
        for(String[] ste :allData){
            ArrayList shiftList = new ArrayList<>();
            input.add(shiftList);
            ArrayList ar = input.get(shift) ;
            int day = 0;
            for(String stem : ste){
                ArrayList<String > dayList = new ArrayList<>();
                shiftList.add(dayList);
                String[] parts = stem.split(",");
                int personIter = 0;
                for(String sta : parts){
                    dayList.add(sta);
                    personIter++;System.out.println(sta);
                }

                day++;
            }
            shift++;
        }
        System.out.println(input);
        System.out.println(input);

         */
        // create csvReader object and skip first Line
    }
    private void writeCSV() throws IOException {
       int year = settings.getCurrentYear();
       int month = settings.getCurrentMonth();
       File fil = new File("C:\\Users\\matej\\VSE\\delete\\test.csv");
       fil.createNewFile();
        ArrayList<String> titles = new ArrayList<String>();
        String first = "Směna";
        String[] line2 = new String[]{"Denni smena"};
        String[] line3 = new String[]{"Nocni smena"};
        titles.add(first);
        int length = Month.of(settings.getCurrentMonth()).length(Year.isLeap(year));
       try( FileOutputStream fos = new FileOutputStream("C:\\Users\\matej\\VSE\\delete\\test.csv"); CSVWriter writer = new CSVWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8), ';' ,DEFAULT_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER,DEFAULT_LINE_END)){
           fos.write(0xEF);
           fos.write(0xBB);
           fos.write(0xBF);
           for(int i =1; i<=length;i++){
               titles.add(new String (1+"."+month+"."+year));
           }
           String[] st = new String[titles.size()];
           st =   titles.toArray(st);
           writer.writeNext(st);
           writer.writeNext(line2);
           writer.writeNext(line3);
       }
    }
    private void loadXSLX(){

    }
    private void writeXSLX(){
        CompletableFuture<Void> future = CompletableFuture.runAsync(()->
        {Database.saveAssistantAvailability(AAGlobal.getYear(),AAGlobal.getMonth(),AAGlobal);});
        XSSFWorkbook workbook = ExcelOutput.convert(AAGlobal);
        try (FileOutputStream fos = new FileOutputStream("C:\\Users\\matej\\VSE\\delete\\"+"available assistants "+ AAGlobal.getYear()+"."+AAGlobal.getMonth()+".xlsx")) {
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private ArrayList prepareAcceptableInputs() {
        mapOfAssistants.clear();
        ArrayList<String> acceptable = new ArrayList<>();
        ListOfAssistants lisfa = Database.loadAssistants();
        for(Assistant a: lisfa.getAssistantList()){
            String nameSurname = a.getName()+a.getSurname();
            nameSurname = nameSurname.replaceAll(" ","");
            String surname = a.getSurname();
            surname = surname.replaceAll(" ","");
            mapOfAssistants.put(nameSurname,a);
            mapOfAssistants.put(surname,a);
            acceptable.add(nameSurname);
            acceptable.add(surname);
        }
        return  acceptable;
    }


}