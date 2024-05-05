package Orakas;

import javafx.geometry.Rectangle2D;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
/*
Class responsible for management of screen size and other graphic related functions.
 */
public class GraphicalFunctions {
    public static String toHexWithAlphaString(javafx.scene.paint.Color color) {
        return String.format("#%02X%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                (int) (color.getOpacity() * 255)); // Include alpha
    }
    public static String toHexFromRGBO(javafx.scene.paint.Color color, double o) {
        return String.format("#%02X%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                (int) (o * 255)); // Include alpha
    }
    public static String toHexFromRGBO(double red, double green, double blue, double o) {
        return String.format("#%02X%02X%02X%02X",
                (int) (red ),
                (int) (green ),
                (int) (blue ),
                (int) (o * 255)); // Include alpha
    }
    public static void screenResizing(Pane mainPane, GridPane mainGrid){
        GraphicalSettings GS = new GraphicalSettings(null, null);
        Stage stage = (Stage) mainPane.getScene().getWindow();

        Rectangle2D screenBounds =  Screen.getPrimary().getVisualBounds();

        stage.setMaxHeight(screenBounds.getHeight());
        stage.setMaxWidth(screenBounds.getWidth());
        mainPane.setPrefSize(screenBounds.getWidth(),screenBounds.getHeight());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        mainGrid.setPrefSize(screenBounds.getWidth(),screenBounds.getHeight()-80);
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(mainPane.widthProperty());
        clip.heightProperty().bind((mainPane.heightProperty()));
        mainPane.setClip(clip);

    }
    public static void screenResizingStandard(Pane mainPane, GridPane mainGrid){
        GraphicalSettings GS = new GraphicalSettings(null, null);
        Stage stage = (Stage) mainPane.getScene().getWindow();

        //stage.setFullScreen(!stage.isFullScreen());
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setMaxHeight(screenBounds.getHeight());
        stage.setMaxWidth(screenBounds.getWidth());
        mainPane.setPrefSize(screenBounds.getWidth(),screenBounds.getHeight());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        mainGrid.setPrefSize(screenBounds.getWidth(),screenBounds.getHeight()-80);
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(mainPane.widthProperty());
        clip.heightProperty().bind((mainPane.heightProperty()));
        mainPane.setClip(clip);

    }
}
