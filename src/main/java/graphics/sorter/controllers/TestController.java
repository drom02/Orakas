package graphics.sorter.controllers;

import graphics.sorter.HelloApplication;
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
    TextArea textArea1 = new TextArea("Enter your name");
    TextArea textArea2 = new TextArea("Enter your nameeeeeeeeeeeeeeeeee");

    public void test(){
        ArrayList<TextArea> areaList = new ArrayList<TextArea>();
        ArrayList<TextArea> dayList = new ArrayList<TextArea>();
        int i = 0;
        while (i <= 155){
            TextArea newTextArea = new TextArea("Enter your" + i);
            areaList.add(newTextArea);
            i++;
        }
        i = 1;
        Month december = Month.DECEMBER;
        while (i <= december.length(false)){
            TextArea newTextArea = new TextArea(i + "."+ "12"+ "."+"2023");
            dayList.add(newTextArea);
            i++;
        }
        i = 0;
        GridPane grid = new GridPane();
        for (TextArea ar : dayList){
            ar.setPrefSize(250,50);
            grid.setConstraints(ar,i,0);
            grid.getChildren().addAll(ar);
            i++;
        }
        int x = 0;
        int y = 0;
        i = 0;
        for (TextArea ar : areaList){
            ar.setPrefSize(250,100);
            grid.setConstraints(ar,x,(i/december.length(false))+1);
            i++;
            x++;
            if(x >= december.length(false)){
                x=0;
            }
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