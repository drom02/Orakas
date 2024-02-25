package graphics.sorter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import graphics.sorter.Structs.DefSettings;
import graphics.sorter.Structs.ListOfLocations;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Start extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        loadSetting();
        int[] deftStart = new int[]{8,30};
        int[] defEnd = new int[]{20,30};
        //Settings set = new Settings(12,2024,"",deftStart,defEnd,16);
        //set.createNewSettingsFile();
       // jsoM.jsonTest();
        FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);

        stage.setTitle("Sorter");
        stage.setScene(scene);
        stage.show();

        ValidateFiles valid = new ValidateFiles();
        valid.run();
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
            System.out.println("error");
            Settings def = new Settings(12, 2024, "E:\\JsonWriteTest\\", new int[]{8, 30}, new int[]{20, 30}, 16);
           // def.setPathToSettings("E:\\JsonWriteTest\\Settings\\Settings.json");
            try {
                objectMapper.writeValue(new File(".\\Settings.json"),def);
                byte[]  jsonData = Files.readAllBytes(Paths.get(".\\Settings.json"));
                lot = objectMapper.readValue(jsonData, Settings.class );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            //throw new RuntimeException(e);
        }
        return lot;

    }
}