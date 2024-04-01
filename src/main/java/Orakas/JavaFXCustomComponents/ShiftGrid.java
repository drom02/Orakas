package Orakas.JavaFXCustomComponents;

import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.UUID;

public class ShiftGrid extends GridPane {
    public ArrayList<UUID> getDisplayedAssistants() {
        return displayedAssistants;
    }

    public void setDisplayedAssistants(ArrayList<UUID> displayedAssistants) {
        this.displayedAssistants = displayedAssistants;
    }
    public void addAssistant(UUID removedA){
        displayedAssistants.add(removedA);
    }
    public boolean removeAssistant(UUID removedA){
        return displayedAssistants.remove(removedA);
    }

   private  ArrayList<UUID> displayedAssistants = new ArrayList<UUID>();
    public ShiftGrid(boolean bool){
        setDay(bool);
    }
    public boolean isDay() {
        return isDay;
    }

    public void setDay(boolean day) {
        isDay = day;
    }

    private boolean isDay = true;
}
