package graphics.sorter.controllers;

import graphics.sorter.Start;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StartViewController  {
    @FXML
    private TabPane mainTabPane;
    private  HashMap<Tab, FXMLLoader> map = new HashMap<Tab, FXMLLoader>();
    public void  initialize()  {
        String[] str = new String[] {"Main-view","shiftPicker-view","client-view","assistant-view","Location-view"};
        String[] title = new String[] {"Main view","Úpravy směn","Klienti","Asistenti","Lokace"};
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
}
