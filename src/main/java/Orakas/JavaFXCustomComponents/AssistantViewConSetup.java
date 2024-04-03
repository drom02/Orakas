package Orakas.JavaFXCustomComponents;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.converter.IntegerStringConverter;
import org.apache.poi.ss.formula.functions.T;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AssistantViewConSetup {
    private GridPane localGrid = new GridPane();
    private Spinner<Integer> startHours = new Spinner<>();
    private Spinner<Integer> startMinutes = new Spinner<>();
    private Spinner<Integer> endHours = new Spinner<>();
    private Spinner<Integer> endMinutes = new Spinner<>();

    public ArrayList<Node> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<Node> itemList) {
        this.itemList = itemList;
    }

    ArrayList<Node> itemList = new ArrayList<Node>(Arrays.asList(startHours,startMinutes,endHours,endMinutes));
    ArrayList<Integer> column = new ArrayList<>(Arrays.asList(0,1,0,1));
    ArrayList<Integer> row = new ArrayList<>(Arrays.asList(0,0,1,1));
    ArrayList<String> tooltip = new ArrayList<>(Arrays.asList("Hodiny začátku intervalu","Minuty začátku intervalu","Hodiny konce intervalu","Minuty konce intervalu"));

    public AssistantViewConSetup(){
        int i = 0;
        ArrayList<SpinnerValueFactory<Integer> > limits = new ArrayList<>(Arrays.asList(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12),
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0),
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12),
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0)));
        for(Node n : itemList){
            if(n instanceof Spinner<?>){
                TextFormatter<Integer> formatter = new TextFormatter<>(new IntegerStringConverter(), 1, change -> {
                    String newText = change.getControlNewText();
                    if (newText.matches("\\d*")) {  // Regex to check if the new text is an integer (including negative integers)
                        return change;  // Return the change if it's an integer
                    }
                    return null;  // Otherwise, ignore the change
                });
                GridPane.setConstraints(n, column.get(i),row.get(i),1,1);
                ((Spinner<Integer>) n).setValueFactory(limits.get(i));
                ((Spinner<?>) n).setEditable(true);
                ((Spinner<?>) n).getEditor().setTextFormatter(formatter);
                i++;
            }

        }
        localGrid.getChildren().addAll(itemList);
        Platform.runLater(() -> {
            addTooltip(itemList,tooltip);
        });
    }
    private void addTooltip(ArrayList<Node> tools, ArrayList<String> tooltip){
        for(int i =0;i<4;i++){
            Tooltip tooltipT =new Tooltip(tooltip.get(i));
            Spinner spin = (Spinner) tools.get(i);
            Tooltip.install(spin,tooltipT);
            Tooltip.install(spin.getEditor(),tooltipT);
        }
    }
    private void observers(){
        for(Node n :itemList){
            Spinner<Integer> s = (Spinner<Integer>) n;
            s.valueProperty().addListener((observable, oldValue, newValue) ->{

            });

        }

    }
    public LocalTime getStartLocalTime(){
        return LocalTime.of(startHours.getValue(),startMinutes.getValue());
    }
    public LocalTime getEndLocalTime(){
        return LocalTime.of(endHours.getValue(),endMinutes.getValue());
    }
    public GridPane getLocalGrid() {
        return localGrid;
    }
    public void setLocalGrid(GridPane localGrid) {
        this.localGrid = localGrid;
    }
    public Spinner<Integer> getStartHours() {
        return startHours;
    }
    public void setStartHours(Spinner<Integer> startHours) {
        this.startHours = startHours;
    }
    public Spinner<Integer> getStartMinutes() {
        return startMinutes;
    }
    public void setStartMinutes(Spinner<Integer> startMinutes) {
        this.startMinutes = startMinutes;
    }
    public Spinner<Integer> getEndHours() {
        return endHours;
    }
    public void setEndHours(Spinner<Integer> endHours) {
        this.endHours = endHours;
    }
    public Spinner<Integer> getEndMinutes() {
        return endMinutes;
    }
    public void setEndMinutes(Spinner<Integer> endMinutes) {
        this.endMinutes = endMinutes;
    }
    public boolean validate(boolean isDay){
        double start = getStartHours().getValue() + ((double) getStartMinutes().getValue() /100);
        double end = getEndHours().getValue() + ((double) getEndMinutes().getValue() /100);
        if(isDay){
            if(start>end){
            raiseAlertWindow("Při denní směně začátek nesmí být po konci");
            return false;
            }
        }else{
            if(start<end){
                raiseAlertWindow("Při noční směně konec nesmí být po začátku");
                return  false;
            }
        }
        return true;
    }
    private  void raiseAlertWindow(String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog Example");
        alert.setHeaderText("Information Alert");
        alert.setContentText(text);
        alert.showAndWait();
    }
}
