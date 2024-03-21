package graphics.sorter.controllers;

import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.util.converter.IntegerStringConverter;

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
    ArrayList<Node> itemList = new ArrayList<Node>(Arrays.asList(startHours,startMinutes,endHours,endMinutes));
    ArrayList<Integer> column = new ArrayList<>(Arrays.asList(0,1,0,1));
    ArrayList<Integer> row = new ArrayList<>(Arrays.asList(0,0,1,1));

    public AssistantViewConSetup(){
        int i = 0;
        ArrayList<SpinnerValueFactory<Integer> > limits = new ArrayList<>(Arrays.asList(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24, 12),
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0),
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24, 12),
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

}
