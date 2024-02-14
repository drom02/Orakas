package graphics.sorter.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphics.sorter.*;
import graphics.sorter.Structs.HumanCellFactory;
import graphics.sorter.Structs.ListOfClientsProfiles;
import graphics.sorter.Structs.ShiftTextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.converter.ColorConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import net.synedra.validatorfx.Check;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.MonthDay;
import java.util.*;
import java.util.stream.Collectors;

public class AssistantViewController {

    @FXML
    private  GridPane clientOpinionGrid;
    @FXML
    private ColumnConstraints parentGrid;
    @FXML
    private GridPane daysInWeekGrid;
    @FXML
    private ListView listViewofA;
    @FXML
    private CheckBox overtimeCheck;
    @FXML
    private CheckBox dayCheck;
    @FXML
    private CheckBox nightCheck;
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
    private ArrayList listOfAssist;
    private Assistant selectedAssistant;
    private int[] stateOfDays;
    private JsonManip jsoMap;
    private ListOfAssistants listOfA;
    private ArrayList<Control> assistantNodes;
    private HashMap<UUID, ArrayList<Object>> itemIndex;
    Settings set;
    public void deleteAssistant(MouseEvent mouseEvent) throws IOException {
        if(!(selectedAssistant == null)){
            listOfA.getAssistantList().remove(selectedAssistant);
            ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.assistantList);
            listViewofA.setItems(observAssistantList);
            for(Control n : assistantNodes){
                if (n instanceof TextArea) {
                    ((TextArea) n).clear();
                } else if (n instanceof TextField) {
                    ((TextField) n).clear();
                } else if (n instanceof CheckBox) {
                    ((CheckBox) n).setSelected(false);
                }
            }
            jsoMap.saveAssistantInfo(listOfA);
        }else{
            System.out.println("Vyberte asistenta k odstranění.");
        }

    }
   /*
   @TODO Add how to deal with somebody changing name and surname, check that it is still unique.
    */
