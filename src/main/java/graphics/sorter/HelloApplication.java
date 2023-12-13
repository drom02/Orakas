package graphics.sorter;

import graphics.sorter.Structs.ClientDay;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("test-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        JsonManip jsoM = new JsonManip();
        jsoM.jsonTest();
        ClientDay clid = new ClientDay();
        clid.test();
    }

    public static void main(String[] args) {
        launch();
    }
}