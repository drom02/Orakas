package Orakas.Structs;

import Orakas.AssistantAvailability.AssistantAvailability;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
/*
Deprecated class for management of Shifts
 */
public class ShiftTextArea extends TextArea {
    //Text area with added ID and ArrayList<Assistant>
    private int ID;
    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }



    //List of All assistants that are signed for working in that specific shift
    private ArrayList<AssistantAvailability> availableAssistants = new ArrayList<AssistantAvailability>();
    //0 = day shift
    //1 = night shift
    private int Type;
    public ArrayList<AssistantAvailability> getAvailableAssistants() {
        return availableAssistants;
    }

    public void setAvailableAssistants(ArrayList<AssistantAvailability> availableAssistants) {
        this.availableAssistants = availableAssistants;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }
}