public void saveAssistant(MouseEvent mouseEvent) throws IOException {
        selectedAssistant.setName(nameField.getText());
        selectedAssistant.setSurname(surnameField.getText());
        selectedAssistant.setContractType((String) contractField.getValue());
        selectedAssistant.setLikesOvertime(overtimeCheck.isSelected());
        selectedAssistant.setWorksOnlyDay(dayCheck.isSelected());
        selectedAssistant.setWorksOnlyNight(nightCheck.isSelected());
        selectedAssistant.setWorkTime(Double.parseDouble(workField.getText()));
        selectedAssistant.setComments(comments.getText());
        selectedAssistant.setWorkDays(stateOfDays);
        selectedAssistant.setClientPreference(savePreferred());
        jsoMap.saveAssistantInfo(listOfA);
        ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.assistantList);
        listViewofA.setItems(observAssistantList);

    }
    public void initialize() throws IOException {

        assistantNodes = new ArrayList<>(Arrays.asList(nameField, surnameField,overtimeCheck,dayCheck,nightCheck,workField,comments));
        contractField.getItems().addAll("HPP","DPP","DPČ");
        contractField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
                    loadContract();
                });
        listViewofA.setCellFactory(new HumanCellFactory());
        jsoMap= new JsonManip();
        set = jsoMap.loadSettings("E:\\JsonWriteTest\\");
         listOfA = jsoMap.loadAssistantInfo();
        listOfAssist = listOfA .getAssistantList();
        ObservableList<Assistant> observAssistantList = FXCollections.observableList(listOfA.assistantList);
        listViewofA.setItems(observAssistantList);
        populateClientOpinion();




    }
    private ListOfAssistants loadAssistantsJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[]  jsonData = Files.readAllBytes(Paths.get("E:\\JsonWriteTest\\test.json"));
        ListOfAssistants listOfA = objectMapper.readValue(jsonData, ListOfAssistants.class );
        listOfAssist = listOfA.assistantList;
        return listOfA;
    }

    public void switchPage(ActionEvent actionEvent) throws IOException {
        Scene scen = listViewofA.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Main-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);
    }

    public void loadAssistant(MouseEvent mouseEvent) {
        selectedAssistant = (Assistant) listViewofA.getSelectionModel().getSelectedItem();
    if(!(selectedAssistant== null)){
        nameField.setText(selectedAssistant.getName());
        surnameField.setText(selectedAssistant.getSurname());
        contractField.setValue(selectedAssistant.getContractType());
        overtimeCheck.setSelected(selectedAssistant.getLikesOvertime());
        dayCheck.setSelected(selectedAssistant.getWorksOnlyDay());
        nightCheck.setSelected(selectedAssistant.getWorksOnlyNight());
        loadContract();
        workField.setText(String.valueOf(selectedAssistant.getWorkTime()));
        comments.setText(selectedAssistant.getComments());
        stateOfDays = selectedAssistant.getWorkDays();
        populateDaysInWeekTable();
        loadClientOpinion(selectedAssistant);
    }

    }

    public void loadContract(){
    if(contractField.getValue().equals("HPP") ){
        workText.setText("Úvazek");
    }else{
        workText.setText("Počet smluvních hodin v měsíci");
    }
}
 private ArrayList<ArrayList<UUID>> savePreferred() throws IOException {
     ArrayList<ArrayList<UUID>> output = new ArrayList<ArrayList<UUID>>(Arrays.asList(new ArrayList<UUID>(),new ArrayList<UUID>(),new ArrayList<UUID>()));
     for(ClientProfile cl : jsoMap.loadClientProfileInfo().getClientList()){
        // ArrayList<RadioButton>
        List<Object> buttons = itemIndex.get(cl.getID()).subList(2,5);
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
        if(!(nameField.getText().isEmpty()) & !(surnameField.getText().isEmpty())){
            if(listOfA.getAssistantList().isEmpty()){
                listOfAssist.add(new Assistant(UUID.randomUUID(),nameField.getText(), surnameField.getText(), (String) contractField.getValue(),Double.parseDouble(workField.getText()),overtimeCheck.isSelected(), dayCheck.isSelected(), nightCheck.isSelected(), comments.getText(),stateOfDays, savePreferred()));
                jsoMap.saveAssistantInfo(listOfA);
                ObservableList<Assistant> observLocationList = FXCollections.observableList(listOfA.getAssistantList());
                listViewofA.setItems(observLocationList);
                System.out.println("No other assistants exist");
                return;

            }
            ArrayList<String> nameAndSurname = new ArrayList<>();
            for(Assistant loc: listOfA.getAssistantList()){
                nameAndSurname.add(loc.getName() +" "+ loc.getSurname());
            }
                if(!( nameAndSurname.contains(nameField.getText() +" "+ surnameField.getText()))){
                    listOfAssist.add(new Assistant(UUID.randomUUID(),nameField.getText(), surnameField.getText(), (String) contractField.getValue(),Double.parseDouble(workField.getText()),overtimeCheck.isSelected(), dayCheck.isSelected(), nightCheck.isSelected(), comments.getText(),stateOfDays, savePreferred()));
                    jsoMap.saveAssistantInfo(listOfA);
                    ObservableList<Assistant> observLocationList = FXCollections.observableList(listOfA.getAssistantList());
                    listViewofA.setItems(observLocationList);
                    System.out.println("FFS");

                }else{
                    System.out.println("Asistent už existuje");

                }


        }else{
            System.out.println("Vyplntě povinné údaje");
        }
    }
    public void populateDaysInWeekTable(){
        String[] days = {"Pondělí","Úterý","Středa","Čtvrtek","Pátek","Sobota","Neděle"};
        ArrayList<TextArea> allAreas = new ArrayList<>();
        for(int iRow=0; iRow<7;iRow++){
            for(int iCol=0; iCol<2;iCol++){
                TextArea dayArea = new TextArea();
                dayArea.setEditable(false);
                allAreas.add(dayArea);
                daysInWeekGrid.setConstraints(dayArea,iCol,iRow,1,1);
                String displayText;
                dayArea.setOnMouseClicked(this :: switchState);

                if(iCol==0){
                    displayText = days[iRow];
                }else{
                    displayText = "";
                }
                dayArea.setText(displayText);
                setDayState(dayArea,stateOfDays[iRow]);
            }
        }
        daysInWeekGrid.getChildren().addAll(allAreas);
        setGridSize();

    }
    public void setGridSize(){
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        daysInWeekGrid.getColumnConstraints().setAll(column1, column2);
        ArrayList<RowConstraints> rowConList = new ArrayList<>();
        for(int i=0;i<7;i++){
            RowConstraints rowC = new RowConstraints();
            rowConList.add(rowC);
            rowC.setPercentHeight(100/7);
        }
        daysInWeekGrid.getRowConstraints().setAll(rowConList);
    }
    private void setDayState(TextArea inputText, int day){

        if(day == 0){
            inputText.setStyle("-fx-control-inner-background:" +toHexString(Color.RED));
        }else{
            inputText.setStyle("-fx-control-inner-background:" +toHexString(Color.GREEN));
        }

    }
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }


    public void populateClientOpinion() throws IOException {
    clientOpinionGrid.getRowConstraints().clear();
    ListOfClientsProfiles lip = jsoMap.loadClientProfileInfo();
     itemIndex = new HashMap<>();
    int citer = 0;
        for(ClientProfile clp : lip.getClientList()) {
            ArrayList<Object> templist = new ArrayList();

            GridPane grp = new GridPane();
            grp.getRowConstraints().clear();
            grp.setStyle("-fx-background-color:" +toHexString(Color.GRAY));
            grp.setStyle("-fx-border-color:" +toHexString(Color.BLACK));
            templist.add(grp);
            templist.add(new Text());
            ((Text) templist.get(1)).setText(clp.getName() +" "+ clp.getSurname());
            ToggleGroup group = new ToggleGroup();
            ArrayList<String> titles = new ArrayList<>(Arrays.asList("Neutrální", "Preferovaný", "Nežádoucí"));
            for(int i = 0; i<3;i++){
                RadioButton temp = new RadioButton(titles.get(i));
                if( i  == 0){
                    temp.setSelected(true);
                }
                temp.setToggleGroup(group);
                templist.add(temp);
            }
           // ArrayList<CheckBox> listOfB = new ArrayList<>(Arrays.asList(new CheckBox(),new CheckBox(),new CheckBox()));
           // templist.add(listOfB);
           int[] columnWidth = new int[]{25,25,25,25};
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
           System.out.println(grp.getRowCount());
           clientOpinionGrid.setConstraints(grp,0,citer++);
            clientOpinionGrid.setMargin(grp, new Insets(0,10,0,10));
           // clientOpinionGrid.setValignment(grp,VPos.CENTER);
            //clientOpinionGrid.setHalignment(grp,HPos.CENTER);
            //clientOpinionGrid.getRowConstraints().add(new RowConstraints() {{ setPercentHeight(10);}});
            itemIndex.put(clp.getID(),templist);

        }
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
            if(selectedAssistant.getWorkDays()[rowIndex] ==0){
                selectedAssistant.getWorkDays()[rowIndex] = 1;
                setDayState(loadedArea,1);
            }else{
                selectedAssistant.getWorkDays()[rowIndex] = 0;
                setDayState(loadedArea,0);
            }
        }else{
            if(selectedAssistant.getWorkDays()[rowIndex+7] ==0){
                selectedAssistant.getWorkDays()[rowIndex+7] = 1;
                setDayState(loadedArea,1);
            }else{
                selectedAssistant.getWorkDays()[rowIndex+7] = 0;
                setDayState(loadedArea,0);
            }
        }

    System.out.println(rowIndex);

    }
}
