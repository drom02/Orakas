package graphics.sorter.Structs;

import graphics.sorter.Assistant;

import java.util.ArrayList;

public class AvailableAssistants {


    public ArrayList<ArrayList<Assistant>> getAvailableAssistantsAtDays() {
        return availableAssistantsAtDays;
    }

    public void setAvailableAssistantsAtDays(ArrayList<ArrayList<Assistant>> availableAssistantsAtDays) {
        this.availableAssistantsAtDays = availableAssistantsAtDays;
    }

    public ArrayList<ArrayList<Assistant>> getAvailableAssistantsAtNights() {
        return availableAssistantsAtNights;
    }

    public void setAvailableAssistantsAtNights(ArrayList<ArrayList<Assistant>> availableAssistantsAtNights) {
        this.availableAssistantsAtNights = availableAssistantsAtNights;
    }

    private ArrayList<ArrayList<Assistant>> availableAssistantsAtDays = new ArrayList();
    private ArrayList<ArrayList<Assistant>> availableAssistantsAtNights = new ArrayList();
}
