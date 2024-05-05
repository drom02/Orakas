package Orakas.controllers.Utils;

import javafx.scene.layout.AnchorPane;
/*
Class is used  for management of selected anchorPane.
 */
public class SelectedAnchorPane {

    public AnchorPane get() {
        return selectedAnchorPane;
    }

    public void set(AnchorPane selectedAnchorPane) {
        this.selectedAnchorPane = selectedAnchorPane;
    }

    private AnchorPane selectedAnchorPane = null;
}
