package Orakas.JavaFXCustomComponents;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ToggleButton {
    public GridPane getLocalGrid() {
        return localGrid;
    }

    public void setLocalGrid(GridPane localGrid) {
        this.localGrid = localGrid;
    }

    private GridPane localGrid = new GridPane();
    private SwitchPane leftPane = new SwitchPane();
    private SwitchPane rightPane = new SwitchPane();
    private Label leftLabel = new Label();
    private Label rightLabel = new Label();

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
    private boolean state = true;
    private HashMap<SwitchPane,SwitchPane> paneSwitch = new HashMap<>();
    ArrayList<SwitchPane> panes = new ArrayList<>(Arrays.asList(leftPane,rightPane));
    String selected = "-fx-background-color: #795eff; -fx-border-color: #000000;-fx-border-width: 2px;";


    String unSelected = "-fx-background-color: #f3f2f7; -fx-border-color: #000000;-fx-border-width: 2px;";
    public ToggleButton(String labelLeft,String labelRight){
        //localGrid.setStyle("-fx-border-color: #000000;-fx-border-width: 1.5px;");
        localGrid.setMaxHeight(35);
        leftPane.setPrefSize(100,35);
        rightPane.setPrefSize(100,35);
        setPanes();
        leftPane.setState(true);
        leftLabel.setText(labelLeft);
        rightLabel.setText(labelRight);
        GridPane.setConstraints(leftPane,0,0);
        GridPane.setConstraints(rightPane,1,0);
        localGrid.getChildren().addAll(leftPane,rightPane);
        leftPane.setOnMouseClicked(this::toggle);
        rightPane.setOnMouseClicked(this::toggle);
        rightPane.setState(false);
        setStyle(panes);
        leftPane.getChildren().add(leftLabel);
        rightPane.getChildren().add(rightLabel);
    }
    private void setPanes(){
        for(SwitchPane p : panes){
            for(SwitchPane p2 : panes.stream().filter(item-> !item.equals(p)).toList()){
                paneSwitch.put(p,p2);
            }
        }
    }
    private void toggle(MouseEvent mouseEvent){
        SwitchPane source = (SwitchPane) mouseEvent.getSource();
        if(!source.isState()){
            switchState();
            source.setState(true);
            paneSwitch.get(source).setState(false);
            setStyle(panes);
        }

    }
    private void setStyle(ArrayList<SwitchPane> arrayList){
        for(SwitchPane sw:arrayList){
            if(sw.isState()){
                sw.setStyle(selected);
            }else{
                sw.setStyle(unSelected);
            }
        }
    }
    private void switchState(){
        state = !state;
    }
}
