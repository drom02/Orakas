package Orakas.controllers.Utils;

import javafx.scene.text.TextFlow;
/*
Class is used  for management of selected textFlow.
 */
public class SelectedTextFlow {

    public TextFlow get() {
        return selectedTextArea;
    }

    public void set(TextFlow selectedTextArea) {
        this.selectedTextArea = selectedTextArea;
    }

    private TextFlow selectedTextArea = null;

}
