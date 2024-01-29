package graphics.sorter.controllers;

import graphics.sorter.*;
import graphics.sorter.Structs.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;

public class TestController {
    @FXML
    private ScrollPane TestScrollPane;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    private Month editedMonth;
    private ArrayList<TextArea> nightList = new ArrayList<TextArea>();
    private ArrayList<TextArea> dayList = new ArrayList<TextArea>();
    JsonManip jsom = new JsonManip();
    public void populateView(ListOfClients LiCcl){
        ListOfClientMonths LiClMo = new ListOfClientMonths();
        for(Client cl : LiCcl.getClientList()){
            LiClMo.getListOfClientMonths().add(cl.getClientsMonth());
        }
        //vytahnout y klientu m2s9c2
        editedMonth = Month.DECEMBER;
        ArrayList<TextArea> areaList = new ArrayList<TextArea>();
        ArrayList<TextArea> titleList = new ArrayList<TextArea>();

        int i = 0;
        /*
        Vytvoří nadpisy pro jednotlivé dny
         */
        while (i <= editedMonth.length(false)){
            String inputText;
            if(i == 0){
                inputText = "Jméno Klienta";
            }else{
                 inputText = i + "."+ "12"+ "."+"2023";
            }
            TextArea newTextArea = new TextArea(inputText);
            titleList.add(newTextArea);
            i++;
        }
        i = 0;
        GridPane grid = new GridPane();
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
            for (ClientDay cl : clm.getClientDaysInMonth()){
                String inputText;
                TextArea dayTextAr = new TextArea();
                if(i==0){
                    inputText = LiCcl.getClientList().get(clientIter).getName() + " " + LiCcl.getClientList().get(clientIter).getSurname();
                    dayTextAr.setEditable(false);
                    dayTextAr.setText(inputText);
                    areaList.add(dayTextAr);
                    dayTextAr.setPrefSize(250,100);
                    grid.setConstraints(dayTextAr,i,clienMothIter,2,1);
                    i = i+2;
                }else{
                    if(getAssistantOfDay(cl) == null){
                        inputText= "Day" +"\n" + getAssistantOfDay(cl);
                    }else{
                        inputText= "Day" +"\n" + getAssistantOfDay(cl).getName() +" "+getAssistantOfDay(cl).getSurname();
                    }

                    dayTextAr.setText(inputText);
                    areaList.add(dayTextAr);
                    dayList.add(dayTextAr);
                    dayTextAr.setPrefSize(250,100);
                    grid.setConstraints(dayTextAr,i,clienMothIter,1,1);
                    i = i+2;
                }


            }
            i=0;
            for (ClientDay cl : clm.getClientNightsInMonth()){
                String inputText;
                TextArea nightTextAr = new TextArea();
                if(i==0){
                    inputText = LiCcl.getClientList().get(clientIter).getName() + " " + LiCcl.getClientList().get(clientIter).getSurname();
                    nightTextAr.setEditable(false);
                    nightTextAr.setText(inputText);
                    areaList.add(nightTextAr);
                    nightList.add(nightTextAr);
                    nightTextAr.setPrefSize(250,100);
                    grid.setConstraints(nightTextAr,i,clienMothIter+1,2,1);
                    /*
                    TextArea ar2 = new TextArea();
                    inputText = clm.getClientOwningMonth().getName() + " " + clm.getClientOwningMonth().getSurname();
                    ar2.setEditable(false);
                    ar2.setText(inputText);
                    areaList.add(ar2);
                    ar2.setPrefSize(250,100);
                    grid.setConstraints(ar2,i+2,clienMothIter+1,1,1);
                     */
                    i = i+3;
                }else{
                    inputText= "Night";
                    nightTextAr.setText(inputText);
                    areaList.add(nightTextAr);
                    nightTextAr.setPrefSize(250,100);
                    grid.setConstraints(nightTextAr,i,clienMothIter+1,2,1);
                    i = i+2;
                }


            }
            clienMothIter = clienMothIter+2;
            clientIter++;
        }

        grid.getChildren().addAll(areaList);
        TestScrollPane.setContent(grid);
    }
    public Assistant getAssistantOfDay(ClientDay clDay){
        if(!clDay.getDayIntervalList().isEmpty()){
            return clDay.getDayIntervalList().get(0).getOverseeingAssistant();
        }else{
            return  null;
        }


    }
    public void initialize() throws IOException {

        populateView(jsom.loadClientInfo(12,2024));
        AttachAssistants attachAssistants = new AttachAssistants();
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
        AvailableAssistants avAs = jsom.loadAvailableAssistantInfo();
        ListOfClients listOfClients = jsom.loadClientInfo(12,2024);
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
                }
            }
        }

        jsom.saveClientInfo(listOfClients.convertToListOfClientProfiles());
        populateView(listOfClients);
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
}