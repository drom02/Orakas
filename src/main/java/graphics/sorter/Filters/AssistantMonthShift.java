package graphics.sorter.Filters;

import graphics.sorter.Assistant;

import java.util.UUID;

public class AssistantMonthShift {


    private UUID assistantID;
    private double workedHours;
    private int lastDayInWork;

    public AssistantMonthShift(UUID id){
        this.setAssistantID(id);

    }
    public UUID getAssistantID() {
        return assistantID;
    }

    public void setAssistantID(UUID assistantID) {
        this.assistantID = assistantID;
    }

    public double getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(double workedHours) {
        this.workedHours = workedHours;
    }

    public int getLastDayInWork() {
        return lastDayInWork;
    }

    public void setLastDayInWork(int lastDayInWork) {
        this.lastDayInWork = lastDayInWork;
    }
}
