package graphics.sorter;

import graphics.sorter.Structs.ClientDay;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Month;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        JsonManip jsoM = new JsonManip();
        int[] deftStart = new int[]{8,30};
        int[] defEnd = new int[]{20,30};
        Settings set = new Settings(12,2024,"",deftStart,defEnd,16);
        set.createNewSettingsFile();
       // jsoM.jsonTest();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);

        stage.setTitle("Sorter");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}