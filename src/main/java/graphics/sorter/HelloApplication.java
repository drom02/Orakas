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
        Settings set = new Settings(12,2024,"");
        set.createNewSettingsFile();
        jsoM.jsonTest();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Sorter");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}