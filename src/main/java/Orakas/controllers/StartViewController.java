package Orakas.controllers;

import Orakas.Mediator.InternalController;
import Orakas.Settings;
import Orakas.Start;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class StartViewController  implements ControllerInterface {
    @FXML
    private TextFlow informationFlow;
    @FXML
    private TabPane mainTabPane;
    private  HashMap<Tab, FXMLLoader> map = new HashMap<Tab, FXMLLoader>();
    private InternalController internalController = new InternalController(this);
    private static final List<String> monthNames = Arrays.asList(
            "Leden",    // 0 - January
            "Únor",     // 1 - February
            "Březen",   // 2 - March
            "Duben",    // 3 - April
            "Květen",   // 4 - May
            "Červen",   // 5 - June
            "Červenec", // 6 - July
            "Srpen",    // 7 - August
            "Září",     // 8 - September
            "Říjen",    // 9 - October
            "Listopad", // 10 - November
            "Prosinec"  // 11 - December
    );
    public void  initialize()  {
        String[] str = new String[] {"Main-view","shiftPicker-view","client-view","assistant-view","Location-view"};
        String[] title = new String[] {"Hlavní přehled","Úpravy směn","Klienti","Asistenti","Lokace"};
        ArrayList<CompletableFuture> futures = new ArrayList<>();
        mainTabPane.getTabs().clear();
        Platform.runLater(() -> {
            int i = 0;
            for(String stri : str){
                    FXMLLoader loader = new FXMLLoader(Start.class.getResource(stri+".fxml"));
                    Parent root = null;
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Tab tab = new Tab(title[i]);
                    i++;
                    map.put(tab,loader);
                    tab.setContent(root);
                    mainTabPane.getTabs().add(tab);

            }
            informationFlow.getStyleClass().add("DateFlow");
            setDate();
            });
        mainTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                if (newTab != null) {
                    onTabSelected(newTab);
                }
            }
        });
    }
   private void onTabSelected(Tab tab) {
       Platform.runLater(() -> {Stage stag = (Stage)tab.getContent().getScene().getWindow();
           ControllerInterface cont = map.get(tab).getController();
           cont.updateScreen();
           });

   }

    @Override
    public void updateScreen() {

    }
    public void setDate(){
        informationFlow.getChildren().clear();
        Text text = new Text(monthNames.get(Settings.getSettings().getCurrentMonth()-1) + " " + Settings.getSettings().getCurrentYear());
        text.getStyleClass().add("DateTextFormat");
        informationFlow.getChildren().add(text);
    }
    @Override
    public void loadAndUpdateScreen() {
        Platform.runLater(this::setDate);

    }
}
