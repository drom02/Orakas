package graphics.sorter.controllers;

import graphics.sorter.Client;
import graphics.sorter.HelloApplication;
import graphics.sorter.Structs.ClientDay;
import graphics.sorter.Structs.ClientMonth;
import graphics.sorter.Structs.ListOfClientMonths;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
    TextArea textArea1 = new TextArea("Enter your name");
    TextArea textArea2 = new TextArea("Enter your nameeeeeeeeeeeeeeeeee");

    public void test(){
        editedMonth = Month.DECEMBER;
        ArrayList<TextArea> areaList = new ArrayList<TextArea>();
        ArrayList<TextArea> dayList = new ArrayList<TextArea>();
        int i = 0;
        /*
        Vytvoří nadpisy pro jednotlivé dny
         */
        while (i < editedMonth.length(false)){
            String inputText;
            if(i == 0){
                inputText = "Jméno Klienta";
            }else{
                 inputText = i+1 + "."+ "12"+ "."+"2023";
            }
            TextArea newTextArea = new TextArea(inputText);
            dayList.add(newTextArea);
            i++;
        }
        i = 0;
        GridPane grid = new GridPane();
        for (TextArea ar : dayList){
            ar.setPrefSize(250,50);
            grid.setConstraints(ar,i+1,0);
            grid.getChildren().addAll(ar);
            i++;
        }
        Client client = new Client("Client", "Clientov");
        ClientMonth clMoth1 = new ClientMonth(editedMonth, false, client);
        Client client1 = new Client("Client2", "Clientov2");
        ClientMonth clMoth2 = new ClientMonth(editedMonth, false, client1);
        ListOfClientMonths LiClMo = new ListOfClientMonths();
        LiClMo.getListOfClientMonths().add(clMoth1);
        LiClMo.getListOfClientMonths().add(clMoth2);

        int clienMothIter = 0;
        /* Vytvoří text area pro jednotlivé měsíce klienta
        */

        for(ClientMonth clm : LiClMo.getListOfClientMonths()){
            i = 0;
            for (ClientDay cl : clm.getClientDaysInMonth()){
                String inputText;
                TextArea ar = new TextArea();
                if(i==0){
                    inputText = clm.getClientOwningMonth().getName() + " " + clm.getClientOwningMonth().getName();
                    ar.setEditable(false);
                }else{
                    inputText= "";
                }
                ar.setText(inputText);
                areaList.add(ar);
                ar.setPrefSize(250,100);
                grid.setConstraints(ar,i+1,clienMothIter+1);
                i++;
            }
            clienMothIter++;
        }

        grid.getChildren().addAll(areaList);
        TestScrollPane.setContent(grid);

    }
    public void initialize(){
        test();
        System.out.println("hi");
    }


    public void switchPage(ActionEvent actionEvent) throws IOException {
       Scene scen = TestScrollPane.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("assistant-view.fxml"));
        Parent rot = fxmlLoader.load();
        scen.setRoot(rot);

    }
}