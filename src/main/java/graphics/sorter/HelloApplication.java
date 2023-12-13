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
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("test-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        Month testMonth =Month.DECEMBER;
        JsonManip jsoM = new JsonManip();
        jsoM.jsonTest();

    }

    public static void main(String[] args) {
        launch();
    }
}