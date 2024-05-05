package Orakas.JavaFXCustomComponents;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
/*
Component of toggleButton that represents clickable pane.
 */
public class SwitchPane extends StackPane {
    public boolean isState()  {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    boolean state = false;
}
