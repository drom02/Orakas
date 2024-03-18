package graphics.sorter.controllers;

import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import graphics.sorter.*;
import graphics.sorter.Structs.HumanCellFactory;
import graphics.sorter.Structs.AvailableAssistants;
import graphics.sorter.Structs.ListOfAssistants;
import graphics.sorter.Structs.ShiftTextArea;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import com.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;



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
    private ArrayList listOfAssist;
    private Month editedMonth;
    private Assistant selectedAssistant;
    private ArrayList<ArrayList<String>> dayArrayList = new ArrayList();
    private ArrayList<ArrayList<String>> nightArrayList = new ArrayList();
    private ArrayList<TextArea> areaList = new ArrayList<TextArea>();
    private ArrayList<TextFlow> titleList = new ArrayList<TextFlow>();
    private ArrayList<ArrayList> listOfAssistantLists = new ArrayList<>();
   private  ArrayList<ShiftTextArea> assistantsDayList = new ArrayList<ShiftTextArea>();
   private  ArrayList<ShiftTextArea> assistantsNightList = new ArrayList<ShiftTextArea>();
  // private JsonManip jsoMap= JsonManip.getJsonManip();
   private Settings settings;
   private AvailableAssistants AAGlobal;
   private HashMap<String,Assistant> mapOfAssistants= new HashMap<>();
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
            ShiftTextArea rowTitleAr = new ShiftTextArea();
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
                    rowTitleAr = new ShiftTextArea();
                    rowTitleAr.setContextMenu(emptyContextMenu);
                    rowTitleAr.setEditable(false);
                    rowTitleAr.getStyleClass().add(dayStyles[shift]);
                    inputText= "";
                    rowTitleAr.setText(inputText);
                    rowTitleAr.setType(shift);
                    rowTitleAr.setID(i);
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
    public void initialize() throws IOException {
        settings = Database.loadSettings();
        assistantList.setCellFactory(new HumanCellFactory());
        ListOfAssistants listOfA = Database.loadAssistants();
        listOfAssist = listOfA .getAssistantList();
        ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.getAssistantList());
        assistantList.setItems(observAssistantList);
        populateTable(Month.of(settings.getCurrentMonth()),settings.getCurrentYear());
        loadAvailableAssistants(Database.loadAssistantAvailability(settings.getCurrentYear(),settings.getCurrentMonth()));
        writeXSLX();
        Platform.runLater(() -> {
            GraphicalFunctions.screenResizing(mainPane,mainGrid);

        });
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
    Will switch window to main window.
     */
    public void switchPageToMain(ActionEvent actionEvent) throws IOException {
       Scene scen = TestScrollPane.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("Main-view.fxml"));
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
                    output = output +a.getName() +" "+ a.getSurname()+"\n";
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
                    output = output  +a.getName() +" "+ a.getSurname()+"\n";
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
    private void purgeText(){
        for(ArrayList<ShiftTextArea> l : listOfAssistantLists){
            for(ShiftTextArea s : l){
                s.clear();
            }
        }
    }
    public void loadAvailableAssistants(AvailableAssistants av) throws IOException {
        purgeText();
        int i =0;
        ArrayList<ShiftTextArea> tempListDay = listOfAssistantLists.get(0);
       // AvailableAssistants AAGlobal = null;
        AAGlobal = av;
        ArrayList<ArrayList<Assistant>> listDays = AAGlobal.getAvailableAssistantsAtDays();
        for(ShiftTextArea arList: tempListDay ){
                arList.setAvailableAssistants(listDays.get(i));
                String output;
                for(Assistant as: listDays.get(i)){
                     output = arList.getText();
                    arList.setText(output  + as.getName() +" "+ as.getSurname()+"\n");
                }
                i++;

        }
        i = 0;
        ArrayList<ShiftTextArea> tempListNight = listOfAssistantLists.get(1);
        //AvailableAssistants avAsN = jsom.loadAvailableAssistantInfo(settings);
        ArrayList<ArrayList<Assistant>> listNights = AAGlobal.getAvailableAssistantsAtNights();
        for(ShiftTextArea sText: tempListNight ){
                sText.setAvailableAssistants(listNights.get(i));
            String output;
            for(Assistant as: listNights.get(i)){
                output = sText.getText();
                sText.setText(output + as.getName() +" "+ as.getSurname()+"\n");
            }
                i++;
        }
    }
    public void generateNewMonthsAssistants() throws IOException {

        ListOfAssistants listOfA = Database.loadAssistants();
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
        Database.saveAssistantAvailability(settings.getCurrentYear(),settings.getCurrentMonth(),availableAssistants);
    }
    /*
    Will save current state to json file.
    //TODO better save system
     */
    public void saveCurrentState() throws IOException {
       // JsonManip jsom = JsonManip.getJsonManip();
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
          Database.saveAssistantAvailability(settings.getCurrentYear(),settings.getCurrentMonth(),availableAssistants);
    }
    public void generateEmptyState() throws IOException {
        JsonManip jsom = JsonManip.getJsonManip();
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
        Database.saveAssistantAvailability(settings.getCurrentYear(),settings.getCurrentMonth(),availableAssistants);
    }
    private AvailableAssistants loadExcel(String path) throws IOException {
       ArrayList<String> acceptableInputs = prepareAcceptableInputs();
        settings = Database.loadSettings();
        AvailableAssistants out = new AvailableAssistants();
        ArrayList<ArrayList<Assistant>> dayList = new ArrayList<ArrayList<Assistant>>();
        ArrayList<ArrayList<Assistant>>  nightList = new ArrayList<ArrayList<Assistant>>();
        try (FileInputStream fileInputStream = new FileInputStream(new File(path));
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheet("availableAssistants");
            for(int i =1; i < 3;i++){
                Row editedRow = sheet.getRow(i);
                for(int c = 0;c< Month.of(settings.getCurrentMonth()).length(Year.isLeap(settings.getCurrentYear()));c++){
                    Cell editedCell = editedRow.getCell(c);
                    if(i==1){
                        dayList.add(ExcelOutput.parseCell(acceptableInputs,editedCell, mapOfAssistants));
                    }else{
                        nightList.add(ExcelOutput.parseCell(acceptableInputs,editedCell,mapOfAssistants));
                    }
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