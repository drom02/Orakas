package graphics.sorter.controllers;

import graphics.sorter.Start;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class StartViewController {
    @FXML
    private TabPane mainTabPane;
    public void  initialize()  {
        /*


         */
        String[] str = new String[] {"Main-view","shiftPicker-view","client-view","assistant-view","Location-view"};
        String[] title = new String[] {"Main-view","shiftPicker-view","client-view","assistant-view","Location-view"};
        mainTabPane.getTabs().clear();
        Platform.runLater(() -> {
            for(String stri : str){
                FXMLLoader loader = new FXMLLoader(Start.class.getResource(stri+".fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Tab tab = new Tab(stri);
                tab.setContent(root);
                mainTabPane.getTabs().add(tab);
            }
            });
    }
}
