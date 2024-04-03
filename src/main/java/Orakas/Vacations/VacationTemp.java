package Orakas.Vacations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.UUID;

public class VacationTemp {
    private ArrayList<Vacation> tempVacation;
    private UUID assistantID;
    @JsonCreator
    public VacationTemp(@JsonProperty("tempVacation" )ArrayList<Vacation> tempVacation,
                        @JsonProperty("assistantID" )UUID assistantID){
     setTempVacation(tempVacation);
     setAssistantID(assistantID);
    }
    public ArrayList<Vacation> getTempVacation() {
        return tempVacation;
    }

    public void setTempVacation(ArrayList<Vacation> tempVacation) {
        this.tempVacation = tempVacation;
    }
    public UUID getAssistantID() {
        return assistantID;
    }

    public void setAssistantID(UUID assistantID) {
        this.assistantID = assistantID;
    }
}
