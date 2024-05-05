package Orakas;

import Orakas.Database.Database;
import Orakas.Excel.ExcelOutput;
import Orakas.workHoursAllocation.WorkHoursCalcul;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
/*

 */
public class Start extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/resultDisplay.css")).toExternalForm());
        stage.setTitle("Orakas");
        stage.setScene(scene);
        stage.show();
        Database.prepareTables();
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