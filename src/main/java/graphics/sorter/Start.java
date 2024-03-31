package graphics.sorter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import graphics.sorter.Structs.DefSettings;
import graphics.sorter.Structs.ListOfLocations;
import graphics.sorter.workHoursAllocation.WorkHoursCalcul;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class Start extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        scene.getStylesheets().add(getClass().getResource("/main.css").toExternalForm());
        stage.setTitle("Orakas");
        stage.setScene(scene);
        stage.show();
        WorkHoursCalcul work = new WorkHoursCalcul();
        Database.prepareTables();
        ExcelOutput.writeXSLX();

    }

    public static void main(String[] args) {
        launch();
    }

    private Settings loadSetting(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Settings lot = null;
        try {
        byte[]  jsonData = Files.readAllBytes(Paths.get(".\\Settings.json"));
        lot = objectMapper.readValue(jsonData, Settings.class );
        } catch (IOException e) {
          //  System.out.println("error");
            try {
                Settings.createNewSettingsFile();
                return loadSetting();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return lot;

    }
}